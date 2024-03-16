package Model.Gamestate;

import Controller.ShortestPath;

import java.util.*;

public class Board {
    private final int BOARD_DIMENSION = 9;
    public List<Square>[] graph;
    public List<Wall> walls;

    public Board() {
        this.graph = new LinkedList[BOARD_DIMENSION*BOARD_DIMENSION];
        this.walls = new LinkedList<Wall>();
        initializeGraph();
    }

    public Board(Board board) {
        this.graph = new LinkedList[board.graph.length];
        for(int i = 0; i < board.graph.length; i++) {
            this.graph[i] = new LinkedList<>();
            for (Square square : board.graph[i]) {
                this.graph[i].add(new Square(square.getRow(), square.getCol()));
            }
        }
        this.walls = new LinkedList<>(board.walls); // Shallow copy of walls

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
                if(col > 0)
                    graph[i].add(new Square(row, col - 1)); // left.
                if(col < BOARD_DIMENSION -1)
                    graph[i].add(new Square(row, col + 1)); // right.
                if(row > 0)
                    graph[i].add(new Square(row - 1, col)); // up.
                if(row < BOARD_DIMENSION - 1)
                    graph[i].add(new Square(row + 1, col)); // down.
            }
        }
    }
    public boolean isValidWallPlacement(Wall wall, Player player0, Player player1) {

        if(wall.getOrientation() == Wall.Orientation.HORIZONTAL) { // Check Horizontal wall not intersecting others.
//            System.out.println(wall + " " +
//                    wall.neighbor(1, 0, Wall.Orientation.VERTICAL) + " " +
//                    wall.neighbor(0, -1, Wall.Orientation.HORIZONTAL) + " " +
//                    wall.neighbor(0, 1, Wall.Orientation.HORIZONTAL) + "\n\n");
//            System.out.println(wall + " " +
//                    walls.contains(wall.neighbor(1, 0, Wall.Orientation.VERTICAL)) + " " +
//                    walls.contains(wall.neighbor(0, -1, Wall.Orientation.HORIZONTAL) )+ " " +
//                    walls.contains(wall.neighbor(0, 1, Wall.Orientation.HORIZONTAL)) + "\n\n");
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
//        System.out.println(!(ShortestPath.shortestPathToRow(graph, player0.getPos(), player0.getDestRow()).isEmpty() || ShortestPath.shortestPathToRow(graph, player1.getPos(), player1.getDestRow()).isEmpty()));
        return !(ShortestPath.shortestPathToRow(graph, player0.getPos(), player0.getDestRow()).isEmpty() || ShortestPath.shortestPathToRow(graph, player1.getPos(), player1.getDestRow()).isEmpty());
    }
    public void addWall(Wall wall) {
        if(wall.getOrientation() == Wall.Orientation.HORIZONTAL) {
            removeEdge(wall.startingSq, wall.startingSq.neighbor(1, 0));
            removeEdge(wall.startingSq.neighbor(0, 1), wall.startingSq.neighbor(1, 1)); //
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
        if(dest.equals(currentPlayerPos) || dest.equals(otherPlayerPos)) { // If dest equals any of the player's positions.
            return false;
        }
        else if (graph[currentPlayerSquareIndex].contains(dest)) { // If the player's square is connected to dest.
            return true;
        }
        else if(graph[currentPlayerSquareIndex].contains(otherPlayerPos)) { // If square of current is directly connected to square of other.
            if(graph[currentPlayerSquareIndex].contains(currentPlayerPos.opposite(otherPlayerPos))) { // If Square of current contains
                return graph[otherPlayerSquareIndex].contains(dest) && otherPlayerPos.isCardinalTo(dest);
            }
            else {
                return graph[otherPlayerSquareIndex].contains(dest); // Other's square is connected to dest.
            }
        }
        return false;
    }


    public List<Square> shortestPathToRow(Square src, int destRow) {
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
            for (Square e: graph[i]) {
                if (!parentNode.containsKey(e)) {
                    parentNode.put(e, curr);
                    queue.add(e);
                }
            }
        }
        return path;
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
