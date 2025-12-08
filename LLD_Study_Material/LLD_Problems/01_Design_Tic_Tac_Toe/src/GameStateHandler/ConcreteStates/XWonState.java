package GameStateHandler.ConcreteStates;

import GameStateHandler.Context.GameContext;
import GameStateHandler.GameState;
import Utility.Player;

/**
 * Represents the state where player X has won the game.
 */
public class XWonState implements GameState {
    /**
     * No next state since the game is over.
     * @param context The game context.
     * @param player The current player.
     * @param hasWon Whether the player has won.
     */
    @Override
    public void next(GameContext context, Player player , boolean hasWon) {
       // Game over, no next state
    }

    /**
     * Indicates that the game is over.
     * @return true since the game is over.
     */
    @Override
    public boolean isGameOver() {
        return true;
    }
}
