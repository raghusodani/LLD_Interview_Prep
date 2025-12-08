package MovementStrategyPattern.ConcreteMovementStrategies;

import MovementStrategyPattern.MovementStrategy;
import UtilityClasses.Board;
import UtilityClasses.Cell;

/**
 * Represents the movement strategy for a knight.
 */
public class KnightMovementStrategy implements MovementStrategy {
    /**
     * Checks if a knight can move from a starting cell to an ending cell on the given board.
     * @param board The game board.
     * @param startCell The starting cell of the knight.
     * @param endCell The ending cell of the knight.
     * @return true if the move is valid, false otherwise.
     */
    @Override
    public boolean canMove(Board board, Cell startCell, Cell endCell) {
        return false;
    }
}
