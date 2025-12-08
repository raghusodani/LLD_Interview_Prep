package PieceFactoryPackage.ConcretePieces;

import MovementStrategyPattern.ConcreteMovementStrategies.RookMovementStrategy;
import PieceFactoryPackage.Piece;
import UtilityClasses.Board;
import UtilityClasses.Cell;

/**
 * Represents a rook piece.
 */
public class Rook extends Piece {

    /**
     * Constructs a new Rook.
     * @param isWhitePiece Whether the piece is white or not.
     */
    public Rook(boolean isWhitePiece) {
        super(isWhitePiece, new RookMovementStrategy());
    }

    /**
     * Checks if the rook can move from a starting cell to an ending cell on the given board.
     * @param board The game board.
     * @param startCell The starting cell of the rook.
     * @param endCell The ending cell of the rook.
     * @return true if the move is valid, false otherwise.
     */
    @Override
    public boolean canMove(Board board, Cell startCell, Cell endCell) {
        return super.canMove(board, startCell, endCell);
    }
}
