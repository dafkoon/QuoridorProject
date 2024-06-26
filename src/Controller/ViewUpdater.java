package Controller;

import View.GUI;
import View.pieces.Tile;
import View.pieces.Walls.HorizontalWall;
import View.pieces.Walls.VerticalWall;

import java.util.LinkedList;
import java.util.Stack;

import static Utilities.Constants.BOARD_SIZE;
import static Utilities.Constants.TILE_SIZE;

/**
 * Singleton class responsible for updating the view based on game events.
 */
public class ViewUpdater {
    private final GUI view;
    LinkedList<Tile> highlightedTiles = new LinkedList<>();

    /**
     * Constructs a ViewUpdater instance.
     *
     * @param view The Game view to be updated.
     */
    public ViewUpdater(GUI view) {
        this.view = view;
    }


    // These methods are only used from the client side to show/remove walls.

    /**
     * Fills a vertical wall.
     *
     * @param wall1     The first vertical wall.
     */
    public void fillVerticalWall(VerticalWall wall1) {
        int row = wall1.getRow();
        int col = wall1.getCol();
        VerticalWall wall2 = view.findVerticalWall(row - 1, col);
        view.fillWall(wall1, wall2, false);
    }

    /**
     * Removes fill from a vertical wall.
     *
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
     *
     * @param wall1     The first horizontal wall.
     */
    public void fillHorizontalWall(HorizontalWall wall1) {
        int row = wall1.getRow();
        int col = wall1.getCol();
        HorizontalWall wall2 = view.findHorizontalWall(row, col + 1);
        view.fillWall(wall1, wall2, false);
    }

    /**
     * Removes fill from a horizontal wall on the human side.
     *
     * @param wall1 The first horizontal wall.
     */
    public void removeFillHorizontalWall(HorizontalWall wall1) {
        int row = wall1.getRow();
        int col = wall1.getCol();
        HorizontalWall wall2 = view.findHorizontalWall(row, col + 1);
        view.removeFill(wall1, wall2);
    }

    public void showTile(String stringTile) {
        Tile tile = view.findTile(stringTile);
        highlightedTiles.add(tile);
        tile.highlight();
    }

    public void hideTile() {
        for (Tile tile : highlightedTiles)
            tile.hide();
    }

    // These methods are used by both AI class and Client to place walls.

    /**
     * Updates a horizontal wall on the board.
     *
     * @param row        The row of the wall.
     * @param col        The column of the wall.
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
     *
     * @param row        The row of the wall.
     * @param col        The column of the wall.
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
     *
     * @param row        The row of the pawn.
     * @param col        The column of the pawn.
     * @param playerTurn The player's turn.
     */
    public void updatePawnPosition(int row, int col, int playerTurn) {
        if (row == -1 || col == -1) {
            view.updatePawnLocation(playerTurn, -1, -1);
        } else {
            int xLocation = col * TILE_SIZE;
            int yLocation = (BOARD_SIZE - TILE_SIZE) - row * TILE_SIZE;
            view.updatePawnLocation(playerTurn, xLocation, yLocation);
        }
    }

    /**
     * Calls for the view to display a window with the winner's information.
     *
     * @param playerTurn the player whose turn it is that won, also is the id of that player.
     */
    public void setWinner(int playerTurn) {
        view.showWinner(playerTurn);
    }

    public void showMoves(Stack<String> moves) {
        for(int i = 0; i < moves.size(); i+=2) {
            System.out.print(moves.get(i));
            if (i != moves.size() - 1) {
                System.out.print(", ");
            }
        }
        System.out.println();
        for(int j = 1; j < moves.size(); j+=2) {
            System.out.print(moves.get(j));
            if (j != moves.size() - 1) {
                System.out.print(", ");
            }
        }
    }

}
