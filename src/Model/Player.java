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
    public Player(String name, String color, Square pos, int destRow) {
        this.playerName = name;
        this.playerColor = color;
        this.pos = pos;
        this.destRow = destRow;
        this.walls = MAX_WALLS;
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
    public String getColor() { return playerColor; }

    /**
     * Gets the amount of walls a player has left.
     * @return amount of walls.
     */
    public int getWallsLeft() { return walls; }


    public Square getPos() {
        return pos;
    }

    public void setPos(Square pos) {
        this.pos = pos;
    }

    public int getDestRow() { return destRow; }

    /**
     * Decreased the amount of walls a player has.
     */
    public void decWalls() {
        if(walls > 0)
            walls--;
    }

    public void incWalls() {
        if(walls < MAX_WALLS)
            walls++;
    }


}
