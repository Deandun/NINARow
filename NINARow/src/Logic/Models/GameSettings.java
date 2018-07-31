package Logic.Models;

import org.w3c.dom.Document;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

// This class will contain all of the game related details that were read from the XML file.
public class GameSettings {

    private static GameSettings ourInstance = new GameSettings();
    private List<Player> mPlayers = new ArrayList<>();
    public static final int MAX_BOARD_ROWS = 50;

    public static final int MIN_BOARD_ROWS = 5;

    public static final int MAX_BOARD_COLS = 60;

    public static final int MIN_BOARD_COLS = 6;

    //TODO: Change dummy
    private int mTarget = 4;
    private int mRows = 6;
    private int mColumns = 17;
    private String mVariant = "Regular";

    public GameSettings() {

    }

    public static GameSettings getInstance() {
        return ourInstance;
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
