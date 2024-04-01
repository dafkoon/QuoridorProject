package View.pieces;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

import static Utilities.Constants.*;


/**
 * Represents a tile on the game board.
 * Each tile has a specific column and row position.
 */
public class Tile extends StackPane {
    private final int col;
    private final int row;

    /**
     * Constructs a new tile with the specified column and row position.
     *
     * @param col the column position of the tile
     * @param row the row position of the tile
     */
    public Tile(int col, int row) {
        this.col = col;
        this.row = row;

        // Create a rectangle representing the tile
        Rectangle rectangle = new Rectangle(TILE_SIZE, TILE_SIZE);
        rectangle.setFill(Color.BURLYWOOD); // Set fill color
        rectangle.setStroke(Color.BLACK); // Set border color

        // Create a label for the tile's notation (e.g., "a1", "b2", etc.)
        Label notationLabel = new Label(toString());

        getChildren().addAll(rectangle, notationLabel);
        relocate((col - 1) * TILE_SIZE, (BOARD_DIMENSION - row) * TILE_SIZE);
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

