package sample;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;


public class NinARowController {
    @FXML private Button mBtnStartGame;

    @FXML protected void handleLoadFileButtonAction(ActionEvent event) {
        mBtnStartGame.setDisable(!mBtnStartGame.isDisable()); //dummy
    }




}
