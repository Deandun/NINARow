package Logic.Models;

public class Cell {
    private Player mPlayer;

    public boolean isEmpty() {
        return mPlayer == null;
    }

    public void setPlayer(Player player) {
        this.mPlayer = player;
    }
}
