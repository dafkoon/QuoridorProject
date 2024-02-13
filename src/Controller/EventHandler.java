package Controller;
import Model.Gamestate.Wall;
import View.pieces.HorizontalWall;
import View.pieces.Pawn;
import View.Game;
import Model.Gamestate.Square;

import Model.Gamestate.GameSession;
import View.pieces.VerticalWall;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;


import static Controller.Controller.BOARD_DIMENSION;
import static Controller.Controller.TILE_SIZE;

public class EventHandler {
    private final Game view;
    private final GameSession gameSession;
    private final int BOARD_SIZE = BOARD_DIMENSION*TILE_SIZE;

    public EventHandler(GameSession gameSession, Game view) {
        this.gameSession = gameSession;
        this.view = view;
    }

    public void handlePawnMovement(MouseEvent event, Pawn pawn) {
        if(gameSession.currentTurn() == pawn.getType().ordinal()){
            switch(event.getEventType().getName()) {
                case "MOUSE_PRESSED":
                    pawnMousePressed(event, pawn);
                    break;
                case "MOUSE_DRAGGED":
                    pawnMouseDragged(event, pawn);
                    break;
                case "MOUSE_RELEASED":
                    pawnMouseReleased(event, pawn);
                    break;
            }
        }
    }
    public void handleVerticalWallMovement(MouseEvent event, VerticalWall wall) {
        switch(event.getEventType().getName()) {
            case "MOUSE_ENTERED":
                verticalWallEntered(event, wall);
                break;
            case "MOUSE_PRESSED":
                verticalWallPressed(event, wall);
                break;
            case "MOUSE_EXITED":
                verticalWallExited(event, wall);
                break;
        }
    }
    public void handleHorizontalWallMovement(MouseEvent event, HorizontalWall wall) {
        switch(event.getEventType().getName()) {
            case "MOUSE_ENTERED":
                horizontalWallEntered(event, wall);
                break;
            case "MOUSE_PRESSED":
                horizontalWallPressed(event, wall);
                break;
            case "MOUSE_EXITED":
                horizontalWallExited(event, wall);
                break;

        }
    }

    public void pawnMousePressed(MouseEvent event, Pawn pawn) {
        pawn.mouseX = event.getSceneX();
        pawn.mouseY = event.getSceneY();
    }
    public void pawnMouseDragged(MouseEvent event, Pawn pawn) {
        if(pawn.getType() == Pawn.PawnType.HUMAN) {
            // e.getSceneX()-mouseX continually calculates horizontal distance mouse has moved since last update.
            // getLayoutX current X coordinate of the node within its parent's coordinate system.
            pawn.relocate(pawn.getLayoutX() + (event.getSceneX()-pawn.mouseX), pawn.getLayoutY() + (event.getSceneY() - pawn.mouseY));
            pawn.mouseX = event.getSceneX();
            pawn.mouseY = event.getSceneY();
        }
    }
    public void pawnMouseReleased(MouseEvent event, Pawn pawn) {
        double xPixel = pawn.getLayoutX();
        double yPixel = (BOARD_SIZE-TILE_SIZE) - pawn.getLayoutY();
        int newCol = pixelToBoard(xPixel);
        int newRow = pixelToBoard(yPixel);
        Square dest = new Square(newRow, newCol);
        if(gameSession.move(dest.toString())) {
            pawn.move(newCol*TILE_SIZE, (BOARD_SIZE-TILE_SIZE)-newRow*TILE_SIZE);
            System.out.println(pawn.getType() + "-> " + dest);
            view.updateInfoPanel();
        }
        else
            pawn.reverse();
    }

    public void verticalWallEntered(MouseEvent event, VerticalWall wall) {
        int row = wall.getRow();
        int col = wall.getCol();
        if(wall.getRow() > 0) {
            if(!doesWallExist(wall.toAlgebraic(BOARD_DIMENSION - (row + 1), col), false)) {
                VerticalWall wallAbove = view.findVwall(row - 1, col);
                wallAbove.setFill(Color.BLACK);
                wall.setFill(Color.BLACK);
            }
        }
    }
    public void verticalWallPressed(MouseEvent event, VerticalWall wall) {
        int row = wall.getRow();
        int col = wall.getCol();
        if(row == 0 || getPlayerWallLeft() == 0 || getTurn() != 0)
            return;
        if(doesWallExist(wall.toAlgebraic(BOARD_DIMENSION - (row + 1), col), false)) {
            System.out.println("There is already a wall here.");
        }
        else {
            addWall(wall.toAlgebraic(BOARD_DIMENSION - (row + 1), col), false);
            VerticalWall wallAbove = view.findVwall(row - 1, col);
            wall.setFill(Color.BLACK);
            wallAbove.setFill(Color.BLACK);
            wall.setPressCommit(true);
            view.generateInfoPanel();
        }
    }
    public void verticalWallExited(MouseEvent event, VerticalWall wall) {
        int row = wall.getRow();
        int col = wall.getCol();
        if(row > 0 && !wall.isPressCommit()) {
            if(!doesWallExist(wall.toAlgebraic(BOARD_DIMENSION - (row + 1), col), false)) {
                VerticalWall wallAbove = view.findVwall(row - 1, col);
                wallAbove.setFill(Color.SILVER);
                wall.setFill(Color.SILVER);
            }
        }
    }

    public void horizontalWallEntered(MouseEvent event, HorizontalWall wall) {
        int row = wall.getRow();
        int col = wall.getCol();
        if(col < BOARD_DIMENSION-1) {
            if(!doesWallExist(wall.toAlgebraic(BOARD_DIMENSION - (row + 1), col), true)) {
                HorizontalWall rightWall = view.findHwall(row, col+1);
                rightWall.setFill(Color.BLACK);
                wall.setFill(Color.BLACK);
            }

        }
    }
    public void horizontalWallPressed(MouseEvent event, HorizontalWall wall) {
        int row = wall.getRow();;
        int col = wall.getCol();
        if(col == BOARD_DIMENSION-1 || getPlayerWallLeft() == 0 || getTurn() != 0)
            return;
        if(doesWallExist(wall.toAlgebraic(BOARD_DIMENSION - (row + 1), col), true)) {
            System.out.println("There is already a wall here.");
        }
        else {
            addWall(wall.toAlgebraic(BOARD_DIMENSION - (row + 1), col), true);
            HorizontalWall rightWall = view.findHwall(row, col + 1);
            wall.setFill(Color.BLACK);
            rightWall.setFill(Color.BLACK);
            wall.setPressCommit(true);
            view.updateInfoPanel();
        }
    }
    public void horizontalWallExited(MouseEvent event, HorizontalWall wall) {
        int row = wall.getRow();
        int col = wall.getCol();
        if(col < BOARD_DIMENSION-1 && !wall.isPressCommit()) {
            if(!doesWallExist(wall.toAlgebraic(BOARD_DIMENSION - (row + 1), col), true)) {
                HorizontalWall rightWall = view.findHwall(row, col+1);
                rightWall.setFill(Color.SILVER);
                wall.setFill(Color.SILVER);
            }
        }
    }


    public boolean doesWallExist(String squareLocation, boolean isHorizontal) {
        char orientation = isHorizontal ? 'h' : 'v';
        Square sq = new Square(squareLocation);
        Wall wall = new Wall(sq, orientation);
        return !gameSession.isValidWallPlacement(wall);
    }

    public int getPlayerWallLeft() {
        return gameSession.getPlayer(gameSession.currentTurn()).getWallsLeft();
    }
    public int getTurn() {
        return gameSession.currentTurn();
    }
    public void addWall(String squareLocation, boolean isHorizontal) {
        char orientation = isHorizontal ? 'h' : 'v';
        Square thisSquare = new Square(squareLocation);
        Wall wall = new Wall(thisSquare, orientation);
        gameSession.move(wall.toString());
    }


    public int pixelToBoard(double pixel) {
        return (int)(pixel+ TILE_SIZE/2)/TILE_SIZE;
    }
}
