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

    public static final String BACKGROUND_SETTINGS = " -fx-background-repeat: stretch;   \n" +
        "    -fx-background-size: cover;\n" +
        "    -fx-background-position: center;";
    public static final String AVIADCOHEN_THEAME_IMAGE_BACKGROUND = "-fx-background-image: url(\"UI/Images/AviadCohenTheameBackground.jpg\");";
    public static final String GUYRONEN_THEAME_IMAGE_BACKGROUND = "-fx-background-image: url(\"UI/Images/GuyRonenTheameBackground.png\");";
    public static final String AVIADCOHEN_THEAME_BOARD_BACKGROUND = "-fx-background-color: #F9D793;"; //only for example

}
