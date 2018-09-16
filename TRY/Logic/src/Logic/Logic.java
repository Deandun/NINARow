package Logic;

import Logic.ComputerPlayer.ComputerPlayerAlgo;
import Logic.Enums.eGameState;
import Logic.Enums.ePlayerType;
import Logic.Enums.eTurnType;
import Logic.Enums.eVariant;
import Logic.Exceptions.InvalidFileInputException;
import Logic.Exceptions.InvalidInputException;
import Logic.Exceptions.InvalidUserInputException;
import Logic.Interfaces.IComputerPlayerAlgo;
import Logic.Interfaces.IGameStatus;
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

public class Logic{

    private FileManager mFileManager;
    private HistoryManager mHistoryManager;
    private GameStatus mGameStatus;
    private Board mGameBoard;
    private SequenceSearcher mSequenceSearcher;
    private IComputerPlayerAlgo mComputerPlayerAlgo;

    public Logic() {
        this.mFileManager = new FileManager();
        this.mHistoryManager = new HistoryManager();
        this.mGameStatus = new GameStatus();
        this.mSequenceSearcher = new SequenceSearcher();
        this.mComputerPlayerAlgo = new ComputerPlayerAlgo();
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

    private void executeTurn(PlayTurnParameters playTurnParameters) throws InvalidInputException {
        // Handle human players turn.
        PlayedTurnData playedTurnData = playTurnParameters.getmTurnType().equals(eTurnType.AddDisc) ?
                this.addDisc(playTurnParameters.getmSelectedColumn()) : this.popout(playTurnParameters.getmSelectedColumn());

        playedTurnData.setTurnType(playTurnParameters.getmTurnType());

        // Update game state when turn ends.
        this.finishedPlayingTurn(playedTurnData);
    }

    private void playComputerAlgoTurn() throws InvalidInputException {
        if(this.mComputerPlayerAlgo.hasNextPlay(this.mGameStatus.mCurrentPlayer)) {
            // Use algo to determine next computer turn.
            PlayTurnParameters computerPlayTurnParams = this.mComputerPlayerAlgo.getNextPlay(this.mGameStatus.mCurrentPlayer);
            this.playTurn(computerPlayTurnParams);
        } else {
            // Computer player cannot make a play - Draw!
            // In Ex02 we shouldn't get to this situation because a draw would've been determined after the previous turn.
            // Check again in Ex03
        }
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
            // Cannot draw after popout - only check if a player won.
            playedTurnData.setGameState(this.mSequenceSearcher.CheckColumnForWinningSequences(column) ? eGameState.Won : eGameState.Active);
            playedTurnData.setPlayerTurn(this.mGameStatus.mCurrentPlayer);
            playedTurnData.setTurnType(eTurnType.Popout);

            // Check if there is a winning sequence starting from a cell in the selected column as a result of the Popout.
            return playedTurnData;
        } else {
            throw new InvalidUserInputException("Player named " + this.mGameStatus.getPlayer().getName() + " cannot perform popout on column " + column);
        }
    }

    private void currentPlayerQuit(PlayTurnParameters turnParameters) {
        PlayedTurnData playedTurnData = new PlayedTurnData();
        eGameState gameState = eGameState.Active;

        // If there are more than 2 players, the game will go on after player quits.
        Collection<Cell> updatedCells = this.mGameBoard.RemoveAllPlayerDiscsFromBoardAndGetUpdatedCells(this.mGameStatus.getPlayer());
        playedTurnData.setUpdatedCellsCollection(updatedCells);

        if(GameSettings.getInstance().getPlayers().size() > 2) {
            if (this.mSequenceSearcher.CheckEntireBoardForWinningSequences()){  //run all over board and check if someone won
                gameState = eGameState.Won;
            } else if(this.isDrawForNextPlayer()) {
                gameState = eGameState.Draw;
            }
        } else {
            gameState = eGameState.Won;
        }

        this.mGameStatus.CurrentPlayerQuitGame();

        playedTurnData.setGameState(gameState);
        playedTurnData.setTurnType(turnParameters.getmTurnType());

        // Don't update game status, it was done in GameStatus.CurrentPlayerQuit.
        this.finishedPlayingTurn(playedTurnData, false);
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
    }

    public List<Integer> getAvailablePopoutColumnsForCurrentPlayer() {
        return mGameBoard.getAvailablePopoutColumnsForCurrentPlayer(mGameStatus.mCurrentPlayer);
    }

    private boolean isDrawForNextPlayer() {
        boolean isPopoutGameMode = GameSettings.getInstance().getVariant().equals(eVariant.Popout);
        // Check if next player can perform popout. If he can't, and the board is full - game is in a draw.
        boolean canNextPlayerPopout = this.mGameBoard.CanPlayerPerformPopout(this.mGameStatus.getNextPlayer());
        boolean isBoardFull = this.mGameBoard.IsBoardFull();
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

    public void playTurn(PlayTurnParameters playedTurnParams) throws InvalidInputException {
        // Check if game is active.
        if(this.mGameStatus.getGameState().equals(eGameState.Active)) {
            // Check turn type
            if (playedTurnParams.getmTurnType().equals(eTurnType.PlayerQuit)) {
                this.currentPlayerQuit(playedTurnParams);
            } else {
                this.executeTurn(playedTurnParams);
            }

            // Play computer algo if needed.
            if (this.mGameStatus.mCurrentPlayer.getType().equals(ePlayerType.Computer)) {
                this.playComputerAlgoTurn();
            }
        }
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
            // Was unable to perform this logic by using Iterator's remove function. Instead, using the following brute force technique.
            Player quittingPlayer = this.mCurrentPlayer;
            int quittingPlayerIndex = GameSettings.getInstance().getPlayers().indexOf(quittingPlayer);

            GameSettings.getInstance().getPlayers().remove(quittingPlayer); // Remove current player.
            this.mPlayerIterator = GameSettings.getInstance().getPlayers().listIterator(quittingPlayerIndex); // Reset iterator after list was changed.
            this.nextPlayer(); // Assign the current player to the iterator's next element.
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
