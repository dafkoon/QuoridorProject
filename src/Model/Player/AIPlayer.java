package Model.Player;
import Model.Gamestate.GameSession;
import Model.Gamestate.Player;
import Model.Gamestate.Square;
import Model.Gamestate.Wall;
import View.Game;

import java.util.*;

public class AIPlayer {
    public static final int BOARD_DIMENSION = 9;
    private GameSession gameSession;
    private List<Square>[] gameGraph;
    private Player player;
    private Square move;
    private int destRow;

    private List<Square>[] getGameGraph() { return this.gameGraph; }
    private void setGameGraph(List<Square>[] gameGraph) { this.gameGraph = gameGraph; }


    public AIPlayer(int destRow) {

    }
    public AIPlayer(Player player, int destRow, GameSession gameSession) {
        this.player = player;
        this.destRow = destRow;
        this.gameSession = gameSession;
    }

    public String generateMove(List<Square>[] currentGraph, Square playerSquare, Square otherPlayer) {
        setGameGraph(currentGraph);
        if(this.player.getWallsLeft() > 0) {
            move = shortestPath(playerSquare, otherPlayer).get(0);

        }
        else {
            move = bestMove(currentGraph, playerSquare, otherPlayer);
        }
        return move.toString();
    }

    public List<Square> shortestPath(Square src, Square otherPlayer) {
        List<Square>[] graph = getGameGraph();
        List<Square> path = new LinkedList<>();
        Queue<Square> queue = new LinkedList<>();
        HashMap<Square, Square> parentNode = new HashMap<>();

        queue.add(src);
        parentNode.put(src, null);
        while (!queue.isEmpty()) {
            Square curr = queue.poll();

            if (curr.getRow() == this.destRow) {
                while (!curr.equals(src)) {
                    path.add(curr);
                    curr = parentNode.get(curr);
                }
                if(path.contains(otherPlayer) && path.size() == 1) {
                    path = generatePawnMoves(curr);
                }
                if(path.contains(otherPlayer) && path.size() > 1) {
                    path.remove(otherPlayer);
                }
                Collections.reverse(path);
                return path;
            }
            int i = squareToIndex(curr);
            for (Square e : graph[i]) {
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

    public List<Square> generatePawnMoves(Square playerPos) {
        List<Square> validMoves = new LinkedList<Square>();
        for (Square sq:playerPos.neighbourhood(2)) {
            if (gameSession.isValidTraversal(sq)) {
                validMoves.add(sq);
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
