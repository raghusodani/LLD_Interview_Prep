package UtilityClasses;

import PieceFactoryPackage.Piece;

/**
 * Represents a cell on the chess board.
 */
public class Cell {
    private int row, col;
    private String label;
    private Piece piece;

    /**
     * Constructs a new Cell.
     * @param row The row of the cell.
     * @param col The column of the cell.
     * @param piece The piece on the cell.
     */
    public Cell(int row, int col, Piece piece) {
        this.row = row;
        this.col = col;
        this.piece = piece;
    }

    /**
     * Returns the current piece on the cell.
     * @return The piece on the cell.
     */
    public Piece getPiece() {
        return piece;
    }

    /**
     * Puts a piece on the cell.
     * @param piece The piece to put on the cell.
     */
    public void setPiece(Piece piece) {
        this.piece = piece;
    }
}
