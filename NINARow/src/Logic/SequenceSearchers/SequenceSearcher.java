package Logic.SequenceSearchers;

import Logic.Enums.eSequenceSearcherType;
import Logic.Interfaces.ISequenceSearcherStrategy;
import Logic.Models.Board;
import Logic.Models.Cell;
import Logic.Models.GameSettings;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

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

    public boolean DidPlayerWinGameInRecentTurn(Cell updatedCell) {
        boolean isHorizontalSequence = checkHorizontalSequence(updatedCell);
        boolean isVerticalSequence = checkVerticalSequence(updatedCell);
        boolean isDiagonalSequence = checkDiagonalSequence(updatedCell);

        return isHorizontalSequence || isVerticalSequence || isDiagonalSequence;
    }

    private int getSequence(Cell updatedCell, ISequenceSearcherStrategy sequenceSearcher) {
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

    // Check horizontal sequence: "---"
    private boolean checkHorizontalSequence(Cell sequenceStartingCell) {
        // Check for sequence to the right
        ISequenceSearcherStrategy rightSequenceSearcher = SequenceSearcherStrategyFactory.getSequenceSearcherStrategyForType(eSequenceSearcherType.Right);
        int rightSequence = getSequence(sequenceStartingCell, rightSequenceSearcher);

        // Check for sequence to the left
        ISequenceSearcherStrategy leftSequenceSearcher = SequenceSearcherStrategyFactory.getSequenceSearcherStrategyForType(eSequenceSearcherType.Left);
        int leftSequence = getSequence(sequenceStartingCell, leftSequenceSearcher);

        return rightSequence + leftSequence - 1 >= GameSettings.getInstance().getTarget();
    }

    // Check vertical sequence:
    //      |
    //      |
    //      |
    private boolean checkVerticalSequence(Cell sequenceStartingCell) {
        // Check for sequence to the top
        ISequenceSearcherStrategy topSequenceSearcher = SequenceSearcherStrategyFactory.getSequenceSearcherStrategyForType(eSequenceSearcherType.Top);
        int topSequence = getSequence(sequenceStartingCell, topSequenceSearcher);

        // Check for sequence to the bottom
        ISequenceSearcherStrategy bottomSequenceSearcher = SequenceSearcherStrategyFactory.getSequenceSearcherStrategyForType(eSequenceSearcherType.Bottom);
        int bottomSequence = getSequence(sequenceStartingCell, bottomSequenceSearcher);

        return topSequence + bottomSequence - 1 >= GameSettings.getInstance().getTarget();
    }

    // Check diagonal sequences:
    //  \      OR       /
    //   \             /
    //    \           /
    private boolean checkDiagonalSequence(Cell sequenceStartingCell) {
        // Check for sequence to the top-right
        ISequenceSearcherStrategy topRightSequenceSearcher = SequenceSearcherStrategyFactory.getSequenceSearcherStrategyForType(eSequenceSearcherType.TopRight);
        int topRightSequence = getSequence(sequenceStartingCell, topRightSequenceSearcher);

        // Check for sequence to the bottom-left
        ISequenceSearcherStrategy bottomLeftSequenceSearcher = SequenceSearcherStrategyFactory.getSequenceSearcherStrategyForType(eSequenceSearcherType.BottomLeft);
        int bottomLeftSequence = getSequence(sequenceStartingCell, bottomLeftSequenceSearcher);

        // Check for sequence to the top-left
        ISequenceSearcherStrategy topLeftSequenceSearcher = SequenceSearcherStrategyFactory.getSequenceSearcherStrategyForType(eSequenceSearcherType.TopLeft);
        int topLeftSequence = getSequence(sequenceStartingCell, topLeftSequenceSearcher);

        // Check for sequence to the bottom-right
        ISequenceSearcherStrategy bottomRightSequenceSearcher = SequenceSearcherStrategyFactory.getSequenceSearcherStrategyForType(eSequenceSearcherType.BottomRight);
        int bottomRightSequence = getSequence(sequenceStartingCell, bottomRightSequenceSearcher);

        int target = GameSettings.getInstance().getTarget();

        return topRightSequence + bottomLeftSequence - 1 >= target || topLeftSequence + bottomRightSequence - 1 >= target;
    }
}
