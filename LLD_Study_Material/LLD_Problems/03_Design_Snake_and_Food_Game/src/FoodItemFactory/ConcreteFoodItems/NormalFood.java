package FoodItemFactory.ConcreteFoodItems;

import FoodItemFactory.FoodItem;

/**
 * Concrete class representing normal food with 1 point.
 */
public class NormalFood extends FoodItem {
    /**
     * Constructs a new NormalFood.
     * @param row The row of the food.
     * @param column The column of the food.
     */
    public NormalFood(int row, int column) {
        super(row, column); // Call superclass constructor
        this.points = 1; // Assign point value
    }
}