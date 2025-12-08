package UtilityClasses;

/**
 * Represents a move in the game.
 */
public class Move {
    private Cell startCell;
    private Cell endCell;

    /**
     * Constructor to initialize the move with start and end cells.
     * @param startCell The starting cell of the move.
     * @param endCell The ending cell of the move.
     */
    public Move(Cell startCell, Cell endCell) {
        this.startCell = startCell;
        this.endCell = endCell;
    }

    /**
     * Validate if the move is valid.
     * A move is valid if the end cell is empty or contains a piece of the opposite color.
     * @return true if the move is valid, false otherwise.
     */
    public boolean isValid() {
        if(endCell.getPiece() == null) return true;
        else return !(startCell.getPiece().isWhite() == endCell.getPiece().isWhite());
    }

    /**
     * Gets the start cell.
     * @return The starting cell of the move.
     */
    public Cell getStartCell() {
        return startCell;
    }

    /**
     * Gets the end cell.
     * @return The ending cell of the move.
     */
    public Cell getEndCell() {
        return endCell;
    }
}
