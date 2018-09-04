package Logic.Interfaces;

import Logic.Models.PlayedTurnData;

public interface ILogicDelegate {

    void onTurnPlayedSuccess(PlayedTurnData playedTurnData);

    void discAddedToFullColumn(int column);

    void currentPlayerCannotPopoutAtColumn(int column);

    void turnInProgress();

    void noTurnInProgress();
}
