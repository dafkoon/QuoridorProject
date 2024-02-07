package Controller;
import Model.Square;
import View.pieces.Pawn;
import View.pieces.Tile;
import Model.GameSession;
import javafx.scene.input.MouseEvent;

public class EventHandler {
    public static final int TILE_SIZE = 50;
    GameSession gameSession;

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
        Square tile = new Square(pixelToBoard(pawn.mouseX),pixelToBoard(pawn.mouseX));
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
        int newX = pixelToBoard(pawn.getLayoutX());
        int newY = pixelToBoard(pawn.getLayoutY());
        Square dest = new Square(newX, newY);
        if(gameSession.isValidTraversal(dest)) {
        }
//            if(controller.isValidMove(type.ordinal() ,nextTile)); {
//                System.out.println(type + " x:" + newX + " y:" + newY);
//                pawn.moveTo(newX, newY);
////                pawn.moveTo(newX, newY);
////                gameSession.getBoard().getTile(currentSquare.getX(), currentSquare.getY()).setContainsPawn(false);
////                gameSession.getBoard().getTile(nextSquare.getX(), nextSquare.getY()).setContainsPawn(true);
//            }
        pawn.relocate(newX*TILE_SIZE, newY*TILE_SIZE);
    }

    public int pixelToBoard(double pixel) {
        return (int)(pixel+ TILE_SIZE/2)/TILE_SIZE;
    }
}
