package CommonEnum;

public enum PlayerRole {
    WK("Wicket Keeper", 1, 4),
    BAT("Batsman", 1, 8),
    BOWL("Bowler", 1, 8),
    AR("All Rounder", 1, 4);

    private final String displayName;
    private final int minRequired;
    private final int maxAllowed;

    PlayerRole(String displayName, int minRequired, int maxAllowed) {
        this.displayName = displayName;
        this.minRequired = minRequired;
        this.maxAllowed = maxAllowed;
    }

    public String getDisplayName() { return displayName; }
    public int getMinRequired() { return minRequired; }
    public int getMaxAllowed() { return maxAllowed; }
}
