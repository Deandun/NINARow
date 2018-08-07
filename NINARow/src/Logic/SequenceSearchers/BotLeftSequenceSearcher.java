package Logic.SequenceSearchers;

import Logic.Interfaces.ISequenceSearcher;
import Logic.Models.GameSettings;

public class BotLeftSequenceSearcher implements ISequenceSearcher {
    @Override
    public int GetNextRow(int currentRow) {
        return currentRow + 1;
    }

    @Override
    public int GetNextColumn(int currentColumn) {
        return currentColumn - 1;
    }

    @Override
    public boolean shouldStopLooking(int row, int column) {
        return row >= GameSettings.getInstance().getRows() || column < 0;
    }
}
