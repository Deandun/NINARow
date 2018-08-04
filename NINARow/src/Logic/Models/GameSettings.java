package Logic.Models;

import Logic.Enums.ePlayerType;
import Logic.Enums.eVariant;
import org.w3c.dom.Document;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

// This class will contain all of the game related details that were read from the XML file.
public class GameSettings {

    private static GameSettings ourInstance = new GameSettings();

    public static final int MAX_BOARD_ROWS = 50;
    public static final int MIN_BOARD_ROWS = 5;
    public static final int MAX_BOARD_COLS = 60;
    public static final int MIN_BOARD_COLS = 6;

    private List<Player> mPlayers = new ArrayList<>();
    //TODO: Change dummy
    private int mTarget = 4;
    private int mRows = 6;
    private int mColumns = 17;
    private eVariant mVariant;

    private GameSettings() {
    }

    public static GameSettings getInstance() {
        return ourInstance;
    }

    public void DummyInit() {
        //TODO: dummy impl! get players from input or file
        Player player1 = new Player();
        player1.init("1", "Aviad", ePlayerType.Human);
        Player player2 = new Player();
        player2.init("2", "Guy", ePlayerType.Human);

        mPlayers.add(player1);
        mPlayers.add(player2);
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

    public eVariant getVariant() {
        return mVariant;
    }

    public List<Player> getPlayers() {
        return mPlayers;
    }

    public void setTarget(int target) {
        this.mTarget = target;
    }

    public void setRows(int rows) {
        this.mRows = rows;
    }

    public void setColumns(int columns) {
        this.mColumns = columns;
    }

    public void setVariant(eVariant variant) {
        this.mVariant = variant;
    }
}
