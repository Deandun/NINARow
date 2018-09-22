package UI.Controllers;

import Logic.Enums.eVariant;
import NinaRowHTTPClient.Lobby.ILobbyClientLogicDelegate;
import NinaRowHTTPClient.Lobby.LobbyClientLogic;
import UI.Enums.eGameState;
import UI.UIMisc.GameDescriptionData;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public class LobbyController implements ILobbyClientLogicDelegate {

    private String mOnlineUserName;
    private Consumer<GameDescriptionData> mOnEnteringGame;
    private Runnable mOnLogout;
    private LobbyClientLogic mLobbyClientLogic;

    @FXML
    private ScrollPane muiScrollPane;

    @FXML
    private BorderPane muiBorderPane;

    @FXML
    private HBox muiGameOptionsTopTab;

    @FXML
    private Button muiLogoutBtn;

    @FXML
    private VBox muiOnlinePlayers;

    @FXML
    private Text muiOnlinePlayerTitle;

    @FXML
    private FlowPane muiGameDetailsFlowPane;

    @FXML
    private void initialize() {
        this.muiLogoutBtn.setOnMouseClicked(
                (e) -> this.mLobbyClientLogic.logout(this.mOnlineUserName)
        );

        this.mLobbyClientLogic = new LobbyClientLogic(this);
        this.initFetchingPlayers();
        this.initFetchingGames();
    }

    private void initFetchingPlayers() {
        this.mLobbyClientLogic.observeOnlinePlayerNames();
    }

    public void setmOnlineUserName(String mOnlineUserName) {
        this.mOnlineUserName = mOnlineUserName;
    }


    private void initFetchingGames() {
        this.mLobbyClientLogic.observeGames();
    }

    private void onJoinGameClick(GameDescriptionData gameDescriptionData) {
        this.mOnEnteringGame.accept(gameDescriptionData);
    }

    public void setmOnEnteringGame(Consumer<GameDescriptionData> mOnEnteringGame) {
        this.mOnEnteringGame = mOnEnteringGame;
    }

    public void setmOnLogout(Runnable mOnLogout) {
        this.mOnLogout = mOnLogout;
    }

    @Override
    public void onPlayerNamesUpdate(List<String> playerNames) {
        Platform.runLater(
                () -> {
                    this.muiOnlinePlayers.getChildren().clear();
                    this.muiOnlinePlayers.getChildren().add(new Label("Online Players"));
                    playerNames.forEach(
                            (name) -> {
                                Label nameLabel = new Label(name);
                                this.muiOnlinePlayers.getChildren().add(nameLabel);
                            }
                    );
                }
        );
    }

    @Override
    public void onErrorUpdatingPlayerNames(String errorMessage) {

    }

    @Override
    public void onGamesUpdate(List<GameDescriptionData> updatedGamesList) {
        Platform.runLater(
                () -> {

                    this.muiGameDetailsFlowPane.getChildren().clear();
                    updatedGamesList.forEach(
                            (gameDescriptionData) -> {
                                GameDescriptionController gameController =
                                        new GameDescriptionController(gameDescriptionData, this::onJoinGameClick);
                                this.muiGameDetailsFlowPane.getChildren().add(gameController.getRoot());
                            }
                    );
                }
        );
    }

    @Override
    public void onErrorUpdatingGames(String errorMessage) {

    }

    @Override
    public void onLogoutFinish() {
        Platform.runLater(this.mOnLogout);
    }
}
