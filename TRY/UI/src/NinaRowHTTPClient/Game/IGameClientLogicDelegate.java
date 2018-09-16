package NinaRowHTTPClient.Game;

import Logic.Models.PlayedTurnData;
import Logic.Models.Player;

import java.util.List;

public interface IGameClientLogicDelegate {

    void myTurnStarted();

    void gameStarted();

    void updatePlayers(List<Player> playerList);

    void onTurnPlayedSuccess(PlayedTurnData playedTurnData);

    void discAddedToFullColumn(int column);

    void currentPlayerCannotPopoutAtColumn(int column);
}
