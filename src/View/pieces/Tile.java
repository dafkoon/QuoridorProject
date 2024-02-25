package View.pieces;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

import static Controller.Controller.BOARD_DIMENSION;
import static Controller.Controller.TILE_SIZE;
public class Tile extends Rectangle{
    private int row;
    private int col;

    public Tile(int col, int row) {
        this.col = col;
        this.row = BOARD_DIMENSION - (row+1); // number

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
