package Logic.Models;

public class Board {
    // TODO: get board size from GameSettings singleton (get game size from file).
    private Cell[][] mBoard = new Cell[5][5];

    public Cell[][] getBoard() {
        return mBoard;
    }

    // TODO: Throw exception if column is full
    public void UpdateBoard(int column, eCellStatus cellStatus) {
        // Insert to first available cell in column (max available row) if there is one.
    }

    public void Clear() {

    }
}
