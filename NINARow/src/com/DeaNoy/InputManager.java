package com.DeaNoy;

import Logic.Models.Board;
import Logic.Models.Player;

// Receive input from human or "computer" user.
public class InputManager {

    public eGameOptions GetCommandFromPlayer(Player player) {
        // if computer's turn, always return "play turn" option.
        return eGameOptions.Exit;
    }

    public int GetCellIndexFromPlayer() {
        // if computer's turn, use algorithm to determine the input.
        return 0;
    }
}
