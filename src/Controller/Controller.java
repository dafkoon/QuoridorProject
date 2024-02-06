package Controller;
<<<<<<< Updated upstream

public class Controller {
=======
import javafx.scene.input.MouseEvent;


import View.pieces.Pawn;
import View.pieces.Tile;
import Model.Square;
import Model.Player;
import Model.GameSession;

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




    public Square convertTileToSquare(Tile tile) {
        int tileX = tile.getRow();
        int tileY = tile.getCol();
        return new Square(tileX, tileY);
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




>>>>>>> Stashed changes
}
