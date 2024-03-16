package Controller;

import static Controller.ShortestPath.shortestPathToRow;
import Model.Gamestate.*;
import View.Game;
import View.pieces.HorizontalWall;
import View.pieces.VerticalWall;

import java.util.List;
import java.util.ArrayList;

public class AI {
    private static AI instance = null;

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
        String aiMove = null;
        if(validator.getTurn() == this.agentID) {
            setGraph(validator.getBoardGraph());
            setWalls(validator.getBoardWalls());
            aiMove = (agent.getWallsLeft() == 0) ? takeShortestPath() : evaluateAllMoves() ;
        }
        if(aiMove != null) {
//            System.out.println(aiMove);
            updateView(aiMove);

        }
    }
    public String takeShortestPath() {
        ArrayList<Square> shortestPath = shortestPathToRow(graph, agent.getPos(), agent.getDestRow());
        if(shortestPath.contains(opponent.getPos()) && shortestPath.size() == 1)
            shortestPath = generatePawnMoves(agent.getPos());
        if(shortestPath.contains(opponent.getPos()) && shortestPath.size() > 1)
            shortestPath.remove(opponent.getPos());
        return shortestPath.get(0).toString();
    }

    public String FSM() {
        String move = null;
        if(agent.getWallsLeft() > 0)
            takeShortestPath(getGraph(), agent, opponent);
        else {
            boolean isAgentCloser = isAgentCloserToGoal(agent, opponent);
            if(isAgentCloser) {
                if(opponent.getWallsLeft() == 0) {
                    takeShortestPath(getGraph(), agent, opponent);
                }
                else {
                }
            }
            else {
                boolean foundWall = findBestWall(agent, opponent);
                if(!foundWall) {
                    if(opponent.getWallsLeft() == 0) {
                        takeShortestPath(getGraph(), agent, opponent);
                    }
                    else {

                    }
                }
            }
        }
        return move;
    }

    public void takeShortestPath(List<Square>[] graph, Player agent, Player opponent) {
        ArrayList<Square> shortestPath = shortestPathToRow(graph, agent.getPos(), agent.getDestRow());
        if(shortestPath.contains(opponent.getPos()) && shortestPath.size() == 1)
            shortestPath = generatePawnMoves(agent.getPos());
        if(shortestPath.contains(opponent.getPos()) && shortestPath.size() > 1)
            shortestPath.remove(opponent.getPos());
        updateView(shortestPath.get(0).toString());
    }

    public boolean isAgentCloserToGoal(Player agent, Player opponent) {
        int agentPathLength = shortestPathToRow(getGraph(), agent.getPos(), agent.getDestRow()).size();
        int opponentPathLength = shortestPathToRow(getGraph(), opponent.getPos(), opponent.getDestRow()).size();
        return agentPathLength > opponentPathLength;
    }

    public boolean findBestWall(Player agent, Player opponent) {
        ArrayList<Wall> wallMoves = generateWallMoves();
        ArrayList<Square> agentPath = shortestPathToRow(getGraph(), agent.getPos(), agent.getDestRow());
        ArrayList<Square> opponentPath = shortestPathToRow(getGraph(), opponent.getPos(), opponent.getDestRow());
        wallMoves = removeUselessWalls(wallMoves, opponentPath);
        int minPathDifference = Integer.MAX_VALUE;
        int pathDifference;
        Wall bestWall = null;

        for(Wall wall : wallMoves) {
            doVirtualMove(wall.toString());
            pathDifference = agentPath.size() - opponentPath.size();
            if(pathDifference < minPathDifference) {
                minPathDifference = pathDifference;
                bestWall = wall;
            }
        }
        if(bestWall != null) {
            updateView(bestWall.toString());
            return true;
        }
        return false;
    }


    public String evaluateAllMoves() {
        int agentPathLength = shortestPathToRow(getGraph(), agent.getPos(), agent.getDestRow()).size();
        int opponentPathLength = shortestPathToRow(getGraph(), opponent.getPos(), opponent.getDestRow()).size();
        ArrayList<Wall> wallMoves = generateWallMoves();
//        wallMoves = removeUselessWalls(wallMoves);
        ArrayList<Square> pawnMoves = generatePawnMoves(agent.getPos());;
        double maxGrade = Double.NEGATIVE_INFINITY;
        double grade;
        String bestMove = null;
        if (opponentPathLength > 2 || agentPathLength <= opponentPathLength) {
            // Only consider Pawn moves if
            // opponent's path is longer than 2 traversal moves  (if opponent is far from target)
            // OR length of agent's path is less or equal to opponent's path. (if agent is closer to target than opponent)
            for (Square moveTo : pawnMoves) {
                Square currentSquare = agent.getPos();
                doVirtualMove(moveTo.toString());
                grade = heuristicFun();
                undoVirtualMove(currentSquare.toString());
                if (grade > maxGrade) {
                    maxGrade = grade;
                    bestMove = moveTo.toString();
                }
            }
        }
        for(Wall wallToPlace : wallMoves) {
            doVirtualMove(wallToPlace.toString());
            grade = heuristicFun();
            undoVirtualMove(wallToPlace.toString());
            if(grade > maxGrade) {
                maxGrade = grade;
                bestMove = wallToPlace.toString();
            }
        }
        return bestMove;
    }


    public ArrayList<Wall> removeUselessWalls(ArrayList<Wall> wallMoves, ArrayList<Square> opponentPath) {
        ArrayList<Wall> usefulWalls = new ArrayList<>();
        for (Wall wall : wallMoves) {
            doVirtualMove(wall.toString());
            ArrayList<Square> newOpponentPath = shortestPathToRow(getGraph(), opponent.getPos(), opponent.getDestRow());
            if(newOpponentPath.size() > opponentPath.size())
                usefulWalls.add(wall);
            undoVirtualMove(wall.toString());
        }
        return usefulWalls;
    }

//    public boolean isUseful(Wall wall, ArrayList<Square> agentPath, ArrayList<Square> opponentPath) {
//        doVirtualMove(wall.toString());
//        ArrayList<Square> newAgentPath = shortestPathToRow(getGraph(), agent.getPos(), agent.getDestRow());
//        ArrayList<Square> newOpponentPath = shortestPathToRow(getGraph(), opponent.getPos(), opponent.getDestRow());
//        undoVirtualMove(wall.toString());
//        int opponentPathDifference = newOpponentPath.size() - opponentPath.size();
//        return opponentPathDifference > 0;
////        return(opponentPathDifference > 0 && agentPathDifference == 0 || proximityToWalls > 0);
////        return(opponentPathDifference > 0 || proximityToWalls > 0 || agentPathDifference == 0);
//        /*
//        TODO add proximity to other walls
//             blocking potential escape routes for opponent
//             Creating traps/funneling for opponent
//             Wall chaining
//         */
//    }
    public int calculateWallsInRange(Wall wall, int r) {
        int count = 0;
        List<Square> neighbouringSquares = wall.getStartingSq().neighbourhood(r);
        List<Wall> wallOnBoard = getWalls();
        for(Wall curWall : wallOnBoard) {
            if(!wall.equals(curWall)) {
                if(neighbouringSquares.contains(curWall.getStartingSq()))
                    count++;
            }
        }
        return count;
    }

    public double heuristicFun() {
        double score = 0.0;
        Square agentSquare = agent.getPos();
        Square opponentSquare = opponent.getPos();

        int agentDistToGoal = shortestPathToRow(getGraph(), agentSquare, agent.getDestRow()).size();
        int opponentDistToGoal = shortestPathToRow(getGraph(), opponentSquare, opponent.getDestRow()).size();
        int agentWallsLeft = agent.getWallsLeft();
        int opponentWallsLeft = opponent.getWallsLeft();
        int agentManDist = Math.abs(agentSquare.getRow() - agent.getDestRow());
        int opponentManDist = Math.abs(opponentSquare.getRow() - opponent.getDestRow());

        int[] featureScores = new int[3];
        double[] mFeatureWeights = {2.5, 1.5, 2};
        featureScores[0] = opponentDistToGoal - agentDistToGoal;
        featureScores[1] = opponentWallsLeft - agentWallsLeft;
        featureScores[2] = opponentManDist - agentManDist;

        for(int i = 0; i < featureScores.length; i++) {
            score += mFeatureWeights[i] * featureScores[i];
        }
        return score + 0.5 * Math.random();
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
        }
        else
            view.updatePawnLocation(playerTurn, -1, -1);
    }

    private void setGraph(List<Square>[] graph) { this.graph = graph; }
    private void setWalls(List<Wall> walls) { this.walls = walls; }
    private List<Square>[] getGraph() { return this.graph; }
    private List<Wall> getWalls() { return this.walls; }

    public ArrayList<Square> generatePawnMoves(Square src) {
        ArrayList<Square> validMoves = new ArrayList<>();
        for (Square sq: src.neighbourhood(2)) {
            if (validator.isValidTraversal(sq)) {
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
