package UI.Controllers;

import Logic.Enums.ePlayerType;
import Logic.Enums.eVariant;
import Logic.Models.Cell;
import Logic.Models.GameSettings;
import Logic.Models.Player;
import UI.Controllers.ControllerDelegates.ICellControllerDelegate;
import UI.UIMisc.ImageManager;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import UI.Controllers.ControllerDelegates.IBoardControllerDelegate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import static UI.FinalSettings.POPOUT_BTN_SIZE;

public class BoardController implements ICellControllerDelegate {

    private List<CellController> mCellControllerList;
    private List<Button> mPopOutButtonList;
    private GridPane mBoardPane;
    private int mNumColumns;
    private int mNumOfDiscRows;
    private int mPopoutRowIndex;
    private IBoardControllerDelegate mDelegate;
    private boolean mIsPopoutEnabled;

    public BoardController(int numRows, int numCols, IBoardControllerDelegate delegate) {
        this.mBoardPane = new GridPane();
        this.mBoardPane.setDisable(true);
        this.mPopOutButtonList = new ArrayList<>();
        this.mNumColumns = numCols;
        this.mNumOfDiscRows = numRows;
        this.mCellControllerList = new ArrayList<>();
        this.mPopoutRowIndex = this.mNumOfDiscRows;
        this.mIsPopoutEnabled = GameSettings.getInstance().getVariant().equals(eVariant.Popout);
        this.mDelegate = delegate;
    }

    public GridPane getBoardPane() {
        return mBoardPane;
    }

    public void InitBoard() {
        int i;

        for (i = 0; i < this.mNumColumns; i++) { //build board
            for (int j = 0; j < this.mNumOfDiscRows; j++) {
                CellController cell = new CellController(i, j, this);
                this.mCellControllerList.add(cell);
                this.mBoardPane.add(cell.getPane(), i, j);
            }
        }

        if(mIsPopoutEnabled) {
            initPopoutButtons();
        }
    }

    private void initPopoutButtons() {
        for (int i = 0; i < this.mNumColumns; i++) { //set popout list
            Button popoutBtn = new Button();
            Image img = new Image(getClass().getResourceAsStream("/UI/Images/popoutArrow.JPG"), POPOUT_BTN_SIZE, POPOUT_BTN_SIZE, true, true);
            popoutBtn.setGraphic(new ImageView(img));
            popoutBtn.setPrefSize(POPOUT_BTN_SIZE, POPOUT_BTN_SIZE);
            popoutBtn.setPadding(new Insets(1));
            popoutBtn.setDisable(true);

            this.mPopOutButtonList.add(popoutBtn);
            this.mBoardPane.add(popoutBtn, i, this.mPopoutRowIndex);
            setPopoutActions(popoutBtn);
        }
    }

    public void InsertDiscstAt(Collection<Cell> updatedCellsCollection) {
        // Insert discs one by one.
        for(Cell updatedCell: updatedCellsCollection) {
            CellController selectedCell = this.mCellControllerList
                    .stream()
                    .filter(
                            cell -> cell.getRow() == updatedCell.getRowIndex() && cell.getColumn() == updatedCell.getColumnIndex()
                    )
                    .findFirst()
                    .get(); // There has to be a cell that answers the predicate above, so there is no need to check ifPresent().

            if(updatedCell.getPlayer().getType().equals(ePlayerType.Computer)) {
                try {
                    Thread.sleep(500); // Sleep for 0.5s before showing computer player's play.
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            selectedCell.setImage(ImageManager.getImageForPlayerID(updatedCell.getPlayer().getID()));
        }
    }

    private void setPopoutActions(Button popoutBtn) {
        DropShadow shadow = new DropShadow();

        popoutBtn.setOnAction(
                e -> {
                    int btnColumnInd = this.getColumnIndexForPopoutBtn(popoutBtn);
                    mDelegate.PopoutBtnClicked(btnColumnInd);
                }
        );
        popoutBtn.setOnMouseEntered(
                e -> popoutBtn.setEffect(shadow)
        );
        popoutBtn.setOnMouseExited(
                e -> popoutBtn.setEffect(null)
        );
    }

    private int getColumnIndexForPopoutBtn(Button btn) {
        return mPopOutButtonList.indexOf(btn);
    }

    @Override
    public void CellClicked(int row, int column) {
        this.mDelegate.ColumnClicked(column);
    }

    public void DisplayWinningSequences(Map<Player, Collection<Cell>> playerToWinningSequenceMap) {
        //TODO: Add an effect to the winning sequence cells! Do it by adding a style class to the buttons and adding the class and effect to the css file.
    }

    public void DisablePopoutButtonsForColumns(List<Integer> columnsToEnableSortedList) {
        // First, disable all buttons.
        mPopOutButtonList.forEach(
                btn -> btn.setDisable(true)
        );

        // Enable buttons whose indexes are in the list.
        columnsToEnableSortedList.forEach(
                index -> mPopOutButtonList.get(index).setDisable(false)
        );
    }

    public void ResetBoard() {
        this.mCellControllerList.stream().forEach(
                cellController -> cellController.setImage(ImageManager.getEmptyDiscSlotImage())
        );
    }
}

