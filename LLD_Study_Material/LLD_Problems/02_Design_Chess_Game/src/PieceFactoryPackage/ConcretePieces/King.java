package PieceFactoryPackage.ConcretePieces;

import MovementStrategyPattern.ConcreteMovementStrategies.KingMovementStrategy;
import PieceFactoryPackage.Piece;
import UtilityClasses.Board;
import UtilityClasses.Cell;

/**
 * Represents a king piece.
 */
public class King extends Piece {
    /**
     * Constructs a new King.
     * @param isWhitePiece Whether the piece is white or not.
     */
    public King(boolean isWhitePiece) {
        super(isWhitePiece, new KingMovementStrategy());
    }

    /**
     * Checks if the king can move from a starting cell to an ending cell on the given board.
     * @param board The game board.
     * @param startCell The starting cell of the king.
     * @param endCell The ending cell of the king.
     * @return true if the move is valid, false otherwise.
     */
    @Override
    public boolean canMove(Board board, Cell startCell, Cell endCell) {
        return super.canMove(board, startCell, endCell);
    }
}
