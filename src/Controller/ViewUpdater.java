package Controller;

import View.GUI;
import View.pieces.Tile;
import View.pieces.Walls.HorizontalWall;
import View.pieces.Walls.VerticalWall;


import java.util.LinkedList;

import static Utilities.Constants.BOARD_SIZE;
import static Utilities.Constants.TILE_SIZE;

/**
 * Singleton class responsible for updating the view based on game events.
 */
public class ViewUpdater {
    private static ViewUpdater instance;
    private final GUI view;
    LinkedList<Tile> tiles = new LinkedList<>();

    /**
     * Constructs a ViewUpdater instance.
     * @param view The Game view to be updated.
     */
    public ViewUpdater(GUI view) {
        this.view = view;
    }


    /**
     * Gets the singleton instance of ViewUpdater.
     * @param view The Game view.
     * @return The ViewUpdater instance.
     */
    public static ViewUpdater getInstance(GUI view) {
        if(instance == null)
            instance = new ViewUpdater(view);
        return instance;
    }

    // These methods are only used from the client side to show/remove walls.

    /**
     * Fills a vertical wall.
     * @param wall1 The first vertical wall.
     * @param isPressed Indicates if the wall is being pressed.
     */
    public void fillVerticalWall(VerticalWall wall1, boolean isPressed) {
        int row = wall1.getRow();
        int col = wall1.getCol();
        VerticalWall wall2 = view.findVerticalWall(row - 1, col);
        view.fillWall(wall1, wall2, isPressed);
    }

    /**
     * Removes fill from a vertical wall.
     * @param wall1 The first vertical wall.
     */
    public void removeFillVerticalWall(VerticalWall wall1) {
        int row = wall1.getRow();
        int col = wall1.getCol();
        VerticalWall wall2 = view.findVerticalWall(row - 1, col);
        view.removeFill(wall1, wall2);
    }

    /**
     * Fills a horizontal wall on the human side.
     * @param wall1 The first horizontal wall.
     * @param isPressed Indicates if the wall is being pressed.
     */
    public void fillHorizontalWall(HorizontalWall wall1, boolean isPressed) {
        int row = wall1.getRow();
        int col = wall1.getCol();
        HorizontalWall wall2 = view.findHorizontalWall(row, col+1);
        view.fillWall(wall1, wall2, isPressed);
    }

    /**
     * Removes fill from a horizontal wall on the human side.
     * @param wall1 The first horizontal wall.
     */
    public void removeFillHorizontalWall(HorizontalWall wall1) {
        int row = wall1.getRow();
        int col = wall1.getCol();
        HorizontalWall wall2 = view.findHorizontalWall(row, col+1);
        view.removeFill(wall1, wall2);
    }

    public void showTile(String stringTile) {
        Tile tile = view.findTile(stringTile);
        tiles.add(tile);
        tile.highlight();
    }

    public void hideTile() {
        for(Tile tile : tiles) {
            tile.hide();
        }
    }

    // These methods are used by both AI class and Client to place walls.

    /**
     * Updates a horizontal wall on the board.
     * @param row The row of the wall.
     * @param col The column of the wall.
     * @param playerTurn The player's turn.
     */
    public void placeHorizontalWalls(int row, int col, int playerTurn) {
        HorizontalWall wall1 = view.findHorizontalWall(row, col);
        HorizontalWall wall2 = view.findHorizontalWall(row, col + 1);
        view.fillWall(wall1, wall2, true);
        view.updateInfoPanel(playerTurn);
    }

    /**
     * Updates a vertical wall on the board.
     * @param row The row of the wall.
     * @param col The column of the wall.
     * @param playerTurn The player's turn.
     */
    public void placeVerticalWall(int row, int col, int playerTurn) {
        VerticalWall wall1 = view.findVerticalWall(row, col);
        VerticalWall wall2 = view.findVerticalWall(row - 1, col);
        view.fillWall(wall1, wall2, true);
        view.updateInfoPanel(playerTurn);
    }

    /**
     * Updates the position of a pawn.
     * @param row The row of the pawn.
     * @param col The column of the pawn.
     * @param playerTurn The player's turn.
     */
    public void updatePawnPosition(int row, int col, int playerTurn) {
        if(row == -1 || col == -1) {
            view.updatePawnLocation(playerTurn, -1, -1);
        } else
            view.updatePawnLocation(playerTurn, col*TILE_SIZE, (BOARD_SIZE-TILE_SIZE)-row*TILE_SIZE);
    }

    public void setWinner(int playerTurn) {
        view.showWinner(playerTurn);
    }
}
