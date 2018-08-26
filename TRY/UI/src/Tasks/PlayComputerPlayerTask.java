package Tasks;

import Logic.Models.PlayedTurnData;
import javafx.concurrent.Task;
import Logic.Logic;
import java.util.function.Consumer;

public class PlayComputerPlayerTask extends Task<Void> {

    private Logic mLogic;
    private Runnable mOnFinishedPlayingAllComputerPlayers;
    private Consumer<PlayedTurnData> mOnComputerPlayerFinishedPlayingTurn;

    public PlayComputerPlayerTask(Logic logic, Runnable onFinishedPlayingAllComputerPlayers, Consumer<PlayedTurnData> onComputerPlayerFinishedPlayingTurn) {
        this.mLogic = logic;
        this.mOnFinishedPlayingAllComputerPlayers = onFinishedPlayingAllComputerPlayers;
        this.mOnComputerPlayerFinishedPlayingTurn = onComputerPlayerFinishedPlayingTurn;
    }

    @Override
    protected Void call() throws Exception {

        this.mLogic.playComputerAlgoGamesIfNeededAndGetData(mOnComputerPlayerFinishedPlayingTurn); // Play all of the computer players turns.
        this.mOnFinishedPlayingAllComputerPlayers.run();

        return null;
    }
}
