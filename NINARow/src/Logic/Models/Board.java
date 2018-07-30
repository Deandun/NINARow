package Logic.Models;

import Logic.Exceptions.InvalidUserInputException;

public class Board {
    // TODO: get board size from GameSettings singleton (get game size from file).
    private Cell[][] mBoard;

    public Board(int numRows, int numCols) {
        this.mBoard = new Cell[numRows][numCols];
    }

    public Cell[][] getBoard() {
        return mBoard;
    }

    public Cell UpdateBoard(int column, Player player) throws Exception {
        boolean isColumnFull = true;
        Cell chosenCell = null;

        // Insert to first available cell in column (max available row) if there is one.
        for (Cell cell : mBoard[column]) {
            if (cell.isEmpty()) {
                cell.setPlayer(player); //found empty cell in column
                isColumnFull = false;
                chosenCell = cell;
                break;
                }
            }

        if (isColumnFull){
            throw new InvalidUserInputException("Cannot insert to column because it is Full!");
        }
        return chosenCell;
    }
    /*

     public void UpdateBoard(int column, Player player) {
        boolean isColumnFull = true;

        try {
            // Insert to first available cell in column (max available row) if there is one.
            for (Cell cell : mBoard[column]) {
                if (cell.isEmpty()) {
                    try {                  //TODO: Ugly code. Should we remove the inner try&catch?
                        cell.setPlayer(player);
                    } catch (Exception e) {
                        throw new InvalidUserInputException(e.getMessage());
                    }
                    isColumnFull = false;
                }
            }
            if (isColumnFull){
                throw new InvalidUserInputException("Cannot insert to column because it is Full!");
            }
        }catch (InvalidUserInputException e){
            e.printStackTrace();
        }
    }
     */

    public void Clear() {
        for(Cell[] column : mBoard){
            for (Cell cell : column){
                cell.Clear();
            }
        }
    }
}
