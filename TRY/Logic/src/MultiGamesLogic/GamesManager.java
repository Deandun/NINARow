package MultiGamesLogic;

import Logic.Enums.eGameState;
import Logic.Exceptions.InvalidFileInputException;
import Logic.Game;
import Logic.Managers.FileManager;
import Logic.Models.*;

import javax.xml.bind.JAXBException;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;


public class GamesManager {
    private Map<String, Game> mGameNameToGame = new HashMap<>();
    private FileManager mFileManager = new FileManager();

    // User actions.

    public synchronized void addGame(InputStream gameContentStream, String uploaderName) throws InterruptedException, IOException, JAXBException, InvalidFileInputException {
        GameSettings gameSettingsFromFile;

        gameSettingsFromFile = this.mFileManager.LoadGameFile(gameContentStream);
        gameSettingsFromFile.setUploaderName(uploaderName);

        if(!gameSettingsFromFile.getmGameName().contains("+")) { // Game name does not contain seporator - valid name.
            if (!this.isGameNameAlreadyExist(gameSettingsFromFile.getmGameName())) {
                Game newGame = new Game(gameSettingsFromFile);
                this.mGameNameToGame.put(gameSettingsFromFile.getmGameName(), newGame);
            } else {
                throw new InvalidFileInputException("Invalid game file! Game name is already exists.");
            }
        } else {
            throw new InvalidFileInputException("Invalid game name! Game name must not contain '+'."); // Game contains seporator - invalid name.
        }
    }

    private boolean isGameNameAlreadyExist(String gameName) {
        return this.mGameNameToGame.keySet().stream().anyMatch(name -> name.equals(gameName));
    }

    public void addUserToGame(String gameName, Player joiningPlayer) throws Exception {
        Game game = this.mGameNameToGame.get(gameName);

        game.addPlayer(joiningPlayer);

        if(game.shouldStartGame()) {
            game.StartGame();
        }
    }

    public void userLeftGame(String gameName, String playerName) {
        // get relevant game from map.

        // remove player from game.
    }

    public void playTurn(String gameName, PlayTurnParameters params) {
        // get relevant game from map.

        // play turn with params.
    }


    public GameDescriptionData getGameDescriptionForGameName(String gameName) {
        Game game = this.mGameNameToGame.get(gameName);

        return game.getGameDescriptionData();
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
        Game game = this.mGameNameToGame.get(gameName);

        return game.getTurnHistory();
    }

    public Collection<Player> getPlayersForGame(String gameName) {
        Game game = this.mGameNameToGame.get(gameName);
        return game.getPlayers();
    }

    public eGameState getGameState(String gameName) {
        Game game = this.mGameNameToGame.get(gameName);
        return game.GetGameState();
    }
}
