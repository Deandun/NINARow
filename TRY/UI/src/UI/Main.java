package UI;

import UI.Controllers.App;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.awt.*;
import java.net.URL;

import static UI.FinalSettings.APP_WINDOW_HEIGHT;
import static UI.FinalSettings.APP_WINDOW_WIDTH;

public class Main extends Application {

    private final String mCSSPath = "UI/css/NInARowCss.css";

    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader loader = new FXMLLoader();

        URL mainFXML = getClass().getResource("NinARow.fxml");
        loader.setLocation(mainFXML);
        ScrollPane root = loader.load();
        App appController = loader.getController();

        // wire up controller
        primaryStage.setTitle("DeaNoy Game - NinARow");
        Scene scene = new Scene(root);
        scene.getStylesheets().add(mCSSPath);

       appController.resizeWidth(primaryStage.widthProperty());
       appController.resizeHeight(primaryStage.heightProperty());
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
