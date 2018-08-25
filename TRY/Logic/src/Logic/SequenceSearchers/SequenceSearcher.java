package Logic.SequenceSearchers;

import Logic.Enums.eSequenceSearcherType;
import Logic.Interfaces.ISequenceSearcherStrategy;
import Logic.Models.Board;
import Logic.Models.Cell;
import Logic.Models.GameSettings;
import Logic.Models.Player;

import java.util.*;

public class SequenceSearcher {
    private Cell[][] mBoard;
    private Map<Player, Collection<Cell>> mPlayerToWinningSequencesMap = new HashMap<>(); // A map between a player's ID and his winning sequence (if exists)

    public void setBoard(Board board) {
        mBoard = board.getCellArray();
    }

    public Map<Player, Collection<Cell>> getPlayerToWinningSequencesMap() {
        return mPlayerToWinningSequencesMap;
    }

    public void Clear() {
        mPlayerToWinningSequencesMap.clear();
        mBoard = null;
    }

    public boolean CheckEntireBoardForWinningSequences() {
        // Check for winning sequences in all of the boards columns.
        for(int i = 0; i < GameSettings.getInstance().getColumns(); i++) {
            this.CheckColumnForWinningSequences(i);
        }

        return !mPlayerToWinningSequencesMap.isEmpty(); // If winning sequence is found, winning sequences map won't be empty
    }

    public boolean CheckColumnForWinningSequences(int column) {
        // Check for winning sequences in all of the column's cells
        for(int i = 0; i < GameSettings.getInstance().getRows(); i++) {
            this.CheckCellForWinningSequence(mBoard[i][column]);
        }

        return !mPlayerToWinningSequencesMap.isEmpty(); // If winning sequence is found, winning sequences map won't be empty;
    }

    public boolean CheckCellForWinningSequence(Cell updatedCell) {
        Player playingPlayer = updatedCell.getPlayer();
        Collection<Cell> horizontalSequenceList = getkHorizontalSequence(updatedCell, playingPlayer);
        Collection<Cell> verticalSequenceList = getVerticalSequence(updatedCell, playingPlayer);
        Collection<Cell> maxDiagonalSequenceList = getMaxDiagonalSequence(updatedCell, playingPlayer);
        Player player = updatedCell.getPlayer();

        return  isSequenceBigEnough(horizontalSequenceList, player) || isSequenceBigEnough(verticalSequenceList, player)
                || isSequenceBigEnough(maxDiagonalSequenceList, player);
    }

    public int getLargestSequenceSize(Cell cell, Player playingPlayer) {
        int horizontalSequenceSize = getkHorizontalSequence(cell, playingPlayer).size();
        int verticalSequenceSize = getVerticalSequence(cell, playingPlayer).size();
        int maxDiagonalSequenceSize = getMaxDiagonalSequence(cell, playingPlayer).size();

        // Return max sequence size.
        return Math.max(Math.max(horizontalSequenceSize, verticalSequenceSize), maxDiagonalSequenceSize);
    }

    private boolean isSequenceBigEnough(Collection<Cell> cellSequence, Player player) {
        boolean isSequenceBigEnough = cellSequence.size() >= GameSettings.getInstance().getTarget();

        if(isSequenceBigEnough) {
            mPlayerToWinningSequencesMap.put(player, cellSequence);
        }

        return isSequenceBigEnough;
    }

    // Returns the sequence of cells that belong to the same player. The sequence's direction is determined by the chosen strategy.
    // The player param represents the player who we are searching the sequence for.
    private Set<Cell> getSequence(Cell updatedCell, ISequenceSearcherStrategy sequenceSearcher, Player playerSequenceIsSearchedFor) {
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
                // True if cell is not empty and cell is occupied by the same player we're sequence searching for.
                isSamePlayer = playerSequenceIsSearchedFor.equals(mBoard[currentRow][currentColumn].getPlayer());
            }
        }

        return cellSequence;
    }

    // Connects the sequences from both the first and second strategies. Duplicate cells are dealt with by using a set.
    private Set<Cell> getSequenceFinalSet(Cell sequenceStartingCell, ISequenceSearcherStrategy firstStrategy, ISequenceSearcherStrategy secondStrategy, Player playingPlayer) {
        Set<Cell> firstSequence = getSequence(sequenceStartingCell, firstStrategy, playingPlayer);
        Set<Cell> secondSequence = getSequence(sequenceStartingCell, secondStrategy, playingPlayer);
        firstSequence.addAll(secondSequence);

        return firstSequence;
    }

    // Check horizontal sequence: "---"
    private Collection<Cell> getkHorizontalSequence(Cell sequenceStartingCell, Player playingPlayer) {
        ISequenceSearcherStrategy rightSequenceSearcher = SequenceSearcherStrategyFactory.getSequenceSearcherStrategyForType(eSequenceSearcherType.Right);
        ISequenceSearcherStrategy leftSequenceSearcher = SequenceSearcherStrategyFactory.getSequenceSearcherStrategyForType(eSequenceSearcherType.Left);

        return getSequenceFinalSet(sequenceStartingCell, rightSequenceSearcher, leftSequenceSearcher, playingPlayer);
    }

    // Check vertical sequence:
    //      |
    //      |
    //      |
    private Collection<Cell> getVerticalSequence(Cell sequenceStartingCell, Player playingPlayer) {
        ISequenceSearcherStrategy topSequenceSearcher = SequenceSearcherStrategyFactory.getSequenceSearcherStrategyForType(eSequenceSearcherType.Top);
        ISequenceSearcherStrategy bottomSequenceSearcher = SequenceSearcherStrategyFactory.getSequenceSearcherStrategyForType(eSequenceSearcherType.Bottom);
        return getSequenceFinalSet(sequenceStartingCell, topSequenceSearcher, bottomSequenceSearcher, playingPlayer);
    }

    // Check diagonal sequences:
    //  \      OR       /
    //   \             /
    //    \           /
    private Collection<Cell> getMaxDiagonalSequence(Cell sequenceStartingCell, Player playingPlayer) {
        // Get bottom left to top right diagonal cell set.
        ISequenceSearcherStrategy topRightSequenceSearcher = SequenceSearcherStrategyFactory.getSequenceSearcherStrategyForType(eSequenceSearcherType.TopRight);
        ISequenceSearcherStrategy bottomLeftSequenceSearcher = SequenceSearcherStrategyFactory.getSequenceSearcherStrategyForType(eSequenceSearcherType.BottomLeft);
        Set<Cell> finalBotLeftToTopRightDiagonalSet = getSequenceFinalSet(sequenceStartingCell, topRightSequenceSearcher, bottomLeftSequenceSearcher, playingPlayer);

        // Get top left to bottom right diagonal cell set.
        ISequenceSearcherStrategy topLeftSequenceSearcher = SequenceSearcherStrategyFactory.getSequenceSearcherStrategyForType(eSequenceSearcherType.TopLeft);
        ISequenceSearcherStrategy bottomRightSequenceSearcher = SequenceSearcherStrategyFactory.getSequenceSearcherStrategyForType(eSequenceSearcherType.BottomRight);
        Set<Cell> finalTopLeftToBotRightDiagonalSet = getSequenceFinalSet(sequenceStartingCell, bottomRightSequenceSearcher, topLeftSequenceSearcher, playingPlayer);

        // Return max sequence
        return finalBotLeftToTopRightDiagonalSet.size() > finalTopLeftToBotRightDiagonalSet.size() ?
                finalBotLeftToTopRightDiagonalSet : finalTopLeftToBotRightDiagonalSet;
    }
}
