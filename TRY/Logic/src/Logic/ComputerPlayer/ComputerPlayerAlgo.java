package Logic.ComputerPlayer;

import Logic.Enums.eTurnType;
import Logic.Enums.eVariant;
import Logic.Interfaces.IComputerPlayerAlgo;
import Logic.Models.Board;
import Logic.Models.GameSettings;
import Logic.Models.Player;

public class ComputerPlayerAlgo implements IComputerPlayerAlgo {
    private Board mBoard; // The game board.

    public void Init(Board board) {
        this.mBoard = board;
    }

    // TODO: exception is thrown when the board is full and the player can't even use popout. handle it in UI (game draw)
    @Override
    public ComputerPlayerTurnData getNextPlay(Player playingPlayer) throws Exception {
        int selectedColumn;
        eTurnType turnType;

        if(shouldPopout(playingPlayer)) {
            selectedColumn = getPopoutColumn(playingPlayer);
            turnType = eTurnType.Popout;
        } else {
            selectedColumn = getColumnToAddDiscTo();
            turnType = eTurnType.AddDisc;
        }

        return new ComputerPlayerTurnData(selectedColumn, turnType);
    }

    private int getPopoutColumn(Player playingPlayer) {
        int selectedColumn = 0;

        for(int i = 0; i < GameSettings.getInstance().getColumns(); i++) {
            if(mBoard.CanPlayerPerformPopoutForColumn(playingPlayer, i)) {
                selectedColumn = i;
                break;
            }
        }

        return selectedColumn;
    }

    private boolean shouldPopout(Player playingPlayer) throws Exception {
        boolean isBoardFull = mBoard.IsBoardFull();
        boolean canPopout = GameSettings.getInstance().getVariant().equals(eVariant.Popout) && mBoard.CanPlayerPerformPopout(playingPlayer);
        boolean shouldPopout = false;

        if(isBoardFull && canPopout) {
            shouldPopout = true;
        } else if (isBoardFull) {
            //TODO: create a new exception with a fitting name.
            throw new Exception("Board is full, ComputerPlayerAlgo cannot select an available column." +
                    " Game should have already ended in a draw");
        }

        return shouldPopout;
    }

    private int getColumnToAddDiscTo() {
        int selectedColumn = 0;

        for(int i = 0; i < GameSettings.getInstance().getColumns(); i++) {
            if(!mBoard.IsColumnFull(i)) {
                selectedColumn = i;
                break;
            }
        }

        return selectedColumn;
    }
}
