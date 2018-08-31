package UI.Replay;

import Logic.Models.PlayedTurnData;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

public class ReplayManager {
    private IntegerProperty mCurrentTurnNumberInReplay;
    private ListIterator<PlayedTurnData> mPlayedTurnsDataListIterator;

    public ReplayManager() {
        this.mCurrentTurnNumberInReplay = new SimpleIntegerProperty();
    }

    public void start(List<PlayedTurnData> playedTurnData) {
        this.mCurrentTurnNumberInReplay.setValue(playedTurnData.size());
        this.mPlayedTurnsDataListIterator = playedTurnData.listIterator(playedTurnData.size()); // Set to last element.
    }

    public PlayedTurnData getNextTurnData() {
        this.mCurrentTurnNumberInReplay.setValue(this.mPlayedTurnsDataListIterator.nextIndex());
        return this.mPlayedTurnsDataListIterator.next();
    }

    public PlayedTurnData getPreviousTurnData() {
        this.mCurrentTurnNumberInReplay.setValue(this.mPlayedTurnsDataListIterator.previousIndex());
        return this.mPlayedTurnsDataListIterator.previous();
    }

    public boolean hasNext() {
        return this.mPlayedTurnsDataListIterator.hasNext();
    }

    public boolean hasPrevious() {
        return this.mPlayedTurnsDataListIterator.hasPrevious();
    }

    //TODO: bind this property with a label that holds a text like "(currentTurn) / (total num of turns)"
    public IntegerProperty getCurrentTurnNumberInReplayProperty() {
        return this.mCurrentTurnNumberInReplay;
    }

    public List<PlayedTurnData> getAllNextTurnsCollection() {
        List<PlayedTurnData> remainingTurnDataList = new ArrayList<>();

        this.mPlayedTurnsDataListIterator.forEachRemaining(remainingTurnDataList::add);

        return remainingTurnDataList;
    }
}
