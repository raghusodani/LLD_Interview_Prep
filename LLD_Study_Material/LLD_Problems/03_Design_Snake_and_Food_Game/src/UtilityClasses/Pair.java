package UtilityClasses;

/**
 * Represents a pair of coordinates.
 */
public class Pair {
    int row;
    int col;

    /**
     * Constructs a new Pair.
     * @param row The row of the pair.
     * @param col The column of the pair.
     */
    public Pair(int row , int col){
        this.row = row;
        this.col = col;
    }

    /**
     * Gets the row of the pair.
     * @return The row of the pair.
     */
    public int getRow(){
        return row;
    }

    /**
     * Gets the column of the pair.
     * @return The column of the pair.
     */
    public int getCol(){
        return col;
    }
}
