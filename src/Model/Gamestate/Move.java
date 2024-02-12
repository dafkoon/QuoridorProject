package Model.Gamestate;

/**
 * Represents a move taken during the game.
 */
public class Move {
    private String move;
    private Player player;

    public Move(String move, Player player) throws IllegalArgumentException{
        if(player == null) {
            throw new IllegalArgumentException("Player cannot be null");
        }
        this.move = move;
        this.player = player;
    }

    /**
     * Gets the move that was preformed.
     * @return move in game notation.
     */
    public String getMove() {
        return move;
    }


    /**
     * Gets the player who made the move.
     * @return player that made the move.
     */
    public Player getPlayer() {
        return player;
    }
}
