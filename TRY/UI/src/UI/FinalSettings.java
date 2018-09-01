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

    public static final String BACKGROUND_SETTINGS = " -fx-background-repeat: stretch;   \n" +
        "    -fx-background-size: cover;\n" +
        "    -fx-background-position: center;";
    public static final String AVIAD_THEAME_IMAGE_BACKGROUND = "-fx-background-image: url(\"UI/Images/AviadTheameBackground.jpg\");";
    public static final String GUY_THEAME_IMAGE_BACKGROUND = "-fx-background-image: url(\"UI/Images/GuyTheameBackground.png\");";
    public static final String Aviad_THEAME_BOARD_BACKGROUND = "-fx-background-color: #F9D793;"; //only for example

}
