package View.pieces;


import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import View.Game;

/**
 * Represents a horizontal wall component within the board using the {@link Rectangle} shape.
 */
public class HorizontalWall extends Rectangle {
    public HorizontalWall(int x, int y) {
        setWidth(((double) Game.TILE_SIZE / 5) + 40);
        setHeight((double) Game.TILE_SIZE / 10);
        relocate(x * Game.TILE_SIZE, y * Game.TILE_SIZE);
        setFill(Color.SILVER);
    }
}
