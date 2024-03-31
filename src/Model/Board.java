package Model;

import static Utilities.BFS.calculateBFS;
import static Utilities.Constants.*;

import java.util.*;

public class Board {
    public List<Square>[] graph;
    public List<Wall> walls;

    public Board() {
        this.graph = new LinkedList[BOARD_DIMENSION*BOARD_DIMENSION];
        this.walls = new LinkedList<>();
        initializeGraph();
    }

    /**
     * Build the adjacency list.
     * Uses a 1D array that contains a LinkedList of type Square, The index represents the Square index.
     * For each Square add its connections (edges) to the LinkedList.
     */
    public void initializeGraph() {
        for(int row = 0; row < BOARD_DIMENSION; row++) {
            for(int col = 0; col < BOARD_DIMENSION; col++) {
                int i = row * BOARD_DIMENSION + col;
                graph[i] = new LinkedList<>();
                if (col > 0)
                    graph[i].add(new Square(row, col - 1)); // left.
//                else
//                    graph[i].add(null);

                if (col < BOARD_DIMENSION - 1)
                    graph[i].add(new Square(row, col + 1)); // right.
//                else
//                    graph[i].add(null);

                if (row > 0)
                    graph[i].add(new Square(row - 1, col)); // up.
//                else
//                    graph[i].add(null);

                if (row < BOARD_DIMENSION - 1)
                    graph[i].add(new Square(row + 1, col)); // down.
//                else
//                    graph[i].add(null);
            }
        }
    }

    public boolean doesWallIntersectOther(Wall wall) {
        if(squareToIndex(wall.startingSq) > graph.length || squareToIndex(wall.startingSq) < 0)
            return true;
        if(wall.getOrientation() == Wall.Orientation.HORIZONTAL) { // Check Horizontal wall not intersecting others.
            return walls.contains(wall) ||
                    walls.contains(wall.neighbor(1, 0, Wall.Orientation.VERTICAL)) || // Through it
                    walls.contains(wall.neighbor(0, -1, Wall.Orientation.HORIZONTAL)) || //
                    walls.contains(wall.neighbor(0, 1, Wall.Orientation.HORIZONTAL));
        }
        else {
            return walls.contains(wall) ||
                    walls.contains(wall.neighbor(-1, 0, Wall.Orientation.HORIZONTAL)) ||
                    walls.contains(wall.neighbor(-1, 0, Wall.Orientation.VERTICAL)) ||
                    walls.contains(wall.neighbor(1, 0, Wall.Orientation.VERTICAL));
        }
    }
    public boolean doesWallCompletelyBlock(Wall wall, Player player0, Player player1) {
        if(wall.getOrientation() == Wall.Orientation.HORIZONTAL) {
            removeEdge(wall.startingSq, wall.startingSq.neighbor(1, 0));
            removeEdge(wall.startingSq.neighbor(0, 1), wall.startingSq.neighbor(1, 1)); //
        }
        else {
            removeEdge(wall.startingSq, wall.startingSq.neighbor(0, 1)); // remove connecting between startingSq and the wall to the left of it
            removeEdge(wall.startingSq.neighbor(-1, 0), wall.startingSq.neighbor(-1, 1)); // remove the connection between squares on the next rank.
        }
        boolean hasPath = hasPathToGoal(player0, player1);
        if(wall.getOrientation() == Wall.Orientation.HORIZONTAL) {
            addEdge(wall.startingSq, wall.startingSq.neighbor(1, 0));
            addEdge(wall.startingSq.neighbor(0, 1), wall.startingSq.neighbor(1, 1));
        }
        else {
            addEdge(wall.startingSq, wall.startingSq.neighbor(0, 1));
            addEdge(wall.startingSq.neighbor(-1, 0), wall.startingSq.neighbor(-1, 1));
        }
        return !hasPath;
    }
    public boolean isValidWallPlacement(Wall wall, Player player0, Player player1) {
        if(squareToIndex(wall.startingSq) > graph.length || squareToIndex(wall.startingSq) < 0)
            return false;
        if(wall.getOrientation() == Wall.Orientation.HORIZONTAL) { // Check Horizontal wall not intersecting others.
            if (walls.contains(wall) ||
                    walls.contains(wall.neighbor(1, 0, Wall.Orientation.VERTICAL)) || // Through it
                    walls.contains(wall.neighbor(0, -1, Wall.Orientation.HORIZONTAL)) || //
                    walls.contains(wall.neighbor(0, 1, Wall.Orientation.HORIZONTAL))) {
                return false;
            }
        }
        else {
            if (walls.contains(wall) ||
                    walls.contains(wall.neighbor(-1, 0, Wall.Orientation.HORIZONTAL)) ||
                    walls.contains(wall.neighbor(-1, 0, Wall.Orientation.VERTICAL)) ||
                    walls.contains(wall.neighbor(1, 0, Wall.Orientation.VERTICAL))) {
                return false;
            }
        }
        if(wall.getOrientation() == Wall.Orientation.HORIZONTAL) {
            removeEdge(wall.startingSq, wall.startingSq.neighbor(1, 0));
            removeEdge(wall.startingSq.neighbor(0, 1), wall.startingSq.neighbor(1, 1)); //
        }
        else {
            removeEdge(wall.startingSq, wall.startingSq.neighbor(0, 1)); // remove connecting between startingSq and the wall to the left of it
            removeEdge(wall.startingSq.neighbor(-1, 0), wall.startingSq.neighbor(-1, 1)); // remove the connection between squares on the next rank.
        }
        boolean hasPath = hasPathToGoal(player0, player1);
        if(wall.getOrientation() == Wall.Orientation.HORIZONTAL) {
            addEdge(wall.startingSq, wall.startingSq.neighbor(1, 0));
            addEdge(wall.startingSq.neighbor(0, 1), wall.startingSq.neighbor(1, 1));
        }
        else {
            addEdge(wall.startingSq, wall.startingSq.neighbor(0, 1));
            addEdge(wall.startingSq.neighbor(-1, 0), wall.startingSq.neighbor(-1, 1));
        }
        return hasPath;
    }
    private void addEdge(Square sq1, Square sq2) {
        int sq1_index = squareToIndex(sq1);
        int sq2_index = squareToIndex(sq2);
        if (sq1_index >= 0 && sq1_index < 81 && sq2_index >= 0 && sq2_index < 81) {
            graph[sq1_index].add(sq2);
            graph[sq2_index].add(sq1);
        }
    }
    private void removeEdge(Square sq1, Square sq2) {
        int sq1_index = squareToIndex(sq1);
        int sq2_index = squareToIndex(sq2);
        if (sq1_index >= 0 && sq1_index < 81 && sq2_index >= 0 && sq2_index < 81) {
            graph[sq1_index].remove(sq2);
            graph[sq2_index].remove(sq1);
        }
    }
    public boolean hasPathToGoal(Player player0, Player player1) {
        return !(calculateBFS(graph, player0.getPos(), player0.getDestRow()).isEmpty() || calculateBFS(graph, player1.getPos(), player1.getDestRow()).isEmpty());
    }
    public void addWall(Wall wall) {
        if(wall.getOrientation() == Wall.Orientation.HORIZONTAL) {
            removeEdge(wall.startingSq, wall.startingSq.neighbor(1, 0));
            removeEdge(wall.startingSq.neighbor(0, 1), wall.startingSq.neighbor(1, 1));
        }
        else {
            removeEdge(wall.startingSq, wall.startingSq.neighbor(0, 1)); // remove connecting between startingSq and the wall to the left of it
            removeEdge(wall.startingSq.neighbor(-1, 0), wall.startingSq.neighbor(-1, 1)); // remove the connection between squares on the next rank.
        }
        walls.add(wall);
    }
    public void removeWall(Wall wall) {
        if(wall.getOrientation() == Wall.Orientation.HORIZONTAL) {
            addEdge(wall.startingSq, wall.startingSq.neighbor(1, 0));
            addEdge(wall.startingSq.neighbor(0, 1), wall.startingSq.neighbor(1, 1)); //
        }
        else {
            addEdge(wall.startingSq, wall.startingSq.neighbor(0, 1)); // remove connecting between startingSq and the wall to the left of it
            addEdge(wall.startingSq.neighbor(-1, 0), wall.startingSq.neighbor(-1, 1)); // remove the connection between squares on the next rank.
        }
        walls.remove(wall);
    }
    public boolean isValidTraversal(Square dest, Square currentPlayerPos, Square otherPlayerPos){
        int currentPlayerSquareIndex = squareToIndex(currentPlayerPos);
        int otherPlayerSquareIndex = squareToIndex(otherPlayerPos);
        if(currentPlayerSquareIndex > graph.length || otherPlayerSquareIndex > graph.length)
            return false;
        if(dest.equals(currentPlayerPos) || dest.equals(otherPlayerPos)) { // If dest equals any of the player's positions.
            return false;
        }
        else if (graph[currentPlayerSquareIndex].contains(dest)) { // If the player's square is connected to dest.
            return true;
        }
        else if(graph[currentPlayerSquareIndex].contains(otherPlayerPos)) { // If square of current is directly connected to square of other square.
            if(graph[otherPlayerSquareIndex].contains(currentPlayerPos.opposite(otherPlayerPos))) {
                return graph[otherPlayerSquareIndex].contains(dest) && currentPlayerPos.isCardinalTo(dest);
            }
            else {
                return graph[otherPlayerSquareIndex].contains(dest); // Other's square is connected to dest.
            }
        }
        return false;
    }


    /**
     * Turns a Square object to its index components which is used to find its location in the adjacency list.
     * for example: new Square(row=4, col=4) --> row*(how many Squares are there in a board) + col = index in array.
     * @param sq Square to get index in adjacency list for.
     * @return The index of the Square in adjacency list.
     */
    public int squareToIndex(Square sq) {
        int sq_row = sq.getRow();
        int sq_col = sq.getCol();
        return sq_row*BOARD_DIMENSION+sq_col;
    }

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < BOARD_DIMENSION * BOARD_DIMENSION; i++) {

            result.append(i).append(" -> ").append(graph[i]).append("\n");
        }
        return result.toString();
    }


}
