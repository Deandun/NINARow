package Logic;

import Logic.Exceptions.InvalidFileInputException;
import Logic.Managers.FileManager;
import Logic.Managers.HistoryManager;
import Logic.Models.*;

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

    //@Override
    public void ReadGameFile(String filePath) throws InvalidFileInputException {
        //TODO: init GameSettings by params in file from filePath

        //this.mGameBoard = new Board(10, 10); //TODO: change numbers to file's input
        this.mGameBoard = new Board(GameSettings.getInstance().getRows(), GameSettings.getInstance().getColumns());
    }


    //@Override
    public void StartGame() {
        if (!mGameStatus.isIsGameActive()) {
            //TODO: ask each player if he is human or computer and update players in GameSettings
            mGameStatus.StartNewGame(GameSettings.getInstance().getPlayers().get(0));
        }
    }

    //@Override
    public String GetGameStatus() {
        return mGameStatus.toString();
    }

    //@Override
    public void PlayTurn(int column) {

        try {
            Cell chosenCell = mGameBoard.UpdateBoard(column, mGameStatus.getPlayer()); // send parameter to logic board
            mHistoryManager.SaveTurn(chosenCell, GameSettings.getInstance().getPlayers().get(mGameStatus.getPlayerIndex()));
            checkIfGameFinish();
            //update GameStatus in case succeed
            mGameStatus.increaseTurnNumber();
            mGameStatus.increasePlayerIndex();
            mGameStatus.setPlayer(GameSettings.getInstance().getPlayers().get(mGameStatus.getPlayerIndex()));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //@Override
    private void checkIfGameFinish() {
        //Check if following this turn - player won
    }

    //@Override
    public List<PlayerTurn> GetTurnHistory() {
        // send parameters to history manager
        return mHistoryManager.GetGameHistory();
    }

    private class GameStatus{
        private boolean mIsGameActive = false;
        private int mTurn = 0;
        private Player mPlayer;
        private int mPlayerIndex;

        public void StartNewGame(Player player){
            this.mIsGameActive = true;
            this.mTurn = 0;
            this.mPlayerIndex = 0;
            this.mPlayer = player;
        }

        @Override
        public String toString(){
            String gameActivation = "Game is ";

            if (mIsGameActive){
                gameActivation += "active";
            }
            else{
                gameActivation += "not active";
            }

            return gameActivation + ", Turn number is " + mTurn + ", Player turn is: " + this.mPlayer.getName(); //TODO: check player's get name
        }

        public void increaseTurnNumber(){
            mTurn ++;
        }

        public void increasePlayerIndex(){
            mPlayerIndex++;
        }
        public boolean isIsGameActive() {
            return mIsGameActive;
        }

        public int getTurn() {
            return mTurn;
        }

        public Player getPlayer() {
            return mPlayer;
        }

        public void setPlayer(Player mPlayer) {
            this.mPlayer = mPlayer;
        }

        public int getPlayerIndex() {
            return mPlayerIndex;
        }

        public void setPlayerIndex(int mPlayerIndex) {
            this.mPlayerIndex = mPlayerIndex;
        }
    }
}
