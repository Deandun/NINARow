package Logic.Models;

import Logic.Enums.eGameState;
import Logic.Enums.eTurnType;

import java.io.Serializable;
import java.util.Collection;

public class PlayedTurnData implements Serializable {
    private Collection<Cell> mUpdatedCellsCollection;
    private Player mPlayer;
    private eGameState mGameStateAfterTurn;
    private eTurnType mTurnType;

    public eTurnType getTurnType() {
        return mTurnType;
    }

    public void setTurnType(eTurnType turnType) {
        this.mTurnType = turnType;
    }

    public Collection<Cell> getUpdatedCellsCollection() {
        return mUpdatedCellsCollection;
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

    public void setUpdatedCellsCollection(Collection<Cell> updatedCell) {
        this.mUpdatedCellsCollection = updatedCell;
    }

    public void setPlayerTurn(Player player) {
        this.mPlayer = player;
    }

    public Player getPlayerTurn(){ return this.mPlayer;}

}
