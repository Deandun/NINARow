package Logic.Models;

import Logic.Enums.ePlayerType;
import Logic.Enums.eVariant;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

// This class will contain all of the game related details that were read from the XML file.

public class GameSettings implements Serializable {

    private static GameSettings ourInstance = new GameSettings();

    public static final int MAX_BOARD_ROWS = 50;
    public static final int MIN_BOARD_ROWS = 5;
    public static final int MAX_BOARD_COLS = 60;
    public static final int MIN_BOARD_COLS = 6;
    public static final int Min_TARGET = 2;
    public static final int MIN_NUM_OF_PLAYERS = 2;
    public static final int MAX_NUM_OF_PLAYERS = 5;
    private static final String SAVED_GAME_FILE_NAME = "DeaNoy_Game.txt";

    private List<Player> mPlayers = new ArrayList<>();
    private int mTarget;
    private int mRows;
    private int mColumns;
    private eVariant mVariant;

    private GameSettings() {
    }

    public static GameSettings getInstance() {
        return ourInstance;
    }

    public static String getSavedGameFileName() {
        return SAVED_GAME_FILE_NAME;
    }

    public static void LoadNewInstance(GameSettings gameSettings) {
        ourInstance = gameSettings;
    }

    public void InitPlayers(String player1Type, String player2Type) {
        Player player1 = new Player();
        player1.init("1", "Aviad", ePlayerType.ConvertStringToePlayerType(player1Type));
        Player player2 = new Player();
        player2.init("2", "Guy", ePlayerType.ConvertStringToePlayerType(player2Type));

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

    public void clear(){
        mPlayers.clear();
    }
}
