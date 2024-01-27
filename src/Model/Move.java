package Model;

/**
 * Represents a move taken during the game.
 */
public class Move {
    private Tile from;
    private Tile to;
    private Player player;

    public Move(Tile from, Tile to, Player player) throws IllegalArgumentException{
        if(from == null || to == null) {
            throw new IllegalArgumentException("Tiles cannot be null");
        }
        if(player == null) {
            throw new IllegalArgumentException("Player cannot be null");
        }
    }

    /**
     * Gets the tile the player moved from.
     * @return tile player moved from.
     */
    public Tile getFrom() {
        return from;
    }

    /**
     * Gets the tile the player moved to.
     * @return tile player moved to.
     */
    public Tile getTo() {
        return to;
    }

    /**
     * Gets the player who made the move.
     * @return player that made the move.
     */
    public Player getPlayer() {
        return player;
    }
}
