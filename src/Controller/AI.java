package Controller;

import static Controller.ShortestPath.shortestPathToRow;
import Model.Gamestate.*;
import View.Game;

import java.util.List;
import java.util.ArrayList;

public class AI {

    public static final int TILE_SIZE = 50;
    public static final int BOARD_DIMENSION = 9;
    public static final int BOARD_SIZE = TILE_SIZE*BOARD_DIMENSION;

    private boolean isFirstWeakPoint = true;
    private Wall wallBlockingWeakSpot = null;
    private boolean fastLaneCloser = false;
    private final GameRules model;
    private final ViewUpdater viewUpdater;
    private final int agentID;
    private final Player agent;
    private final Player opponent;
    private List<Square>[] graph;
    private List<Wall> walls;

    public AI(int id, GameRules model, Game view) {
        this.agentID = id;
        this.model = model;
        this.viewUpdater = ViewUpdater.getInstance(view);

        this.agent = model.getPlayer(id);
        this.opponent = model.getPlayer((id+1)%2);
    }


    public void AiTurn() {
        if(model.getTurn() == this.agentID) {
            setGraph(model.getBoardGraph());
            setWalls(model.getBoardWalls());
            if (agent.getWallsLeft() == 0)
                takeShortestPath();
            else
                comparePlayersPaths();
        }
    }
    private void takeShortestPath() {
        ArrayList<Square> path = calculateShortestPath(agent, opponent.getPos());
        makeMove(path.get(0).toString());
    }
    private ArrayList<Square> calculateShortestPath(Player movingPlayer, Square otherPlayerPos) {
        ArrayList<Square> shortestPath = shortestPathToRow(getGraph(), movingPlayer.getPos(), movingPlayer.getDestRow());
        if(shortestPath.contains(otherPlayerPos) && shortestPath.size() == 1) {
            shortestPath = generatePawnMoves(otherPlayerPos);
        }
        if(shortestPath.contains(otherPlayerPos) && shortestPath.size() > 1) {
            shortestPath.remove(otherPlayerPos);
            if(!model.isValidTraversal(movingPlayer.getPos(), shortestPath.get(0))) {
                ArrayList<Square> possibleFirstMoves = generatePawnMoves(movingPlayer.getPos());
                ArrayList<Square> currentPath;
                int minPathLength = Integer.MAX_VALUE;
                for(Square firstMove : possibleFirstMoves) {
                    currentPath = shortestPathToRow(getGraph(), firstMove, movingPlayer.getDestRow());
                    if(currentPath.size() < minPathLength) {
                        minPathLength = currentPath.size();
                        shortestPath = currentPath;
                        shortestPath.add(0, firstMove);
                    }
                }
            }
        }
        return shortestPath;
    }

    private void comparePlayersPaths() {
        ArrayList<Square> agentShortestPath = calculateShortestPath(agent, opponent.getPos());
        ArrayList<Square> opponentShortestPath = calculateShortestPath(opponent, agent.getPos());
        if(isAgentStarting()) {
            boolean decisionMade = false;
            if(isOpponentCloser(agentShortestPath, opponentShortestPath, 0))
                decisionMade = searchAndPlaceWall(opponentShortestPath);
            if(!decisionMade)
                checkIfOpponentHasWalls(agentShortestPath, opponentShortestPath);
        }
        else {
            if(isAfterFifthRound()) {
                boolean decisionMade = false;
                if(isOpponentCloser(agentShortestPath, opponentShortestPath, 0))
                    decisionMade = searchAndPlaceWall(opponentShortestPath);
                if(!decisionMade)
                    checkIfOpponentHasWalls(agentShortestPath, opponentShortestPath);
            }
            else {
                boolean decisionMade = false;
                if(isOpponentCloser(agentShortestPath, opponentShortestPath, 2))
                    decisionMade = searchAndPlaceWall(opponentShortestPath);
                if(!decisionMade)
                    checkIfOpponentHasWalls(agentShortestPath, opponentShortestPath);
            }
        }
    }
    private boolean isAgentStarting() {
        return model.getStartingPlayer() == agentID;
    }
    private boolean isAfterFifthRound() {
        return model.getMoveNum() > 10;
    }
    private boolean isOpponentCloser(ArrayList<Square> agentPath, ArrayList<Square> opponentPath, int extraForOpponent) {
        return agentPath.size() > opponentPath.size() + extraForOpponent;
    }

    private void checkIfOpponentHasWalls(ArrayList<Square> agentPath, ArrayList<Square> opponentPath) {
        if (opponent.getWallsLeft() == 0) {
            takeShortestPath();
            return;
        }
        ArrayList<Square> opponentQuickPath = checkForQuickPath(agentPath, opponentPath);
        if(opponentQuickPath == null) {
            searchForWeakPoints(agentPath, opponentPath);
            return;
        }
        fastLaneCloser = true;
        boolean decisionMade = searchAndPlaceWall(opponentPath);
        if(!decisionMade)
            searchAndPlaceWall(opponentPath);
    }

    private ArrayList<Square> checkForQuickPath(ArrayList<Square> agentPath, ArrayList<Square> opponentPath) {
        boolean foundDeviation = false;
        boolean laneExists = true;
        ArrayList<Square> quickPath = new ArrayList<>();
        int width;
        opponentPath.add(0, opponent.getPos());
        for(int i = 0; i < opponentPath.size() && laneExists; i++) {
            width = calculateWidth(opponentPath.get(i));
            if(width <= 2) {
                quickPath.add(opponentPath.get(i));
            }
            else { // If the width is bigger than 2.
                if(!foundDeviation && i != 0) { // If not found a deviation yet AND the current square isn't the first in the opponent's path.
                    foundDeviation = true;
                }
                else
                    laneExists = false;
                // If a deviation is found for a square that isn't the first, a quick path doesn't exist.
            }
        }
        opponentPath.remove(0);
        if(quickPath.size() < 6)
            return null;
        if(quickPath.size() > opponentPath.size()-2)
            return null;
        for(Square square : quickPath) {
            if(agentPath.contains(square))
                return null;
        }
        return quickPath;
    }

    private boolean searchAndPlaceWall(ArrayList<Square> opponentPath) {
        int bestDifference = Integer.MAX_VALUE;
        int newAgentPath, newOpponentPath, pathDifference;
        ArrayList<Wall> increasePathWalls = wallsToIncreasePath(opponentPath, opponent, agent.getPos());
        Wall bestWall = null;
        for(Wall wall : increasePathWalls) {
            doVirtualMove(wall.toString());
            newAgentPath = calculateShortestPath(agent, opponent.getPos()).size();
            newOpponentPath = calculateShortestPath(opponent, agent.getPos()).size();
            pathDifference = newAgentPath-newOpponentPath;
            if(fastLaneCloser) {
                if(newOpponentPath > opponentPath.size()+4) {
                    bestWall = wall;
                    fastLaneCloser = false;
                }
            }
            else {
                if(pathDifference < bestDifference) {
                    bestDifference = pathDifference;
                    bestWall = wall;
                }
            }
            undoVirtualMove(wall.toString());
        }
        if(bestWall == null)
            return false;
        makeMove(bestWall.toString());
        return true;
    }

    private void searchForWeakPoints(ArrayList<Square> agentPath, ArrayList<Square> opponentPath) {
        boolean decisionMade = false;
        int width;
        agentPath.add(0, agent.getPos());
        for(int i = 0; i < agentPath.size() && !decisionMade; i++) {
            width = calculateWidth(agentPath.get(i));
            if(width <= 2)
                decisionMade = handleWeakPoint(agentPath, i, opponentPath);
        }
        agentPath.remove(0);
        if(!decisionMade) {
            if(wallBlockingWeakSpot == null) {
                takeShortestPath();
                return;
            }
            makeMove(wallBlockingWeakSpot.toString());
        }
    }
    private boolean handleWeakPoint(ArrayList<Square> agentPath, int weakPointIndex, ArrayList<Square> originalOpponentPath) {
        int minPathDifference = Integer.MAX_VALUE;
        int pathDifference;
        int newOpponentPathLength;
        int width;
        Square weakPoint = agentPath.get(weakPointIndex);
        ArrayList<Wall> blockingWalls = wallsToIncreasePath(agentPath, agent, opponent.getPos());
        Wall opponentWallForWeakPoint = null; // Wall that the opponent may place that creates a weak point for the agent
        for(Wall wall : blockingWalls) {
            doVirtualMove(wall.toString());
            width = calculateWidth(weakPoint);
            if(width == 1) {
                newOpponentPathLength = calculateShortestPath(opponent, agent.getPos()).size();
                pathDifference = newOpponentPathLength - originalOpponentPath.size();
                if(pathDifference < minPathDifference && pathDifference <= 2)
                    opponentWallForWeakPoint = wall;
            }
            undoVirtualMove(wall.toString());
        }
        if(opponentWallForWeakPoint == null)
            return false;
        doVirtualMove(opponentWallForWeakPoint.toString());
        int newAgentPath = calculateShortestPath(agent, opponent.getPos()).size();
        int newOpponentPath = calculateShortestPath(opponent, agent.getPos()).size();
        undoVirtualMove(opponentWallForWeakPoint.toString());
        if(newAgentPath <= newOpponentPath)
            return false;
        if(newAgentPath-newOpponentPath <= 2 && newOpponentPath > 5)
            // If difference between agent path to opponent path is less/equal to two and opponent is farther away from 5 traversals (far)
            return false;
        return blockWeakPoint(opponentWallForWeakPoint);
    }

    private boolean blockWeakPoint(Wall opponentWallForWeakPoint) {
        ArrayList<Square> alternatePath;
        if(wallBlockingWeakSpot == null) {
            Square originalAgentSquare = agent.getPos();
            doVirtualMove(opponentWallForWeakPoint.toString());
            alternatePath = calculateShortestPath(agent, opponent.getPos());
            Wall blockingWall = getPathBlockingWall();
            for(Square sq : alternatePath) {
                doVirtualMove(sq.toString());
                undoVirtualMove(opponentWallForWeakPoint.toString());
                if(model.isValidWallPlacement(blockingWall))
                    wallBlockingWeakSpot = blockingWall;
                doVirtualMove(opponentWallForWeakPoint.toString());
            }
            undoVirtualMove(opponentWallForWeakPoint.toString());
            undoVirtualMove(originalAgentSquare.toString());
            if(wallBlockingWeakSpot != null) {
                isFirstWeakPoint = false;
            }
        }
        if(isFirstWeakPoint) {
        }
        doVirtualMove(opponentWallForWeakPoint.toString());
        if(wallBlockingWeakSpot != null)
            return false;
        alternatePath = calculateShortestPath(agent, opponent.getPos());
        undoVirtualMove(opponentWallForWeakPoint.toString());
        makeMove(alternatePath.get(0).toString());
        return true;

    }

    private Wall getPathBlockingWall() {
        Wall pathBlocker = null;
        for(int row = 0; row < BOARD_DIMENSION-1; row++) {
            for(int col = 0; col < BOARD_DIMENSION-1; col++) {
                Square sq = new Square(row, col);
                Wall wall = new Wall(sq, Wall.Orientation.HORIZONTAL);
                if(model.doesWallBlockGoal(wall))
                    pathBlocker = wall;
            }
        }
        for(int row = 1; row < BOARD_DIMENSION; row++) {
            for(int col = 0; col < BOARD_DIMENSION-1; col++) {
                Square sq = new Square(row, col);
                Wall wall = new Wall(sq, Wall.Orientation.VERTICAL);
                if(model.doesWallBlockGoal(wall))
                    pathBlocker = wall;
            }
        }
        return pathBlocker;
    }

    /**
     * direction(r, c)
     * down(-1, 0)
     * up(1, 0)
     * left(0, -1)
     * right(0, 1)
     */
    private ArrayList<Wall> blockCrossing(Square prev, Square next) {
        int rowDiff = prev.getRow() - next.getRow();
        int colDiff = prev.getCol() - next.getCol();
        ArrayList<Wall> blockWeakSpots = new ArrayList<>();
        Square startingSq1 = null, startingSq2 = null;
        if(rowDiff == 0) { // Moved horizontally
            if(colDiff < 0) { // Moved right
                startingSq1 = prev;
                startingSq2 = startingSq1.neighbor(1, 0); // one up
            }
            if(colDiff > 0) { // Moved left
                startingSq1 = prev.neighbor(0, -1); // one left
                startingSq2 = startingSq1.neighbor(-1, 0); // one up
            }
            blockWeakSpots.add(new Wall(startingSq1, 'v'));
            blockWeakSpots.add(new Wall(startingSq2, 'v'));
        }
        if(colDiff == 0) { // Moved vertically
            if(rowDiff > 0) { // Moved down
                startingSq1 = prev.neighbor(-1, 0); // one down
                startingSq2 = startingSq1.neighbor(0, -1); // one left
            }
            if(rowDiff < 0) { // Moved up
                startingSq1 = prev;
                startingSq2 = startingSq1.neighbor(0, -1); // one left
            }
            blockWeakSpots.add(new Wall(startingSq1, 'h'));
            blockWeakSpots.add(new Wall(startingSq2, 'h'));
        }
        ArrayList<Wall> weakSpotBlockers = new ArrayList<>();
        for(Wall wall : blockWeakSpots)
            if(model.isValidWallPlacement(wall)) weakSpotBlockers.add(wall);
        return weakSpotBlockers;
    }

    public int calculateWidth(Square src) {
        int steps = moveSideways(src, 1);
        steps += moveSideways(src, -1);
        return steps+1;
    }
    public int moveSideways(Square src, int direction) {
        int steps = 0;
        boolean finished = false;
        Square next, prev = src;

        for (int i = 1; i < BOARD_DIMENSION && !finished; i++) {
            next = src.neighbor(0, direction * i);
            if(next.equals(opponent.getPos())) {
                i++;
                next = src.neighbor(0, direction * i);
            }
            if(model.isValidTraversal(prev, next)) {
                prev = next;
                steps++;
            } else
                finished = true;
        }
        return steps;
    }

    public ArrayList<Wall> wallsToIncreasePath(ArrayList<Square> pathToLengthen, Player playerToLengthen, Square otherPlayerPos) {
        ArrayList<Wall> wallMoves = generateWallMoves();
        ArrayList<Wall> usefulWalls = new ArrayList<>();
        for (Wall wall : wallMoves) {
            doVirtualMove(wall.toString());
            ArrayList<Square> newPlayerPath = calculateShortestPath(playerToLengthen, otherPlayerPos);
            undoVirtualMove(wall.toString());
            if(newPlayerPath.size() > pathToLengthen.size()) {
                usefulWalls.add(wall);
            }
        }
        return usefulWalls;
    }

    private void doVirtualMove(String move) {
        if(move.length() == 2) {
            model.movePlayerToSquare(new Square(move));
        }
        else if(move.length() == 3) {
            model.addWallToBoard(new Wall(move));
        }
    }
    private void undoVirtualMove(String move) {
        if(move.length() == 2) {
            model.movePlayerToSquare(new Square(move));
        }
        else if(move.length() == 3) {
            model.removeWallFromBoard(new Wall(move));
        }
    }

    private void makeMove(String move) {
        if(move != null) {
            int turn = model.getTurn();
            if(move.length() == 2) {
                Square sq = new Square(move);
                if(model.commitMove(sq.toString()))
                    viewUpdater.updatePawnPosition(turn, sq.getRow(), sq.getCol());
                else
                    viewUpdater.updatePawnPosition(turn, -1, -1);
            }
            else if(move.length() == 3) {
                Wall wall = new Wall(move);
                if(model.commitMove(wall.toString())) {
                    int row = wall.getStartingSq().getRow()+1;
                    int col = wall.getStartingSq().getCol()+1;
                    if(move.charAt(2) == 'h')
                        viewUpdater.updateHorizontalWall(row, col, turn);
                    else if(move.charAt(2) == 'v')
                        viewUpdater.updateVerticalWall(row, col, turn);
                }
            }
        }
    }
//    public void updateViewHorizontalWall(int row, int col) {
//        HorizontalWall wall1 = view.findHorizontalWallObject(row, col);
//        HorizontalWall wall2 = view.findHorizontalWallObject(row, col + 1);
//        view.fillHorizontalWall(wall1, wall2, true);
//        view.updateInfoPanel((model.getTurn()+1)%2);
//    }
//    public void updateViewVerticalWall(int row, int col) {
//        VerticalWall wall1 = view.findVerticalWallObject(row, col);
//        VerticalWall wall2 = view.findVerticalWallObject(row - 1, col);
//        view.fillVerticalWall(wall1, wall2, true);
//        view.updateInfoPanel((model.getTurn()+1)%2);
//    }
//    public void updateViewPawnPos(int playerTurn, Square move) {
//        if(move == null)
//            return;
//        if(model.commitMove(move.toString())) {
//            view.updatePawnLocation(playerTurn, move.getCol()*TILE_SIZE, (BOARD_SIZE-TILE_SIZE)-move.getRow()*TILE_SIZE);
//            if(model.gameOver()) {
//                view.decideWinner(playerTurn);
//            }
//        } else
//            view.updatePawnLocation(playerTurn, -1, -1);
//    }

    private void setGraph(List<Square>[] graph) { this.graph = graph; }
    private void setWalls(List<Wall> walls) { this.walls = walls; }
    private List<Square>[] getGraph() { return this.graph; }
    private List<Wall> getWalls() { return this.walls; }

    public ArrayList<Square> generatePawnMoves(Square src) {
        ArrayList<Square> validMoves = new ArrayList<>();
        for (Square sq: src.neighbourhood(2)) {
            if (model.isValidTraversal(src, sq)) {
                validMoves.add(sq);
            }
        }
        return validMoves;
    }

    public ArrayList<Wall> generateWallMoves() {
        ArrayList<Wall> validMoves = new ArrayList<>();
        for(int row = 0; row < BOARD_DIMENSION-1; row++) {
            for(int col = 0; col < BOARD_DIMENSION-1; col++) {
                Square sq = new Square(row, col);
                Wall wall = new Wall(sq, Wall.Orientation.HORIZONTAL);
                if(model.isValidWallPlacement(wall))
                    validMoves.add(wall);
            }
        }
        for(int row = 1; row < BOARD_DIMENSION; row++) {
            for(int col = 0; col < BOARD_DIMENSION-1; col++) {
                Square sq = new Square(row, col);
                Wall wall = new Wall(sq, Wall.Orientation.VERTICAL);
                if(model.isValidWallPlacement(wall))
                    validMoves.add(wall);
            }
        }
        return validMoves;
    }
}
