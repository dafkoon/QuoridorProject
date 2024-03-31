package Controller;

import View.Game;
import View.pieces.HorizontalWall;
import View.pieces.VerticalWall;

import static Utilities.Constants.*;

public class ViewUpdater {
    private static ViewUpdater instance;
    private final Game view;
    private ViewUpdater(Game view) {
        this.view = view;
    }

    public static ViewUpdater getInstance(Game view) {
        if(instance == null)
            instance = new ViewUpdater(view);
        return instance;
    }
    // These are for human side
    public void fillVerticalWall(VerticalWall wall1, boolean isPressed) {
        int row = wall1.getRow();
        int col = wall1.getCol();
        VerticalWall wall2 = view.findVerticalWallObject(row - 1, col);
        view.fillVerticalWall(wall1, wall2, isPressed);
    }
    public void removeFillVerticalWall(VerticalWall wall1) {
        int row = wall1.getRow();
        int col = wall1.getCol();
        VerticalWall wall2 = view.findVerticalWallObject(row - 1, col);
        view.removeFillVerticalWall(wall1, wall2);
    }
    public void fillHorizontalWall(HorizontalWall wall1, boolean isPressed) {
        int row = wall1.getRow();
        int col = wall1.getCol();
        HorizontalWall wall2 = view.findHorizontalWallObject(row, col+1);
        view.fillHorizontalWall(wall1, wall2, isPressed);
    }
    public void removeFillHorizontalWall(HorizontalWall wall1) {
        int row = wall1.getRow();
        int col = wall1.getCol();
        HorizontalWall wall2 = view.findHorizontalWallObject(row, col+1);
        view.removeFillHorizontalWall(wall1, wall2);
    }

    // These are for both
    public void updateHorizontalWall(int row, int col, int playerTurn) {
        HorizontalWall wall1 = view.findHorizontalWallObject(row, col);
        HorizontalWall wall2 = view.findHorizontalWallObject(row, col + 1);
        view.fillHorizontalWall(wall1, wall2, true);
        view.updateInfoPanel(playerTurn);
    }
    public void updateVerticalWall(int row, int col, int playerTurn) {
        VerticalWall wall1 = view.findVerticalWallObject(row, col);
        VerticalWall wall2 = view.findVerticalWallObject(row - 1, col);
        view.fillVerticalWall(wall1, wall2, true);
        view.updateInfoPanel(playerTurn);
    }
    public void updatePawnPosition(int playerTurn, int row, int col) {
        if(row == -1 || col == -1) {
            view.updatePawnLocation(playerTurn, -1, -1);
        } else
            view.updatePawnLocation(playerTurn, col*TILE_SIZE, (BOARD_SIZE-TILE_SIZE)-row*TILE_SIZE);
    }
}
