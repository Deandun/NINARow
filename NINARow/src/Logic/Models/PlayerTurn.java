package Logic.Models;


public class PlayerTurn{
    private Cell mUpdatedCell;
    private Player mPlayerTurn;

    public PlayerTurn(Cell cell, Player player) {
        this.mUpdatedCell = cell;
        this.mPlayerTurn = player;
    }

    public Cell getUpdatedCell() {
        return mUpdatedCell;
    }

    public Player getPlayerTurn() {
        return mPlayerTurn;
    }
}
