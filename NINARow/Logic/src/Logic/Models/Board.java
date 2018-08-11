package Logic.Models;

import Logic.Exceptions.InvalidUserInputException;

public class Board{
    private Cell[][] mBoard;

    public Board(int numRows, int numCols) {
        this.mBoard = new Cell[numRows][numCols];

        for(int i = 0; i < numRows; i++) {
            for(int j = 0; j < numCols; j++) {
                mBoard[i][j] = new Cell();
                mBoard[i][j].setRowIndex(i);
                mBoard[i][j].setColumnIndex(j);
            }
        }
    }

    public Cell[][] getCellArray() {
        return mBoard;
    }

    public Cell UpdateBoard(int column, Player player) throws Exception, InvalidUserInputException {
        // Selected column is full
        if(isColumnFull(column)) {
            throw new InvalidUserInputException("Cannot insert to column " + column + " because it is full!");
        }

        Cell chosenCell = null;

        // Insert to first available cell in column (max available row) if there is one.
        // Starts from highest row and makes its way down.
        for (int row = mBoard.length - 1; row >= 0; row--) {
            if (mBoard[row][column].isEmpty()) {
                mBoard[row][column].setPlayer(player); //found empty cell in column
                chosenCell = mBoard[row][column];
                break;
            }
        }

        return chosenCell;
    }

    public void Clear() {
        for(Cell[] column : mBoard){
            for (Cell cell : column){
                cell.Clear();
            }
        }
    }

    public boolean IsBoardFull() {
        boolean isBoardFull = true;

        // Check if all columns are full
        for(int i = 0; i < GameSettings.getInstance().getColumns(); i++) {
            if(!isColumnFull(i)) {
                isBoardFull = false; // Found a column that is not full.
                break;
            }
        }

        return isBoardFull;
    }

    public boolean isColumnFull(int columnIndex) {
        return !mBoard[0][columnIndex].isEmpty();
    }
}
