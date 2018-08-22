package UI.Controllers;

import UI.Controllers.ControllerDelegates.ICellControllerDelegate;
import javafx.beans.property.SimpleObjectProperty;
import javafx.fxml.FXML;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Rectangle;

public class CellController {

    private StackPane mPane;
    private Rectangle mRecBackColor;
    private ImageView mIVSign;

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
        this.init();
    }

    private void init() {
        //TODO: remove dummy image implementation. If an image is not given then the image view will not show.
        Image image = new Image("/UI/Images/1.JPG");
        mIVSign = new ImageView(image);
        mIVSign.setOnMouseClicked(
            e -> System.out.println("Cell clicked!")
        );
        mPane = new StackPane();
        mPane.getChildren().add(mIVSign);
    }

    @FXML
    void CellClicked(MouseEvent event){
        this.mIsEmpty = false;
        System.out.println("Cell clicked!");
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
