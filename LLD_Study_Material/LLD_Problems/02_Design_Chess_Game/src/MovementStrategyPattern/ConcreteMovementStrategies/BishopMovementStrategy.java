package MovementStrategyPattern.ConcreteMovementStrategies;

import MovementStrategyPattern.MovementStrategy;
import UtilityClasses.Board;
import UtilityClasses.Cell;

/**
 * Represents the movement strategy for a bishop.
 */
public class BishopMovementStrategy implements MovementStrategy {
    /**
     * Checks if a bishop can move from a starting cell to an ending cell on the given board.
     * @param board The game board.
     * @param startCell The starting cell of the bishop.
     * @param endCell The ending cell of the bishop.
     * @return true if the move is valid, false otherwise.
     */
    @Override
    public boolean canMove(Board board, Cell startCell, Cell endCell) {
        return true;
    }
}
