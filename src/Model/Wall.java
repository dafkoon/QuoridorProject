package Model;

public class Wall {

    private Tile tile1;
    private Tile tile2;
    private Orientation orientation;

    public Wall(Tile t1, Tile t2, Orientation orientation) {
        this.tile1 = t1;
        this.tile2 = t2;
        this.orientation = orientation;
    }

    /**
     * Gets one of the tiles the wall was placed between.
     * @return
     */
    public Tile getTile1() {
        return tile1;
    }

    public Tile getTile2() {
        return tile2;
    }

    public Orientation getOrientation() {
        return orientation;
    }

    public enum Orientation{
        HORIZONTAL, VERTICAL;
    }
}
