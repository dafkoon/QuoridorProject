package View.pieces.Walls;

import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class Wall extends Rectangle {
    private final int row;
    private final int col;
    private boolean pressCommit;

    /**
     * Constructor for a wall.
     * @param row The row of the wall.
     * @param col The column of the wall.
     */
    public Wall(int row, int col) {
        this.row = row;
        this.col = col;
        setFill(Color.SILVER);
        setStrokeWidth(1);
    }

    /**
     * A method to change the color of the wall.
     */
    public void fill() {
        this.setFill(Color.BLACK);
    }

    /**
     * A method to change the color of the wall to its original color.
     */
    public void removeFill() {
        this.setFill(Color.SILVER);
    }

    /**
     * A Getter to get the column of the wall.
     * @return the column of the wall.
     */
    public int getCol() {
        return col;
    }

    /**
     * A Getter to get the row of the wall.
     * @return the row of the wall.
     */
    public int getRow() {
        return row;
    }

    /**
     * A Setter to set the value of the pressCommit data member. Used to tell if a wall was placed on the board or not.
     * @param pressed a value to set the pressCommit to.
     */
    public void setPressCommit(boolean pressed) {
        this.pressCommit = pressed;
    }

    /**
     * A Getter to get the value of the pressCommit data member.
     * @return the value of pressCommit.
     */
    public boolean getPressCommit() {
        return this.pressCommit;
    }

    /**
     * Calculates a string representation of the wall by algebraic nation.
     * @return a string representation of the wall.
     */
    public String toString() {
        char row = (char) ('0' + this.row);
        char col = (char) ('`' + this.col);
        return ""+col+row;
    }

}
