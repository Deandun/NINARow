package Logic.Interfaces;

import Logic.Models.PlayTurnParameters;
import Logic.Models.Board;
import Logic.Models.Player;

public interface IComputerPlayerAlgo {
    PlayTurnParameters getNextPlay(Player playingPlayer) throws Exception;

    void Init(Board board);
}
