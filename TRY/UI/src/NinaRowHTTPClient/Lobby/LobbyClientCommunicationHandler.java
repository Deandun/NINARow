package NinaRowHTTPClient.Lobby;

import NinaRowHTTPClient.GeneralCommunication.CommunicationHandler;
import Tasks.PullTimerTask;

import java.util.Timer;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class LobbyClientCommunicationHandler {
    private static final int PULL_INTERVAL = 1500;
    private static final String USERNAME_PARAM = "username";
    private PullTimerTask mPullOnlinePlayersTimerTask;
    private PullTimerTask mPullGamessTimerTask;

    public void startObservingOnlinePlayerNames(Consumer<String> onReceivedOnlinePlayerNames, BiConsumer<String, Integer> onErrorObservingOnlinePlayerNames) {
        CommunicationHandler communicationHandler = new CommunicationHandler();
        communicationHandler.setPath(LobbyServerAddresses.PULL_PLAYERS_URL);
        this.mPullOnlinePlayersTimerTask = new PullTimerTask(
                () -> communicationHandler.doGet(onReceivedOnlinePlayerNames, onErrorObservingOnlinePlayerNames)
        );

        Timer timer = new Timer();

        timer.schedule(this.mPullOnlinePlayersTimerTask, 0, PULL_INTERVAL);
    }

    public void startObservingGames(Consumer<String> onReceivedUpdatedGames, BiConsumer<String, Integer> onErrorObservingGames) {
        CommunicationHandler communicationHandler = new CommunicationHandler();
        communicationHandler.setPath(LobbyServerAddresses.PULL_GAMES_URL);
        this.mPullGamessTimerTask = new PullTimerTask(
                () -> communicationHandler.doGet(onReceivedUpdatedGames, onErrorObservingGames)
        );

        Timer timer = new Timer();

        timer.schedule(this.mPullGamessTimerTask, 0, PULL_INTERVAL);
    }

    public void logout(Runnable onLogoutFinish, String userName) {
        CommunicationHandler communicationHandler = new CommunicationHandler();

        // logout, cancel timer tasks
        this.mPullOnlinePlayersTimerTask.cancel();
        this.mPullGamessTimerTask.cancel();

        communicationHandler.setPath(LobbyServerAddresses.LOGOUT_URL);
        communicationHandler.addParameter(USERNAME_PARAM, userName);
        communicationHandler.doGet(onLogoutFinish);
    }

    private static class LobbyServerAddresses {
        private static final String PULL_PLAYERS_URL = "userslist";
        private static final String PULL_GAMES_URL = "gamesdata";
        private static final String LOGOUT_URL = "javafx/logout";
    }
}
