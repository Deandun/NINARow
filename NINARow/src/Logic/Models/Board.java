package Logic.Models;

import Logic.Exceptions.InvalidUserInputException;

public class Board {
    private Cell[][] mBoard;

    public Board(int numRows, int numCols) {
        this.mBoard = new Cell[numRows][numCols];

        for(int i = 0; i < numRows; i++) {
            for(int j = 0; j < numRows; j++) {
                mBoard[i][j] = new Cell();
            }
        }
    }

    public Cell[][] getBoard() {
        return mBoard;
    }

    public Cell UpdateBoard(int column, Player player) throws Exception, InvalidUserInputException {
        boolean isColumnFull = true;
        Cell chosenCell = null;

        // Insert to first available cell in column (max available row) if there is one.
        // Starts from highest row and makes its way down.
        for (int row = mBoard.length - 1; row >= 0; row--) {
            if (mBoard[row][column].isEmpty()) {
                mBoard[row][column].setPlayer(player); //found empty cell in column
                isColumnFull = false;
                chosenCell = mBoard[row][column];
                break;
            }
        }

        if (isColumnFull){
            throw new InvalidUserInputException("Cannot insert to column " + column + " because it is Full!");
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
}
