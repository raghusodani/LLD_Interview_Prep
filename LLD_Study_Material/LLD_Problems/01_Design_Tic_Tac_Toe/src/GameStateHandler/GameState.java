package GameStateHandler;

import GameStateHandler.Context.GameContext;
import Utility.Player;

/**
 * Represents the state of the game.
 */
public interface GameState {
    /**
     * Transitions to the next state.
     * @param context The context of the game.
     * @param player The current player.
     * @param hasWon Whether the current player has won.
     */
    void next(GameContext context, Player player , boolean hasWon);

    /**
     * Checks if the game is over.
     * @return True if the game is over, false otherwise.
     */
    boolean isGameOver();
}