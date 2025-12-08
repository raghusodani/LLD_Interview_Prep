package PlayerStrategies;

import Utility.Board;
import Utility.Position;

/**
 * Strategy Interface for Player Moves.
 * Defines a makeMove(Board board) method.
 * Allows different player strategies to be used interchangeably without
 * modifying client code.
 */
public interface PlayerStrategy {
    /**
     * Makes a move on the board.
     * @param board The game board.
     * @return The position of the move.
     */
    Position makeMove(Board board);
}