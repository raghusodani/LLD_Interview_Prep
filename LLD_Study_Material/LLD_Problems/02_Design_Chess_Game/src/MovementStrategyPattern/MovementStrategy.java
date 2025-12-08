package MovementStrategyPattern;

import UtilityClasses.Board;
import UtilityClasses.Cell;

/**
 * Represents a movement strategy for a chess piece.
 */
public interface MovementStrategy {
    /**
     * Checks if a piece can move from a starting cell to an ending cell on the given board.
     * @param board The game board.
     * @param startCell The starting cell of the piece.
     * @param endCell The ending cell of the piece.
     * @return true if the move is valid, false otherwise.
     */
    boolean canMove(Board board, Cell startCell, Cell endCell);
}
