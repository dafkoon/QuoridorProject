package View.pieces;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import View.Game;
public class Tile extends Rectangle{
<<<<<<< Updated upstream
    public Tile(int x, int y) {
        setWidth(Game.TILE_SIZE);
        setHeight(Game.TILE_SIZE);
        relocate(x * Game.TILE_SIZE, y *Game.TILE_SIZE);
        setFill(Color.WHITE);

        setStroke(Color.BLACK);
        setStrokeWidth(1.0);
=======
    private int row;
    private int col;

    public Tile(int rowIndex, int colIndex) {
        this.row = rowIndex;
        this.col = colIndex;

        setWidth(TILE_SIZE);
        setHeight(TILE_SIZE);
        relocate(colIndex * TILE_SIZE, rowIndex * TILE_SIZE);
        setFill(Color.BURLYWOOD);
        setStroke(Color.BLACK);
        setStrokeWidth(1.0);
    }

    public int getRow() {
        return row;
    }

    public int getCol() {
        return col;
    }

>>>>>>> Stashed changes
    }
}
