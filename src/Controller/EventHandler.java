package Controller;
import Model.Gamestate.Wall;
import View.pieces.HorizontalWall;
import View.pieces.Pawn;
import View.Game;
import Model.Gamestate.Square;

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
        triggerAI();
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
        triggerAI();
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
        triggerAI();
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
        if(move(dest.toString())) {
            view.updatePawn(Pawn.PawnType.HUMAN, newCol*TILE_SIZE, (BOARD_SIZE-TILE_SIZE)-newRow*TILE_SIZE);
        }
        else
            view.updatePawn(Pawn.PawnType.HUMAN, -1, -1);
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
    public void verticalWallPressed(MouseEvent event, VerticalWall wall) {
        int row = wall.getRow();
        int col = wall.getCol();
        if(row == 0 || getPlayerWalls() == 0 || getTurn() != 0)
            return;
        if(doesWallExist(wall.toAlgebraic(BOARD_DIMENSION - (row + 1), col), false)) {
            System.out.println("There is already a wall here.");
        }
        else {
            addWall(wall.toAlgebraic(BOARD_DIMENSION - (row + 1), col), false);
            VerticalWall wallAbove = view.findVwall(row - 1, col);
            view.updateVertWall(wall, wallAbove);
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
    public void horizontalWallPressed(MouseEvent event, HorizontalWall wall) {
        int row = wall.getRow();;
        int col = wall.getCol();
        if(col == BOARD_DIMENSION-1 || getPlayerWalls() == 0 || getTurn() != 0)
            return;
        if(doesWallExist(wall.toAlgebraic(BOARD_DIMENSION - (row + 1), col), true)) {
            System.out.println("There is already a wall here.");
        }
        else {
            addWall(wall.toAlgebraic(BOARD_DIMENSION - (row + 1), col), true);
            HorizontalWall rightWall = view.findHwall(row, col + 1);
            view.updateHorzWall(wall, rightWall);
        }
    }


    public boolean doesWallExist(String squareLocation, boolean isHorizontal) {
        char orientation = isHorizontal ? 'h' : 'v';
        Square sq = new Square(squareLocation);
        Wall wall = new Wall(sq, orientation);
        return !gameSession.isValidWallPlacement(wall);
    }
    public void addWall(String squareLocation, boolean isHorizontal) {
        char orientation = isHorizontal ? 'h' : 'v';
        Square thisSquare = new Square(squareLocation);
        Wall wall = new Wall(thisSquare, orientation);
        move(wall.toString());
    }
    public boolean move(String move) {
        return gameSession.move(move);
    }
    public int getPlayerWalls() {
        return gameSession.getPlayer(gameSession.currentTurn()).getWallsLeft();
    }
    public int getTurn() {
        return gameSession.currentTurn();
    }
    public int pixelToBoard(double pixel) {
        return (int)(pixel+ TILE_SIZE/2)/TILE_SIZE;
    }
    public int boardToPixel(int boardIndex) { return boardIndex*TILE_SIZE; }

    public void triggerAI() {
        if(getTurn() != 0) {
            String aiMove = this.gameSession.getAIMove();
            if(aiMove != null) {
                if(aiMove.length() == 2) {
                    Square sq = new Square(aiMove);
                    view.updatePawn(Pawn.PawnType.AI, boardToPixel(sq.getCol()), boardToPixel(BOARD_DIMENSION-(sq.getRow()+1)));
                }
                else if(aiMove.length() == 3) {
                    Wall wall = new Wall(aiMove);
                    if(aiMove.charAt(2) == 'h') {
                        HorizontalWall wall1 = new HorizontalWall(wall.getStartingSq().getCol(), wall.getStartingSq().getRow());
                        HorizontalWall wall2 = view.findHwall(wall.getStartingSq().getRow(), wall.getStartingSq().getCol() + 1);
                        view.updateHorzWall(wall1, wall2);
                    }
                    else if(aiMove.charAt(2) == 'v') {
                        VerticalWall wall1 = new VerticalWall(wall.getStartingSq().getCol(), wall.getStartingSq().getRow());
                        VerticalWall wall2 = view.findVwall(wall.getStartingSq().getRow() - 1, wall.getStartingSq().getCol());
                        view.updateVertWall(wall1, wall2);
                    }
                }
            }
        }
    }
}
