package Model.Player;
import Model.Gamestate.Board;
import Model.Gamestate.Square;


public interface AI {
    String move = null;
    String decideMove(Board board, Square src);
}