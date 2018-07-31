package Logic.Managers;

import Logic.Exceptions.InvalidFileInputException;
import Logic.Models.GameSettings;

public class FileManager {

    private boolean mIsFileLoaded = false;

    public boolean getIsFileLoaded() {
        return mIsFileLoaded;
    }

    // TODO: throw exception if needed
    public void LoadGameFile(String filePath) throws InvalidFileInputException {
        readDataFromFile(filePath);
        checkIfFileInputIsValid(); // Throws if input is invalid
    }

    private void readDataFromFile(String filePath) {
        // TODO: Don't forget to close file when done
    }


    //@Override
    private void checkIfFileInputIsValid() throws InvalidFileInputException {
        mIsFileLoaded = false; // Reset this boolean for the new file that is being loaded.
        GameSettings gameSettings = GameSettings.getInstance();

        //check validation of rows number
        if (gameSettings.getRows() < GameSettings.MIN_BOARD_ROWS || gameSettings.getRows() > GameSettings.MAX_BOARD_ROWS) {
            throw new InvalidFileInputException("Number of rows in file is out of range! Must be between " +
                    GameSettings.MIN_BOARD_ROWS + " and " + GameSettings.MAX_BOARD_ROWS);
        }
        //check validation of cols number
        if (gameSettings.getColumns() < GameSettings.MIN_BOARD_COLS || gameSettings.getColumns() > GameSettings.MAX_BOARD_COLS) {
            throw new InvalidFileInputException("Number of columns in file is out of range! Must be between " +
                    GameSettings.MIN_BOARD_COLS + " and " + GameSettings.MAX_BOARD_COLS);
        }
        //check validation of target
        if (gameSettings.getTarget() >= gameSettings.getRows() || gameSettings.getTarget() >= gameSettings.getColumns()) {
            throw new InvalidFileInputException("Target in file is invalid. Must be bellow " +
                                                Math.min(gameSettings.getColumns(), gameSettings.getRows()));
        }

        mIsFileLoaded = true; // File loaded successfully
    }
}
