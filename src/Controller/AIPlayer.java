package Controller;
import Model.Gamestate.Player;
import Model.Gamestate.Square;
import Model.Gamestate.Wall;

import static Controller.Utility.shortestPathBFS;

import java.util.*;

public class AIPlayer {
    public static final int BOARD_DIMENSION = 9;
    private GameSession gameSession;
    private Player player;
    private int destRow;

    public AIPlayer(Player player, int destRow, GameSession gameSession) {
        this.player = player;
        this.destRow = destRow;
        this.gameSession = gameSession;
    }

    public String generateMove(List<Square>[] currentGraph, Square playerSquare, Square otherPlayer) {
        String move;
        if(this.player.getWallsLeft() > 0) {
            List<String> shortestPath = shortestPathBFS(currentGraph, playerSquare, this.destRow);
            if(shortestPath.contains(otherPlayer.toString()) && shortestPath.size() == 1)
                shortestPath = generatePawnMoves(playerSquare);
            if(shortestPath.contains(otherPlayer.toString()) && shortestPath.size() > 1)
                shortestPath.remove(otherPlayer.toString());
            move = shortestPath.get(0);

        }
        else {
            move = bestMove(currentGraph, playerSquare, otherPlayer);
        }
        return move;
    }

    public String bestMove(List<Square>[] currentGraph, Square src, Square other) {
        List<String> pawnMoves = generatePawnMoves(src);
        List<String> wallMoves = generateWallMoves();
        System.out.println(pawnMoves);
        System.out.println(wallMoves);
        return pawnMoves.get(0);
    }


    public List<String> generatePawnMoves(Square playerPos) {
        List<String> validMoves = new LinkedList<String>();
        for (Square sq:playerPos.neighbourhood(2)) {
            if (gameSession.isValidTraversal(sq)) {
                validMoves.add(sq.toString());
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
                    if (gameSession.isValidWallPlacement(wall)) {
                        validMoves.add(wall.toString());
                    }
                }
            }
        }
        return validMoves;
    }

}
