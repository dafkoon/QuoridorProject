package View.pieces;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import View.Game;
public class Tile extends Rectangle{
    public Tile(int x, int y) {
        setWidth(Game.TILE_SIZE);
        setHeight(Game.TILE_SIZE);
        relocate(x * Game.TILE_SIZE, y *Game.TILE_SIZE);
        setFill(Color.WHITE);

        setStroke(Color.BLACK);
        setStrokeWidth(1.0);
    }
}
