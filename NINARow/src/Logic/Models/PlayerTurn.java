package Logic.Models;

import Logic.Enums.eGameState;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class PlayerTurn {
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

    @XmlElement
    public void setGameState(eGameState gameState) {
        this.mGameState = gameState;
    }

    public String toString(){
        return  "Game state = " + mGameState.name() +
                ", Play turn = " + mUpdatedCell.getPlayer().toString() +
                ", Chosen column = " + mUpdatedCell; //mUpdatedCell.getColumnIndex();
    }

    @XmlElement
    public void setUpdatedCell(Cell updatedCell) {
        this.mUpdatedCell = updatedCell;
    }


    @XmlElement
    public void setPlayerTurn(Player player) {
        this.mPlayer = player;
    }

    public Player getPlayerTurn(){ return this.mPlayer;}

}
