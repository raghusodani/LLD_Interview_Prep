package Models;

public class MatchEvent {
    public enum EventType {
        RUN_SCORED, WICKET_TAKEN, CATCH_TAKEN, BOUNDARY, SIX
    }

    private final String playerId;
    private final EventType eventType;
    private final int runs; // For run events

    public MatchEvent(String playerId, EventType eventType, int runs) {
        this.playerId = playerId;
        this.eventType = eventType;
        this.runs = runs;
    }

    public MatchEvent(String playerId, EventType eventType) {
        this(playerId, eventType, 0);
    }

    public String getPlayerId() { return playerId; }
    public EventType getEventType() { return eventType; }
    public int getRuns() { return runs; }

    @Override
    public String toString() {
        return String.format("Event: %s by Player %s%s",
            eventType, playerId, runs > 0 ? " (" + runs + " runs)" : "");
    }
}
