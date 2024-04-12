package View.pieces.Walls;

import static Utilities.Constants.BOARD_DIMENSION;
import static Utilities.Constants.TILE_SIZE;

public class VerticalWall extends Wall {

    /**
     * Constructor for a vertical wall.
     * @param row The row of the wall.
     * @param col The column of the wall.
     */
    public VerticalWall(int row, int col) {
        super(row, col);
        setWidth(((double) TILE_SIZE / 10));
        setHeight(TILE_SIZE);
        relocate(col * TILE_SIZE, (BOARD_DIMENSION - row) * TILE_SIZE);
    }

}