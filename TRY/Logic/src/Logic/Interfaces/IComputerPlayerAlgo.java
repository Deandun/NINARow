package Logic.Interfaces;

import Logic.Models.PlayTurnParameters;
import Logic.Models.Board;
import Logic.Models.Player;

public interface IComputerPlayerAlgo {
    PlayTurnParameters getNextPlay(Player playingPlayer);

    void Init(Board board);
}
