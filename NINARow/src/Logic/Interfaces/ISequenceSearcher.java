package Logic.Interfaces;

public interface ISequenceSearcher {
    int GetNextRow(int currentRow);

    int GetNextColumn(int currentColumn);

    boolean shouldStopLooking(int row, int column);
}
