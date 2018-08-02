package Logic.Interfaces;

import Logic.Models.Player;

import java.time.Duration;
import java.util.Collection;
import java.util.Timer;

public interface IGameStatus {
    String getNameOfPlayerCurrentlyPlaying();

    Duration getGameDuration();
}
