package Logic.Managers;

import Logic.Models.Cell;
import Logic.Models.Player;
import Logic.Models.PlayerTurn;

import java.util.ArrayList;
import java.util.List;

public class HistoryManager {
    // hold a collection of PlayerTurns
    private List<PlayerTurn> mPlayerTurns = new ArrayList<>();

    public List<PlayerTurn> GetGameHistory(){
        return mPlayerTurns;
    }

    public void SaveTurn(Cell cell, Player player) {
        mPlayerTurns.add(new PlayerTurn(cell, player)); //Add new turn to history collection
    }

    public void Clear() {
        mPlayerTurns.clear();
    }



}
