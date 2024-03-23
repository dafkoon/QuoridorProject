package Controller;

import Model.Gamestate.Square;
import View.pieces.HorizontalWall;
import View.pieces.VerticalWall;

import java.util.*;

public class ShortestPath {
    public static final int TILE_SIZE = 50;
    public static final int BOARD_DIMENSION = 9;
    public static final int BOARD_SIZE = TILE_SIZE*BOARD_DIMENSION;

    public static ArrayList<Square> shortestPathToRow(List<Square>[] currentGraph, Square srcSq, int destRow) {
        ArrayList<Square> path = new ArrayList<>();
        if (currentGraph == null)
            return path;

        Queue<Square> queue = new LinkedList<>();
        HashMap<Square, Square> parentNode = new HashMap<>();
        queue.add(srcSq);
        parentNode.put(srcSq, null);

        // Perform BFS search
        Square destination = bfsSearch(currentGraph, destRow, queue, parentNode);

        // If destination found, construct the path
        if (destination != null)
            path = constructPath(srcSq, destination, parentNode);
        return path;
    }

    private static Square bfsSearch(List<Square>[] currentGraph, int destRow, Queue<Square> queue, HashMap<Square, Square> parentNode) {
        while (!queue.isEmpty()) {
            Square curr = queue.poll();
            if (curr.getRow() == destRow)
                return curr;

            int i = squareToIndex(curr);
            for (Square e : currentGraph[i]) {
                if (!parentNode.containsKey(e)) {
                    parentNode.put(e, curr);
                    queue.add(e);
                }
            }
        }
        return null; // Destination not found
    }

    private static ArrayList<Square> constructPath(Square srcSq, Square destination, HashMap<Square, Square> parentNode) {
        ArrayList<Square> path = new ArrayList<>();
        Square curr = destination;
        while (!curr.equals(srcSq)) {
            path.add(curr);
            curr = parentNode.get(curr);
        }
        path.add(srcSq);
        Collections.reverse(path);
        return path;
    }




    public static int squareToIndex(Square sq) {
        int sq_row = sq.getRow();
        int sq_col = sq.getCol();
        return sq_row*BOARD_DIMENSION+sq_col;
    }

}
