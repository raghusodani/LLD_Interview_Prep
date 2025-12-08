package Utility;

import CommonEnum.Symbol;
import PlayerStrategies.PlayerStrategy;

/**
 * Represents a player in the game.
 */
public class Player {
    Symbol symbol;
    PlayerStrategy playerStrategy;

    /**
     * Constructs a new Player.
     * @param symbol The symbol of the player.
     * @param playerStrategy The strategy of the player.
     */
    public Player (Symbol symbol , PlayerStrategy playerStrategy){
        this.symbol = symbol;
        this.playerStrategy = playerStrategy;
    }

    /**
     * Gets the symbol of the player.
     * @return The symbol of the player.
     */
    public Symbol getSymbol(){
        return symbol;
    }

    /**
     * Gets the strategy of the player.
     * @return The strategy of the player.
     */
    public PlayerStrategy getPlayerStrategy(){
        return playerStrategy;
    }
}
