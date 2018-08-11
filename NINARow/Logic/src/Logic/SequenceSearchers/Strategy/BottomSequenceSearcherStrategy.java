package Logic.SequenceSearchers.Strategy;

import Logic.Interfaces.ISequenceSearcherStrategy;
import Logic.Models.GameSettings;

public class BottomSequenceSearcherStrategy implements ISequenceSearcherStrategy {
    @Override
    public int GetNextRow(int currentRow) {
        return currentRow + 1;
    }

    @Override
    public int GetNextColumn(int currentColumn) {
        return currentColumn;
    }

    @Override
    public boolean shouldStopLooking(int row, int column) {
        return row >= GameSettings.getInstance().getRows();
    }
}
