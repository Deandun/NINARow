package Tasks;

import Logic.Models.PlayedTurnData;
import javafx.concurrent.Task;
import Logic.Logic;
import java.util.function.Consumer;

public class PlayComputerPlayerTask extends Task<Void> {

    private Consumer<PlayedTurnData> mOnComputerPlayerFinishedPlayingTurn;
    private Logic mLogic;

    @Override
    protected Void call() throws Exception {

        this.mLogic.playComputerAlgoGamesIfNeededAndGetData(mOnComputerPlayerFinishedPlayingTurn);

        return null;
    }
}
