package Logic.Models;

import Logic.Exceptions.InvalidUserInputException;

public class Cell {
    private Player mPlayer;
    private int mColumnIndex;
    private int mRowIndex;

    public boolean isEmpty() {
        return mPlayer == null;
    }

    public void setPlayer(Player player) throws Exception {
        if (this.isEmpty()){
            this.mPlayer = player;
        }
        else{
            throw new InvalidUserInputException("Cell is already taken!");
        }
    }

    public void Clear(){
        mPlayer = null;
    }

    public int getColumnIndex() {
        return mColumnIndex;
    }

    public void setColumnIndex(int columnIndex) {
        this.mColumnIndex = columnIndex;
    }

    public int getRowIndex() {
        return mRowIndex;
    }

    public void setRowIndex(int rowIndex) {
        this.mRowIndex = rowIndex;
    }

    public Player getPlayer() {
        return mPlayer;
    }
}
