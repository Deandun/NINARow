package UI.Replay;

import Logic.Models.Cell;
import Logic.Models.PlayedTurnData;
import javafx.beans.property.IntegerProperty;

import java.util.*;

// An adapter class for the replay manager, manipulate the return data for "getPrevious" so that the UI could handle it better.
public class ReplayTurnDataAdapter {
    private ReplayManager mReplayManager;

    public void start(List<PlayedTurnData> playedTurnData) {
        this.mReplayManager.start(playedTurnData);
    }

    public PlayedTurnData getNextTurnData() {
        // Return the original next turn data
        return this.mReplayManager.getNextTurnData();
    }

    public PlayedTurnData getPreviousTurnData() {
        PlayedTurnData previousTurnData = this.mReplayManager.getPreviousTurnData();
        PlayedTurnData manipulatedTurnData = null;

        switch(previousTurnData.getTurnType()) {
            case AddDisc:
                manipulatedTurnData = this.manipulatePreviousAddDiscTurn(previousTurnData);
                break;
            case Popout:
                manipulatedTurnData = this.manipulatePreviousPopoutTurn(previousTurnData);
                break;
            case PlayerQuit:
                manipulatedTurnData = this.manipulatePreviousPlayerQuitTurn(previousTurnData);
                break;
        }

        manipulatedTurnData.setTurnType(previousTurnData.getTurnType());
        manipulatedTurnData.setPlayerTurn(previousTurnData.getPlayerTurn());
        manipulatedTurnData.setGameState(previousTurnData.getGameState());

        return manipulatedTurnData;
    }

    private PlayedTurnData manipulatePreviousAddDiscTurn(PlayedTurnData previousTurnData) {
        PlayedTurnData manipulatedTurnData = new PlayedTurnData();
        Collection<Cell> updatedCellCollection = new ArrayList<>();

        // In add disc there's only 1 updated cell. Make the player that occupies that cell null.
        Cell updatedCell = ((List<Cell>)previousTurnData.getUpdatedCellsCollection()).get(0).getShallowCopy();
        updatedCell.setPlayer(null);
        updatedCellCollection.add(updatedCell);

        manipulatedTurnData.setUpdatedCellsCollection(updatedCellCollection);

        return manipulatedTurnData;
    }

    private PlayedTurnData manipulatePreviousPopoutTurn(PlayedTurnData previousTurnData) {
        PlayedTurnData manipulatedTurnData = new PlayedTurnData();
        Cell updatedCellCollection[] = new Cell[previousTurnData.getUpdatedCellsCollection().size()];

        Iterator<Cell> cellIterator = previousTurnData.getUpdatedCellsCollection().iterator();
        int index = 0;

        while (cellIterator.hasNext()) {
            // Shallow copy cell so that manipulating it won't affect the original cell.
            updatedCellCollection[index++] = cellIterator.next().getShallowCopy();
        }

        // Sort by rows in an descending order
        updatedCellCollection =
                (Cell[]) Arrays.stream(updatedCellCollection)
                .sorted(
                    (cell1, cell2) -> cell2.getRowIndex() - cell1.getRowIndex()
                ).toArray();

        //TODO: go over the sorted cells collection. Set each cell's player with the previous cell's player (starting with the second cell).
        //TODO: set the first cell's player as null.

        //TODO: turn to arraylist
        //manipulatedTurnData.setUpdatedCellsCollection(updatedCellCollection);

        return manipulatedTurnData;
    }

    private PlayedTurnData manipulatePreviousPlayerQuitTurn(PlayedTurnData previousTurnData) {
        return null;
    }

    public boolean hasNext() {
        return this.mReplayManager.hasNext();
    }

    public boolean hasPrevious() {
        return this.mReplayManager.hasPrevious();
    }


    public IntegerProperty getCurrentTurnNumberInReplayProperty() {
        return this.mReplayManager.getCurrentTurnNumberInReplayProperty();
    }
}
