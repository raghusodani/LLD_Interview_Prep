package Models;

import java.util.*;

public class UserTeam {
    private final String teamId;
    private final String userId;
    private final String teamName;
    private final List<Player> players;
    private Player captain;
    private Player viceCaptain;
    private double totalPoints;

    public UserTeam(String teamId, String userId, String teamName) {
        this.teamId = teamId;
        this.userId = userId;
        this.teamName = teamName;
        this.players = new ArrayList<>();
        this.totalPoints = 0.0;
    }

    public void addPlayer(Player player) {
        players.add(player);
    }

    public void setCaptain(Player captain) {
        if (!players.contains(captain)) {
            throw new IllegalArgumentException("Captain must be from the team");
        }
        this.captain = captain;
    }

    public void setViceCaptain(Player viceCaptain) {
        if (!players.contains(viceCaptain)) {
            throw new IllegalArgumentException("Vice-captain must be from the team");
        }
        if (viceCaptain.equals(captain)) {
            throw new IllegalArgumentException("Captain and Vice-captain cannot be same");
        }
        this.viceCaptain = viceCaptain;
    }

    public void calculateTotalPoints() {
        totalPoints = 0.0;
        for (Player player : players) {
            double playerPoints = player.getPoints();
            if (player.equals(captain)) {
                playerPoints *= 2; // Captain gets 2x points
            } else if (player.equals(viceCaptain)) {
                playerPoints *= 1.5; // Vice-captain gets 1.5x points
            }
            totalPoints += playerPoints;
        }
    }

    public String getTeamId() { return teamId; }
    public String getUserId() { return userId; }
    public String getTeamName() { return teamName; }
    public List<Player> getPlayers() { return new ArrayList<>(players); }
    public Player getCaptain() { return captain; }
    public Player getViceCaptain() { return viceCaptain; }
    public double getTotalPoints() { return totalPoints; }

    @Override
    public String toString() {
        return String.format("Team: %s (User: %s) - Points: %.1f", teamName, userId, totalPoints);
    }
}
