package Model;

/**
 * Represents a player in the game.
 */
public class Player {
    private static final int MAX_WALLS = 10;
    private final String playerName;
    private final String playerColor;
    private final int destRow;
    private Square pos;
    private int walls;

    /**
     * Constructs a player with the specified name, color, position, and destination row.
     * @param name the name of the player (HUMAN / AI)
     * @param color the color of the player (BLUE / RED)
     * @param pos the initial position of the player
     * @param destRow the destination row of the player
     */
    public Player(String name, String color, Square pos, int destRow) {
        this.playerName = name;
        this.playerColor = color;
        this.pos = pos;
        this.destRow = destRow;
        this.walls = MAX_WALLS;
    }

    /**
     * Gets the name of the player.
     * @return the name of the player (HUMAN / AI)
     */
    public String getName() {
        return playerName;
    }

    /**
     * Gets the color of the player.
     * @return the color of the player (BLUE / RED)
     */
    public String getColor() {
        return playerColor;
    }

    /**
     * Gets the amount of walls the player has left.
     * @return the number of walls the player has left
     */
    public int getWallsLeft() {
        return walls;
    }

    /**
     * Gets the current position of the player.
     * @return the current position of the player
     */
    public Square getPos() {
        return pos;
    }

    /**
     * Sets the position of the player.
     * @param pos the new position of the player
     */
    public void setPos(Square pos) {
        this.pos = pos;
    }

    /**
     * Gets the destination row of the player.
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
