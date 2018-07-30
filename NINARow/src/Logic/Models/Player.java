package Logic.Models;

import Logic.Enums.ePlayerType;

public class Player {

    private int mID;
    private String mName;
    private ePlayerType mType;

    public void init(int ID, String name, ePlayerType type) {
        this.mID = ID;
        this.mName = name;
        this.mType = type;
    }

    @Override
    public String toString(){
        return "ID: " + Integer.toString(mID) +
                " Name: " + mName +
                " Type: " + mType.name();
    }
    public int getID() {
        return mID;
    }

    public String getName() {
        return mName;
    }

    public ePlayerType getType() {
        return mType;
    }
}
