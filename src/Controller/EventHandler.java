package Controller;
import Model.Board;
import Model.Square;
import View.pieces.Pawn;
import View.pieces.Tile;
import Model.GameSession;
import javafx.scene.input.MouseEvent;

import static Controller.Controller.BOARD_DIMENSION;
import static Controller.Controller.TILE_SIZE;

public class EventHandler {
    GameSession gameSession;
    private final int BOARD_SIZE = BOARD_DIMENSION*TILE_SIZE;

    public EventHandler(GameSession gameSession) {
        this.gameSession = gameSession;
    }

    public void handlePawnMovement(MouseEvent event, Pawn pawn) {
        switch(event.getEventType().getName()) {
            case "MOUSE_PRESSED":
                pawnMousePressed(event, pawn);
                break;
            case "MOUSE_DRAGGED":
                pawnMouseDragged(event, pawn);
                break;
            case "MOUSE_RELEASED":
                pawnMouseReleased(event, pawn);

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
//        System.out.println("pixels " + pawn.getType() + " moved to x:" + xPixel + " y:" + yPixel);
        System.out.println("square col:" + newCol + " row:" + newRow + "   " + dest);
//        System.out.println("relocate " + pawn.getType() + " moved to: " + xIndex*TILE_SIZE + ", " + yIndex*TILE_SIZE);
        if(gameSession.isValidTraversal(dest)) {
            System.out.println(pawn.getType() + " x:" + newCol + " y:" + newRow);
        }
//            if(controller.isValidMove(type.ordinal() ,nextTile)); {
//                System.out.println(type + " x:" + newX + " y:" + newY);
//                pawn.moveTo(newX, newY);
////                pawn.moveTo(newX, newY);
////                gameSession.getBoard().getTile(currentSquare.getX(), currentSquare.getY()).setContainsPawn(false);
////                gameSession.getBoard().getTile(nextSquare.getX(), nextSquare.getY()).setContainsPawn(true);
//            }

        pawn.relocate(newCol*TILE_SIZE, (BOARD_SIZE-TILE_SIZE)-newRow*TILE_SIZE);
    }

    public int pixelToBoard(double pixel) {
        return (int)(pixel+ TILE_SIZE/2)/TILE_SIZE;
    }
}
