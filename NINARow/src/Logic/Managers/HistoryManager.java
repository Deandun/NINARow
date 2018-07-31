package Logic.Managers;

import Logic.Enums.eGameState;
import Logic.Models.Cell;
import Logic.Models.Player;
import Logic.Models.PlayerTurn;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class HistoryManager {
    // hold a collection of PlayerTurns
    private List<PlayerTurn> mPlayerTurns = new ArrayList<>();

    public Collection<PlayerTurn> GetGameHistory(){
        return mPlayerTurns;
    }

    public void SaveTurn(PlayerTurn playerTurn) {
        mPlayerTurns.add(playerTurn); //Add new turn to history collection
    }

    public void Clear() {
        mPlayerTurns.clear();
    }



}
