package Utility;

/**
 * Represents a position on the game board.
 */
public class Position {
    public int row;
    public int col;

    /**
     * Constructs a new Position.
     * @param row The row of the position.
     * @param col The column of the position.
     */
    public Position(int row, int col) {
        this.row = row;
        this.col = col;
    }

    /**
     * Returns a string representation of the position.
     * @return A string representation of the position.
     */
    @Override
    public String toString() {
        return "(" + row + ", " + col + ")";
    }

    /**
     * Checks if this position is equal to another object.
     * @param obj The object to compare to.
     * @return true if the objects are equal, false otherwise.
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof Position)) return false;
        Position other = (Position) obj;
        return this.row == other.row && this.col == other.col;
    }

    /**
     * Returns the hash code of the position.
     * @return The hash code of the position.
     */
    @Override
    public int hashCode() {
        return 31 * row + col;
    }
}
