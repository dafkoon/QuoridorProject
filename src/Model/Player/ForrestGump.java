package Model.Player;

import Model.Gamestate.GameSession;
import Model.Gamestate.Square;
import Model.Gamestate.Board;
import Model.Gamestate.Wall;
import sun.awt.image.ImageWatched;

import java.util.*;

public class ForrestGump implements AI {
    private String move;
    private int destRow;
    private List<Square> shortestPath;

    public ForrestGump(int destRow) {
        this.destRow = destRow;
        this.shortestPath = new LinkedList<Square>();
    }

    public String decideMove(Board board, Square src) {
        shortestPath = shortestPathToRow(board, src, this.destRow);
        System.out.println(shortestPath);
        return "";
    }

    public List<Square> shortestPathToRow(Board board, Square src, int destRow) {
        List<Square> path = new LinkedList<Square>();
        Queue<Square> queue = new LinkedList<Square>();
        HashMap<Square,Square> parentNode = new HashMap<Square,Square>();
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
}
