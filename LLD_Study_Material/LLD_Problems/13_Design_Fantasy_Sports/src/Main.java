import Models.*;
import CommonEnum.*;
import Validators.*;
import Scoring.*;
import Services.*;
import java.util.*;

public class Main {
    public static void main(String[] args) {
        System.out.println("🏏 === FANTASY SPORTS PLATFORM ===\n");

        // Initialize services
        TeamService teamService = new TeamService();
        ContestService contestService = new ContestService();
        ScoringEngine scoringEngine = new ScoringEngine(new CricketScoreCalculator());

        // Register ContestService as listener for real-time updates
        scoringEngine.addListener(contestService);

        // Create player pool (Real players from two teams: India and Australia)
        List<Player> playerPool = createPlayerPool();

        // Register all players with scoring engine
        for (Player player : playerPool) {
            scoringEngine.registerPlayer(player);
        }

        System.out.println("📋 Player Pool Created: " + playerPool.size() + " players\n");

        // Scenario 1: Create valid team
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        System.out.println("SCENARIO 1: Creating Valid Team");
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n");

        List<Player> team1Players = Arrays.asList(
            playerPool.get(0),  // Dhoni (IND) - WK
            playerPool.get(1),  // Virat (IND) - BAT
            playerPool.get(2),  // Rohit (IND) - BAT
            playerPool.get(3),  // Bumrah (IND) - BOWL
            playerPool.get(4),  // Jadeja (IND) - AR
            playerPool.get(10), // Smith (AUS) - BAT
            playerPool.get(11), // Warner (AUS) - BAT
            playerPool.get(12), // Starc (AUS) - BOWL
            playerPool.get(13), // Cummins (AUS) - BOWL
            playerPool.get(14), // Maxwell (AUS) - AR
            playerPool.get(5)   // Pandya (IND) - AR (7th from IND - max allowed)
        );

        UserTeam team1 = teamService.createTeam("T1", "user1", "Winning Strikers",
            team1Players, playerPool.get(1), playerPool.get(0)); // Virat (C), Dhoni (VC)

        // Scenario 2: Try to create invalid team (too many from one team)
        System.out.println("\n━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        System.out.println("SCENARIO 2: Invalid Team (8 from India)");
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n");

        try {
            List<Player> invalidTeam = Arrays.asList(
                playerPool.get(0),  // 1. Dhoni (IND)
                playerPool.get(1),  // 2. Virat (IND)
                playerPool.get(2),  // 3. Rohit (IND)
                playerPool.get(3),  // 4. Bumrah (IND)
                playerPool.get(4),  // 5. Jadeja (IND)
                playerPool.get(5),  // 6. Pandya (IND)
                playerPool.get(6),  // 7. Shami (IND)
                playerPool.get(7),  // 8. Rahul (IND) - TOO MANY!
                playerPool.get(10), // Smith (AUS)
                playerPool.get(11), // Warner (AUS)
                playerPool.get(12)  // Starc (AUS)
            );

            teamService.createTeam("T_INVALID", "user2", "Invalid Team",
                invalidTeam, playerPool.get(1), playerPool.get(0));
        } catch (IllegalArgumentException e) {
            System.out.println("Expected error: " + e.getMessage() + "\n");
        }

        // Create a second valid team
        List<Player> team2Players = Arrays.asList(
            playerPool.get(9),  // Carey (AUS) - WK
            playerPool.get(10), // Smith (AUS) - BAT
            playerPool.get(11), // Warner (AUS) - BAT
            playerPool.get(12), // Starc (AUS) - BOWL
            playerPool.get(13), // Cummins (AUS) - BOWL
            playerPool.get(14), // Maxwell (AUS) - AR
            playerPool.get(15), // Marsh (AUS) - AR
            playerPool.get(1),  // Virat (IND) - BAT
            playerPool.get(2),  // Rohit (IND) - BAT
            playerPool.get(3),  // Bumrah (IND) - BOWL
            playerPool.get(4)   // Jadeja (IND) - AR (4 from IND)
        );

        UserTeam team2 = teamService.createTeam("T2", "user2", "Aussie Legends",
            team2Players, playerPool.get(10), playerPool.get(14)); // Smith (C), Maxwell (VC)

        // Create contest
        System.out.println("\n━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        System.out.println("SCENARIO 3: Contest Management");
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n");

        Contest contest = contestService.createContest("C1", "India vs Australia Mega Contest",
            ContestType.SMALL_LEAGUE, 50.0, 5000.0);

        contestService.joinContest("C1", team1);
        contestService.joinContest("C1", team2);

        contestService.startContest("C1");

        // Simulate match events
        System.out.println("\n━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        System.out.println("SCENARIO 4: Match Simulation & Scoring");
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");

        List<MatchEvent> matchEvents = Arrays.asList(
            new MatchEvent("P1", MatchEvent.EventType.RUN_SCORED, 4),  // Virat 4 runs
            new MatchEvent("P1", MatchEvent.EventType.BOUNDARY),       // Virat boundary
            new MatchEvent("P2", MatchEvent.EventType.RUN_SCORED, 6),  // Rohit 6 runs
            new MatchEvent("P2", MatchEvent.EventType.SIX),             // Rohit six
            new MatchEvent("P3", MatchEvent.EventType.WICKET_TAKEN),    // Bumrah wicket
            new MatchEvent("P3", MatchEvent.EventType.WICKET_TAKEN),    // Bumrah wicket
            new MatchEvent("P10", MatchEvent.EventType.RUN_SCORED, 8),  // Smith 8 runs
            new MatchEvent("P10", MatchEvent.EventType.BOUNDARY),       // Smith boundary
            new MatchEvent("P11", MatchEvent.EventType.SIX),            // Warner six
            new MatchEvent("P12", MatchEvent.EventType.WICKET_TAKEN),   // Starc wicket
            new MatchEvent("P4", MatchEvent.EventType.CATCH_TAKEN),     // Jadeja catch
            new MatchEvent("P14", MatchEvent.EventType.RUN_SCORED, 15), // Maxwell 15 runs
            new MatchEvent("P14", MatchEvent.EventType.SIX)             // Maxwell six
        );

        scoringEngine.simulateMatch(matchEvents);

        // End contest and declare winners
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        System.out.println("SCENARIO 5: Contest Results");
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n");

        contestService.endContest("C1");

        // Show team details
        System.out.println("\n━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        System.out.println("TEAM BREAKDOWNS");
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n");

        printTeamDetails(team1);
        System.out.println();
        printTeamDetails(team2);

        System.out.println("\n✅ === FANTASY SPORTS PLATFORM DEMO COMPLETE ===");
    }

    private static List<Player> createPlayerPool() {
        List<Player> pool = new ArrayList<>();

        // India players
        pool.add(new Player("P0", "MS Dhoni", "IND", PlayerRole.WK));
        pool.add(new Player("P1", "Virat Kohli", "IND", PlayerRole.BAT));
        pool.add(new Player("P2", "Rohit Sharma", "IND", PlayerRole.BAT));
        pool.add(new Player("P3", "Jasprit Bumrah", "IND", PlayerRole.BOWL));
        pool.add(new Player("P4", "Ravindra Jadeja", "IND", PlayerRole.AR));
        pool.add(new Player("P5", "Hardik Pandya", "IND", PlayerRole.AR));
        pool.add(new Player("P6", "Mohammed Shami", "IND", PlayerRole.BOWL));
        pool.add(new Player("P7", "KL Rahul", "IND", PlayerRole.BAT));
        pool.add(new Player("P8", "Rishabh Pant", "IND", PlayerRole.WK));

        // Australia players
        pool.add(new Player("P9", "Alex Carey", "AUS", PlayerRole.WK));
        pool.add(new Player("P10", "Steve Smith", "AUS", PlayerRole.BAT));
        pool.add(new Player("P11", "David Warner", "AUS", PlayerRole.BAT));
        pool.add(new Player("P12", "Mitchell Starc", "AUS", PlayerRole.BOWL));
        pool.add(new Player("P13", "Pat Cummins", "AUS", PlayerRole.BOWL));
        pool.add(new Player("P14", "Glenn Maxwell", "AUS", PlayerRole.AR));
        pool.add(new Player("P15", "Mitchell Marsh", "AUS", PlayerRole.AR));
        pool.add(new Player("P16", "Josh Hazlewood", "AUS", PlayerRole.BOWL));
        pool.add(new Player("P17", "Travis Head", "AUS", PlayerRole.BAT));

        return pool;
    }

    private static void printTeamDetails(UserTeam team) {
        System.out.println("Team: " + team.getTeamName() + " (User: " + team.getUserId() + ")");
        System.out.println("Captain: " + team.getCaptain().getName() + " (2x points)");
        System.out.println("Vice-Captain: " + team.getViceCaptain().getName() + " (1.5x points)");
        System.out.println("\nPlayers:");

        Map<PlayerRole, List<Player>> byRole = new HashMap<>();
        for (Player p : team.getPlayers()) {
            byRole.computeIfAbsent(p.getRole(), k -> new ArrayList<>()).add(p);
        }

        for (PlayerRole role : PlayerRole.values()) {
            List<Player> players = byRole.get(role);
            if (players != null && !players.isEmpty()) {
                System.out.println("  " + role.getDisplayName() + ":");
                for (Player p : players) {
                    System.out.printf("    - %s: %.1f points", p.getName(), p.getPoints());
                    if (p.equals(team.getCaptain())) System.out.print(" (C)");
                    if (p.equals(team.getViceCaptain())) System.out.print(" (VC)");
                    System.out.println();
                }
            }
        }

        System.out.printf("\nTotal Team Points: %.1f\n", team.getTotalPoints());
    }
}
