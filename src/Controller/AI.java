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
    private static int totalScore = 0;
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
            move = determineMove(agent.getPos(), opponent.getPos());
        }
        return move;
    }

    public String determineMove(Square src, Square other) {
        ArrayList<Wall> wallMoves = generateWallMoves();
        totalMoves = wallMoves.size();
        wallMoves = removeUselessWalls(wallMoves);
        System.out.println(totalScore + " " + totalMoves + " " + (totalScore/totalMoves) + " " + wallMoves.size());
        totalScore = 0;
        totalMoves = 0;
        ArrayList<Square> pawnMoves = generatePawnMoves(src);
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
            controller.placeWall(wallMoves.get(j));
            postMoveEval = evaluate();
            if(postMoveEval > bestEval) {
                bestEval = postMoveEval;
                isPawnMove = false;
                bestMoveIndex = j;
            }
            controller.removeWall(wallMoves.get(j));
        }
        return (isPawnMove) ? pawnMoves.get(bestMoveIndex).toString() : wallMoves.get(bestMoveIndex).toString();
    }

    public ArrayList<Wall> removeUselessWalls(ArrayList<Wall> walls) {
        ArrayList<Square> agentPath = shortestPathToRow(getGraph(), agent.getPos(), agent.getDestRow());
        ArrayList<Square> opponentPath = shortestPathToRow(getGraph(), opponent.getPos(), opponent.getDestRow());
        ArrayList<Wall> usefulWalls = new ArrayList<>();
        for(Wall wall : walls) {
            if(isUsefulWall(wall, agentPath, opponentPath))
                usefulWalls.add(wall);
        }
        return usefulWalls;
    }

    public boolean isUsefulWall(Wall wall, ArrayList<Square> agentPath, ArrayList<Square> opponentPath) {
        double score = 0.0;
        controller.placeWall(wall);
        ArrayList<Square> newAgentPath = shortestPathToRow(getGraph(), agent.getPos(), agent.getDestRow());
        ArrayList<Square> newOpponentPath = shortestPathToRow(getGraph(), opponent.getPos(), opponent.getDestRow());
        controller.removeWall(wall);
        Square startingSq = wall.getStartingSq();
        int agentPathSize = agentPath.size();
        int newAgentPathSize = newAgentPath.size();
        int opponentPathSize = opponentPath.size();
        int newOpponentPathSize = newOpponentPath.size();
        int distanceFromAgent = Math.abs(startingSq.getRow() - agent.getPos().getRow())
                + Math.abs(startingSq.getCol() - agent.getPos().getCol());
        int distanceFromOpponent = Math.abs(startingSq.getRow() - opponent.getPos().getRow())
                + Math.abs(startingSq.getCol() - opponent.getPos().getCol());
        score += (newAgentPathSize-agentPathSize > 0) ? -10*(newAgentPathSize-agentPathSize) : 10;
        score += (newOpponentPathSize-opponentPathSize > 0) ? 10*(newOpponentPathSize-opponentPathSize) : -10;
        score+= (distanceFromOpponent > distanceFromAgent) ? 10*(distanceFromOpponent-distanceFromAgent)
                : -10*(distanceFromOpponent-distanceFromAgent);
        totalScore += score;
        /*
        TODO add proximity to other walls
             blocking potential escape routes for opponent
             Creating traps/funneling for opponent
             Wall chaining
         */
        return score > 50;
    }

    public double evaluate() {
        double score = 0.0;
        double maxPath = BOARD_DIMENSION*(BOARD_DIMENSION-1);
        double maxDistance = BOARD_DIMENSION-1;
        double maxWalls = 10;
        Square agentSquare = agent.getPos();
        Square opponentSquare = opponent.getPos();

        // evaluate the board based on the max player's perspective
        // currently we only consider shortest path length and remaining walls
        //Q.debug();
        int agentDistToGoal = shortestPathToRow(getGraph(), agentSquare, agent.getDestRow()).size();
        int opponentDistToGoal = shortestPathToRow(getGraph(), opponentSquare, opponent.getDestRow()).size();
//        if(playerDistToGoal == 0 || opponentDistToGoal == 0) {
//            //Str.println("Something fucked up");
//            //Q.display();
//            return -10000000;
//        }
        //int maxPath = maxBFS[0];
        //int minPath = minBFS[0];
        int agentWallsLeft = agent.getWallsLeft();
        int opponentWallsLeft = opponent.getWallsLeft();
        int agentManDist = Math.abs(agentSquare.getRow() - agent.getDestRow());
        int opponentManDist = Math.abs(opponentSquare.getRow() - opponent.getDestRow());

        /* Previously test working */
        //Str.println("Max path: " + maxPath + " Min path: " + minPath + " Max Walls: " + maxWalls + " min walls " + minWalls);
        // Evaluate Score: Min's Path - Max's Path + Max's Walls - Min's Walls
        //double score = minBFS[0] - maxBFS[0] + (maxWalls - minWalls);

        int[] featureScores = new int[6];
        double[] mFeatureWeights = {1.5, -1.5, 1, -1, 0.7, -0.7};
        featureScores[0] = agentDistToGoal;
        featureScores[1] = opponentManDist;
        featureScores[2] = agentWallsLeft;
        featureScores[3] = opponentWallsLeft;
        featureScores[4] = agentManDist;
        featureScores[5] = opponentManDist;

        //for(double weight : mFeatureWeights) Str.println("Weight: " + weight);
        // Evalute score: Max Path, Min Path, Max ManDist, Min ManDist, Max Walls, Min Walls

        for(int i = 0; i < featureScores.length; i++) score += mFeatureWeights[i] * featureScores[i];
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
