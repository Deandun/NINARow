package UI.Controllers;

import Logic.Models.Player;
import UI.Controllers.ControllerDelegates.ICellControllerDelegate;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import UI.Controllers.ControllerDelegates.IBoardControllerDelegate;
import java.util.ArrayList;
import java.util.List;
import static UI.FinalSettings.POPOUT_BTN_SIZE;


public class BoardController implements ICellControllerDelegate {

    private List<CellController> mCellControllerList;
    private List<Button> mPopOutList;
    private GridPane mBoardPane;
    private int mNumColumns;
    private int mNumOfDiscRows;
    private int mPopoutRowIndex;
    private IBoardControllerDelegate mDelegate;
    private boolean mIsEnabled;

    public BoardController(int numRows, int numCols, IBoardControllerDelegate delegate) {
        this.mBoardPane = new GridPane();
        this.mPopOutList = new ArrayList<>();
        this.mNumColumns = numCols;
        this.mNumOfDiscRows = numRows;
        this.mCellControllerList = new ArrayList<>();
        this.mPopoutRowIndex = this.mNumOfDiscRows;
        this.mIsEnabled = false;
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

        for (i = 0; i < this.mNumColumns; i++) { //set popout list
            Button popoutBtn = new Button();
            Image img = new Image(getClass().getResourceAsStream("/UI/Images/popoutArrow.JPG"), POPOUT_BTN_SIZE, POPOUT_BTN_SIZE, true, true);
           // popoutBtn.setDisable(true);
            popoutBtn.setGraphic(new ImageView(img));
            popoutBtn.setPrefSize(POPOUT_BTN_SIZE, POPOUT_BTN_SIZE);
            popoutBtn.setPadding(new Insets(1));

            this.mPopOutList.add(popoutBtn);
            this.mBoardPane.add(popoutBtn, i, this.mPopoutRowIndex);
            setPopoutActions(popoutBtn);
        }
    }

    private void setPopoutActions(Button popoutBtn) {
        DropShadow shadow = new DropShadow();

        popoutBtn.setOnAction(
                e -> {
                    int btnColumnInd = getColumnIndexForPopoutBtn(popoutBtn);
                    if (this.mIsEnabled){
                        mDelegate.PopoutBtnClicked(btnColumnInd);
                    }
                }
        );
        popoutBtn.setOnMouseEntered(
                e ->   popoutBtn.setEffect(shadow)
        );
        popoutBtn.setOnMouseExited(
                e -> popoutBtn.setEffect(null)
        );
    }

    private int getColumnIndexForPopoutBtn(Button btn) {
        return mPopOutList.indexOf(btn);
    }

    @Override
    public void CellClicked(int column, int row) {

    }

    public boolean isPopoutAviable(Player currPlayer){
        //TODO: need logic to check

        return false;
    }

    public boolean getIsBoardEnabled() {
        return mIsEnabled;
    }

    public void setIsBoardEnabled(boolean isEnabled) {
        this.mIsEnabled = isEnabled;
    }
}

