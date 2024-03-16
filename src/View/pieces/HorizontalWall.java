package View.pieces;

import static Controller.HumanInputHandler.TILE_SIZE;
import static Controller.HumanInputHandler.BOARD_DIMENSION;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

/**
 * Represents a horizontal wall component within the board using the {@link Rectangle} shape.
 */
public class HorizontalWall extends Rectangle {
    private int row;
    private int col;
    private boolean pressCommit;
    public HorizontalWall(int row, int col) {
        this.row = row;
        this.col = col;
        setWidth(TILE_SIZE);
        setHeight((double) TILE_SIZE / 10);
        relocate((col-1) * TILE_SIZE, (BOARD_DIMENSION-row) * TILE_SIZE);

        setFill(Color.SILVER);
        setStrokeWidth(1);
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
        char row = (char) ('0' + this.row);
        char col = (char) ('`' + this.col);
        return ""+col+row;
    }

    public String toAlgebraic(int r, int c) {
        char row = (char) ('1' + r);
        char col = (char) ('a' + c);
        return ""+col+row;
    }
}
