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

    private final Validator validator;
    private final Game view;
    private final int agentID;
    private final Player agent;
    private final Player opponent;
    private List<Square>[] graph;
    private List<Wall> walls;

    public AI(int id, Validator validator, Game view) {
        this.agentID = id;
        this.validator = validator;
        this.view = view;

        this.agent = validator.getPlayer(id);
        this.opponent = validator.getPlayer((id+1)%2);
    }


    public void AiTurn() {
        if(validator.getTurn() == this.agentID) {
            setGraph(validator.getBoardGraph());
            setWalls(validator.getBoardWalls());
            if(agent.getWallsLeft() > 0)
                takeShortestPath();
            else
                comparePlayersPaths();
        }
    }
    private void takeShortestPath() {
        ArrayList<Square> agentPath = shortestPath(agent, opponent);
        Square prev = agent.getPos();
        updateView(agentPath.get(0).toString());
        Square next = agent.getPos();
//        int rowDiff = prev.getRow() - agent.getPos().getRow();
//        int colDiff = prev.getCol() - agent.getPos().getCol();
//        System.out.println(rowDiff + " " + colDiff);
    }
    private ArrayList<Square> shortestPath(Player movingPlayer, Player stander) {
        ArrayList<Square> shorestPath = shortestPathToRow(getGraph(), movingPlayer.getPos(), movingPlayer.getDestRow());
        if(shorestPath.contains(stander.getPos()) && shorestPath.size() == 1)
            shorestPath = generatePawnMoves(movingPlayer.getPos());
        if(shorestPath.contains(stander.getPos()) && shorestPath.size() > 1)
            shorestPath.remove(stander.getPos());
        return shorestPath;
    }

    private void comparePlayersPaths() {
        ArrayList<Square> agentShortestPath = shortestPath(agent, opponent);
        ArrayList<Square> opponentShortestPath = shortestPath(opponent, agent);
        if(agentShortestPath.size() > opponentShortestPath.size()) {
            boolean foundBlockingWall = searchAndPlaceWall(opponentShortestPath, false);
            if(!foundBlockingWall)
                checkOpponentWalls(agentShortestPath, opponentShortestPath);
        } else
            checkOpponentWalls(agentShortestPath, opponentShortestPath);

    }
    private boolean searchAndPlaceWall(ArrayList<Square> opponentPath, boolean closeFastLane) {
        ArrayList<Square> newAgentPath;
        ArrayList<Square> newOpponentPath;
        Wall bestWall = null;
        int minPathDifference = Integer.MAX_VALUE;
        int pathDifference;
        ArrayList<Wall> wallMoves = generateWallMoves();
        wallMoves = removeUselessWalls(wallMoves, opponentPath);

        for(Wall wall : wallMoves) {
            doVirtualMove(wall.toString());
            newAgentPath = shortestPathToRow(getGraph(), agent.getPos(), agent.getDestRow());
            newOpponentPath = shortestPathToRow(getGraph(), opponent.getPos(), opponent.getDestRow());
            pathDifference = newAgentPath.size() - newOpponentPath.size();
            if(pathDifference < minPathDifference) {
                minPathDifference = pathDifference;
                bestWall = wall;
            }
        }
        if(bestWall == null)
            return false;
        updateView(bestWall.toString());
        return true;
    }

    private void checkOpponentWalls(ArrayList<Square> agentPath, ArrayList<Square> opponentPath) {
        if(opponent.getWallsLeft() == 0)
            takeShortestPath();
        else {
            if(doesQuickPathExist(agentPath, opponentPath))
                searchAndPlaceWall(opponentPath, true);
            else
                checkPathWidth(agentPath, opponentPath);
        }
    }
    private void checkPathWidth(ArrayList<Square> agentPath, ArrayList<Square> opponentPath) {
        boolean decidedOnMove = false;
        int width;
        for(int i = 0; i < agentPath.size() && !decidedOnMove; i++) {
            width = calculateWidth(agentPath.get(i));
            if(width <= 2)
                decidedOnMove = handleWeakPoint(agentPath, i, opponentPath);
//            if(!decidedOnMove) {
//
//            }
        }
    }

    private boolean handleWeakPoint(ArrayList<Square> agentPath, int weakPointIndex, ArrayList<Square> opponentPath) {
        int minPathDifference = Integer.MAX_VALUE;
        int pathDifference;
        int newPathLength;
        int width;
        Square weakPoint = agentPath.get(weakPointIndex);
        Square prev = (weakPointIndex == 0) ? agent.getPos() : agentPath.get(weakPointIndex-1);
        ArrayList<Wall> blockingWalls = findBlockingWalls(weakPoint, prev);
        Wall blockingWall = null;
        for(Wall wall : blockingWalls) {
            doVirtualMove(wall.toString());
            width = calculateWidth(weakPoint);
            if(width == 0) {
                newPathLength = shortestPath(opponent, agent).size();
                pathDifference = newPathLength - opponentPath.size();
                if(pathDifference < minPathDifference)
                    blockingWall = wall;
            }
            undoVirtualMove(wall.toString());
        }
        if(blockingWall == null)
            return false;
        doVirtualMove(blockingWall.toString());
        int newAgentPath = shortestPath(agent, opponent).size();
        int newOpponentPath = shortestPath(opponent, agent).size();
        if(newAgentPath <= newOpponentPath)
            return false;
        if(newAgentPath-newOpponentPath <= 2 && newOpponentPath > 5)
            return false;
        return blockWeakPoint();
    }

    private boolean blockWeakPoint() {
        return true;
    }

    /**
     * direction(r, c)
     * down(-1, 0)
     * up(1, 0)
     * left(0, -1)
     * right(0, 1)
     */
    private ArrayList<Wall> findBlockingWalls(Square next, Square prev) {
        int rowDiff = prev.getRow() - next.getRow();
        int colDiff = prev.getCol() - next.getCol();
        ArrayList<Wall> walls = new ArrayList<>();
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
            walls.add(new Wall(startingSq1, 'h'));
            walls.add(new Wall(startingSq2, 'h'));
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
            walls.add(new Wall(startingSq1, 'v'));
            walls.add(new Wall(startingSq2, 'v'));
        }
        walls.removeIf(wall -> !validator.isValidWallPlacement(wall));
        return walls;
    }


    private boolean doesQuickPathExist(ArrayList<Square> agentPath, ArrayList<Square> opponentPath) {
        int quickPathLength = 0;
        int widthAtCurrentSquare;
        boolean quickPathDeviation = false;
        boolean laneExists = true;
        ArrayList<Square> quickPath = new ArrayList<>();

        for(int i = 0; i < opponentPath.size() && laneExists; i++) {
            widthAtCurrentSquare = calculateWidth(opponentPath.get(i));
            if(widthAtCurrentSquare <= 2) {
                quickPathLength++;
                quickPath.add(opponentPath.get(i));
            }
            else { // If the width is bigger than 2.
                if(!quickPathDeviation && i != 0) // If not found a deviation yet AND the current square isn't the first in the opponent's path.
                    quickPathDeviation = true;
                else // If a deviation is found for a square that isn't the first, a quick path doesn't exist.
                    laneExists = false;
            }
        }
        if(quickPathLength > 6 || quickPathLength > opponentPath.size()-2)
            return false;
        for(Square square : quickPath) {
            if(agentPath.contains(square))
                return false;
        }
        return true;
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
            if(validator.isValidTraversal(next, prev)) {
                steps++;
                prev = next;
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
            validator.setCurrentPlayerPos(new Square(move));
        }
        else if(move.length() == 3) {
            validator.addWall(new Wall(move));
        }
    }
    private void undoVirtualMove(String move) {
        if(move.length() == 2) {
            validator.setCurrentPlayerPos(new Square(move));
        }
        else if(move.length() == 3) {
            validator.removeWall(new Wall(move));
        }
    }

    public void updateView(String move) {
        if(move != null) {
            if(move.length() == 2) {
                Square sq = new Square(move);
                updatePawnPosition(validator.getTurn(), sq);
            }
            else if(move.length() == 3) {
                Wall wall = new Wall(move);
                if(validator.wallMoveProcess(wall)) {
                    int row = wall.getStartingSq().getRow()+1;
                    int col = wall.getStartingSq().getCol()+1;
                    if(move.charAt(2) == 'h')
                        updateHorizontalWall(row, col);
                    else if(move.charAt(2) == 'v')
                        updateVerticalWall(row, col);
                }
            }
        }
    }
    public void updateHorizontalWall(int row, int col) {
        HorizontalWall wall1 = view.findHorizontalWallObject(row, col);
        HorizontalWall wall2 = view.findHorizontalWallObject(row, col + 1);
        view.fillHorizontalWall(wall1, wall2, true);
        view.updateInfoPanel((validator.getTurn()+1)%2);
    }
    public void updateVerticalWall(int row, int col) {
        VerticalWall wall1 = view.findVerticalWallObject(row, col);
        VerticalWall wall2 = view.findVerticalWallObject(row - 1, col);
        view.fillVerticalWall(wall1, wall2, true);
        view.updateInfoPanel((validator.getTurn()+1)%2);
    }
    public void updatePawnPosition(int playerTurn, Square move) {
        if(move == null)
            return;
        if(validator.makeMove(move.toString())) {
            view.updatePawnLocation(playerTurn, move.getCol()*TILE_SIZE, (BOARD_SIZE-TILE_SIZE)-move.getRow()*TILE_SIZE);
            if(validator.gameOver()) {
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
            if (validator.isValidTraversal(sq, src)) {
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
                if(validator.isValidWallPlacement(wall))
                    validMoves.add(wall);
            }
        }
        for(int row = 1; row < BOARD_DIMENSION; row++) {
            for(int col = 0; col < BOARD_DIMENSION-1; col++) {
                Square sq = new Square(row, col);
                Wall wall = new Wall(sq, Wall.Orientation.VERTICAL);
                if(validator.isValidWallPlacement(wall))
                    validMoves.add(wall);
            }
        }
        return validMoves;
    }
}
