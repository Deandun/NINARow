package UI;

import Logic.Models.Player;
import UI.Controllers.App;
import UI.Controllers.LobbyController;
import UI.Controllers.LoginController;
import UI.UIMisc.GameDescriptionData;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;

public class UIGameHandler extends Application {
    private final String mCSSPath = "UI/css/NInARowCss.css";

    private FXMLLoader mLoader = new FXMLLoader();
    private Stage mPrimaryStage;
    private Player mLoggedInPlayer;

    @Override
    public void start(Stage primaryStage) throws Exception {
        this.mPrimaryStage = primaryStage;
        this.loadLoginController();
    }

    private void loadLoginController() {
        this.mLoader = new FXMLLoader();
        URL mainFXML = getClass().getResource("Login.fxml");
        mLoader.setLocation(mainFXML);
        Pane root = null;
        try {
            root = mLoader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.mLoggedInPlayer = null;
        LoginController loginController = mLoader.getController();
        loginController.setmOnFinishedLogin(
                (playerID) ->  {
                    this.mLoggedInPlayer = playerID;
                    this.loadLobbyController();
                }
            );

        this.loadControllerToStage("Login", root);
    }

    private void loadLobbyController() {
        mLoader = new FXMLLoader();
        URL mainFXML = getClass().getResource("Lobby.fxml");
        mLoader.setLocation(mainFXML);
        ScrollPane root = null;

        try {
            root = mLoader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }

        LobbyController lobbyController = mLoader.getController();
        lobbyController.setmOnLogout(this::loadLoginController);
        lobbyController.setmOnEnteringGame(this::loadGameController);

        // wire up controller
        mPrimaryStage.setTitle("Lobby");
        Scene scene = new Scene(root);
        //scene.getStylesheets().add(mCSSPath);
        mPrimaryStage.setScene(scene);
        mPrimaryStage.show();
    }

    private void loadGameController(GameDescriptionData data) {
        mLoader = new FXMLLoader();
        URL mainFXML = getClass().getResource("NinARow.fxml");
        mLoader.setLocation(mainFXML);
        ScrollPane root = null;
        try {
            root = mLoader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
        App appController = mLoader.getController();
        appController.init(data, this.mLoggedInPlayer);
        appController.setOnPlayerLeftGame(this::loadLobbyController);

        // wire up controller
        mPrimaryStage.setTitle("DeaNoy Game - NinARow");
        Scene scene = new Scene(root);
        scene.getStylesheets().add(mCSSPath);

        //TODO: check if this is necessary. if not - remove and use loadControllerToStage
        appController.resizeWidth(mPrimaryStage.widthProperty());
        appController.resizeHeight(mPrimaryStage.heightProperty());

        mPrimaryStage.setScene(scene);
        mPrimaryStage.show();
    }

    private void loadControllerToStage(String title, Pane rootPane) {
        // wire up controller
        mPrimaryStage.setTitle(title);
        Scene scene = new Scene(rootPane);
        //scene.getStylesheets().add(mCSSPath);
        mPrimaryStage.setScene(scene);
        mPrimaryStage.show();
    }

    public void launch() {
        launch(null);
    }
}