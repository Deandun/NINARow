package UI.Controllers;
import UI.Controllers.ControllerDelegates.IGameSettingsControllerDelegate;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import UI.FinalSettings;

import java.util.Optional;

import static UI.FinalSettings.EXIT_BTN_SIZE;


public class GameDetailsController{

    @FXML private VBox mPaneGameDetails;
    @FXML private Label mLblDetails;
    @FXML private Label mLblCurrentPlayer;
    @FXML private Label mLblTurnNumber;
    @FXML private Label mLblTargetSize;
    @FXML private Label mLblVariant;
    @FXML private Button BtnExitGame;

    private SimpleStringProperty mCurrentPlayer;
    private SimpleStringProperty mTurnNumber;
    private SimpleStringProperty mTargetSize;
    private SimpleStringProperty mVariant;
    private int mTurnCounter;
    private IGameSettingsControllerDelegate mDelegate;

    public GameDetailsController() {
        this.mCurrentPlayer = new SimpleStringProperty();
        this.mTurnNumber= new SimpleStringProperty();
        this.mVariant = new SimpleStringProperty();
        this.mTargetSize = new SimpleStringProperty();

        this.BtnExitGame.setGraphic(new ImageView(new Image(getClass().getResourceAsStream("/UI/Images/Exit.JPG"), EXIT_BTN_SIZE, EXIT_BTN_SIZE, true, true)));
        this.BtnExitGame.setPadding(new Insets(1));
        this.BtnExitGame.setOnMouseClicked(
                e -> exitGame(e)
        );
    }

    @FXML
    private void initialize() {
        this.mLblCurrentPlayer.textProperty().bind(this.mCurrentPlayer);
        this.mLblTurnNumber.textProperty().bind(this.mTurnNumber);
        this.mLblVariant.textProperty().bind(this.mVariant);
        this.mLblTargetSize.textProperty().bind(this.mTargetSize);
    }

    public void updateDetails(String playerName){
        this.mLblCurrentPlayer.setText(FinalSettings.CURRENT_PLAYER_STR + playerName);
    }

    public void clearDetails() {
        this.mTurnCounter = 0;
        initLbls();
    }

    public void initLbls() {
        this.mLblCurrentPlayer.setText(FinalSettings.CURRENT_PLAYER_STR);
        this.mLblTargetSize.setText(FinalSettings.TARGET_SIZE_STR);
        this.mLblTurnNumber.setText(FinalSettings.TURN_NUM_STR);
        this.mLblVariant.setText(FinalSettings.VARIANT_STR);
    }

    @FXML
    void exitGame(MouseEvent event) { //pop up msg that ask if the use sure he wants to quit
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Exit Game");
        alert.setHeaderText("Exit Game Button pressed");
        alert.setContentText("Are you sure you want to exit game?");
        Optional<ButtonType> result = alert.showAndWait();

        if (result.get() == ButtonType.OK){ //user chose ok
            mDelegate.ExitGameBtnClicked(true);
        } else { //user chose cancel

        }
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

    public void setDelegate(IGameSettingsControllerDelegate delegate){
        this.mDelegate = delegate;
    }
}
