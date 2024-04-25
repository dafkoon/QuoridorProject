package Controller;

import Model.Player;
import Model.Square;
import Model.Wall;


import java.util.ArrayList;
import java.util.List;

import static Utilities.BFS.calculateBFS;
import static Utilities.Constants.BOARD_DIMENSION;

/**
 * This class contains the algorithm to decide a move for the AI player.
 */
public class AI {

    private final GameRules gameRules;
    private final ViewUpdater viewUpdater;
    private final int agentID;
    private final Player agentPlayer;
    private final Player opponentPlayer;
    private List<Square>[] graphOfBoard;
    private final ArrayList<Square> totalPath = new ArrayList<>();


    /**
     * Constructs a new AI object with the specified ID, game rules gameManager, and game view.
     *
     * @param id          The ID of the AI agent.
     * @param gameRules The game rules class.
     */
    public AI(ViewUpdater viewUpdater, GameRules gameRules, int id) {
        this.agentID = id;
        this.gameRules = gameRules;
        this.viewUpdater = viewUpdater;

        this.agentPlayer = gameRules.getPlayer(id);
        this.opponentPlayer = gameRules.getPlayer((id + 1) % 2);


    }

    /**
     * Initiates the AI's turn, making decisions based on the game state.
     */
    public void AiTurn() {
        setGraphOfBoard(gameRules.getBoard().graph);
        if (agentPlayer.getWallsLeft() == 0)
            takeShortestPath();
        else
            compareBetweenPaths();
    }

    /**
     * Initiates the AI's move by taking the s
     * shortest path to the destination.
     */
    private void takeShortestPath() {
        ArrayList<Square> path = calculatePath(agentPlayer, opponentPlayer.getPosition());
        makeMove(path.get(1).toString()); // Index 0 is the player's current position.
    }

    /**
     * Calculates the path for the movingPlayer to take.
     *
     * @param movingPlayer   The player for whom the path is being calculated.
     * @param occupiedSquare The square that is occupied by the other player.
     * @return The calculated path.
     */
    private ArrayList<Square> calculatePath(Player movingPlayer, Square occupiedSquare) {
        ArrayList<Square> shortestPath = calculateBFS(getGraphOfBoard(), movingPlayer.getPosition(), movingPlayer.getDestRow());
        if (shortestPath.contains(occupiedSquare) && shortestPath.size() <= 2) { // The other player is sitting on the destination row.
            shortestPath.remove(occupiedSquare); // Remove the square

            // Calculate the potential pawn moves.
            ArrayList<Square> potentialMoves = generatePawnMoves(movingPlayer.getPosition());
            for(Square square: potentialMoves) {
                // From the potential moves look for one that is on the destination row.
                if(square.getRow() == movingPlayer.getDestRow())
                    shortestPath.add(square);
            }
            if(shortestPath.size() == 1) {
                // Wasn't able to add a square which is on the destination row.
                shortestPath.add(potentialMoves.get(0));
            }
        }
        // The occupied square is somewhere along the path - A jump is needed.
        if (shortestPath.contains(occupiedSquare) && shortestPath.size() > 2) {
            shortestPath.remove(occupiedSquare);

            // A check is needed to see if the jump is a direct jump (no back wall)
            // or indirect jump (back wall), in that case the player needs to check to one side of the occupied square.
            if (!gameRules.isValidTraversal(shortestPath.get(0), shortestPath.get(1))) {
                // If enters - indirect jump.
                ArrayList<Square> possibleMoves = generatePawnMoves(movingPlayer.getPosition());
                ArrayList<Square> currentPath;
                int minPathLength = Integer.MAX_VALUE;
                for (Square firstMove : possibleMoves) {
                    // Search for the shortest path from all the possible squares.
                    currentPath = calculateBFS(getGraphOfBoard(), firstMove, movingPlayer.getDestRow());
                    if (currentPath.size() < minPathLength) {
                        minPathLength = currentPath.size();
                        shortestPath = currentPath;
                    }
                }
                // Add the movingPlayer's square because short path was calculated from the first move in the alternate path.
                shortestPath.add(0, movingPlayer.getPosition());
            }
        }
        return shortestPath;
    }

    /**
     * Method to check if the agent is the starting player.
     *
     * @return True if the agent is the starting player, false otherwise.
     */
    private boolean isAgentStarting() {
        return gameRules.getStartingPlayer() == agentID;
    }

    /**
     * Method to check if the game is after the fifth round.
     *
     * @return True if it's after the fifth round, false otherwise.
     */
    private boolean isAfterFifthRound() {
        return gameRules.getMoveNum() > 10;
    }

    /**
     * Method to check if the opponent placed a wall.
     *
     * @return True if the opponent placed a wall, false otherwise.
     */
    private boolean opponentPlacedWall() {
        return opponentPlayer.getWallsLeft() < 10;
    }

    /**
     * Method to check if the opponent is close to its destination compared to the agent.
     *
     * @param agentPathLength    The length of the agent path.
     * @param opponentPathLength The length of the opponent path.
     * @param extraForOpponent   Additional steps considered for the opponent's path.
     * @return True if the opponent is closer, false otherwise.
     */
    private boolean isOpponentCloser(int agentPathLength, int opponentPathLength, int extraForOpponent) {
        return agentPathLength > opponentPathLength + extraForOpponent;
    }


    /**
     * Method that determines whether a "head start" for the opponent is needed.
     * The "head start" is for handling the case that the opponent is the starting player which means
     * that from the start of the game it will be close to its goal.
     * In that case the agent shouldn't just try to block the opponent from the start, so it how much closer can
     * the opponent be until it's a real problem.
     *
     * @return 2 if the opponent is the starting player and 5th round not yet reached,
     * or opponent is the starting player, but it placed a wall already.
     */
    private int decideOnOpponentHeadStart() {
        if (isAgentStarting() || (!isAgentStarting() && isAfterFifthRound()) || (!isAgentStarting() && opponentPlacedWall()))
            return 0;
        return 2;
    }

    /**
     * Calculates shortest paths for agent/opponent and head start for opponent and decides who's considered closer to goal.
     */
    private void compareBetweenPaths() {
        ArrayList<Square> agentPath = calculatePath(agentPlayer, opponentPlayer.getPosition());
        ArrayList<Square> opponentPath = calculatePath(opponentPlayer, agentPlayer.getPosition());

        int opponentHeadStart = decideOnOpponentHeadStart();

        if (isOpponentCloser(agentPath.size(), opponentPath.size(), opponentHeadStart)) {
            // Opponent is considered closer to destination.
            if(blockOpponent(opponentPath)) // A wall to block the opponent was found
                return;
        }
        checkIfOpponentHasWalls(agentPath, opponentPath);
    }

    /**
     * Searches for a wall that stands in the path of the opponent.
     * Tries to minimize the value of AgentBFS-OpponentBFS,
     *
     * @param opponentPath Path of the opponent.
     * @return True if a wall is successfully placed, false otherwise.
     */
    private boolean blockOpponent(ArrayList<Square> opponentPath) {
        ArrayList<Wall> increasePathWalls;
        int minDifference = Integer.MAX_VALUE;
        int newOpponentPath = 0, newAgentPath, newPathDifference;
        Wall bestWall = null;

        // Search for the wall that has minimum value for AgentBFS-OpponentBFS.
        // If agent's path length gets longer (bad) the number gets bigger.
        // If opponent's path length gets longer (good) the number gets smaller.
        // In other words, trying to find the wall that increased the most for the opponent while not lengthening the agent's by as much.
        for(int i = 0; i < opponentPath.size() - 1; i++) {
            increasePathWalls = getWallsBetween(opponentPath.get(i), opponentPath.get(i+1));
            for(Wall wall : increasePathWalls) {
                addVirtualWall(wall);
                newAgentPath = calculatePath(agentPlayer, opponentPlayer.getPosition()).size();
                newOpponentPath = calculatePath(opponentPlayer, agentPlayer.getPosition()).size();
                newPathDifference = newAgentPath - newOpponentPath;
                if (newPathDifference < minDifference) {
                    minDifference = newAgentPath - newOpponentPath;
                    bestWall = wall;
                }
                removeVirtualWall(wall);
            }
        }
        if (bestWall == null || opponentPath.size() == newOpponentPath) {
            // Wall not found OR the wall doesn't increase the opponent's path at all.
            return false;
        }
        makeMove(bestWall.toString());
        return true;
    }

    /**
     * Checks if the opponent has walls left and decides whether to take a short path or continue on.
     *
     * @param agentPath    Path of the agent.
     * @param opponentPath Path of the opponent.
     */
    private void checkIfOpponentHasWalls(ArrayList<Square> agentPath, ArrayList<Square> opponentPath) {
        if (opponentPlayer.getWallsLeft() == 0) {
            takeShortestPath();
            return;
        }
        ArrayList<Square> quickPath = searchQuickPath(agentPath, opponentPath);
        if(quickPath != null && blockQuickPath(opponentPath, quickPath)) {
            // A quick path exists and was able to block it.
            return;
        }
        // A quick path was not found / couldn't block it.
        searchForKillerWalls(agentPath, opponentPath);
    }

    /**
     * Searches for a quick path of the opponent based on players' paths.
     *
     * @param agentPath    Path of the agent.
     * @param opponentPath Path of the opponent.
     * @return The quick path of the opponent if found, null otherwise.
     */
    private ArrayList<Square> searchQuickPath(ArrayList<Square> agentPath, ArrayList<Square> opponentPath) {
        boolean foundDeviation = false;
        boolean laneExists = true;
        int width;
        ArrayList<Square> quickPath = new ArrayList<>();
        for (int i = 0; i < opponentPath.size() && laneExists; i++) {
            // Calculate width at square.
            width = calculateWidthAtSquare(opponentPath.get(i));
            if (width <= 2)
                quickPath.add(opponentPath.get(i));

            // If there wasn't a deviation yet, and it's not the first square (player's current square).
            else if (!foundDeviation && i != 0)
                foundDeviation = true;
            else
                laneExists = false;
        }

        if (quickPath.size() < 6) // Too short to be considered a short path.
            return null;

        if (quickPath.size() > opponentPath.size() - 2)
            return null;

        for (Square square : quickPath) { // If agent's path intersect a square in the quick path, it's also the agent's quick path.
            if (agentPath.contains(square))
                return null;
        }
        return quickPath;
    }

    /**
     * A wrapper method for "blockNarrowPath".
     * Checks if the wall from "blockNarrowPath" actually blocks the opponent.
     *
     * @param opponentPath Path of the opponent.
     * @param quickPath    Sub-Path of the opponent path which is the quick path.
     * @return True if was able to successfully blocks the quick path, false otherwise.
     */
    private boolean blockQuickPath(ArrayList<Square> opponentPath, ArrayList<Square> quickPath) {
        // Calculate the index in the opponent's path which starts the quick path.
        Square pathStartSq = quickPath.get(0);
        int indexOfPathStart = opponentPath.indexOf(pathStartSq);

        if(indexOfPathStart == opponentPath.size()-1) { // The start is the end of the path, Can't block after it.
            return false;
        }

        Wall wall = blockNarrowPath(opponentPath, indexOfPathStart);
        if (wall == null)
            return false;

        int opponentPathLen = opponentPath.size();
        addVirtualWall(wall);
        int newOpponentPathLen = calculatePath(opponentPlayer, agentPlayer.getPosition()).size();
        removeVirtualWall(wall);

        // Check if the wall even increase the opponent's path.
        if(newOpponentPathLen > opponentPathLen) {
            makeMove(wall.toString());
            return true;
        }
        return false;
    }

    /**
     * Tries to find a wall that blocks the narrow path of the given player.
     *
     * @param playerPath Path of the player.
     * @param startPathIndex The index from which to start looking.
     * @return A wall that blocks the path, null otherwise.
     */
    private Wall blockNarrowPath(ArrayList<Square> playerPath, int startPathIndex) {
        Square sq1, sq2, sq3, sq4;
        ArrayList<Wall> blockingWalls1 = new ArrayList<>(), blockingWalls2;
        Wall wallThatBlocksPath = null;
        int rowOffsetDirection = 0;

        // Calculates the direction on which the path goes, up or down.
        int verticalDirection = playerPath.get(startPathIndex+1).getRow()-playerPath.get(startPathIndex).getRow();
        if(verticalDirection == 0) { // Moves Horizontally.
            return null;
        }

        // Calculates the first square in the path and the one after it.
        Square pathStart = playerPath.get(startPathIndex);
        Square afterPathStartSq = playerPath.get(startPathIndex + 1);
        int otherLaneOffset = getOtherLaneOffset(pathStart);

        if (otherLaneOffset == 0) { // Lane is 1 column wide.
            // While no walls were found and the squares which walls are calculated from are on the board.
            while (blockingWalls1.isEmpty() && (pathStart.getRow()+rowOffsetDirection >= 0 && pathStart.getRow()+rowOffsetDirection < 9)) {
                // Try to find a wall that blocks passage between sq1 to sq2.
                sq1 = pathStart.neighbor(rowOffsetDirection, 0);
                sq2 = afterPathStartSq.neighbor(rowOffsetDirection, 0);
                blockingWalls1 = getWallsBetween(sq1, sq2);
                rowOffsetDirection += verticalDirection;
            }
            if(!blockingWalls1.isEmpty())
                wallThatBlocksPath = blockingWalls1.get(0);

        } else {
            // In this case the lane is 2 columns wide and the "otherLaneOffset" gives an offset of -1/1 to the column to get to that other column in the path.
            // Try to find a wall that blocks both columns in the narrow path.
            boolean found = false;
            while (!found && (pathStart.getRow()+rowOffsetDirection >= 0 && pathStart.getRow()+rowOffsetDirection < 9)) {

                // Squares on the other lane of the narrow path.
                sq1 = pathStart.neighbor(rowOffsetDirection, otherLaneOffset);
                sq2 = afterPathStartSq.neighbor(rowOffsetDirection, otherLaneOffset);

                // Squares on the lane that the player is on.
                sq3 = pathStart.neighbor(rowOffsetDirection, 0);
                sq4 = afterPathStartSq.neighbor(rowOffsetDirection, 0);

                blockingWalls1 = getWallsBetween(sq1, sq2);
                blockingWalls2 = getWallsBetween(sq3, sq4);

                for (Wall wall : blockingWalls1) { // Find common wall that blocks both lanes in quick path.
                    if (blockingWalls2.contains(wall)) {
                        wallThatBlocksPath = wall;
                        found = true;
                    }
                }
                rowOffsetDirection += verticalDirection;
            }
        }
        return wallThatBlocksPath;
    }

    /**
     * Calculates the column offset to reach the other lane/column in a narrow path.
     * For example if the src is on the right side of the narrow path it will have a left lane.
     * @param src Square which is located on one lane.
     * @return A number which is used as offset to get the other lane.
     */
    private int getOtherLaneOffset(Square src) {
        int index = src.toIndex();
        List<Square>[] graph = getGraphOfBoard();

        Square leftLane = src.neighbor(0, -1);
        Square rightLane = src.neighbor(0, 1);

        // Check which square is in the graph because the method neighbor doesn't calculate if the square exists on the board.
        if(graph[index].contains(leftLane))
            return -1;
        if(graph[index].contains(rightLane))
            return 1;
        return 0;

    }


    /**
     * Searches and tries to block killer walls: walls that drastically increase the path length for the agent.
     *
     * @param agentPath    The path of the agent.
     * @param opponentPath The path of the opponent.
     */
    private void searchForKillerWalls(ArrayList<Square> agentPath, ArrayList<Square> opponentPath) {
        boolean blockedKillerWalls;
        ArrayList<Wall> walls = drasticallyIncreasePath(agentPlayer, agentPath, opponentPath.get(0));
        ArrayList<Wall> killerWalls = new ArrayList<>();

        // Check for every wall if it's really a killer wall.
        for(Wall wall : walls) {
            if(isReallyKillerWall(wall))
                killerWalls.add(wall);
        }
        if(killerWalls.isEmpty()) {
            takeShortestPath();
        } else {
            // Call method and if the return value is false that means no wall that blocks ALL killer wall was found.
            blockedKillerWalls = blockKillerWalls(killerWalls);
            if(!blockedKillerWalls) {
                // Take alternate path around a killer wall.
                addVirtualWall(killerWalls.get(0));
                ArrayList<Square> alternatePath = calculatePath(agentPlayer, opponentPath.get(0));
                removeVirtualWall(killerWalls.get(0));
                makeMove(alternatePath.get(1).toString());
            }
        }
    }

    /**
     * Tries to find a wall which when placed makes all killer walls illegal.
     *
     * @param killerWalls A list of all killer walls.
     * @return True if such wall is found, false otherwise.
     */
    private boolean blockKillerWalls(ArrayList<Wall> killerWalls) {
        Wall blockingWall = null;
        for(int i = 0; i < killerWalls.size() && blockingWall == null; i++) { // Iterate over all the killerWalls.
            boolean blocksAllKillerWalls = true;

            addVirtualWall(killerWalls.get(i));
            ArrayList<Wall> interferingWalls = getIllegalWalls(); // Calculate the walls that are illegal when killerWall is placed (one wall, not the full list).
            removeVirtualWall(killerWalls.get(i));

            for (Wall interferingWall : interferingWalls) { // Iterate over all the illegal walls.
                addVirtualWall(interferingWall);
                for (int j = 0; j < killerWalls.size() && blocksAllKillerWalls; j++) { // Iterate over all the killer walls and check if any one them is valid.
                    if (gameRules.isValidWallPlacement(killerWalls.get(j))) {
                        // If one wall out of the killer walls is valid that means that the interfering wall doesn't block all killer walls.
                        blocksAllKillerWalls = false;
                    }
                }
                removeVirtualWall(interferingWall);

                // Check if current interfering wall blocked all killerWalls placements and in itself is valid (without killerWall on board).
                if (blocksAllKillerWalls && gameRules.isValidWallPlacement(interferingWall)) {
                    blockingWall = interferingWall;
                }
            }
        }
        if(blockingWall != null) {
            makeMove(blockingWall.toString());
            return true;
        }
        return false;

    }

    /**
     * Method to check if a "killer wall" is actually a killer wall, does this by calculations of the players' paths when
     * the wall is placed.
     *
     * @param wall A wall to check if its actually killer.
     * @return True if the wall is a killer wall, false otherwise.
     */
    private boolean isReallyKillerWall(Wall wall) {
        addVirtualWall(wall);
        int newAgentPathLength = calculatePath(agentPlayer, opponentPlayer.getPosition()).size();
        int newOpponentPathLength = calculatePath(opponentPlayer, agentPlayer.getPosition()).size();
        removeVirtualWall(wall);
        if (newAgentPathLength <= newOpponentPathLength) {
            // The wall doesn't make it so that the agent's path is longer than the opponent's, it's not a killer wall.
            return false;
        }
        if(newAgentPathLength - newOpponentPathLength <= 2 && newOpponentPathLength > 5) {
            // The wall increases the path of the opponent that means it's in his path as well so;
            // it's not likely that the opponent will place it.
            return false;
        }
        return true;
    }

    /**
     * Checks for every wall that is between prev and next if its legal.
     * The method "blockingCrossingBetween" retrieves a list of the walls that are between the two squares even if they're illegal;
     *
     * @param prev A square
     * @param next A square
     * @return A list of legal walls between prev and next.
     */
    private ArrayList<Wall> getWallsBetween(Square prev, Square next) {
        ArrayList<Wall> walls = blockCrossingBetween(prev, next);
        ArrayList<Wall> legal = new ArrayList<>();
        for (Wall wall : walls) {
            if (gameRules.isValidWallPlacement(wall))
                legal.add(wall);
        }
        return legal;
    }

    /**
     * Calculates every wall that possible to be placed (legal or illegal, any wall without the restriction of other walls)
     * @return A list of all walls that block any player from reaching their goal.
     */
    private ArrayList<Wall> getIllegalWalls() {
        ArrayList<Wall> walls = generateAllWalls();
        ArrayList<Wall> illegals = new ArrayList<>();
        for(Wall wall : walls) {
            if(gameRules.doesBlockPathToGoal(wall))
                illegals.add(wall);
        }
        return illegals;
    }

    /**
     * Blocks the crossing between two squares by placing walls.
     * To go a square in some direction:
     * down - (-1, 0)    up - (1, 0)    left - (0, -1)    right - (0, 1)
     *
     * @param prev The starting square.
     * @param next The destination square.
     * @return The list of walls used to block the crossing.
     */
    private ArrayList<Wall> blockCrossingBetween(Square prev, Square next) {
        int rowDiff = prev.getRow() - next.getRow();
        int colDiff = prev.getCol() - next.getCol();
        ArrayList<Wall> walls = new ArrayList<>();
        Square startingSq1 = null, startingSq2 = null;
        if (rowDiff == 0) { // Moved horizontally
            if (colDiff < 0) { // Moved right
                startingSq1 = prev;
                startingSq2 = startingSq1.neighbor(1, 0); // one up
            }
            if (colDiff > 0) { // Moved left
                startingSq1 = prev.neighbor(0, -1); // one left
                startingSq2 = startingSq1.neighbor(1, 0); // one up
            }
            walls.add(new Wall(startingSq1, 'v'));
            walls.add(new Wall(startingSq2, 'v'));

        }
        if (colDiff == 0) { // Moved vertically
            if (rowDiff > 0) { // Moved down
                startingSq1 = prev.neighbor(-1, 0); // one down
                startingSq2 = startingSq1.neighbor(0, -1); // one left
            }
            if (rowDiff < 0) { // Moved up
                startingSq1 = prev;
                startingSq2 = startingSq1.neighbor(0, -1); // one left
            }
            walls.add(new Wall(startingSq1, 'h'));
            walls.add(new Wall(startingSq2, 'h'));
        }
        ArrayList<Wall> existsInBoard = new ArrayList<>();
        for(Wall wall: walls) {
            if(gameRules.isValidWallSyntax(wall.toString()))
                existsInBoard.add(wall);
        }
        return existsInBoard;
    }

    /**
     * Calculates the width at a square.
     *
     * @param src The source square.
     * @return The width at the specified square.
     */
    private int calculateWidthAtSquare(Square src) {
        int steps = stepInDirection(src, 1);
        steps += stepInDirection(src, -1);
        return steps + 1;
    }

    /**
     * Moves in a direction from a source square and counts the number of steps.
     *
     * @param src       The source square.
     * @param direction The direction of movement (-1 for left or down, 1 for right or up).
     * @return The number of steps moved in the specified direction.
     */
    private int stepInDirection(Square src, int direction) {
        int steps = 0;
        boolean finished = false;
        Square next, prev = src;

        for (int i = 1; i < BOARD_DIMENSION && !finished; i++) {
            next = src.neighbor(0, direction * i);
            if (next.equals(opponentPlayer.getPosition())) {
                next = src.neighbor(0, direction * ++i);
            }
            if (gameRules.isValidTraversal(prev, next)) {
                prev = next;
                steps++;
            } else
                finished = true;
        }
        return steps;
    }


    /**
     * Places a wall on the board, called virtual because after this method the removal of that wall will be called.
     *
     * @param wall The wall to place.
     */
    private void addVirtualWall(Wall wall) {
        gameRules.addWall(wall);

    }

    /**
     * Removes a wall placed in addVirtualWall.
     *
     * @param wall The wall to remove.
     */
    private void removeVirtualWall(Wall wall) {
        gameRules.removeWall(wall);
    }


    /**
     * Makes a move and updates the view.
     *
     * @param move The move to make.
     */
    private void makeMove(String move) {
        if (move != null) {
            int turn = gameRules.getTurn();
            if (move.length() == 2) {
                Square sq = new Square(move);
                Square before = agentPlayer.getPosition();
                if (gameRules.commitMove(sq.toString())) {
                    // Valid.
                    totalPath.add(before);
                    viewUpdater.updatePawnPosition(sq.getRow(), sq.getCol(), turn);
                }
                else
                    // Invalid.
                    viewUpdater.updatePawnPosition(-1, -1, turn);

            } else if (move.length() == 3) {
                Wall wall = new Wall(move);
                if (gameRules.commitMove(wall.toString())) {
                    int row = wall.getStartingSq().getRow() + 1;
                    int col = wall.getStartingSq().getCol() + 1;
                    if (move.charAt(2) == 'h') // Horizontal wall.
                        viewUpdater.placeHorizontalWalls(row, col, turn);
                    else if (move.charAt(2) == 'v') // Vertical wall.
                        viewUpdater.placeVerticalWall(row, col, turn);
                }
            }
        }
    }

    /**
     * Sets the graph representing the game board.
     *
     * @param graphOfBoard The graph representing the game board.
     */
    private void setGraphOfBoard(List<Square>[] graphOfBoard) {
        this.graphOfBoard = graphOfBoard;
    }


    /**
     * Gets the graph representing the game board.
     *
     * @return The graph representing the game board.
     */
    private List<Square>[] getGraphOfBoard() {
        return this.graphOfBoard;
    }

    /**
     * Generates valid pawn moves from the given source square.
     *
     * @param src The source square.
     * @return An ArrayList of valid pawn moves.
     */
    private ArrayList<Square> generatePawnMoves(Square src) {
        ArrayList<Square> validMoves = new ArrayList<>();
        for (Square sq : src.neighbourhood(2)) {
            if (gameRules.isValidTraversal(src, sq)) {
                validMoves.add(sq);
            }
        }
        return validMoves;
    }

    /**
     * Generates valid wall moves on the game board.
     *
     * @return An ArrayList of valid wall moves.
     */
    private ArrayList<Wall> generateAllWalls() {
        ArrayList<Wall> walls = new ArrayList<>();
        for (int row = 0; row < BOARD_DIMENSION - 1; row++) {
            for (int col = 0; col < BOARD_DIMENSION - 1; col++) {
                Square sq = new Square(row, col);
                Wall wall = new Wall(sq, Wall.Orientation.HORIZONTAL);
                if(!gameRules.doesIntersectOtherWalls(wall)) {
                    walls.add(wall);
                }
            }
        }
        for (int row = 1; row < BOARD_DIMENSION; row++) {
            for (int col = 0; col < BOARD_DIMENSION - 1; col++) {
                Square sq = new Square(row, col);
                Wall wall = new Wall(sq, Wall.Orientation.VERTICAL);
                if(!gameRules.doesIntersectOtherWalls(wall)) {
                    walls.add(wall);
                }
            }
        }
        return walls;
    }

    /**
     * Calculates the walls that increase a player's path by 4 or more moves.
     *
     * @param player Player for which to calculate.
     * @param playerPath Player's path.
     * @param occupiedSquare Square that is occupied by the other player.
     * @return A list of all walls that increase the player's path by 4 or more moves.
     */
    private ArrayList<Wall> drasticallyIncreasePath(Player player, ArrayList<Square> playerPath, Square occupiedSquare) {
        ArrayList<Wall> possibleWalls = generateAllWalls();
        ArrayList<Wall> wallsToIncreasePath = new ArrayList<>();
        int originalPlayerPathLength = playerPath.size();
        int newPlayerPathLength;
        for(Wall wall: possibleWalls) {
            if(gameRules.isValidWallPlacement(wall)) {
                addVirtualWall(wall);
                newPlayerPathLength = calculatePath(player, occupiedSquare).size();
                if(newPlayerPathLength >= originalPlayerPathLength + 4)
                    wallsToIncreasePath.add(wall);
                removeVirtualWall(wall);
            }
        }
        return wallsToIncreasePath;
    }
}
