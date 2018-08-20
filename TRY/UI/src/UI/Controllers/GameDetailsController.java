package UI.Controllers;
import Logic.Logic;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import UI.FinalLabelText;

public class GameDetailsController {

    @FXML private VBox mPaneGameDetails;
    @FXML private Label mLblDetails;
    @FXML private Label mLblCurrentPlayer;
    @FXML private Label mLblGameTime;
    @FXML private Label mLblTurnNumber;
    @FXML private Label mLblTargetSize;
    @FXML private Label mLblVariant;
    @FXML private ImageView mIVExitGame;

    private SimpleStringProperty mCurrentPlayer;
    private SimpleStringProperty mGameTime;
    private SimpleStringProperty mTurnNumber;
    private SimpleStringProperty mTargetSize;
    private SimpleStringProperty mVariant;
    private Logic mLogic = new Logic();
    private int mTurnCounter;

    public GameDetailsController() {
        this.mCurrentPlayer = new SimpleStringProperty();
        this.mGameTime = new SimpleStringProperty();
        this.mTurnNumber= new SimpleStringProperty();
        this.mVariant = new SimpleStringProperty();
        this.mTargetSize = new SimpleStringProperty();
    }

    @FXML
    private void initialize() {
        mLblCurrentPlayer.textProperty().bind(mCurrentPlayer);
        mLblGameTime.textProperty().bind(mGameTime);
        mLblTurnNumber.textProperty().bind(mTurnNumber);
        mLblVariant.textProperty().bind(mVariant);
        mLblTargetSize.textProperty().bind(mTargetSize);
    }

    public void updateDetails(String playerName){
        mLblCurrentPlayer.setText(FinalLabelText.CURRENT_PLAYER_STR + playerName);
    }

    public void clearDetails() {
        mTurnCounter = 0;
        initLbls();
    }

    public void initLbls()
    {
        mLblCurrentPlayer.setText(FinalLabelText.CURRENT_PLAYER_STR);
        mLblTargetSize.setText(FinalLabelText.TARGET_SIZE_STR);
        mLblTurnNumber.setText(FinalLabelText.TURN_NUM_STR);
        mLblVariant.setText(FinalLabelText.VARIANT_STR);

    }

    @FXML
    void exitGame(MouseEvent event) {
        //create pop up msg that ask if the use sure he wants to quit
        mLogic.exitGame();
    }

    public void setTargetSize(String targetSize) {
        this.mTargetSize.set(targetSize);
    }

    public void setVariant(String variant) {
        this.mVariant.set(variant);
    }

    public VBox getPaneGameDetails() {
        return mPaneGameDetails;
    }
}
