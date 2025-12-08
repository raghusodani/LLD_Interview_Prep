package GameStateHandler.Context;

import GameStateHandler.ConcreteStates.XTurnState;
import GameStateHandler.GameState;
import Utility.Player;

/**
 * Manages the current state of the game and transitions between states.
 */
public class GameContext {
    private GameState currentState;

    /**
     * Initializes the game context, starting with player X's turn.
     */
    public GameContext() {
        currentState = new XTurnState(); // Start with X's turn
    }

    /**
     * Sets the current state of the game.
     * @param state The new game state.
     */
    public void setState(GameState state) {
        this.currentState = state;
    }

    /**
     * Transitions to the next game state.
     * @param player The current player.
     * @param hasWon Whether the current player has won.
     */
    public void next(Player player, boolean hasWon) {
        currentState.next(this, player , hasWon);
    }

    /**
     * Checks if the game is over.
     * @return true if the game is over, false otherwise.
     */
    public boolean isGameOver() {
        return currentState.isGameOver();
    }

    /**
     * Gets the current state of the game.
     * @return The current game state.
     */
    public GameState getCurrentState() {
        return currentState;
    }
}