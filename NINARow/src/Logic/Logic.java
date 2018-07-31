package Logic;

import Logic.Enums.eGameState;
import Logic.Exceptions.InvalidFileInputException;
import Logic.Exceptions.InvalidUserInputException;
import Logic.Managers.FileManager;
import Logic.Managers.HistoryManager;
import Logic.Models.*;

import java.util.Collection;

public class Logic implements ILogic {

    private FileManager mFileManager;
    private HistoryManager mHistoryManager;
    private GameStatus mGameStatus;
    private Board mGameBoard;

    public Logic() {
        this.mFileManager = new FileManager();
        this.mHistoryManager = new HistoryManager();
        this.mGameStatus = new GameStatus();
    }

    // ILogic interface implementation.

    //@Override
    public void ReadGameFile(String filePath) throws InvalidFileInputException {
        mFileManager.LoadGameFile(filePath);
    }

    //@Override
    public void StartGame() {
        // Set game board
        this.mGameBoard = new Board(GameSettings.getInstance().getRows(), GameSettings.getInstance().getColumns());

        if (mFileManager.getIsFileLoaded()) {
            mGameStatus.StartNewGame(GameSettings.getInstance().getPlayers().get(0));
        } else {
            // File not loaded yet exception
        }
    }

    //@Override
    public GameStatus GetGameStatus() {
        return mGameStatus;
    }

    //@Override
    public PlayerTurn PlayTurn(int column) throws Exception, InvalidUserInputException {
        Cell chosenCell = mGameBoard.UpdateBoard(column, mGameStatus.getPlayer()); // send parameter to logic board
        updateGameStatus();
        PlayerTurn playerTurn = new PlayerTurn(chosenCell, mGameStatus.getPlayer(), mGameStatus.mGameState);
        mHistoryManager.SaveTurn(playerTurn);

        //update GameStatus in case succeed
        mGameStatus.FinishedTurn();

        return playerTurn;
    }

    //@Override
    private void updateGameStatus() {
        //Check if following this turn - player won
    }

    //@Override
    public Collection<PlayerTurn> GetTurnHistory() {
        // send parameters to history manager
        return mHistoryManager.GetGameHistory();
    }

    public Player GetCurrentPlayer() {
        return mGameStatus.getPlayer();
    }

    public class GameStatus {
        private eGameState mGameState;
        private int mTurn = 0;
        private Player mPlayer;
        private int mPlayerIndex;

        // Getters/Setters
        public int getTurn() {
            return mTurn;
        }

        public Player getPlayer() {
            return mPlayer;
        }

        public eGameState getGameState() { return mGameState; }

        // API

        void StartNewGame(Player player){
            this.mGameState = eGameState.Inactive;
            this.mTurn = 0;
            this.mPlayerIndex = 0;
            this.mPlayer = player;
        }

        // Adjust game status when a player has finished his turn.
        void FinishedTurn() {
            mTurn++;
            mPlayerIndex++;
            mPlayer.FinishedTurn();
            nextPlayer();
        }

        boolean IsIsGameActive() {
            return mGameState != mGameState.Inactive;
        }

        @Override
        public String toString(){
            String gameActivation = "Game is ";

            if (mGameState != eGameState.Inactive){
                gameActivation += "active";
            }
            else{
                gameActivation += "not active";
            }

            return gameActivation + ", Turn number is " + mTurn + ", Player turn is: " + this.mPlayer.getName(); //TODO: check player's get name
        }

        // Helper functions

        void nextPlayer() {
            mPlayer = GameSettings.getInstance().getPlayers().get(getNextPlayerIndex());
        }

        int getNextPlayerIndex() {
            mPlayerIndex++;
            return mPlayerIndex % GameSettings.getInstance().getPlayers().size();
        }
    }
}
