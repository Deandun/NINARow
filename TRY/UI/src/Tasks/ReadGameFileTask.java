package Tasks;

import javafx.concurrent.Task;
import Logic.Logic;

import java.util.function.Consumer;


// DEAN change
public class ReadGameFileTask extends Task<Void> {

    private String mAbsoluteGameFilePath;
    private Runnable mOnFinish;
    private Logic mLogic;

    public ReadGameFileTask(String absoluteGameFilePath, Logic logic, Runnable onFinish) {
        this.mAbsoluteGameFilePath = absoluteGameFilePath;
        this.mLogic = logic;
        this.mOnFinish = onFinish;
    }

    @Override
    protected Void call() throws Exception {
        // Open file.
        this.updateMessage("Preparing to load file...");
        this.updateProgress(0, 100);
        Thread.sleep(300);

        // Set callbacks
        Runnable onLoadFileFinish = () -> {
            // Check if file is valid.
            this.updateProgress(33, 100);
            this.updateMessage("Checking file content validation...");
        };

        Runnable onFinishedCheckingFileValidity = () -> {
            // Apply file content to game settings.
            this.updateMessage("Reading from file...");
            this.updateProgress(66, 100);
        };

        this.mLogic.ReadGameFile(mAbsoluteGameFilePath, onLoadFileFinish, onFinishedCheckingFileValidity);

        this.updateMessage("Finished reading from file :)");
        this.updateProgress(100, 100);
        Thread.sleep(300);

        this.mOnFinish.run();

        return null;
    }
}


