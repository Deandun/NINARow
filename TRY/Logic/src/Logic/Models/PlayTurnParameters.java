package Logic.Models;

import Logic.Enums.eTurnType;

public class PlayTurnParameters {

    private int mSelectedColumn;
    private eTurnType mTurnType;

    public PlayTurnParameters(eTurnType turnType) {
        this.mTurnType = turnType;
    }

    public PlayTurnParameters(int selectedColumn, eTurnType turnType) {
        this.mSelectedColumn = selectedColumn;
        this.mTurnType = turnType;
    }

    public int getmSelectedColumn() {
        return mSelectedColumn;
    }

    public eTurnType getmTurnType() {
        return this.mTurnType;
    }
}
