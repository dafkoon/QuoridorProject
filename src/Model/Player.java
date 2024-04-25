package Model;

import static Utilities.Constants.BOARD_DIMENSION;

/**
 * Represents a player in the game.
 */
public class Player {
    private static final int MAX_WALLS = 10;
    private final String playerName;
    private final int destRow;
    private Square position;
    private int walls;

    /**
     * Constructs a player with the specified name, color, position, and destination row.
     *
     * @param playerName     the name of the player (HUMAN / AI)
     * @param startingSquare the initial square of the player
     */
    public Player(String playerName, Square startingSquare) {
        this.playerName = playerName;
        this.position = startingSquare;
        this.destRow = BOARD_DIMENSION - startingSquare.getRow() - 1;
        this.walls = MAX_WALLS;
    }

    /**
     * Gets the name of the player.
     *
     * @return the name of the player (HUMAN / AI)
     */
    public String getName() {
        return playerName;
    }

    /**
     * Gets the amount of walls the player has left.
     *
     * @return the number of walls the player has left
     */
    public int getWallsLeft() {
        return walls;
    }

    /**
     * Gets the current position of the player.
     *
     * @return the current position of the player
     */
    public Square getPosition() {
        return position;
    }

    /**
     * Sets the position of the player.
     *
     * @param position the new position of the player
     */
    public void setPosition(Square position) {
        this.position = position;
    }

    /**
     * Gets the destination row of the player.
     *
     * @return the destination row of the player
     */
    public int getDestRow() {
        return destRow;
    }

    /**
     * Decreases the number of walls the player has.
     */
    public void decWalls() {
        if (walls > 0)
            walls--;
    }

    /**
     * Increases the number of walls the player has.
     */
    public void incWalls() {
        if (walls < MAX_WALLS)
            walls++;
    }

}
