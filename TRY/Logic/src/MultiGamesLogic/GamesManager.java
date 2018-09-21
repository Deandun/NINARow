package MultiGamesLogic;

import Logic.Game;
import Logic.Models.PlayTurnParameters;
import Logic.Models.PlayedTurnData;

import java.util.List;
import java.util.Map;

public class GamesManager {
    private static Map<String, Game> mGameNameToGame;

    // User actions.

    public void userJoinedGame(String gameName, String playerName) {
        // get relevant game from map.

        // create player with new name.

        // check if game should start - start game.
    }

    public void userLeftGame(String gameName, String playerName) {
        // get relevant game from map.

        // remove player from game.
    }

    public void playTurn(String gameName, PlayTurnParameters params) {
        // get relevant game from map.

        // play turn with params.
    }

    // Pull

    // TODO: return a collection of all of the games info (made out of relevant info from both GameSettings and GameStatus.
    public void getAllGamesInfo() {
        // get relevant game info for all games.
    }

    public List<PlayedTurnData> getTurnHistoryForGame(String gameName, int currentTurnForClient) {
        // get the delta of played turn data.
        return null;
    }
}
