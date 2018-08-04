package Logic;

import Logic.Enums.eGameState;
import Logic.Exceptions.InvalidFileInputException;
import Logic.Exceptions.InvalidUserInputException;
import Logic.Interfaces.IGameStatus;
import Logic.Interfaces.ILogic;
import Logic.Managers.FileManager;
import Logic.Managers.HistoryManager;
import Logic.Models.*;

import javax.xml.bind.JAXBException;
import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
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
    public void ReadGameFile(String filePath) throws InvalidFileInputException, IOException, JAXBException {
        mFileManager.LoadGameFile(filePath);
    }

    //@Override
    public void StartGame() {
        // Set game board
        this.mGameBoard = new Board(GameSettings.getInstance().getRows(), GameSettings.getInstance().getColumns());
        this.mHistoryManager.Clear();

        if (mFileManager.getIsFileLoaded()) {
            mGameStatus.StartNewGame();
        } else {
            // TODO: File not loaded yet exception
        }
    }

    //@Override
    public GameStatus GetGameStatus() {
        return mGameStatus;
    }

    //@Override
    public PlayerTurn PlayTurn(int column) throws Exception, InvalidUserInputException {
        Cell chosenCell = mGameBoard.UpdateBoard(column, mGameStatus.getPlayer()); // send parameter to logic board
        PlayerTurn playerTurn  = updateGameStatus(chosenCell);

        // Update game state when turn ends.
        mGameStatus.mGameState = playerTurn.getGameState();
        mHistoryManager.SaveTurn(playerTurn);
        mGameStatus.FinishedTurn();

        return playerTurn;
    }

    //@Override
    private PlayerTurn updateGameStatus(Cell updatedCell) {
        eGameState currentGameState = mGameBoard.getCurrentGameState(updatedCell);
        return new PlayerTurn(updatedCell, updatedCell.getPlayer(), currentGameState);
    }

    //@Override
    public Collection<PlayerTurn> GetTurnHistory() {
        // send parameters to history manager
        return mHistoryManager.GetGameHistory();
    }

    public Player GetCurrentPlayer() {
        return mGameStatus.getPlayer();
    }

    public class GameStatus implements IGameStatus {
        private eGameState mGameState;
        private int mTurn = 0;
        private Player mPlayer;
        private int mPlayerIndex;
        private Instant mGameStart;

        // Getters/Setters
        public int getTurn() {
            return mTurn;
        }

        public Player getPlayer() {
            return mPlayer;
        }

        public eGameState getGameState() { return mGameState; }

        // API

        void StartNewGame(){
            this.mGameState = eGameState.Active;
            this.mTurn = 0;
            this.mPlayerIndex = 0;
            this.mPlayer = GameSettings.getInstance().getPlayers().get(mPlayerIndex);
            this.mGameStart = Instant.now();
        }

        // Adjust game status when a player has finished his turn.
        void FinishedTurn() {
            mTurn++;
            mPlayer.FinishedTurn();
            nextPlayer();
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
            return ++mPlayerIndex % GameSettings.getInstance().getPlayers().size();
        }

        @Override
        public String getNameOfPlayerCurrentlyPlaying() {
            return mPlayer.getName();
        }

        @Override
        public Duration getGameDuration() {
            return Duration.between(mGameStart, Instant.now());
        }
    }
}
