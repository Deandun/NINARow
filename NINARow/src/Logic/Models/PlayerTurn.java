package Logic.Models;


import Logic.Enums.eGameState;

public class PlayerTurn {
    private Cell mUpdatedCell;
    private Player mPlayerTurn;
    private eGameState mGameState;


    public PlayerTurn(Cell cell, Player player, eGameState gameState) {
        this.mUpdatedCell = cell;
        this.mPlayerTurn = player;
        this.mGameState = gameState;
    }

    public Cell getUpdatedCell() {
        return mUpdatedCell;
    }

    public Player getPlayer() {
        return mPlayerTurn;
    }

    public eGameState getGameState() {
        return mGameState;
    }

    void setGameState(eGameState gameState) {
        this.mGameState = gameState;
    }
}
