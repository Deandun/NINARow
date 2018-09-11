package UI;

import UI.Controllers.App;
import UI.Controllers.LoginController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;

public class UIGameHandler extends Application {
    private final String mCSSPath = "UI/css/NInARowCss.css";

    private FXMLLoader mLoader = new FXMLLoader();
    private Stage mPrimaryStage;
    private String mLoggedInPlayerID;

    @Override
    public void start(Stage primaryStage) throws Exception {
        this.mPrimaryStage = primaryStage;
        this.loadLoginController();
        //this.loadGameController(); // TODO: remove dummy first call to game controller.

    }

    private void loadLoginController() throws IOException {
        URL mainFXML = getClass().getResource("Login.fxml");
        mLoader.setLocation(mainFXML);
        Pane root = mLoader.load();
        LoginController loginController = mLoader.getController();
        loginController.setmOnFinishedLogin(
                (playerID) ->  {
                    this.mLoggedInPlayerID = playerID;
                    this.loadLobbyController();
                }
            );

        this.loadControllerToStage("Login", root);
    }

    private void loadLobbyController() {
        // TODO:
    }

    private void loadGameController() throws IOException {
        URL mainFXML = getClass().getResource("NinARow.fxml");
        mLoader.setLocation(mainFXML);
        ScrollPane root = mLoader.load();
        App appController = mLoader.getController();
        appController.setOnPlayerQuitGame(this::loadLobbyController);

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
        scene.getStylesheets().add(mCSSPath);
        mPrimaryStage.setScene(scene);
        mPrimaryStage.show();
    }

    public void launch() {
        launch(null);
    }
}
