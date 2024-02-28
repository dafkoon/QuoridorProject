package Controller;

import Model.Gamestate.Square;

import java.util.*;

import static Model.Gamestate.GameState.BOARD_DIMENSION;

public class Utility {
    public static List<String> shortestPathToRow(List<Square>[] currentGraph, Square srcSq, int destRow) {
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

    public static List<String> shortestPathToPlayer(List<Square>[] currentGraph, Square src, Square dest) {
        List<String> path = new LinkedList<>();
        Queue<Square> queue = new LinkedList<>();
        HashMap<Square, Square> parentNode = new HashMap<>();
        queue.add(src);
        parentNode.put(src, null);

        while (!queue.isEmpty()) {
            Square curr = queue.poll();
            if (curr.equals(dest)) {
                while (!curr.equals(src)) {
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

//    public static int countOpenPathsToGoal(Square currentPos, int goalRow) {
//        int openPaths = 0;
//        List<Square> neighbors = currentPos.neighbourhood(2);
//
//        for (Square neighbor : neighbors) {
//            // Check if neighbor is not blocked and leads towards the goal row
//            if (!neighbor.hasWall() && neighbor.getRow() == currentPos.getRow() + (goalRow - currentPos.getRow() > 0 ? 1 : -1)) {
//                // If neighbor is the goal, count it as a path and stop searching
//                if (neighbor.getRow() == goalRow) {
//                    return 1;
//                }
//
//                // Recursively explore open paths from the neighbor
//                openPaths += countOpenPathsToGoal(neighbor, goalRow);
//            }
//        }
//        return openPaths;
//    }

    public static int squareToIndex(Square sq) {
        int sq_row = sq.getRow();
        int sq_col = sq.getCol();
        return sq_row*BOARD_DIMENSION+sq_col;
    }

}
