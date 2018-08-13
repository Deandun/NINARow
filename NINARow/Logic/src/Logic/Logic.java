package Logic;

import Logic.Enums.eGameState;
import Logic.Exceptions.InvalidFileInputException;
import Logic.Exceptions.InvalidUserInputException;
import Logic.Interfaces.IGameStatus;
import Logic.Interfaces.ILogic;
import Logic.Managers.FileManager;
import Logic.Managers.HistoryFileManager;
import Logic.Managers.HistoryManager;
import Logic.Models.*;
import Logic.SequenceSearchers.SequenceSearcher;

import javax.xml.bind.JAXBException;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public class Logic implements ILogic {

    private FileManager mFileManager;
    private HistoryManager mHistoryManager;
    private GameStatus mGameStatus;
    private Board mGameBoard;
    private SequenceSearcher mSequenceSearcher;

    public Logic() {
        this.mFileManager = new FileManager();
        this.mHistoryManager = new HistoryManager();
        this.mGameStatus = new GameStatus();
        this.mSequenceSearcher = new SequenceSearcher();
    }

    // ILogic interface implementation.

    @Override
    public void ReadGameFile(String filePath) throws FileNotFoundException, InvalidFileInputException, IOException, JAXBException {
        mFileManager.LoadGameFile(filePath);
        // TODO: when loading game file is moved to a seporate thread, create new board only when done loading file
        this.mGameBoard = new Board(GameSettings.getInstance().getRows(), GameSettings.getInstance().getColumns());
    }

    @Override
    public void StartGame() {
        // Set game board
        this.mGameBoard.Init(GameSettings.getInstance().getRows(), GameSettings.getInstance().getColumns());
        this.mSequenceSearcher.Clear();
        this.mSequenceSearcher.setBoard(mGameBoard);
        this.mHistoryManager.Clear();
        this.mGameStatus.StartNewGame();
    }

    public Map<String, Collection<Cell>> getWinningSequencesMap() {
        return mSequenceSearcher.getWinningSequencesMap();
    }

    public GameStatus GetGameStatus() {
        return mGameStatus;
    }

    @Override
    public PlayerTurn PlayTurn(int column) throws InvalidUserInputException, Exception {
        Cell chosenCell = mGameBoard.UpdateBoard(column, mGameStatus.getPlayer()); // send parameter to logic board
        PlayerTurn playerTurn  = updateGameStatus(chosenCell);

        // Update game state when turn ends.
        mGameStatus.mGameState = playerTurn.getGameState();
        mHistoryManager.SetCurrentTurn(playerTurn);
        mGameStatus.FinishedTurn();

        return playerTurn;
    }

    public void Popout(int column) {
        mGameBoard.Popout(column - 1);
    }

    public void PlayerQuit(Player player) {
        // TODO: set the next player's index accordingly.
        GameSettings.getInstance().getPlayers().remove(player);

        if(GameSettings.getInstance().getPlayers().size() == 1) {
            //TODO: only one player left, he won!
        }

        mGameBoard.RemoveAllPlayerDiscsFromBoard(player);
    }

    public boolean isGameActive(){
        return (mGameStatus != null && mGameStatus.getGameState() == eGameState.Active);
    }

    private PlayerTurn updateGameStatus(Cell updatedCell) {
        eGameState currentGameState = getCurrentGameState(updatedCell);
        PlayerTurn playerTurn = new PlayerTurn();

        playerTurn.setUpdatedCell(updatedCell);
        playerTurn.setPlayerTurn(updatedCell.getPlayer());
        playerTurn.setGameState(currentGameState);

        return playerTurn;
    }

    private eGameState getCurrentGameState(Cell updatedCell) {
        eGameState gameState;

        if(mSequenceSearcher.DidPlayerWinGameInRecentTurn(updatedCell)) {
            gameState = eGameState.Won;
        } else if (mGameBoard.IsBoardFull()) {
            gameState = eGameState.Draw;
        } else {
            gameState = eGameState.Active;
        }

        return gameState;
    }

    @Override
    public List<PlayerTurn> GetTurnHistory() {
        // send parameters to history manager
        return this.mHistoryManager.GetGameHistory();
    }

    @Override
    public Player GetCurrentPlayer() {
        return this.mGameStatus.getPlayer();
    }

    @Override
    public void SaveGame() throws  IOException, ClassNotFoundException, Exception {
        HistoryFileManager.SaveGameHistoryInXMLFile(GameSettings.getSavedGameFileName(), mHistoryManager.GetGameHistory());
    }

    @Override
    public void LoadExistsGame() throws IOException, ClassNotFoundException, Exception {
        String path = GameSettings.getSavedGameFileName();
        File loadedFile = new File(path);

        if (loadedFile.exists()) {
            List<PlayerTurn> loadedGameTurnHistory = HistoryFileManager.ReadGameHistoryFromXMLFile(path);
            StartGame();

            if (loadedGameTurnHistory != null) {
                for (PlayerTurn turn : loadedGameTurnHistory) {
                    this.PlayTurn(turn.getUpdatedCell().getColumnIndex());
                }
            }
        } else {
            throw new FileNotFoundException("Cannot find file: " + path);
        }
    }

    @Override
    public Board getBoard() {
        return mGameBoard;
    }

    @Override
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

        @Override
        public String getNameOfPlayerCurrentlyPlaying() {
            return mPlayer.getName();
        }

        @Override
        public Duration getGameDuration() {
            return Duration.between(mGameStart, Instant.now());
        }

        // API

        void StartNewGame(){
            this.mGameState = eGameState.Active;
            this.mTurn = 0;
            this.mPlayerIndex = 0;
            GameSettings.getInstance().getPlayers().forEach(
                    (player) -> player.setTurnCounter(0) // Reset turn counter
            );
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
    }
}
