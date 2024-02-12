package Controller;


import View.pieces.Pawn;
import View.Game;

import Model.Gamestate.Square;
import Model.Gamestate.Wall;
import Model.Gamestate.Player;
import Model.Gamestate.GameSession;
import Model.Gamestate.Board;

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


    public boolean doesWallExist(String squareLocation, boolean isHorizontal) {
        char orientation = isHorizontal ? 'h' : 'v';
        Square sq = new Square(squareLocation);
        Wall wall = new Wall(sq, orientation);
        return !gameSession.isValidWallPlacement(wall);
    }

    public void addWall(String squareLocation, boolean isHorizontal) {
        char orientation = isHorizontal ? 'h' : 'v';
        Square thisSquare = new Square(squareLocation);
        Wall wall = new Wall(thisSquare, orientation);
        gameSession.move(wall.toString());
    }

    public int wallsLeft() {
        return getPlayerWallLeft();
    }

    public int getTurn() {
        return gameSession.currentTurn();
    }

    public String getPlayerName() {
        return gameSession.getPlayer(gameSession.currentTurn()).getName();
    }

    public String getPlayerColor() {
        return gameSession.getPlayer(gameSession.currentTurn()).getPlayerColor();
    }

    public int getPlayerWallLeft() {
        return gameSession.getPlayer(gameSession.currentTurn()).getWallsLeft();
    }
}
