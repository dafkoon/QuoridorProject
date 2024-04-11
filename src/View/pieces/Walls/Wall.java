package View.pieces.Walls;

import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class Wall extends Rectangle {
    private final int row;
    private final int col;
    private boolean pressCommit;

    public Wall(int row, int col) {
        this.row = row;
        this.col = col;
        setFill(Color.SILVER);
        setStrokeWidth(1);
    }

    public void fill() {
        this.setFill(Color.BLACK);
    }

    public void removeFill() {
        this.setFill(Color.SILVER);
    }

    public int getCol() {
        return col;
    }
    public int getRow() {
        return row;
    }
    public void setIsPlaced(boolean pressed) {
        this.pressCommit = pressed;
    }
    public boolean isPlaced() {
        return this.pressCommit;
    }
    public String toAlgebraic() {
//        char row = (char) ('1' + BOARD_DIMENSION-(this.row+1));
        char row = (char) ('0' + this.row);
        char col = (char) ('`' + this.col);
        return ""+col+row;
    }

}
