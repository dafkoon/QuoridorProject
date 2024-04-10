package Model;

import java.util.LinkedList;
import java.util.List;

import static Utilities.BFS.calculateBFS;
import static Utilities.Constants.BOARD_DIMENSION;

/**
 * Represents the game board.
 */
public class Board {
    /** The adjacency list representing the graph of the board. */
    public List<Square>[] graph;
    /** The list of walls placed on the board. */
    public List<Wall> walls;

    /**
     * Initializes a new instance of the Board class.
     * Constructs the board graph and initializes the list of walls.
     */
    public Board() {
        this.graph = new LinkedList[BOARD_DIMENSION * BOARD_DIMENSION];
        this.walls = new LinkedList<>();
        initializeGraph();
    }

    /**
     * Builds the adjacency list representing the board graph.
     * Each index in the graph array corresponds to a square on the board,
     * and the linked list at each index contains neighboring squares.
     */
    public void initializeGraph() {
        for(int row = 0; row < BOARD_DIMENSION; row++) {
            for(int col = 0; col < BOARD_DIMENSION; col++) {
                int i = row * BOARD_DIMENSION + col;
                graph[i] = new LinkedList<>();
                if (col > 0)
                    graph[i].add(new Square(row, col - 1)); // left.
                if (col < BOARD_DIMENSION - 1)
                    graph[i].add(new Square(row, col + 1)); // right.
                if (row > 0)
                    graph[i].add(new Square(row - 1, col)); // up.
                if (row < BOARD_DIMENSION - 1)
                    graph[i].add(new Square(row + 1, col)); // down.
            }
        }
    }

    /**
     * Checks if placing the specified wall is a valid move without blocking the players' paths to their goals.
     * @param wall The wall to check.
     * @param player0 The first player.
     * @param player1 The second player.
     * @return True if the wall placement is valid, false otherwise.
     */
    public boolean isLegalWallPlacment(Wall wall, Player player0, Player player1) {
        if(wall.startingSq.toIndex() >= graph.length || wall.startingSq.toIndex() < 0)
            return false;

        if(wall.getOrientation() == Wall.Orientation.HORIZONTAL) { // Check Horizontal wall not intersecting others.
            if (walls.contains(wall) ||
                    walls.contains(wall.neighbor(1, 0, Wall.Orientation.VERTICAL)) || // Through it
                    walls.contains(wall.neighbor(0, -1, Wall.Orientation.HORIZONTAL)) || //
                    walls.contains(wall.neighbor(0, 1, Wall.Orientation.HORIZONTAL))) {
                return false;
            }
        } else {
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
        } else {
            removeEdge(wall.startingSq, wall.startingSq.neighbor(0, 1)); // remove connecting between startingSq and the wall to the left of it
            removeEdge(wall.startingSq.neighbor(-1, 0), wall.startingSq.neighbor(-1, 1)); // remove the connection between squares on the next rank.
        }
        boolean hasPath = hasPathToGoal(player0, player1);
        if(wall.getOrientation() == Wall.Orientation.HORIZONTAL) {
            addEdge(wall.startingSq, wall.startingSq.neighbor(1, 0));
            addEdge(wall.startingSq.neighbor(0, 1), wall.startingSq.neighbor(1, 1));
        } else {
            addEdge(wall.startingSq, wall.startingSq.neighbor(0, 1));
            addEdge(wall.startingSq.neighbor(-1, 0), wall.startingSq.neighbor(-1, 1));
        }
        return hasPath;
    }

    /**
     * Adds an edge between two squares in the graph.
     * @param sq1 The first square.
     * @param sq2 The second square.
     */
    private void addEdge(Square sq1, Square sq2) {
        int sq1_index = sq1.toIndex();
        int sq2_index = sq2.toIndex();
        if (sq1_index >= 0 && sq1_index < 81 && sq2_index >= 0 && sq2_index < 81) {
            graph[sq1_index].add(sq2);
            graph[sq2_index].add(sq1);
        }
    }

    /**
     * Removes an edge between two squares in the graph.
     * @param sq1 The first square.
     * @param sq2 The second square.
     */
    private void removeEdge(Square sq1, Square sq2) {
        int sq1_index = sq1.toIndex();
        int sq2_index = sq2.toIndex();
        if (sq1_index >= 0 && sq1_index < 81 && sq2_index >= 0 && sq2_index < 81) {
            graph[sq1_index].remove(sq2);
            graph[sq2_index].remove(sq1);
        }
    }

    /**
     * Checks if there is a path from each player's current position to their respective goal positions.
     * @param player0 The first player.
     * @param player1 The second player.
     * @return True if both players have a path to their goal, false otherwise.
     */
    public boolean hasPathToGoal(Player player0, Player player1) {
        return !(calculateBFS(graph, player0.getPosition(), player0.getDestRow()).isEmpty() || calculateBFS(graph, player1.getPosition(), player1.getDestRow()).isEmpty());
    }

    /**
     * Adds a wall to the board.
     * @param wall The wall to add.
     */
    public void addWall(Wall wall) {
        if(wall.getOrientation() == Wall.Orientation.HORIZONTAL) {
            removeEdge(wall.startingSq, wall.startingSq.neighbor(1, 0));
            removeEdge(wall.startingSq.neighbor(0, 1), wall.startingSq.neighbor(1, 1));
        } else {
            removeEdge(wall.startingSq, wall.startingSq.neighbor(0, 1)); // remove connecting between startingSq and the wall to the left of it
            removeEdge(wall.startingSq.neighbor(-1, 0), wall.startingSq.neighbor(-1, 1)); // remove the connection between squares on the next rank.
        }
        walls.add(wall);
    }

    /**
     * Removes a wall from the board.
     * @param wall The wall to remove.
     */
    public void removeWall(Wall wall) {
        if(wall.getOrientation() == Wall.Orientation.HORIZONTAL) {
            addEdge(wall.startingSq, wall.startingSq.neighbor(1, 0));
            addEdge(wall.startingSq.neighbor(0, 1), wall.startingSq.neighbor(1, 1)); //
        } else {
            addEdge(wall.startingSq, wall.startingSq.neighbor(0, 1)); // remove connecting between startingSq and the wall to the left of it
            addEdge(wall.startingSq.neighbor(-1, 0), wall.startingSq.neighbor(-1, 1)); // remove the connection between squares on the next rank.
        }
        walls.remove(wall);
    }

    /**
     * Checks if a traversal to the destination square is valid for the current player, considering the other player's position.
     * @param newSquare The destination square.
     * @param currentSquare The current player's position.
     * @param otherPlayerSquare The other player's position.
     * @return True if the traversal is valid, false otherwise.
     */
    public boolean isLegalTraversal(Square currentSquare, Square newSquare, Square otherPlayerSquare){
        int currentSquareIndex = currentSquare.toIndex();
        int otherPlayerSquareIndex = otherPlayerSquare.toIndex();
        if(currentSquareIndex >= graph.length || otherPlayerSquareIndex >= graph.length) {
            return false;
        }
        if(newSquare.equals(currentSquare) || newSquare.equals(otherPlayerSquare)) {
            // Check if the destination is a square that's already occupied.
            return false;
        } else if (graph[currentSquareIndex].contains(newSquare)) {
            // Check if the destination is connected to the playing player's Square
            return true;
        } else if(graph[currentSquareIndex].contains(otherPlayerSquare)) {
            // Check if the playing player's Square is connected to other player's Square

            if(graph[otherPlayerSquareIndex].contains(currentSquare.opposite(otherPlayerSquare))) {
                // Return a boolean if the destination is connected to other's Square and is adjacent horizontally or vertically.
                // Basically return false if the destination is diagonal to playing player's square.
                return graph[otherPlayerSquareIndex].contains(newSquare) && currentSquare.isCardinalTo(newSquare);
            } else {
                // There is a back wall
                return graph[otherPlayerSquareIndex].contains(newSquare); // Other player's Square is connected to the destination
            }
        }
        return false;
    }


    /**
     * Checks if the specified wall intersects with other walls on the board.
     * @param wall The wall to check.
     * @return True if the wall intersects with other walls, false otherwise.
     */
    public boolean doesWallIntersectOther(Wall wall) {
        if(wall.startingSq.toIndex() >= graph.length || wall.startingSq.toIndex() < 0)
            return true;
        if(wall.getOrientation() == Wall.Orientation.HORIZONTAL) { // Check Horizontal wall not intersecting others.
            return walls.contains(wall) ||
                    walls.contains(wall.neighbor(1, 0, Wall.Orientation.VERTICAL)) || // Through it
                    walls.contains(wall.neighbor(0, -1, Wall.Orientation.HORIZONTAL)) || //
                    walls.contains(wall.neighbor(0, 1, Wall.Orientation.HORIZONTAL));
        } else {
            return walls.contains(wall) ||
                    walls.contains(wall.neighbor(-1, 0, Wall.Orientation.HORIZONTAL)) ||
                    walls.contains(wall.neighbor(-1, 0, Wall.Orientation.VERTICAL)) ||
                    walls.contains(wall.neighbor(1, 0, Wall.Orientation.VERTICAL));
        }
    }

    /**
     * Checks if placing the specified wall completely blocks a path between the two players.
     * @param wall The wall to check.
     * @param player0 The first player.
     * @param player1 The second player.
     * @return True if the wall completely blocks the path, false otherwise.
     */
    public boolean doesWallBlockCompletely(Wall wall, Player player0, Player player1) {
        if(wall.getOrientation() == Wall.Orientation.HORIZONTAL) {
            removeEdge(wall.startingSq, wall.startingSq.neighbor(1, 0));
            removeEdge(wall.startingSq.neighbor(0, 1), wall.startingSq.neighbor(1, 1)); //
        } else {
            removeEdge(wall.startingSq, wall.startingSq.neighbor(0, 1)); // remove connecting between startingSq and the wall to the left of it
            removeEdge(wall.startingSq.neighbor(-1, 0), wall.startingSq.neighbor(-1, 1)); // remove the connection between squares on the next rank.
        }
        boolean hasPath = hasPathToGoal(player0, player1);
        if(wall.getOrientation() == Wall.Orientation.HORIZONTAL) {
            addEdge(wall.startingSq, wall.startingSq.neighbor(1, 0));
            addEdge(wall.startingSq.neighbor(0, 1), wall.startingSq.neighbor(1, 1));
        } else {
            addEdge(wall.startingSq, wall.startingSq.neighbor(0, 1));
            addEdge(wall.startingSq.neighbor(-1, 0), wall.startingSq.neighbor(-1, 1));
        }
        return !hasPath;
    }

    public String toString() {
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < BOARD_DIMENSION * BOARD_DIMENSION; i++)
            result.append(i).append(" -> ").append(graph[i]).append("\n");
        return result.toString();
    }
}
