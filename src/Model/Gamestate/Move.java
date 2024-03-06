package Model.Gamestate;

import java.util.Stack;

/**
 * Represents a move taken during the game.
 */
public class Move {
    private static Stack<String> moves;



    public void addMove(String move) {
        moves.add(move);
    }

    public Stack<String> getMoves() {
        return moves;
    }
}
