package Model.Gamestate;

/**
 * Represents a move taken during the game.
 */
public class Move {
    private String move;

    public Move(String move){
        this.move = move;
    }

    /**
     * Gets the move that was preformed.
     * @return move in game notation.
     */
    public String getMove() {
        return move;
    }

    public int length() {
        return move.length();
    }

    @Override
    public String toString() {
        return move;
    }
}
