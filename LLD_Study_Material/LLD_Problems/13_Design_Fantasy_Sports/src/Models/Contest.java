package Models;

import CommonEnum.ContestType;
import java.util.*;

public class Contest {
    private final String contestId;
    private final String contestName;
    private final ContestType type;
    private final double entryFee;
    private final double prizePool;
    private final int maxParticipants;
    private final List<UserTeam> participants;
    private boolean started;
    private boolean completed;

    public Contest(String contestId, String contestName, ContestType type,
                   double entryFee, double prizePool) {
        this.contestId = contestId;
        this.contestName = contestName;
        this.type = type;
        this.entryFee = entryFee;
        this.prizePool = prizePool;
        this.maxParticipants = type.getMaxParticipants();
        this.participants = new ArrayList<>();
        this.started = false;
        this.completed = false;
    }

    public boolean joinContest(UserTeam team) {
        if (started) {
            System.out.println("Contest already started!");
            return false;
        }
        if (participants.size() >= maxParticipants) {
            System.out.println("Contest is full!");
            return false;
        }
        participants.add(team);
        System.out.println("Team " + team.getTeamName() + " joined contest: " + contestName);
        return true;
    }

    public void startContest() {
        if (participants.isEmpty()) {
            System.out.println("No participants in contest!");
            return;
        }
        started = true;
        System.out.println("Contest " + contestName + " started with " + participants.size() + " teams!");
    }

    public void endContest() {
        if (!started) {
            System.out.println("Contest not started yet!");
            return;
        }
        completed = true;
        System.out.println("Contest " + contestName + " completed!");
        declareWinners();
    }

    public void declareWinners() {
        if (participants.isEmpty()) return;

        // Sort by total points descending
        List<UserTeam> sortedTeams = new ArrayList<>(participants);
        sortedTeams.sort((t1, t2) -> Double.compare(t2.getTotalPoints(), t1.getTotalPoints()));

        System.out.println("\n=== LEADERBOARD: " + contestName + " ===");
        for (int i = 0; i < Math.min(5, sortedTeams.size()); i++) {
            UserTeam team = sortedTeams.get(i);
            System.out.printf("Rank %d: %s - %.1f points\n",
                i + 1, team.getTeamName(), team.getTotalPoints());
        }

        if (!sortedTeams.isEmpty()) {
            UserTeam winner = sortedTeams.get(0);
            double winAmount = prizePool * 0.5; // Winner gets 50% of prize pool
            System.out.printf("\n🏆 WINNER: %s wins $%.2f!\n",
                winner.getTeamName(), winAmount);
        }
    }

    public String getContestId() { return contestId; }
    public String getContestName() { return contestName; }
    public double getEntryFee() { return entryFee; }
    public double getPrizePool() { return prizePool; }
    public List<UserTeam> getParticipants() { return new ArrayList<>(participants); }
    public boolean isStarted() { return started; }
    public boolean isCompleted() { return completed; }
}
