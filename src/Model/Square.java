package Model;

import java.util.LinkedList;
import java.util.List;

import static Utilities.Constants.BOARD_DIMENSION;

/**
 * Represents a square on the game board.
 */
public class Square {
    private int row;
    private int col;

    /**
     * Constructs a square with the specified row and column indices.
     *
     * @param row the index of the row
     * @param col the index of the column
     */
    public Square(int row, int col) {
        this.row = row;
        this.col = col;
    }

    /**
     * Constructs a square using a string location.
     * Example: "a4" represents the square at column a and row 4.
     *
     * @param s the string representation of the square's location
     */
    public Square(String s) {
        if (s.length() > 1) {
            this.row = s.charAt(1) - '1';
            this.col = s.charAt(0) - 'a';
        }
    }

    /**
     * Creates a new square object with the indices of the current square plus the specified displacements.
     *
     * @param row the displacement of the row
     * @param col the displacement of the column
     * @return a new square with the displacement applied
     */
    public Square neighbor(int row, int col) {
        return new Square(this.row + row, this.col + col);
    }

    /**
     * Assigns a list of squares which are the neighbors of the current square at the specified radius.
     *
     * @param radius the displacement of squares surrounding the current square
     * @return a list of neighboring square objects
     */
    public List<Square> neighbourhood(int radius) {
        List<Square> neighbors = new LinkedList<>();
        for (int rowOffset = -radius; rowOffset <= radius; rowOffset++) {
            for (int colOffset = -radius; colOffset <= radius; colOffset++) {
                if (rowOffset != 0 || colOffset != 0) { // Exclude the current square
                    int newRow = row + rowOffset;
                    int newCol = col + colOffset;
                    if (row >= 0 && row < 9 && col >= 0 && col < 9) {
                        neighbors.add(new Square(newRow, newCol));
                    }
                }
            }
        }
        return neighbors;
    }

    /**
     * A boolean method that calculates if the given sq is adjacent in a straight line, either horizontally or vertically to this.
     *
     * @param sq The square to check.
     * @return True if the square is adjacent in a straight line, either horizontally or vertically to this.
     */
    public boolean isCardinalTo(Square sq) {
        return (row - sq.row != 0) ^ (col - sq.col != 0);
    }

    /**
     * Returns the square opposite to the specified square (for jumping).
     *
     * @param sq the square to get the opposite square for
     * @return the square that is 2 squares away from the current square
     */
    public Square opposite(Square sq) {
//        System.out.println(this + " " + new Square(2 * sq.row - row, 2 * sq.col - col));
        return new Square(2 * sq.row - row, 2 * sq.col - col);
    }

    /**
     * Converts the square to its corresponding index in the board array.
     *
     * @return the index of the square in the board array
     */
    public int toIndex() {
        return this.row * BOARD_DIMENSION + this.col;
    }

    /**
     * Gets the index of the row.
     *
     * @return the index of the row
     */
    public int getRow() {
        return row;
    }

    /**
     * Gets the index of the column.
     *
     * @return the index of the column
     */
    public int getCol() {
        return col;
    }

    /**
     * Gets the notation of the row (e.g., '1' for row 1).
     *
     * @return the notation of the row
     */
    public char getRowNotation() {
        return (char) ('1' + this.row);
    }

    /**
     * Gets the notation of the column (e.g., 'a' for column a).
     *
     * @return the notation of the column
     */
    public char getColNotation() {
        return (char) ('a' + this.col);
    }

    public String toString() {
        char row = getRowNotation();
        char col = getColNotation();
        return "" + col + row;
    }

    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Square square = (Square) o;
        return row == square.row && col == square.col;
    }
}

