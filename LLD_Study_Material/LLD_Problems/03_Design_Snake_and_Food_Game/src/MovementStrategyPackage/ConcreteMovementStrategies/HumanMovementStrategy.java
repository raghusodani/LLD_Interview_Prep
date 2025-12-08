package MovementStrategyPackage.ConcreteMovementStrategies;

import MovementStrategyPackage.MovementStrategy;
import UtilityClasses.Pair;

/**
 * Represents a human movement strategy.
 */
public class HumanMovementStrategy implements MovementStrategy {
    /**
     * Gets the next position of the snake's head based on the user's input.
     * @param currentHead The current position of the snake's head.
     * @param direction The direction of movement.
     * @return The next position of the snake's head.
     */
    @Override
    public Pair getNextPosition(Pair currentHead, String direction) {
        int row = currentHead.getRow();
        int col = currentHead.getCol();
        switch (direction) {
            case "U": return new Pair(row - 1, col);
            case "D": return new Pair(row + 1, col);
            case "L": return new Pair(row, col - 1);
            case "R": return new Pair(row, col + 1);
            default: return currentHead;
        }
    }
}
