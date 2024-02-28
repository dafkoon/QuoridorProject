package Controller;
import Model.Gamestate.*;

import static Controller.Utility.shortestPathToRow;
import static Controller.Utility.shortestPathToPlayer;
//import static Controller.Utility.countOpenPathsToGoal;


import java.sql.SQLOutput;
import java.util.*;

public class AI {
    public static final int BOARD_DIMENSION = 9;
    private int agentID;
    private Controller controller;
    private Player agent;
    private Player opponent;
    private List<Square>[] graph;

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
            List<String> shortestPath = shortestPathToRow(graph, agent.getPos(), agent.getDestRow());
            if(shortestPath.contains(opponent.getPos().toString()) && shortestPath.size() == 1)
                shortestPath = generateValidMoves(agent.getPos());
            if(shortestPath.contains(opponent.getPos().toString()) && shortestPath.size() > 1)
                shortestPath.remove(opponent.getPos().toString());
            move = shortestPath.get(0);
        }
        else {
            move = determineMove(agent.getPos(), opponent.getPos());
        }
        return move;
    }

    public String determineMove(Square src, Square other) {
        List<String> validMoves = generateValidMoves(src);
        Move bestMove = null;
//        validMoves = screenUselessMoves(validMoves);
        double bestEval = Double.NEGATIVE_INFINITY;
        double postMoveEval;

        for(String move : validMoves) {
            if(move.length() == 3) {
                controller.placeWall(new Wall(move));
                postMoveEval = evaluate();
                if(postMoveEval > bestEval) {
                    bestEval = postMoveEval;
                    bestMove = new Move(move);
                }
                controller.removeWall(new Wall(move));
            }
//            if(move.length() == 2) {
//                Square previous = controller.movePawn(new Square(move));
//                postMoveEval = evaluate();
//                if(postMoveEval > bestEval) {
//                    bestEval = postMoveEval;
//                    bestMove = new Move(move);
//                }
//                controller.movePawn(previous);
//            }
        }
        return bestMove.toString();
    }

//    public List<String> screenUselessMoves(List<String> moves) {
//        List<String> agentPath = shortestPathToRow(getGraph(), agent.getPos(), agent.getDestRow());
//        List<String> opponentPath = shortestPathToRow(getGraph(), opponent.getPos(), opponent.getDestRow());
//        List<String> usefulMoves = new LinkedList<>();
//        for(String move : moves) {
//            if(move.length() == 3) {
//                if ((isUsefulWall(move, agentPath, opponentPath))) {
//                    usefulMoves.add(move);
//                }
//            }
//            else
//                usefulMoves.add(move);
//        }
//        return usefulMoves;
//    }
//
//    public boolean isUsefulWall(String wall, List<String> agentPath, List<String> opponentPath) {
//        double blockingEfficency = 1;
//        Wall wallToPlace = new Wall(wall);
//        String startingSq = wallToPlace.getStartingSq().toString();
//        String secondarySq = wallToPlace.getSecondarySq().toString();
//        List<Square> wallSurroundings = wallToPlace.getStartingSq().neighbourhood(3);
//
//        if(opponentPath.contains(startingSq) || opponentPath.contains(secondarySq)) { // checks if wall is interacting opponent path.
//            int startingSqIndex = opponentPath.indexOf(startingSq);
//            int secondarySqIndex = opponentPath.indexOf(secondarySq);
//            blockingEfficency *= 2*(startingSqIndex + secondarySqIndex);
//        }
//        if(agentPath.contains(startingSq) || agentPath.contains(secondarySq)) { // checks if wall is interacting agent path.
//            int startingSqIndex = agentPath.indexOf(startingSq);
//            int secondarySqIndex = agentPath.indexOf(secondarySq);
//            blockingEfficency /= 1.5 * (startingSqIndex + secondarySqIndex);
//        }
//        if(wallSurroundings.contains(opponent.getPos())) // checks if wall is near the opponent.
//            blockingEfficency*=1.3;
//
//        /*
//        TODO add proximity to other walls
//             blocking potential escape routes for opponent
//             Creating traps/funneling for opponent
//             Wall chaining
//         */
//        return blockingEfficency > 2; // large number = good        small number = bad
//    }

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
        double[] mFeatureWeights = {-1.5, 1.5, 1, -1, -0.7, +0.7};
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
    public List<String> generateValidMoves(Square playerPos) {
        List<String> validMoves = new LinkedList<>();
        for (Square sq:playerPos.neighbourhood(2)) {
            if (controller.isValidTraversal(sq)) {
                validMoves.add(sq.toString());
            }
        }
        for(int row = 0; row < BOARD_DIMENSION-1; row++) {
            for(int col = 0; col < BOARD_DIMENSION-1; col++) {
                Square sq = new Square(row, col);
                Wall wall = new Wall(sq, Wall.Orientation.HORIZONTAL);
                if(controller.isValidWallPlacement(wall))
                    validMoves.add(wall.toString());
            }
        }
        for(int row = 1; row < BOARD_DIMENSION; row++) {
            for(int col = 0; col < BOARD_DIMENSION-1; col++) {
                Square sq = new Square(row, col);
                Wall wall = new Wall(sq, Wall.Orientation.VERTICAL);
                if(controller.isValidWallPlacement(wall))
                    validMoves.add(wall.toString());
            }
        }

//        for (int row = 0; row < BOARD_DIMENSION ; row++) {
//            for (int col = 0; col < BOARD_DIMENSION; col++) {
//                Square sq = new Square(row,col);
//                System.out.print(sq + " ");
//                for (Wall.Orientation o: Wall.Orientation.values()) {
//                    Wall wall = new Wall(sq, o);
//                    if (controller.isValidWallPlacement(wall)) {
//                        validMoves.add(wall.toString());
//                    }
//                }
//            }
//            System.out.println();
//        }

        return validMoves;
    }



    public List<String> generateWallMoves() {
        List<String> validMoves = new LinkedList<String>();
        for (int row = 0; row < BOARD_DIMENSION ; row++) {
            for (int col = 0; col < BOARD_DIMENSION; col++) {
                Square sq = new Square(row,col);
                System.out.print(sq + " ");
                for (Wall.Orientation o: Wall.Orientation.values()) {
                    Wall wall = new Wall(sq, o);
                    if (controller.isValidWallPlacement(wall)) {
                        validMoves.add(wall.toString());
                    }
                }
            }
            System.out.println();
        }
        return validMoves;
    }

}
