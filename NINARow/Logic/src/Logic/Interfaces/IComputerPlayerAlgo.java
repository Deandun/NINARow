package Logic.Interfaces;

import Logic.ComputerPlayer.ComputerPlayerTurnData;
import Logic.Models.Board;
import Logic.Models.Player;

public interface IComputerPlayerAlgo {
    ComputerPlayerTurnData getNextPlay(Player playingPlayer) throws Exception;

    void Init(Board board);
}
