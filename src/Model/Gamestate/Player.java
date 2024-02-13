package Model.Gamestate;

/**
 * Represents a player in the game.
 */
public class Player {
    private String playerName;
    private String playerColor;
    private int walls;

    public Player(String name, String color) {
        this.playerName = name;
        this.playerColor = color;
        this.walls = 10; // Default number of walls.
    }

    /**
     * Gets the name of the player (HUMAN / AI).
     * @return A string of the player name.
     */
    public String getName() {
        return playerName; }

    /**
     * Gets the Color of the player (BLUE / RED).
     * @return A string of the player color.
     */
    public String getPlayerColor() { return playerColor; }

    /**
     * Gets the amount of walls a player has left.
     * @return amount of walls.
     */
    public int getWallsLeft() { return walls; }

    /**
     * Decreased the amount of walls a player has.
     */
    public void decWalls() {
        if(walls > 0)
            walls--;
    }


}