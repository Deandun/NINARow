package Logic.Models;

import Logic.Enums.eGameState;
import Logic.Exceptions.InvalidUserInputException;
import Logic.Interfaces.ISequenceSearcher;
import Logic.SequenceSearchers.*;

import java.io.Serializable;
import java.util.Arrays;

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

    public eGameState getCurrentGameState(Cell updatedCell) {
        eGameState currentGameState;

        if(didPlayerWinGameInRecentTurn(updatedCell)) {
            currentGameState = eGameState.Won;
        } else if (isBoardFull()) {
            currentGameState = eGameState.Draw;
        } else {
            currentGameState = eGameState.Active;
        }

        return currentGameState;
    }



    // Helper functions
    private int getSequence(Cell updatedCell, ISequenceSearcher sequenceSearcher) {
        boolean isSamePlayer = true;
        int sequence = 0;
        int currentRow = updatedCell.getRowIndex();
        int currentColumn = updatedCell.getColumnIndex();

        while(isSamePlayer) {
            sequence++;
            currentRow = sequenceSearcher.GetNextRow(currentRow);
            currentColumn = sequenceSearcher.GetNextColumn(currentColumn);

            if(sequenceSearcher.shouldStopLooking(currentRow, currentColumn)) {
                break;
            } else {
                isSamePlayer = updatedCell.getPlayer().equals(mBoard[currentRow][currentColumn].getPlayer());
            }
        }

        return sequence;
    }

    private boolean didPlayerWinGameInRecentTurn(Cell updatedCell) {
        boolean isHorizontalSequence = checkHorizontalSequence(updatedCell);
        boolean isVerticalSequence = checkVerticalSequence(updatedCell);
        boolean isDiagonalSequence = checkDiagonalSequence(updatedCell);

        return isHorizontalSequence || isVerticalSequence || isDiagonalSequence;
    }

    // Check horizontal sequence: "---"
    private boolean checkHorizontalSequence(Cell sequenceStartingCell) {
        // Check for sequence to the right
        ISequenceSearcher rightSequenceSearcher = new RightSequenceSearcher();
        int rightSequence = getSequence(sequenceStartingCell, rightSequenceSearcher);

        // Check for sequence to the left
        ISequenceSearcher leftSequenceSearcher = new LeftSequenceSearcher();
        int leftSequence = getSequence(sequenceStartingCell, leftSequenceSearcher);

        return rightSequence + leftSequence - 1 >= GameSettings.getInstance().getTarget();
    }

    // Check vertical sequence:
    //      |
    //      |
    //      |
    private boolean checkVerticalSequence(Cell sequenceStartingCell) {
        // Check for sequence to the top
        ISequenceSearcher topSequenceSearcher = new TopSequenceSearcher();
        int topSequence = getSequence(sequenceStartingCell, topSequenceSearcher);

        // Check for sequence to the bottom
        ISequenceSearcher bottomSequenceSearcher = new BottomSequenceSearcher();
        int bottomSequence = getSequence(sequenceStartingCell, bottomSequenceSearcher);

        return topSequence + bottomSequence - 1 >= GameSettings.getInstance().getTarget();
    }

    // Check diagonal sequences:
    //  \      OR       /
    //   \             /
    //    \           /
    private boolean checkDiagonalSequence(Cell sequenceStartingCell) {
        // Check for sequence to the top-right
        ISequenceSearcher topRightSequenceSearcher = new TopRightSequenceSearcher();
        int topRightSequence = getSequence(sequenceStartingCell, topRightSequenceSearcher);

        // Check for sequence to the bottom-left
        ISequenceSearcher bottomLeftSequenceSearcher = new BotLeftSequenceSearcher();
        int bottomLeftSequence = getSequence(sequenceStartingCell, bottomLeftSequenceSearcher);

        // Check for sequence to the top-left
        ISequenceSearcher topLeftSequenceSearcher = new TopLeftSequenceSearcher();
        int topLeftSequence = getSequence(sequenceStartingCell, topLeftSequenceSearcher);

        // Check for sequence to the bottom-right
        ISequenceSearcher bottomRightSequenceSearcher = new BotRightSequenceSearcher();
        int bottomRightSequence = getSequence(sequenceStartingCell, bottomRightSequenceSearcher);

        int target = GameSettings.getInstance().getTarget();

        return topRightSequence + bottomLeftSequence - 1 >= target || topLeftSequence + bottomRightSequence - 1 >= target;
    }

    private boolean isBoardFull() {
        boolean isBoardFull = false;

        // Check if all columns are full
        for(int i = 0; i < GameSettings.getInstance().getColumns(); i++) {
            if(isColumnFull(i)) {
                isBoardFull = true;
                break;
            }
        }

        return isBoardFull;
    }

    public boolean isColumnFull(int columnIndex) {
        return !mBoard[0][columnIndex].isEmpty();
    }
}
