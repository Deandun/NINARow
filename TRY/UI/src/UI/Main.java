package UI;

import UI.Controllers.App;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
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
        primaryStage.setTitle("DeaNoy Game - NinARow");
        Scene scene = new Scene(root, 670, 575);

        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
