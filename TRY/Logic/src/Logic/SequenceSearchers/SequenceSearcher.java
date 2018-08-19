package Logic.SequenceSearchers;

import Logic.Enums.eSequenceSearcherType;
import Logic.Interfaces.ISequenceSearcherStrategy;
import Logic.Models.Board;
import Logic.Models.Cell;
import Logic.Models.GameSettings;

import java.util.*;

public class SequenceSearcher {
    private Cell[][] mBoard;
    private Map<String, Collection<Cell>> mPlayerID2WinningSequence = new HashMap<>(); // A map between a player's ID and his winning sequence (if exists)

    public void setBoard(Board board) {
        mBoard = board.getCellArray();
    }

    public Map<String, Collection<Cell>> getWinningSequencesMap() {
        return mPlayerID2WinningSequence;
    }

    public void Clear() {
        mPlayerID2WinningSequence.clear();
        mBoard = null;
    }

    public boolean CheckEntireBoardForWinningSequences() {
        // Check for winning sequences in all of the boards columns.
        for(int i = 0; i < GameSettings.getInstance().getColumns(); i++) {
            this.CheckColumnForWinningSequences(i);
        }

        return !mPlayerID2WinningSequence.isEmpty(); // If winning sequence is found, winning sequences map won't be empty
    }

    public boolean CheckColumnForWinningSequences(int column) {
        // Check for winning sequences in all of the column's cells
        for(int i = 0; i < GameSettings.getInstance().getRows(); i++) {
            this.CheckCellForWinningSequence(mBoard[i][column]);
        }

        return !mPlayerID2WinningSequence.isEmpty(); // If winning sequence is found, winning sequences map won't be empty;
    }

    public boolean CheckCellForWinningSequence(Cell updatedCell) {
        return checkHorizontalSequence(updatedCell) || checkVerticalSequence(updatedCell) || checkDiagonalSequence(updatedCell);
    }

    // Returns the sequence of cells that belong to the same player. The sequence's direction is determined by the chosen strategy.
    private Set<Cell> getSequence(Cell updatedCell, ISequenceSearcherStrategy sequenceSearcher) {
        boolean isSamePlayer = true;
        Set<Cell> cellSequence = new HashSet<>();
        int currentRow = updatedCell.getRowIndex();
        int currentColumn = updatedCell.getColumnIndex();

        while(isSamePlayer) {
            cellSequence.add(mBoard[currentRow][currentColumn]);
            currentRow = sequenceSearcher.GetNextRow(currentRow);
            currentColumn = sequenceSearcher.GetNextColumn(currentColumn);

            if(sequenceSearcher.shouldStopLooking(currentRow, currentColumn)) {
                break;
            } else {
                isSamePlayer = updatedCell.getPlayer().equals(mBoard[currentRow][currentColumn].getPlayer());
            }
        }

        return cellSequence;
    }

    // Connects the sequences from both the first and second strategies. Duplicate cells are dealt with by using a set.
    private Set<Cell> getSequenceFinalSet(Cell sequenceStartingCell, ISequenceSearcherStrategy firstStrategy, ISequenceSearcherStrategy secondStrategy) {
        Set<Cell> firstSequence = getSequence(sequenceStartingCell, firstStrategy);
        Set<Cell> secondSequence = getSequence(sequenceStartingCell, secondStrategy);
        firstSequence.addAll(secondSequence);
        return firstSequence;
    }

    // Check horizontal sequence: "---"
    private boolean checkHorizontalSequence(Cell sequenceStartingCell) {
        ISequenceSearcherStrategy rightSequenceSearcher = SequenceSearcherStrategyFactory.getSequenceSearcherStrategyForType(eSequenceSearcherType.Right);
        ISequenceSearcherStrategy leftSequenceSearcher = SequenceSearcherStrategyFactory.getSequenceSearcherStrategyForType(eSequenceSearcherType.Left);
        Set<Cell> finalSet = getSequenceFinalSet(sequenceStartingCell, rightSequenceSearcher, leftSequenceSearcher);

        boolean isSequenceFound = false;

        if(finalSet.size() >= GameSettings.getInstance().getTarget()) {
            isSequenceFound = true;
            mPlayerID2WinningSequence.put(sequenceStartingCell.getPlayer().getID(), finalSet);
        }

        return isSequenceFound;
    }

    // Check vertical sequence:
    //      |
    //      |
    //      |
    private boolean checkVerticalSequence(Cell sequenceStartingCell) {
        ISequenceSearcherStrategy topSequenceSearcher = SequenceSearcherStrategyFactory.getSequenceSearcherStrategyForType(eSequenceSearcherType.Top);
        ISequenceSearcherStrategy bottomSequenceSearcher = SequenceSearcherStrategyFactory.getSequenceSearcherStrategyForType(eSequenceSearcherType.Bottom);
        Set<Cell> finalSet = getSequenceFinalSet(sequenceStartingCell, topSequenceSearcher, bottomSequenceSearcher);

        boolean isSequenceFound = false;

        if(finalSet.size() >= GameSettings.getInstance().getTarget()) {
            isSequenceFound = true;
            mPlayerID2WinningSequence.put(sequenceStartingCell.getPlayer().getID(), finalSet);
        }

        return isSequenceFound;
    }

    // Check diagonal sequences:
    //  \      OR       /
    //   \             /
    //    \           /
    private boolean checkDiagonalSequence(Cell sequenceStartingCell) {
        // Get bottom left to top right diagonal cell set.
        ISequenceSearcherStrategy topRightSequenceSearcher = SequenceSearcherStrategyFactory.getSequenceSearcherStrategyForType(eSequenceSearcherType.TopRight);
        ISequenceSearcherStrategy bottomLeftSequenceSearcher = SequenceSearcherStrategyFactory.getSequenceSearcherStrategyForType(eSequenceSearcherType.BottomLeft);
        Set<Cell> finalBotLeftToTopRightDiagonalSet = getSequenceFinalSet(sequenceStartingCell, topRightSequenceSearcher, bottomLeftSequenceSearcher);

        // Get top left to bottom right diagonal cell set.
        ISequenceSearcherStrategy topLeftSequenceSearcher = SequenceSearcherStrategyFactory.getSequenceSearcherStrategyForType(eSequenceSearcherType.TopLeft);
        ISequenceSearcherStrategy bottomRightSequenceSearcher = SequenceSearcherStrategyFactory.getSequenceSearcherStrategyForType(eSequenceSearcherType.BottomRight);
        Set<Cell> finalTopLeftToBotRightDiagonalSet = getSequenceFinalSet(sequenceStartingCell, bottomRightSequenceSearcher, topLeftSequenceSearcher);

        boolean isSequenceFound = false;
        int target = GameSettings.getInstance().getTarget();

        if(finalBotLeftToTopRightDiagonalSet.size() >= target) {
            isSequenceFound = true;
            mPlayerID2WinningSequence.put(sequenceStartingCell.getPlayer().getID(), finalBotLeftToTopRightDiagonalSet);
        } else if (finalTopLeftToBotRightDiagonalSet.size() >= target) {
            isSequenceFound = true;
            mPlayerID2WinningSequence.put(sequenceStartingCell.getPlayer().getID(), finalTopLeftToBotRightDiagonalSet);
        }

        return isSequenceFound;
    }
}
