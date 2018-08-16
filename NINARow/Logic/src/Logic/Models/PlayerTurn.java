package Logic.Models;

import Logic.Enums.eGameState;
import Logic.Enums.eTurnType;

import java.io.Serializable;

public class PlayerTurn implements Serializable {
    private Cell mUpdatedCell;
    private Player mPlayer;
    private eGameState mGameStateAfterTurn;
    private eTurnType mTurnType;

    public eTurnType getTurnType() {
        return mTurnType;
    }

    public void setTurnType(eTurnType turnType) {
        this.mTurnType = turnType;
    }

    public Cell getUpdatedCell() {
        return mUpdatedCell;
    }

    public Player getPlayer() {
        return mPlayer;
    }

    public eGameState getGameState() {
        return mGameStateAfterTurn;
    }

    public void setGameState(eGameState gameState) {
        this.mGameStateAfterTurn = gameState;
    }

    public String toString(){
        return  "Game state = " + mGameStateAfterTurn.name() +
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
