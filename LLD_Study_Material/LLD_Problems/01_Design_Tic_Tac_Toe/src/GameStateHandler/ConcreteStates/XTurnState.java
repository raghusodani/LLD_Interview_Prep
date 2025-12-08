package GameStateHandler.ConcreteStates;

import CommonEnum.Symbol;
import GameStateHandler.Context.GameContext;
import GameStateHandler.GameState;
import Utility.Player;

/**
 * Represents the state where it is player X's turn.
 */
public class XTurnState implements GameState {

    /**
     * Transitions to the next state based on the game's outcome.
     *
     * @param context The game's context.
     * @param player  The current player.
     * @param hasWon  A boolean indicating if the player has won.
     */
    @Override
    public void next(GameContext context, Player player , boolean hasWon) {
        if(hasWon){
            context.setState(player.getSymbol() == Symbol.X ? new XWonState() : new OWonState());
        }else {
        // Switch to OTurnState
        context.setState(new OTurnState());
        }
    }

    /**
     * Indicates that the game is not over yet.
     *
     * @return false, as the game is still in progress.
     */
    @Override
    public boolean isGameOver() {
        return false;
    }
}
