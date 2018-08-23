package UI.Controllers;

import UI.Controllers.ControllerDelegates.ICellControllerDelegate;
import javafx.beans.property.SimpleObjectProperty;
import javafx.fxml.FXML;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Rectangle;

import static UI.FinalSettings.CELL_SIZE;

public class CellController {

    private StackPane mPane;
    private Rectangle mRecBackColor;
    private ImageView mIVSign;
    private Image mEmptyCellImg;
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
        this.mEmptyCellImg = new Image("/UI/Images/EmptyCell.JPG");
        this.init();
    }

    private void init() {
        //TODO: remove dummy image implementation. If an image is not given then the image view will not show.
        mIVSign = new ImageView(this.mEmptyCellImg);
        mIVSign.setFitHeight(CELL_SIZE);
        mIVSign.setFitWidth(CELL_SIZE);
        setOnAction();

        mPane = new StackPane();
        mPane.getChildren().add(mIVSign);
        mPane.setPrefSize(CELL_SIZE, CELL_SIZE);
    }

    private void setOnAction() {
        DropShadow shadow = new DropShadow();
        mIVSign.setOnMouseClicked(
                e -> setImage(new Image("/UI/Images/playerOne.JPG"))

        );

        mIVSign.setOnMouseEntered(
                e ->   mIVSign.setEffect(shadow)
        );
        mIVSign.setOnMouseExited(
                e -> mIVSign.setEffect(null)
        );

    }

    @FXML
    void CellClicked(MouseEvent event){
        System.out.println("Cell clicked! (" + this.getRow() + ", " + this.getColumn() + ")");
        mDelegate.CellClicked(mRow, mColumn);

        if (this.mIsEmpty) {
            this.mIsEmpty = false;
        }
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

    public void clearCell(){
        this.mIsEmpty = true;
        setImage(this.mEmptyCellImg);
    }


}
