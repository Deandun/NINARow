package UI.Controllers;
import Logic.Enums.eGameState;
import Logic.Enums.ePlayerType;
import Logic.Enums.eTurnType;
import Logic.Enums.eVariant;
import Logic.Interfaces.ILogicDelegate;
import Logic.Logic;
import Logic.Models.*;
import Logic.Models.Cell;
import Tasks.ReadGameFileTask;
import UI.Controllers.ControllerDelegates.IBoardControllerDelegate;
import UI.Controllers.ControllerDelegates.IGameSettingsControllerDelegate;
import UI.Theame;
import UI.UIMisc.ImageManager;
import UI.Replay.ReplayManager;
import UI.eInvalidMoveType;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import UI.eTheameType;

import javafx.event.ActionEvent;
import java.io.File;
import java.util.*;

import static UI.FinalSettings.EXIT_BTN_SIZE;

public class App implements IBoardControllerDelegate, IGameSettingsControllerDelegate, ILogicDelegate {

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
    @FXML private FlowPane mBottomProgressPane;
    @FXML private Label mProgressTextLabel;
    @FXML private ProgressBar mProgressBar;
    @FXML private ComboBox<eTheameType> mComboBoxTheame;

    //TODO: remove temp replay buttons and work with real fxml buttons.
    private Button mReplayButton = new Button("Start");
    private Button mBackReplayButton = new Button("Back");
    private Button mForwardReplayButton = new Button("Forward");

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

    // Replay
    private BooleanProperty mIsReplayInProgressProperty = new SimpleBooleanProperty();
    private ReplayManager mReplayManager = new ReplayManager();

    // Concurrency
    private boolean mIsTurnInProgress;

    // Themes
    private Theame mTheame;

    public App() {
        this.mLogic = new Logic(this);
        this.mCurrentPlayer = new SimpleStringProperty();
        this.mTurnNumber = new SimpleStringProperty();
        this.mVariant = new SimpleStringProperty();
        this.mTargetSize = new SimpleStringProperty();
        this.mTheame = new Theame();
        this.mTheame.setAviadTheame();
        this.mIsTurnInProgress = false;
    }

    private boolean shouldPlayComputerPlayerNext() {
        return this.mLogic.GetCurrentPlayer().getType().equals(ePlayerType.Computer);
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
        this.mComboBoxTheame.getItems().addAll(eTheameType.Aviad, eTheameType.Guy);
        changeTheame();
        this.initReplay();
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
                // DEAN change
                Runnable onTaskFinish = this::onReadGameFileFinish;
                this.mBottomProgressPane.setVisible(true);

                ReadGameFileTask readGameFileTask = new ReadGameFileTask(selectedFile.getAbsolutePath(), this.mLogic, onTaskFinish);
                this.bindTaskToUI(readGameFileTask);
                new Thread(readGameFileTask).start();
                // TODO: make it so setCenter doesn't "pull" top, left and right panes towards the center.

            } catch(Exception e) {
                System.out.println(e.getMessage());
                //TODO: implement this and all of the other exceptions
            }
        }
    }

    private void bindTaskToUI(ReadGameFileTask readGameFileTask) {
        // task message
        this.mProgressTextLabel
                .textProperty()
                // Concat between "task message" + "task percentage" + "%"
                .bind(Bindings.concat(
                        readGameFileTask.messageProperty(),
                        Bindings.format(
                                " (Progress: %.0f",
                                Bindings.multiply(
                                        readGameFileTask.progressProperty(),
                                        100)),
                        " %)")
                );
        //.bind(readGameFileTask.messageProperty());

        // task progress bar
        this.mProgressBar.progressProperty().bind(readGameFileTask.progressProperty());
    }

    private void onReadGameFileFinish() {
        Platform.runLater(this::updateUIAfterGameFileRead);
    }

    private void updateUIAfterGameFileRead() {
        this.mBottomProgressPane.setVisible(false);
        setGameEnabled();
        this.mBoardController = new BoardController(GameSettings.getInstance().getRows(), GameSettings.getInstance().getColumns(), this);
        this.mBoardController.InitBoard();
        this.mBorderPane.setMaxSize(300, 300);
        this.mBorderPane.setCenter(this.mBoardController.getBoardPane());
        this.initImageManagerWithPlayerImages();
    }

    @FXML
    void startGame() throws Exception {
        setGameEnabled();
        this.mBoardController.getBoardPane().setDisable(false);
        this.mBoardController.ResetBoard();
        this.mLogic.StartGame();
//        this.mGameDetailsController.setDelegate(this); TODO: figure out why this is null when we start game.
        initDetails();
    }

    public void setOnAction() {
        this.mBtnLoadFile.setOnAction(e -> loadFile());
        this.mBtnStartGame.setOnAction(e -> {
            try {
                startGame();
            } catch (Exception e1) {
                // This method throws an exception that should'nt occure. If it does, then theres something wrong with the computer player algo.
                e1.printStackTrace();
            }
        });
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

        if (result.get() == ButtonType.OK){
            //TODO: remove dummy player quit, uncomment exit game logic.
            //this.mLogic.exitGame();
            //clear();
            PlayTurnParameters params = new PlayTurnParameters(eTurnType.PlayerQuit);
            this.mLogic.playTurnAsync(params);
            setGameDisabled();
        }
    }

    private void initImageManagerWithPlayerImages() {
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

    // Replay

    private void initReplay() {
        // On clicks
        this.mReplayButton.setOnMouseClicked(this::onReplayButtonClick);
        this.mBackReplayButton.setOnMouseClicked(this::onBackReplayButtonClick);
        this.mForwardReplayButton.setOnMouseClicked(this::onForwardReplayButtonClick);

        // Bind buttons disable property.
        //TODO: bind ReplayButton's disableProperty to a new boolean property isGameActiveProperty.
        //TODO: bind replayButtons text property to the boolean property mIsReplayInProgress:
        //this.mReplayButton.textProperty().bind(Bindings.selectBoolean());
        this.mBackReplayButton.disableProperty().bind(this.mIsReplayInProgressProperty);
        this.mForwardReplayButton.disableProperty().bind(this.mIsReplayInProgressProperty);

        //TODO: remove temp adding buttons manually
        this.mGridPaneConfig.add(this.mBackReplayButton, 4, 0);
        this.mGridPaneConfig.add(this.mReplayButton, 5, 0);
        this.mGridPaneConfig.add(this.mForwardReplayButton, 6, 0);
    }

    private void onReplayButtonClick(MouseEvent mouseEvent) {
        // Toggle boolean property
        this.mIsReplayInProgressProperty.setValue(!this.mIsReplayInProgressProperty.getValue());
    }

    private void onBackReplayButtonClick(MouseEvent mouseEvent) {
        if(this.mReplayManager.hasPrevious()) {

        }
    }

    private void onForwardReplayButtonClick(MouseEvent mouseEvent) {

    }

    // ILogicDelegate Implementation

    @Override
    public void onTurnPlayedSuccess(PlayedTurnData playedTurnData) {
        Platform.runLater(
                () -> this.handleUIAfterPlayedTurns(playedTurnData)
        );
    }

    @Override
    public void discAddedToFullColumn(int column) {
        try {
            this.mBoardController.handelInvalidAction(column, eInvalidMoveType.ColumnFull); //notify user
        } catch (InterruptedException e) {
            System.out.println("SleepError. "+ e.getMessage());
        }
    }

    @Override
    public void currentPlayerCannotPopoutAtColumn(int column) {
        try {
            this.mBoardController.handelInvalidAction(column, eInvalidMoveType.InvalidPopout);
        } catch (InterruptedException e) {
            System.out.println("SleepError. "+ e.getMessage());
        }
    }

    @Override
    public void turnInProgress() {
        // Turn is in progress, human player cannot make any moves.
        this.mIsTurnInProgress = true;
    }

    @Override
    public void finishedTurn() {
        // Notify (in the UI thread) that there's no longer a turn in progress.
        Platform.runLater(
                () -> this.mIsTurnInProgress = false
        );
    }

    // IBoardControllerDelegate implementation

    @Override
    public void PopoutBtnClicked(int btnIndex) {
        try {
            PlayTurnParameters playTurnParameters = new PlayTurnParameters(btnIndex, eTurnType.Popout);
            this.playTurn(playTurnParameters);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println(e.getMessage());
            //TODO: catch all of the exceptions and handle them.
        }
    }

    @Override
    public void ColumnClicked(int columnIndex) {
        try {
            PlayTurnParameters playTurnParameters = new PlayTurnParameters(columnIndex, eTurnType.AddDisc);
            this.playTurn(playTurnParameters);
        } catch (Exception e) {
            e.printStackTrace();
            //TODO: catch all of the exceptions and handle them.
        }
    }

    private void playTurn(PlayTurnParameters playTurnParameters)  {
        if(!this.mIsTurnInProgress) {
            this.mLogic.playTurnAsync(playTurnParameters);
        } else {
            // Notify user that the computer is making his turn.
        }
    }

    private void handleUIAfterPlayedTurns(PlayedTurnData turnData) {
        if(turnData.getUpdatedCellsCollection() != null) {
            // Update board if needed.
            this.mBoardController.UpdateDiscs(turnData.getUpdatedCellsCollection());
            this.mBoardController.UpdateDiscs(turnData.getUpdatedCellsCollection());
        }

        eGameState gameState = turnData.getGameState();
        if(gameState.equals(eGameState.Won)) {
            Map<Player, Collection<Cell>> playerToWinningSequenceMap = this.mLogic.getPlayerToWinningSequencesMap();
            System.out.println("Game won by players:");
            for(Player p: playerToWinningSequenceMap.keySet()) {
                System.out.println(p.getName());
            }

            this.mBoardController.DisplayWinningSequences(playerToWinningSequenceMap);
            setGameDisabled();
            gameWonMsg(turnData.getPlayer().getName());
            //TODO: notify players that the game has been won. Disable the game and "Reset" logic to a state where there's a file loaded but game hasn't started.
        } else if (gameState.equals(eGameState.Draw)) {
            //TODO: notify players that the game has ended in a draw.
        }

        if(GameSettings.getInstance().getVariant().equals(eVariant.Popout)) {
            // Enable popout buttons that the user is allowed to click.
            List<Integer> availablePopoutColumnSortedList = this.mLogic.getAvailablePopoutColumnsForCurrentPlayer();
            this.mBoardController.DisablePopoutButtonsForColumns(availablePopoutColumnSortedList);
        }
    }

    private void gameWonMsg(String WinnerName) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Game Finish");
        alert.setContentText(WinnerName +" Player won the game! Congratulation.");
        Optional<ButtonType> result = alert.showAndWait();

        if (result.get() == ButtonType.OK){ //user chose ok
            alert.close();
        } 
    }

    private void setGameEnabled() {
        this.mBtnLoadFile.setDisable(false);
        this.mBtnStartGame.setDisable(true);
        this.mBtnExitGame.setDisable(false);
    }

    @Override
    public void ExitGameBtnClicked(boolean doExit) {
        System.out.println("Handle exit game btn clicked"); //DEBUG
        if (doExit){
            clear();
            this.mLogic.exitGame();
        }
    }

    private void setGameDisabled(){
        this.mBtnStartGame.setDisable(true);
        this.mBtnLoadFile.setDisable(false);
        this.mBtnExitGame.setDisable(true);
    }

    private void clear() {
        ImageManager.Clear(); //TODO: check what else
        this.mBoardController.ClearBoard();
        this.mTheame.setAviadTheame();
        setGameDisabled();
    }

    public void changeTheame(){
        this.mStackPane.setStyle(this.mTheame.getCurrentTheameBackground());
    }

    public void onComboBoxItemChange(ActionEvent actionEvent) {
        if (this.mComboBoxTheame.getSelectionModel().getSelectedItem().equals(eTheameType.Aviad)){
            this.mTheame.setAviadTheame();
        }
        else{
            this.mTheame.setGuyTheame();
        }
        changeTheame();
    }
}
