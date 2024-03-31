package View.pieces;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

import static Utilities.Constants.*;


public class Tile extends StackPane {
    private final int col;
    private final int row;

    public Tile(int col, int row) {
        this.col = col;
        this.row = row;
        Rectangle rectangle = new Rectangle(TILE_SIZE, TILE_SIZE);
        rectangle.setFill(Color.BURLYWOOD);
        rectangle.setStroke(Color.BLACK);
        rectangle.setStrokeWidth(1.0);
        Label notationLabel = new Label(toString());
        getChildren().addAll(rectangle, notationLabel);
        relocate((col-1) * TILE_SIZE, (BOARD_DIMENSION-row) * TILE_SIZE);
    }
    public String toString() {
        char row = (char) ('0' + this.row);
        char col = (char) ('`' + this.col);
        return ""+col+row;
    }
}
