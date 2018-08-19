package UI.Controllers;
import java.io.File;
import java.net.URL;
import Logic.Logic;
import javafx.fxml.FXML;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class ConfigAndStartGameController extends GridPane {

    @FXML private ResourceBundle resources;
    @FXML private URL location;
    @FXML private GridPane mPaneConfigAndStartGame;
    @FXML private Button mBtnLoadFile;
    @FXML private Button mBtnStartFile;

    private Logic mLogic = new Logic(); // TODO: remove logic from this controller, only call logic in main app controller


    @FXML
    void loadFile(ActionEvent event) {
        //TODO: move to onclick
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select xml file");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("xml files", "*.xml"));
        File selectedFile = fileChooser.showOpenDialog(new Stage());
        if (selectedFile == null) {
            return;
        } else {
            mBtnStartFile.setDisable(false);
        }
    }

    @FXML
    void startGame(ActionEvent event) {
        mBtnLoadFile.setDisable(true);
        mLogic.StartGame();
    }

    public GridPane getPaneConfigAndStartGame() {
        return mPaneConfigAndStartGame;
    }
}
