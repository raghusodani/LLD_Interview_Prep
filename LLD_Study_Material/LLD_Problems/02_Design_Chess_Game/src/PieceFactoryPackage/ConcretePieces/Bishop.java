package PieceFactoryPackage.ConcretePieces;

import MovementStrategyPattern.ConcreteMovementStrategies.BishopMovementStrategy;
import PieceFactoryPackage.Piece;
import UtilityClasses.Board;
import UtilityClasses.Cell;

/**
 * Represents a bishop piece.
 */
public class Bishop extends Piece {
    /**
     * Constructs a new Bishop.
     * @param isWhitePiece Whether the piece is white or not.
     */
    public Bishop(boolean isWhitePiece) {
        super(isWhitePiece, new BishopMovementStrategy());
    }

    /**
     * Checks if the bishop can move from a starting cell to an ending cell on the given board.
     * @param board The game board.
     * @param startCell The starting cell of the bishop.
     * @param endCell The ending cell of the bishop.
     * @return true if the move is valid, false otherwise.
     */
    @Override
    public boolean canMove(Board board, Cell startCell, Cell endCell) {
        return super.canMove(board, startCell, endCell);
    }
}
