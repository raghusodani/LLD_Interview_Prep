package Models;

import CommonEnum.PlayerRole;

public class Player {
    private final String playerId;
    private final String name;
    private final String realTeam;
    private final PlayerRole role;
    private double points;

    public Player(String playerId, String name, String realTeam, PlayerRole role) {
        this.playerId = playerId;
        this.name = name;
        this.realTeam = realTeam;
        this.role = role;
        this.points = 0.0;
    }

    public String getPlayerId() { return playerId; }
    public String getName() { return name; }
    public String getRealTeam() { return realTeam; }
    public PlayerRole getRole() { return role; }
    public double getPoints() { return points; }

    public void addPoints(double points) {
        this.points += points;
    }

    public void resetPoints() {
        this.points = 0.0;
    }

    @Override
    public String toString() {
        return String.format("%s (%s - %s) [%s]", name, realTeam, role.getDisplayName(), points);
    }
}
