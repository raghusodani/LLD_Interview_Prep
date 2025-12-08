package MovementStrategyPattern.ConcreteMovementStrategies;

import MovementStrategyPattern.MovementStrategy;
import UtilityClasses.Board;
import UtilityClasses.Cell;

/**
 * Represents the movement strategy for a queen.
 */
public class QueenMovementStrategy implements MovementStrategy {
    /**
     * Checks if a queen can move from a starting cell to an ending cell on the given board.
     * @param board The game board.
     * @param startCell The starting cell of the queen.
     * @param endCell The ending cell of the queen.
     * @return true if the move is valid, false otherwise.
     */
    @Override
    public boolean canMove(Board board, Cell startCell, Cell endCell) {
        return false;
    }
}
