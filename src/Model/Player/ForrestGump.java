package Model.Player;

import Model.Gamestate.GameSession;
import Model.Gamestate.Square;
import Model.Gamestate.Board;
import Model.Gamestate.Wall;
import sun.awt.image.ImageWatched;

import java.util.*;

public class ForrestGump{
    public static final int BOARD_DIMENSION = 9;
    private String move;
    private int destRow;
    private List<Square> shortestPath;
    private Board board;

    public ForrestGump(int destRow) {
        this.destRow = destRow;
        this.shortestPath = new LinkedList<Square>();
    }

    public Square makeMove(List<Square>[] currentGraph, Square src) {
        shortestPath = shortestPathToRow(currentGraph, src, this.destRow);
        System.out.println("ai moves: " + shortestPath.get(0));
        return shortestPath.get(0);
    }

    public List<Square> shortestPathToRow(List<Square>[] currentGraph, Square src, int destRow) {
        System.out.println(src + "  src");
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

    public int squareToIndex(Square sq) {
        int sq_row = sq.getRow();
        int sq_col = sq.getCol();
        return sq_row*BOARD_DIMENSION+sq_col;
    }
}
