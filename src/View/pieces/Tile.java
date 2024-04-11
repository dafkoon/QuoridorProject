package View.pieces;

import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

import static Utilities.Constants.BOARD_DIMENSION;
import static Utilities.Constants.TILE_SIZE;


/**
 * Represents a tile on the game board.
 * Each tile has a specific column and row position.
 */
public class Tile extends StackPane {
    private final int col;
    private final int row;
    private Rectangle rectangle;

    public int getCol() {
        return col;
    }

    public int getRow() {
        return row;
    }

    /**
     * Constructs a new tile with the specified column and row position.
     * @param col the column position of the tile
     * @param row the row position of the tile
     */
    public Tile(int row, int col) {
        this.col = col;
        this.row = row;

        // Create a rectangle representing the tile
        this.rectangle = new Rectangle(TILE_SIZE, TILE_SIZE);
        this.rectangle.setFill(Color.BURLYWOOD); // Set fill color
        this.rectangle.setStroke(Color.BLACK); // Set border color

        // Create a label for the tile's notation (e.g., "a1", "b2", etc.)
        Label notationLabel = new Label(toString());

        getChildren().addAll(this.rectangle, notationLabel);
        relocate((col - 1) * TILE_SIZE, (BOARD_DIMENSION - row) * TILE_SIZE);
    }

    public void highlight() {
        this.rectangle.setFill(Color.LIGHTGRAY);
    }

    public void hide() {
        this.rectangle.setFill(Color.BURLYWOOD);
    }

    /**
     * Returns a string representation of the tile based on its column and row position.
     * The string representation consists of the column letter (a-z) and the row number (1-9).
     * @return the string representation of the tile
     */
    @Override
    public String toString() {
        char row = (char) ('0' + this.row);
        char col = (char) ('`' + this.col);
        return "" + col + row;
    }
}

