package UI.Controllers;
import Logic.Logic;
import javafx.beans.property.SimpleStringProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.awt.event.MouseEvent;
import java.io.File;

public class App {

    @FXML private GridPane mGridPaneConfig;
    @FXML private Button mBtnLoadFile;
    @FXML private Button mBtnStartGame;
    @FXML private FlowPane mFlowPanePlayerDetails;
    @FXML private Label mLblPlayer1Title;
    @FXML private Label mLblPlayer1Name;
    @FXML private Label mLblPlayer1ID;
    @FXML private Label mLblPlayer1Type;
    @FXML private VBox mVBoxGameDetails;
    @FXML private Label mLblDetails;
    @FXML private Label mLblCurrentPlayer;
    @FXML private Label mLblTurnNumber;
    @FXML private Label mLblTargetSize;
    @FXML private Label mLblVariant;

    //PlayerDetails Members:

    private SimpleStringProperty mPlayerName;
    private SimpleStringProperty mPlayerID;
    private SimpleStringProperty mPlayerType;
    private ImageView mPlayerSign;

    //GameDetails Members:

    private SimpleStringProperty mCurrentPlayer;
    private SimpleStringProperty mTurnNumber;
    private SimpleStringProperty mTargetSize;
    private SimpleStringProperty mVariant;
    private Logic mLogic;
    private int mTurnCounter;

    public App() {
        this.mLogic = new Logic();
        this.mCurrentPlayer = new SimpleStringProperty();
        this.mTurnNumber= new SimpleStringProperty();
        this.mVariant = new SimpleStringProperty();
        this.mTargetSize = new SimpleStringProperty();
    }

    @FXML
    private void initialize() {
        //PlayerDetails
        mLblPlayer1Name.textProperty().bind(mPlayerName);
        mLblPlayer1ID.textProperty().bind(mPlayerID);
        mLblPlayer1Type.textProperty().bind(mPlayerType);
        // mIVPlayer1Sign.imageProperty().bind(mPlayerSign);

        //GameDetails:
        mLblCurrentPlayer.textProperty().bind(mCurrentPlayer);
        mLblTurnNumber.textProperty().bind(mTurnNumber);
        mLblVariant.textProperty().bind(mVariant);
        mLblTargetSize.textProperty().bind(mTargetSize);

        setOnAction();
    }

    @FXML
    public void loadFile(ActionEvent event) {

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select xml file");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("xml files", "*.xml"));
        File selectedFile = fileChooser.showOpenDialog(new Stage());

        if (selectedFile == null) {
            return;
        } else {
            mBtnStartGame.setDisable(false);
        }
    }

    @FXML
    void startGame(ActionEvent event) {
        mBtnLoadFile.setDisable(true);
        mLogic.StartGame();
    }

    public void setOnAction() {
        mBtnLoadFile.setOnAction(e -> loadFile(e));
        mBtnStartGame.setOnAction(e -> startGame(e));

    }

}
