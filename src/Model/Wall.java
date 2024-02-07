package Model;

import java.util.Objects;

public class Wall {

    Square startingSq = new Square();
    Orientation orientation = null;

    /**
     * @param startingSq Square located at the start of the wall.
     * @param orientation direction that the wall will go in (vertical or horizontal)
     */
    public Wall(Square startingSq, Orientation orientation) {
        this.startingSq = startingSq;
        this.orientation = orientation;
    }

    /** Constructor for Wall using a string.
     * example: Vertical wall between column e and f spanning rows 3 and 4 is e3v.
     * @param s string constructor
     */
    public Wall(String s) {
        // TODO Should probably have preconditions to check the string is valid though that may be the job of isValidMove
        if (s.length() > 2) {
            this.startingSq = new Square (s.substring(0, 2));
            this.orientation = s.charAt(2) == 'h' ? Orientation.HORIZONTAL : Orientation.VERTICAL;
        }
    }

    public Wall neighbor(int row, int column, Orientation orientation) {
        Square neighborSq = new Square(startingSq.getRow()+row, startingSq.getCol()+column);
        return new Wall(neighborSq, orientation);
    }

    public Square getStartingSq() { return startingSq; }

    public Orientation getOrientation() { return orientation; }

    @Override
    public String toString() {
        return orientation.toString() + orientation.name().toLowerCase().charAt(0);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Wall wall = (Wall) o;
        return Objects.equals(startingSq, wall.startingSq) && orientation == wall.orientation;
    }

    @Override
    public int hashCode() {
        return Objects.hash(startingSq, orientation);
    }

    public enum Orientation{
        HORIZONTAL, VERTICAL;
    }
}
