package MovementStrategyPackage;

import UtilityClasses.Pair;

/**
 * Represents a movement strategy for the snake.
 */
public interface MovementStrategy {
    /**
     * Gets the next position of the snake's head based on the current head position and direction.
     * @param currentHead The current position of the snake's head.
     * @param direction The direction of movement.
     * @return The next position of the snake's head.
     */
    Pair getNextPosition(Pair currentHead, String direction);
}