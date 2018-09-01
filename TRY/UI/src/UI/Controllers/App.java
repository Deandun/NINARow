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
import UI.ChangeListeners.TextChangingListeners;
import UI.Controllers.ControllerDelegates.IBoardControllerDelegate;
import UI.Controllers.ControllerDelegates.IGameSettingsControllerDelegate;
import UI.FinalSettings;
import UI.Replay.ReplayTurnDataAdapter;
import UI.Theame;
import UI.UIMisc.ImageManager;
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
    private Button mReplayButton = new Button("Start Replay");
    private Button mBackReplayButton = new Button("Back");
    private Button mForwardReplayButton = new Button("Forward");

    private BoardController mBoardController;

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
    private ReplayTurnDataAdapter mReplayAdapter = new ReplayTurnDataAdapter();

    //States
    private BooleanProperty mIsAppInInitModeProperty = new SimpleBooleanProperty();
    private BooleanProperty mIsFileGameLoaded = new SimpleBooleanProperty();
    private BooleanProperty mIsGameActiveProperty = new SimpleBooleanProperty();

    // Change listeners
    private TextChangingListeners mReplayTextChangingListener;
    private TextChangingListeners mRestartTextChangingListener;

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
        this.mRestartTextChangingListener = new TextChangingListeners(FinalSettings.RESTART_BTN_TEXT,
                FinalSettings.START_BTN_TEXT,
                (str) -> this.mBtnStartGame.setText(str)
                );

        this.mReplayTextChangingListener = new TextChangingListeners(FinalSettings.REPLAY_STOP_BTN_TEXT,
                FinalSettings.REPLAY_START_BTN_TEXT,
                (str) -> this.mReplayButton.setText(str)
        );
    }

    private boolean shouldPlayComputerPlayerNext() {
        return this.mLogic.GetCurrentPlayer().getType().equals(ePlayerType.Computer);
    }

    @FXML
    private void initialize() {
        setOnAction();
        this.mBtnExitGame.setGraphic(new ImageView(new Image(getClass().getResourceAsStream("/UI/Images/Exit.JPG"), EXIT_BTN_SIZE, EXIT_BTN_SIZE, true, true)));
        this.mBtnExitGame.setText(null);
        this.mBtnExitGame.setPadding(new Insets(1));
        this.mComboBoxTheame.getItems().addAll(eTheameType.Aviad, eTheameType.Guy);
        changeTheame();
        this.initReplay();
        this.mIsAppInInitModeProperty.setValue(true);
        initBinding();
    }

    private void initBinding() {
        //disabled binding
        this.mBtnLoadFile.disableProperty().bind(this.mIsGameActiveProperty);
        this.mBtnStartGame.disableProperty().bind(this.mIsAppInInitModeProperty);
        this.mReplayButton.disableProperty().bind(this.mIsGameActiveProperty.not());
        this.mBtnExitGame.disableProperty().bind(this.mIsGameActiveProperty.not());
        this.mBackReplayButton.disableProperty().bind(this.mReplayAdapter.getCurrentTurnNumberInReplayProperty().isEqualTo(0));

        // Panes visibility bindings.
        this.mVBoxGameDetails.visibleProperty().bind(this.mIsGameActiveProperty);
        this.mFlowPanePlayerDetails.visibleProperty().bind(this.mIsAppInInitModeProperty.not());

        // Text listeners
        this.mIsReplayInProgressProperty.addListener(this.mReplayTextChangingListener);
        this.mIsGameActiveProperty.addListener(this.mRestartTextChangingListener);
        //TODO: fix this.mForwardReplayButton.disableProperty()
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
                this.mIsAppInInitModeProperty.setValue(false);
                this.mIsFileGameLoaded.setValue(true);

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
        this.mBoardController = new BoardController(GameSettings.getInstance().getRows(), GameSettings.getInstance().getColumns(), this);
        this.mBoardController.InitBoard();
        this.mBorderPane.setMaxSize(300, 300);
        this.mBorderPane.setCenter(this.mBoardController.getBoardPane());
        this.initImageManagerWithPlayerImages();
    }

    @FXML
    void startGame() {
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
                this.mIsFileGameLoaded.setValue(false);
                this.mIsGameActiveProperty.setValue(true);
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
        this.mBackReplayButton.disableProperty().bind(this.mIsReplayInProgressProperty.not());
        this.mForwardReplayButton.disableProperty().bind(this.mIsReplayInProgressProperty.not());

        //TODO: remove temp adding buttons manually
        this.mGridPaneConfig.add(this.mBackReplayButton, 4, 0);
        this.mGridPaneConfig.add(this.mReplayButton, 5, 0);
        this.mGridPaneConfig.add(this.mForwardReplayButton, 6, 0);
    }

    private void onReplayButtonClick(MouseEvent mouseEvent) {
        if(this.mIsReplayInProgressProperty.getValue()) {
            // Replay is in progress - User wants to end replay.
            this.mReplayAdapter.getAllNextTurnsCollection().forEach(this::handleUIAfterPlayedTurns);
        } else {
            // Replay is not in progress - User wants to start replaying.
            this.mReplayAdapter.start(this.mLogic.GetTurnHistory());
        }

        // Toggle boolean property
        this.mIsReplayInProgressProperty.setValue(!this.mIsReplayInProgressProperty.getValue());
    }

    private void onBackReplayButtonClick(MouseEvent mouseEvent) {
        if(this.mReplayAdapter.hasPrevious()) {
            PlayedTurnData previousTurnData = this.mReplayAdapter.getPreviousTurnData();
            this.handleUIAfterPlayedTurns(previousTurnData);
        }
    }

    private void onForwardReplayButtonClick(MouseEvent mouseEvent) {
        if(this.mReplayAdapter.hasNext()) {
            PlayedTurnData nextTurnData = this.mReplayAdapter.getNextTurnData();
            this.handleUIAfterPlayedTurns(nextTurnData);
        }
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
        }
    }

    @Override
    public void ColumnClicked(int columnIndex) {
        PlayTurnParameters playTurnParameters = new PlayTurnParameters(columnIndex, eTurnType.AddDisc);
        this.playTurn(playTurnParameters);
}

    private void playTurn(PlayTurnParameters playTurnParameters)  {
        // Cannot play turn if the computer player is in progress or replay is in progress.

        if(!this.mIsTurnInProgress) {
            if(!this.mIsReplayInProgressProperty.getValue()) {
                this.mLogic.playTurnAsync(playTurnParameters);
            } else {
                // Notify user that a replay is in progress.
            }
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
            this.mBoardController.DisplayWinningSequences(playerToWinningSequenceMap);
            setGameDisabled();
            //todo: handle more than one winner
            gameWonMsg(playerToWinningSequenceMap.keySet());
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

    private void gameWonMsg(Set<Player> WinnerName) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Game Finish");
    //        alert.setContentText(for(String name : WinnerName) {name +" Player won the game! Congratulation."});
        Optional<ButtonType> result = alert.showAndWait();

        if (result.get() == ButtonType.OK){ //user chose ok
            alert.close();
        } 
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
        //TODO: bind these!
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
