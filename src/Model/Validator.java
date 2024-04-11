package Model;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * Manages the game rules and stores data.
 */
public class Validator {
    private static final int MAX_PLAYERS = 2;
    private static int moveCounter;
    private static int headstart = 0;

    private final int startingPlayer;
    private int turn;

    private final Player[] players = new Player[MAX_PLAYERS];
    private final Board board = new Board();

    /**
     * Constructs the game rules with the specified starting player.
     * @param startingPlayer the index of the starting player
     */
    public Validator(int startingPlayer) {
        this.startingPlayer = startingPlayer;
        this.turn = startingPlayer;
    }

    /**
     * Adds a player to the game.
     * @param playerName the name of the player
     * @param startingSquare the initial square of the player
     * @param playerId the ID of the player
     */
    public void addPlayer(String playerName, String startingSquare, int playerId) {
        Player player = new Player(playerName, new Square(startingSquare));
        players[playerId] = player;
    }

    /**
     * Commits a move in the game.
     * @param move the move to commit
     * @return true if the move is legal, false otherwise
     */
    public boolean commitMove(String move) {
        if(gameOver()) {
            return false;
        }
        if (isValidWallSyntax(move)) {
            Wall wall = new Wall(move);
            if (isValidWallPlacement(wall)) {
                addWall(wall);
                updateTurn();
                return true;
            }
        }
        else if(move.length() == 2) {
            Square newPos = new Square(move);
            Square oldPos = getCurrentPlayerPos();
            if (isValidTraversal(oldPos, newPos)) {
                players[getTurn()].setPosition(newPos);
                updateTurn();
                return true;
            }
        }
        return false;
    }

    /**
     * Checks if the wall placement is valid.
     * @param startingSquareString The String that represents the StartingSquare of the wall.
     * @param isHorizontal a boolean flag that is true if the wall is horizontal, false if vertical
     * @return True if the wall placement is legal, false otherwise
     */
    public boolean isValidWallPlacement(String startingSquareString, boolean isHorizontal) {
        char orientation = isHorizontal ? 'h' : 'v';
        Square sq = new Square(startingSquareString);
        Wall wall = new Wall(sq, orientation);
        return isValidWallPlacement(wall);
    }
    /**
     * Checks if the wall placement is valid.
     * @param wall the wall to check
     * @return true if the wall placement is valid, false otherwise
     */
    public boolean isValidWallPlacement(Wall wall) {
        if(players[getTurn()].getWallsLeft() <= 0 || !isValidWallSyntax(wall.toString()))
            return false;
        return board.isLegalWallPlacment(wall, players[0], players[1]);
    }

    /**
     * Checks if the syntax of the wall string is valid.
     * @param move the move to validate
     * @return true if the move syntax is valid, false otherwise
     */
    private boolean isValidWallSyntax(String move) {
        if(move.length() == 2)
            return false;
        Pattern p = Pattern.compile("([a-h][1-8]h?)|([a-h][2-9]v?)");
        Matcher m = p.matcher(move);
        return m.matches();
    }

    /**
     * Adds a wall to the game board.
     * @param wall the wall to add
     */
    public void addWall(Wall wall) {
        board.addWall(wall);
        players[getTurn()].decWalls();
    }

    /**
     * Removes a wall from the game board.
     * @param wall the wall to remove
     */
    public void removeWall(Wall wall) {
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
        return board.isLegalTraversal(from, dest, getOtherPlayerPos());
    }


    public boolean gameOver() {
        return players[0].getPosition().getRow() == players[0].getDestRow() || players[1].getPosition().getRow() == players[1].getDestRow();
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
     * Checks if placing a wall blocks the goal of any player.
     * @param wall the wall to check
     * @return true if the wall blocks the goal, false otherwise
     */
    public boolean doesWallBlockGoal(Wall wall) {
        return !board.doesWallIntersectOther(wall) && board.doesWallBlockCompletely(wall, players[0], players[1]);
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
        return getCurrentPlayer().getPosition();
    }

    /**
     * Gets the position of the other player.
     * @return The position of the other player.
     */
    public Square getOtherPlayerPos() {
        return getOtherPlayer().getPosition();
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
