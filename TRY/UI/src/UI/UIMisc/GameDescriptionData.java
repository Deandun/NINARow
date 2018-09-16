package UI.UIMisc;

import Logic.Enums.eVariant;
import UI.Enums.eGameState;

public class GameDescriptionData {

    private eGameState mGameState;
    private eVariant mVariant; //TODO: change to eGameType enum!!
    private int mRows;
    private int mColumns;
    private int mMaxPlayers;
    private int mCurrentNumberOfPlayers;
    private int mTarget;
    private String mGameName;
    private String mUploaderName;

    public eGameState getmGameState() {
        return mGameState;
    }

    public void setmGameState(eGameState mGameState) {
        this.mGameState = mGameState;
    }

    public eVariant getmVariant() {
        return mVariant;
    }

    public void setmVariant(eVariant mVariant) {
        this.mVariant = mVariant;
    }

    public int getmRows() {
        return mRows;
    }

    public void setmRows(int mRows) {
        this.mRows = mRows;
    }

    public int getmColumns() {
        return mColumns;
    }

    public void setmColumns(int mColumns) {
        this.mColumns = mColumns;
    }

    public int getmMaxPlayers() {
        return mMaxPlayers;
    }

    public void setmMaxPlayers(int mMaxPlayers) {
        this.mMaxPlayers = mMaxPlayers;
    }

    public int getmNumberOfPlayers() {
        return mCurrentNumberOfPlayers;
    }

    public void setmCurrentNumberOfPlayers(int mCurrentNumberOfPlayers) {
        this.mCurrentNumberOfPlayers = mCurrentNumberOfPlayers;
    }

    public int getmTarget() {
        return mTarget;
    }

    public void setmTarget(int mTarget) {
        this.mTarget = mTarget;
    }

    public String getmGameName() {
        return mGameName;
    }

    public void setmGameName(String mGameName) {
        this.mGameName = mGameName;
    }

    public String getmUploaderName() {
        return mUploaderName;
    }

    public void setmUploaderName(String mUploaderName) {
        this.mUploaderName = mUploaderName;
    }
}
