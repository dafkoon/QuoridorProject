package Model.Player;
import Model.Gamestate.GameSession;
import Model.Gamestate.Player;
import Model.Gamestate.Square;
import Model.Gamestate.Wall;
import View.Game;
import com.sun.javafx.binding.OrElseBinding;

import java.util.*;

public class AIPlayer {
    public static final int BOARD_DIMENSION = 9;
    private Player player;
    private Square move;
    private int destRow;
    private GameSession gameSession;


    public AIPlayer(int destRow) {

    }
    public AIPlayer(Player player, int destRow, GameSession gameSession) {
        this.player = player;
        this.destRow = destRow;
        this.gameSession = gameSession;
    }

    public String generateMove(List<Square>[] currentGraph, Square playerSquare, Square otherSquare) {
        if(this.player.getWallsLeft() > 0) {
            move = shortestPath(currentGraph, playerSquare, otherSquare).get(0);
            if(move.equals(otherSquare)) {
                move = makeJump(currentGraph, playerSquare, otherSquare);
            }
        }
        else {
            move = bestMove(currentGraph, playerSquare, otherSquare);
        }
        return move.toString();
    }

    public List<Square> shortestPath(List<Square>[] currentGraph, Square src, Square otherPos) {
        List<Square> path = new LinkedList<Square>();
        Queue<Square> queue = new LinkedList<Square>();
        HashMap<Square,Square> parentNode = new HashMap<Square,Square>();

        queue.add(src);
        parentNode.put(src, null);

        while (!queue.isEmpty()) {
            Square curr = queue.poll();

            if (curr.getRow() == this.destRow) {
                while (!curr.equals(src)) {
                    path.add(curr);
                    curr = parentNode.get(curr);
                }
                Collections.reverse(path);
                return path;
            }
            int i = squareToIndex(curr);
            for (Square e: currentGraph[i]) {
                if (!parentNode.containsKey(e)) {
                    parentNode.put(e, curr);
                    queue.add(e);
                }
            }
        }
        return path;
    }
    public Square bestMove(List<Square>[] currentGraph, Square src, Square other) {
        return new Square();
    }
    public Square makeJump(Square move, Square otherPos) {

    }

    public List<String> generatePawnMoves(Square playerPos) {
        List<String> validMoves = new LinkedList<String>();
        for (Square sq:playerPos.neighbourhood(2)) {
            if (gameSession.isValidTraversal(sq)) {
                validMoves.add(sq.toString());
            }
        }
        return validMoves;
    }

    public List<String> generateWallMoves() {
        List<String> validMoves = new LinkedList<String>();
        for (int i = 0; i < BOARD_DIMENSION ; i++) {
            for (int j = 0; j < BOARD_DIMENSION; j++) {
                Square sq = new Square(i,j);
                for (Wall.Orientation o: Wall.Orientation.values()) {
                    Wall wall = new Wall(sq, o);
                    if (gameSession.isValidWallPlacement(wall)) {
                        validMoves.add(wall.toString());
                    }
                }
            }
        }
        return validMoves;
    }

    public int squareToIndex(Square sq) {
        int sq_row = sq.getRow();
        int sq_col = sq.getCol();
        return sq_row*BOARD_DIMENSION+sq_col;
    }
}
