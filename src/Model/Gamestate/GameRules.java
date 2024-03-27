package Model.Gamestate;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * Represents a game session consisting of 2 players.
 */
public class GameRules {
    private static GameRules instance = null;

    public static final int TILE_SIZE = 50;
    public static final int BOARD_DIMENSION = 9;
    public static final int BOARD_SIZE = TILE_SIZE*BOARD_DIMENSION;
    public static final int MAX_PLAYERS = 2;
    private final Player[] players = new Player[MAX_PLAYERS];
    private final Board board = new Board();
    private final Stack<MoveInfo> moveStack = new Stack<>();
    private final int startingPlayer;
    private int turn;
    private static int headstart = 0;

    public GameRules(int startingPlayer) {
        this.startingPlayer = startingPlayer;
        this.turn = startingPlayer;
    }


    public void addPlayer(String name, String color, Square pos, int destRow, int id){
        Player player = new Player(name, color, pos, destRow);
        players[id] = player;
    }

    public boolean doesWallBlockGoal(Wall wall) {
        return !board.doesWallIntersectOther(wall) && board.doesWallCompletelyBlock(wall, players[0], players[1]);
    }

    public boolean isLegalWallPlacement(String squareLocation, boolean isHorizontal) {
        char orientation = isHorizontal ? 'h' : 'v';
        Square sq = new Square(squareLocation);
        Wall wall = new Wall(sq, orientation);
        return isValidWallPlacement(wall);
    }

    public boolean placeWall(Wall wall) {
        boolean isHorizontal = (wall.toString().charAt(2) == 'h');
        int row = wall.getStartingSq().getRow();
        int col = wall.getStartingSq().getCol();
        if(isHorizontal) {
            if(col == BOARD_DIMENSION-1 || players[getTurn()].getWallsLeft() == 0) {
                return false;
            } else if(!isLegalWallPlacement(wall.toString(), true)) {
                System.out.println("There is already a wall here.");
                return false;
            } else
                return commitMove(wall.toString());
        }
        else {
            if(row == 0 || players[getTurn()].getWallsLeft() == 0) {
                return false;
            } else if(!isLegalWallPlacement(wall.toString(), false)) {
                System.out.println("There is already a wall here");
                return false;
            } else
                return commitMove(wall.toString());
        }
    }

    public boolean gameOver() {
        return players[0].getPos().getRow() == players[0].getDestRow() || players[1].getPos().getRow() == players[1].getDestRow();
    }

    private boolean isValidMoveSyntax(String move) {
        Pattern p = Pattern.compile("[a-h][2-9][hv]?");
        Matcher m = p.matcher(move);
        return m.matches();
    }
    private boolean isWallMove(String move) {
        return isValidMoveSyntax(move) && move.length() == 3;
    }

    public boolean isValidWallPlacement(Wall wall) {
        if(players[getTurn()].getWallsLeft() <= 0 || !isWallMove(wall.toString()))
            return false;
        return board.isValidWallPlacement(wall, players[0], players[1]);
    }
    public void addWallToBoard(Wall wall) {
        board.addWall(wall);
        players[getTurn()].decWalls();
    }

    public void removeWallFromBoard(Wall wall) {
        board.removeWall(wall);
        players[getTurn()].incWalls();
    }
    public boolean isValidTraversal(Square from, Square dest) {
        return board.isValidTraversal(dest, from, getOtherPlayerPos());
    }

    public void movePlayerToSquare(Square sq) {
        players[getTurn()].setPos(sq);
    }

    public ArrayList<Square> generatePawnMoves(Square src) {
        ArrayList<Square> validMoves = new ArrayList<>();
        for (Square sq: src.neighbourhood(2)) {
            if (isValidTraversal(src, sq)) {
                validMoves.add(sq);
            }
        }
        return validMoves;
    }

    public boolean commitMove(String move) {
        boolean flag = gameOver();
        if(flag) // Game over.
            return false;
        if (isWallMove(move)) {
            Wall wall = new Wall(move);
            flag = isValidWallPlacement(wall); // Check validity of wall move.
            if (flag) {
                addWallToBoard(wall); // Update player walls and add wall to walls list.
                moveStack.add(new MoveInfo(turn, wall.toString()));
                if(headstart != 0) {
                    headstart--;
                }
                else
                    turn++;
            }
        }
        if(move.length() == 2) {
            Square dest = new Square(move);
            Square from = getCurrentPlayerPos();
            flag = isValidTraversal(from, dest);
            if (flag) {
                movePlayerToSquare(dest);
                moveStack.add(new MoveInfo(turn, dest.toString()));
                if(headstart != 0) {
                    headstart--;
                }
                else
                    turn++;
            }
        }
        return flag;
    }



    public int boardToPixel(int boardIndex) { return boardIndex*TILE_SIZE; }
    public int pixelToBoard(double pixel) {
        return (int)(pixel+ TILE_SIZE/2)/TILE_SIZE;
    }
    public String getCurrentPlayerName() {
        return players[getTurn()].getName();
    }
    public String getCurrentPlayerColor() {
        return players[getTurn()].getColor();
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

    public List<Square>[] getBoardGraph() { return this.board.graph; }
    public List<Wall> getBoardWalls() { return this.board.walls; }

    public Player getPlayer(int id) {
        return players[id];
    }
    public Player[] getPlayers() { return players; }

    public int getStartingPlayer() {
        return this.startingPlayer;
    }

    public int getMoveNum() {
        return moveStack.peek().getMoveNum();
    }

}
