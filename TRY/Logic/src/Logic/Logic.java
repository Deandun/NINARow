package Logic;

import Logic.ComputerPlayer.ComputerPlayerAlgo;
import Logic.ComputerPlayer.PlayComputerPlayerTask;
import Logic.Enums.eGameState;
import Logic.Enums.ePlayerType;
import Logic.Enums.eTurnType;
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
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
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
    private boolean mIsTurnInProgress; // A flag that states that another players turn is in progress (user cannot play at this time).
    private BlockingQueue<PlayTurnParameters> mTurnsBlockingQueue;
    private ILogicDelegate mDelegate;

    public Logic(ILogicDelegate logicDelegate) {
        this.mFileManager = new FileManager();
        this.mHistoryManager = new HistoryManager();
        this.mGameStatus = new GameStatus();
        this.mSequenceSearcher = new SequenceSearcher();
        this.mComputerPlayerAlgo = new ComputerPlayerAlgo();
        this.mIsTurnInProgress = false;
        this.mTurnsBlockingQueue = new ArrayBlockingQueue<>(QUEUE_CAPACITY);
        this.mDelegate = logicDelegate;
    }

    // ILogic interface implementation.

    public void ReadGameFile(String filePath, Runnable onLoadFileFinish, Runnable onFinishedCheckingFileValidity) throws FileNotFoundException, InvalidFileInputException, IOException, JAXBException, InterruptedException {
        mFileManager.LoadGameFile(filePath, onLoadFileFinish, onFinishedCheckingFileValidity);
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
        new Thread(this::startConsumingPlayerTurns).start(); // Start listening to player turns on a different thread.

        if(this.mGameStatus.getPlayer().getType().equals(ePlayerType.Computer)) {
            // If computer is playing first - Play computer turn.
            this.mIsTurnInProgress = true;
            PlayComputerPlayerTask playComputerPlayerTask = new PlayComputerPlayerTask(this::playComputerAlgoTurn);
            new Thread(playComputerPlayerTask).start();
        }
    }

    public Map<Player, Collection<Cell>> getPlayerToWinningSequencesMap() {
        return this.mSequenceSearcher.getPlayerToWinningSequencesMap();
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

    // This method is synchronized with the function called startConsumingPlayerTurns to ensure that a human player's turn is not registered
    // Until the computer player's turn is not noticeable to him.
    private synchronized void playComputerAlgoTurn()  {

        // Use algo to determine next computer turn.
        PlayTurnParameters computerPlayTurnParams = this.mComputerPlayerAlgo.getNextPlay(this.mGameStatus.mPlayer);

        this.playTurnAsync(computerPlayTurnParams);

        try {
            Thread.sleep(300);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void finishedPlayingTurn(PlayedTurnData playedTurnData) {
        this.mGameStatus.mGameState = playedTurnData.getGameState();
        this.mHistoryManager.SetCurrentTurn(playedTurnData);
        this.mGameStatus.FinishedTurn();
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
            playedTurnData.setPlayerTurn(this.mGameStatus.mPlayer);
            playedTurnData.setTurnType(eTurnType.Popout);

            // Check if there is a winning sequence starting from a cell in the selected column as a result of the Popout.
            return playedTurnData;
        } else {
            throw new InvalidUserInputException("Player named " + this.mGameStatus.getPlayer().getName() + " cannot perform popout on column " + column);
        }
    }

    //TODO: make adjustments so that player quit will be considered as a played turn data. Save this "turn" in turn history.
    private PlayedTurnData playerQuit(Player player) {
        this.mGameStatus.PlayerQuitGame(player);
        eGameState gameState = eGameState.Active;

        if(GameSettings.getInstance().getPlayers().size() == 1) {//only one player remains, he has won!
           gameState = eGameState.Won;
        } else {
            mGameBoard.RemoveAllPlayerDiscsFromBoard(player);

            if (mSequenceSearcher.CheckEntireBoardForWinningSequences()){  //run all over board and check if someone won
                gameState =  eGameState.Won;
            }
        }

        return null;
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
    }

    public List<Integer> getAvailablePopoutColumnsForCurrentPlayer() {
        return mGameBoard.getAvailablePopoutColumnsForCurrentPlayer(mGameStatus.mPlayer);
    }

    // Turns blocking queue

    public synchronized void playTurnAsync(PlayTurnParameters turnParameters) {
        // (At least for exercise 2) UI thread executes this code.
        this.mIsTurnInProgress = true;
        this.mTurnsBlockingQueue.add(turnParameters);
    }

    // This method is executed in a background thread.
    private void startConsumingPlayerTurns() {

        // While game is active.
        while(true) {
            PlayTurnParameters playedTurnParams;
            try {
                playedTurnParams = this.mTurnsBlockingQueue.take();
            } catch (InterruptedException e) {
                e.printStackTrace(); // TODO: figure out when this happens and react accordingly
                break;
            }

            // Sync between computer player turn and the following code to ensure that a human player cannot play until the computer player's changes
            // Are noticeable. The human player cannot play while mIsTurnInProgress is true or the queue is not empty.
            // At the start of the following sync code block we set mIsTurnInProgress To true, meaning the human player can't make a play (while his UI stays responsive).
            // If the computer is not playing next, mIsTurnInProgress Is set to false, allowing the human player to play.
            // Else, the computer sends his play params to the async queue, and only when all of the computer players are done playing
            // mIsTurnInProgress is set to false.
            synchronized (this) {
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
                    this.mIsTurnInProgress = false; // If the computer player is not playing next, signal there is no turn in progress.
                }
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
        PlayedTurnData turnData = this.playerQuit(playedTurnParams.getPlayer());
        this.mDelegate.onTurnPlayedSuccess(turnData);
    }

    public boolean getIsTurnInProgress() {
        return mIsTurnInProgress && this.mTurnsBlockingQueue.isEmpty();
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

        public void PlayerQuitGame(Player quittingPlayer) {
            int quittingPlayerIndex = GameSettings.getInstance().getPlayers().indexOf(quittingPlayer);
            int currentlyPlayingPlayerIndex = GameSettings.getInstance().getPlayers().indexOf(mPlayer);

            // If quitting player is currently playing, or if the index of the quitting player is greater than
            // that of the player that is currently playing, decrement the playing player's index to accommodate.
            if(quittingPlayer.equals(mPlayer) || quittingPlayerIndex > currentlyPlayingPlayerIndex) {
                mPlayerIndex--;
            }

            GameSettings.getInstance().getPlayers().remove(quittingPlayer);
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

        private void nextPlayer() {
            mPlayer = GameSettings.getInstance().getPlayers().get(getNextPlayerIndex());
        }

        private int getNextPlayerIndex() {
            return ++mPlayerIndex % GameSettings.getInstance().getPlayers().size();
        }

    }
}
