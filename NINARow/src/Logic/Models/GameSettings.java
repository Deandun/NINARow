package Logic.Models;

import org.w3c.dom.Document;

import java.util.ArrayList;
import java.util.List;

// This class will contain all of the game related details that were read from the XML file.
public class GameSettings {

    private static GameSettings ourInstance = new GameSettings();
    private List<Player> mPlayers = new ArrayList<>();
    //TODO: Change dummy
    private int mTarget = 4;
    private int mRows = 6;
    private int mColumns = 7;
    private String mVariant = "Regular";

    public GameSettings() {

    }

    public static GameSettings getInstance() {
        return ourInstance;
    }

    public void ParseDom(Document doc){

    }

    public int getTarget() {
        return mTarget;
    }

    public int getRows() {
        return mRows;
    }

    public int getColumns() {
        return mColumns;
    }

    public String getVariant() {
        return mVariant;
    }

    public List<Player> getPlayers() {
        return mPlayers;
    }

}
