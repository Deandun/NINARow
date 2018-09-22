package NinaRowHTTPClient.Game;

import Logic.Enums.eGameState;
import Logic.Enums.eVariant;
import Logic.Models.Cell;
import Logic.Models.PlayTurnParameters;
import Logic.Models.PlayedTurnData;
import Logic.Models.Player;
import UI.UIMisc.GameDescriptionData;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

// The main class used to communicate with the server. Acts as an http client.
public class GameClientLogic {
    // Game state
    private eGameState mGameState;
    private GameDescriptionData mGameData;
    private List<PlayedTurnData> mPlayedTurnDataList;


    //Players
    private String mLoggedInPlayerName;
    private String mCurrentPlayerName;
    private List<Player> mCachedPlayersList;

    private Gson mGson;

    // Delegate
    private IGameClientLogicDelegate mDelegate;

    // TODO: create a communication class, a static path class (to determine where the requests should go) and a parser. Implement all of the communication using those classes.

    public GameClientLogic(GameDescriptionData mGameData, String loggedInPlayerName, IGameClientLogicDelegate delegate) {
        this.mLoggedInPlayerName = loggedInPlayerName;
        this.mGameState = eGameState.Inactive;
        this.mGameData = mGameData;
        this.mDelegate = delegate;
        this.mCachedPlayersList = new ArrayList<>();
        this.mPlayedTurnDataList = new ArrayList<>();
        this.mGson = new GsonBuilder().create();
    }

    public void exitGame() {
        // Send request for player quit (no need for response)
    }

    public GameDescriptionData getmGameData() {
        return this.mGameData;
    }

    public List<Player> getPlayers() {
        return this.mCachedPlayersList;
    }

    public List<PlayedTurnData> GetTurnHistory() {
        return this.mPlayedTurnDataList;
    }

    public boolean isGameActive() {
        return this.mGameState.equals(eGameState.Active);
    }

    public boolean isMyTurn() {
        return this.mLoggedInPlayerName.equals(this.mCurrentPlayerName);
    }

    public void playTurnAsync(PlayTurnParameters playTurnParameters) {
        // Send request to play turn. If returns error, notify delegate by using the following methods:
        //this.mDelegate.currentPlayerCannotPopoutAtColumn(playTurnParameters.getmSelectedColumn());
        //this.mDelegate.discAddedToFullColumn(playTurnParameters.getmSelectedColumn());
    }

    public void fetchPlayerToWinningSequencesMap(Consumer<Map<Player, Collection<Cell>>> finishedFetchingWinningSequencesMap) {
        // Send request to server, return the following on success
        Map<Player, Collection<Cell>> playerToWinningSequencesMap = null;
        finishedFetchingWinningSequencesMap.accept(playerToWinningSequencesMap);
    }

    public void fetchAvailablePopoutColumnsForCurrentPlayer(Consumer<List<Integer>> finishedFetchingAvailableColumnsForPopout) {
        // Send request to server
        List<Integer> availablePopoutColumnsForCurrentUser = null;
        finishedFetchingAvailableColumnsForPopout.accept(availablePopoutColumnsForCurrentUser);
    }

    public boolean isPopoutAllowed() {
        return this.mGameData.getmVariant().equals(eVariant.Popout); // check if popout mode
    }
}
