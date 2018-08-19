package UI.Controllers;

import UI.Controllers.ControllerDelegates.ICellControllerDelegate;
import javafx.beans.property.SimpleObjectProperty;
import javafx.fxml.FXML;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Rectangle;

public class CellController {

    @FXML private Pane mPane;
    @FXML private Rectangle mRecBackColor;
    @FXML private ImageView mIVSign;

    private ICellControllerDelegate mDelegate;
    private SimpleObjectProperty mRec;
    private int mColumn;
    private int mRow;
    private boolean mIsEmpty;

    public CellController(int column, int row, ICellControllerDelegate delegate){
        this.mColumn = column;
        this.mRow = row;
        this.mDelegate = delegate;
        this.mIsEmpty = true;
    }

    @FXML
    void CellClicked(MouseEvent event){
        this.mIsEmpty = false;
        mDelegate.CellClicked(mRow, mColumn);
    }


    public void setImage(Image image) {
        this.mIVSign.setImage(image);
    }

    public Pane getPane() {
        return mPane;
    }

    public int getColumn() {
        return mColumn;
    }

    public int getRow() {
        return mRow;
    }

    public boolean getIsEmpty(){ return mIsEmpty;}
}
