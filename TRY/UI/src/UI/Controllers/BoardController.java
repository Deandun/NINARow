package UI.Controllers;

import Logic.Models.Player;
import UI.Controllers.ControllerDelegates.ICellControllerDelegate;
import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;
import java.util.ArrayList;
import java.util.List;
import Logic.Logic;

public class BoardController implements ICellControllerDelegate {

    private List<CellController> mCellControllerList;
    private List<Button> mPopOutList;
    private GridPane mBoardPane;
    private int mNumColumns;
    private int mNumOfDiscRows;
    private int mPopoutRowIndex;
    private boolean mIsBoardFull;
    private Logic mLogic;

    public BoardController(int numRows, int numCols) {
        this.mBoardPane = new GridPane();
        this.mPopOutList = new ArrayList<>();
        this.mNumColumns = numCols;
        this.mNumOfDiscRows = numRows;
        this.mCellControllerList = new ArrayList<>();
        this.mPopoutRowIndex = this.mNumOfDiscRows;
        this.mIsBoardFull = false;
        this.mLogic = new Logic();
    }

    public GridPane getBoardPane() {
        return mBoardPane;
    }

    public void InitBoard() {
        int i;

        for (i = 0; i < this.mNumOfDiscRows; i++) { //build board
            for (int j = 0; j < this.mNumColumns; j++) {
                CellController cell = new CellController(i, j, this);
                this.mCellControllerList.add(cell);
                this.mBoardPane.add(cell.getPane(), i, j);
            }
        }

        for (i = 0; i < this.mNumColumns; i++) { //set popout list
            Button popoutBtn = new Button();
            popoutBtn.setStyle("-fx-background-image: url('/UI/Images/popoutArrow.JPG')");
            popoutBtn.setOnAction(
                    event -> {
                        int btnColumnIndex = getColumnIndexForPopoutBtn((Button) event.getSource());
                        // mDelegate.popoutAtColumn(btnColumnIndex);
                    }
            );
            this.mPopOutList.add(popoutBtn);
            this.mBoardPane.add(popoutBtn, this.mPopoutRowIndex, i);
        }
    }

    private int getColumnIndexForPopoutBtn(Button btn) {
        return mPopOutList.indexOf(btn);
    }

    @Override
    public void CellClicked(int column, int row) {

    }

    public boolean getIsBoardFull() {
        return mIsBoardFull;
    }

    public void setIsBoardFull(boolean mIsBoardFull) {
        this.mIsBoardFull = mIsBoardFull;
    }

    public boolean isPopupAviable(Player currPlayer){
        for (CellController cell: mCellControllerList) {
            if (!cell.getIsEmpty() && cell.getRow() == this.mNumOfDiscRows - 1){ //TODO: check
                //check if the cell is taken by currPlayer
            }
        }

        return false;
    }
}

