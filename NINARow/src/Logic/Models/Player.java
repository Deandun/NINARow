package Logic.Models;

import Logic.Enums.ePlayerType;

public class Player {

    private String mID;
    private int mTurnCounter;
    private String mName;
    private ePlayerType mType;


    public void init(String ID, String name, ePlayerType type) {
        this.mID = ID;
        this.mTurnCounter = 0;
        this.mName = name;
        this.mType = type;
    }

    @Override
    public String toString(){
        return "ID: " + mID +
                " Name: " + mName +
                " Type: " + mType.name();
    }

    @Override
    public boolean equals(Object otherPlayer) {
        // Check if other player is not null and IDs match.
        return otherPlayer != null && mID.contentEquals(((Player)otherPlayer).mID);
    }

    public String getID() {
        return mID;
    }

    public void FinishedTurn() {
        mTurnCounter++;
    }

    public String getName() {
        return mName;
    }

    public ePlayerType getType() {
        return mType;
    }
}
