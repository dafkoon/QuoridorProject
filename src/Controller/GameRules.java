package Controller;
import Model.*;

import java.util.ArrayList;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GameRules {
    private final Player[] players;
    private final Board board;

    private int turn;
    private final int startingPlayer;
    private static int moveCounter;
    private final Stack<String> moveStack = new Stack<>();


    /**
     * Constructs the game rules with the specified starting player.
     *
     * @param startingPlayer A number that is used to index the starting player.
     */
    public GameRules(int startingPlayer) {
        int MAX_PLAYERS = 2;
        this.players = new Player[MAX_PLAYERS];
        this.board = new Board();

        this.startingPlayer = startingPlayer;
        this.turn = startingPlayer;
    }

    /**
     * Adds a player to the game.
     *
     * @param playerName     Name of the player to add.
     * @param startingSquare Initial square of the player.
     * @param playerId       ID of the player.
     */
    public void addPlayerData(String playerName, String startingSquare, int playerId) {
        Player player = new Player(playerName, new Square(startingSquare));
        players[playerId] = player;
    }

    /**
     * Processes a move played by a player.
     *
     * @param move A move to process.
     * @return True if the move is legal and was committed, false otherwise.
     */
    public boolean commitMove(String move) {
        if (gameOver()) {
            return false;
        }
        if (isValidWallSyntax(move)) { // Check if the move is a wall move in syntax.
            Wall wall = new Wall(move);
            if (isValidWallPlacement(wall)) {
                addWall(wall);
                moveStack.add(wall.toString());
                updateTurn();
                return true;
            }
        } else if(move.length() == 2) { // It's a traversal move.
            Square newPos = new Square(move);
            Square oldPos = getCurrentPlayerPos();
            if (isValidTraversal(oldPos, newPos, getOtherPlayerPos())) {
                players[getTurn()].setPosition(newPos);
                moveStack.add(newPos.toString());
                updateTurn();
                return true;
            }
        }
        return false;
    }

    public Stack<String> getMoveStack() {
        return moveStack;
    }

    /**
     * Checks if the syntax of the wall string is valid.
     *
     * @param move the move to validate
     * @return true if the move syntax is valid, false otherwise
     */
    public boolean isValidWallSyntax(String move) {
        if (move.length() == 2)
            return false;
        Pattern p = Pattern.compile("([a-h][1-8]h?)|([a-h][2-9]v?)");
        Matcher m = p.matcher(move);
        return m.matches();
    }

    /**
     * Checks if the wall placement is valid.
     *
     * @param startingSquareString A String representation of the StartingSquare of the wall.
     * @param isHorizontal         A boolean flag that is either true or false whether the wall is horizontal or not.
     * @return True if the wall placement is legal, false otherwise.
     */
    public boolean isValidWallPlacement(String startingSquareString, boolean isHorizontal) {
        char orientation = isHorizontal ? 'h' : 'v';
        Square sq = new Square(startingSquareString);
        Wall wall = new Wall(sq, orientation);
        return isValidWallPlacement(wall);
    }

    /**
     * Checks if the wall placement is valid.
     *
     * @param wall The wall to check if is valid.
     * @return True if the wall placement is legal, false otherwise.
     */
    public boolean isValidWallPlacement(Wall wall) {
        if (players[getTurn()].getWallsLeft() <= 0 || !isValidWallSyntax(wall.toString()))
            return false;
        return board.isLegalWallPlacement(wall, players[0], players[1]);
    }

    /**
     * Adds a wall to the game board.
     *
     * @param wall A to add
     */
    public void addWall(Wall wall) {
        board.addWall(wall);
        players[getTurn()].decWalls();
    }

    /**
     * Removes a wall from the game board.
     *
     * @param wall A wall to remove.
     */
    public void removeWall(Wall wall) {
        board.removeWall(wall);
        players[getTurn()].incWalls();
    }

    /**
     * Checks if a traversal from one square to another is valid.
     *
     * @param from The square to move from.
     * @param dest The square to move to.
     * @return True if the traversal is valid, false otherwise
     */
    public boolean isValidTraversal(Square from, Square dest, Square occupiedSquare) {
        return board.isLegalTraversal(from, dest, occupiedSquare);
    }

    /**
     * A method to check if any player reached their goal meaning the game is over.
     *
     * @return True if any player reached their goal, false if neither.
     */
    public boolean gameOver() {
        return players[0].getPosition().getRow() == players[0].getDestRow() || players[1].getPosition().getRow() == players[1].getDestRow();
    }

    private void updateTurn() {
        turn++;
        moveCounter++;
    }

    /**
     * Checks if placing a wall completely blocks a player from reaching their goal.
     *
     * @param wall A wall to check for.
     * @return True if the wall blocks the goal while not intersecting other, false otherwise.
     */
    public boolean doesBlockPathToGoal(Wall wall) {
        return !board.doesWallCrossAnother(wall) && board.doesWallBlockPathToGoal(wall, players[0], players[1]);
    }

    /**
     * Checks if the given wall intersects any other placed walls on board.
     *
     * @param wall A wall to check if it intersects others.
     * @return True if it does, false otherwise.
     */
    public boolean doesWallCrossOthers(Wall wall) {
        return board.doesWallCrossAnother(wall);
    }

    public ArrayList<Wall> getCrossingWalls(Wall wallToCross) {
        ArrayList<Wall> crossingWalls = board.getCrossingWalls(wallToCross);
        ArrayList<Wall> crossingLegalWalls = new ArrayList<>();
        for(Wall wall: crossingWalls) {
            if(isValidWallPlacement(wall))
                crossingLegalWalls.add(wall);
        }
        return crossingLegalWalls;
    }



    /**
     * Checks if both walls intersect each other.
     *
     * @param wall1 One wall.
     * @param wall2 Second wall.
     * @return True if they intersect, false otherwise.
     */
    public boolean doPairIntersect(Wall wall1, Wall wall2) {
        return board.doWallsCrossEachOther(wall1, wall2);
    }

    /**
     * Gets the current turn number of the current player.
     *
     * @return The turn number.
     */
    public int getTurn() {
        return turn % 2;
    }


    /**
     * Gets the position of the current player.
     *
     * @return The position of the current player.
     */
    public Square getCurrentPlayerPos() {
        return getCurrentPlayer().getPosition();
    }

    /**
     * Gets the position of the other player.
     *
     * @return The position of the other player.
     */
    public Square getOtherPlayerPos() {
        return getOtherPlayer().getPosition();
    }

    /**
     * Gets the pointer of the current player.
     *
     * @return The current player.
     */
    public Player getCurrentPlayer() {
        return players[getTurn()];
    }

    /**
     * Gets the pointer of the other player.
     *
     * @return The other player.
     */
    public Player getOtherPlayer() {
        return players[(getTurn() + 1) % 2];
    }

    /**
     * Gets the board graph.
     *
     * @return The board graph.
     */
    public Board getBoard() {
        return this.board;
    }

    /**
     * Gets the player with the specified ID.
     *
     * @param id The ID of the player.
     * @return The player with the specified ID.
     */
    public Player getPlayer(int id) {
        return players[id];
    }

    /**
     * Gets the ID of the starting player.
     *
     * @return The ID of the starting player.
     */
    public int getStartingPlayer() {
        return startingPlayer;
    }

    /**
     * Gets the number of moves made so far.
     *
     * @return The number of moves made.
     */
    public int getMoveNum() {
        return moveCounter;
    }
}
