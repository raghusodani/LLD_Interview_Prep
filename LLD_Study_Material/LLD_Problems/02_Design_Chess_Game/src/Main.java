import Controller.ConcreteGames.ChessGame;
import UtilityClasses.Player;

/**
 * The main class to start the chess game.
 */
public class Main {
    /**
     * The main method to start the chess game.
     * @param args Command line arguments (not used).
     */
    public static void main(String[] args) {
        // Create players
        Player player1 = new Player("Player1", true); // White
        Player player2 = new Player("Player2", false); // Black
        // Initialize game
        ChessGame chessGame = new ChessGame(player1, player2);
        // Start the game
        chessGame.start();
    }
}