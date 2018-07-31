package com.DeaNoy;

import Logic.Models.Player;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class ConsoleConfig {
    private static final char[] sfPlayerSigns = {'J', 'C', 'R', 'S', 'P'};
    private static Map<String, Character> sPlayerSignsMap = new HashMap(); // Maps between player ids and the player signs.

    public static void InitPlayerSigns(Collection<Player> players) {
        int index = 0;

        for(Player player: players) {
            sPlayerSignsMap.put(player.getID(), sfPlayerSigns[index++]);
            index = index % sfPlayerSigns.length;
        }
    }

    public static char GetSignForPlayerID(String playerID) {
        return sPlayerSignsMap.get(playerID);
    }
}
