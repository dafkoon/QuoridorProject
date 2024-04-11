package Controller;

import Model.Validator;
import Model.Player;
import Model.Square;

import View.pieces.Pawn;
import View.pieces.Pawn.PawnType;
import View.pieces.Tile;
import View.pieces.Walls.HorizontalWall;
import View.pieces.Walls.VerticalWall;
import View.pieces.Walls.Wall;
import javafx.scene.input.MouseEvent;


import java.util.ArrayList;

import static Utilities.Constants.*;

/**
 * This class handles human input events and interactions in the game.
 */
public class ClientHandler {
    private final Validator validator;
    private final ViewUpdater viewUpdater;
    private AI ai;
//    private final int startingPlayer;
    private boolean isHumanTurn;

    /**
     * Constructs a ClientSideHandler object.
     * @param viewUpdater The game view.
     * @param startingPlayer The ID of the starting player.
     */
    public ClientHandler(ViewUpdater viewUpdater, int startingPlayer) {
        this.viewUpdater = viewUpdater;
        this.isHumanTurn = startingPlayer == PawnType.HUMAN.ordinal();
        this.validator = new Validator(startingPlayer);
    }

    public void initPlayers(Pawn[] pawns) {
        for (Pawn pawn : pawns) {
            String name = pawn.getType().name();
            int id = pawn.getType().ordinal();
            if (name.equals("HUMAN"))
                validator.addPlayer(name, "e1", id);
            else
                validator.addPlayer(name, "e9", id);
        }
        InitAI(PawnType.AI.ordinal());
    }

    /**
     * Adds an opponent to the game.
     * @param id The ID of the opponent.
     */
    private void InitAI(int id) {
        ai = new AI(viewUpdater, validator, id);
    }

    public void showReachableTiles() {
        Square src = validator.getPlayer(PawnType.HUMAN.ordinal()).getPosition();
        for (Square sq : src.neighbourhood(2)) {
            if (validator.isValidTraversal(src, sq)) {
                viewUpdater.showTile(sq.toString());

            }
        }
        System.out.println();
    }

    public void hideReachableTiles() {
        viewUpdater.hideTile();
    }

    /**
     * Handles the mouse release event for moving a pawn.
     * @param pawn The pawn object.
     */
    public void mouseReleasedPawn(Pawn pawn) {
        // Check if it's the human player's turn
        if (!isHumanTurn)
            return;

        // Get the current position of the pawn in pixels
        double xPixel = pawn.getLayoutX();
        double yPixel = (BOARD_SIZE - TILE_SIZE) - pawn.getLayoutY();

        // Convert pixel positions to board coordinates
        int newCol = pixelToBoard(xPixel);
        int newRow = pixelToBoard(yPixel);
//        System.out.println(newCol + " " + newRow);
        // Create a Square object representing the destination square
        Square squareToGo = new Square(newRow, newCol);
        int turn = validator.getTurn();
        // Try to commit the move to the model
        if (validator.commitMove(squareToGo.toString())) {
            // If move is successful, update pawn position and switch turn to AI
            viewUpdater.updatePawnPosition(newRow, newCol, turn);
            isHumanTurn = false;
            callAI(); // Call AI for its turn
        } else {
            // If move is invalid, update pawn position to indicate no move and stay on the human's turn
            viewUpdater.updatePawnPosition(-1, -1, turn);
        }
    }

    public void wallEvents(MouseEvent event, Wall wall) {
        if (wall instanceof HorizontalWall) {
            horizontalWallEvents(event, (HorizontalWall) wall);
        } else if (wall instanceof VerticalWall) {
            verticalWallEvents(event, (VerticalWall) wall);
        }
    }

    /**
     * Handles mouse events related to vertical wall movement.
     * @param event The mouse event.
     * @param wall  The horizontal wall object.
     */
    public void verticalWallEvents(MouseEvent event, VerticalWall wall) {
        if (!isHumanTurn)
            return;
        switch (event.getEventType().getName()) {
            case "MOUSE_ENTERED":
                verticalWallEntered(wall);
                break;
            case "MOUSE_PRESSED":
                verticalWallPressed(wall);
                break;
            case "MOUSE_EXITED":
                verticalWallExited(wall);
                break;
        }
        if (!isHumanTurn) {
            callAI();
        }
    }

    /**
     * Handles mouse events related to horizontal wall movement.
     *
     * @param event The mouse event.
     * @param wall  The horizontal wall object.
     */
    public void horizontalWallEvents(MouseEvent event, HorizontalWall wall) {
        if (!isHumanTurn)
            return;
        switch (event.getEventType().getName()) {
            case "MOUSE_ENTERED":
                horizontalWallEntered(wall);
                break;
            case "MOUSE_PRESSED":
                horizontalWallPressed(wall);
                break;
            case "MOUSE_EXITED":
                horizontalWallExited(wall);
                break;
        }
        if (!isHumanTurn)
            callAI();
    }

    /**
     * Handles the mouse entering event for a vertical wall.
     *
     * @param wall The vertical wall object.
     */
    private void verticalWallEntered(VerticalWall wall) {
        if (wall.getRow() > 1) {
            if (validator.isValidWallPlacement(wall.toAlgebraic(), false))
                viewUpdater.fillVerticalWall(wall, false); // Fill the wall but don't set it as pressed.
        }
    }

    /**
     * Handles the mouse exiting event for a vertical wall.
     *
     * @param wall The vertical wall object.
     */
    private void verticalWallExited(VerticalWall wall) {
        if (wall.getRow() > 1 && !wall.isPlaced()) {
            if (validator.isValidWallPlacement(wall.toAlgebraic(), false))
                viewUpdater.removeFillVerticalWall(wall);
        }
    }

    /**
     * Handles the mouse press event for a vertical wall.
     *
     * @param wall The vertical wall object.
     */
    private void verticalWallPressed(VerticalWall wall) {
        String wallString = wall.toAlgebraic() + 'v';
        if (validator.commitMove(wallString)) {
            int turn = validator.getTurn();
            viewUpdater.placeVerticalWall(wall.getRow(), wall.getCol(), turn);
            isHumanTurn = false;
        }
    }

    /**
     * Handles the mouse entering event for a horizontal wall.
     * @param wall The horizontal wall object.
     */
    private void horizontalWallEntered(HorizontalWall wall) {
        if (wall.getCol() < BOARD_DIMENSION) {
            if (validator.isValidWallPlacement(wall.toAlgebraic(), true))
                viewUpdater.fillHorizontalWall(wall, false);
        }
    }

    /**
     * Handles the mouse exiting event for a horizontal wall.
     *
     * @param wall The horizontal wall object.
     */
    private void horizontalWallExited(HorizontalWall wall) {
        if (wall.getCol() < BOARD_DIMENSION && !wall.isPlaced()) {
            if (validator.isValidWallPlacement(wall.toAlgebraic(), true))
                viewUpdater.removeFillHorizontalWall(wall);
        }
    }

    /**
     * Handles the mouse press event for a horizontal wall.
     *
     * @param wall The horizontal wall object.
     */
    private void horizontalWallPressed(HorizontalWall wall) {
        String wallString = wall.toAlgebraic() + 'h';
        if (validator.commitMove(wallString)) {
            int turn = validator.getTurn();
            viewUpdater.placeHorizontalWalls(wall.getRow(), wall.getCol(), turn);
            isHumanTurn = false;
        }
    }


    public void startGame() {
        if(!isHumanTurn)
            callAI();
    }

    /**
     * Handles the completion of a human player's move by initiating the AI's turn.
     */
    public void callAI() {
        boolean isGameOver = validator.gameOver();
        if (isGameOver) {
            viewUpdater.setWinner(PawnType.HUMAN.ordinal());
        } else {
            ai.AiTurn();
            if (validator.gameOver())
                viewUpdater.setWinner(PawnType.AI.ordinal());
        }

        isHumanTurn = true;
    }

    /**
     * Converts pixel coordinates to board coordinates.
     *
     * @param pixel The pixel coordinate.
     * @return The corresponding board coordinate.
     */
    private int pixelToBoard(double pixel) {
        return (int) (pixel + TILE_SIZE / 2) / TILE_SIZE;
    }

    /**
     * Retrieves the number of walls left for a player.
     * @param id The ID of the player.
     * @return The number of walls left for the player.
     */
    public int getPlayerWallsLeft(int id) {
        Player player = this.validator.getPlayer(id);
        return player.getWallsLeft();
    }
}