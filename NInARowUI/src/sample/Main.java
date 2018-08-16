package sample;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    private final String mCSSPath = "sample/css/NInARowCss.css";

    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("FxmlNinARow.fxml"));

        primaryStage.setTitle("DeaNoy Game - NinARow");
        Scene scene  = new Scene(root, 670, 575);
        //scene.getStylesheets().add(getClass().getResource(mCSSPath).toExternalForm());
        primaryStage.setScene(scene);
        primaryStage.show();
    }


    public static void main(String[] args) {
        launch(args);
    }
}
