package Model;

import java.util.Objects;

/**
 * Represents a wall in the game.
 */
public class Wall {

    public Square startingSq;
    public Orientation orientation = null;

    /**
     * Constructs a wall with the specified starting square and orientation.
     *
     * @param startingSq  the square located at the start of the wall
     * @param orientation the direction that the wall will go in (vertical or horizontal)
     */
    public Wall(Square startingSq, Orientation orientation) {
        this.startingSq = startingSq;
        this.orientation = orientation;
    }

    /**
     * Constructs a wall with the specified starting square and orientation.
     *
     * @param startingSq  the square located at the start of the wall
     * @param orientation the direction that the wall will go in (specified as 'h' for horizontal or 'v' for vertical)
     */
    public Wall(Square startingSq, char orientation) {
        this.startingSq = startingSq;
        this.orientation = (orientation == 'h') ? Orientation.HORIZONTAL : Orientation.VERTICAL;
    }

    /**
     * Constructs a wall using a string representation.
     * Example: "e3v" represents a vertical wall between columns e and f spanning rows 3 and 2.
     *
     * @param s the string representation of the wall
     */
    public Wall(String s) {
        if (s.length() > 2) {
            this.startingSq = new Square(s.substring(0, 2));
            this.orientation = s.charAt(2) == 'h' ? Orientation.HORIZONTAL : Orientation.VERTICAL;
        }
    }

    public Wall(Wall other) {
        this.startingSq = other.startingSq;
        this.orientation = other.orientation;
    }

    /**
     * Returns the neighbor wall of the specified row, column, and orientation.
     *
     * @param row         the row offset for the neighbor wall
     * @param column      the column offset for the neighbor wall
     * @param orientation the orientation of the neighbor wall
     * @return the neighbor wall
     */
    public Wall neighbor(int row, int column, Orientation orientation) {
        Square neighborSq = new Square(startingSq.getRow() + row, startingSq.getCol() + column);
        return new Wall(neighborSq, orientation);
    }

    /**
     * Gets the starting square of the wall.
     *
     * @return the starting square of the wall
     */
    public Square getStartingSq() {
        return startingSq;
    }

    /**
     * Gets the orientation of the wall.
     *
     * @return the orientation of the wall
     */
    public Orientation getOrientation() {
        return orientation;
    }


    public String toString() {
        return startingSq.toString() + orientation.name().toLowerCase().charAt(0);
    }

    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Wall wall = (Wall) o;
        return Objects.equals(startingSq, wall.startingSq) && orientation == wall.orientation;
    }

    /**
     * Represents the orientation of a wall (horizontal or vertical).
     */
    public enum Orientation {
        HORIZONTAL, VERTICAL
    }
}

