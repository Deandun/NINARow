package Logic.Models;

import Logic.Exceptions.InvalidUserInputException;

public class Cell {
    private Player mPlayer;

    public boolean isEmpty() {
        return mPlayer == null;
    }

    public void setPlayer(Player player) throws Exception {
        if (this.isEmpty()){
            this.mPlayer = player;
        }
        else{
            throw new InvalidUserInputException("Cell is already taken!");
        }
    }

    public void Clear(){
        mPlayer = null;
    }
}
