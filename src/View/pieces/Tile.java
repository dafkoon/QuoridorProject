package View.pieces;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

import static Controller.Controller.BOARD_DIMENSION;
import static Controller.Controller.TILE_SIZE;
public class Tile extends StackPane {
    private int col;
    private int row;
    private Rectangle rectangle;
    private Label notationLabel;

    public Tile(int col, int row) {
        this.col = col;
        this.row = row;
        rectangle = new Rectangle(TILE_SIZE, TILE_SIZE);
        rectangle.setFill(Color.BURLYWOOD);
        rectangle.setStroke(Color.BLACK);
        rectangle.setStrokeWidth(1.0);
        notationLabel = new Label(notation());
        getChildren().addAll(rectangle, notationLabel);
        relocate(col * TILE_SIZE, (BOARD_DIMENSION-(row+1)) * TILE_SIZE);


    }


    public String notation() {
        char row = (char) ('1' + this.row);
        char col = (char) ('a' + this.col);
        return ""+col+row;
    }
}
