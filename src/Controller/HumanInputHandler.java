package Controller;
import Model.Square;
import Model.GameRules;
import Model.Wall;
import Model.Player;

import View.pieces.PawnElements.*;
import View.Game;
import View.pieces.Walls.*;
import javafx.scene.input.MouseEvent;

import static Utilities.Constants.*;

/**
 * This class handles human input events and interactions in the game.
 */
public class HumanInputHandler {
    private final GameRules model;
    private final ViewUpdater viewUpdater;
    private final Game view;
    private AI ai;

    /**
     * Constructs a HumanInputHandler object.
     * @param view            The game view.
     * @param startingPlayer  The ID of the starting player.
     */
    public HumanInputHandler(Game view, int startingPlayer) {
        this.view = view;
        this.viewUpdater = ViewUpdater.getInstance(view);
        this.model = new GameRules(startingPlayer);
    }

    /**
     * Adds a player to the game.
     * @param name  The name of the player.
     * @param color The color of the player.
     * @param id    The ID of the player.
     */
    public void addPlayer(String name, String color, int id) {
        if (name.equals("HUMAN")) {
            model.addPlayer(name, color, new Square("e1"), 8, id);
        } else {
            model.addPlayer(name, color, new Square("e9"), 0, id);
        }
    }

    /**
     * Adds an opponent to the game.
     * @param id The ID of the opponent.
     */
    public void addOpponent(int id) {
        ai = new AI(id, model, view);
    }

    /**
     * Retrieves the name of a player.
     * @param id The ID of the player.
     * @return The name of the player.
     */
    public String getPlayerName(int id) {
        Player player = this.model.getPlayer(id);
        return player.getName();
    }


    /**
     * Retrieves the number of walls left for a player.
     * @param id The ID of the player.
     * @return The number of walls left for the player.
     */
    public int getPlayerWallsLeft(int id) {
        Player player = this.model.getPlayer(id);
        return player.getWallsLeft();
    }

    /**
     * Retrieves the color of a player.
     * @param id The ID of the player.
     * @return The color of the player.
     */
    public String getPlayerColor(int id) {
        Player player = this.model.getPlayer(id);
        return player.getColor();
    }

    /**
     * Handles pawn movement events.
     * @param event The mouse event.
     * @param pawn The pawn object.
     */
    public void handlePawnMovement(MouseEvent event, Pawn pawn) {
        if (model.getTurn() == pawn.getType().ordinal()) {
            switch (event.getEventType().getName()) {
                case "MOUSE_PRESSED":
                    pawnMousePressed(event, pawn);
                    break;
                case "MOUSE_DRAGGED":
                    pawnMouseDragged(event, pawn);
                    break;
                case "MOUSE_RELEASED":
                    pawnMouseReleased(pawn);
                    onHumanMoveCompleted();
                    break;
            }
        }
    }
    /**
     * Handles mouse events related to vertical wall movement.
     * @param event The mouse event.
     * @param wall The horizontal wall object.
     */
    public void handleVerticalWallMovement(MouseEvent event, VerticalWall wall) {
        switch(event.getEventType().getName()) {
            case "MOUSE_ENTERED":
                verticalWallEntered(wall);
                break;
            case "MOUSE_PRESSED":
                verticalWallPressed(wall);
                break;
            case "MOUSE_EXITED":
                verticalWallExited(wall);
                onHumanMoveCompleted();
                break;
        }
    }
    /**
     * Handles mouse events related to horizontal wall movement.
     * @param event The mouse event.
     * @param wall The horizontal wall object.
     */
    public void handleHorizontalWallMovement(MouseEvent event, HorizontalWall wall) {
        switch (event.getEventType().getName()) {
            case "MOUSE_ENTERED":
                horizontalWallEntered(wall);
                break;
            case "MOUSE_PRESSED":
                horizontalWallPressed(wall);
                break;
            case "MOUSE_EXITED":
                horizontalWallExited(wall);
                onHumanMoveCompleted();
                break;
        }
    }

    /**
     * Handles the mouse press event for moving a pawn.
     * @param event The mouse event.
     * @param pawn The pawn object.
     */
    public void pawnMousePressed(MouseEvent event, Pawn pawn) {
        pawn.mouseX = event.getSceneX();
        pawn.mouseY = event.getSceneY();
    }

    /**
     * Handles the mouse drag event for moving a pawn.
     * @param event The mouse event.
     * @param pawn The pawn object.
     */
    public void pawnMouseDragged(MouseEvent event, Pawn pawn) {
        if (pawn.getType() == PawnType.HUMAN) {
            // Continually calculates horizontal distance mouse has moved since last update.
            // getLayoutX: current X coordinate of the node within its parent's coordinate system.
            pawn.relocate(pawn.getLayoutX() + (event.getSceneX() - pawn.mouseX), pawn.getLayoutY() + (event.getSceneY() - pawn.mouseY));
            pawn.mouseX = event.getSceneX();
            pawn.mouseY = event.getSceneY();
        }
    }

    /**
     * Handles the mouse release event for moving a pawn.
     * @param pawn The pawn object.
     */
    public void pawnMouseReleased(Pawn pawn) {
        double xPixel = pawn.getLayoutX();
        double yPixel = (BOARD_SIZE - TILE_SIZE) - pawn.getLayoutY();
        int newCol = pixelToBoard(xPixel);
        int newRow = pixelToBoard(yPixel);
        Square dest = new Square(newRow, newCol);
        int turn = model.getTurn();
        if (model.commitMove(dest.toString()))
            viewUpdater.updatePawnPosition(turn, newRow, newCol);
        else
            viewUpdater.updatePawnPosition(turn, -1, -1);
    }

    /**
     * Handles the mouse entering event for a vertical wall.
     * @param wall The vertical wall object.
     */
    public void verticalWallEntered(VerticalWall wall) {
        if (wall.getRow() > 1) {
            if (model.isLegalWallPlacement(wall.toAlgebraic(), false)) {
                viewUpdater.fillVerticalWall(wall, false);
            }
        }
    }

    /**
     * Handles the mouse exiting event for a vertical wall.
     * @param wall The vertical wall object.
     */
    public void verticalWallExited(VerticalWall wall) {
        if (wall.getRow() > 1 && !wall.isPlaced()) {
            if (model.isLegalWallPlacement(wall.toAlgebraic(), false)) {
                viewUpdater.removeFillVerticalWall(wall);
            }
        }
    }

    /**
     * Handles the mouse press event for a vertical wall.
     * @param wall The vertical wall object.
     */
    public void verticalWallPressed(VerticalWall wall) {
        Wall newWall = new Wall(wall.toAlgebraic() + 'v');
        int turn = model.getTurn();
        if (model.commitMove(newWall.toString())) {
            viewUpdater.updateVerticalWall(wall.getRow(), wall.getCol(), turn);
        }
    }

    /**
     * Handles the mouse entering event for a horizontal wall.
     * @param wall The horizontal wall object.
     */
    public void horizontalWallEntered(HorizontalWall wall) {
        if (wall.getCol() < BOARD_DIMENSION) {
            if (model.isLegalWallPlacement(wall.toAlgebraic(), true)) {
                viewUpdater.fillHorizontalWall(wall, false);
            }
        }
    }

    /**
     * Handles the mouse exiting event for a horizontal wall.
     * @param wall The horizontal wall object.
     */
    public void horizontalWallExited(HorizontalWall wall) {
        if (wall.getCol() < BOARD_DIMENSION && !wall.isPlaced()) {
            if (model.isLegalWallPlacement(wall.toAlgebraic(), true)) {
                viewUpdater.removeFillHorizontalWall(wall);
            }
        }
    }

    /**
     * Handles the mouse press event for a horizontal wall.
     * @param wall The horizontal wall object.
     */
    public void horizontalWallPressed(HorizontalWall wall) {
        Wall newWall = new Wall(wall.toAlgebraic() + 'h');
        int turn = model.getTurn();
        if (model.commitMove(newWall.toString())) {
            viewUpdater.updateHorizontalWall(wall.getRow(), wall.getCol(), turn);
        }
    }


    /**
     * Converts pixel coordinates to board coordinates.
     * @param pixel The pixel coordinate.
     * @return The corresponding board coordinate.
     */
    public int pixelToBoard(double pixel) {
        return (int) (pixel + TILE_SIZE / 2) / TILE_SIZE;
    }

    /**
     * Handles the completion of a human player's move by initiating the AI's turn.
     */
    public void onHumanMoveCompleted() {
        ai.AiTurn();
    }

}
