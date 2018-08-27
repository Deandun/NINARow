package UI;

import static UI.FinalSettings.*;

public class Theame {

    private String mBoardBackground;
    private String mGameDetailsBackground;
    private String mPlayerDetailsBackground;
    private String mConfigBackground;

    public Theame(){
        setRelaxTheame();
    }

    public void setRelaxTheame() {
        this.mBoardBackground = RELAX_THEAME_BOARD_BACKGROUND;
        this.mConfigBackground = RELAX_THEAME_CONFIG_BACKGROUND;
        this.mPlayerDetailsBackground = RELAX_THEAME_PLAYERDETAILS_BACKGROUND;
        this.mPlayerDetailsBackground = RELAX_THEAME_GAMEDETAILS_BACKGROUND;
    }

    public void setCrazyTheame(){
        this.mBoardBackground = CRAZY_THEAME_BOARD_BACKGROUND;
        this.mConfigBackground = CRAZY_THEAME_CONFIG_BACKGROUND;
        this.mPlayerDetailsBackground = CRAZY_THEAME_PLAYERDETAILS_BACKGROUND;
        this.mPlayerDetailsBackground = CRAZY_THEAME_GAMEDETAILS_BACKGROUND;
    }

    public String getCurrentBoardBackground() {
        return mBoardBackground;
    }

    public String getCurrentGameDetailsBackground() {
        return mGameDetailsBackground;
    }

    public String getCurrentPlayerDetailsBackground() {
        return mPlayerDetailsBackground;
    }

    public String getCurrentConfigBackground() {
        return mConfigBackground;
    }
}

