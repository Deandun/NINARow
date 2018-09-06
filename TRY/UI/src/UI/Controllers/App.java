package UI.Controllers;

import Logic.Enums.eGameState;
import Logic.Enums.eTurnType;
import Logic.Enums.eVariant;
import Logic.Interfaces.ILogicDelegate;
import Logic.Logic;
import Logic.Models.*;
import Logic.Models.Cell;
import Tasks.ReadGameFileTask;
import UI.ChangeListeners.TextChangingListeners;
import UI.Controllers.ControllerDelegates.IBoardControllerDelegate;
import UI.FinalSettings;
import UI.Replay.ReplayTurnDataAdapter;
import UI.Theme;
import UI.UIMisc.ImageManager;
import UI.eInvalidMoveType;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.property.*;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import UI.eThemeType;
import javafx.event.ActionEvent;

import java.io.File;
import java.util.*;
import java.util.stream.Collectors;

import static UI.FinalSettings.*;

public class App implements IBoardControllerDelegate, ILogicDelegate {

    @FXML private StackPane mStackPane;
    @FXML private BorderPane mBorderPane;
    @FXML private GridPane mGridPaneConfig;
    @FXML private Button mBtnLoadFile;
    @FXML private Button mBtnStartGame;
    @FXML private Button mBtnExitGame;
    @FXML private VBox mVBoxGameDetails;
    @FXML private Label mLblTurnNumber;
    @FXML private Label mLblTargetSize;
    @FXML private Label mLblVariant;
    @FXML private FlowPane mBottomProgressPane;
    @FXML private Label mProgressTextLabel;
    @FXML private ProgressBar mProgressBar;
    @FXML private ComboBox<eThemeType> mComboBoxTheame;

    //TODO: remove temp replay buttons and work with real fxml buttons.
    private Button mBtnReplay = new Button("Start Replay");
    private Button mBtnBackReplay = new Button("Back");
    private Button mBtnForwardReplay = new Button("Forward");

    private BoardController mBoardController;

    // Game details (left pan)
    private IntegerProperty mCurrentTurnProperty;

    //PlayerDetails (right pane)
    private PlayerDetailsController mPlayerDetailsController;

    private Logic mLogic;

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
    private Theme mTheme;

    public App() {
        this.mLogic = new Logic(this);
        this.mPlayerDetailsController = new PlayerDetailsController();
        this.mCurrentTurnProperty = new SimpleIntegerProperty();
        this.mTheme = new Theme();
        this.mIsTurnInProgress = false;
        this.mRestartTextChangingListener = new TextChangingListeners(FinalSettings.RESTART_BTN_TEXT,
                FinalSettings.START_BTN_TEXT,
                (str) -> this.mBtnStartGame.setText(str)
                );

        this.mReplayTextChangingListener = new TextChangingListeners(FinalSettings.REPLAY_STOP_BTN_TEXT,
                FinalSettings.REPLAY_START_BTN_TEXT,
                (str) -> this.mBtnReplay.setText(str)
        );
    }

    @FXML
    private void initialize() {
        setOnAction();
        this.mBorderPane.setRight(this.mPlayerDetailsController.getRoot());
        this.mBtnExitGame.setGraphic(new ImageView(new Image(getClass().getResourceAsStream("/UI/Images/Exit.JPG"), EXIT_BTN_SIZE, EXIT_BTN_SIZE, true, true)));
        this.mBtnExitGame.setText(null);
        this.mBtnExitGame.setPadding(new Insets(1));
        this.mComboBoxTheame.getItems().addAll(eThemeType.Default, eThemeType.Aviad, eThemeType.Guy);
        initReplay();
        this.mIsAppInInitModeProperty.setValue(true);
        initBinding();
        setDefaultDesign();
    }

    private void setDefaultDesign() {
        this.mVBoxGameDetails.setId("mVBoxGameDetails");
        changeOpacity(LOW_OPACITY);
        this.mBtnExitGame.setId("mBtnExitGame");
        this.mTheme.setDefaultTheme();
        setTheme(eThemeType.Default);
        changeTheme();
    }

    private void initBinding() {
        //disabled binding
        this.mBtnLoadFile.disableProperty().bind(this.mIsGameActiveProperty);
        this.mBtnStartGame.disableProperty().bind(this.mIsAppInInitModeProperty);
        this.mBtnReplay.disableProperty().bind(this.mIsGameActiveProperty.not());
        this.mBtnExitGame.disableProperty().bind(this.mIsGameActiveProperty.not());
        this.mBtnBackReplay.disableProperty().bind(
                Bindings.or(this.mIsReplayInProgressProperty.not(), this.mReplayAdapter.getCurrentTurnNumberInReplayProperty().isEqualTo(0)));

        // Panes visibility bindings.
        this.mVBoxGameDetails.visibleProperty().bind(this.mIsAppInInitModeProperty.not());
        this.mPlayerDetailsController.getRoot().visibleProperty().bind(this.mIsAppInInitModeProperty.not());

        // Text listeners
        this.mIsReplayInProgressProperty.addListener(this.mReplayTextChangingListener);
        this.mIsGameActiveProperty.addListener(this.mRestartTextChangingListener);
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
            this.mBottomProgressPane.setVisible(true);

            ReadGameFileTask readGameFileTask = new ReadGameFileTask(selectedFile.getAbsolutePath(), this.mLogic,
                    this::onReadGameFileFinish, this::onReadGameFileError);
            this.bindTaskToUI(readGameFileTask);
            // TODO: make it so setCenter doesn't "pull" top, left and right panes towards the center.
            new Thread(readGameFileTask).start();
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

        // task progress bar
        this.mProgressBar.progressProperty().bind(readGameFileTask.progressProperty());
    }

    private void onReadGameFileFinish() {
        Platform.runLater(
                () -> {
                    this.updateUIAfterGameFileRead();
                    this.mPlayerDetailsController.setPlayersDetails(GameSettings.getInstance().getPlayers());
                    this.mIsAppInInitModeProperty.setValue(false);
                    this.mIsFileGameLoaded.setValue(true);
                });
    }

    private void onReadGameFileError(String errorDescription) {
        this.showAlert("File error!", errorDescription);
    }

    private void updateUIAfterGameFileRead() {
        this.mBottomProgressPane.setVisible(false);
        this.mBoardController = new BoardController(GameSettings.getInstance().getRows(), GameSettings.getInstance().getColumns(), this);
        this.mBoardController.InitBoard();
        this.mBorderPane.setMaxSize(300, 300);
        this.mBorderPane.setCenter(this.mBoardController.getBoardPane());
        this.initImageManagerWithPlayerImages();
        this.mLblTargetSize.setText("Target: " + Integer.toString(GameSettings.getInstance().getTarget()));
        this.mLblVariant.setText("Variant: " + GameSettings.getInstance().getVariant().name());
        this.mCurrentTurnProperty.setValue(0);
        this.mLblTurnNumber.textProperty().bind(
                Bindings.concat(
                        "Turn number: ",
                        this.mCurrentTurnProperty
                )
        );
    }

    @FXML
    void startGame() {
        if(GameSettings.getInstance().getPlayers().size() > 1) {
            this.mBoardController.getBoardPane().setDisable(false);
            this.mBoardController.ResetBoard();
            setLabelsStyle(LABEL_STYLE_DEFAULT);
            this.mPlayerDetailsController.reset();
            this.mCurrentTurnProperty.setValue(0);
            this.mLogic.StartGame();
        } else {
            this.showAlert("Now hold on just a minute.", "We will not allow you to play alone! The game requires at least 2 players to start");
        }
    }

    public void setOnAction() {
        this.mBtnLoadFile.setOnAction(e -> loadFile());
        this.mBtnStartGame.setOnAction(e -> {
            try {
                this.mIsFileGameLoaded.setValue(false);
                this.mIsGameActiveProperty.setValue(true);
                this.startGame();
                changeOpacity(HIGH_OPACITY);
            } catch (Exception e1) {
                // This method throws an exception that should'nt occure. If it does, then theres something wrong with the computer player algo.
                e1.printStackTrace();
            }
        });

        this.mBtnExitGame.setOnMouseClicked(e -> exitGame());
    }

    private void exitGame() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Exit Game");
        alert.setHeaderText("Exit Game Button pressed");
        alert.setContentText("Are you sure you want to exit game?");
        Optional<ButtonType> result = alert.showAndWait();

        if (result.get() == ButtonType.OK){
            if(this.mIsReplayInProgressProperty.getValue()) {
                // If exited during replay, stop replay.
                this.mIsReplayInProgressProperty.setValue(false);
            }

            this.mIsGameActiveProperty.setValue(false);
            this.mIsAppInInitModeProperty.setValue(true);
            this.mComboBoxTheame.setValue(eThemeType.Default);
            setDefaultDesign();
            this.mLogic.exitGame();
            clear();
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
        this.mBtnReplay.setOnMouseClicked(this::onReplayButtonClick);
        this.mBtnBackReplay.setOnMouseClicked(this::onBackReplayButtonClick);
        this.mBtnForwardReplay.setOnMouseClicked(this::onForwardReplayButtonClick);

        this.mBtnForwardReplay.setDisable(true); // Manually set forward button's disableness
        //TODO: remove temp adding buttons manually
        this.mGridPaneConfig.add(this.mBtnBackReplay, 4, 0);
        this.mGridPaneConfig.add(this.mBtnReplay, 5, 0);
        this.mGridPaneConfig.add(this.mBtnForwardReplay, 6, 0);
    }

    private void onReplayButtonClick(MouseEvent mouseEvent) {
        if(this.mIsReplayInProgressProperty.getValue()) {
            // Replay is in progress - User wants to end replay.
            this.mReplayAdapter.getAllNextTurnsCollection().forEach(this::handleUIAfterPlayedTurns);
            this.mBtnForwardReplay.setDisable(true); // Manually set forward button's disableness
        } else {
            // Replay is not in progress - User wants to start replaying.
            this.mReplayAdapter.start(this.mLogic.GetTurnHistory());
            this.mBoardController.disableAllPopoutButtons();
            // Replay just begun, we start at the final turn - can't go forward in replay.
            this.mBtnForwardReplay.setDisable(true); // Manually set forward button's disableness
        }

        // Toggle boolean property
        this.mIsReplayInProgressProperty.setValue(!this.mIsReplayInProgressProperty.getValue());
    }

    private void onBackReplayButtonClick(MouseEvent mouseEvent) {
        this.mBtnForwardReplay.setDisable(false); // Manually set forward button's disableness
        if(this.mReplayAdapter.hasPrevious()) {
            PlayedTurnData previousTurnData = this.mReplayAdapter.getPreviousTurnData();
            this.handleUIAfterPlayedTurns(previousTurnData, true);
        }
    }

    private void onForwardReplayButtonClick(MouseEvent mouseEvent) {
        PlayedTurnData nextTurnData = this.mReplayAdapter.getNextTurnData();
        this.handleUIAfterPlayedTurns(nextTurnData);

        if(!this.mReplayAdapter.hasNext()) {
            // Button will be disabled after this click.
            this.mBtnForwardReplay.setDisable(true); // Manually set forward button's disableness
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
    public void noTurnInProgress() {
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

        if(this.mLogic.GetGameState().equals(eGameState.Active)) {
            if(!this.mIsTurnInProgress) { // TODO: after game won/draw turn in progress is stuck as true.
                if(!this.mIsReplayInProgressProperty.getValue()) {
                    this.mLogic.playTurnAsync(playTurnParameters);
                } else {
                    // Replay in progress
                    this.showAlert("Oh no you didn't", "Cannot play turn while replay is in progress.");
                }
            } else {
                // Computer player turn in progress.
                // Notify user that the computer is making his turn.
            }
        } else {
            // Game no longer active.
            this.showAlert("Cannot play turn after the game has ended.", "Either restart or move on with your life.");
        }
    }

    private void handleUIAfterPlayedTurns(PlayedTurnData turnData) {
        this.handleUIAfterPlayedTurns(turnData, false);
    }

    private void handleUIAfterPlayedTurns(PlayedTurnData turnData, boolean isReverseTurn) {
        this.handleBoardControllerUIAfterTurn(turnData);
        this.handlePlayerDetailsControllerUIAfterTurn(turnData, isReverseTurn);
        this.handleGameDetailsUIAfterTurn(isReverseTurn);
        //TODO: remove marked border from current player that moved
        if(!this.mIsReplayInProgressProperty.getValue()) {
            this.handleGameStateEvents(turnData.getGameState());
        }
    }

    private void handleGameStateEvents(eGameState gameState) {
        if(gameState.equals(eGameState.Won)) {
            Map<Player, Collection<Cell>> playerToWinningSequenceMap = this.mLogic.getPlayerToWinningSequencesMap();
            this.mBoardController.DisplayWinningSequences(playerToWinningSequenceMap);
            gameWonMsg(playerToWinningSequenceMap.keySet());
        } else if (gameState.equals(eGameState.Draw)) {
            this.showAlert("Draw!", "The game has ended in a draw. Please start a new game or exit.");
        }
    }

    private void handleGameDetailsUIAfterTurn(boolean isReverseTurn) {
        if(isReverseTurn) {
            this.mCurrentTurnProperty.set(this.mCurrentTurnProperty.getValue() - 1);
        } else {
            this.mCurrentTurnProperty.set(this.mCurrentTurnProperty.getValue() + 1);
        }
    }

    private void handlePlayerDetailsControllerUIAfterTurn(PlayedTurnData turnData, boolean isReverseTurn) {
        if(turnData.getTurnType().equals(eTurnType.PlayerQuit)) {
            this.mPlayerDetailsController.playerQuit();
        } else {
            if (isReverseTurn) {
                this.mPlayerDetailsController.updateToPreviousTurn();
            } else {
                this.mPlayerDetailsController.updateToNextTurn();
            }
        }
    }

    private void handleBoardControllerUIAfterTurn(PlayedTurnData turnData) {
        if(turnData.getUpdatedCellsCollection() != null) {
            // Update board if needed.
            this.mBoardController.UpdateDiscs(turnData.getUpdatedCellsCollection());
            this.mBoardController.UpdateDiscs(turnData.getUpdatedCellsCollection());
        }

        boolean shouldEnablePopoutButtons = !this.mIsReplayInProgressProperty.getValue() && GameSettings.getInstance().getVariant().equals(eVariant.Popout);

        if(shouldEnablePopoutButtons) {
            // Enable popout buttons that the user is allowed to click.
            List<Integer> availablePopoutColumnSortedList = this.mLogic.getAvailablePopoutColumnsForCurrentPlayer();
            this.mBoardController.DisablePopoutButtonsForColumns(availablePopoutColumnSortedList);
        }
    }

    private void gameWonMsg(Set<Player> winnersSet) {
        Set<String> winnerNamesSet = winnersSet.stream().map(Player::getName).collect(Collectors.toSet());
        final String namesSeporator = ", ";
        String winnersNames = String.join(namesSeporator, winnerNamesSet);
        String bodyString = "The winners are " + winnersNames + "." + System.lineSeparator() + "Please start a new game or exit.";
        this.showAlert("The game was won!", bodyString);
    }

    private void showAlert(String header, String body) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setHeaderText(header);
        alert.setContentText(body);
        Optional<ButtonType> result = alert.showAndWait();

        if (result.get() == ButtonType.OK){ //user chose ok
            alert.close();
        }
    }

    private void clear() {
        ImageManager.Clear();
        this.mPlayerDetailsController.clear();
        this.mBoardController.ClearBoard();
        this.mTheme.setAviadTheme();
    }

    public void changeTheme(){
        this.mStackPane.setStyle(this.mTheme.getCurrentThemeBackground());
    }

    public void onComboBoxItemChange(ActionEvent actionEvent) {
        if (this.mComboBoxTheame.getSelectionModel().getSelectedItem().equals(eThemeType.Aviad)){
            this.mTheme.setAviadTheme();
            setTheme(eThemeType.Aviad);
        }
        else if (this.mComboBoxTheame.getSelectionModel().getSelectedItem().equals(eThemeType.Guy)){
            this.mTheme.setGuyTheme();
            setTheme(eThemeType.Guy);
        } else{
            this.mTheme.setDefaultTheme();
            setTheme(eThemeType.Default);
        }
        changeTheme();
        if (this.mPlayerDetailsController != null){
            this.mPlayerDetailsController.markCurrentPlayer();
        }

    }

    public void changeOpacity(double opacity){
        this.mVBoxGameDetails.setOpacity(opacity);
        this.mPlayerDetailsController.setOpacity(opacity);
    }

    private void setTheme(eThemeType themeType){
        if (themeType.equals(eThemeType.Aviad)){
            this.mVBoxGameDetails.setStyle(VBOX_STYLE_AVIAD);
            this.mPlayerDetailsController.setTheme(PLAYERS_PANE_STYLE_AVIAD + FONT_AVIAD);
            setButtonsFill(BUTTON_GRADIENT_AVIAD + FONT_AVIAD);
            setLabelsStyle(LABEL_STYLE_AVIAD + FONT_AVIAD);
        }else if (themeType.equals(eThemeType.Guy)){
            this.mVBoxGameDetails.setStyle(VBOX_STYLE_GUY);
            this.mPlayerDetailsController.setTheme(PLAYERS_PANE_STYLE_GUY + FONT_GUY);
            setButtonsFill(BUTTON_GRADIENT_GUY + FONT_GUY);
            setLabelsStyle(LABEL_STYLE_GUY + FONT_GUY);
        }else{
            this.mVBoxGameDetails.setStyle(VBOX_STYLE_DEFAULT);
            this.mPlayerDetailsController.setTheme(PLAYERS_PANE_STYLE_DEFAULT + FONT_DEFAULT);
            setButtonsFill(BUTTON_GRADIENT_DEFAULT + FONT_DEFAULT);
            setLabelsStyle(LABEL_STYLE_DEFAULT + FONT_DEFAULT);
        }
    }

    private void setButtonsFill(String style) {
        this.mBtnStartGame.setStyle(style);
        this.mBtnLoadFile.setStyle(style);
        this.mComboBoxTheame.setStyle(style);
        this.mBtnReplay.setStyle(style);
        this.mBtnForwardReplay.setStyle(style);
        this.mBtnBackReplay.setStyle(style);
    }

    private void setLabelsStyle(String style){
        this.mLblTargetSize.setStyle(style);
        this.mLblTurnNumber.setStyle(style);
        this.mLblVariant.setStyle(style);
        this.mPlayerDetailsController.setLabelsStyle(style);
    }
}
