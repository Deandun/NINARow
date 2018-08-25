package Logic;

import Logic.ComputerPlayer.ComputerPlayerAlgo;
import Logic.Enums.eGameState;
import Logic.Enums.ePlayerType;
import Logic.Enums.eTurnType;
import Logic.Exceptions.InvalidFileInputException;
import Logic.Exceptions.InvalidUserInputException;
import Logic.Interfaces.IComputerPlayerAlgo;
import Logic.Interfaces.IGameStatus;
import Logic.Interfaces.ILogic;
import Logic.Managers.FileManager;
import Logic.Managers.HistoryFileManager;
import Logic.Managers.HistoryManager;
import Logic.Models.*;
import Logic.SequenceSearchers.SequenceSearcher;

import javax.xml.bind.JAXBException;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public class Logic implements ILogic {

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
        this.mSequenceSearcher.setBoard(this.mGameBoard);
        this.mComputerPlayerAlgo.Init(this.mGameBoard);
        this.mHistoryManager.Clear();
        this.mGameStatus.StartNewGame();
    }

    public Map<Player, Collection<Cell>> getPlayerToWinningSequencesMap() {
        return this.mSequenceSearcher.getPlayerToWinningSequencesMap();
    }

    public GameStatus GetGameStatus() {
        return mGameStatus;
    }

    @Override
    public List<PlayedTurnData> PlayTurn(PlayTurnParameters playTurnParameters) throws InvalidUserInputException, Exception {
        // Handle human players turn.
        PlayedTurnData playedTurnData = playTurnParameters.getmTurnType().equals(eTurnType.AddDisc) ?
                this.addDisc(playTurnParameters.getmSelectedColumn()) : this.popout(playTurnParameters.getmSelectedColumn());

        // Update game state when turn ends.
        this.finishedPlayingTurn(playedTurnData);

        List<PlayedTurnData> playedTurnDataList = new ArrayList<>();

        playedTurnDataList.add(playedTurnData);
        playedTurnDataList.addAll(this.playComputerAlgoGamesIfNeededAndGetData()); // Add all of the computer algo's turns to the list.

        return playedTurnDataList;
    }

    private List<PlayedTurnData> playComputerAlgoGamesIfNeededAndGetData() throws Exception {
        List<PlayedTurnData> playedTurnDataList = new ArrayList<>();
        boolean isComputerPlayerPlayingNext = this.mGameStatus.mPlayer.getType().equals(ePlayerType.Computer);
        boolean shouldPlayAnotherTurn = true;

        while(shouldPlayAnotherTurn && isComputerPlayerPlayingNext) { // While current player is a computer.
            // Use algo to determine next computer turn.
            PlayTurnParameters computerPlayTurnParams = this.mComputerPlayerAlgo.getNextPlay(this.mGameStatus.mPlayer);
            // Play add disc or popout.
            PlayedTurnData playedTurnData = computerPlayTurnParams.getmTurnType().equals(eTurnType.AddDisc) ?
                    this.addDisc(computerPlayTurnParams.getmSelectedColumn()) : this.popout(computerPlayTurnParams.getmSelectedColumn());

            playedTurnDataList.add(playedTurnData);
            this.finishedPlayingTurn(playedTurnData);

            isComputerPlayerPlayingNext = this.mGameStatus.mPlayer.getType().equals(ePlayerType.Computer);
            shouldPlayAnotherTurn = playedTurnData.getGameState().equals(eGameState.Active); // Stop playing computer turns when game has ended.
        }

        return playedTurnDataList;
    }

    private void finishedPlayingTurn(PlayedTurnData playedTurnData) {
        this.mGameStatus.mGameState = playedTurnData.getGameState();
        this.mHistoryManager.SetCurrentTurn(playedTurnData);
        this.mGameStatus.FinishedTurn();
    }

    private PlayedTurnData addDisc(int column) throws InvalidUserInputException, Exception {
        Cell chosenCell = this.mGameBoard.UpdateBoard(column, this.mGameStatus.getPlayer()); // send parameter to logic board
        return this.updateGameStatusAfterDiscAdded(chosenCell);
    }

    private PlayedTurnData popout(int column) {
        PlayedTurnData playedTurnData = new PlayedTurnData();
        Collection<Cell> updatedCells = this.mGameBoard.PopoutAndGetUpdatedCells(column - 1);

        playedTurnData.setUpdatedCellsCollection(updatedCells);
        playedTurnData.setGameState(this.mSequenceSearcher.CheckColumnForWinningSequences(column) ? eGameState.Won : eGameState.Active);
        playedTurnData.setPlayerTurn(this.mGameStatus.mPlayer);
        playedTurnData.setTurnType(eTurnType.Popout);

        this.finishedPlayingTurn(playedTurnData);

        // Check if there is a winning sequence starting from a cell in the selected column as a result of the Popout.
        return playedTurnData;
    }

    public List<PlayedTurnData> playComputerPlayersTurns() throws Exception {
        return this.playComputerAlgoGamesIfNeededAndGetData();
    }


    public eGameState PlayerQuit(Player player) {
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

        return gameState;
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

    @Override
    public List<PlayedTurnData> GetTurnHistory() {
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

//    @Override
//    public void LoadExistsGame() throws IOException, ClassNotFoundException, Exception {
//        String path = GameSettings.getSavedGameFileName();
//        File loadedFile = new File(path);
//
//        if (loadedFile.exists()) {
//            List<PlayedTurnData> loadedGameTurnHistory = HistoryFileManager.ReadGameHistoryFromXMLFile(path);
//            StartGame();
//
//            if (loadedGameTurnHistory != null) {
//                for (PlayedTurnData turn : loadedGameTurnHistory) {
//                    this.PlayTurn(turn.getUpdatedCell().getColumnIndex());
//                }
//            }
//        } else {
//            throw new FileNotFoundException("Cannot find file: " + path);
//        }
//    }

    @Override
    public Board getBoard() {
        return mGameBoard;
    }

    @Override
    public eGameState GetGameState() {
        return mGameStatus.getGameState();
    }

    public void exitGame() {
        //TODO: implement
    }

    public List<Integer> getAvailablePopoutColumnsForCurrentPlayer() {
        return mGameBoard.getAvailablePopoutColumnsForCurrentPlayer(mGameStatus.mPlayer);
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
