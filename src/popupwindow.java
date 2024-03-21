import Model.Gamestate.Square;
import Model.Gamestate.Wall;

import java.util.ArrayList;
import java.util.List;

import static Controller.ShortestPath.shortestPathToRow;

class popupwindow {
    //    public String takeShortestPath() {
//        ArrayList<Square> shortestPath = shortestPathToRow(graph, agent.getPos(), agent.getDestRow());
//        if(shortestPath.contains(opponent.getPos()) && shortestPath.size() == 1)
//            shortestPath = generatePawnMoves(agent.getPos());
//        if(shortestPath.contains(opponent.getPos()) && shortestPath.size() > 1)
//            shortestPath.remove(opponent.getPos());
//        return shortestPath.get(0).toString();
//    }
//    public String evaluateAllMoves() {
//        int agentPathLength = shortestPathToRow(getGraph(), agent.getPos(), agent.getDestRow()).size();
//        int opponentPathLength = shortestPathToRow(getGraph(), opponent.getPos(), opponent.getDestRow()).size();
//        ArrayList<Wall> wallMoves = generateWallMoves();
////        wallMoves = removeUselessWalls(wallMoves);
//        ArrayList<Square> pawnMoves = generatePawnMoves(agent.getPos());
//        double maxGrade = Double.NEGATIVE_INFINITY;
//        double grade;
//        String bestMove = null;
//        if (opponentPathLength > 2 || agentPathLength <= opponentPathLength) {
//            // Only consider Pawn moves if
//            // opponent's path is longer than 2 traversal moves  (if opponent is far from target)
//            // OR length of agent's path is less or equal to opponent's path. (if agent is closer to target than opponent)
//            for (Square moveTo : pawnMoves) {
//                Square currentSquare = agent.getPos();
//                doVirtualMove(moveTo.toString());
//                grade = heuristicFun();
//                undoVirtualMove(currentSquare.toString());
//                if (grade > maxGrade) {
//                    maxGrade = grade;
//                    bestMove = moveTo.toString();
//                }
//            }
//        }
//        for(Wall wallToPlace : wallMoves) {
//            doVirtualMove(wallToPlace.toString());
//            grade = heuristicFun();
//            undoVirtualMove(wallToPlace.toString());
//            if(grade > maxGrade) {
//                maxGrade = grade;
//                bestMove = wallToPlace.toString();
//            }
//        }
//        return bestMove;
//    }
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
//    public int calculateWallsInRange(Wall wall, int r) {
//        int count = 0;
//        List<Square> neighbouringSquares = wall.getStartingSq().neighbourhood(r);
//        List<Wall> wallOnBoard = getWalls();
//        for(Wall curWall : wallOnBoard) {
//            if(!wall.equals(curWall)) {
//                if(neighbouringSquares.contains(curWall.getStartingSq()))
//                    count++;
//            }
//        }
//        return count;
//    }
//    public double heuristicFun() {
//        double score = 0.0;
//        Square agentSquare = agent.getPos();
//        Square opponentSquare = opponent.getPos();
//
//        int agentDistToGoal = shortestPathToRow(getGraph(), agentSquare, agent.getDestRow()).size();
//        int opponentDistToGoal = shortestPathToRow(getGraph(), opponentSquare, opponent.getDestRow()).size();
//        int agentWallsLeft = agent.getWallsLeft();
//        int opponentWallsLeft = opponent.getWallsLeft();
//        int agentManDist = Math.abs(agentSquare.getRow() - agent.getDestRow());
//        int opponentManDist = Math.abs(opponentSquare.getRow() - opponent.getDestRow());
//
//        int[] featureScores = new int[3];
//        double[] mFeatureWeights = {2.5, 1.5, 2};
//        featureScores[0] = opponentDistToGoal - agentDistToGoal;
//        featureScores[1] = opponentWallsLeft - agentWallsLeft;
//        featureScores[2] = opponentManDist - agentManDist;
//
//        for(int i = 0; i < featureScores.length; i++) {
//            score += mFeatureWeights[i] * featureScores[i];
//        }
//        return score + 0.5 * Math.random();
//    }

}