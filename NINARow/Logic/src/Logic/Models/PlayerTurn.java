package Logic.Models;

import Logic.Enums.eGameState;

import java.io.Serializable;

public class PlayerTurn  implements Serializable {
    private Cell mUpdatedCell;
    private Player mPlayer;
    private eGameState mGameState;

    public Cell getUpdatedCell() {
        return mUpdatedCell;
    }

    public Player getPlayer() {
        return mPlayer;
    }

    public eGameState getGameState() {
        return mGameState;
    }

    public void setGameState(eGameState gameState) {
        this.mGameState = gameState;
    }

    public String toString(){
        return  "Game state = " + mGameState.name() +
                ", Play turn = " + mUpdatedCell.getPlayer().toString() +
                ", Chosen column = " + mUpdatedCell; //mUpdatedCell.getColumnIndex();
    }

    public void setUpdatedCell(Cell updatedCell) {
        this.mUpdatedCell = updatedCell;
    }

    public void setPlayerTurn(Player player) {
        this.mPlayer = player;
    }

    public Player getPlayerTurn(){ return this.mPlayer;}

}
