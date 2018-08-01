package Logic.SequenceSearchers;

import Logic.Interfaces.ISequenceSearcher;

public class TopLeftSequenceSearcher implements ISequenceSearcher {
    @Override
    public int GetNextRow(int currentRow) {
        return currentRow - 1;
    }

    @Override
    public int GetNextColumn(int currentColumn) {
        return currentColumn - 1;
    }

    @Override
    public boolean shouldStopLooking(int row, int column) {
        return row <= 0 || column <= 0;
    }
}
