package PieceFactoryPackage.ConcretePieces;

import MovementStrategyPattern.ConcreteMovementStrategies.QueenMovementStrategy;
import PieceFactoryPackage.Piece;
import UtilityClasses.Board;
import UtilityClasses.Cell;

/**
 * Represents a queen piece.
 */
public class Queen extends Piece {
    /**
     * Constructs a new Queen.
     * @param isWhitePiece Whether the piece is white or not.
     */
    public Queen(boolean isWhitePiece) {
        super(isWhitePiece, new QueenMovementStrategy());
    }

    /**
     * Checks if the queen can move from a starting cell to an ending cell on the given board.
     * @param board The game board.
     * @param startCell The starting cell of the queen.
     * @param endCell The ending cell of the queen.
     * @return true if the move is valid, false otherwise.
     */
    @Override
    public boolean canMove(Board board, Cell startCell, Cell endCell) {
        return super.canMove(board, startCell, endCell);
    }
}
