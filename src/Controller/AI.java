package Controller;
import Model.GameRules;
import Model.Player;
import Model.Square;
import Model.Wall;
import View.Game;

import java.util.List;
import java.util.ArrayList;

import static Utilities.BFS.calculateBFS;
import static Utilities.Constants.*;

public class AI {

    private boolean firstWeakPointThisTurn = true;
    private Wall preventWeakPoint = null;
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
        ArrayList<Square> path = calculatePath(agent, opponent.getPos());
        makeMove(path.get(0).toString());
    }
    private ArrayList<Square> calculatePath(Player movingPlayer, Square otherPlayerPos) {
        ArrayList<Square> shortestPath = calculateBFS(getGraph(), movingPlayer.getPos(), movingPlayer.getDestRow());
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
                    currentPath = calculateBFS(getGraph(), firstMove, movingPlayer.getDestRow());
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

    private void comparePaths( ArrayList<Square> agentShortestPath,
                               ArrayList<Square> opponentShortestPath,
                               int extraForOpponent){
        boolean decisionMade = false;
        if(isOpponentCloser(agentShortestPath, opponentShortestPath, extraForOpponent)) {
            decisionMade = searchAndPlaceWall(opponentShortestPath);
        }
        if(!decisionMade)
            checkIfOpponentHasWalls(agentShortestPath, opponentShortestPath);
    }

    private void comparePlayersPaths() {
        ArrayList<Square> agentShortestPath = calculatePath(agent, opponent.getPos());
        ArrayList<Square> opponentShortestPath = calculatePath(opponent, agent.getPos());
        if(isAgentStarting()) {
                comparePaths(agentShortestPath, opponentShortestPath,0);
        }
        else {
            if(isAfterFifthRound()) {
                comparePaths(agentShortestPath, opponentShortestPath,0);
            }
            else {
                comparePaths(agentShortestPath, opponentShortestPath,2);
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
        if (opponent.getWallsLeft() == 0)
            takeShortestPath();
        else {
            ArrayList<Square> quickPath = checkForQuickPath(agentPath, opponentPath);
            if(quickPath == null) {
                searchForWeakPoints(agentPath, opponentPath);
            } else {
                searchAndPlaceWall(opponentPath);
            }
        }
    }
    private void blockQuickPath(ArrayList<Square> opponentPath, ArrayList<Square> quickPath) {
        System.out.println(quickPath);
        int bestDifference = Integer.MIN_VALUE;
        int newAgentPath, newOpponentPath, pathDifference;
        ArrayList<Wall> increasePathWalls = disruptiveWalls(opponentPath, opponent, agent.getPos());
        Wall firstWall = null;
        System.out.println(quickPath);
        for(Wall wall : increasePathWalls) {
            doVirtualMove(wall.toString());
            newAgentPath = calculatePath(agent, opponent.getPos()).size();
            newOpponentPath = calculatePath(opponent, agent.getPos()).size();
            pathDifference = newOpponentPath-opponentPath.size();
//            System.out.println(wall + " " + pathDifference);
            if(pathDifference > bestDifference) {
                bestDifference = pathDifference;
                firstWall = wall;
            }
            undoVirtualMove(wall.toString());
        }
    }

    private ArrayList<Square> checkForQuickPath(ArrayList<Square> agentPath, ArrayList<Square> opponentPath) {
        boolean foundDeviation = false;
        boolean laneExists = true;
        int width;
        ArrayList<Square> quickPath = new ArrayList<>();
        opponentPath.add(0, opponent.getPos());
        for(int i = 0; i < opponentPath.size() && laneExists; i++) {
            width = calculateWidthAtSquare(opponentPath.get(i));
            if (width <= 2 ) {
                quickPath.add(opponentPath.get(i));
            } else if (!foundDeviation && i != 0) { // If there wasn't a deviation yet
                foundDeviation = true;
            } else
                laneExists = false;
        }
        opponentPath.remove(0);
        if(quickPath.size() < 6) {
            return null;
        } if(quickPath.size() > opponentPath.size()-2)
            return null;
        for(Square square : quickPath) {
            if(agentPath.contains(square))
                return null;
        }
        if(opponent.getPos().equals(quickPath.get(0)))
            return quickPath;
        return null;
    }
    private boolean searchAndPlaceWall(ArrayList<Square> opponentPath) {
        int bestDifference = Integer.MIN_VALUE;
        int newAgentPath, newOpponentPath, pathDifference;
        ArrayList<Wall> increasePathWalls = disruptiveWalls(opponentPath, opponent, agent.getPos());
        Wall bestWall = null;
        for(Wall wall : increasePathWalls) {
            doVirtualMove(wall.toString());
            newAgentPath = calculatePath(agent, opponent.getPos()).size();
            newOpponentPath = calculatePath(opponent, agent.getPos()).size();
            pathDifference = newAgentPath-newOpponentPath;
            if(pathDifference > bestDifference) {
                bestDifference = pathDifference;
                bestWall = wall;
            }
            undoVirtualMove(wall.toString());
        }
        if(bestWall == null)
            return false;
        System.out.println(bestWall);
        makeMove(bestWall.toString());
        return true;
    }

    private void searchForWeakPoints(ArrayList<Square> agentPath, ArrayList<Square> opponentPath) {
        boolean decisionMade = false;
        int width;
        agentPath.add(0, agent.getPos());
        for(int i = 0; i < agentPath.size()-1 && !decisionMade; i++) {
            width = calculateWidthAtSquare(agentPath.get(i));
            if(width <= 2)
                decisionMade = handleWeakPoint(agentPath, i, opponentPath);
        }
        agentPath.remove(0);
        if(!decisionMade) {
            if(preventWeakPoint == null) {
                takeShortestPath();
            } else
                makeMove(preventWeakPoint.toString());
        }
        firstWeakPointThisTurn = true;
    }
    private boolean handleWeakPoint(ArrayList<Square> agentPath, int weakPointIndex, ArrayList<Square> originalOpponentPath) {
        int newOpponentPathLength;
        Wall weakPointUtilized = null;
        Square weakPoint = agentPath.get(weakPointIndex);
        Square next = agentPath.get(weakPointIndex+1);
        ArrayList<Wall> walls = blockCrossing(weakPoint, next);
        for(Wall wall : walls) {
            doVirtualMove(wall.toString());
            newOpponentPathLength = calculatePath(opponent, agent.getPos()).size();
            if(newOpponentPathLength == originalOpponentPath.size())
                weakPointUtilized = wall;
            undoVirtualMove(wall.toString());
        }
        if(weakPointUtilized == null)
            return false;
        doVirtualMove(weakPointUtilized.toString());
        int myPathLength = calculatePath(agent, opponent.getPos()).size();
        int oppPathLength = calculatePath(opponent, agent.getPos()).size();
        undoVirtualMove(weakPointUtilized.toString());
        if(myPathLength <= oppPathLength)
            return false;
        if(myPathLength - oppPathLength <= 2 && oppPathLength >= 5)
            return false;
        return blockWeakPoint(weakPointUtilized);
    }

    private boolean blockWeakPoint(Wall weakPointUtilized) {
        ArrayList<Square> alternatePath;
        if(firstWeakPointThisTurn) {
            Square originalPos = agent.getPos();
            doVirtualMove(weakPointUtilized.toString());
            alternatePath = calculatePath(agent, opponent.getPos());
            Wall blockingWall;
            for(int i = 0; i < alternatePath.size()-1; i++) {
                doVirtualMove(alternatePath.get(i).toString());
                blockingWall = getPathBlockingWall();
                if(blockingWall != null) {
                    undoVirtualMove(weakPointUtilized.toString());
                    if(!blockingWall.equals(getPathBlockingWall()))
                        preventWeakPoint = blockingWall;
                    doVirtualMove(weakPointUtilized.toString());
                }
            }
            undoVirtualMove(weakPointUtilized.toString());
            doVirtualMove(originalPos.toString());
            if(preventWeakPoint != null) {
                firstWeakPointThisTurn = false;
                return false;
            }
        }
        doVirtualMove(weakPointUtilized.toString());
        alternatePath = calculatePath(agent, opponent.getPos());
        undoVirtualMove(weakPointUtilized.toString());
        makeMove(alternatePath.get(0).toString());
        return true;
    }

    private Wall getPathBlockingWall() {
        Wall pathBlocker = null;
        for(int row = 0; row < BOARD_DIMENSION-1; row++) {
            for(int col = 0; col < BOARD_DIMENSION-1; col++) {
                Square sq = new Square(row, col);
                Wall wall = new Wall(sq, Wall.Orientation.HORIZONTAL);
                if(model.doesWallBlockGoal(wall)) {
                    pathBlocker = wall;
                }
            }
        }
        for(int row = 1; row < BOARD_DIMENSION; row++) {
            for(int col = 0; col < BOARD_DIMENSION-1; col++) {
                Square sq = new Square(row, col);
                Wall wall = new Wall(sq, Wall.Orientation.VERTICAL);
                if(model.doesWallBlockGoal(wall)) {
                    pathBlocker = wall;
                }
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
                startingSq2 = startingSq1.neighbor(1, 0); // one up
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

    private int calculateWidthAtSquare(Square src) {
        int steps = moveInDirection(src, 1);
        steps += moveInDirection(src, -1);
        return steps+1;
    }
    private int moveInDirection(Square src, int direction) {
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

    /**
     *
     * @param pathToLengthen
     * @param playerToLengthen
     * @param otherPlayerPos
     * @return
     */
    public ArrayList<Wall> disruptiveWalls(ArrayList<Square> pathToLengthen, Player playerToLengthen, Square otherPlayerPos) {
        ArrayList<Wall> wallMoves = generateWallMoves();
        ArrayList<Wall> usefulWalls = new ArrayList<>();
        for (Wall wall : wallMoves) {
            doVirtualMove(wall.toString());
            ArrayList<Square> newPlayerPath = calculatePath(playerToLengthen, otherPlayerPos);
            undoVirtualMove(wall.toString());
            if(newPlayerPath.size() > pathToLengthen.size())
                usefulWalls.add(wall);
        }
        return usefulWalls;
    }

    private void doVirtualMove(String move) {
        if(move.length() == 2)
            model.movePlayerToSquare(new Square(move));
        else if(move.length() == 3)
            model.addWallToBoard(new Wall(move));
    }
    private void undoVirtualMove(String move) {
        if(move.length() == 2)
            model.movePlayerToSquare(new Square(move));
        else if(move.length() == 3)
            model.removeWallFromBoard(new Wall(move));
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
