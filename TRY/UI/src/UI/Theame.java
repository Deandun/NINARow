package UI;

import static UI.FinalSettings.*;

public class Theame {

    private eTheameType mCurrentTheame;
    private String mCurrentBackgound;

    public Theame(){
        this.mCurrentTheame = eTheameType.AviadCohen;
        this.mCurrentBackgound = AVIADCOHEN_THEAME_IMAGE_BACKGROUND;
    }

    public void  setAviadTheame() {
        this.mCurrentTheame = eTheameType.AviadCohen;
        this.mCurrentBackgound = AVIADCOHEN_THEAME_IMAGE_BACKGROUND;
    }

    public void setGuyTheame(){
        this.mCurrentTheame = eTheameType.GuyRonen;
        this.mCurrentBackgound = GUYRONEN_THEAME_IMAGE_BACKGROUND;
    }

    public eTheameType getCurrentTheame() {
        return this.mCurrentTheame;
    }

    public String getCurrentTheameBackground() {
        return this.mCurrentBackgound + "\n" + BACKGROUND_SETTINGS; //TODO: change opacity
    }
}

