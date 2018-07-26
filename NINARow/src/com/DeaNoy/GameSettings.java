package com.DeaNoy;

public class GameSettings {
    private static GameSettings ourInstance = new GameSettings();

    public static GameSettings getInstance() {
        return ourInstance;
    }

    private String[] mPlayerNames = { "Aviad", "Guy" };

    private char[] mPlayerSigns = { 'J', 'C'};

    private GameSettings() {
    }
}
