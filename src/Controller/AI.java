package Controller;
import Model.Gamestate.*;

import static Controller.Utility.shortestPathToRow;
//import static Controller.Utility.countOpenPathsToGoal;


import java.util.*;

public class AI {
    public static final int BOARD_DIMENSION = 9;
    private int agentID;
    private Controller controller;
    private Player agent;
    private Player opponent;
    private List<Square>[] graph;
    private static double totalScore = 0;
    private static int totalMoves;

    public AI(int id, Controller controller) {
        this.agentID = id;
        this.controller = controller;
        this.agent = controller.getPlayer(id);
        this.opponent = controller.getPlayer((this.agentID +1)%2);
    }

    public String generateMove(List<Square>[] graph,List<Wall> walls) {
        setGraph(graph);
        String move;
        if(agent.getWallsLeft() == 0) {
            ArrayList<Square> shortestPath = shortestPathToRow(graph, agent.getPos(), agent.getDestRow());
            if(shortestPath.contains(opponent.getPos()) && shortestPath.size() == 1)
                shortestPath = generatePawnMoves(agent.getPos());
            if(shortestPath.contains(opponent.getPos()) && shortestPath.size() > 1)
                shortestPath.remove(opponent.getPos());
            move = shortestPath.get(0).toString();
        }
        else {
            move = determineMove();
        }
        return move;
    }

    public String determineMove() {
        ArrayList<Wall> wallMoves = generateWallMoves();
        wallMoves = removeUselessWalls(wallMoves);
        ArrayList<Square> pawnMoves = generatePawnMoves(agent.getPos());

        double bestEval = Double.NEGATIVE_INFINITY;
        double postMoveEval;
        int bestMoveIndex = 0;
        boolean isPawnMove = false;
        for(int i = 0; i < pawnMoves.size(); i++) {
            Square prev = controller.movePawn(pawnMoves.get(i));
            postMoveEval = evaluate();
            if(postMoveEval > bestEval) {
                bestEval = postMoveEval;
                isPawnMove = true;
                bestMoveIndex = i;
            }
            controller.movePawn(prev);
        }
        for(int j = 0; j < wallMoves.size(); j++) {
            controller.placeWallInModel(wallMoves.get(j));
            postMoveEval = evaluate();
            if(postMoveEval > bestEval) {
                bestEval = postMoveEval;
                isPawnMove = false;
                bestMoveIndex = j;
            }
            controller.removeWall(wallMoves.get(j));
        }
        totalScore = 0;
        totalMoves = 0;
        return (isPawnMove) ? pawnMoves.get(bestMoveIndex).toString() : wallMoves.get(bestMoveIndex).toString();
    }

    public ArrayList<Wall> removeUselessWalls(ArrayList<Wall> wallMoves) {
        totalMoves = wallMoves.size();
        double score = 0;
        double[] wallScore = new double[wallMoves.size()];
        double wallAvg = 0;
        ArrayList<Square> agentPath = shortestPathToRow(getGraph(), agent.getPos(), agent.getDestRow());
        ArrayList<Square> opponentPath = shortestPathToRow(getGraph(), opponent.getPos(), opponent.getDestRow());
        ArrayList<Wall> usefulWalls = new ArrayList<>();
        for(int i = 0; i < wallMoves.size(); i++) {
            wallScore[i] = wallScore(wallMoves.get(i), agentPath, opponentPath);
            wallAvg = wallScore[i];
        }
//        System.out.println("total: " + wallAvg);
        wallAvg /= totalMoves;
//        System.out.println("average: " + wallAvg);

        return usefulWalls;
    }

    public double wallScore(Wall wall, ArrayList<Square> agentPath, ArrayList<Square> opponentPath) {
        double score = 0.0;
        controller.placeWallInModel(wall);
        ArrayList<Square> newAgentPath = shortestPathToRow(getGraph(), agent.getPos(), agent.getDestRow());
        ArrayList<Square> newOpponentPath = shortestPathToRow(getGraph(), opponent.getPos(), opponent.getDestRow());
        controller.removeWall(wall);
        Square startingSq = wall.getStartingSq();
        int agentPathDifference = newAgentPath.size() - agentPath.size();
        int opponentPathDifference = newOpponentPath.size() - opponentPath.size();
        int wallDistanceFromAgent = Math.abs(startingSq.getRow() - agent.getPos().getRow()) + Math.abs(startingSq.getCol() - agent.getPos().getCol());
        int wallDistanceFromOpponent = Math.abs(startingSq.getRow() - opponent.getPos().getRow()) + Math.abs(startingSq.getCol() - opponent.getPos().getCol());

        score += ((agentPathDifference > 0) ? -20*agentPathDifference : 10);
        score += ((opponentPathDifference > 0) ? 20*opponentPathDifference : -10);
        score += (wallDistanceFromOpponent - wallDistanceFromAgent) * ((wallDistanceFromOpponent > wallDistanceFromAgent) ? 10 : -10); // Closer to opponent = better.
        // opp > agent = + * +
        // opp < agent = - * -
        totalScore += score;
        /*
        TODO add proximity to other walls
             blocking potential escape routes for opponent
             Creating traps/funneling for opponent
             Wall chaining
         */
//        System.out.println(score);
        return score;
    }

    public double evaluate() {
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
        return score + 0.1 * Math.random();
    }



    private void setGraph(List<Square>[] graph) {
        this.graph = graph;
    }
    private List<Square>[] getGraph() { return this.graph; }

    public ArrayList<Square> generatePawnMoves(Square src) {
        ArrayList<Square> validMoves = new ArrayList<>();
        for (Square sq: src.neighbourhood(2)) {
            if (controller.isValidTraversal(sq)) {
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
                if(controller.isValidWallPlacement(wall))
                    validMoves.add(wall);
            }
        }
        for(int row = 1; row < BOARD_DIMENSION; row++) {
            for(int col = 0; col < BOARD_DIMENSION-1; col++) {
                Square sq = new Square(row, col);
                Wall wall = new Wall(sq, Wall.Orientation.VERTICAL);
                if(controller.isValidWallPlacement(wall))
                    validMoves.add(wall);
            }
        }
        return validMoves;
    }



}
