package Logic;

import Logic.Interfaces.IComputerPlayerAlgo;
import Logic.Models.Board;
import Logic.Models.GameSettings;

import java.util.Random;

public class ComputerPlayerAlgo implements IComputerPlayerAlgo {
    private Board mBoard; // The game board.

    public void Init(Board board) {
        this.mBoard = board;
    }

    public int getNextPlay() {
        int selectedColumn;

        for(int i = 0; i < GameSettings.getInstance().getColumns(); i++) {
            if(!mBoard.IsColumnFull(i)) {
                selectedColumn = i;
                return selectedColumn;
            }
        }

        throw new RuntimeException("Board is full, Logic.ComputerPlayerAlgo cannot select an available column." +
                " Game should have already ended in a draw");
    }

    public int getNextPopOutPlay(){ //TODO : use
        try {
            return getNextPlay();
        }catch (RuntimeException e){
            return new Random().nextInt(GameSettings.getInstance().getColumns());
        }
    }
}
