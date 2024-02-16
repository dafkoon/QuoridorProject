package Controller;
import Model.Gamestate.Square;

import java.util.*;

import static Controller.GameSession.BOARD_DIMENSION;

public class Utility {
    public static List<String> shortestPathBFS(List<Square>[] currentGraph, Square srcSq, int destRow) {
        List<String> path = new LinkedList<>();
        Queue<Square> queue = new LinkedList<>();
        HashMap<Square, Square> parentNode = new HashMap<>();
        queue.add(srcSq);
        parentNode.put(srcSq, null);

        while (!queue.isEmpty()) {
            Square curr = queue.poll();
            if (curr.getRow() == destRow) {
                while (!curr.equals(srcSq)) {
                    path.add(curr.toString());
                    curr = parentNode.get(curr);
                }
                Collections.reverse(path);
                return path;
            }
            int i = squareToIndex(curr);
            for (Square e : currentGraph[i]) {
                if (!parentNode.containsKey(e)) {
                    parentNode.put(e, curr);
                    queue.add(e);
                }
            }
        }
        return path;
    }

    public static int squareToIndex(Square sq) {
        int sq_row = sq.getRow();
        int sq_col = sq.getCol();
        return sq_row*BOARD_DIMENSION+sq_col;
    }
}
