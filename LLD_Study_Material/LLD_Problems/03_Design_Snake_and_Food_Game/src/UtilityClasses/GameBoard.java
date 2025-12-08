package UtilityClasses;

/**
 * Singleton class representing the game board.
 */
public class GameBoard {
    private static GameBoard instance; // Single instance of the game board
    private int width;  // Width of the game board
    private int height; // Height of the game board

    /**
     * Private constructor to prevent direct instantiation.
     * @param width The width of the game board.
     * @param height The height of the game board.
     */
    private GameBoard(int width, int height) {
        this.width = width;
        this.height = height;
    }

    /**
     * Method to get the singleton instance of the game board.
     * @param width The width of the game board.
     * @param height The height of the game board.
     * @return The singleton instance of the game board.
     */
    public static GameBoard getInstance(int width, int height) {
        if (instance == null) {
            instance = new GameBoard(width, height); // Create instance if not already created
        }
        return instance; // Return the existing instance
    }

    /**
     * Getter for the width of the game board.
     * @return The width of the game board.
     */
    public int getWidth() { return width; }

    /**
     * Getter for the height of the game board.
     * @return The height of the game board.
     */
    public int getHeight() { return height; }
}
