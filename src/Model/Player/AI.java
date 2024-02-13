package Model.Player;
import Model.Gamestate.Board;


public interface AI {
    String move = null;
    String decideMove(Board board);
}
