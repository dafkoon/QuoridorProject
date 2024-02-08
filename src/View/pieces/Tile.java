package View.pieces;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;

import static Controller.Controller.TILE_SIZE;
public class Tile extends Rectangle{
    private int row;
    private int col;

    public Tile(int row, int col) {
        this.row = row;
        this.col = col;

        setWidth(TILE_SIZE);
        setHeight(TILE_SIZE);
        relocate(col * TILE_SIZE, row * TILE_SIZE);
        setFill(Color.BURLYWOOD);
        setStroke(Color.BLACK);
        setStrokeWidth(1.0);

    }

    public int getRow() {
        return row;
    }
    public int getCol() {
        return col;
    }
    }
