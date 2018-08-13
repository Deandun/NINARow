package Logic.Models;

import Logic.Exceptions.InvalidUserInputException;

import java.util.Arrays;

public class Board{
    private Cell[][] mBoard;

    public Board(int numRows, int numCols) {
        this.Init(numRows, numCols);
    }

    public void Init(int numRows, int numCols) {
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
        if(IsColumnFull(column)) {
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

    public boolean CanPlayerPerformPopout(Player player) {
        int lastRowIndex = mBoard.length - 1;

        // Check if there is a disc at the bottom of any of the columns.
        return Arrays.stream(mBoard[lastRowIndex]).anyMatch(
                    (cell) -> cell.getPlayer().equals(player)
                );
    }

    public boolean CanPlayerPerformPopoutForColumn(Player player, int column) {
        int lastRowIndex = mBoard.length - 1;

        // Check the bottom most disc of the column.
        return mBoard[lastRowIndex][column].getPlayer().equals(player);
    }

    public void Popout(int column) {
        removeCellFromIndex(mBoard.length - 1, column); // Remove cell from the bottom most row at the selected column.
    }

    public void RemoveAllPlayerDiscsFromBoard(Player player) {
        for(int i = 0; i < mBoard.length; i++) { // Go over rows.
            for(int j = 0; j < mBoard[i].length; j++) { // Go over columns.
                if(mBoard[i][j].getPlayer() != null && mBoard[i][j].getPlayer().equals(player)) { // Check if the cell was set by the player that quit.
                    removeCellFromIndex(i, j); // Remove cell from board.
                }
            }
        }
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
            if(!IsColumnFull(i)) {
                isBoardFull = false; // Found a column that is not full.
                break;
            }
        }

        return isBoardFull;
    }

    public boolean IsColumnFull(int columnIndex) {
        return !mBoard[0][columnIndex].isEmpty();
    }

    private void removeCellFromIndex(int row, int column) {
        Player upperCellPlayer;

        // Go over the discs of the selected column starting from the selected row and update the player in the cell
        // until reaching null or the start of the column.
        for(int i = row; i > 0; i--) {
            upperCellPlayer = mBoard[i - 1][column].getPlayer();
            mBoard[i][column].setPlayer(upperCellPlayer);

            if(upperCellPlayer == null) {
                break; // Reached the up most disc in board, break.
            }
        }

        mBoard[0][column].setPlayer(null); // After this function, the up most disc in the column should always be null.
    }
}
