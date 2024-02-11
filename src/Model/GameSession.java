package Model;

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
    private List<Wall> walls;
    private Square player1; // AI
    private Square player2; // HUMAN
    private Player winner;
    private int turn;


    public GameSession() {
        this.players = new Player[MAX_PLAYERS];
        this.moves = new Stack<Move>();
        this.walls = new LinkedList<Wall>();
        this.board = new Board();
        this.player1 = new Square("e9");
        this.player2 = new Square("e1");
        this.turn = 0;
    }

    /**
     * Adds a player to the session.
     * @param player player to add.
     * @throws IllegalStateException if more players than the limit are added.
     * @throws IllegalArgumentException if player is null.
     */
    public void addPlayer(Player player) throws IllegalStateException, IllegalArgumentException{
        if(players.length > MAX_PLAYERS) {
            throw new IllegalStateException("Tried to surpass max amount of player: " + MAX_PLAYERS);
        }
        if(player == null) {
            throw new IllegalArgumentException("Player cannot be null.");
        }
        for(int i = 0; i < players.length; i++) {
            if(players[i] == null)
                players[i] = player;
        }
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
            }
        }
        else {
            Square sq = new Square(move);
            flag = isValidTraversal(sq);
            if (flag) {
                movePawn(sq);
            }
        }
        if(currentTurn() == 0)
            moves.push(new Move(move, players[0]));
        else
            moves.push(new Move(move, players[0]));
        turn++;
        return flag;
    }

    public List<String> generateValidMoves() {

        List<String> validMoves = new LinkedList<String>();
        for(Square sq: getCurrentPlayerSquare().neighbourhood(1)) {
            if(isValidTraversal(sq)) {
                validMoves.add(sq.toString());
            }
        }
        for(int i = 0; i < BOARD_DIMENSION; i++) {
            for(int j = 0; j < BOARD_DIMENSION; j++) {
                Square sq = new Square(i, j);
                for(Wall.Orientation o: Wall.Orientation.values()) {
                    Wall wall = new Wall(sq, o);
                    if(isValidWallPlacement(wall)) {
                        validMoves.add(wall.toString());
                    }
                }
            }
        }
        return validMoves;
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

    /**
     * Checks if a move is a wall move by checking its syntax validity and length.
     * @param move A move in String notation.
     * @return true if the move is a wall move, false othewise.
     */
    private boolean isWallMove(String move) {
        return isValidSyntax(move) && move.length() == 3;
    }

    public boolean isValidWallPlacement(Wall wall) {
        if(currentPlayerNumWalls() <= 0)
            return false;
        if(wall.getOrientation() == Wall.Orientation.HORIZONTAL) { // Check Horizontal wall not intersecting others.
            System.out.println(wall + " " + wall.neighbor(0, 0, Wall.Orientation.VERTICAL) + " " +
                    wall.neighbor(0, -1, Wall.Orientation.HORIZONTAL) + " " +
                    wall.neighbor(0, 1, Wall.Orientation.HORIZONTAL));
            if (walls.contains(wall) ||
                    walls.contains(wall.neighbor(0, 0, Wall.Orientation.VERTICAL)) || // Through it
                    walls.contains(wall.neighbor(0, -1, Wall.Orientation.HORIZONTAL)) || //
                    walls.contains(wall.neighbor(0, 1, Wall.Orientation.HORIZONTAL))) {
                System.out.println(wall + " intersecting ");
                return false;
            }
        }
        else { // Check Vertical wall not intersecting others.
            if (walls.contains(wall) ||
                    walls.contains(wall.neighbor(0, 0, Wall.Orientation.HORIZONTAL)) ||
                    walls.contains(wall.neighbor(-1, 0, Wall.Orientation.VERTICAL)) ||
                    walls.contains(wall.neighbor(1, 0, Wall.Orientation.VERTICAL))) {
                System.out.println(wall + " intersecting ");
                return false;
            }
        }
        if(wall.getOrientation() == Wall.Orientation.HORIZONTAL) {
            removeEdge(wall.startingSq, wall.startingSq.neighbor(1, 0));
            removeEdge(wall.startingSq.neighbor(0, 1), wall.startingSq.neighbor(1, 1)); //
        }
        else {
            removeEdge(wall.startingSq, wall.startingSq.neighbor(0, 1)); // remove connecting between startingSq and the wall to the left of it
            removeEdge(wall.startingSq.neighbor(1, 0), wall.startingSq.neighbor(1, 1)); // remove the connection between squares on the next rank.
        }
        boolean hasPath = hasPathToGoal();
        if(wall.getOrientation() == Wall.Orientation.HORIZONTAL) {
            addEdge(wall.startingSq, wall.startingSq.neighbor(1, 0));
            addEdge(wall.startingSq.neighbor(0, 1), wall.startingSq.neighbor(1, 1));
        }
        else {
            addEdge(wall.startingSq, wall.startingSq.neighbor(0, 1));
            addEdge(wall.startingSq.neighbor(1, 0), wall.startingSq.neighbor(1, 1));
        }
        return hasPath;
    }

    public boolean isValidTraversal(Square dest) {
        int currentPlayerSquareIndex = board.squareToIndex(getCurrentPlayerSquare());
        int otherPlayerSquareIndex = board.squareToIndex(getOtherPlayerSquare());
        if(dest.equals(getCurrentPlayerSquare()) || dest.equals(getOtherPlayerSquare())) { // If dest equals any of the player's positions.
            return false;
        }
        else if (board.graph[currentPlayerSquareIndex].contains(dest)) { // If the player's square is connected to dest.
            return true;
        }
        else if(board.graph[currentPlayerSquareIndex].contains(getOtherPlayerSquare())) { // If players are adjacent.
            if(board.graph[currentPlayerSquareIndex].contains(getCurrentPlayerSquare().opposite(getOtherPlayerSquare()))) {
                return board.graph[otherPlayerSquareIndex].contains(dest) && getCurrentPlayerSquare().isCardinalTo(dest);
            }
            else {
                return board.graph[otherPlayerSquareIndex].contains(dest); // Other's square is connected to dest.
            }
        }
        return false;
    }

    public boolean hasPathToGoal() {
        return !(BFS(player2, 8).isEmpty() || BFS(player1, 0).isEmpty());
    }

    public List<Square> BFS(Square src, int destRow) {
        List<Square> shortestPath = new LinkedList<Square>();
        Queue <Square> queue = new LinkedList<Square>();
        HashMap<Square, Square> parentNode = new HashMap<Square, Square>();
        queue.add(src);
        parentNode.put(src, null);

        while (!queue.isEmpty()) {
            Square sq = queue.poll();
            if (sq.getRow() == destRow) {
                while (!sq.equals(src)) {
                    shortestPath.add(sq);
                    sq = parentNode.get(sq);
                }
                Collections.reverse(shortestPath);
                return shortestPath;
            }
            int i = board.squareToIndex(sq);
            for (Square e: board.graph[i]) {
                if (!parentNode.containsKey(e)) {
                    parentNode.put(e, sq);
                    queue.add(e);
                }
            }
        }
        return shortestPath;
    }

    public void placeWall(Wall wall) {
        if(currentTurn() == 0)
            players[0].decWalls();
        else
            players[1].decWalls();
        walls.add(wall);

    }

    private void addEdge(Square sq1, Square sq2) {
        int sq1_index = board.squareToIndex(sq1);
        int sq2_index = board.squareToIndex(sq2);

        board.graph[sq1_index].add(sq2);
        board.graph[sq2_index].add(sq1);
    }

    private void removeEdge(Square sq1, Square sq2) {
        int sq1_index = board.squareToIndex(sq1);
        int sq2_index = board.squareToIndex(sq2);
//        System.out.println(sq2 + " " + sq2_index);
//        System.out.println(sq1 + " " + sq1_index);
        if(sq1_index <= 81 && sq2_index <= 81) {
            board.graph[sq2_index].remove(sq1);
            board.graph[sq1_index].remove(sq2);
        }
    }

    public int currentTurn() {
        return turn%2;
    }

    public Square getCurrentPlayerSquare() { return currentTurn() == 0 ? player2 : player1; }

    public Square getOtherPlayerSquare() { return getCurrentPlayerSquare().equals(player1) ? player1 : player2; }

    public int currentPlayerNumWalls() {
        if (currentTurn()==0) {
            return players[0].getWallsLeft();
        } else {
            return players[1].getWallsLeft();
        }
    }

    public void movePawn(Square sq) {
        if(currentTurn() == 0)
            player2 = sq;
        else
            player1 = sq;
    }

    public boolean gameOver() {
        return player1.getRow() == 0 || player2.getRow() == 8;
    }





    public Board getBoard() { return board; }
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

    public List<Wall> getWalls() { return walls; }

}
