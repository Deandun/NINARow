package UI.Controllers;
import Logic.Enums.eGameState;
import Logic.Enums.eTurnType;
import Logic.Logic;
import Logic.Models.*;
import UI.Controllers.ControllerDelegates.IBoardControllerDelegate;
import UI.Controllers.ControllerDelegates.IGameSettingsControllerDelegate;
import UI.UIMisc.ImageManager;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.util.*;

import static UI.FinalSettings.EXIT_BTN_SIZE;

public class App implements IBoardControllerDelegate, IGameSettingsControllerDelegate {

    @FXML private StackPane mStackPane;
    @FXML private BorderPane mBorderPane;
    @FXML private GridPane mGridPaneConfig;
    @FXML private Button mBtnLoadFile;
    @FXML private Button mBtnStartGame;
    @FXML private Button mBtnExitGame;
    @FXML private FlowPane mFlowPanePlayerDetails;
    @FXML private Label mLblPlayer1Title;
    @FXML private Label mLblPlayer1Name;
    @FXML private Label mLblPlayer1ID;
    @FXML private Label mLblPlayer1Type;
    @FXML private VBox mVBoxGameDetails;
    @FXML private Label mLblDetails;
    @FXML private Label mLblCurrentPlayer;
    @FXML private Label mLblTurnNumber;
    @FXML private Label mLblTargetSize;
    @FXML private Label mLblVariant;

    private BoardController mBoardController;

    private GameDetailsController mGameDetailsController;
    //PlayerDetails Members:

    private SimpleStringProperty mPlayerName;
    private SimpleStringProperty mPlayerID;
    private SimpleStringProperty mPlayerType;
    private ImageView mPlayerSign;

    //GameDetails Members:

    private SimpleStringProperty mCurrentPlayer;
    private SimpleStringProperty mTurnNumber;
    private SimpleStringProperty mTargetSize;
    private SimpleStringProperty mVariant;
    private Logic mLogic;
    private int mTurnCounter;

    public App() {
        this.mLogic = new Logic();
        this.mCurrentPlayer = new SimpleStringProperty();
        this.mTurnNumber = new SimpleStringProperty();
        this.mVariant = new SimpleStringProperty();
        this.mTargetSize = new SimpleStringProperty();
    }

    @FXML
    private void initialize() {
        //PlayerDetails
//        mLblPlayer1Name.textProperty().bind(mPlayerName);
//        mLblPlayer1ID.textProperty().bind(mPlayerID);
//        mLblPlayer1Type.textProperty().bind(mPlayerType);
        // mIVPlayer1Sign.imageProperty().bind(mPlayerSign);

        //GameDetails:
//        mLblCurrentPlayer.textProperty().bind(mCurrentPlayer);
//        mLblTurnNumber.textProperty().bind(mTurnNumber);
//        mLblVariant.textProperty().bind(mVariant);
//        mLblTargetSize.textProperty().bind(mTargetSize);
        setOnAction();
        this.mBtnExitGame.setGraphic(new ImageView(new Image(getClass().getResourceAsStream("/UI/Images/Exit.JPG"), EXIT_BTN_SIZE, EXIT_BTN_SIZE, true, true)));
        this.mBtnExitGame.setText(null);
        this.mBtnExitGame.setPadding(new Insets(1));
    }

    @FXML
    public void loadFile() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select xml file");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("xml files", "*.xml"));
        File selectedFile = fileChooser.showOpenDialog(new Stage());

        if (selectedFile == null) {
            return;
        } else {
            try {
                this.mLogic.ReadGameFile(selectedFile.getAbsolutePath());
                this.mBtnStartGame.setDisable(false);
                this.mBoardController = new BoardController(GameSettings.getInstance().getRows(), GameSettings.getInstance().getColumns(), this);
                this.mBoardController.InitBoard();
                this.mBorderPane.setMaxSize(300, 300);
                // TODO: make it so setCenter doesn't "pull" top, left and right panes towards the center.
                this.mBorderPane.setCenter(this.mBoardController.getBoardPane());
                this.initImageManager();
            } catch(Exception e) {
                System.out.println(e.getMessage());
                //TODO: implement this and all of the other exceptions
            }
        }
    }

    @FXML
    void startGame() {
        this.mBtnLoadFile.setDisable(true); // TODO: do we really need to disable load file? I think we need to support loading a new file during a game. Need to check exercise
        this.mBoardController.setIsBoardEnabled(true);
        this.mBoardController.ResetBoard();
        this.mLogic.StartGame();
//        this.mGameDetailsController.setDelegate(this); TODO: figure out why this is null when we start game.
        initDetails();
    }

    public void setOnAction() {
        this.mBtnLoadFile.setOnAction(e -> loadFile());
        this.mBtnStartGame.setOnAction(e -> startGame());
        this.mBtnExitGame.setOnMouseClicked(e -> exitGame());
    }

    private void initDetails() {
      //  mLogic.getPlayerName
    }

    private void exitGame() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Exit Game");
        alert.setHeaderText("Exit Game Button pressed");
        alert.setContentText("Are you sure you want to exit game?");
        Optional<ButtonType> result = alert.showAndWait();

        if (result.get() == ButtonType.OK){ //user chose ok
            //mDelegate.ExitGameBtnClicked(true);
        } else { //user chose cancel

        }
    }

    private void initImageManager() {
        List<String> playerIDs = new ArrayList<>();

        GameSettings
                .getInstance()
                .getPlayers()
                .forEach(
                        player -> playerIDs.add(player.getID())
                );

        ImageManager.Clear(); // Reset existing images if there are any.
        ImageManager.SetImagesForPlayerIDs(playerIDs);
    }

    // IBoardControllerDelegate implementation

    @Override
    public void PopoutBtnClicked(int index) {

    }

    @Override
    public void ColumnClicked(int columnIndex) {
        try {
            PlayTurnParameters playTurnParameters = new PlayTurnParameters(columnIndex, eTurnType.AddDisc);
            List<PlayedTurnData> playedTurnData = this.mLogic.PlayTurn(playTurnParameters);
            this.handleUIAfterPlayedTurns(playedTurnData);
        } catch (Exception e) {
            e.printStackTrace();
            //TODO: catch all of the exceptions and handle them.
        }
    }

    private void handleUIAfterPlayedTurns(List<PlayedTurnData> playedTurnDataList) {
        for(PlayedTurnData turnData: playedTurnDataList) {
            this.mBoardController.InsertDistAt(turnData.getUpdatedCellsCollection());
            eGameState gameState = turnData.getGameState();

            if(gameState.equals(eGameState.Won)) {
                Map<Player, Collection<Cell>> playerToWinningSequenceMap = this.mLogic.getPlayerToWinningSequencesMap();

                this.mBoardController.DisplayWinningSequences(playerToWinningSequenceMap);
                //TODO: notify players that the game has been won. Disable the game and "Reset" logic to a state where there's a file loaded but game hasn't started.
                break;
            } else if (gameState.equals(eGameState.Draw)) {
                //TODO: notify players that the game has ended in a draw.
                break;
            }
        }
    }

    @Override
    public void ExitGameBtnClicked(boolean doExit) {

    }
}