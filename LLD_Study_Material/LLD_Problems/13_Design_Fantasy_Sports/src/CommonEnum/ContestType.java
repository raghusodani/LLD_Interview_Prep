package CommonEnum;

public enum ContestType {
    HEAD_TO_HEAD("Head to Head", 2),
    SMALL_LEAGUE("Small League", 10),
    MEGA_CONTEST("Mega Contest", 100000);

    private final String displayName;
    private final int maxParticipants;

    ContestType(String displayName, int maxParticipants) {
        this.displayName = displayName;
        this.maxParticipants = maxParticipants;
    }

    public String getDisplayName() { return displayName; }
    public int getMaxParticipants() { return maxParticipants; }
}
