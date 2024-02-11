package Controller;


import View.pieces.Pawn;
import Model.Square;
import Model.Wall;
import Model.Player;
import Model.GameSession;

import java.sql.SQLOutput;

public class Controller {
    public static final int TILE_SIZE = 50;
    public static final int BOARD_DIMENSION = 9;
    private GameSession gameSession;

    public Controller() {}

    public EventHandler startup() {
        this.gameSession = new GameSession();
        this.gameSession.addPlayer(new Player(Pawn.PawnType.HUMAN.name(), Pawn.PawnColor.BLUE.name()));
        this.gameSession.addPlayer(new Player(Pawn.PawnType.AI.name(), Pawn.PawnColor.RED.name()));
        return new EventHandler(this.gameSession);

    }

    //    public boolean getMoves(Tile nextTile) {
//        Square nextSquare = convertTileToSquare(nextTile);
//        List<String> moves = gameSession.generateValidMoves();
//        //System.out.println(moves.toString());
//         //gameSession.move()
//        return gameSession.isValidTraversal(nextSquare) && gameSession.currentPlayer() == playerTurn;
//    }

    public boolean doesWallExist(String squareLocation, boolean isHorizontal) {
        char orientation = isHorizontal ? 'h' : 'v';
        Square sq = new Square(squareLocation);
        Wall wall = new Wall(sq, orientation);
//        System.out.println(wall);
        return !gameSession.isValidWallPlacement(wall);
    }

    public int wallsLeft() {
        return getCurrentPlayerWallsLeft();
    }

    public void addWall(String squareLocation, boolean isHorizontal) {
        char orientation = isHorizontal ? 'h' : 'v';
        Square thisSquare = new Square(squareLocation);
        Wall wall = new Wall(thisSquare, orientation);
        System.out.println(wall);
        gameSession.move(wall.toString());
    }

    public boolean isHumanTurn() { return gameSession.currentTurn() == 0; }

    public String getCurrentPlayerName() {
        return gameSession.getPlayer(gameSession.currentTurn()).getName();
    }

    public String getCurrentPlayerColor() {
        return gameSession.getPlayer(gameSession.currentTurn()).getPlayerColor();
    }

    public int getCurrentPlayerWallsLeft() {
        return gameSession.getPlayer(gameSession.currentTurn()).getWallsLeft();
    }
}
