package UI.Controllers;

import javafx.fxml.FXML;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;

public class AppController{
    @FXML private StackPane AppStackPane;
    @FXML private BorderPane AppBoardePane;
    @FXML private ConfigAndStartGameController ConfigAndStartGameControllerController;
    @FXML private GameDetailsController GameDetailsControllerController;
    @FXML private PlayerDetailsController PlayerDetailsControllerController;
    @FXML private BoardController BoardControllerController;

    public AppController() {
        this.ConfigAndStartGameControllerController = new ConfigAndStartGameController();
        this.GameDetailsControllerController = new GameDetailsController();
        this.PlayerDetailsControllerController = new PlayerDetailsController();
    }

}
