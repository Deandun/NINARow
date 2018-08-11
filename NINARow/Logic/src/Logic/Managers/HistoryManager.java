package Logic.Managers;

import Logic.Models.PlayerTurn;

import java.util.ArrayList;
import java.util.List;

public class HistoryManager {
    // hold a collection of PlayerTurns
    private List<PlayerTurn> mPlayerTurns = new ArrayList<>();

    public List<PlayerTurn> GetGameHistory(){
        return mPlayerTurns;
    }

    public void SetCurrentTurn(PlayerTurn playerTurn) {
        mPlayerTurns.add(playerTurn); //Add new turn to history collection
    }

    public void Clear() {
        mPlayerTurns.clear();
    }


}
