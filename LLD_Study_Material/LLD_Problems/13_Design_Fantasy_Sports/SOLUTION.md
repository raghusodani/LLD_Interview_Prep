# Design Fantasy Sports Platform - Comprehensive Solution 🏏

## **Problem Statement**

Design a Fantasy Sports platform (like Dream11, FanDuel) where users can:
- Create virtual teams with real players
- Join contests with entry fees and prize pools
- Earn points based on real-life player performance
- Compete against other users
- Win prizes based on rankings

### **Zeta-Specific Constraints:**
- ✅ Max 7 players from one real team
- ✅ Specific role requirements (WK, BAT, BOWL, AR)
- ✅ Near real-time scoring engine
- ✅ Contest entry fees and prize distribution

---

## **🎯 Our Approach**

### **1. Core Requirements Analysis**

**Functional Requirements:**
- ✅ Create teams with 11 players
- ✅ Validate team composition (roles, real team limits)
- ✅ Captain selection (2x points multiplier)
- ✅ Vice-captain selection (1.5x points multiplier)
- ✅ Join contests with entry fees
- ✅ Process match events and update scores in real-time
- ✅ Calculate rankings and distribute prizes

**Non-Functional Requirements:**
- ✅ Real-time score updates (Observer pattern)
- ✅ Extensible scoring rules (Strategy pattern)
- ✅ Validation before team creation
- ✅ Thread-safe operations (for production)

---

## **🏗️ Architecture & Design Patterns**

### **Pattern 1: Observer Pattern (Real-time Updates)** ⭐

**Where:** Scoring Engine notifies ContestService

**Why:**
- Match events happen continuously
- Multiple contests need score updates simultaneously
- Decouples scoring logic from contest management
- Enables real-time leaderboard updates

**Implementation:**

```java
public interface ScoreUpdateListener {
    void onScoreUpdate(Player player, double newPoints);
}

public class ContestService implements ScoreUpdateListener {
    @Override
    public void onScoreUpdate(Player player, double newPoints) {
        // Recalculate all teams containing this player
        for (UserTeam team : allTeams) {
            if (team.hasPlayer(player)) {
                team.calculateTotalPoints();
            }
        }
    }
}

// In ScoringEngine
public void processMatchEvent(MatchEvent event) {
    player.addPoints(points);
    notifyListeners(player, player.getPoints()); // ⚡ Real-time!
}
```

**Benefits:**
- ✅ Loose coupling between scoring and contests
- ✅ Easy to add new observers (analytics, notifications)
- ✅ Real-time updates without polling
- ✅ Follows Open/Closed principle

---

### **Pattern 2: Strategy Pattern (Scoring Rules)**

**Where:** ScoreCalculator interface with CricketScoreCalculator

**Why:**
- Different sports have different scoring rules
- Easy to add Football, Basketball scoring
- Business rules change frequently
- Testable scoring logic

**Implementation:**

```java
public interface ScoreCalculator {
    double calculatePoints(MatchEvent event);
}

public class CricketScoreCalculator implements ScoreCalculator {
    @Override
    public double calculatePoints(MatchEvent event) {
        switch (event.getEventType()) {
            case RUN_SCORED: return runs * 1.0;
            case WICKET_TAKEN: return 25.0;
            case BOUNDARY: return 1.0;
            case SIX: return 2.0;
            case CATCH_TAKEN: return 8.0;
        }
    }
}

// Easy to add new sport
public class FootballScoreCalculator implements ScoreCalculator {
    @Override
    public double calculatePoints(MatchEvent event) {
        // Different rules: goals, assists, saves, etc.
    }
}
```

**Benefits:**
- ✅ Sport-agnostic platform
- ✅ Easy to modify scoring rules per season
- ✅ A/B testing different scoring systems
- ✅ Individual testing of scoring logic

---

### **Pattern 3: Validator Pattern (Team Validation)**

**Where:** TeamValidator with ValidationResult

**Why:**
- Complex validation rules (7 checks!)
- Need detailed error messages
- Separation from business logic
- Reusable across services

**Validation Rules:**
1. Exactly 11 players
2. Max 7 from single real team ⚠️ Key constraint!
3. Min/Max role requirements:
   - WK: 1-4
   - BAT: 1-8
   - BOWL: 1-8
   - AR: 1-4
4. Captain selected and in team
5. Vice-captain selected and in team
6. Captain ≠ Vice-captain

**Implementation:**

```java
public static ValidationResult validateTeam(UserTeam team) {
    List<String> errors = new ArrayList<>();

    // Rule 1: Total players
    if (team.getPlayers().size() != 11) {
        errors.add("Must have 11 players");
    }

    // Rule 2: Max 7 from single team
    Map<String, Integer> teamCount = new HashMap<>();
    for (Player p : team.getPlayers()) {
        teamCount.merge(p.getRealTeam(), 1, Integer::sum);
    }
    for (Map.Entry<String, Integer> e : teamCount.entrySet()) {
        if (e.getValue() > 7) {
            errors.add("Max 7 from " + e.getKey() + ", found " + e.getValue());
        }
    }

    // Rule 3: Role requirements
    // ... similar validation

    return new ValidationResult(errors.isEmpty(), errors);
}
```

**Benefits:**
- ✅ Clear error messages for users
- ✅ Single Responsibility Principle
- ✅ Easy to add new validation rules
- ✅ Testable independently

---

### **Pattern 4: Service Layer Pattern**

**Where:** TeamService, ContestService

**Why:**
- Separation of concerns
- Business logic encapsulation
- Easy to add caching/persistence
- Clean API for controllers

**Architecture:**

```
┌─────────────┐
│    Main     │ (Presentation Layer)
└──────┬──────┘
       │
   ┌───┴────────────────┐
   │                    │
┌──▼───────────┐  ┌────▼──────────┐
│ TeamService  │  │ContestService │ (Service Layer)
└──────┬───────┘  └────┬──────────┘
       │               │
   ┌───┴───────────────┴──┐
   │                      │
┌──▼──────┐        ┌─────▼────────┐
│UserTeam │        │   Contest    │ (Domain Models)
└─────────┘        └──────────────┘
```

---

## **🔑 Key Design Decisions**

### **Decision 1: Captain/Vice-Captain Multipliers in Team**

**What:** Captain gets 2x points, Vice-captain gets 1.5x

**Where:**
```java
public void calculateTotalPoints() {
    totalPoints = 0.0;
    for (Player player : players) {
        double points = player.getPoints();
        if (player.equals(captain)) {
            points *= 2;  // Captain multiplier
        } else if (player.equals(viceCaptain)) {
            points *= 1.5; // Vice-captain multiplier
        }
        totalPoints += points;
    }
}
```

**Why:**
- Strategic decision-making for users
- Risk-reward balance (picking right captain matters!)
- Differentiates teams even with same players

**Interview Question:**
> "What if captain gets injured and doesn't play?"

**Answer:**
> "Current: Captain still gets 2x points (0 * 2 = 0). Better approach: Allow captain transfer before match starts or auto-promote vice-captain. Could add `substituteCaptain()` method with validation that match hasn't started."

---

### **Decision 2: Real-Time Updates with Observer Pattern**

**What:** ContestService observes ScoringEngine

**Why:**
- Push model > Pull model for real-time
- Avoids polling overhead
- Instant leaderboard updates
- Better user experience

**Interview Question:**
> "How to handle millions of teams? Won't Observer pattern be slow?"

**Answer:**
```
Optimizations:
1. **Batching:** Buffer updates for 1 second, update in batch
2. **Async Processing:** Use queue + worker threads
3. **Sharding:** Partition contests by ID
4. **Caching:** Cache team-player mappings
5. **Dirty Flag:** Only recalculate changed teams

Production:
- Use message queue (Kafka/SQS)
- WebSockets for client updates
- Redis for leaderboard caching
- Database writes are async
```

---

### **Decision 3: Immutable Player Points**

**What:** Player points updated directly, not copied per team

**Why:**
- Memory efficient (one Player object shared across teams)
- Real-time updates automatically reflected
- Avoids synchronization issues

**Trade-off:**
- Player points are global (all teams see same points)
- Can't have different scoring per contest easily

**Better for Scale:**
```java
// Alternative: PlayerPerformance value object
class PlayerPerformance {
    Player player;
    double points;  // Copy per team
}

// Teams store PlayerPerformance, not Player directly
```

**Interview Question:**
> "What if two contests have different scoring rules?"

**Answer:**
> "Current design: All contests share same player points. For different rules: (1) Create separate Player instances per contest, or (2) Store points in Team-Player mapping, not in Player directly. Trade-off: Memory (duplicate players) vs Complexity (mapping management)."

---

### **Decision 4: Validation Before Team Creation**

**What:** Validate BEFORE adding to service

**Why:**
- Fail fast principle
- Don't persist invalid state
- Clear error messages to user
- Can charge entry fee only after validation

**Implementation Flow:**
```
1. User selects 11 players
   ↓
2. TeamValidator.validateTeam() ← VALIDATE FIRST
   ↓
3. If valid: teamService.createTeam()
   ↓
4. If invalid: Return errors, don't create
```

---

## **💡 SOLID Principles Applied**

### **S - Single Responsibility**
- `TeamValidator` - Only validates teams
- `ScoringEngine` - Only processes events and updates scores
- `ContestService` - Only manages contests
- `ScoreCalculator` - Only calculates points per event

### **O - Open/Closed**
- Add new sport: Implement `ScoreCalculator` interface
- Add new validation rule: Extend `TeamValidator`
- Add new observer: Implement `ScoreUpdateListener`
- No modification of existing code!

### **L - Liskov Substitution**
- Any `ScoreCalculator` can replace `CricketScoreCalculator`
- All implementations follow contract correctly

### **I - Interface Segregation**
- `ScoreCalculator` - Only one method `calculatePoints()`
- `ScoreUpdateListener` - Only one method `onScoreUpdate()`
- Clients depend on minimal interfaces

### **D - Dependency Inversion**
- `ScoringEngine` depends on `ScoreCalculator` interface, not concrete class
- `ContestService` implements `ScoreUpdateListener` interface
- High-level modules don't depend on low-level details

---

## **🎭 Scenario Walkthrough**

### **Complete Flow: Team Creation → Contest → Scoring → Winner**

```
1. User creates team "Winning Strikers"
   ├─ Selects 11 players (4 BAT, 3 BOWL, 1 WK, 3 AR)
   ├─ Max 7 from India, 4 from Australia
   ├─ Captain: Virat Kohli (2x multiplier)
   └─ Vice-Captain: MS Dhoni (1.5x multiplier)
   ↓
2. Validation runs (TeamValidator)
   ├─ ✅ 11 players total
   ├─ ✅ 7 from IND (max allowed)
   ├─ ✅ Role requirements met
   └─ ✅ Captain & VC selected
   ↓
3. Team joins contest "India vs Australia"
   ├─ Entry fee: $50
   ├─ Prize pool: $5000
   └─ Contest type: Small League (10 teams max)
   ↓
4. Match starts, events stream in
   ├─ Event: Virat scores 4 runs
   │   ├─ ScoringEngine calculates: 4.0 points
   │   ├─ Updates Player: Virat (4.0 total)
   │   └─ Notifies ContestService (Observer)
   │       └─ Recalculates all teams with Virat
   ↓
5. Match ends
   ├─ Final points calculated
   ├─ Captain bonus applied: Virat 5.0 → 10.0
   ├─ Vice-captain bonus: Dhoni 0.0 → 0.0
   └─ Team total: 129.0 points
   ↓
6. Contest declares winner
   ├─ Sort teams by total points
   ├─ Rank 1: Aussie Legends (141.5 pts)
   ├─ Rank 2: Winning Strikers (129.0 pts)
   └─ Winner gets 50% of prize pool: $2500
```

---

## **🚀 Extensions & Enhancements**

### **1. Salary Cap System**
```java
public class Player {
    private final double salary; // Credit cost
}

public class TeamValidator {
    private static final double MAX_CREDITS = 100.0;

    public static ValidationResult validateBudget(UserTeam team) {
        double totalCost = team.getPlayers().stream()
            .mapToDouble(Player::getSalary)
            .sum();

        if (totalCost > MAX_CREDITS) {
            return new ValidationResult(false,
                List.of("Exceeds budget: " + totalCost + " credits"));
        }
        return new ValidationResult(true, List.of());
    }
}
```

### **2. Multiple Contest Types**
```java
public class MegaContest extends Contest {
    @Override
    public void distributePrizes() {
        // Top 30% win (not just winner)
        int winnersCount = (int)(participants.size() * 0.3);
        // Progressive prize distribution
    }
}

public class HeadToHead extends Contest {
    @Override
    public void distributePrizes() {
        // Winner takes all
    }
}
```

### **3. Live Leaderboard Updates**
```java
public class LeaderboardService implements ScoreUpdateListener {
    private final Map<String, List<TeamRanking>> leaderboards;

    @Override
    public void onScoreUpdate(Player player, double newPoints) {
        // Update only affected rankings
        // Use sorted data structure (TreeSet) for efficiency
        // Push to clients via WebSocket
    }
}
```

### **4. Player Injury Status**
```java
public enum PlayerStatus {
    AVAILABLE, INJURED, DOUBTFUL
}

public class Player {
    private PlayerStatus status;

    public boolean isPlayingInMatch() {
        return status == PlayerStatus.AVAILABLE;
    }
}

// Before match starts, validate all players are available
```

---

## **🎯 Interview Questions & Answers**

### **Q1: How to prevent users from creating 100 teams and gaming the system?**

**Answer:**
```
Multiple approaches:

1. **Team Limit per User:**
   - Limit to 10-15 teams per contest
   - Store: Map<UserId, List<TeamId>> per contest

2. **Entry Fee Economics:**
   - Higher entry fees discourage spam teams
   - Bulk entry discounts (5+ teams)

3. **Similarity Check:**
   - Flag teams >90% similar
   - Penalize if detected

4. **Contest Types:**
   - Single-entry contests (only 1 team per user)
   - Multi-entry contests (up to N teams)

Production:
- Rate limiting on team creation API
- Fraud detection system
- Machine learning for pattern detection
```

---

### **Q2: How to handle match data ingestion at scale?**

**Answer:**
```
Real-world architecture:

1. **Data Source:**
   Third-party API (CricketAPI, SportRadar)
   ├─ Polling every 5 seconds
   └─ Or WebSocket for real-time

2. **Message Queue:**
   API → Kafka Topic → Multiple Consumers
   ├─ Event: { playerId, eventType, timestamp }
   └─ Partitioned by matchId

3. **Processing:**
   Consumer Threads (per partition)
   ├─ Idempotency check (deduplicate events)
   ├─ Score calculation
   └─ Publish to update topic

4. **Distribution:**
   Update Topic → Multiple Services
   ├─ Scoring Service
   ├─ Leaderboard Service
   └─ Notification Service

5. **Storage:**
   Write-behind pattern
   ├─ In-memory: Redis (hot data)
   ├─ Database: PostgreSQL (persistence)
   └─ Analytics: Data Lake (historical)

Throughput: 10,000+ events/second
Latency: <100ms end-to-end
```

---

### **Q3: Explain the "Max 7 from one team" validation. Why this rule?**

**Answer:**
```
Business Reason:
- Encourages balanced team selection
- Prevents all users from picking entire IND team
- Makes contest more interesting and competitive
- Risk diversification

Implementation:
Map<RealTeam, Count> teamCount = new HashMap<>();
for (Player p : userTeam) {
    teamCount.merge(p.getRealTeam(), 1, Integer::sum);
}
// Check each count <= 7

Edge Cases:
1. What if match is IND vs IND (domestic)?
   - Still apply rule (prevents 11 from Mumbai Indians)

2. What about neutral players (umpires)?
   - Not part of fantasy (only actual players)

Performance:
- O(11) for counting = O(1) practically
- HashMap lookup: O(1)
- Total: O(1) for validation
```

---

### **Q4: How to handle captain change after match starts?**

**Answer:**
```
Current: Not allowed (captain locked at team creation)

Business Rules:
1. **Before Match:** Can change captain freely
2. **After Match Start:** Locked (prevents gaming)
3. **If Captain Injured:** Auto-promote vice-captain

Implementation:
public class UserTeam {
    private LocalDateTime lockTime;

    public void setCaptain(Player p) {
        if (LocalDateTime.now().isAfter(lockTime)) {
            throw new IllegalStateException("Team locked");
        }
        this.captain = p;
    }

    public void lockTeam(LocalDateTime matchStart) {
        this.lockTime = matchStart.minusMinutes(15);
    }
}

Edge Case:
What if user tries to exploit time zones?
- Use server time, not user time
- Lock based on match venue timezone
```

---

### **Q5: How to calculate and distribute prizes fairly?**

**Answer:**
```
Prize Distribution Strategies:

1. **Winner Takes All (Head-to-Head):**
   Winner: 90% of pool
   Platform: 10% rake

2. **Top Heavy (Small League):**
   Rank 1: 50%
   Rank 2: 25%
   Rank 3: 15%
   Rank 4-5: 5% each

3. **Distributed (Mega Contest):**
   Top 30% get prizes
   Progressive distribution:
   - Rank 1: 10%
   - Rank 2-10: 20%
   - Rank 11-100: 30%
   - Rank 101-1000: 30%
   - Rest: 10%

Implementation:
public interface PrizeDistributionStrategy {
    Map<UserTeam, Double> distributePrizes(
        List<UserTeam> rankedTeams, double prizePool);
}

Fairness Checks:
- Handle ties (same points) → Equal prize split
- Min prize amount (no $0.001 prizes)
- Platform rake (10-20% is standard)
- Tax withholding for large wins

Production:
- Payment gateway integration
- Transaction log for audit
- Refund mechanism if contest cancelled
```

---

### **Q6: How would you implement live scoring with 1 million users?**

**Answer:**
```
Scalability Architecture:

1. **Scoring Service (Stateless):**
   Load Balanced Servers
   ├─ Process match events
   ├─ Calculate points
   └─ Publish to queue

2. **Leaderboard Service (Stateful):**
   Redis Sorted Set per Contest
   ├─ ZADD contestId userId score
   ├─ ZREVRANGE contestId 0 9 (top 10)
   └─ O(log N) updates, O(1) queries

3. **Notification Service:**
   WebSocket Connections
   ├─ Users subscribe to their contests
   ├─ Server pushes rank changes
   └─ Only send diffs (not full leaderboard)

4. **Database (Eventual Consistency):**
   Async writes to PostgreSQL
   ├─ Batch every 10 seconds
   └─ Serves as source of truth

Example Redis Commands:
# Update score
ZADD contest:C1 129.0 team:T1
ZADD contest:C1 141.5 team:T2

# Get top 10
ZREVRANGE contest:C1 0 9 WITHSCORES

# Get user rank
ZREVRANK contest:C1 team:T1  # Returns: 1 (0-indexed)

Benefits:
- Handles 1M+ teams per contest
- Sub-millisecond leaderboard queries
- Minimal database load
- Real-time updates

Cost:
- Redis memory: ~1KB per team = 1GB for 1M teams
- Database: Batch writes reduce load by 100x
```

---

### **Q7: How to test the validation logic?**

**Answer:**
```java
@Test
public void testMaxPlayersFromSingleTeam() {
    // Create team with 8 players from IND
    UserTeam team = createTeamWith8FromIndia();

    ValidationResult result = TeamValidator.validateTeam(team);

    assertFalse(result.isValid());
    assertTrue(result.getErrors().stream()
        .anyMatch(e -> e.contains("Max 7 players")));
}

@Test
public void testRoleRequirements() {
    // Team with 0 wicket keepers
    UserTeam team = createTeamWithNoWK();

    ValidationResult result = TeamValidator.validateTeam(team);

    assertFalse(result.isValid());
    assertTrue(result.getErrors().stream()
        .anyMatch(e -> e.contains("At least 1 Wicket Keeper")));
}

@Test
public void testValidTeam() {
    UserTeam team = createValidTeam();
    ValidationResult result = TeamValidator.validateTeam(team);
    assertTrue(result.isValid());
}

Edge Cases to Test:
1. Exactly 7 from one team (boundary)
2. Captain = Vice-captain (should fail)
3. Captain not in team (should fail)
4. Exactly min/max role counts (boundaries)
5. Null captain/vice-captain
6. Duplicate players
7. 10 players (missing one)
8. 12 players (extra one)
```

---

### **Q8: How would you handle different match formats (T20, ODI, Test)?**

**Answer:**
```
Strategy Pattern for Match Format:

public interface MatchFormatScoring {
    double getMultiplier(MatchEvent event);
}

public class T20Scoring implements MatchFormatScoring {
    @Override
    public double getMultiplier(MatchEvent event) {
        // T20: Aggressive batting gets bonus
        if (event.getEventType() == SIX) {
            return 1.5;  // 50% bonus for sixes
        }
        return 1.0;
    }
}

public class TestMatchScoring implements MatchFormatScoring {
    @Override
    public double getMultiplier(MatchEvent event) {
        // Test: Wickets matter more
        if (event.getEventType() == WICKET) {
            return 2.0;  // 2x bonus for wickets
        }
        return 1.0;
    }
}

Usage:
double basePoints = scoreCalculator.calculatePoints(event);
double formatMultiplier = matchFormat.getMultiplier(event);
double finalPoints = basePoints * formatMultiplier;

This allows:
- Same event, different points in different formats
- Easy to add new formats (The Hundred, IPL rules)
- A/B test different scoring systems
```

---

### **Q9: How to handle contest cancellation and refunds?**

**Answer:**
```
Cancellation Scenarios:

1. **Match Abandoned (Rain):**
   - Refund 100% entry fee
   - No winner declared
   - State: CANCELLED

2. **Less than Minimum Participants:**
   - Contest needs 2+ teams to run
   - Refund all if not met
   - State: FAILED_TO_START

3. **Technical Issue:**
   - Scoring failure
   - Manual intervention
   - Refund or re-run

Implementation:
public enum ContestStatus {
    OPEN, STARTED, COMPLETED, CANCELLED, FAILED
}

public class Contest {
    public void cancel(String reason) {
        if (completed) {
            throw new IllegalStateException("Cannot cancel completed contest");
        }

        this.status = ContestStatus.CANCELLED;

        // Initiate refunds
        for (UserTeam team : participants) {
            refundService.processRefund(team.getUserId(), entryFee);
        }

        // Log for audit
        auditLog.log("Contest " + contestId + " cancelled: " + reason);
    }
}

Refund Processing:
- Idempotent (can retry safely)
- Async processing
- Transaction log for tracking
- Notification to users (email/SMS)
```

---

### **Q10: Design the rank tracking system for live updates**

**Answer:**
```
Challenge: Efficiently track rank changes for 1M teams

Solution 1: Redis Sorted Set (Recommended)
┌─────────────────────────────────────┐
│ Redis Sorted Set (contestId)       │
├─────────────────────────────────────┤
│ Score    TeamId                     │
├─────────────────────────────────────┤
│ 245.5 → team:T100 (Rank 1)         │
│ 238.2 → team:T459 (Rank 2)         │
│ 235.8 → team:T777 (Rank 3)         │
│ ...                                 │
└─────────────────────────────────────┘

Commands:
ZADD contest:C1 245.5 team:T100    # Update score: O(log N)
ZREVRANK contest:C1 team:T100      # Get rank: O(log N)
ZREVRANGE contest:C1 0 9 WITHSCORES # Top 10: O(log N + 10)

Solution 2: Skip List (In-Memory)
public class RankTracker {
    private final SkipList<Double, TeamId> rankings;

    public void updateScore(String teamId, double newScore) {
        rankings.remove(oldScore, teamId);  // O(log N)
        rankings.insert(newScore, teamId);  // O(log N)
    }

    public int getRank(String teamId) {
        return rankings.rank(teamId);  // O(log N)
    }
}

Solution 3: Segment Tree (Advanced)
- Range queries: "Teams between rank 100-200"
- Bulk updates
- O(log N) for all operations

Comparison:
┌──────────────┬─────────┬──────────┬───────────┐
│ Solution     │ Update  │ GetRank  │ Top-K     │
├──────────────┼─────────┼──────────┼───────────┤
│ Naive Sort   │ O(1)    │ O(N logN)│ O(N logN) │
│ Redis ZSet   │ O(log N)│ O(log N) │ O(log N+K)│
│ Skip List    │ O(log N)│ O(log N) │ O(log N+K)│
│ Segment Tree │ O(log N)│ O(log N) │ O(log N+K)│
└──────────────┴─────────┴──────────┴───────────┘

Recommendation: Redis for simplicity + performance
```

---

### **Q11: How would you implement substitution feature?**

**Answer:**
```
Requirement: Replace player before match starts

Implementation:
public class SubstitutionService {
    public boolean substitutePlayer(String teamId,
                                   Player oldPlayer,
                                   Player newPlayer) {
        UserTeam team = teamService.getTeam(teamId);

        // 1. Check timing
        if (team.isLocked()) {
            return false;  // Too late!
        }

        // 2. Remove old player
        team.removePlayer(oldPlayer);

        // 3. Add new player
        team.addPlayer(newPlayer);

        // 4. Re-validate
        ValidationResult result = TeamValidator.validateTeam(team);
        if (!result.isValid()) {
            // Rollback
            team.removePlayer(newPlayer);
            team.addPlayer(oldPlayer);
            return false;
        }

        // 5. Persist change
        teamRepository.save(team);
        return true;
    }
}

Business Rules:
- Free substitutions before deadline
- Charged substitutions (₹10) after initial creation
- Max 5 substitutions per team
- Can't substitute after match starts (15 min before)

Edge Cases:
- Substitute captain → Need to select new captain
- Substitute last WK → Validation fails (need min 1 WK)
- Undo substitution → Keep history
```

---

### **Q12: How to implement private contests (friends only)?**

**Answer:**
```
Features:
- Create private contest with invite code
- Only friends can join
- Custom prize pool (shared entry fees)

Implementation:
public class Contest {
    private final String inviteCode; // null for public
    private final Set<String> allowedUsers; // null for public

    public Contest createPrivateContest(Set<String> friendIds) {
        String inviteCode = generateInviteCode(); // 6-char alphanumeric
        return new Contest(id, name, type, entryFee, prizePool,
                          inviteCode, friendIds);
    }

    @Override
    public boolean joinContest(UserTeam team) {
        // Check if private
        if (inviteCode != null) {
            if (!allowedUsers.contains(team.getUserId())) {
                throw new UnauthorizedException("Not in allowed users");
            }
        }

        // Normal join logic
        return super.joinContest(team);
    }
}

Invite Code Generation:
private String generateInviteCode() {
    // 6 chars = 36^6 = 2 billion combinations
    return RandomStringUtils.randomAlphanumeric(6).toUpperCase();
    // Example: "A3X9KL"
}

Sharing:
- Generate shareable link: app.dream11.com/join/A3X9KL
- Auto-join if user in allowed list
- QR code for easy sharing
```

---

### **Q13: How would you implement player statistics and form?**

**Answer:**
```
Feature: Show player recent performance

Model:
public class PlayerStats {
    private final String playerId;
    private final double averagePoints; // Last 5 matches
    private final double formIndex; // 0-100 (recent trend)
    private final List<MatchPerformance> recentMatches;

    public double getProjectedPoints() {
        // ML model or simple average
        return averagePoints * formIndex / 100.0;
    }
}

public class MatchPerformance {
    private final String matchId;
    private final double points;
    private final LocalDateTime date;
}

Usage in Team Selection:
// Show to user during team creation
PlayerStats stats = statsService.getStats(playerId);
System.out.println("Avg Points: " + stats.getAveragePoints());
System.out.println("Form: " + stats.getFormIndex() + "%");
System.out.println("Projected: " + stats.getProjectedPoints());

Helps users make informed decisions!

Calculation:
formIndex = (recentPoints / historicalAverage) * 100
- If recent performance > average → >100 (good form)
- If recent performance < average → <100 (poor form)

Example:
Historical average: 45 points
Last 3 matches: 60, 55, 58 (avg 57.7)
Form index: (57.7 / 45) * 100 = 128% ⭐ Excellent form!
```

---

## **⚠️ Known Limitations & Trade-offs**

### **1. No Persistence**
- **Current:** In-memory only
- **Impact:** Data lost on restart
- **Fix:** Add Repository pattern with database
  ```java
  public interface TeamRepository {
      void save(UserTeam team);
      UserTeam findById(String teamId);
  }
  ```

### **2. No Transaction Management**
- **Current:** If contest join fails, entry fee not refunded atomically
- **Impact:** Money can be deducted without joining contest
- **Fix:** Use database transactions
  ```java
  @Transactional
  public void joinContest(String userId, String contestId) {
      deductBalance(userId, entryFee);
      addTeamToContest(contestId, team);
  }
  ```

### **3. No Thread Safety**
- **Current:** Race condition if two users join contest simultaneously
- **Impact:** Could exceed max participants
- **Fix:** Synchronized or optimistic locking
  ```java
  public synchronized boolean joinContest(UserTeam team) {
      if (participants.size() < maxParticipants) {
          participants.add(team);
          return true;
      }
      return false;
  }
  ```

### **4. Points Shared Across Contests**
- **Current:** Player points global, not per-contest
- **Impact:** Can't have different scoring for different contests
- **Fix:** Store points in Team-Player association
  ```java
  class TeamPlayerPerformance {
      Player player;
      double pointsInThisContest;
  }
  ```

### **5. No Fraud Detection**
- **Current:** Users can create similar teams
- **Impact:** Can game the system with 100 nearly-identical teams
- **Fix:** Similarity detection + rate limiting
  ```java
  double similarity = calculateTeamSimilarity(team1, team2);
  if (similarity > 0.9 && sameUser) {
      flagForReview();
  }
  ```

---

## **📚 Key Takeaways**

**Design Patterns Used:**
- ✅ **Observer Pattern** - Real-time score updates (most important!)
- ✅ **Strategy Pattern** - Pluggable scoring rules
- ✅ **Validator Pattern** - Complex validation logic
- ✅ **Service Layer** - Business logic encapsulation

**SOLID Principles:**
- ✅ All 5 principles demonstrated

**Zeta-Specific Features:**
- ✅ Max 7 from one team validation
- ✅ Role-based requirements
- ✅ Real-time scoring engine
- ✅ Contest management with prizes

**Interview Focus Points:**
- Observer pattern for real-time updates (critical!)
- Validation logic (most questions here)
- Scalability (1M+ users, 10K events/sec)
- Prize distribution fairness
- Fraud prevention

**Production Considerations:**
- Redis for leaderboards
- Kafka for event streaming
- WebSockets for live updates
- Database for persistence
- Async processing for scale

---

## **🎓 What You Should Master**

Before interview, ensure you can:

1. ✅ **Explain Observer Pattern** - Why it's perfect for real-time scoring
2. ✅ **Code validation logic** - All 7 rules with examples
3. ✅ **Discuss scalability** - Handle 1M users, 10K events/sec
4. ✅ **Draw architecture diagram** - Show all components
5. ✅ **Implement captain multiplier** - 2x and 1.5x logic
6. ✅ **Add new sport** - Create FootballScoreCalculator
7. ✅ **Explain trade-offs** - Shared points vs per-contest points
8. ✅ **Handle edge cases** - Captain injured, contest cancelled
9. ✅ **Design leaderboard** - Redis Sorted Set approach
10. ✅ **Discuss fraud prevention** - Similarity detection

**Practice Exercises:**
1. Add salary cap validation (100 credits)
2. Implement player injury status
3. Add substitute player feature
4. Create FootballScoreCalculator
5. Add private contest with invite code

**Time to Master:** 4-5 hours

**Difficulty:** ⭐⭐⭐⭐ (Advanced - Real-world system)

**Interview Frequency:** ⭐⭐ (Medium - Common at sports-tech companies like Dream11, FanDuel)

---

## **🎯 Interview Pro Tips**

**Start Strong:**
- Clarify: "T20, ODI, or Test match?"
- Ask: "How many users and contests?"
- Discuss: "Public or private contests?"

**During Design:**
- Draw architecture diagram first
- Mention Observer pattern early
- Explain "max 7 from one team" validation clearly

**Common Follow-ups:**
1. "How to scale to 10M users?" → Redis + Kafka
2. "How to prevent fraud?" → Similarity detection
3. "What if captain gets injured?" → Auto-promotion
4. "How to handle refunds?" → Transaction management

**Red Flags (Avoid):**
- ❌ No validation logic
- ❌ Tight coupling between scoring and contests
- ❌ No mention of real-time updates
- ❌ Ignoring captain/vice-captain multipliers

**Green Flags (Show These):**
- ✅ Observer pattern for real-time
- ✅ Detailed validation with error messages
- ✅ Extensible scoring (Strategy pattern)
- ✅ Production considerations (Redis, Kafka)
- ✅ Handles edge cases (cancellation, refunds)

**Ace the Interview:** Focus on Observer pattern and validation logic - these are the most discussed!
