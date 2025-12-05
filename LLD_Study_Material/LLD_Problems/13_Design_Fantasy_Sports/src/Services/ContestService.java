package Services;

import Models.*;
import CommonEnum.ContestType;
import Scoring.ScoringEngine;
import java.util.*;

public class ContestService implements ScoringEngine.ScoreUpdateListener {
    private final Map<String, Contest> contests;
    private final Map<String, List<UserTeam>> contestTeams; // contestId -> teams

    public ContestService() {
        this.contests = new HashMap<>();
        this.contestTeams = new HashMap<>();
    }

    public Contest createContest(String contestId, String contestName,
                                 ContestType type, double entryFee, double prizePool) {
        Contest contest = new Contest(contestId, contestName, type, entryFee, prizePool);
        contests.put(contestId, contest);
        contestTeams.put(contestId, new ArrayList<>());
        System.out.println("✅ Contest created: " + contestName);
        return contest;
    }

    public boolean joinContest(String contestId, UserTeam team) {
        Contest contest = contests.get(contestId);
        if (contest == null) {
            System.out.println("Contest not found!");
            return false;
        }

        if (contest.joinContest(team)) {
            contestTeams.get(contestId).add(team);
            return true;
        }
        return false;
    }

    public void startContest(String contestId) {
        Contest contest = contests.get(contestId);
        if (contest != null) {
            contest.startContest();
        }
    }

    public void endContest(String contestId) {
        Contest contest = contests.get(contestId);
        if (contest != null) {
            // Update all team scores before ending
            List<UserTeam> teams = contestTeams.get(contestId);
            for (UserTeam team : teams) {
                team.calculateTotalPoints();
            }
            contest.endContest();
        }
    }

    @Override
    public void onScoreUpdate(Player player, double newPoints) {
        // Real-time update: Recalculate all teams containing this player
        for (List<UserTeam> teams : contestTeams.values()) {
            for (UserTeam team : teams) {
                for (Player p : team.getPlayers()) {
                    if (p.getPlayerId().equals(player.getPlayerId())) {
                        team.calculateTotalPoints();
                        break;
                    }
                }
            }
        }
    }

    public Contest getContest(String contestId) {
        return contests.get(contestId);
    }
}
