package Controller;

import Model.GameData;
import Model.Player;
import Model.Square;
import Model.Wall;
import View.GUI;

import java.util.ArrayList;
import java.util.List;

import static Utilities.BFS.calculateBFS;
import static Utilities.Constants.BOARD_DIMENSION;

/**
 * This class contains the algorithm to decide a move for the AI player.
 */
public class AI {

    private boolean foundWeakPoint = false;
    private Wall preventWeakPoint = null;
    private final GameData model;
    private final ViewUpdater viewUpdater;
    private final int agentID;
    private final Player agentPlayer;
    private final Player opponentPlayer;
    private List<Square>[] boardGraph;

    /**
     * Constructs a new AI object with the specified ID, game rules model, and game view.
     * @param id  The ID of the AI agent.
     * @param model The game rules model.
     * @param view The game view.
     */
    public AI(int id, GameData model, GUI view) {
        this.agentID = id;
        this.model = model;
        this.viewUpdater = ViewUpdater.getInstance(view);

        this.agentPlayer = model.getPlayer(id);
        this.opponentPlayer = model.getPlayer((id + 1) % 2);
    }



    /**
     * Initiates the AI's turn, making decisions based on the game state.
     */
    public void AiTurn() {
        if(model.getTurn() == this.agentID) {
            setBoardGraph(model.getBoardGraph());
            if (agentPlayer.getWallsLeft() > 0)
                takeShortestPath();
            else
                compareBetweenPaths();
        }
    }

    /**
     * Calculates the path for the movingPlayer to take.
     * @param movingPlayer The player for whom the path is being calculated.
     * @param otherPlayerPos The position of the other player.
     * @return The calculated path.
     */
    private ArrayList<Square> calculatePath(Player movingPlayer, Square otherPlayerPos) {
        ArrayList<Square> shortestPath = calculateBFS(getBoardGraph(), movingPlayer.getPos(), movingPlayer.getDestRow());
        if(shortestPath.contains(otherPlayerPos) && shortestPath.size() <= 2) {
            shortestPath = generatePawnMoves(otherPlayerPos);
            shortestPath.remove(movingPlayer.getPos());
        }
        if(shortestPath.contains(otherPlayerPos) && shortestPath.size() > 2) { // path contains other's square - jump needed.
            shortestPath.remove(otherPlayerPos);
            if(!model.isValidTraversal(movingPlayer.getPos(), shortestPath.get(1))) {
                ArrayList<Square> possibleMoves = generatePawnMoves(movingPlayer.getPos());
                ArrayList<Square> currentPath;
                int minPathLength = Integer.MAX_VALUE;
                for (Square firstMove : possibleMoves) {
                    currentPath = calculateBFS(getBoardGraph(), firstMove, movingPlayer.getDestRow());
                    if(currentPath.size() < minPathLength) {
                        minPathLength = currentPath.size();
                        shortestPath = currentPath;
//                        System.out.println(shortestPath);
                    }
                }
            }
        }
        return shortestPath;
    }

    /**
     * Initiates the AI's move by taking the shortest path to the destination.
     */
    private void takeShortestPath() {
        ArrayList<Square> path = calculatePath(agentPlayer, opponentPlayer.getPos());
        makeMove(path.get(1).toString());
    }

    /**
     * Checks if the agent is starting the game.
     * @return True if the agent is the starting player, false otherwise.
     */
    private boolean isAgentStarting() {
        return model.getStartingPlayer() == agentID;
    }
    /**
     * Checks if it is after the fifth round of the game.
     * @return True if it is after the fifth round, false otherwise.
     */
    private boolean isAfterFifthRound() {
        return model.getMoveNum() > 10;
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
     * Checks if the opponent has placed at least one wall.
     * @return {@code true} if the opponent has placed a wall, {@code false} otherwise.
     */
    private boolean opponentPlacedWall() {
        return opponentPlayer.getWallsLeft() < 10;
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
        ArrayList<Square> agentPath = calculatePath(agentPlayer, opponentPlayer.getPos());
        ArrayList<Square> opponentPath = calculatePath(opponentPlayer, agentPlayer.getPos());
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
        searchForWeakPoints(agentPath, opponentPath);
    }

    /**
     * Blocks the quick path of the opponent if possible to hinder their progress.
     * @param opponentPath The path of the opponent.
     * @param quickPath The quick path of the opponent.
     * @return True if the quick path is successfully blocked, false otherwise.
     */
    private boolean blockQuickPath(ArrayList<Square> opponentPath, ArrayList<Square> quickPath) {
        ArrayList<Wall> blockingWalls1 = new ArrayList<>(), blockingWalls2 = new ArrayList<>();
        Square sq1, sq2, sq3, sq4;
        Square lastSqInQuickPath = quickPath.get(quickPath.size() - 1);
        int endOfQuickPath = opponentPath.indexOf(lastSqInQuickPath);
        Square sqAfterQuickPath = opponentPath.get(endOfQuickPath + 1);
        Square leftNeighbor = getConnectedNeighbor(true);
        Square rightNeighbor = getConnectedNeighbor(false);
        int rowOffset = 0;
        Wall wallToBlockPath = null;
        if (leftNeighbor == null && rightNeighbor == null) {
            while(blockingWalls1.isEmpty() && quickPath.size() > rowOffset) {
                sq1 = lastSqInQuickPath.neighbor(rowOffset, 0);
                sq2 = sqAfterQuickPath.neighbor(rowOffset, 0);

                blockingWalls1 = blockCrossing(sq1, sq2);
                rowOffset++;
            }
//            do {
//                sq1 = lastSqInQuickPath.neighbor(rowOffset, 0);
//                sq2 = sqAfterQuickPath.neighbor(rowOffset, 0);
//                blockingWalls1 = blockCrossing(sq1, sq2);
//                rowOffset++;
//            } while (blockingWalls1.isEmpty());
            if(quickPath.size() != rowOffset)
                wallToBlockPath = blockingWalls1.get(0);

        } else {
            int direction = (leftNeighbor != null) ? -1 : 1;
            boolean found = false;
            while(!found && quickPath.size() > rowOffset) {
                sq1 = lastSqInQuickPath.neighbor(rowOffset, direction);
                sq2 = sqAfterQuickPath.neighbor(rowOffset, direction);
                sq3 = lastSqInQuickPath.neighbor(rowOffset, 0);
                sq4 = sqAfterQuickPath.neighbor(rowOffset, 0);
                System.out.println(sq1 + " " + sq2 + " " + sq3 + " " + sq4);
                blockingWalls1 = blockCrossing(sq1, sq2);
                blockingWalls2 = blockCrossing(sq3, sq4);
                rowOffset++;

                for (Wall wall : blockingWalls1) {
                    if (blockingWalls2.contains(wall)) {
                        wallToBlockPath = wall;
                        found = true;
                    }
                }
            }
//            do {
//                sq1 = lastSqInQuickPath.neighbor(rowOffset, direction);
//                sq2 = sqAfterQuickPath.neighbor(rowOffset, direction);
//                sq3 = lastSqInQuickPath.neighbor(rowOffset, 0);
//                sq4 = sqAfterQuickPath.neighbor(rowOffset, 0);
//
//                blockingWalls1 = blockCrossing(sq1, sq2);
//                blockingWalls2 = blockCrossing(sq3, sq4);
//                rowOffset++;
//
//                for (Wall wall : blockingWalls1) {
//                    if (blockingWalls2.contains(wall)) {
//                        wallToBlockPath = wall;
//                        found = true;
//                    }
//                }
//            } while (!found);
        }
        if(wallToBlockPath == null)
            return false;
        int originalOpponentPathLen = opponentPath.size();
        doVirtualMove(wallToBlockPath.toString());
        int newOpponentPathLen = calculatePath(opponentPlayer, agentPlayer.getPos()).size();
        undoVirtualMove(wallToBlockPath.toString());
        if (newOpponentPathLen > originalOpponentPathLen) {
            makeMove(wallToBlockPath.toString());
            return true;
        }
        return false;
    }

    /**
     * Retrieves the connected neighbor of the opponent.
     * @param isLeft True if the neighbor is on the left side, false if on the right side.
     * @return The connected neighbor square if it exists, null otherwise.
     */
    private Square getConnectedNeighbor(boolean isLeft) {
        Square neighbor;
        List<Square>[] graph = getBoardGraph();
        if (isLeft) {
            neighbor = opponentPlayer.getPos().neighbor(0, -1);
        } else {
            neighbor = opponentPlayer.getPos().neighbor(0, 1);
        }
        int opponentPosIndex = opponentPlayer.getPos().squareToIndex();
        if (graph[opponentPosIndex].contains(neighbor))
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
            width = calculateWidthAtSquare(opponentPath.get(i));
            if (width <= 2) {
                quickPath.add(opponentPath.get(i));
            } else if (!foundDeviation && i != 0) { // If there wasn't a deviation yet
                foundDeviation = true;
            } else
                laneExists = false;
        }
        if (quickPath.size() < 6) {
            return null;
        } if (quickPath.size() > opponentPath.size() - 2) {
            return null;
        } for (Square square : quickPath) {
            if (agentPath.contains(square))
                return null;
        }
        return quickPath;
    }

    /**
     * Searches for and places a wall to hinder the opponent's progress.
     *
     * @param opponentPath The path of the opponent.
     * @return True if a wall is successfully placed, false otherwise.
     */
    private boolean blockOpponent(ArrayList<Square> opponentPath) {
        ArrayList<Wall> increasePathWalls = disruptiveWalls(opponentPath, opponentPlayer, agentPlayer.getPos());
        int maxDifference = Integer.MIN_VALUE;
        int newOpponentPath, pathDifference;
        Wall bestWall = null;
        for (Wall wall : increasePathWalls) {
            doVirtualMove(wall.toString());
            newOpponentPath = calculatePath(opponentPlayer, agentPlayer.getPos()).size();
            pathDifference = newOpponentPath - opponentPath.size();
            if (pathDifference > maxDifference) {
                maxDifference = pathDifference;
                bestWall = wall;
            }
            undoVirtualMove(wall.toString());
        }
        if (bestWall == null)
            return false;
        makeMove(bestWall.toString());
        return true;
    }


    /**
     * Searches for weak points in the agent's path and handles them.
     *
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
        if (!decisionMade) {
            if(!foundWeakPoint && preventWeakPoint != null) {
                foundWeakPoint = true;
                makeMove(preventWeakPoint.toString());
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
        Square weakPoint = agentPath.get(weakPointIndex);
        Square next = agentPath.get(weakPointIndex + 1);
        ArrayList<Wall> walls = blockCrossing(weakPoint, next);
        for (Wall wall : walls) {
            doVirtualMove(wall.toString());
            newOpponentPathLength = calculatePath(opponentPlayer, agentPlayer.getPos()).size();
            newAgentPathLength = calculatePath(agentPlayer, opponentPlayer.getPos()).size();
            if((newAgentPathLength-agentPath.size()) >= (newOpponentPathLength-opponentPath.size()))
                trappingWall = wall;
            undoVirtualMove(wall.toString());
        }
        if (trappingWall == null)
            return false;
        doVirtualMove(trappingWall.toString());
        newAgentPathLength = calculatePath(agentPlayer, opponentPlayer.getPos()).size();
        newOpponentPathLength = calculatePath(opponentPlayer, agentPlayer.getPos()).size();
//        System.out.println(newAgentPathLength <= newOpponentPathLength);
//        System.out.println(newAgentPathLength - newOpponentPathLength <= 2 && newOpponentPathLength > 5);
        undoVirtualMove(trappingWall.toString());
        if (newAgentPathLength <= newOpponentPathLength) {
            return false;
        } if (newAgentPathLength - newOpponentPathLength <= 2 && newOpponentPathLength > 5) {
            return false;
        }
        return blockWeakPoint(trappingWall);
    }


    /**
     * Blocks a weak point by placing a wall and adjusting the path of the agent.
     * @param trappingWall The wall used to block the weak point.
     * @return True if the weak point is successfully blocked, false otherwise.
     */
    private boolean blockWeakPoint(Wall trappingWall) {
        ArrayList<Square> alternatePath;
        if (!foundWeakPoint) {
            Square originalPos = agentPlayer.getPos();
            doVirtualMove(trappingWall.toString());
            alternatePath = calculatePath(agentPlayer, opponentPlayer.getPos());
            Wall blockingWall;
            for (int i = 0; i < alternatePath.size() - 1; i++) {
                doVirtualMove(alternatePath.get(i).toString());
                blockingWall = getPathBlockingWall();
                if (blockingWall != null) {
                    undoVirtualMove(trappingWall.toString());
                    if(model.isValidWallPlacement(blockingWall))
                        preventWeakPoint = blockingWall;
//                    if (!blockingWall.equals(getPathBlockingWall()))
//                        preventWeakPoint = blockingWall;
                    doVirtualMove(trappingWall.toString());
                }
            }
            undoVirtualMove(trappingWall.toString());
            doVirtualMove(originalPos.toString());
            if (preventWeakPoint != null) {
                return false;
            }
        }
        doVirtualMove(trappingWall.toString());
        alternatePath = calculatePath(agentPlayer, opponentPlayer.getPos());
        undoVirtualMove(trappingWall.toString());
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
                if (model.doesWallBlockGoal(wall)) {
                    pathBlocker = wall;
                }
            }
        }
        for (int row = 1; row < BOARD_DIMENSION; row++) {
            for (int col = 0; col < BOARD_DIMENSION - 1; col++) {
                Square sq = new Square(row, col);
                Wall wall = new Wall(sq, Wall.Orientation.VERTICAL);
                if (model.doesWallBlockGoal(wall)) {
                    pathBlocker = wall;
                }
            }
        }
//        System.out.println("wall that blocks " + pathBlocker);
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
    private ArrayList<Wall> blockCrossing(Square prev, Square next) {
        int rowDiff = prev.getRow() - next.getRow();
        int colDiff = prev.getCol() - next.getCol();
        ArrayList<Wall> blockWeakSpots = new ArrayList<>();
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
            blockWeakSpots.add(new Wall(startingSq1, 'v'));
            blockWeakSpots.add(new Wall(startingSq2, 'v'));
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
            blockWeakSpots.add(new Wall(startingSq1, 'h'));
            blockWeakSpots.add(new Wall(startingSq2, 'h'));
        }
        ArrayList<Wall> weakSpotBlockers = new ArrayList<>();
        for (Wall wall : blockWeakSpots)
            if (model.isValidWallPlacement(wall))
                weakSpotBlockers.add(wall);
        return weakSpotBlockers;
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
            if (next.equals(opponentPlayer.getPos())) {
                i++;
                next = src.neighbor(0, direction * i);
            }
            if (model.isValidTraversal(prev, next)) {
                prev = next;
                steps++;
            } else
                finished = true;
        }
        return steps;
    }


    /**
     * Generates a list of wall moves that could potentially lengthen the path of the specified player.
     * @param pathToLengthen The current path of the player to lengthen.
     * @param playerToLengthen The player whose path needs to be lengthened.
     * @param otherPlayerPos The position of the other player.
     * @return A list of wall moves that could lengthen the path of the specified player.
     */
    public ArrayList<Wall> disruptiveWalls(ArrayList<Square> pathToLengthen, Player playerToLengthen, Square otherPlayerPos) {
        ArrayList<Wall> wallMoves = generateWallMoves();
        ArrayList<Wall> usefulWalls = new ArrayList<>();
        for (Wall wall : wallMoves) {
            doVirtualMove(wall.toString());
            ArrayList<Square> newPlayerPath = calculatePath(playerToLengthen, otherPlayerPos);
            undoVirtualMove(wall.toString());
            if (newPlayerPath.size() > pathToLengthen.size())
                usefulWalls.add(wall);
        }
        return usefulWalls;
    }

    /**
     * Performs a virtual move.
     * @param move The move to perform.
     */
    private void doVirtualMove(String move) {
        if (move.length() == 2)
            model.movePlayerToSquare(new Square(move));
        else if (move.length() == 3)
            model.addWallToBoard(new Wall(move));
    }

    /**
     * Undoes a virtual move.
     * @param move The move to undo.
     */
    private void undoVirtualMove(String move) {
        if (move.length() == 2)
            model.movePlayerToSquare(new Square(move));
        else if (move.length() == 3)
            model.removeWallFromBoard(new Wall(move));
    }

    /**
     * Makes a move and updates the view.
     * @param move The move to make.
     */
    private void makeMove(String move) {
        if (move != null) {
            int turn = model.getTurn();
            if (move.length() == 2) {
                Square sq = new Square(move);
                if (model.commitMove(sq.toString()))
                    viewUpdater.updatePawnPosition(turn, sq.getRow(), sq.getCol());
                else
                    viewUpdater.updatePawnPosition(turn, -1, -1);
            } else if (move.length() == 3) {
                Wall wall = new Wall(move);
                if (model.commitMove(wall.toString())) {
                    int row = wall.getStartingSq().getRow() + 1;
                    int col = wall.getStartingSq().getCol() + 1;
                    if (move.charAt(2) == 'h')
                        viewUpdater.updateHorizontalWall(row, col, turn);
                    else if (move.charAt(2) == 'v')
                        viewUpdater.updateVerticalWall(row, col, turn);
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
            if (model.isValidTraversal(src, sq)) {
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
                if (model.isValidWallPlacement(wall))
                    validMoves.add(wall);
            }
        }
        for (int row = 1; row < BOARD_DIMENSION; row++) {
            for (int col = 0; col < BOARD_DIMENSION - 1; col++) {
                Square sq = new Square(row, col);
                Wall wall = new Wall(sq, Wall.Orientation.VERTICAL);
                if (model.isValidWallPlacement(wall))
                    validMoves.add(wall);
            }
        }
        return validMoves;
    }

}
