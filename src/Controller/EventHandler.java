package Controller;
import View.pieces.Pawn;
import View.Game;
import Model.Gamestate.Square;

import Model.Gamestate.GameSession;
import javafx.scene.input.MouseEvent;


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
            }
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

    public int pixelToBoard(double pixel) {
        return (int)(pixel+ TILE_SIZE/2)/TILE_SIZE;
    }
}
