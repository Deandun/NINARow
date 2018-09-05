package UI;

import java.io.Serializable;

public class FinalSettings implements Serializable {

    public static final String CURRENT_PLAYER_STR = "Current player: ";

    public static final String TARGET_SIZE_STR = "Target Size: ";
    public static final String TURN_NUM_STR = "Turn number: ";
    public static final String VARIANT_STR = "Variant: ";
    public static final int CELL_SIZE = 45;
    public static final int POPOUT_BTN_SIZE = CELL_SIZE - 5;
    public static final int EXIT_BTN_SIZE = 45;

    // Button text
    public static final String START_BTN_TEXT = "Start Game";
    public static final String RESTART_BTN_TEXT = "Restart Game";
    public static final String REPLAY_START_BTN_TEXT = "Start Replay";
    public static final String REPLAY_STOP_BTN_TEXT = "Stop Replay";

    //css
    public static final String BACKGROUND_SETTINGS = " -fx-background-repeat: stretch;   \n" +
        "    -fx-background-size: cover;\n" +
        "    -fx-background-position: center;";
    public static final String AVIAD_THEME_IMAGE_BACKGROUND = "-fx-background-image: url(\"UI/Images/AviadTheameBackground.jpg\");";
    public static final String GUY_THEME_IMAGE_BACKGROUND = "-fx-background-image: url(\"UI/Images/GuyTheameBackground.png\");";
    public static final double LOW_OPACITY = 0.5;
    public static final double HIGH_OPACITY = 0.8;
    public static final String CELL_BORDER_ERROR = "-fx-border-color: red;" + " -fx-border-radius: 15.0;" + "-fx-border-width: 2px";
    public static final String CELL_BORDER_WINNING = "-fx-border-color: red;" + " -fx-border-radius: 15.0;" + "-fx-border-width: 2px";
    public static final String CELL_BORDER_DEFAULT = "-fx-border-color: none";
    public static final String BUTTON_GRADIENT_AVIAD = "-fx-background-color: linear-gradient(#008307, #9ce7a0)";
    public static final String BUTTON_GRADIENT_GUY = "-fx-background-color: linear-gradient(#2A5058, #61a2b1);";
    public static final String BUTTON_GRADIENT_AVIAD_UPSIDEDOWN =  "-fx-background-color: linear-gradient(#9ce7a0 #008307)";
    public static final String BUTTON_GRADIENT_GUY_UPSIDEDOWN ="-fx-background-color: linear-gradient( #61a2b1, #2A5058);";
}
