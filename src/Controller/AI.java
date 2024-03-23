package Controller;

import static Controller.ShortestPath.shortestPathToRow;
import Model.Gamestate.*;
import View.Game;
import View.pieces.HorizontalWall;
import View.pieces.VerticalWall;

import java.util.List;
import java.util.ArrayList;

public class AI {

    public static final int TILE_SIZE = 50;
    public static final int BOARD_DIMENSION = 9;
    public static final int BOARD_SIZE = TILE_SIZE*BOARD_DIMENSION;

    private boolean isFirstWeakpoint = true;
    private Wall wallBlockingWeakSpot = null;
    private final Model model;
    private final Game view;
    private final int agentID;
    private final Player agent;
    private final Player opponent;
    private List<Square>[] graph;
    private List<Wall> walls;

    public AI(int id, Model model, Game view) {
        this.agentID = id;
        this.model = model;
        this.view = view;

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
        ArrayList<Square> agentPath = calculateShortestPath(agent, opponent);
        makeMove(agentPath.get(1).toString());
    }
    private ArrayList<Square> calculateShortestPath(Player movingPlayer, Player stander) {
        ArrayList<Square> shorestPath = shortestPathToRow(getGraph(), movingPlayer.getPos(), movingPlayer.getDestRow());
        if(shorestPath.contains(stander.getPos()) && shorestPath.size() == 1)
            shorestPath = generatePawnMoves(movingPlayer.getPos());
        if(shorestPath.contains(stander.getPos()) && shorestPath.size() > 1)
            shorestPath.remove(stander.getPos());
        return shorestPath;
    }

    private void comparePlayersPaths() {
        ArrayList<Square> agentShortestPath = calculateShortestPath(agent, opponent);
        ArrayList<Square> opponentShortestPath = calculateShortestPath(opponent, agent);
        boolean foundBlockingWall = false;
        if (model.getStartingPlayer() == agentID || model.getMoveNum() > 10) {
            if (agentShortestPath.size() > opponentShortestPath.size()) {
                foundBlockingWall = searchAndPlaceWall(opponentShortestPath);
            }
        } else {
            if (agentShortestPath.size() > opponentShortestPath.size() + 2) {
                foundBlockingWall = searchAndPlaceWall(opponentShortestPath);
            }
        }
        if (!foundBlockingWall) {
            checkOpponentWalls(agentShortestPath, opponentShortestPath);
        }
    }

    private void checkOpponentWalls(ArrayList<Square> agentPath, ArrayList<Square> opponentPath) {
        if (opponent.getWallsLeft() == 0)
            takeShortestPath();
        else {
            if (!isOpponentQuickPath(agentPath, opponentPath)) {
                checkAIPathWidth(agentPath, opponentPath);
            } else {
                searchAndPlaceWallB(opponentPath, true);
//                if (!searchAndPlaceWallB(opponentPath, true))
//                    checkAIPathWidth(agentPath, opponentPath);
            }
        }
    }

    private boolean isOpponentQuickPath(ArrayList<Square> agentPath, ArrayList<Square> opponentPath) {
        int quickPathLength = 0;
        int widthAtCurrentSquare;
        boolean foundDeviation = false;
        boolean laneExists = true;
        ArrayList<Square> quickPath = new ArrayList<>();
        for(int i = 0; i < opponentPath.size() && laneExists; i++) {
            widthAtCurrentSquare = calculateWidth(opponentPath.get(i));
            if(widthAtCurrentSquare <= 2) {
                quickPathLength++;
                quickPath.add(opponentPath.get(i));
            }
            else { // If the width is bigger than 2.
                if(!foundDeviation && i != 0) // If not found a deviation yet AND the current square isn't the first in the opponent's path.
                    foundDeviation = true;
                else // If a deviation is found for a square that isn't the first, a quick path doesn't exist.
                    laneExists = false;
            }
        }
        if(quickPathLength < 6)
            return false;
        if(quickPathLength > opponentPath.size()-2)
            return false;
        for(Square square : quickPath) {
            if(agentPath.contains(square))
                return false;
        }
        return opponent.getPos().equals(quickPath.get(0));
    }
    private boolean searchAndPlaceWall(ArrayList<Square> opponentPath) {
        return searchAndPlaceWallB(opponentPath, false);
    }
    private boolean searchAndPlaceWall(ArrayList<Square> opponentPath, boolean opponentFastLane) {
        ArrayList<Square> newAgentPath;
        ArrayList<Square> newOpponentPath;
        ArrayList<Wall> wallMoves = removeUselessWalls(generateWallMoves(), opponentPath);
        Wall bestWall = null;
        int minPathDifference = Integer.MAX_VALUE;
        int pathDifference;
        for(Wall wall : wallMoves) {
            doVirtualMove(wall.toString());
            newAgentPath = shortestPathToRow(getGraph(), agent.getPos(), agent.getDestRow());
            newOpponentPath = shortestPathToRow(getGraph(), opponent.getPos(), opponent.getDestRow());
            pathDifference = newAgentPath.size() - newOpponentPath.size();
            System.out.println(wall + " " + (!opponentFastLane) + " " +  (newOpponentPath.size() - opponentPath.size() > 1) + " " + (newOpponentPath.size() - opponentPath.size()));
            if (!opponentFastLane || newOpponentPath.size() - opponentPath.size() > 1) {
                if (pathDifference < minPathDifference) {
                    minPathDifference = pathDifference;
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
    private boolean searchAndPlaceWallB(ArrayList<Square> opponentPath, boolean opponentFastLane) {
        int bestDifference = (opponentFastLane) ? Integer.MIN_VALUE : Integer.MAX_VALUE;
        int newAgentPath, newOpponentPath, pathDifference;
        ArrayList<Wall> blockingWalls;
        Wall bestWall = null;
        for(int i = 0; i < opponentPath.size()-1; i++) {
            blockingWalls = blockCrossing(opponentPath.get(i), opponentPath.get(i+1));
            for(Wall wall : blockingWalls) {
                doVirtualMove(wall.toString());
                newAgentPath = calculateShortestPath(agent, opponent).size();
                newOpponentPath = calculateShortestPath(opponent, agent).size();
                pathDifference = newAgentPath-newOpponentPath;
                if(!opponentFastLane) {
                    if(pathDifference < bestDifference) {
                        bestDifference = pathDifference;
                        bestWall = wall;
                    }
                }
                else {
                    if(pathDifference > bestDifference) {
                        bestDifference = pathDifference;
                        bestWall = wall;
                    }
                }
                undoVirtualMove(wall.toString());
            }
        }
//        System.out.println(bestWall);
        if(bestWall == null)
            return false;
        makeMove(bestWall.toString());
        return true;
    }

    private void checkAIPathWidth(ArrayList<Square> agentPath, ArrayList<Square> opponentPath) {
        boolean decidedOnMove = false;
        int width;
        for(int i = 1; i < agentPath.size() && !decidedOnMove; i++) {
            width = calculateWidth(agentPath.get(i));
            if(width <= 2) {
                decidedOnMove = handleWeakPoint(agentPath, i, opponentPath);
            }
        }
        if(!decidedOnMove) {
            if(this.wallBlockingWeakSpot == null) {
                takeShortestPath();
            }
            else
                makeMove(this.wallBlockingWeakSpot.toString());
        }
    }
    private boolean handleWeakPoint(ArrayList<Square> agentPath, int weakPointIndex, ArrayList<Square> originalOpponentPath) {
        int minPathDifference = Integer.MAX_VALUE;
        int pathDifference;
        int newOpponentPathLength;
        int width;
        Square weakPoint = agentPath.get(weakPointIndex);
        Square prev = agentPath.get(weakPointIndex-1);
        ArrayList<Wall> traversalBlockingWall = blockCrossing(weakPoint, prev);
//        System.out.println(traversalBlockingWall);
//        System.out.println(prev + "->" + weakPoint);
        Wall traversalBlockerWall = null; // Wall that the opponent may place that creates a weak point for the agent
        for(Wall wall : traversalBlockingWall) {
            doVirtualMove(wall.toString());
            width = calculateWidth(weakPoint);
            if(width == 0) {
                newOpponentPathLength = calculateShortestPath(opponent, agent).size();
                pathDifference = newOpponentPathLength - originalOpponentPath.size();
                if(pathDifference < minPathDifference && pathDifference <= 2) // TODO remove pathdifference <=2 if its bad
                    traversalBlockerWall = wall;
            }
            undoVirtualMove(wall.toString());
        }
        if(traversalBlockerWall == null)
            return false;
        doVirtualMove(traversalBlockerWall.toString());
        int newAgentPath = calculateShortestPath(agent, opponent).size();
        int newOpponentPath = calculateShortestPath(opponent, agent).size();
        undoVirtualMove(traversalBlockerWall.toString());
        if(newAgentPath <= newOpponentPath)
            return false;
        if(newAgentPath-newOpponentPath <= 2 && newOpponentPath > 5)
            return false;
        return blockWeakPoint(traversalBlockerWall);
    }

    private boolean blockWeakPoint(Wall wall) {
        if(this.wallBlockingWeakSpot == null) {
            doVirtualMove(wall.toString());
            ArrayList<Square> alternatePath = calculateShortestPath(agent, opponent);
            Wall pathBlockingWall;
            for(Square sq : alternatePath) {
                Square originalPos = agent.getPos();
                doVirtualMove(sq.toString());
                pathBlockingWall = getPathBlockingWall();
                undoVirtualMove(wall.toString());
                if(pathBlockingWall != null && model.isValidWallPlacement(wall)) {
                    this.wallBlockingWeakSpot = pathBlockingWall;
                    undoVirtualMove(originalPos.toString());
                    return false;
                }
                undoVirtualMove(originalPos.toString());
            }
        }
        takeShortestPath();

        return true;
    }
    private Wall getPathBlockingWall() {
        for(int row = 0; row < BOARD_DIMENSION-1; row++) {
            for(int col = 0; col < BOARD_DIMENSION-1; col++) {
                Square sq = new Square(row, col);
                Wall wall = new Wall(sq, Wall.Orientation.HORIZONTAL);
                if(model.doesWallBlockGoal(wall))
                    return wall;
            }
        }
        for(int row = 1; row < BOARD_DIMENSION; row++) {
            for(int col = 0; col < BOARD_DIMENSION-1; col++) {
                Square sq = new Square(row, col);
                Wall wall = new Wall(sq, Wall.Orientation.VERTICAL);
                if(model.doesWallBlockGoal(wall))
                    return wall;
            }
        }
        return null;
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
//        System.out.println(rowDiff + " " + colDiff + "\n\n");
//        System.out.println(prev + " " + next + "\n\n");
        ArrayList<Wall> blockWeakSpots = new ArrayList<>();
        Square startingSq1 = null, startingSq2 = null;
        if(rowDiff == 0) { // Moved horizontally
            if(colDiff == -1) { // Moved right
                startingSq1 = prev;
                startingSq2 = startingSq1.neighbor(1, 0); // one up
            }
            if(colDiff == 1) { // Moved left
                startingSq1 = prev.neighbor(0, -1); // one left
                startingSq2 = startingSq1.neighbor(-1, 0); // one up
            }
            blockWeakSpots.add(new Wall(startingSq1, 'v'));
            blockWeakSpots.add(new Wall(startingSq2, 'v'));
        }
        if(colDiff == 0) { // Moved vertically
            if(rowDiff == 1) { // Moved down
                startingSq1 = prev.neighbor(-1, 0); // one down
                startingSq2 = startingSq1.neighbor(0, -1); // one left
            }
            if(rowDiff == -1) { // Moved up
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
        return steps;
    }
    public int moveSideways(Square src, int direction) {
        int steps = 0;
        boolean finished = false;
        Square next, prev = src;

        for (int i = 1; i < BOARD_DIMENSION && !finished; i++) {
            next = src.neighbor(0, direction * i);
            if(model.isValidTraversal(next, prev)) {
                prev = next;
                steps++;
            } else
                finished = true;
        }
        return steps;
    }

    public ArrayList<Wall> removeUselessWalls(ArrayList<Wall> wallMoves, ArrayList<Square> opponentPath) {
        ArrayList<Wall> usefulWalls = new ArrayList<>();
        for (Wall wall : wallMoves) {
            doVirtualMove(wall.toString());
            ArrayList<Square> newOpponentPath = shortestPathToRow(getGraph(), opponent.getPos(), opponent.getDestRow());
            undoVirtualMove(wall.toString());
            if(newOpponentPath.size() > opponentPath.size()) {
                usefulWalls.add(wall);
            }
        }
        return usefulWalls;
    }

    private void doVirtualMove(String move) {
        if(move.length() == 2) {
            model.setCurrentPlayerPos(new Square(move));
        }
        else if(move.length() == 3) {
            model.addWall(new Wall(move));
        }
    }
    private void undoVirtualMove(String move) {
        if(move.length() == 2) {
            model.setCurrentPlayerPos(new Square(move));
        }
        else if(move.length() == 3) {
            model.removeWall(new Wall(move));
        }
    }

    public void makeMove(String move) {
        if(move != null) {
            if(move.length() == 2) {
                Square sq = new Square(move);
                updateViewPawnPos(model.getTurn(), sq);
            }
            else if(move.length() == 3) {
                Wall wall = new Wall(move);
                if(model.placeWall(wall)) {
                    int row = wall.getStartingSq().getRow()+1;
                    int col = wall.getStartingSq().getCol()+1;
                    if(move.charAt(2) == 'h')
                        updateViewHorizontalWall(row, col);
                    else if(move.charAt(2) == 'v')
                        updateViewVerticalWall(row, col);
                }
            }
        }
    }
    public void updateViewHorizontalWall(int row, int col) {
        HorizontalWall wall1 = view.findHorizontalWallObject(row, col);
        HorizontalWall wall2 = view.findHorizontalWallObject(row, col + 1);
        view.fillHorizontalWall(wall1, wall2, true);
        view.updateInfoPanel((model.getTurn()+1)%2);
    }
    public void updateViewVerticalWall(int row, int col) {
        VerticalWall wall1 = view.findVerticalWallObject(row, col);
        VerticalWall wall2 = view.findVerticalWallObject(row - 1, col);
        view.fillVerticalWall(wall1, wall2, true);
        view.updateInfoPanel((model.getTurn()+1)%2);
    }
    public void updateViewPawnPos(int playerTurn, Square move) {
        if(move == null)
            return;
        if(model.commitMove(move.toString())) {
            view.updatePawnLocation(playerTurn, move.getCol()*TILE_SIZE, (BOARD_SIZE-TILE_SIZE)-move.getRow()*TILE_SIZE);
            if(model.gameOver()) {
                view.decideWinner(playerTurn);
            }
        } else
            view.updatePawnLocation(playerTurn, -1, -1);
    }

    private void setGraph(List<Square>[] graph) { this.graph = graph; }
    private void setWalls(List<Wall> walls) { this.walls = walls; }
    private List<Square>[] getGraph() { return this.graph; }
    private List<Wall> getWalls() { return this.walls; }

    public ArrayList<Square> generatePawnMoves(Square src) {
        ArrayList<Square> validMoves = new ArrayList<>();
        for (Square sq: src.neighbourhood(2)) {
            if (model.isValidTraversal(sq, src)) {
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
