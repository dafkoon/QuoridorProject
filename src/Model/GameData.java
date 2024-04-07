package Model;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * Manages the game rules and stores data.
 */
public class GameData {
    private static final int MAX_PLAYERS = 2;
    private static int moveCounter;
    private static int headstart = 5;

    /** The index of the starting player. */
    private final int startingPlayer;
    /** The index of the current turn. */
    private int turn;

    /** Array to store the players in the game. */
    private final Player[] players = new Player[MAX_PLAYERS];
    /** The game board. */
    private final Board board = new Board();

    /**
     * Constructs the game rules with the specified starting player.
     * @param startingPlayer the index of the starting player
     */
    public GameData(int startingPlayer) {
        this.startingPlayer = startingPlayer;
        this.turn = startingPlayer;
    }

    /**
     * Adds a player to the game.
     * @param name the name of the player
     * @param color the color of the player
     * @param pos the initial position of the player
     * @param destRow the destination row of the player
     * @param id the ID of the player
     */
    public void addPlayer(String name, String color, Square pos, int destRow, int id){
        Player player = new Player(name, color, pos, destRow);
        players[id] = player;
    }

    /**
     * Checks if placing a wall blocks the goal of any player.
     * @param wall the wall to check
     * @return true if the wall blocks the goal, false otherwise
     */
    public boolean doesWallBlockGoal(Wall wall) {
        return !board.doesWallIntersectOther(wall) && board.doesWallCompletelyBlock(wall, players[0], players[1]);
    }

    /**
     * Checks if a wall placement is legal.
     * @param squareLocation the location of the wall
     * @param isHorizontal true if the wall is horizontal, false if vertical
     * @return true if the wall placement is legal, false otherwise
     */
    public boolean isLegalWallPlacement(String squareLocation, boolean isHorizontal) {
        char orientation = isHorizontal ? 'h' : 'v';
        Square sq = new Square(squareLocation);
        Wall wall = new Wall(sq, orientation);
        return isValidWallPlacement(wall);
    }
    /**
     * Checks if the syntax of the wall string is valid.
     * @param move the move to validate
     * @return true if the move syntax is valid, false otherwise
     */
    private boolean isValidMoveSyntax(String move) {
        Pattern p = Pattern.compile("([a-h][1-8]h?)|([a-h][2-9]v?)");
        Matcher m = p.matcher(move);
        return m.matches();
    }

    /**
     * Checks if the move is a wall move.
     * @param move the move to check
     * @return true if the move is a wall move, false otherwise
     */
    private boolean isWallMove(String move) {
        return isValidMoveSyntax(move) && move.length() == 3;
    }

    /**
     * Checks if the wall placement is valid.
     * @param wall the wall to check
     * @return true if the wall placement is valid, false otherwise
     */
    public boolean isValidWallPlacement(Wall wall) {
        if(players[getTurn()].getWallsLeft() <= 0 || !isWallMove(wall.toString()))
            return false;
        return board.isValidWallPlacement(wall, players[0], players[1]);
    }

    /**
     * Adds a wall to the game board.
     * @param wall the wall to add
     */
    public void addWallToBoard(Wall wall) {
        board.addWall(wall);
        players[getTurn()].decWalls();
    }

    /**
     * Removes a wall from the game board.
     * @param wall the wall to remove
     */
    public void removeWallFromBoard(Wall wall) {
        board.removeWall(wall);
        players[getTurn()].incWalls();
    }

    /**
     * Checks if a traversal from one square to another is valid.
     * @param from the starting square
     * @param dest the destination square
     * @return true if the traversal is valid, false otherwise
     */
    public boolean isValidTraversal(Square from, Square dest) {
        return board.isValidTraversal(dest, from, getOtherPlayerPos());
    }

    /**
     * Moves the current player to a specified square.
     * @param sq the square to move the player to
     */
    public void movePlayerToSquare(Square sq) {
        players[getTurn()].setPos(sq);
        if(players[getTurn()].getDestRow() == sq.getRow())
            System.out.println(players[getTurn()].getName() + " WINNER");
    }

    /**
     * Commits a move in the game.
     * @param move the move to commit
     * @return true if the move is legal, false otherwise
     */
    public boolean commitMove(String move) {
        if(gameOver()) // Game over.
            return false;
        if (isWallMove(move)) {
            Wall wall = new Wall(move);
            if (isValidWallPlacement(wall)) {
                addWallToBoard(wall); // Update player walls and add wall to walls list.
                updateTurn();
                return true;
            }
        }
        else if(move.length() == 2) {
            Square dest = new Square(move);
            Square from = getCurrentPlayerPos();
            if (isValidTraversal(from, dest)) {
                movePlayerToSquare(dest);
                updateTurn();
                return true;
            }
        }
        return false;
    }

    public boolean gameOver() {
        return players[0].getPos().getRow() == players[0].getDestRow() || players[1].getPos().getRow() == players[1].getDestRow();
    }

    private void updateTurn() {
        if (headstart != 0) {
            headstart--;
        } else {
            turn++;
            moveCounter++;
        }
    }

    /**
     * Gets the current turn number of the current player.
     * @return The turn number.
     */
    public int getTurn() {
        return turn%2;
    }

    /**
     * Gets the position of the current player.
     * @return The position of the current player.
     */
    public Square getCurrentPlayerPos() {
        return getCurrentPlayer().getPos();
    }

    /**
     * Gets the position of the other player.
     * @return The position of the other player.
     */
    public Square getOtherPlayerPos() {
        return getOtherPlayer().getPos();
    }

    /**
     * Gets the pointer of the current player.
     * @return The current player.
     */
    public Player getCurrentPlayer() {
        return players[getTurn()];
    }
    /**
     * Gets the pointer of the other player.
     * @return The other player.
     */
    public Player getOtherPlayer() {
        return players[(getTurn()+1)%2];
    }

    /**
     * Gets the board graph.
     * @return The board graph.
     */
    public List<Square>[] getBoardGraph() {
        return this.board.graph;
    }

    /**
     * Gets the player with the specified ID.
     * @param id The ID of the player.
     * @return The player with the specified ID.
     */
    public Player getPlayer(int id) {
        return players[id];
    }

    /**
     * Gets the ID of the starting player.
     * @return The ID of the starting player.
     */
    public int getStartingPlayer() {
        return startingPlayer;
    }

    /**
     * Gets the number of moves made so far.
     * @return The number of moves made.
     */
    public int getMoveNum() {
        return moveCounter;
    }

}
