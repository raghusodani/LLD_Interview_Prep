package PieceFactoryPackage.ConcretePieces;

import MovementStrategyPattern.ConcreteMovementStrategies.PawnMovementStrategy;
import PieceFactoryPackage.Piece;
import UtilityClasses.Board;
import UtilityClasses.Cell;

/**
 * Represents a pawn piece.
 */
public class Pawn extends Piece {
    /**
     * Constructs a new Pawn.
     * @param isWhitePiece Whether the piece is white or not.
     */
    public Pawn(boolean isWhitePiece) {
        super(isWhitePiece, new PawnMovementStrategy());
    }

    /**
     * Checks if the pawn can move from a starting cell to an ending cell on the given board.
     * @param board The game board.
     * @param startCell The starting cell of the pawn.
     * @param endCell The ending cell of the pawn.
     * @return true if the move is valid, false otherwise.
     */
    @Override
    public boolean canMove(Board board, Cell startCell, Cell endCell) {
        return super.canMove(board, startCell, endCell);
    }
}
