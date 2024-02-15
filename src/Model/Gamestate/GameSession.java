package Model.Gamestate;

import Model.Player.AIPlayer;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * Represents a game session consisting of 2 players.
 */
public class GameSession {
    public static int MAX_PLAYERS = 2;
    public static int BOARD_DIMENSION = 9;
    private Board board;
    private Player[] players;
    private Stack<Move> moves;
    private Square player0; // HUMAN
    private Square player1; // AI
    private Player winner;
    private int turn;
    private AIPlayer ai;

    public GameSession() {
        this.board = new Board(this);
        this.players = new Player[MAX_PLAYERS];
        this.player0 = new Square("e1"); // HUMAN
        this.player1 = new Square("e9"); // AI
        this.moves = new Stack<Move>();
        this.turn = 0;
    }

    public void addPlayer(String name, String color, int type){
        Player player = new Player(name, color);
        players[type] = player;
        if(type == 1) {
            ai = new AIPlayer(player, this.player0.getRow(), this);
        }
    }

    public boolean gameOver() {
        return player1.getRow() == 0 || player0.getRow() == 8;
    }

    /**
     * Checks if a move is a wall move by checking its syntax validity and length.
     * @param move A move in String notation.
     * @return true if the move is a wall move, false othewise.
     */
    private boolean isWallMove(String move) {
        return isValidSyntax(move) && move.length() == 3;
    }
    /**
     * Checks if a move given by a String is syntax valid using regular expressions.
     * @param move A move in String notation.
     * @return true if the move is syntax valid, false otherwise.
     */
    private boolean isValidSyntax(String move) {
        Pattern p = Pattern.compile("[a-i][0-9][hv]?");
        Matcher m = p.matcher(move);
        return m.matches();
    }

    public boolean isValidWallPlacement(Wall wall) {
        if(currentPlayerNumWalls() <= 0 || wall == null)
            return false;
        return board.isValidWallPlacement(wall);
    }
    public void placeWall(Wall wall) {
        board.placeWall(wall);
        if(currentTurn() == 0)
            players[0].decWalls();
        else
            players[1].decWalls();
    }

    public boolean isValidTraversal(Square dest) {
        return board.isValidTraversal(dest, getCurrentPlayerSquare(), getOtherPlayerSquare());
    }
    public void movePawn(Square sq) {
        if(currentTurn() == 0)
            player0 = sq;
        else
            player1 = sq;
    }

    /**
     * Performs a move given in string notation. Checks type of move and its validity.
     * @param move A move in String notation.
     * @return true if the move was successful, false otherwise.
     */
    public boolean move(String move) {
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
        if(currentTurn() == 0)
            moves.push(new Move(move, players[0]));
        else
            moves.push(new Move(move, players[1]));
        return flag;
    }

    public int currentTurn() {
        return turn%2;
    }
    public Square getCurrentPlayerSquare() { return currentTurn() == 0 ? player0 : player1; }
    public Square getOtherPlayerSquare() { return currentTurn() == 0 ? player1 : player0; }
    public Square getPlayer0Square() { return player0; }
    public Square getPlayer1Square() { return player1; }
    public int currentPlayerNumWalls() {
        if (currentTurn()==0) {
            return players[0].getWallsLeft();
        } else {
            return players[1].getWallsLeft();
        }
    }

    /**
     * Gets the list of all players in the session.
     * @return players in the session.
     */
    public Player[] getPlayers() { return players; }

    /**
     * Gets the player with the specified id (turn of the player).
     * @param id the player to get.
     * @return player with the specified id.
     */
    public Player getPlayer(int id) {
        return players[id];
    }

    /**
     * Gets all previous moves is taken in the session.
     * @return a stack of the moves.
     */
    public Stack<Move> getMoves() { return moves; }
    public List<Square>[] getGraph()  {return board.graph; }

    public String getAIMove() {
        String move = ai.generateMove(getGraph(), this.player1, this.player0);
        if (move(move)) {
            return move;
        }
        else
            return null;
    }
}
