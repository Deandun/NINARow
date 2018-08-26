package UI;

import UI.Controllers.App;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import javafx.stage.Stage;

import javax.swing.text.html.ImageView;
import java.net.URL;

public class Main extends Application {

    private final String mCSSPath = "css/NInARowCss.css"; //TODO: implement

    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader loader = new FXMLLoader();

        URL mainFXML = getClass().getResource("temp.fxml");
        loader.setLocation(mainFXML);
        StackPane root = loader.load();

        // wire up controller
        App appController = loader.getController();
        primaryStage.setTitle("DeaNoy Game - NinARow");
        Scene scene = new Scene(root, 670, 575);

        primaryStage.setScene(scene);
        primaryStage.show();
        /*
          FXMLLoader loader = new FXMLLoader();

        URL mainFXML = getClass().getResource("AppFxml.fxml");
        loader.setLocation(mainFXML);

        StackPane root = loader.load();

        // wire up controller
        AppController appController = loader.getController();

        primaryStage.setTitle("DeaNoy Game - NinARow");
        Scene scene = new Scene(root, 670, 575);
        primaryStage.setScene(scene);
        primaryStage.show();
         */
    }

    public static void main(String[] args) {
        launch(args);
    }
}
