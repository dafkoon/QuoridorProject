package Controller;
import Model.Gamestate.Square;
import Model.Gamestate.Model;
import Model.Gamestate.Wall;
import Model.Gamestate.Player;

import View.pieces.HorizontalWall;
import View.pieces.Pawn;
import View.pieces.VerticalWall;
import javafx.scene.input.MouseEvent;
import View.Game;

public class HumanInputHandler {
    public static final int TILE_SIZE = 50;
    public static final int BOARD_DIMENSION = 9;
    public static final int BOARD_SIZE = TILE_SIZE*BOARD_DIMENSION;
    private Model model;
    private Game view;
    private AI ai;

    public HumanInputHandler(Game view, int startingPlayer) {
        this.view = view;
        this.model = new Model(startingPlayer);
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
        updatePawnPosition(model.getTurn(), dest);
    }

    public void verticalWallEntered(MouseEvent event, VerticalWall wall) {
        int row = wall.getRow();
        int col = wall.getCol();
        if(row > 1) {
            if(model.isWallLegal(wall.toAlgebraic(), false)) { //BOARD_DIMENSION - (row + 1), col
                VerticalWall secondWall = view.findVerticalWallObject(row - 1, col);
                view.fillVerticalWall(wall, secondWall, false);
            }
        }
    }
    public void verticalWallExited(MouseEvent event, VerticalWall wall) {
        int row = wall.getRow();
        int col = wall.getCol();
        if(row > 1 && !wall.isPressCommit()) {
            if(model.isWallLegal(wall.toAlgebraic(), false)) {
                VerticalWall secondWall = view.findVerticalWallObject(row - 1, col);
                view.removeFillVerticalWall(wall, secondWall);
            }
        }
    }
    public void verticalWallPressed(MouseEvent event, VerticalWall wall) {
        int row = wall.getRow();
        int col = wall.getCol();
        Wall newWall = new Wall(wall.toAlgebraic() + 'v');
        if(model.placeWall(newWall)) {
            updateVerticalWall(row, col);

        }
    }

    public void horizontalWallEntered(MouseEvent event, HorizontalWall wall) {
        int row = wall.getRow();
        int col = wall.getCol();
        if(col < BOARD_DIMENSION) {
            if(model.isWallLegal(wall.toAlgebraic(), true)) {
                HorizontalWall secondWall = view.findHorizontalWallObject(row, col+1);
                view.fillHorizontalWall(wall, secondWall, false);
            }

        }
    }
    public void horizontalWallExited(MouseEvent event, HorizontalWall wall) {
        int row = wall.getRow();
        int col = wall.getCol();
        if(col < BOARD_DIMENSION && !wall.isPressCommit()) {
            if(model.isWallLegal(wall.toAlgebraic(), true)) {
                HorizontalWall secondWall = view.findHorizontalWallObject(row, col+1);
                view.removeFillVerticalWall(wall, secondWall);
            }
        }
    }
    public void horizontalWallPressed(MouseEvent event, HorizontalWall wall) {
        int row = wall.getRow();
        int col = wall.getCol();
        Wall newWall = new Wall(wall.toAlgebraic() + 'h');
        if(model.placeWall(newWall)) {
            updateHorizontalWall(row, col);
        }
    }

    public int pixelToBoard(double pixel) {
        return (int)(pixel+ TILE_SIZE/2)/TILE_SIZE;
    }

    public void updateHorizontalWall(int row, int col) {
        HorizontalWall wall1 = view.findHorizontalWallObject(row, col);
        HorizontalWall wall2 = view.findHorizontalWallObject(row, col + 1);
        view.fillHorizontalWall(wall1, wall2, true);
        view.updateInfoPanel((model.getTurn()+1)%2);
    }

    public void updateVerticalWall(int row, int col) {
        VerticalWall wall1 = view.findVerticalWallObject(row, col);
        VerticalWall wall2 = view.findVerticalWallObject(row - 1, col);
        view.fillVerticalWall(wall1, wall2, true);
        view.updateInfoPanel((model.getTurn()+1)%2);
    }

    public void updatePawnPosition(int playerTurn, Square move) {
        if(move == null)
            return;
        if(model.commitMove(move.toString())) {
            view.updatePawnLocation(playerTurn, move.getCol()*TILE_SIZE, (BOARD_SIZE-TILE_SIZE)-move.getRow()*TILE_SIZE);
            if(model.gameOver()) {
                System.out.println("winner");
                view.decideWinner(playerTurn);
            }
        }
        else
            view.updatePawnLocation(playerTurn, -1, -1);
    }

    public void onHumanMoveCompleted() {
        ai.AiTurn();
    }
}
