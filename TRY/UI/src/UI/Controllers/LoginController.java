package UI.Controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import javafx.scene.input.*;
import java.util.function.Consumer;

public class LoginController {
    private Consumer<String> mOnFinishedLogin; // TODO: maybe consumer<Player>?

    @FXML
    private TextField muiNameTextField;

    @FXML
    private Button muiLoginButton;

    @FXML
    private Label muiErrorLabel;

    @FXML
    private void initialize() {
        this.muiLoginButton.setOnMouseClicked(this::onLoginClick);
        this.muiErrorLabel.setText(""); // Set error message to be invisible at the start.
    }

    public void setmOnFinishedLogin(Consumer<String> mOnFinishedLogin) {
        this.mOnFinishedLogin = mOnFinishedLogin;
    }

    private void onLoginClick(MouseEvent e) {
        String name = this.muiNameTextField.getText();

        // TODO: send request to add player. call login error/success
        this.onLoginSuccess(name);
    }

    private void onLoginError(String errorMessage) {
        this.muiErrorLabel.setText(errorMessage);
    }

    private void onLoginSuccess(String playerID) {
        this.mOnFinishedLogin.accept(playerID);
    }
}
