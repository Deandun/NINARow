package MultiGamesLogic;

import Logic.Exceptions.InvalidFileInputException;
import Logic.Game;
import Logic.Managers.FileManager;
import Logic.Models.GameDescriptionData;
import Logic.Models.GameSettings;
import Logic.Models.PlayTurnParameters;
import Logic.Models.PlayedTurnData;

import javax.xml.bind.JAXBException;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

public class GamesManager {
    private Map<String, Game> mGameNameToGame = new HashMap<>();
    private FileManager mFileManager = new FileManager();

    // User actions.

    public void addGame(InputStream fileContentStream, String uploaderName) throws InterruptedException, IOException, JAXBException, InvalidFileInputException {
        GameSettings gameSettingsFromFile;

        gameSettingsFromFile = this.mFileManager.LoadGameFile(fileContentStream);
        gameSettingsFromFile.setUploaderName(uploaderName);

        if (!this.isGameNameAlreadyExist(gameSettingsFromFile.getmGameName())) {
            Game newGame = new Game(gameSettingsFromFile);
            this.mGameNameToGame.put(gameSettingsFromFile.getmGameName(), newGame);
        } else {
                throw new InvalidFileInputException("Invalid game file! Game name is already exists.");
        }
    }

    private boolean isGameNameAlreadyExist(String gameName) {
        return this.mGameNameToGame.keySet().stream().anyMatch(name -> name.equals(gameName));
    }

    public void userJoinedGame(String gameName, String playerName) {
        // get relevant game from map.

        // create player with new name.

        // check if game should start - start game.
    }

    public void userLeftGame(String gameName, String playerName) {
        // get relevant game from map.

        // remove player from game.
    }

    public void playTurn(String gameName, PlayTurnParameters params) {
        // get relevant game from map.

        // play turn with params.
    }

    // Pull

    public List<GameDescriptionData> getAllGamesInfo() {
        List<GameDescriptionData> gameDescriptionDataList = new ArrayList<>();

        this.mGameNameToGame.values().forEach(
                (game) -> gameDescriptionDataList.add(game.getGameDescriptionData())
        );

        return gameDescriptionDataList;
    }

    public List<PlayedTurnData> getTurnHistoryForGame(String gameName, int currentTurnForClient) {
        //TODO check
        Game game = this.mGameNameToGame.get(gameName);

        return game.getTurnHistory();
    }
}
