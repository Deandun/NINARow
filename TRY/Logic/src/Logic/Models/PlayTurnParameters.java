package Logic.Models;

import Logic.Enums.eTurnType;

public class PlayTurnParameters {
    private int mSelectedColumn;
    private eTurnType mTurnType;
    private Player mPlayer;

    public PlayTurnParameters(int selectedColumn, eTurnType turnType) {
        this.mSelectedColumn = selectedColumn;
        this.mTurnType = turnType;
    }

    public int getmSelectedColumn() {
        return mSelectedColumn;
    }

    public eTurnType getmTurnType() {
        return mTurnType;
    }

    public Player getPlayer() {
        return mPlayer;
    }

    public void setPlayer(Player player) {
        this.mPlayer = player;
    }
}
