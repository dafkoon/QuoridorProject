package Controller;
import Model.Square;
import Model.GameRules;
import Model.Wall;
import Model.Player;

import View.pieces.HorizontalWall;
import View.pieces.Pawn;
import View.pieces.VerticalWall;
import javafx.scene.input.MouseEvent;
import View.Game;

import static Utilities.Constants.*;

public class HumanInputHandler {
    private final GameRules model;
    private final ViewUpdater viewUpdater;
    private final Game view;
    private AI ai;

    public HumanInputHandler(Game view, int startingPlayer) {
        this.view = view;
        this.viewUpdater = ViewUpdater.getInstance(view);
        this.model = new GameRules(startingPlayer);
    }

    public void addPlayer(String name, String color, int id) {
        if (name.equals("HUMAN")) {
            model.addPlayer(name, color, new Square("e1"), 8, id);
        } else {
            model.addPlayer(name, color, new Square("e9"), 0, id);
        }
    }

    public void addOpponent(int id) {
        ai = new AI(id, model, view);
    }

    public String getPlayerName(int id) {
        Player player = this.model.getPlayer(id);
        return player.getName();
    }
    public int getPlayerWallsLeft(int id) {
        Player player = this.model.getPlayer(id);
        return player.getWallsLeft();
    }
    public String getPlayerColor(int id) {
        Player player = this.model.getPlayer(id);
        return player.getColor();
    }

    public void handlePawnMovement(MouseEvent event, Pawn pawn) {
        if(model.getTurn() == pawn.getType().ordinal()){
            switch(event.getEventType().getName()) {
                case "MOUSE_PRESSED":
                    pawnMousePressed(event, pawn);
                    break;
                case "MOUSE_DRAGGED":
                    pawnMouseDragged(event, pawn);
                    break;
                case "MOUSE_RELEASED":
                    pawnMouseReleased(event, pawn);
                    onHumanMoveCompleted();
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
                onHumanMoveCompleted();
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
                onHumanMoveCompleted();
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
        int turn = model.getTurn();
        if(model.commitMove(dest.toString()))
            viewUpdater.updatePawnPosition(turn, newRow, newCol);
        else
            viewUpdater.updatePawnPosition(turn, -1, -1);
    }

    public void verticalWallEntered(MouseEvent event, VerticalWall wall) {
        if(wall.getRow() > 1) {
            if(model.isLegalWallPlacement(wall.toAlgebraic(), false)) { //BOARD_DIMENSION - (row + 1), col
                viewUpdater.fillVerticalWall(wall, false);
            }
        }
    }
    public void verticalWallExited(MouseEvent event, VerticalWall wall) {
        if(wall.getRow() > 1 && !wall.isPressCommit()) {
            if(model.isLegalWallPlacement(wall.toAlgebraic(), false)) {
                viewUpdater.removeFillVerticalWall(wall);
            }
        }
    }
    public void verticalWallPressed(MouseEvent event, VerticalWall wall) {
        Wall newWall = new Wall(wall.toAlgebraic() + 'v');
        int turn = model.getTurn();
        if(model.commitMove(newWall.toString())) {
            viewUpdater.updateVerticalWall(wall.getRow(), wall.getCol(), turn);
        }
    }

    public void horizontalWallEntered(MouseEvent event, HorizontalWall wall) {
        if(wall.getCol() < BOARD_DIMENSION) {
            if(model.isLegalWallPlacement(wall.toAlgebraic(), true)) {
                viewUpdater.fillHorizontalWall(wall, false);
            }

        }
    }
    public void horizontalWallExited(MouseEvent event, HorizontalWall wall) {
        if(wall.getCol() < BOARD_DIMENSION && !wall.isPressCommit()) {
            if(model.isLegalWallPlacement(wall.toAlgebraic(), true)) {
                viewUpdater.removeFillHorizontalWall(wall);
            }
        }
    }
    public void horizontalWallPressed(MouseEvent event, HorizontalWall wall) {
        Wall newWall = new Wall(wall.toAlgebraic() + 'h');
        int turn = model.getTurn();
        if(model.commitMove(newWall.toString())) {
            viewUpdater.updateHorizontalWall(wall.getRow(), wall.getCol(), turn);
        }
    }

    public int pixelToBoard(double pixel) {
        return (int)(pixel+ TILE_SIZE/2)/TILE_SIZE;
    }

    public void onHumanMoveCompleted() {
        ai.AiTurn();
    }
}
