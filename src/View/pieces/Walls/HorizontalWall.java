package View.pieces.Walls;

import static Utilities.Constants.BOARD_DIMENSION;
import static Utilities.Constants.TILE_SIZE;

public class HorizontalWall extends Wall {

    /**
     * Constructor for a horizontal wall.
     *
     * @param row The row of the wall.
     * @param col The column of the wall.
     */
    public HorizontalWall(int row, int col) {
        super(row, col);
        setWidth(TILE_SIZE);
        setHeight((double) TILE_SIZE / 10);
        relocate((col - 1) * TILE_SIZE, (BOARD_DIMENSION - row) * TILE_SIZE);
    }
}

