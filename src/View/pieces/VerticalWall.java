package View.pieces;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import View.Game;
public class VerticalWall extends Rectangle{
    public VerticalWall(int x, int y)
    {
        setWidth(((double) Game.TILE_SIZE / 10));
        setHeight(((double) Game.TILE_SIZE / 5) + 40);
        relocate((x * Game.TILE_SIZE) + 45, y * Game.TILE_SIZE);
        setFill(Color.SILVER);
    }
}
