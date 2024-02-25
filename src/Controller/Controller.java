package Controller;


import Model.Gamestate.*;
import View.pieces.HorizontalWall;
import View.pieces.Pawn;
import View.Game;
import View.pieces.VerticalWall;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Controller {
    public static final int TILE_SIZE = 50;
    public static final int BOARD_DIMENSION = 9;
    public static final int BOARD_SIZE = TILE_SIZE*BOARD_DIMENSION;
    public static final int MAX_PLAYERS = 2;
    private final Player[] players = new Player[MAX_PLAYERS];
    private final Board board = new Board();
    private static int turn = 0;
    private Game view;
    private AIPlayer aiPlayer;

    public Controller(Game view) {
        this.view = view;
    }

    public void addPlayer(String name, String color, int id){
        Player player = (id == 0) ? new Player(name, color, new Square("e1"), 8) : new Player(name, color, new Square("e9"), 0);
        players[id] = player;
        this.aiPlayer = (id == 1) ? new AIPlayer(id, 0, this) : null;

    }

    public void handlePawnMovement(MouseEvent event, Pawn pawn) {
        if(getTurn() == pawn.getType().ordinal()){
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
        if(moveValidator(dest.toString())) {
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
        if(row == 0 || getCurrentPlayerWalls() == 0 || getTurn() != 0)
            return;
        if(doesWallExist(wall.toAlgebraic(BOARD_DIMENSION - (row + 1), col), false)) {
            System.out.println("There is already a wall here.");
        }
        else {
            createWallMove(wall.toAlgebraic(BOARD_DIMENSION - (row + 1), col), false);
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
        if(col == BOARD_DIMENSION-1 || getCurrentPlayerWalls() == 0 || getTurn() != 0)
            return;
        if(doesWallExist(wall.toAlgebraic(BOARD_DIMENSION - (row + 1), col), true)) {
            System.out.println("There is already a wall here.");
        }
        else {
            createWallMove(wall.toAlgebraic(BOARD_DIMENSION - (row + 1), col), true);
            HorizontalWall rightWall = view.findHwall(row, col + 1);
            view.updateHorzWall(wall, rightWall);
        }
    }

    public boolean doesWallExist(String squareLocation, boolean isHorizontal) {
        char orientation = isHorizontal ? 'h' : 'v';
        Square sq = new Square(squareLocation);
        Wall wall = new Wall(sq, orientation);
        return !isValidWallPlacement(wall);
    }
    public void createWallMove(String squareLocation, boolean isHorizontal) {
        char orientation = isHorizontal ? 'h' : 'v';
        Square thisSquare = new Square(squareLocation);
        Wall wall = new Wall(thisSquare, orientation);
        moveValidator(wall.toString());
    }

    public boolean gameOver() {
        return players[0].getPos().getRow() == players[0].getDestRow() || players[1].getPos().getRow() == players[1].getDestRow();
    }
    private boolean isValidSyntax(String move) {
        Pattern p = Pattern.compile("[a-i][0-9][hv]?");
        Matcher m = p.matcher(move);
        return m.matches();
    }
    private boolean isWallMove(String move) {
        return isValidSyntax(move) && move.length() == 3;
    }
    public boolean isValidWallPlacement(Wall wall) {
        if(players[getTurn()].getWallsLeft() <= 0)
            return false;
        return board.isValidWallPlacement(wall, players[0], players[1]);
    }
    public void placeWall(Wall wall) {
        board.placeWall(wall);
        players[getTurn()].decWalls();
    }
    public boolean isValidTraversal(Square dest) {
        return board.isValidTraversal(dest, getCurrentPlayerPos(), getOtherPlayerPos());
    }
    public void movePawn(Square sq) {
        players[getTurn()].setPos(sq);
    }
    public boolean moveValidator(String move) {
        boolean flag = gameOver();
        if(flag) // Game over.
            return false;
        if (isWallMove(move)) {
            Wall wall = new Wall(move);
            flag = isValidWallPlacement(wall); // Check validity of wall move.
            if (flag) {
                placeWall(wall); // Update player walls and add wall to walls list.
                turn++;
            }
        }
        else {
            Square sq = new Square(move);
            flag = isValidTraversal(sq);
            if (flag) {
                movePawn(sq);
                turn++;
            }
        }
        return flag;
    }
    public void triggerAI() {
        if(getTurn() != 0) {
            String aiMove = aiPlayer.generateMove(board.graph ,getCurrentPlayerPos(), getOtherPlayerPos());
            if(aiMove != null) {
                moveValidator(aiMove);
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
    public int boardToPixel(int boardIndex) { return boardIndex*TILE_SIZE; }
    public int pixelToBoard(double pixel) {
        return (int)(pixel+ TILE_SIZE/2)/TILE_SIZE;
    }
    public String getCurrentPlayerName() {
        return players[getTurn()].getName();
    }
    public String getCurrentPlayerColor() {
        return players[getTurn()].getPlayerColor();
    }
    public int getCurrentPlayerWalls() {
        return players[getTurn()].getWallsLeft();
    }
    public int getTurn() {
        return turn%2;
    }
    public Square getCurrentPlayerPos() {
        return getCurrentPlayer().getPos();
    }
    public Square getOtherPlayerPos() {
        return getOtherPlayer().getPos();
    }
    public Player getCurrentPlayer() { return players[getTurn()]; }
    public Player getOtherPlayer() { return players[(getTurn()+1)%2]; }
    public Board getBoard() {
        return this.board;
    }
    public Player getPlayer(int id) { return players[id]; }
}
