package Model;

/**
 * Represents a Tile on the "Board" class.
 */
public class Tile {
    private int x;
    private int y;
    private boolean containsPawn;

    public Tile(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public void setContainsPawn(boolean containsPawn) { this.containsPawn = containsPawn; }

    public boolean getContainsPawn() { return containsPawn; }

}
