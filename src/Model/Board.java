package Model;
import java.util.*;

public class Board {
// board
    private final int BOARD_DIMENSION = 9;
    public List<Square>[] graph;
    private Set<Wall> walls;

    public Board() {
        graph = new LinkedList[BOARD_DIMENSION*BOARD_DIMENSION];
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
//        for(int row = 0; row < BOARD_DIMENSION*BOARD_DIMENSION; row++) {
//            System.out.println(graph[row].toString());


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

    public void addWall(Wall wall) {
        walls.add(wall);
    }

    public List<Square>[] getBoard() { return graph; }
    public int getSize() { return BOARD_DIMENSION; }


    @Override
    public String toString() {
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < BOARD_DIMENSION * BOARD_DIMENSION; i++) {

            result.append(i).append(" -> ").append(graph[i]).append("\n");
        }
        return result.toString();
    }


}
