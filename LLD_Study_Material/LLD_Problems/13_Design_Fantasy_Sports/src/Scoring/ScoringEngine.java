package Scoring;

import Models.*;
import java.util.*;

public class ScoringEngine {
    private final ScoreCalculator scoreCalculator;
    private final Map<String, Player> playerRegistry; // playerId -> Player
    private final List<ScoreUpdateListener> listeners;

    public interface ScoreUpdateListener {
        void onScoreUpdate(Player player, double newPoints);
    }

    public ScoringEngine(ScoreCalculator scoreCalculator) {
        this.scoreCalculator = scoreCalculator;
        this.playerRegistry = new HashMap<>();
        this.listeners = new ArrayList<>();
    }

    public void registerPlayer(Player player) {
        playerRegistry.put(player.getPlayerId(), player);
    }

    public void addListener(ScoreUpdateListener listener) {
        listeners.add(listener);
    }

    public void processMatchEvent(MatchEvent event) {
        Player player = playerRegistry.get(event.getPlayerId());
        if (player == null) {
            System.out.println("Player not found: " + event.getPlayerId());
            return;
        }

        double points = scoreCalculator.calculatePoints(event);
        if (points > 0) {
            player.addPoints(points);
            System.out.printf("⚡ %s earned %.1f points for %s (Total: %.1f)\n",
                player.getName(), points, event.getEventType(), player.getPoints());

            // Notify listeners (Observer Pattern)
            notifyListeners(player, player.getPoints());
        }
    }

    private void notifyListeners(Player player, double newPoints) {
        for (ScoreUpdateListener listener : listeners) {
            listener.onScoreUpdate(player, newPoints);
        }
    }

    public void simulateMatch(List<MatchEvent> events) {
        System.out.println("\n📊 === MATCH SIMULATION STARTED ===\n");
        for (MatchEvent event : events) {
            processMatchEvent(event);
        }
        System.out.println("\n📊 === MATCH SIMULATION COMPLETED ===\n");
    }
}
