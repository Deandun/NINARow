package Logic.Managers;

import Logic.Enums.ePlayerType;
import Logic.Enums.eVariant;
import Logic.Exceptions.InvalidFileInputException;
import Logic.Models.GameSettings;
import Logic.generated.Board;
import Logic.generated.Game;
import Logic.generated.GameDescriptor;
import Logic.generated.Player;
import Logic.generated.Players;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.*;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class FileManager {

    private final static String JAXB_XML_GAME_PACKAGE_NAME = "Logic.generated";
    private boolean mIsFileLoaded = false;

    public boolean getIsFileLoaded() {
        return mIsFileLoaded;
    }

    public void LoadGameFile(String filePath, Runnable onLoadFileFinish, Runnable onFinishedCheckingFileValidity) throws InvalidFileInputException, FileNotFoundException, IOException, JAXBException, InterruptedException {
        mIsFileLoaded = false; // Reset flag before checking if current file loaded successfully.
        GameDescriptor gameDescriptor = getDataFromFile(filePath);
        onLoadFileFinish.run();
        Thread.sleep(300);

        checkIfFileInputIsValid(gameDescriptor); // Throws if input is invalid
        onFinishedCheckingFileValidity.run();
        Thread.sleep(300);
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
        setPlayersData(gameDescriptor.getPlayers().getPlayer());
        setGameInfoData(gameDescriptor.getGame());
    }

    private void setGameInfoData(Game gameInfo) {
        GameSettings gameSettings = GameSettings.getInstance();
        Board boardInfo = gameInfo.getBoard();

        gameSettings.setColumns(boardInfo.getColumns().intValue());
        gameSettings.setRows(boardInfo.getRows());
        gameSettings.setTarget(gameInfo.getTarget().intValue());
        gameSettings.setVariant(eVariant.valueOf(gameInfo.getVariant()));
    }

    // Parameter is generated player class.
    private void setPlayersData(Collection<Player> playersFromFile) {
        // Set players from file.
        playersFromFile.forEach(
                playerFromFile -> {
                    Logic.Models.Player newPlayer = new Logic.Models.Player();
                    newPlayer.init(Short.toString(playerFromFile.getId()),
                            playerFromFile.getName(), ePlayerType.valueOf(playerFromFile.getType()));
                    GameSettings.getInstance().getPlayers().add(newPlayer);
                }
        );
    }

    private void checkIfFileInputIsValid(GameDescriptor gameDescriptor) throws InvalidFileInputException {
        checkIfGameDataIsValid(gameDescriptor.getGame());
        checkIfPlayersDataIsValid(gameDescriptor.getPlayers());
    }

    private void checkIfGameDataIsValid(Game gameInfo) throws InvalidFileInputException {
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
                || gameInfo.getTarget().intValue() < GameSettings.MIN_TARGET) {
            throw new InvalidFileInputException("Target in file is invalid. Must be between " + GameSettings.MIN_TARGET + " and the minumum between the rows/columns.");
        }
    }

    private void checkIfPlayersDataIsValid(Players players) throws InvalidFileInputException {
        if(players.getPlayer().size() < GameSettings.MIN_NUM_OF_PLAYERS || players.getPlayer().size() > GameSettings.MAX_NUM_OF_PLAYERS) {
            throw new InvalidFileInputException("Number of players in file is invalid. Must be between " + GameSettings.MIN_NUM_OF_PLAYERS + " and " + GameSettings.MAX_NUM_OF_PLAYERS);
        }

        if(!doPlayersHaveUniqueIDs(players.getPlayer())) {
            throw new InvalidFileInputException("Two players cannot have the same ID");
        }
    }

    private boolean doPlayersHaveUniqueIDs(Collection<Player> players) {
        Set<Short> IDset = new HashSet<>();

        players.forEach(
                player -> IDset.add(player.getId())
        );

        // For more than 1 player has the same ID, there will be only 1 value in the IDset.
        return players.size() == IDset.size();
    }
}
