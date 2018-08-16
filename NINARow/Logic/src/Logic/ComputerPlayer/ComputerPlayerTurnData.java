package Logic.ComputerPlayer;

import Logic.Enums.eTurnType;

public class ComputerPlayerTurnData {
    private int mSelectedColumn;
    private eTurnType mTurnType;

    public ComputerPlayerTurnData(int selectedColumn, eTurnType turnType) {
        this.mSelectedColumn = selectedColumn;
        this.mTurnType = turnType;
    }

    public int getmSelectedColumn() {
        return mSelectedColumn;
    }

    public eTurnType getmTurnType() {
        return mTurnType;
    }
}
