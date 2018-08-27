package UI.Controllers;
import Logic.Enums.eGameState;
import Logic.Enums.ePlayerType;
import Logic.Enums.eTurnType;
import Logic.Enums.eVariant;
import Logic.Logic;
import Logic.Models.*;
import Logic.Models.Cell;
import Tasks.PlayComputerPlayerTask;
import Tasks.ReadGameFileTask;
import UI.Controllers.ControllerDelegates.IBoardControllerDelegate;
import UI.Controllers.ControllerDelegates.IGameSettingsControllerDelegate;
import UI.Theame;
import UI.UIMisc.ImageManager;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import UI.eTheameType;

import javafx.event.ActionEvent;
import java.io.File;
import java.util.*;
import java.util.function.Consumer;

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
    @FXML private FlowPane mBottomProgressPane;
    @FXML private Label mProgressTextLabel;
    @FXML private ProgressBar mProgressBar;
    @FXML private ComboBox<eTheameType> mComboBoxTheame;

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

    // Computer player.
    private boolean mIsComputerPlayerTurnInProgress; // A flag that states that the computer players turns are in progress (user cannot play at this time).
    private Runnable mOnAllComputerPlayersFinishedTurns = () -> mIsComputerPlayerTurnInProgress = false; // Callback for when all computer players finished their turns.
    private Consumer<PlayedTurnData> mOnSingleComputerPlayerFinishedTurn = // Callback for when a single computer player finished its turns.
            turnData ->
                Platform.runLater( () -> this.handleUIAfterPlayedTurns(turnData)); // Update UI after turn has been played in UI thread.

    private Theame mTheame;

    public App() {
        this.mLogic = new Logic();
        this.mCurrentPlayer = new SimpleStringProperty();
        this.mTurnNumber = new SimpleStringProperty();
        this.mVariant = new SimpleStringProperty();
        this.mTargetSize = new SimpleStringProperty();
        this.mTheame = new Theame();
        this.mTheame.setAviadTheame();
    }

    private boolean shouldPlayComputerPlayerNext() {
        return this.mLogic.GetCurrentPlayer().getType().equals(ePlayerType.Computer);
    }

    private void playComputerPlayersTurnsIfNeeded() {
        if(this.shouldPlayComputerPlayerNext()) {
            mIsComputerPlayerTurnInProgress = true;
            PlayComputerPlayerTask playComputerPlayerTask = new PlayComputerPlayerTask(this.mLogic, mOnAllComputerPlayersFinishedTurns, mOnSingleComputerPlayerFinishedTurn);
            new Thread(playComputerPlayerTask).start();
        }
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
        this.mComboBoxTheame.getItems().addAll(eTheameType.AviadCohen, eTheameType.GuyRonen);
        changeTheame();
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
        this.mBtnStartGame.setDisable(false);
        this.mBoardController = new BoardController(GameSettings.getInstance().getRows(), GameSettings.getInstance().getColumns(), this);
        this.mBoardController.InitBoard();
        this.mBorderPane.setMaxSize(300, 300);
        this.mBorderPane.setCenter(this.mBoardController.getBoardPane());
        this.initImageManagerWithPlayerImages();
    }

    @FXML
    void startGame() throws Exception {
        this.mBtnLoadFile.setDisable(true); // TODO: do we really need to disable load file? I think we need to support loading a new file during a game. Need to check exercise
        this.mBoardController.getBoardPane().setDisable(false);
        this.mBoardController.ResetBoard();
        this.mLogic.StartGame();
//        this.mGameDetailsController.setDelegate(this); TODO: figure out why this is null when we start game.
        initDetails();
        this.playComputerPlayersTurnsIfNeeded();

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

    private void playTurn(PlayTurnParameters playTurnParameters) throws Exception {
        if(!mIsComputerPlayerTurnInProgress) {
            PlayedTurnData playedTurnData = this.mLogic.PlayTurn(playTurnParameters);
            this.handleUIAfterPlayedTurns(playedTurnData);
            this.playComputerPlayersTurnsIfNeeded();
        }
    }

    private void handleUIAfterPlayedTurns(PlayedTurnData turnData) {
        if (turnData.getTurnType().equals(eTurnType.Popout)) {
            this.mBoardController.PopoutDisc(turnData.getUpdatedCellsCollection());
        } else {
            this.mBoardController.InsertDiscstAt(turnData.getUpdatedCellsCollection());
        }
        eGameState gameState = turnData.getGameState();

        if(gameState.equals(eGameState.Won)) {
            Map<Player, Collection<Cell>> playerToWinningSequenceMap = this.mLogic.getPlayerToWinningSequencesMap();

            this.mBoardController.DisplayWinningSequences(playerToWinningSequenceMap);

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

    @Override
    public void ExitGameBtnClicked(boolean doExit) {
        System.out.println("Handle exit game btn clicked"); //DEBUG
        if (doExit){
            clear();
            this.mLogic.exitGame();
        }
    }

    private void setStartConfiguration(){
        this.mBtnStartGame.setDisable(true);
        this.mBtnLoadFile.setDisable(false);
    }

    private void clear() {
        ImageManager.Clear(); //TODO: check what else
        this.mBoardController.ClearBoard();
        this.mTheame.setAviadTheame();
        setStartConfiguration();
    }

    public void changeTheame(){
        this.mStackPane.setStyle(this.mTheame.getCurrentTheameBackground());
    }

    public void onComboBoxItemChange(ActionEvent actionEvent) {
        if (this.mComboBoxTheame.getSelectionModel().getSelectedItem().equals(eTheameType.AviadCohen)){
            this.mTheame.setAviadTheame();
        }
        else{
            this.mTheame.setGuyTheame();
        }
        changeTheame();
    }
}
