package Utilities;

import Model.Square;

import java.util.*;

/**
 * Abstract class for implementing breadth-first search (BFS) algorithm.
 * Provides functionality to calculate BFS on a graph represented by squares.
 */
public abstract class BFS {

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
                int i = curr.toIndex();
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
}

