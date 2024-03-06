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
    private AI ai;

    public Controller(Game view) {
        this.view = view;
    }

    public void addPlayer(String name, String color, int id){
        Player player = (id == 0) ? new Player(name, color, new Square("e1"), 8) : new Player(name, color, new Square("e9"), 0);
        players[id] = player;
        this.ai = (id == 1) ? new AI(id, this) : null;
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
        executeAIMove();
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
        executeAIMove();
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
        executeAIMove();
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
        movePawnOnBoard(getTurn(), dest);
    }
    public void verticalWallEntered(MouseEvent event, VerticalWall wall) {
//        System.out.println(wall.toAlgebraic());
        int row = wall.getRow();
        int col = wall.getCol();
        if(wall.getRow() > 0) {
            if(!doesWallExist(wall.toAlgebraic(), false)) { //BOARD_DIMENSION - (row + 1), col
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
            if(!doesWallExist(wall.toAlgebraic(), false)) {
                VerticalWall wallAbove = view.findVwall(row - 1, col);
                wallAbove.setFill(Color.SILVER);
                wall.setFill(Color.SILVER);
            }
        }
    }
    public void verticalWallPressed(MouseEvent event, VerticalWall wall) {
        int row = wall.getRow();
        int col = wall.getCol();
        Wall newWall = new Wall(row, col, 'v');
        wallMoveProcess(newWall);
    }
    public void horizontalWallEntered(MouseEvent event, HorizontalWall wall) {
//        System.out.println(wall.toAlgebraic());
        int row = wall.getRow();
        int col = wall.getCol();
        if(col < BOARD_DIMENSION-1) {
            if(!doesWallExist(wall.toAlgebraic(), true)) {
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
            if(!doesWallExist(wall.toAlgebraic(), true)) {
                HorizontalWall rightWall = view.findHwall(row, col+1);
                rightWall.setFill(Color.SILVER);
                wall.setFill(Color.SILVER);
            }
        }
    }
    public void horizontalWallPressed(MouseEvent event, HorizontalWall wall) {
        int row = wall.getRow();
        int col = wall.getCol();
        Wall newWall = new Wall(row, col, 'h');
        wallMoveProcess(newWall);
    }
    public boolean doesWallExist(String squareLocation, boolean isHorizontal) {
        char orientation = isHorizontal ? 'h' : 'v';
        Square sq = new Square(squareLocation);
        Wall wall = new Wall(sq, orientation);
        return !isValidWallPlacement(wall);
    }

    public void wallMoveProcess(Wall wall) {
        boolean isHorizontal = (wall.toString().charAt(2) == 'h');
        int row = wall.getStartingSq().getRow();
        int col = wall.getStartingSq().getCol();
        if(isHorizontal) {
            if(col == BOARD_DIMENSION-1 || players[getTurn()].getWallsLeft() == 0)
                return;
            else if(doesWallExist(wall.toString(), true))
                System.out.println("There is already a wall here.");
            else if(moveValidator(wall.toString())) {
                HorizontalWall wall1 = view.findHwall(row, col);
                HorizontalWall wall2 = view.findHwall(row, col + 1);
                view.updateHorzWall(wall1, wall2);
            }
        }
        else {
            if(row == 0 || players[getTurn()].getWallsLeft() == 0)
                return;
            else if(doesWallExist(wall.toString(), false))
                System.out.println("There is already a wall here");
            else if(moveValidator(wall.toString())) {
                VerticalWall wall1 = view.findVwall(row, col);
                VerticalWall wall2 = view.findVwall(row - 1, col);
                view.updateVertWall(wall1, wall2);
            }
        }
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
    public void addWall(Wall wall) {;
        board.addWall(wall);
        players[getTurn()].decWalls();
    }
    public void removeWall(Wall wall) {
        board.removeWall(wall);
        players[getTurn()].incWalls();
    }
    public boolean isValidTraversal(Square dest) {
        return board.isValidTraversal(dest, getCurrentPlayerPos(), getOtherPlayerPos());
    }
    public void setCurrentPlayerPos(Square sq) {
        players[getTurn()].setPos(sq);
    }

    public void movePawnOnBoard(int playerTurn, Square move) {
        if(move == null)
            return;
        if(moveValidator(move.toString()))
            view.updatePawn(playerTurn, move.getCol()*TILE_SIZE, (BOARD_SIZE-TILE_SIZE)-move.getRow()*TILE_SIZE);
        else
            view.updatePawn(playerTurn, -1, -1);
    }

    public boolean moveValidator(String move) {
        boolean flag = gameOver();
        if(flag) // Game over.
            return false;
        if (isWallMove(move)) {
            Wall wall = new Wall(move);
            flag = isValidWallPlacement(wall); // Check validity of wall move.
            if (flag) {
                addWall(wall); // Update player walls and add wall to walls list.
                turn++;
            }
        }
        else {
            Square sq = new Square(move);
            flag = isValidTraversal(sq);
            if (flag) {
                setCurrentPlayerPos(sq);
                turn++;
            }
        }
        return flag;
    }

    public void executeAIMove() {
        String aiMove = null;
        if(getTurn() == this.ai.getID())
            aiMove = this.ai.generateMove(board.graph, board.walls);
        if(aiMove != null) {
            if(aiMove.length() == 2) {
                Square sq = new Square(aiMove);
                movePawnOnBoard(getTurn(), sq);
            }
            else if(aiMove.length() == 3) {
                Wall wall = new Wall(aiMove);
                wallMoveProcess(wall);
            }
        }
    }

    public void AIvsAI() {
        while(!gameOver())
            executeAIMove();
    }
//    public int toNumeric()
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
