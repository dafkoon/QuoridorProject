package View.pieces;

import static Controller.Controller.TILE_SIZE;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import View.Game;

/**
 * Represents a horizontal wall component within the board using the {@link Rectangle} shape.
 */
public class HorizontalWall extends Rectangle {
    public HorizontalWall(int x, int y) {
        setWidth(((double) TILE_SIZE / 5) + 40);
        setHeight((double) TILE_SIZE / 10);
        relocate(x * TILE_SIZE, y * TILE_SIZE);
        setFill(Color.SILVER);
        setStrokeWidth(0.1);
    }
}
