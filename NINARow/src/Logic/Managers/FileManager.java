package Logic.Managers;

import Logic.Exceptions.InvalidFileInputException;
import Logic.Models.Configuration;
import Logic.Models.GameSettings;

public class FileManager {

    private boolean mIsFileLoaded = false;

    public boolean getIsFileLoaded() {
        return mIsFileLoaded;
    }

    // TODO: throw exception if needed
    public void LoadGameFile(String filePath) {
        //TODO: use builder.parser(XML file name)

        try {
            checkIfFileInputIsValid();
        } catch (InvalidFileInputException e) {
            e.printStackTrace();
        }


        mIsFileLoaded = true;
    }


    //@Override
    private void checkIfFileInputIsValid() throws InvalidFileInputException {
        GameSettings gameSettings = GameSettings.getInstance();

        //check validation of rows number
        if (gameSettings.getRows() < Configuration.MIN_BOARD_ROWS || gameSettings.getRows() > Configuration.MAX_BOARD_ROWS) {
            throw new InvalidFileInputException("Number of rows in file is out of range!");
        }
        //check validation of cols number
        if (gameSettings.getColumns() < Configuration.MIN_BOARD_COLS || gameSettings.getColumns() > Configuration.MAX_BOARD_COLS) {
            throw new InvalidFileInputException("Number of columns in file is out of range!");
        }
        //check validation of target
        if (gameSettings.getTarget() >= gameSettings.getRows() || gameSettings.getTarget() >= gameSettings.getColumns()) {
            throw new InvalidFileInputException("Target in file is invalid (bigger than board size");
        }
    }
}
