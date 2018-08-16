package Logic.Models;

import Logic.Enums.ePlayerType;

import java.io.Serializable;

public class Player implements Serializable {

    private String mID;
    private int mTurnCounter;
    private String mName;
    private ePlayerType mType;
    private int mIndex;

    public void init(String ID, String name, ePlayerType type) {
        setID(ID);
        setTurnCounter(0);
        setName(name);
        setType(type);
    }

    @Override
    public String toString(){
        return "ID: " + mID +
                " Name: " + mName +
                " Index: " + mIndex +
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

    public int getTurnCounter(){return mTurnCounter;}

    public void setID(String ID) {
        this.mID = ID;
    }

    public void setTurnCounter(int turnCounter) {
        this.mTurnCounter = turnCounter;
    }

    public void setName(String name) {
        this.mName = name;
    }

    public void setType(ePlayerType mType) {
        this.mType = mType;
    }

    public int getIndex() {
        return mIndex;
    }

    public void setIndex(int index) {
        this.mIndex = index;
    }
}
