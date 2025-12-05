package Services;

import Models.*;
import Validators.TeamValidator;
import java.util.*;

public class TeamService {
    private final Map<String, UserTeam> teams;

    public TeamService() {
        this.teams = new HashMap<>();
    }

    public UserTeam createTeam(String teamId, String userId, String teamName,
                               List<Player> players, Player captain, Player viceCaptain) {
        UserTeam team = new UserTeam(teamId, userId, teamName);

        for (Player player : players) {
            team.addPlayer(player);
        }

        team.setCaptain(captain);
        team.setViceCaptain(viceCaptain);

        // Validate team
        TeamValidator.ValidationResult result = TeamValidator.validateTeam(team);
        TeamValidator.printValidationResult(result, teamName);

        if (result.isValid()) {
            teams.put(teamId, team);
            return team;
        } else {
            throw new IllegalArgumentException("Team validation failed");
        }
    }

    public UserTeam getTeam(String teamId) {
        return teams.get(teamId);
    }

    public List<UserTeam> getAllTeams() {
        return new ArrayList<>(teams.values());
    }
}
