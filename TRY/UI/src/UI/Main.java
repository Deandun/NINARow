package UI;

import UI.Controllers.AppController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import java.net.URL;

public class Main extends Application {

    private final String mCSSPath = "css/NInARowCss.css"; //TODO: implement

    @Override
    public void start(Stage primaryStage) throws Exception{
        FXMLLoader loader = new FXMLLoader();

        URL mainFXML = getClass().getResource("AppFxml.fxml");
        loader.setLocation(mainFXML);
        BorderPane root = loader.load();

        // wire up controller
        AppController appController = loader.getController();

        primaryStage.setTitle("DeaNoy Game - NinARow");
        Scene scene  = new Scene(root, 670, 575);
        primaryStage.setScene(scene);
        primaryStage.show();

        //        FXMLLoader loader = new FXMLLoader();
//        //   Parent root = FXMLLoader.load(getClass().getResource("AppFxml.fxml"));
//
//        // load main fxml
//        URL mainFXML = getClass().getResource("Controllers/AppFxml.fxml");
//        loader.setLocation(mainFXML);
//        BorderPane root = loader.load();
//
//        // wire up controller
//        AppController appController = loader.getController();
//
//        primaryStage.setTitle("DeaNoy Game - NinARow");
//        Scene scene  = new Scene(root, 670, 575);
//        primaryStage.setScene(scene);
//        primaryStage.show();

  /*    BusinessLogic businessLogic = new BusinessLogic(histogramController);
        appController.setPrimaryStage(primaryStage);
        appController.setBusinessLogic(businessLogic);

*/    }
    public static void main(String[] args) {
        launch(args);
    }
}
