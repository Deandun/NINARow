package Logic.Interfaces;

import Logic.Models.Board;

public interface IComputerPlayerAlgo {
    int getNextPlay();

    void Init(Board board);
}
