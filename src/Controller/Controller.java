package Controller;
import javafx.scene.input.MouseEvent;
import javafx.scene.Node;
import java.util.List;


import View.pieces.Pawn;
import View.pieces.Tile;
import Model.Square;
import Model.Wall;
import Model.Move;
import Model.Player;
import Model.GameSession;
import Model.Board;
import Model.Square;

import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

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

    public boolean doesWallExist(int thisRow, int thisCol, boolean isHorizontal) {
        char orientation = isHorizontal ? 'h' : 'v';
        Square thisSquare = new Square(thisRow, thisCol);
        Wall wall = new Wall(thisSquare, orientation);
//        System.out.println(thisSquare.getColNotation() + "" + thisSquare.getRowNotation() + orientation + " " + wall);
        //System.out.print(thisCol + "," + thisRow + "=" +wall + " ");
        return !gameSession.isValidWallPlacement(wall);
//        //System.out.println(thisX + "," + thisY + "  " + nextWallX + "," + nextWallY);
//        return true;
    }



    public String getCurrentPlayerName() {
        return gameSession.getPlayer(gameSession.currentPlayer()).getName();
    }

    public String getCurrentPlayerColor() {
        return gameSession.getPlayer(gameSession.currentPlayer()).getPlayerColor();
    }

    public int getCurrentPlayerWallsLeft() {
        return gameSession.getPlayer(gameSession.currentPlayer()).getWallsLeft();
    }




}
