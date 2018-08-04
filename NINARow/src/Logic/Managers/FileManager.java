package Logic.Managers;

import Logic.Enums.eVariant;
import Logic.Exceptions.InvalidFileInputException;
import Logic.Models.GameSettings;
import Logic.Models.Player;
import Logic.jaxb.schema.generated.Board;
import Logic.jaxb.schema.generated.Game;
import Logic.jaxb.schema.generated.Players;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.util.List;

public class FileManager {

    private final static String JAXB_XML_GAME_PACKAGE_NAME = "Logic.jaxb.schema.generated";
    private boolean mIsFileLoaded = false;

    public boolean getIsFileLoaded() {
        return mIsFileLoaded;
    }

    public void LoadGameFile(String filePath) throws InvalidFileInputException, FileNotFoundException, IOException, JAXBException {
        Game gameInfo = getDataFromFile(filePath);
        checkIfFileInputIsValid(gameInfo); // Throws if input is invalid
        setData(gameInfo);
    }

    private Game getDataFromFile(String filePath) throws FileNotFoundException, IOException, JAXBException {
        Game game;

        try(InputStream inputStream = new FileInputStream(filePath)) {
            game = deserializeFrom(inputStream);
        }

        return game;
    }

    private Game deserializeFrom(InputStream in) throws JAXBException {
        JAXBContext jc = JAXBContext.newInstance(JAXB_XML_GAME_PACKAGE_NAME);
        Unmarshaller u = jc.createUnmarshaller();

        return (Game) u.unmarshal(in);
    }

    private void setData(Game gameInfo) {
        GameSettings gameSettings = GameSettings.getInstance();
        Board boardInfo = gameInfo.getBoard();

        gameSettings.setColumns(boardInfo.getColumns().intValue());
        gameSettings.setRows(boardInfo.getRows());
        gameSettings.setTarget(gameInfo.getTarget().intValue());
        gameSettings.setVariant(eVariant.valueOf(gameInfo.getVariant()));
    }

    private void checkIfFileInputIsValid(Game gameInfo) throws InvalidFileInputException {
        mIsFileLoaded = false; // Reset this boolean for the new file that is being loaded.

        //check validation of rows number
        if (gameInfo.getBoard().getRows() < GameSettings.MIN_BOARD_ROWS || gameInfo.getBoard().getRows() > GameSettings.MAX_BOARD_ROWS) {
            throw new InvalidFileInputException("Number of rows in file is out of range! Must be between " +
                    GameSettings.MIN_BOARD_ROWS + " and " + GameSettings.MAX_BOARD_ROWS);
        }
        //check validation of cols number
        if (gameInfo.getBoard().getColumns().intValue() < GameSettings.MIN_BOARD_COLS || gameInfo.getBoard().getColumns().intValue() > GameSettings.MAX_BOARD_COLS) {
            throw new InvalidFileInputException("Number of columns in file is out of range! Must be between " +
                    GameSettings.MIN_BOARD_COLS + " and " + GameSettings.MAX_BOARD_COLS);
        }
        //check validation of target
        if (gameInfo.getTarget().intValue() >= gameInfo.getBoard().getRows() || gameInfo.getTarget().intValue() >= gameInfo.getBoard().getColumns().intValue()) {
            throw new InvalidFileInputException("Target in file is invalid. Must be bellow " +
                                                Math.min(gameInfo.getBoard().getColumns().intValue(), gameInfo.getBoard().getRows()));
        }

        mIsFileLoaded = true; // No exceptions thrown, file loaded successfully
    }
}
