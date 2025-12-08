package UtilityClasses;

/**
 * Represents a player in the game.
 */
public class Player {
        private String name;
        private boolean isWhiteSide;

        /**
         * Constructs a new Player.
         * @param name The name of the player.
         * @param isWhiteSide Whether the player is on the white side or not.
         */
        public Player(String name, boolean isWhiteSide) {
            this.name = name;
            this.isWhiteSide = isWhiteSide;
        }

        /**
         * Gets the name of the player.
         * @return The name of the player.
         */
        public String getName() {
            return name;
        }

        /**
         * Checks if the player is on the white side.
         * @return true if the player is on the white side, false otherwise.
         */
        public boolean isWhiteSide() {
            return isWhiteSide;
        }
}
