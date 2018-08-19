package UI.Controllers;

import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;

public class PlayerDetailsController {

    @FXML private FlowPane mPanePlayerDetails;
    @FXML private Label mLblPlayer1;
    @FXML private Label mLblPlayer1Name;
    @FXML private Label mLblPlayer1ID;
    @FXML private Label mLblPlayer1Type;
    @FXML private ImageView mIVPlayer1Sign;

    private SimpleStringProperty mPlayerName;
    private SimpleStringProperty mPlayerID;
    private SimpleStringProperty mPlayerType;
    private ImageView mPlayerSign;


    @FXML
    private void initialize() {
        mLblPlayer1Name.textProperty().bind(mPlayerName);
        mLblPlayer1ID.textProperty().bind(mPlayerID);
        mLblPlayer1Type.textProperty().bind(mPlayerType);
       // mIVPlayer1Sign.imageProperty().bind(mPlayerSign);
    }


    public FlowPane getPanePlayerDetails() {
        return mPanePlayerDetails;
    }
}
