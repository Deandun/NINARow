package Logic;

import Logic.Enums.eGameState;
import Logic.Exceptions.InvalidFileInputException;
import Logic.Interfaces.IGameStatus;
import Logic.Interfaces.ILogic;
import Logic.Managers.HistoryFileManager;
import Logic.Managers.FileManager;
import Logic.Managers.HistoryManager;
import Logic.Models.*;

import javax.xml.bind.JAXBException;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.util.Collection;
import java.util.List;

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
    public void ReadGameFile(String filePath) throws FileNotFoundException, InvalidFileInputException, IOException, JAXBException {
        mFileManager.LoadGameFile(filePath);
    }

    //@Override
    public void StartGame() throws NoSuchFieldException {
        // Set game board
        this.mGameBoard = new Board(GameSettings.getInstance().getRows(), GameSettings.getInstance().getColumns());
        this.mHistoryManager.Clear();

        if (mFileManager.getIsFileLoaded()) {
            mGameStatus.StartNewGame();
        } else {
            throw new NoSuchFieldException("File is not loaded yet");
        }
    }

    //@Override
    public GameStatus GetGameStatus() {
        return mGameStatus;
    }

    //@Override
    public PlayerTurn PlayTurn(int column) throws  NullPointerException, Exception {
        Cell chosenCell = mGameBoard.UpdateBoard(column, mGameStatus.getPlayer()); // send parameter to logic board
        PlayerTurn playerTurn  = updateGameStatus(chosenCell);

        // Update game state when turn ends.
        mGameStatus.mGameState = playerTurn.getGameState();
        mHistoryManager.SetCurrentTurn(playerTurn);
        mGameStatus.FinishedTurn();

        return playerTurn;
    }

    public boolean isGameActive(){
        return (mGameStatus != null && mGameStatus.getGameState().equals(eGameState.Active));
    }

    //@Override
    private PlayerTurn updateGameStatus(Cell updatedCell) throws Exception {
        eGameState currentGameState = mGameBoard.getCurrentGameState(updatedCell);
        PlayerTurn playerTurn = new PlayerTurn();
        playerTurn.setUpdatedCell(updatedCell);
        playerTurn.setPlayerTurn(updatedCell.getPlayer());
        playerTurn.setGameState(currentGameState);

        return playerTurn;
    }

    //@Override
    public Collection<PlayerTurn> GetTurnHistory() {
        // send parameters to history manager
        return this.mHistoryManager.GetGameHistory();
    }

    public Player GetCurrentPlayer() {
        return this.mGameStatus.getPlayer();
    }

    public void saveGame() throws  IOException, ClassNotFoundException, Exception {
        HistoryFileManager.saveGameHistoryInXMLFile(GameSettings.getSavedGameFileName(), mHistoryManager.GetGameHistory());
    }

    public void loadExistsGame() throws IOException, ClassNotFoundException, Exception {
        String path = GameSettings.getSavedGameFileName();
        File loadedFile = new File(path);

        if (loadedFile.exists()) {
            mGameBoard.Clear();

            List<PlayerTurn> currentGame = HistoryFileManager.readGameHistoryFromXMLFile(path);

            if (currentGame != null) {
                for (PlayerTurn turn : currentGame) {
                    this.PlayTurn(turn.getUpdatedCell().getColumnIndex());
                }
            }
        }
        else{
            throw new FileNotFoundException("Cannot find file: " + path);
        }
    }

    public Board getBoard() {
        return mGameBoard;
    }

    public eGameState GetGameState() {
        return mGameStatus.getGameState();
    }

    public class GameStatus implements IGameStatus {
        private eGameState mGameState = eGameState.Inactive;
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

        @Override
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

            return gameActivation + ", Turn number is " + mTurn + ", Player turn is: " + this.mPlayer.getName();
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
