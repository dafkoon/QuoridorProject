package Utilities;

import Model.Square;
import static Utilities.Constants.*;

import java.util.*;

/**
 * Abstract class for implementing breadth-first search (BFS) algorithm.
 * Provides functionality to calculate BFS on a graph represented by squares.
 */
public abstract class BFS {

    /**
     * Calculates the shortest path using breadth-first search (BFS) algorithm.
     * @param currentGraph the graph represented as an array of lists of squares
     * @param srcSq the source square from which the search begins
     * @param destRow the row of the destination square
     * @return the shortest path from the source square to a square in the specified destination row
     */
    public static ArrayList<Square> calculateBFS(List<Square>[] currentGraph, Square srcSq, int destRow) {
        ArrayList<Square> path = new ArrayList<>();
        if (currentGraph == null)
            return path;

        Queue<Square> queue = new LinkedList<>();
        HashMap<Square, Square> parentNode = new HashMap<>();
        queue.add(srcSq);
        parentNode.put(srcSq, null);

        while (!queue.isEmpty()) {
            Square curr = queue.poll();
            if (curr != null) {
                if (curr.getRow() == destRow) {
                    while (curr != null) {
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

    /**
     * Converts a square to its corresponding index in the graph array.
     * @param sq the square to convert
     * @return the index of the square in the graph array
     */
    private static int squareToIndex(Square sq) {
        int sq_row = sq.getRow();
        int sq_col = sq.getCol();
        return sq_row * BOARD_DIMENSION + sq_col;
    }
}

