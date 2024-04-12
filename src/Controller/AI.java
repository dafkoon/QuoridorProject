package Controller;

import Model.Player;
import Model.Square;
import Model.Validator;
import Model.Wall;

import java.util.ArrayList;
import java.util.List;

import static Utilities.BFS.calculateBFS;
import static Utilities.Constants.BOARD_DIMENSION;

/**
 * This class contains the algorithm to decide a move for the AI player.
 */
public class AI {

    private boolean handledWeakPoint = false;
    private Wall preventWeakPoint = null;
    private final Validator validator;
    private final ViewUpdater viewUpdater;
    private final int agentID;
    private final Player agentPlayer;
    private final Player opponentPlayer;
    private List<Square>[] boardGraph;

    /**
     * Constructs a new AI object with the specified ID, game rules gameManager, and game view.
     * @param id  The ID of the AI agent.
     * @param gameManager The game rules gameManager.
     */
    public AI(ViewUpdater viewUpdater, Validator gameManager, int id) {
        this.agentID = id;
        this.validator = gameManager;
        this.viewUpdater = viewUpdater;

        this.agentPlayer = gameManager.getPlayer(id);
        this.opponentPlayer = gameManager.getPlayer((id + 1) % 2);
    }



    /**
     * Initiates the AI's turn, making decisions based on the game state.
     */
    public void AiTurn() {
        setBoardGraph(validator.getBoardGraph());
        if (agentPlayer.getWallsLeft() == 0)
            takeShortestPath();
        else
            compareBetweenPaths();
    }

    /**
     * Calculates the path for the movingPlayer to take.
     * @param movingPlayer The player for whom the path is being calculated.
     * @param otherPlayerPos The position of the other player.
     * @return The calculated path.
     */
    private ArrayList<Square> calculatePath(Player movingPlayer, Square otherPlayerPos) {
        ArrayList<Square> shortestPath = calculateBFS(getBoardGraph(), movingPlayer.getPosition(), movingPlayer.getDestRow());
        if(shortestPath.contains(otherPlayerPos) && shortestPath.size() <= 2) { // Opponent is on the destination row.
            shortestPath.remove(otherPlayerPos);
            // Adds to the list the possible moves the pawn has, not a short path anymore because there is only 1 move to win.
            shortestPath.addAll(generatePawnMoves(otherPlayerPos));
        }

        if(shortestPath.contains(otherPlayerPos) && shortestPath.size() > 2) { // Jump is needed somewhere in the path.
            shortestPath.remove(otherPlayerPos);
            if(!validator.isValidTraversal(shortestPath.get(0), shortestPath.get(1))) { // If after removing the opponent's square a jump is possible.
                ArrayList<Square> possibleMoves = generatePawnMoves(movingPlayer.getPosition());
                ArrayList<Square> currentPath;
                int minPathLength = Integer.MAX_VALUE;

                for (Square firstMove : possibleMoves) {
                    // Search for the shortest path from all the alternate paths.
                    currentPath = calculateBFS(getBoardGraph(), firstMove, movingPlayer.getDestRow());
                    if(currentPath.size() < minPathLength) {
                        minPathLength = currentPath.size();
                        shortestPath = currentPath;
                    }
                }
                // Add the movingPlayer's square because shortest path was calculated from the first move in the alternate path.
                shortestPath.add(0, movingPlayer.getPosition());
            }
        }
        return shortestPath;
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
     * Checks if the agent is starting the game.
     * @return True if the agent is the starting player, false otherwise.
     */
    private boolean isAgentStarting() {
        return validator.getStartingPlayer() == agentID;
    }
    /**
     * Checks if it is after the fifth round of the game.
     * @return True if it is after the fifth round, false otherwise.
     */
    private boolean isAfterFifthRound() {
        return validator.getMoveNum() > 10;
    }
    /**
     * Determines if the opponent is closer to their destination compared to the agent.
     * @param agentPathLength The path of the agent.
     * @param opponentPathLength The path of the opponent.
     * @param extraForOpponent Additional steps considered for the opponent's path.
     * @return True if the opponent is closer, false otherwise.
     */
    private boolean isOpponentCloser(int agentPathLength, int opponentPathLength, int extraForOpponent) {
        return agentPathLength > opponentPathLength + extraForOpponent;
    }


    /**
     * Determines whether the opponent needs a head start based on game conditions.
     * @return 0 if Opponent deserves doesn't deserve a head start, 2 if he does. (includes current position).
     */
    private int decideOnOpponentHeadStart() {
        if (isAgentStarting() || (!isAgentStarting() && isAfterFifthRound()))
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
            boolean decisionMade = blockOpponent(opponentPath);
            if(decisionMade)
                return;
        }
        checkIfOpponentHasWalls(agentPath, opponentPath);
    }

    /**
     * Checks if the opponent has walls left and takes appropriate actions to hinder the opponent's progress.
     * @param agentPath The path of the agent.
     * @param opponentPath The path of the opponent.
     */
    private void checkIfOpponentHasWalls(ArrayList<Square> agentPath, ArrayList<Square> opponentPath) {
        if (opponentPlayer.getWallsLeft() == 0) {
            takeShortestPath();
            return;
        }
        ArrayList<Square> quickPath = searchQuickPath(agentPath, opponentPath);
        if (quickPath != null && blockQuickPath(opponentPath, quickPath)) {
            return;
        }
        // A quick path was not found / couldn't block it.
        searchForWeakPoints(agentPath, opponentPath);
    }

    /**
     * Blocks the quick path of the opponent if possible to hinder their progress.
     * @param opponentPath The path of the opponent.
     * @param quickPath The quick path of the opponent.
     * @return True if the quick path is successfully blocked, false otherwise.
     */
    private boolean blockQuickPath(ArrayList<Square> opponentPath, ArrayList<Square> quickPath) {
        ArrayList<Wall> blockingWalls1 = new ArrayList<>(), blockingWalls2;
        Square sq1, sq2, sq3, sq4;

        Square lastSqInQuickPath = quickPath.get(quickPath.size() - 1);
        int endOfQuickPath = opponentPath.indexOf(lastSqInQuickPath);
        Square sqAfterQuickPath = opponentPath.get(endOfQuickPath + 1);

        Square leftNeighbor = getReachableNeighbor(true); // Get left connected square.
        Square rightNeighbor = getReachableNeighbor(false); // Get right connected square.

        int rowOffset = 0;
        Wall wallThatBlocksPath = null;

        if (leftNeighbor == null && rightNeighbor == null) { // Quick path is 1 width (not connected to a left or right square).
            while(blockingWalls1.isEmpty() && quickPath.size() > rowOffset) { // while didn't find a wall and didn't reach end of board.
                sq1 = lastSqInQuickPath.neighbor(rowOffset, 0);
                sq2 = sqAfterQuickPath.neighbor(rowOffset, 0);
                blockingWalls1 = blockCrossingBetween(sq1, sq2);
                rowOffset++;
            }
            if(quickPath.size() != rowOffset)
                wallThatBlocksPath = blockingWalls1.get(0);

        } else {
            int direction = (leftNeighbor != null) ? -1 : 1; // Decide if the player in the quick path is on the left or right side in the path.
            boolean found = false;
            while(!found && quickPath.size() > rowOffset) {
                // Squares on the left/right end of quick path.
                sq1 = lastSqInQuickPath.neighbor(rowOffset, direction);
                sq2 = sqAfterQuickPath.neighbor(rowOffset, direction);

                // Squares cardinal to end of quick path.
                sq3 = lastSqInQuickPath.neighbor(rowOffset, 0);
                sq4 = sqAfterQuickPath.neighbor(rowOffset, 0);
                blockingWalls1 = blockCrossingBetween(sq1, sq2);
                blockingWalls2 = blockCrossingBetween(sq3, sq4);
                rowOffset++;

                for (Wall wall : blockingWalls1) { // Find common wall.
                    if (blockingWalls2.contains(wall)) {
                        wallThatBlocksPath = wall;
                        found = true;
                    }
                }
            }
        }
        if(wallThatBlocksPath == null)
            return false;
        int originalOpponentPathLen = opponentPath.size();
        addVirtualWall(wallThatBlocksPath);
        int newOpponentPathLen = calculatePath(opponentPlayer, agentPlayer.getPosition()).size();
        removeVirtualWall(wallThatBlocksPath);
        if (newOpponentPathLen > originalOpponentPathLen) {
            makeMove(wallThatBlocksPath.toString());
            return true;
        }
        return false;
    }

    /**
     * Retrieves the connected neighbor of the opponent.
     * @param isLeft a boolean flag if the neighbor is on the left side, false if on the right side.
     * @return The connected neighbor square if it exists, null otherwise.
     */
    private Square getReachableNeighbor(boolean isLeft) {
        List<Square>[] graph = getBoardGraph();
        int opponentPosIndex = opponentPlayer.getPosition().toIndex();

        // Get the left/right neighbor/square.
        Square neighbor = (isLeft) ? opponentPlayer.getPosition().neighbor(0, -1) : opponentPlayer.getPosition().neighbor(0, 1);
        if (graph[opponentPosIndex].contains(neighbor)) // Check if It's in the board.
            return neighbor;
        return null;
    }

    /**
     * Searches for a quick path of the opponent based on the agent's and opponent's paths.
     * @param agentPath The path of the agent.
     * @param opponentPath The path of the opponent.
     * @return The quick path of the opponent if found, null otherwise.
     */
    private ArrayList<Square> searchQuickPath(ArrayList<Square> agentPath, ArrayList<Square> opponentPath) {
        boolean foundDeviation = false;
        boolean laneExists = true;
        int width;
        ArrayList<Square> quickPath = new ArrayList<>();
        for (int i = 0; i < opponentPath.size() && laneExists; i++) {
            width = calculateWidthAtSquare(opponentPath.get(i)); // Width at square
            if (width <= 2)
                quickPath.add(opponentPath.get(i));
            else if (!foundDeviation && i != 0) // If there wasn't a deviation yet, and it's not the first square (player current position).
                foundDeviation = true;
            else
                laneExists = false;
        }

        if (quickPath.size() < 6) // Too short, not considered a quick path.
            return null;

        if (quickPath.size() > opponentPath.size() - 2)
            return null;

        for (Square square : quickPath) { // If agent's path intersect, then it's also the agent's quick path.
            if (agentPath.contains(square))
                return null;
        }
        return quickPath;
    }

    /**
     * Searches for and places a wall to hinder the opponent's progress.
     * @param opponentPath The path of the opponent.
     * @return True if a wall is successfully placed, false otherwise.
     */
    private boolean blockOpponent(ArrayList<Square> opponentPath) {
        ArrayList<Wall> increasePathWalls = wallsToIncreasePath(opponentPath, opponentPlayer, agentPlayer.getPosition());
        int minDifference = Integer.MAX_VALUE;
        int newOpponentPath, newAgentPath;
        Wall bestWall = null;

        // Search for the wall that has minimum (agent path length)-(opponent path length).
        // If agent's path gets longer (bad) the number gets bigger.
        // If opponent's path gets longer (good) the number gets smaller.
        // Trying to find the wall that increased the most opponent while not lengthening the agent's by as much.
        for (Wall wall : increasePathWalls) {
            addVirtualWall(wall);
            newAgentPath = calculatePath(agentPlayer, opponentPlayer.getPosition()).size();
            newOpponentPath = calculatePath(opponentPlayer, agentPlayer.getPosition()).size();
            if (newAgentPath - newOpponentPath < minDifference) {
                minDifference = newAgentPath - newOpponentPath;
                bestWall = wall;
            }
            removeVirtualWall(wall);
        }
        if (bestWall == null)
            return false;
        makeMove(bestWall.toString());
        return true;
    }


    /**
     * Searches for weak points in the agent's path and handles them.
     * @param agentPath The path of the agent.
     * @param opponentPath The path of the opponent.
     */
    private void searchForWeakPoints(ArrayList<Square> agentPath, ArrayList<Square> opponentPath) {
        boolean decisionMade = false;
        int width;
        for (int i = 0; i < agentPath.size() - 1 && !decisionMade; i++) {
            width = calculateWidthAtSquare(agentPath.get(i));
            if (width <= 2)
                decisionMade = handleWeakPoint(agentPath, i, opponentPath);
        }
        if (!decisionMade) { // Didn't go for alternate path.
            if(!handledWeakPoint && preventWeakPoint != null) {
                // Didn't handle a weak point yet and the wall the prevents the weak point is not null.
                // In other words, a preventive wall was found and not yet placed, so it needs to be placed because a weak point wasn't handled yet.
                handledWeakPoint = true; // handled it.
                makeMove(preventWeakPoint.toString()); // place the wall.
            }
            else 
                takeShortestPath();
        }
    }

    /**
     * Handles a weak point encountered in the agent's path.
     * @param agentPath The path of the agent.
     * @param weakPointIndex The index of the weak point in the agent's path.
     * @param opponentPath The original path of the opponent.
     * @return True if a decision is made to handle the weak point, false otherwise.
     */
    private boolean handleWeakPoint(ArrayList<Square> agentPath, int weakPointIndex, ArrayList<Square> opponentPath) {
        int newOpponentPathLength, newAgentPathLength;
        Wall trappingWall = null;
        Square weakPoint = agentPath.get(weakPointIndex); // The square that is considered weak.
        Square next = agentPath.get(weakPointIndex + 1); // The square after it.
        ArrayList<Wall> walls = blockCrossingBetween(weakPoint, next);
        // Search for a wall that increased the path more to the agent than the opponent.
        // Trying to find the wall that the opponent may place.
        for (Wall wall : walls) {
            addVirtualWall(wall);
            newOpponentPathLength = calculatePath(opponentPlayer, agentPlayer.getPosition()).size();
            newAgentPathLength = calculatePath(agentPlayer, opponentPlayer.getPosition()).size();
            if((newAgentPathLength-agentPath.size()) >= (newOpponentPathLength-opponentPath.size()))
                trappingWall = wall;
            removeVirtualWall(wall);
        }
        if (trappingWall == null)
            return false;

        addVirtualWall(trappingWall);
        newAgentPathLength = calculatePath(agentPlayer, opponentPlayer.getPosition()).size();
        newOpponentPathLength = calculatePath(opponentPlayer, agentPlayer.getPosition()).size();
        removeVirtualWall(trappingWall);
        if (newAgentPathLength <= newOpponentPathLength) // If the wall doesn't make it so that the agent's path is longer than the opponent's, it's not a weak point.
            return false;
        if (newAgentPathLength - newOpponentPathLength <= 2 && newOpponentPathLength > 5) // The difference is quite small and the path of the opponent is much bigger.
            return false;
        return blockWeakPoint(trappingWall);
    }


    /**
     * Blocks a weak point by placing a wall and adjusting the path of the agent.
     * @param trappingWall The wall used to block the weak point.
     * @return True if the weak point is successfully blocked, false otherwise.
     */
    private boolean blockWeakPoint(Wall trappingWall) {
        ArrayList<Square> alternatePath;
        if (!handledWeakPoint) {
            Square originalPos = agentPlayer.getPosition();
            addVirtualWall(trappingWall);
            alternatePath = calculatePath(agentPlayer, opponentPlayer.getPosition());
            Wall blockingWall;

            for (int i = 0; i < alternatePath.size() - 1; i++) {
                moveAgentTo(alternatePath.get(i));
                blockingWall = getPathBlockingWall(); // Get a wall that is completely blocking a player (illegal).
                if (blockingWall != null) {
                    removeVirtualWall(trappingWall);
                    if(validator.isValidWallPlacement(blockingWall)) // After removing the trappingWall if the blockingWall is legal.
                        preventWeakPoint = blockingWall;
                    addVirtualWall(trappingWall);
                }
            }
            removeVirtualWall(trappingWall);
            moveAgentTo(originalPos);
            if (preventWeakPoint != null) {
                return false;
            }
        }

        addVirtualWall(trappingWall);
        alternatePath = calculatePath(agentPlayer, opponentPlayer.getPosition());
        removeVirtualWall(trappingWall);
        makeMove(alternatePath.get(1).toString());
        return true;
    }

    /**
     * Finds a wall that blocks the path of the opponent.
     * @return The wall that blocks the path of the opponent.
     */
    private Wall getPathBlockingWall() {
        Wall pathBlocker = null;
        for (int row = 0; row < BOARD_DIMENSION - 1; row++) {
            for (int col = 0; col < BOARD_DIMENSION - 1; col++) {
                Square sq = new Square(row, col);
                Wall wall = new Wall(sq, Wall.Orientation.HORIZONTAL);
                if (validator.doesWallBlockGoal(wall)) {
                    pathBlocker = wall;
                }
            }
        }
        for (int row = 1; row < BOARD_DIMENSION; row++) {
            for (int col = 0; col < BOARD_DIMENSION - 1; col++) {
                Square sq = new Square(row, col);
                Wall wall = new Wall(sq, Wall.Orientation.VERTICAL);
                if (validator.doesWallBlockGoal(wall)) {
                    pathBlocker = wall;
                }
            }
        }
        return pathBlocker;
    }



     //direction(r, c)
     //down(-1, 0)
     //up(1, 0)
     //left(0, -1)
     //right(0, 1)
    /**
     * Blocks the crossing between two squares by placing walls.
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
        ArrayList<Wall> validWalls = new ArrayList<>();
        for (Wall wall : walls)
            if (validator.isValidWallPlacement(wall))
                validWalls.add(wall);
        return validWalls;
    }

    /**
     * Calculates the width at a square.
     * @param src The source square.
     * @return The width at the specified square.
     */
    private int calculateWidthAtSquare(Square src) {
        int steps = moveInDirection(src, 1);
        steps += moveInDirection(src, -1);
        return steps + 1;
    }

    /**
     * Moves in a direction from a source square and counts the number of steps.
     * @param src The source square.
     * @param direction The direction of movement (-1 for left or down, 1 for right or up).
     * @return The number of steps moved in the specified direction.
     */
    private int moveInDirection(Square src, int direction) {
        int steps = 0;
        boolean finished = false;
        Square next, prev = src;

        for (int i = 1; i < BOARD_DIMENSION && !finished; i++) {
            next = src.neighbor(0, direction * i);
            if (next.equals(opponentPlayer.getPosition())) {
                next = src.neighbor(0, direction * ++i);
            }
            if (validator.isValidTraversal(prev, next)) {
                prev = next;
                steps++;
            } else
                finished = true;
        }
        return steps;
    }


    /**
     * Generates a list of wall moves that could potentially lengthen the path of the specified player.
     * @param playerPath The current path of the player to lengthen.
     * @param player The player whose path needs to be lengthened.
     * @param occupiedSquare The position of the other player.
     * @return A list of wall moves that could lengthen the path of the specified player.
     */
    public ArrayList<Wall> wallsToIncreasePath(ArrayList<Square> playerPath, Player player, Square occupiedSquare) {
        ArrayList<Wall> wallMoves = generateWallMoves();
        ArrayList<Wall> usefulWalls = new ArrayList<>();

        // Search for walls that increase the path of player.
        for (Wall wall : wallMoves) {
            addVirtualWall(wall);
            ArrayList<Square> newPlayerPath = calculatePath(player, occupiedSquare);
            removeVirtualWall(wall);
            if (newPlayerPath.size() > playerPath.size())
                usefulWalls.add(wall);
        }
        return usefulWalls;
    }

    /**
     * Moves a player to square
     * @param square The square to move a player to.
     */
    private void moveAgentTo(Square square) {
        agentPlayer.setPosition(square);
    }

    /**
     * Places a wall on the board, called virtual because after this method the removal of that wall will be called.
     * @param wall The wall to place.
     */
    private void addVirtualWall(Wall wall) {
        validator.addWall(wall);
    }

    /**
     * Removes a wall placed in addVirtualWall.
     * @param wall The wall to remove.
     */
    private void removeVirtualWall(Wall wall) {
        validator.removeWall(wall);
    }


    /**
     * Makes a move and updates the view.
     * @param move The move to make.
     */
    private void makeMove(String move) {
        if (move != null) {
            int turn = validator.getTurn();
            if (move.length() == 2) {
                Square sq = new Square(move);
                if (validator.commitMove(sq.toString()))
                    // Valid.
                    viewUpdater.updatePawnPosition(sq.getRow(), sq.getCol(), turn);
                else
                    // Invalid.
                    viewUpdater.updatePawnPosition(-1, -1, turn);

            } else if (move.length() == 3) {
                Wall wall = new Wall(move);
                if (validator.commitMove(wall.toString())) {
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
     * @param boardGraph The graph representing the game board.
     */
    private void setBoardGraph(List<Square>[] boardGraph) {
        this.boardGraph = boardGraph;
    }

    /**
     * Gets the graph representing the game board.
     * @return The graph representing the game board.
     */
    private List<Square>[] getBoardGraph() {
        return this.boardGraph;
    }

    /**
     * Generates valid pawn moves from the given source square.
     * @param src The source square.
     * @return An ArrayList of valid pawn moves.
     */
    public ArrayList<Square> generatePawnMoves(Square src) {
        ArrayList<Square> validMoves = new ArrayList<>();
        for (Square sq : src.neighbourhood(2)) {
            if (validator.isValidTraversal(src, sq)) {
                validMoves.add(sq);
            }
        }
        return validMoves;
    }

    /**
     * Generates valid wall moves on the game board.
     * @return An ArrayList of valid wall moves.
     */
    public ArrayList<Wall> generateWallMoves() {
        ArrayList<Wall> validMoves = new ArrayList<>();
        for (int row = 0; row < BOARD_DIMENSION - 1; row++) {
            for (int col = 0; col < BOARD_DIMENSION - 1; col++) {
                Square sq = new Square(row, col);
                Wall wall = new Wall(sq, Wall.Orientation.HORIZONTAL);
                if (validator.isValidWallPlacement(wall))
                    validMoves.add(wall);
            }
        }
        for (int row = 1; row < BOARD_DIMENSION; row++) {
            for (int col = 0; col < BOARD_DIMENSION - 1; col++) {
                Square sq = new Square(row, col);
                Wall wall = new Wall(sq, Wall.Orientation.VERTICAL);
                if (validator.isValidWallPlacement(wall))
                    validMoves.add(wall);
            }
        }
        return validMoves;
    }
}
