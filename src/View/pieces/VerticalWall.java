package View.pieces;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import View.Game;
import static Controller.Controller.TILE_SIZE;

public class VerticalWall extends Rectangle{
    public VerticalWall(int x, int y)
    {
        setWidth(((double) TILE_SIZE / 10));
        setHeight(((double) TILE_SIZE / 5) + 40);
        relocate((x * TILE_SIZE) + 45, y * TILE_SIZE);
        setFill(Color.SILVER);
        setStrokeWidth(0.1);

    }
}
