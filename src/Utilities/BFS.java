package Utilities;

import Model.Square;

import java.util.*;

public abstract class BFS {
    public static final int TILE_SIZE = 50;
    public static final int BOARD_DIMENSION = 9;

    public static ArrayList<Square> calculateBFS(List<Square>[] currentGraph, Square srcSq, int destRow) {
        ArrayList<Square> path = new ArrayList<>();
        if(currentGraph == null)
            return path;
        Queue<Square> queue = new LinkedList<>();
        HashMap<Square, Square> parentNode = new HashMap<>();
        queue.add(srcSq);
        parentNode.put(srcSq, null);

        while (!queue.isEmpty()) {
            Square curr = queue.poll();
            if(curr != null) {
                if (curr.getRow() == destRow) {
                    while (!curr.equals(srcSq)) {
                        path.add(curr);
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
        }
        return path;
    }

    private static int squareToIndex(Square sq) {
        int sq_row = sq.getRow();
        int sq_col = sq.getCol();
        return sq_row*BOARD_DIMENSION+sq_col;
    }
}
