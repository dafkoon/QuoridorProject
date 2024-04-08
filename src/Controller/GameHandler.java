package Controller;

import Model.GameData;
import Model.Player;
import Model.Square;
import Model.Wall;

import View.GUI;
import View.pieces.Pawn;
import View.pieces.Pawn.PawnType;
import View.pieces.Walls.HorizontalWall;
import View.pieces.Walls.VerticalWall;
import javafx.scene.input.MouseEvent;

import static Utilities.Constants.*;

/**
 * This class handles human input events and interactions in the game.
 */
public class GameHandler {
    private final GameData model;
    private final ViewUpdater viewUpdater;
    private final GUI view;
    private AI ai;
    private final int startingPlayer;
    private boolean isHumanTurn = true;

    /**
     * Constructs a ClientSideHandler object.
     * @param view            The game view.
     * @param startingPlayer  The ID of the starting player.
     */
    public GameHandler(GUI view, int startingPlayer) {
        this.view = view;
        this.startingPlayer = startingPlayer;
        this.viewUpdater = ViewUpdater.getInstance(view);
        this.model = new GameData(startingPlayer);
    }

    public void initPlayers(Pawn[] pawns) {
        for(Pawn pawn : pawns) {
            String name = pawn.getType().name();
            int id = pawn.getType().ordinal();
            if(name.equals("HUMAN"))
                model.addPlayer(name, new Square("e1"), id);
            else
                model.addPlayer(name, new Square("e9"), id);
        }
        InitAI(PawnType.AI.ordinal());
    }

    /**
     * Adds an opponent to the game.
     * @param id The ID of the opponent.
     */
    private void InitAI(int id) {
        ai = new AI(id, model, view);
    }


    /**
     * Handles the mouse release event for moving a pawn.
     * @param pawn The pawn object.
     */
    public void pawnReleased(Pawn pawn) {
        if(!isHumanTurn)
            return;
        double xPixel = pawn.getLayoutX();
        double yPixel = (BOARD_SIZE - TILE_SIZE) - pawn.getLayoutY();
        int newCol = pixelToBoard(xPixel);
        int newRow = pixelToBoard(yPixel);
        Square squareToGo = new Square(newRow, newCol);
        int turn = model.getTurn();
        if (model.commitMove(squareToGo.toString())) {
            viewUpdater.updatePawnPosition(turn, newRow, newCol);
            isHumanTurn = false;
        } else {
            viewUpdater.updatePawnPosition(turn, -1, -1);
        }
        if(!isHumanTurn) {
            callAI();
        }
    }

    /**
     * Handles mouse events related to vertical wall movement.
     * @param event The mouse event.
     * @param wall The horizontal wall object.
     */
    public void handleVerticalWallMovement(MouseEvent event, VerticalWall wall) {
        if(!isHumanTurn)
            return;
        switch(event.getEventType().getName()) {
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
        if(!isHumanTurn) {
            callAI();
        }
    }

    /**
     * Handles mouse events related to horizontal wall movement.
     * @param event The mouse event.
     * @param wall The horizontal wall object.
     */
    public void handleHorizontalWallMovement(MouseEvent event, HorizontalWall wall) {
        if(!isHumanTurn)
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
        if(!isHumanTurn)
            callAI();
    }
    /**
     * Handles the mouse entering event for a vertical wall.
     * @param wall The vertical wall object.
     */
    private void verticalWallEntered(VerticalWall wall) {
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
    private void verticalWallExited(VerticalWall wall) {
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
    private void verticalWallPressed(VerticalWall wall) {
        Wall newWall = new Wall(wall.toAlgebraic() + 'v');
        int turn = model.getTurn();
        if (model.commitMove(newWall.toString())) {
            viewUpdater.updateVerticalWall(wall.getRow(), wall.getCol(), turn);
            isHumanTurn = false;
        }
    }

    /**
     * Handles the mouse entering event for a horizontal wall.
     * @param wall The horizontal wall object.
     */
    private void horizontalWallEntered(HorizontalWall wall) {
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
    private void horizontalWallExited(HorizontalWall wall) {
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
    private void horizontalWallPressed(HorizontalWall wall) {
        Wall newWall = new Wall(wall.toAlgebraic() + 'h');
        int turn = model.getTurn();
        if (model.commitMove(newWall.toString())) {
            viewUpdater.updateHorizontalWall(wall.getRow(), wall.getCol(), turn);
            isHumanTurn = false;
        }
    }


    public void startGame() {
        if(startingPlayer == PawnType.AI.ordinal())
            callAI();
    }

    /**
     * Handles the completion of a human player's move by initiating the AI's turn.
     */
    public void callAI() {
        ai.AiTurn();
        isHumanTurn = true;
    }

    /**
     * Converts pixel coordinates to board coordinates.
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
        Player player = this.model.getPlayer(id);
        return player.getWallsLeft();
    }

}
