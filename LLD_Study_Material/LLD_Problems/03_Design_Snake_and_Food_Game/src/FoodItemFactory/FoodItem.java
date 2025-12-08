package FoodItemFactory;

/**
 * Abstract class representing a food item in the game.
 */
public abstract class FoodItem {
    protected int row, column; // Position of the food item
    protected int points; // Points awarded when consumed

    /**
     * Constructor to initialize food item position.
     * @param row The row of the food item.
     * @param column The column of the food item.
     */
    public FoodItem(int row, int column) {
        this.row = row;
        this.column = column;
    }

    /**
     * Getter for the row of the food item.
     * @return The row of the food item.
     */
    public int getRow() { return row; }

    /**
     * Getter for the column of the food item.
     * @return The column of the food item.
     */
    public int getColumn() { return column; }

    /**
     * Getter for the points of the food item.
     * @return The points of the food item.
     */
    public int getPoints() { return points; }
}
