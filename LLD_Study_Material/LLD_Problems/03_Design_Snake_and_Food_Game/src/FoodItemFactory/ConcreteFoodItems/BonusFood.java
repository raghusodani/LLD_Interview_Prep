package FoodItemFactory.ConcreteFoodItems;

import FoodItemFactory.FoodItem;

/**
 * Concrete class representing bonus food with 3 points.
 */
public class BonusFood extends FoodItem {
    /**
     * Constructs a new BonusFood.
     * @param row The row of the food.
     * @param column The column of the food.
     */
    public BonusFood(int row, int column) {
        super(row, column); // Call superclass constructor
        this.points = 3; // Assign higher point value
    }
}
