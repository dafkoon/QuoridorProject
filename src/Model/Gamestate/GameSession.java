package Model.Gamestate;

import View.pieces.Pawn;

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
    private Square player0; // HUMAN
    private Square player1; // AI
    private Player winner;
    private int turn;

    private static int counter;


    public GameSession() {
        this.players = new Player[MAX_PLAYERS];
        this.moves = new Stack<Move>();
        this.walls = new LinkedList<Wall>();
        this.board = new Board();
        this.player0 = new Square("e1");
        this.player1 = new Square("e9");
        this.turn = 0;

    }

    public void addPlayer(String name, String color, int type){
        players[type] = new Player(name, color);
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
                turn++;
                placeWall(wall); // Update player walls and add wall to walls list.
            }
        }
        else {
            Square sq = new Square(move);
            flag = isValidTraversal(sq);
            if (flag) {
                turn++;
                movePawn(sq);
            }
        }
        if(currentTurn() == 0)
            moves.push(new Move(move, players[0]));
        else
            moves.push(new Move(move, players[1]));
        return flag;
    }

    public List<String> generateValidPawnMoves() {
        List<String> validMoves = new LinkedList<String>();
        for (Square sq: getCurrentPlayerSquare().neighbourhood(2)) {
            if (isValidTraversal(sq)) {
                validMoves.add(sq.toString());
            }
        }
        return validMoves;
    }

    public List<String> generateValidWallMoves() {
        List<String> validMoves = new LinkedList<String>();
        for (int i = 0; i < BOARD_DIMENSION ; i++) {
            for (int j = 0; j < BOARD_DIMENSION; j++) {
                Square sq = new Square(i,j);
                for (Wall.Orientation o: Wall.Orientation.values()) {
                    Wall wall = new Wall(sq, o);
                    if (isValidWallPlacement(wall)) {
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

        return !(shortestPathToRow(player0, 8).isEmpty() || shortestPathToRow(player1, 0).isEmpty());
    }

    public List<Square> shortestPathToRow(Square src, int destRow) {
        List<Square> path = new LinkedList<Square>();
        Queue <Square> queue = new LinkedList<Square>();
        HashMap <Square,Square> parentNode = new HashMap<Square,Square>();
        queue.add(src);
        parentNode.put(src, null);
        while (!queue.isEmpty()) {
            Square curr = queue.poll();
            if (curr.getRow() == destRow) {
                while (!curr.equals(src)) {
                    path.add(curr);
                    curr = parentNode.get(curr);
                }
                Collections.reverse(path);
                return path;
            }
            int i = board.squareToIndex(curr);
            for (Square e: board.graph[i]) {
                if (!parentNode.containsKey(e)) {
                    parentNode.put(e, curr);
                    queue.add(e);
                }
            }

        }
        return path;
    }
    public void placeWall(Wall wall) {
        if(wall.getOrientation() == Wall.Orientation.HORIZONTAL) {
            removeEdge(wall.startingSq, wall.startingSq.neighbor(1, 0));
            removeEdge(wall.startingSq.neighbor(0, 1), wall.startingSq.neighbor(1, 1)); //
        }
        else {
            removeEdge(wall.startingSq, wall.startingSq.neighbor(0, 1)); // remove connecting between startingSq and the wall to the left of it
            removeEdge(wall.startingSq.neighbor(1, 0), wall.startingSq.neighbor(1, 1)); // remove the connection between squares on the next rank.
        }
        if(currentTurn() == 0)
            players[0].decWalls();
        else
            players[1].decWalls();
        walls.add(wall);

    }
    private void addEdge(Square sq1, Square sq2) {
        int sq1_index = board.squareToIndex(sq1);
        int sq2_index = board.squareToIndex(sq2);
        if (sq1_index >= 0 && sq1_index < 81 && sq2_index >= 0 && sq2_index < 81) {
            board.graph[sq1_index].add(sq2);
            board.graph[sq2_index].add(sq1);
        }
    }
    private void removeEdge(Square sq1, Square sq2) {
        int sq1_index = board.squareToIndex(sq1);
        int sq2_index = board.squareToIndex(sq2);
        if (sq1_index >= 0 && sq1_index < 81 && sq2_index >= 0 && sq2_index < 81) {
            board.graph[sq1_index].remove(sq2);
            board.graph[sq2_index].remove(sq1);
        }
    }

    public void movePawn(Square sq) {
        if(currentTurn() == 0)
            player0 = sq;
        else
            player1 = sq;
    }

    public int currentTurn() {
        return turn%2;
    }

    public Square getCurrentPlayerSquare() { return currentTurn() == 0 ? player0 : player1; }

    public Square getOtherPlayerSquare() { return currentTurn() == 0 ? player1 : player0; }

    public int currentPlayerNumWalls() {
        if (currentTurn()==0) {
            return players[0].getWallsLeft();
        } else {
            return players[1].getWallsLeft();
        }
    }

    public boolean gameOver() {
        return player1.getRow() == 0 || player0.getRow() == 8;
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


}
