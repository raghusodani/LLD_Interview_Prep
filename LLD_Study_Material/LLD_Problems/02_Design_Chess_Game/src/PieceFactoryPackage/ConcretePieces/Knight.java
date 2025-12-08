package PieceFactoryPackage.ConcretePieces;

import MovementStrategyPattern.ConcreteMovementStrategies.KingMovementStrategy;
import PieceFactoryPackage.Piece;
import UtilityClasses.Board;
import UtilityClasses.Cell;

/**
 * Represents a knight piece.
 */
public class Knight extends Piece {
    /**
     * Constructs a new Knight.
     * @param isWhitePiece Whether the piece is white or not.
     */
    public Knight(boolean isWhitePiece) {
        super(isWhitePiece, new KingMovementStrategy());
    }

    /**
     * Checks if the knight can move from a starting cell to an ending cell on the given board.
     * @param board The game board.
     * @param startCell The starting cell of the knight.
     * @param endCell The ending cell of the knight.
     * @return true if the move is valid, false otherwise.
     */
    @Override
    public boolean canMove(Board board, Cell startCell, Cell endCell) {
        return super.canMove(board, startCell, endCell);
    }
}
