package View.pieces.Walls;
import static Utilities.Constants.*;

public class HorizontalWall extends Wall {
    public HorizontalWall(int row, int col) {
        super(row, col);
        setWidth(TILE_SIZE);
        setHeight((double) TILE_SIZE / 10);
        relocate((col-1) * TILE_SIZE, (BOARD_DIMENSION-row) * TILE_SIZE);
    }
}

