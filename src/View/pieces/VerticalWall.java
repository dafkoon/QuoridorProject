package View.pieces;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

import static Controller.Controller.*;

public class VerticalWall extends Rectangle{
    private int row;
    private int col;
    private boolean pressCommit;
    public VerticalWall(int row, int col) {
        this.col = col;
        this.row = row;
        setWidth(((double) TILE_SIZE / 10));
        setHeight(((double) TILE_SIZE / 5) + 40);
        setFill(Color.SILVER);
        setStrokeWidth(0.1);
        relocate(col * TILE_SIZE, (BOARD_DIMENSION-(row+1)) * TILE_SIZE);
        setTranslateX(46);
    }

    public int getCol() {
        return col;
    }
    public int getRow() {
        return row;
    }
    public void setPressCommit(boolean pressed) {
        this.pressCommit = pressed;
    }
    public boolean isPressCommit() {
        return this.pressCommit;
    }

    public String toAlgebraic() {
//        char row = (char) ('1' + BOARD_DIMENSION-(this.row+1));
        char row = (char) ('1' + this.row);
        char col = (char) ('a' + this.col);
        return ""+col+row;
    }

    public String toAlgebraic(int r, int c) {
        char row = (char) ('1' + r);
        char col = (char) ('a' + c);
        return ""+col+row;
    }
}