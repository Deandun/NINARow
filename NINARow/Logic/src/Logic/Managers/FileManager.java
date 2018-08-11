package Logic.Managers;

import Logic.Enums.eVariant;
import Logic.Exceptions.InvalidFileInputException;
import Logic.Models.GameSettings;
import Logic.generated.Board;
import Logic.generated.Game;
import Logic.generated.GameDescriptor;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.*;

public class FileManager {

    private final static String JAXB_XML_GAME_PACKAGE_NAME = "Logic.jaxb.schema.generated";
    private boolean mIsFileLoaded = false;

    public boolean getIsFileLoaded() {
        return mIsFileLoaded;
    }

    public void LoadGameFile(String filePath) throws InvalidFileInputException, FileNotFoundException, IOException, JAXBException {
        mIsFileLoaded = false; // Reset flag before checking if current file loaded successfully.
        GameDescriptor gameDescriptor = getDataFromFile(filePath);

        checkIfFileInputIsValid(gameDescriptor); // Throws if input is invalid
        setData(gameDescriptor);
        mIsFileLoaded = true;
    }

    private GameDescriptor getDataFromFile(String filePath) throws FileNotFoundException, IOException, JAXBException {
        GameDescriptor gameDescriptor;

        if (!(new File(filePath)).exists()){
            throw new FileNotFoundException("Couldn't find " + filePath + " file!");
        }

        try(InputStream inputStream = new FileInputStream(filePath)) {
            gameDescriptor = deserializeFrom(inputStream);
        }

        return gameDescriptor;
    }

    private GameDescriptor deserializeFrom(InputStream in) throws JAXBException {
        JAXBContext jc = JAXBContext.newInstance(JAXB_XML_GAME_PACKAGE_NAME);
        Unmarshaller u = jc.createUnmarshaller();

        return (GameDescriptor) u.unmarshal(in);
    }

    private void setData(GameDescriptor gameDescriptor) {
        Game gameInfo = gameDescriptor.getGame();
        GameSettings gameSettings = GameSettings.getInstance();
        Board boardInfo = gameInfo.getBoard();

        gameSettings.setColumns(boardInfo.getColumns().intValue());
        gameSettings.setRows(boardInfo.getRows());
        gameSettings.setTarget(gameInfo.getTarget().intValue());
        gameSettings.setVariant(eVariant.valueOf(gameInfo.getVariant()));
    }

    private void checkIfFileInputIsValid(GameDescriptor gameDescriptor) throws InvalidFileInputException {
        Game gameInfo = gameDescriptor.getGame();
        int numOfPlayers;

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
        if (gameInfo.getTarget().intValue() >= Math.max(gameInfo.getBoard().getRows(), gameInfo.getBoard().getColumns().intValue())
                || gameInfo.getTarget().intValue() < GameSettings.Min_TARGET) {
            throw new InvalidFileInputException("Target in file is invalid. Must be between " + GameSettings.Min_TARGET + " and the minumum between the rows/columns.");
        }

        numOfPlayers = countNumOfPlayer(gameInfo);
        //check validation of num of players
        if (numOfPlayers > GameSettings.MAX_NUM_OF_PLAYERS || numOfPlayers < GameSettings.MIN_NUM_OF_PLAYERS){
            throw new InvalidFileInputException("Number of players is invalid. Must be between " + GameSettings.MIN_NUM_OF_PLAYERS + " - " + GameSettings.MAX_NUM_OF_PLAYERS);

        }

    }

    private int countNumOfPlayer(Game gameInfo) {
        int counter = 2;

        //TODO: implement after adding to schema new params
        return counter;
    }
}