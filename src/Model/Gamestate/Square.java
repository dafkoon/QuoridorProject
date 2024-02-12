package Model.Gamestate;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

import static Controller.Controller.BOARD_DIMENSION;

/**
 * Represents a Tile on the "Board" class.
 */
public class Square {

    private int row;
    private int col;

    /**
     * Default constructor (used in Wall).
     */
    public Square() {}

    /**
     * Constructor for Square using index of row and column.
     * @param row index of row.
     * @param col index of column.
     */
    public Square(int row, int col) {
        this.col = col;
        this.row = row;

    }

    /**
     * Constructor for Square using a string location. example a4
     * @param s location on board using string.
     */
    public Square(String s){
        if(s.length() > 1) {
            this.row = s.charAt(1) - '1';
            this.col = s.charAt(0) - 'a';
        }
    }

    /**
     * Copy constructor.
     * @param sq Square instance to copy.
     */
    public Square(Square sq) {
        this.row = sq.getRow();
        this.col = sq.getCol();
    }

    /**
     * Creates a new Square object with the indexes of Square + parameters.
     * @param row displacement of row.
     * @param col displacement of col.
     * @return new square with displacement apploed.
     * @throws IllegalArgumentException
     */
    public Square neighbor(int row, int col) throws IllegalArgumentException{
        if(row < 0 || col < 0) throw new IllegalArgumentException("Paramters cannot be negative.");
        return new Square(this.row+row, this.col+col);
    }

    /**
     * Assigns a LinkedList of Squares which are the neighbors of Square at displacement radius.
     * @param radius displacement of squares surrounding current square.
     * @return A LinkedList of Square objects.
     */
    public List<Square> neighbourhood (int radius) {
        List <Square> neighbors = new LinkedList<Square>();
        for(int distance = -radius; distance <= radius; distance++) {
            if(distance != 0) {
                if(row+distance >= 0 && row+distance < 9)
                    neighbors.add(new Square(row+distance, col));
                if(col+distance >= 0 && col+distance < 9)
                    neighbors.add(new Square(row, col+distance));
            }
        }
        return neighbors;
    }

    /** TODO remove if not used.
     * @param sq square to check if current Square is on the same row or column as sq but not both.
     * @return checks if square is on same the same row or column of current square.
     */
    public boolean isCardinalTo(Square sq) {
        return (row - sq.row != 0) ^ (col - sq.col != 0);
    }

    /** TODO remove if not used.
     * @param sq square to get a square opposite to it (for jumping)
     * @return square that 2 squares away from current square
     */
    public Square opposite(Square sq) {
        return new Square(2*sq.row - row, 2*sq.col - col);
    }


    /**
     * Get the index of the row.
     * @return index of row.
     */
    public int getRow() {
        return row;
    }
    /**
     * Get the index of the column.
     * @return index of column.
     */
    public int getCol() {
        return col;
    }

    public char getRowNotation() { return (char) ('1' + this.row); }
    public char getColNotation() { return (char) ('a' + this.col); }

    @Override
    public String toString() {
        char row = getRowNotation();
        char col = getColNotation();
        return ""+col+row;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Square square = (Square) o;
        return row == square.row && col == square.col;
    }

    @Override
    public int hashCode() {
        return Objects.hash(row, col);
    }
}
