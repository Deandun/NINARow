package UI.Controllers;

import Logic.Enums.eVariant;
import UI.Enums.eGameState;
import UI.UIMisc.GameDescriptionData;
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
import java.util.Map;
import java.util.function.Consumer;

public class LobbyController {

    private Consumer<GameDescriptionData> mOnEnteringGame;
    private Runnable mOnLogout;

    // Maps between the game's name and hte game description controller (can't have 2 games with the same name).
    private Map<String, GameDescriptionController> mGameNameToGameControllerMap = new HashMap<>();

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
                (e) -> this.mOnLogout.run()
        );

        this.initFetchingPlayers();
        this.initFetchingGames();
    }

    private void initFetchingPlayers() {
        //TODO: set interval for getting player names. For now, dummy init names.
        this.muiOnlinePlayers.getChildren().add(new Label("Aviad"));
        this.muiOnlinePlayers.getChildren().add(new Label("Noy Toy"));
        this.muiOnlinePlayers.getChildren().add(new Label("Rusty"));
        this.muiOnlinePlayers.getChildren().add(new Label("Snek"));
    }

    private void initFetchingGames() {
        //TODO: set interval for getting games. For now, dummy init games.
        GameDescriptionData data = new GameDescriptionData();
        data.setmGameState(eGameState.Ready);
        data.setmVariant(eVariant.Popout);
        data.setmRows(7);
        data.setmColumns(8);
        data.setmCurrentNumberOfPlayers(2);
        data.setmMaxPlayers(5);
        data.setmTarget(4);
        data.setmGameName("Best game");
        data.setmUploaderName("Dividend");

        GameDescriptionController controller = new GameDescriptionController(data, this::onJoinGameClick);
        this.muiGameDetailsFlowPane.getChildren().add(controller.getRoot());

        GameDescriptionData data2 = new GameDescriptionData();

        data2.setmGameState(eGameState.Ready);
        data2.setmVariant(eVariant.Circular);
        data2.setmRows(5);
        data2.setmColumns(5);
        data2.setmCurrentNumberOfPlayers(4);
        data2.setmMaxPlayers(6);
        data2.setmTarget(3);
        data2.setmGameName("Second best game, but still a pretty decent game altogether. I mean, can't really complain.");
        data2.setmUploaderName("Dividend's wonderful dream of love");

        controller = new GameDescriptionController(data2, this::onJoinGameClick);
        this.muiGameDetailsFlowPane.getChildren().add(controller.getRoot());
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
}
