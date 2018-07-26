package com.DeaNoy;

// An enum representing the game's options.
public enum eGameOptions {
    ReadGameDetailsFile(1, "Load file containing game details."),
    StartGame(2, "Start game."),
    ShowGameStatus(3, "Show current game status."),
    PlayTurn(4, "Play turn."),
    ShowTurnHistory(5, "Show turn history."),
    Exit(6, "Exit.");

    private final int kIndex;
    private final String kDescription;

    private eGameOptions(int index, String description) {
        this.kIndex = index;
        this.kDescription = description;
    }

    public int getIndex() {
        return this.kIndex;
    }

    public String getDescription() {
        return this.kDescription;
    }
}
