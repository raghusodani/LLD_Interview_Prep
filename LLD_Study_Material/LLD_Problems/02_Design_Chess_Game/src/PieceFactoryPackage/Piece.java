package PieceFactoryPackage;

import MovementStrategyPattern.MovementStrategy;
import UtilityClasses.Board;
import UtilityClasses.Cell;

/**
 * Represents a chess piece.
 */
public abstract class Piece {
    private boolean isWhitePiece; // is the piece white piece or black piece
    private boolean killed = false;
    private MovementStrategy movementStrategy;

    /**
     * Constructs a new Piece.
     * @param isWhitePiece Whether the piece is white or not.
     * @param movementStrategy The movement strategy of the piece.
     */
    public Piece(boolean isWhitePiece, MovementStrategy movementStrategy) {
        this.isWhitePiece = isWhitePiece;
        this.movementStrategy = movementStrategy;
    }

    /**
     * Checks if the piece is white.
     * @return true if the piece is white, false otherwise.
     */
    public boolean isWhite() {
        return isWhitePiece;
    }

    /**
     * Checks if the piece is killed.
     * @return true if the piece is killed, false otherwise.
     */
    public boolean isKilled() {
        return killed;
    }

    /**
     * Sets the killed status of the piece.
     * @param killed The killed status to set.
     */
    public void setKilled(boolean killed) {
        this.killed = killed;
    }

    /**
     * Checks if the piece can move from a starting cell to an ending cell on the given board.
     * @param board The game board.
     * @param startBlock The starting cell of the piece.
     * @param endBlock The ending cell of the piece.
     * @return true if the move is valid, false otherwise.
     */
    public boolean canMove(Board board, Cell startBlock, Cell endBlock) {
        return movementStrategy.canMove(board, startBlock, endBlock);
    }
}
