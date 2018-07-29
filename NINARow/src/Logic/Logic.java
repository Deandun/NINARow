package Logic;

import Logic.Managers.FileManager;
import Logic.Managers.HistoryManager;

public class Logic implements ILogic {

    private FileManager mFileManager;
    private HistoryManager mHistoryManager;

    public Logic() {
        this.mFileManager = new FileManager();
        this.mHistoryManager = new HistoryManager();
    }

    //@Override
    public void ReadGameFile(String filePath) {

    }

    //@Override
    public void StartGame() {

        //1. initialize (player turn number, isGameActive, board, clear managers)
    }

    //@Override
    public void GetGameStatus() {
        // init and return game status details (inner class).
    }

    //@Override
    public void PlayTurn(int column) {
        // send parameter to logic board
    }

    //@Override
    public void GetTurnHistory() {
        // send parameters to history manager
    }
}
