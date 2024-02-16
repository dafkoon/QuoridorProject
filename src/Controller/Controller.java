package Controller;


import View.pieces.Pawn;
import View.Game;

public class Controller {
    public static final int TILE_SIZE = 50;
    public static final int BOARD_DIMENSION = 9;
    private Game view;
    private GameSession gameSession;

    public Controller(Game view) {
        this.view = view;
    }

    public EventHandler startup() {
        this.gameSession = new GameSession();
        this.gameSession.addPlayer(Pawn.PawnType.HUMAN.name(), Pawn.PawnColor.BLUE.name(), Pawn.PawnType.HUMAN.ordinal());
        this.gameSession.addPlayer(Pawn.PawnType.AI.name(), Pawn.PawnColor.RED.name(), Pawn.PawnType.AI.ordinal());
        return new EventHandler(this.gameSession, this.view);

    }


    public String getNextPlayerName() {
        return gameSession.getPlayer(gameSession.currentTurn()).getName();

    }
    public String getNextPlayerColor() {
        return gameSession.getPlayer(gameSession.currentTurn()).getPlayerColor();
    }
    public int getNextPlayerWalls() {
        return gameSession.getPlayer(gameSession.currentTurn()).getWallsLeft();
    }
}
