package Validators;

import Models.*;
import CommonEnum.PlayerRole;
import java.util.*;

public class TeamValidator {
    private static final int MAX_PLAYERS = 11;
    private static final int MAX_FROM_SINGLE_TEAM = 7;

    public static class ValidationResult {
        private final boolean valid;
        private final List<String> errors;

        public ValidationResult(boolean valid, List<String> errors) {
            this.valid = valid;
            this.errors = errors;
        }

        public boolean isValid() { return valid; }
        public List<String> getErrors() { return errors; }
    }

    public static ValidationResult validateTeam(UserTeam team) {
        List<String> errors = new ArrayList<>();

        // Check total players
        if (team.getPlayers().size() != MAX_PLAYERS) {
            errors.add(String.format("Team must have exactly %d players. Found: %d",
                MAX_PLAYERS, team.getPlayers().size()));
        }

        // Check max players from single real team
        Map<String, Integer> teamCount = new HashMap<>();
        for (Player player : team.getPlayers()) {
            teamCount.put(player.getRealTeam(),
                teamCount.getOrDefault(player.getRealTeam(), 0) + 1);
        }

        for (Map.Entry<String, Integer> entry : teamCount.entrySet()) {
            if (entry.getValue() > MAX_FROM_SINGLE_TEAM) {
                errors.add(String.format("Max %d players allowed from team %s. Found: %d",
                    MAX_FROM_SINGLE_TEAM, entry.getKey(), entry.getValue()));
            }
        }

        // Check role requirements
        Map<PlayerRole, Integer> roleCount = new HashMap<>();
        for (Player player : team.getPlayers()) {
            roleCount.put(player.getRole(),
                roleCount.getOrDefault(player.getRole(), 0) + 1);
        }

        for (PlayerRole role : PlayerRole.values()) {
            int count = roleCount.getOrDefault(role, 0);
            if (count < role.getMinRequired()) {
                errors.add(String.format("At least %d %s required. Found: %d",
                    role.getMinRequired(), role.getDisplayName(), count));
            }
            if (count > role.getMaxAllowed()) {
                errors.add(String.format("Maximum %d %s allowed. Found: %d",
                    role.getMaxAllowed(), role.getDisplayName(), count));
            }
        }

        // Check captain and vice-captain
        if (team.getCaptain() == null) {
            errors.add("Captain not selected");
        }
        if (team.getViceCaptain() == null) {
            errors.add("Vice-captain not selected");
        }

        return new ValidationResult(errors.isEmpty(), errors);
    }

    public static void printValidationResult(ValidationResult result, String teamName) {
        if (result.isValid()) {
            System.out.println("✅ Team " + teamName + " is valid!");
        } else {
            System.out.println("❌ Team " + teamName + " validation failed:");
            for (String error : result.getErrors()) {
                System.out.println("  - " + error);
            }
        }
    }
}
