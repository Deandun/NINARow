package UI.Controllers;

import javafx.fxml.FXML;
import javafx.scene.layout.BorderPane;
import sun.security.krb5.Config;

public class AppController {

    @FXML private BorderPane mPaneApp;
    @FXML private ConfigAndStartGameController mConfigAndStartGameController;

    private ConfigAndStartGameController mConfig;

    private BoardController mBoard;
    private GameDetailsController mGameDetails;
    private PlayerDetailsController mPlayerDetails;

    public AppController() {
        this.mConfig = new ConfigAndStartGameController();
        this.mGameDetails = new GameDetailsController();
        this.mPlayerDetails = new PlayerDetailsController();
        this.mBoard = new BoardController(5, 7); // TODO: remove dummy init
        //initPanes();
    }

    public void initialize(){
        mPaneApp.setTop(mConfig.getPaneConfigAndStartGame());
        mPaneApp.setLeft(mGameDetails.getPaneGameDetails());
        mPaneApp.setRight(mPlayerDetails.getPanePlayerDetails());
       // this.mBoard.InitBoard();
        //this.mPaneApp.setCenter(this.mBoard.getBoardPane());
    }
    public BorderPane getPaneApp() {
        return mPaneApp;
    }
}
