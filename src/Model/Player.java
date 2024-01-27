package Model;

/**
 * Represents a player in the game.
 */
public class Player {
    private String pawnColor;
    private int walls;
    //private Statistics stats;

    public Player(String pawnColor) {
        this.pawnColor = pawnColor;
        this.walls = 10;
    }

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
