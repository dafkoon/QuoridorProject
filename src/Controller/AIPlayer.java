package Controller;
import Model.Gamestate.*;

import static Controller.Utility.shortestPathBFS;

import java.util.*;

public class AIPlayer {
    public static final int BOARD_DIMENSION = 9;
    private int playerID;
    private int destRow;
    private Controller controller;
    private Player player;
    private List<Square>[] graph;

    public AIPlayer(int id, int destRow, Controller controller) {
        this.playerID = id;
        this.destRow = destRow;
        this.controller = controller;
        this.player = controller.getPlayer(id);
    }

    public String generateMove(List<Square>[] graph, Square playerSquare, Square opponentSquare) {
        setGraph(graph);
        String move;
        if(player.getWallsLeft() > 0) {
            List<Square> shortestPath = shortestPathBFS(graph, playerSquare, destRow);
            if(shortestPath.contains(opponentSquare) && shortestPath.size() == 1)
                shortestPath = generatePawnMoves(playerSquare);
            if(shortestPath.contains(opponentSquare) && shortestPath.size() > 1)
                shortestPath.remove(opponentSquare);
            move = shortestPath.get(0).toString();

        }
        else {
            move = bestMove(playerSquare, opponentSquare);
        }
        return move;
    }

    public String bestMove(Square src, Square other) {
        List<Square> pawnMoves = generatePawnMoves(src);
        List<String> wallMoves = generateWallMoves();
        List<Double> pawnMoveEval = simulatePawnMove(pawnMoves);
        return pawnMoves.get(0).toString();
    }

    public List<Double> simulatePawnMove(List<Square> moves) {
        Square originalSquare = player.getPos();
        List<Double> evalList = new LinkedList<Double>();
        for(Square move: moves) {
            controller.movePawn(move);
            evalList.add(evalState());
        }
        controller.movePawn(originalSquare);
        return evalList;
    }

    public double evalState() {
        double score = 0;
        if(controller.gameOver()) {
            return Double.POSITIVE_INFINITY;
        };
        Player opponent = controller.getPlayer((this.playerID +1)%2);
        List<Square> playerBFS = shortestPathBFS(getGraph(), player.getPos(), player.getDestRow());
        List<Square> opponentBFS = shortestPathBFS(getGraph(), opponent.getPos(), opponent.getDestRow());

        return 0;
    }

    private void setGraph(List<Square>[] graph) {
        this.graph = graph;
    }
    private List<Square>[] getGraph() { return this.graph; }








    public List<Square> generatePawnMoves(Square playerPos) {
        List<Square> validMoves = new LinkedList<Square>();
        for (Square sq:playerPos.neighbourhood(2)) {
            if (controller.isValidTraversal(sq)) {
                validMoves.add(sq);
            }
        }
        return validMoves;
    }

    public List<String> generateWallMoves() {
        List<String> validMoves = new LinkedList<String>();
        for (int i = 0; i < BOARD_DIMENSION ; i++) {
            for (int j = 0; j < BOARD_DIMENSION; j++) {
                Square sq = new Square(i,j);
                for (Wall.Orientation o: Wall.Orientation.values()) {
                    Wall wall = new Wall(sq, o);
                    if (controller.isValidWallPlacement(wall)) {
                        validMoves.add(wall.toString());
                    }
                }
            }
        }
        return validMoves;
    }

}
