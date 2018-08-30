package UI.UIMisc;

import Logic.Models.PlayedTurnData;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;

import java.util.List;
import java.util.ListIterator;

public class ReplayManager {
    private BooleanProperty mIsReplayInProgressProperty;
    private IntegerProperty mCurrentTurnNumberInReplay;
    private ListIterator<PlayedTurnData> mPlayedTurnsDataListIterator;

    public ReplayManager() {
        this.mIsReplayInProgressProperty = new SimpleBooleanProperty();
        this.mCurrentTurnNumberInReplay = new SimpleIntegerProperty();
    }

    public void start(List<PlayedTurnData> playedTurnData) {
        // TODO: check if this gives us the actual last turn iterator
        this.mCurrentTurnNumberInReplay.setValue(playedTurnData.size());
        this.mPlayedTurnsDataListIterator = playedTurnData.listIterator(playedTurnData.size()); // Set to last element.
        this.mIsReplayInProgressProperty.setValue(true);
    }

    public void stop() {
        this.mIsReplayInProgressProperty.setValue(false);
    }

    public PlayedTurnData getNextTurnData() {
        this.mCurrentTurnNumberInReplay.setValue(this.mCurrentTurnNumberInReplay.getValue() + 1);
        return this.mPlayedTurnsDataListIterator.next();
    }

    public PlayedTurnData getPreviousTurnData() {
        this.mCurrentTurnNumberInReplay.setValue(this.mCurrentTurnNumberInReplay.getValue() - 1);
        return this.mPlayedTurnsDataListIterator.previous();
    }

    public boolean hasNext() {
        return this.mPlayedTurnsDataListIterator.hasNext();
    }

    public boolean hasPrevious() {
        return this.mPlayedTurnsDataListIterator.hasPrevious();
    }

    public boolean IsReplayInProgressProperty() {
        return this.mIsReplayInProgressProperty.get();
    }

    public BooleanProperty getIsReplayInProgressProperty() {
        return this.mIsReplayInProgressProperty;
    }

    public IntegerProperty getCurrentTurnNumberInReplayProperty() {
        return this.mCurrentTurnNumberInReplay;
    }
}
