package UI.Controllers;

import UI.Controllers.ControllerDelegates.ICellControllerDelegate;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;

import static UI.FinalSettings.CELL_SIZE;

public class CellController {

    private StackPane mPane;
    private ImageView mIVSign;
    private Image mEmptyCellImg;
    private ICellControllerDelegate mDelegate;
    private int mColumn;
    private int mRow;

    public CellController(int column, int row, ICellControllerDelegate delegate){
        this.mColumn = column;
        this.mRow = row;
        this.mDelegate = delegate;
        this.mEmptyCellImg = new Image("/UI/Images/EmptyCell.JPG");
        this.init();
    }

    private void init() {
        mIVSign = new ImageView(this.mEmptyCellImg);
        mIVSign.setFitHeight(CELL_SIZE);
        mIVSign.setFitWidth(CELL_SIZE);

        Rectangle clip = new Rectangle(
                mIVSign.getFitWidth(), mIVSign.getFitHeight()
        );
        clip.setArcWidth(40);
        clip.setArcHeight(40);
        mIVSign.setClip(clip);


        setOnAction();

        mPane = new StackPane();
        mPane.getChildren().add(mIVSign);
        mPane.setPrefSize(CELL_SIZE, CELL_SIZE);
    }

    private void setOnAction() {
        DropShadow shadow = new DropShadow();

        mIVSign.setOnMouseClicked(
                e -> this.mDelegate.CellClicked(mRow, mColumn)
        );

        mIVSign.setOnMouseEntered(
                e -> mIVSign.setEffect(shadow)

        );

        mIVSign.setOnMouseExited(
                e -> mIVSign.setEffect(null)
        );
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
}
