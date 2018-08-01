package Logic.SequenceSearchers;

import Logic.Interfaces.ISequenceSearcher;
import Logic.Models.GameSettings;

public class RightSequenceSearcher implements ISequenceSearcher {
    @Override
    public int GetNextRow(int currentRow) {
        return currentRow;
    }

    @Override
    public int GetNextColumn(int currentColumn) {
        return currentColumn + 1;
    }

    @Override
    public boolean shouldStopLooking(int row, int column) {
        return column >= GameSettings.getInstance().getColumns();
    }
}
