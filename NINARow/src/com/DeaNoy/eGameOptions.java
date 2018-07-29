package com.DeaNoy;

// An enum representing the game's options.
public enum eGameOptions {
    ReadGameFile(1, "Load file containing game details."),
    StartGame(2, "Start game."),
    ShowGameStatus(3, "Show current game status."),
    PlayTurn(4, "Play turn."),
    ShowTurnHistory(5, "Show turn history."),
    Exit(6, "Exit.");

    private final int fIndex;
    private final String fDescription;

    private eGameOptions(int index, String description) {
        this.fIndex = index;
        this.fDescription = description;
    }

    public int getIndex() {
        return this.fIndex;
    }

    public String getDescription() {
        return this.fDescription;
    }
}
