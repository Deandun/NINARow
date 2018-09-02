package Logic;

import Logic.ComputerPlayer.ComputerPlayerAlgo;
import Logic.ComputerPlayer.PlayComputerPlayerTask;
import Logic.Enums.eGameState;
import Logic.Enums.ePlayerType;
import Logic.Enums.eTurnType;
import Logic.Enums.eVariant;
import Logic.Exceptions.InvalidFileInputException;
import Logic.Exceptions.InvalidInputException;
import Logic.Exceptions.InvalidUserInputException;
import Logic.Interfaces.IComputerPlayerAlgo;
import Logic.Interfaces.IGameStatus;
import Logic.Interfaces.ILogicDelegate;
import Logic.Managers.FileManager;
import Logic.Managers.HistoryFileManager;
import Logic.Managers.HistoryManager;
import Logic.Models.*;
import Logic.SequenceSearchers.SequenceSearcher;
import Logic.SequenceSearchers.SequenceSearcherStrategyFactory;

import javax.xml.bind.JAXBException;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class Logic{

    private static final int QUEUE_CAPACITY = 1;
    private FileManager mFileManager;
    private HistoryManager mHistoryManager;
    private GameStatus mGameStatus;
    private Board mGameBoard;
    private SequenceSearcher mSequenceSearcher;
    private IComputerPlayerAlgo mComputerPlayerAlgo;
    private BlockingQueue<PlayTurnParameters> mTurnsBlockingQueue;
    private ILogicDelegate mDelegate;
    private Thread mTurnQueueListenerThread;

    public Logic(ILogicDelegate logicDelegate) {
        this.mFileManager = new FileManager();
        this.mHistoryManager = new HistoryManager();
        this.mGameStatus = new GameStatus();
        this.mSequenceSearcher = new SequenceSearcher();
        this.mComputerPlayerAlgo = new ComputerPlayerAlgo();
        this.mTurnsBlockingQueue = new ArrayBlockingQueue<>(QUEUE_CAPACITY);
        this.mDelegate = logicDelegate;
        this.mTurnQueueListenerThread = new Thread(this::startConsumingPlayerTurns);
        this.mTurnQueueListenerThread.setDaemon(true); // Daemon thread will not prevent the JVM from shutting down when the main thread ends.
        this.mTurnQueueListenerThread.start(); // Start listening to player turns on a different thread.
    }

    // ILogic interface implementation.

    public void ReadGameFile(String filePath, Runnable onLoadFileFinish, Runnable onFinishedCheckingFileValidity) throws FileNotFoundException, InvalidFileInputException, IOException, JAXBException, InterruptedException {
        GameSettings.getInstance().Clear();
        this.mFileManager.LoadGameFile(filePath, onLoadFileFinish, onFinishedCheckingFileValidity);
        this.mGameBoard = new Board(GameSettings.getInstance().getRows(), GameSettings.getInstance().getColumns());
    }

    public void StartGame() {
        // Set game board
        this.mGameBoard.Init(GameSettings.getInstance().getRows(), GameSettings.getInstance().getColumns());
        this.mSequenceSearcher.Clear();
        this.mSequenceSearcher.setBoard(this.mGameBoard);
        this.mComputerPlayerAlgo.Init(this.mGameBoard);
        this.mHistoryManager.Clear();
        this.mGameStatus.StartNewGame();

        if(this.mGameStatus.getPlayer().getType().equals(ePlayerType.Computer)) {
            // If computer is playing first - Play computer turn.
            //this.mIsTurnInProgress = true;
            this.mDelegate.turnInProgress();
            PlayComputerPlayerTask playComputerPlayerTask = new PlayComputerPlayerTask(this::playComputerAlgoTurn);
            new Thread(playComputerPlayerTask).start();
        }
    }

    public Map<Player, Collection<Cell>> getPlayerToWinningSequencesMap() {
        Map<Player, Collection<Cell>> playerToWinningSequenceMap;

        if(GameSettings.getInstance().getPlayers().size() == 1) {
            // Player won by default, not by winning sequence.
            playerToWinningSequenceMap = new HashMap<>();
            playerToWinningSequenceMap.put(this.GetCurrentPlayer(), null);
        } else {
            playerToWinningSequenceMap = this.mSequenceSearcher.getPlayerToWinningSequencesMap();
        }

        return playerToWinningSequenceMap;
    }

    public GameStatus GetGameStatus() {
        return mGameStatus;
    }

    private PlayedTurnData playTurn(PlayTurnParameters playTurnParameters) throws InvalidInputException {
        // Handle human players turn.
        PlayedTurnData playedTurnData = playTurnParameters.getmTurnType().equals(eTurnType.AddDisc) ?
                this.addDisc(playTurnParameters.getmSelectedColumn()) : this.popout(playTurnParameters.getmSelectedColumn());

        playedTurnData.setTurnType(playTurnParameters.getmTurnType());

        // Update game state when turn ends.
        this.finishedPlayingTurn(playedTurnData);

        return playedTurnData;
    }

    private void playComputerAlgoTurn()  {
        // Use algo to determine next computer turn.
        PlayTurnParameters computerPlayTurnParams = this.mComputerPlayerAlgo.getNextPlay(this.mGameStatus.mCurrentPlayer);

        try {
            Thread.sleep(300);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        this.playTurnAsync(computerPlayTurnParams);
    }

    // This function should be called when a player has ended his turn (but not on playerQuit)
    private void finishedPlayingTurn(PlayedTurnData playedTurnData) {
        this.finishedPlayingTurn(playedTurnData, true);
    }

    // Second parameter should be true only when player quit. When a player has quit, game status is updated
    // Through another function.
    private void finishedPlayingTurn(PlayedTurnData playedTurnData, boolean shouldUpdateGameStatus) {
        this.mGameStatus.mGameState = playedTurnData.getGameState();
        this.mHistoryManager.SetCurrentTurn(playedTurnData);
        if(shouldUpdateGameStatus) {
            this.mGameStatus.FinishedTurn();
        }
    }

    private PlayedTurnData addDisc(int column) throws InvalidUserInputException {
        Cell chosenCell = this.mGameBoard.UpdateBoard(column, this.mGameStatus.getPlayer()); // send parameter to logic board
        return this.updateGameStatusAfterDiscAdded(chosenCell);
    }

    private PlayedTurnData popout(int column) throws InvalidUserInputException {
        if(mGameBoard.CanPlayerPerformPopoutForColumn(this.mGameStatus.getPlayer(), column)) {
            PlayedTurnData playedTurnData = new PlayedTurnData();
            Collection<Cell> updatedCells = this.mGameBoard.PopoutAndGetUpdatedCells(column);

            playedTurnData.setUpdatedCellsCollection(updatedCells);
            playedTurnData.setGameState(this.mSequenceSearcher.CheckColumnForWinningSequences(column) ? eGameState.Won : eGameState.Active);
            playedTurnData.setPlayerTurn(this.mGameStatus.mCurrentPlayer);
            playedTurnData.setTurnType(eTurnType.Popout);

            // Check if there is a winning sequence starting from a cell in the selected column as a result of the Popout.
            return playedTurnData;
        } else {
            throw new InvalidUserInputException("Player named " + this.mGameStatus.getPlayer().getName() + " cannot perform popout on column " + column);
        }
    }

    private PlayedTurnData currentPlayerQuit(PlayTurnParameters turnParameters) {
        PlayedTurnData playedTurnData = new PlayedTurnData();
        eGameState gameState = eGameState.Active;

        if(GameSettings.getInstance().getPlayers().size() > 2) {
            // If there are more than 2 players, the game will go on after player quits.
            Collection<Cell> updatedCells = this.mGameBoard.RemoveAllPlayerDiscsFromBoardAndGetUpdatedCells(this.mGameStatus.getPlayer());
            playedTurnData.setUpdatedCellsCollection(updatedCells);

            if (this.mSequenceSearcher.CheckEntireBoardForWinningSequences()){  //run all over board and check if someone won
                gameState = eGameState.Won;
            } else if(this.isDrawForNextPlayer()) {
                gameState = eGameState.Draw;
            }
        }

        this.mGameStatus.CurrentPlayerQuitGame();

        if(GameSettings.getInstance().getPlayers().size() < 2) {
            gameState = eGameState.Won;
        }

        playedTurnData.setGameState(gameState);
        playedTurnData.setTurnType(turnParameters.getmTurnType());

        // Don't update game status, it was done in GameStatus.CurrentPlayerQuit.
        this.finishedPlayingTurn(playedTurnData, false);

        return playedTurnData;
    }

    public boolean isGameActive(){
        return (mGameStatus != null && mGameStatus.getGameState() == eGameState.Active);
    }

    private PlayedTurnData updateGameStatusAfterDiscAdded(Cell updatedCell) {
        eGameState currentGameState = this.getCurrentGameState(updatedCell);
        PlayedTurnData playedTurnData = new PlayedTurnData();
        List<Cell> updatedCells = new ArrayList<>();

        updatedCells.add(updatedCell);
        playedTurnData.setUpdatedCellsCollection(updatedCells);
        playedTurnData.setPlayerTurn(updatedCell.getPlayer());
        playedTurnData.setGameState(currentGameState);

        return playedTurnData;
    }

    private eGameState getCurrentGameState(Cell updatedCell) {
        eGameState gameState;

        if(mSequenceSearcher.CheckCellForWinningSequence(updatedCell)) {
            gameState = eGameState.Won;
        } else if (this.isDrawForNextPlayer()) {
            gameState = eGameState.Draw;
        } else {
            gameState = eGameState.Active;
        }

        return gameState;
    }

    public List<PlayedTurnData> GetTurnHistory() {
        // send parameters to history manager
        return this.mHistoryManager.GetGameHistory();
    }

    public Player GetCurrentPlayer() {
        return this.mGameStatus.getPlayer();
    }

    public void SaveGame() throws IOException, ClassNotFoundException, Exception {
        HistoryFileManager.SaveGameHistoryInXMLFile(GameSettings.getSavedGameFileName(), mHistoryManager.GetGameHistory());
    }

    public Board getBoard() {
        return mGameBoard;
    }

    public eGameState GetGameState() {
        return mGameStatus.getGameState();
    }

    public void exitGame() {
        this.mGameBoard.Clear();
        this.mHistoryManager.Clear();
        this.mSequenceSearcher.Clear();
        GameSettings.getInstance().Clear();
        SequenceSearcherStrategyFactory.ClearCache();
        this.mTurnsBlockingQueue.clear();
    }

    public List<Integer> getAvailablePopoutColumnsForCurrentPlayer() {
        return mGameBoard.getAvailablePopoutColumnsForCurrentPlayer(mGameStatus.mCurrentPlayer);
    }

    private boolean isDrawForNextPlayer() {
        boolean isPopoutGameMode = GameSettings.getInstance().getVariant().equals(eVariant.Popout);
        // Check if next player can perform popout. If he can't, and the board is full - game is in a draw.
        boolean canNextPlayerPopout = this.mGameBoard.CanPlayerPerformPopout(this.mGameStatus.getNextPlayer());
        boolean isBoardFull = this.mGameBoard.getIsBoardFull();
        boolean isDraw;

        if(!isPopoutGameMode && isBoardFull) {
            // Not in popout mode and board is full
            isDraw = true;
        } else if (isPopoutGameMode && isBoardFull && !canNextPlayerPopout) {
            // In popout mode, board is full and next player can't popout
            isDraw = true;
        } else {
            isDraw = false;
        }

        return isDraw;
    }
    // Turns blocking queue

    public void playTurnAsync(PlayTurnParameters turnParameters) {
        // (At least for exercise 2) UI thread executes this code.
        this.mDelegate.turnInProgress();
        this.mTurnsBlockingQueue.offer(turnParameters);
    }

    // This method is executed in a background thread.
    private void startConsumingPlayerTurns() {

        // While game is active.
        while(true) {
            PlayTurnParameters playedTurnParams;

            try {
                playedTurnParams = this.mTurnsBlockingQueue.take();
            } catch (InterruptedException e) {
                break;
            }

            // If game has ended, break.
            if (!this.mGameStatus.getGameState().equals(eGameState.Active)) {
                break;
            }

            this.executeTurn(playedTurnParams);

            // Play computer player if needed.
            if (this.mGameStatus.getPlayer().getType().equals(ePlayerType.Computer)) {
                PlayComputerPlayerTask playComputerPlayerTask = new PlayComputerPlayerTask(this::playComputerAlgoTurn);
                new Thread(playComputerPlayerTask).start();
            } else {
                this.mDelegate.finishedTurn(); // If the computer player is not playing next, signal there is no turn in progress.
            }
        }

        this.mTurnsBlockingQueue.clear(); // Clear any remaining params to consume when game ended.
    }

    private void executeTurn(PlayTurnParameters playedTurnParams) {
        switch(playedTurnParams.getmTurnType()) {
            case AddDisc:
                this.executeAddDisc(playedTurnParams);
                break;
            case Popout:
                this.executePopout(playedTurnParams);
                break;
            case PlayerQuit:
                this.executePlayerQuit(playedTurnParams);
                break;
        }
    }

    private void executeAddDisc(PlayTurnParameters playedTurnParams) {
        try {
            PlayedTurnData turnData = this.playTurn(playedTurnParams);
            this.mDelegate.onTurnPlayedSuccess(turnData);
        } catch (InvalidInputException e) {
            e.printStackTrace();
            this.mDelegate.discAddedToFullColumn(playedTurnParams.getmSelectedColumn());
        }
    }

    private void executePopout(PlayTurnParameters playedTurnParams) {
        try {
            PlayedTurnData turnData = this.playTurn(playedTurnParams);
            this.mDelegate.onTurnPlayedSuccess(turnData);
        } catch (InvalidInputException e) {
            e.printStackTrace();
            this.mDelegate.currentPlayerCannotPopoutAtColumn(playedTurnParams.getmSelectedColumn());
        }
    }

    private void executePlayerQuit(PlayTurnParameters playedTurnParams) {
        PlayedTurnData turnData = this.currentPlayerQuit(playedTurnParams);
        this.mDelegate.onTurnPlayedSuccess(turnData);
    }

    public class GameStatus implements IGameStatus {
        private eGameState mGameState = eGameState.Inactive;
        private Player mCurrentPlayer;
        private ListIterator<Player> mPlayerIterator;
        private Instant mGameStart;

        // Getters/Setters

        public Player getPlayer() {
            return mCurrentPlayer;
        }

        @Override
        public eGameState getGameState() { return mGameState; }

        @Override
        public String getNameOfPlayerCurrentlyPlaying() {
            return mCurrentPlayer.getName();
        }

        @Override
        public Duration getGameDuration() {
            return Duration.between(mGameStart, Instant.now());
        }

        // API

        private void StartNewGame() {
            this.mPlayerIterator = GameSettings.getInstance().getPlayers().listIterator();
            this.mCurrentPlayer = this.mPlayerIterator.next();
            this.mGameState = eGameState.Active;
            GameSettings.getInstance().getPlayers().forEach(
                    (player) -> player.setTurnCounter(0) // Reset turn counter
            );
            this.mGameStart = Instant.now();
        }

        // Adjust game status when a player has finished his turn.
        private void FinishedTurn() {
            this.mCurrentPlayer.FinishedTurn();
            this.nextPlayer();
        }

        private void CurrentPlayerQuitGame() {
            Player quittingPlayer = this.mCurrentPlayer;
            this.nextPlayer(); // Set next player.
            // Remove player by ID
            GameSettings.getInstance().getPlayers().remove(quittingPlayer);
        }

        // Helper functions

        private void nextPlayer() {
            // If done iterating over players, reset iterator.
            if(!this.mPlayerIterator.hasNext()) {
                this.mPlayerIterator = GameSettings.getInstance().getPlayers().listIterator();
            }

            this.mCurrentPlayer = this.mPlayerIterator.next();
        }

        private Player getNextPlayer() {
            Player nextPlayer;

            if(this.mPlayerIterator.hasNext()) {
                nextPlayer = this.mPlayerIterator.next(); // Go next and then set back to previous element
                this.mPlayerIterator.previous();
            } else {
                nextPlayer = GameSettings.getInstance().getPlayers().iterator().next(); // Get first element.
            }

            return nextPlayer;
        }
    }
}
