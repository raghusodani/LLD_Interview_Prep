# Design Snake and Food Game - Comprehensive Solution 🐍

## **Problem Statement**

Design a classic Snake game that:
- Snake moves continuously in real-time
- Grows when eating food
- Collision detection (walls, self)
- Different food types with different scores
- Score tracking system
- Support for both human and AI players
- Game state management (running, paused, game over)

---

## **🎯 Our Approach**

### **1. Core Requirements Analysis**

**Functional Requirements:**
- ✅ Snake movement in 4 directions (UP, DOWN, LEFT, RIGHT)
- ✅ Collision detection (walls, self-collision)
- ✅ Food generation at random positions
- ✅ Snake growth upon eating food
- ✅ Score calculation based on food type
- ✅ Game state management
- ✅ Support for different player types

**Non-Functional Requirements:**
- ✅ Real-time responsive gameplay
- ✅ Extensible for new food types
- ✅ Easy to add AI strategies
- ✅ Simple to add game variations

---

## **🏗️ Architecture & Design Patterns**

### **Pattern 1: Factory Pattern**

**Where:** Food Creation

**Why:**
- Multiple food types (Normal, Bonus)
- Each food has different point values
- Easy to add new food types
- Centralized food creation logic

**Implementation:**

```java
public abstract class FoodItem {
    protected Pair position;
    protected int points;

    public abstract int getPoints();
    public abstract String getType();
}

public class NormalFood extends FoodItem {
    public NormalFood(Pair position) {
        this.position = position;
        this.points = 10;
    }

    @Override
    public int getPoints() { return points; }

    @Override
    public String getType() { return "NORMAL"; }
}

public class BonusFood extends FoodItem {
    public BonusFood(Pair position) {
        this.position = position;
        this.points = 50;
    }

    @Override
    public int getPoints() { return points; }

    @Override
    public String getType() { return "BONUS"; }
}

public class FoodFactory {
    public static FoodItem createFood(String type, Pair position) {
        switch(type.toLowerCase()) {
            case "normal":
                return new NormalFood(position);
            case "bonus":
                return new BonusFood(position);
            default:
                return new NormalFood(position);
        }
    }

    // Random food generation
    public static FoodItem createRandomFood(GameBoard board) {
        Pair randomPos = board.getRandomEmptyPosition();
        // 80% normal, 20% bonus
        String type = (Math.random() < 0.8) ? "normal" : "bonus";
        return createFood(type, randomPos);
    }
}
```

**Benefits:**
- ✅ Open/Closed Principle - Add new food types without modifying factory
- ✅ Single Responsibility - Factory handles creation
- ✅ Easy to introduce probability-based food spawning

---

### **Pattern 2: Strategy Pattern**

**Where:** Player Movement

**Why:**
- Different input sources (Human keyboard, AI logic)
- Can add new player types (Network, Replay)
- Separates movement logic from game logic

**Implementation:**

```java
public interface MovementStrategy {
    String getNextMove(GameBoard board, Snake snake);
}

public class HumanMovementStrategy implements MovementStrategy {
    @Override
    public String getNextMove(GameBoard board, Snake snake) {
        // Read from keyboard/input device
        return readUserInput();
    }
}

public class AIMovementStrategy implements MovementStrategy {
    @Override
    public String getNextMove(GameBoard board, Snake snake) {
        // Simple AI: Move towards food
        Pair head = snake.getHead();
        Pair food = board.getFoodPosition();

        if (food.x < head.x) return "LEFT";
        if (food.x > head.x) return "RIGHT";
        if (food.y < head.y) return "UP";
        if (food.y > head.y) return "DOWN";

        return "RIGHT"; // Default
    }
}
```

**Benefits:**
- ✅ Easy to switch between human and AI
- ✅ Can add sophisticated AI (A*, pathfinding)
- ✅ Testable in isolation

---

### **Pattern 3: State Pattern (Implicit)**

**Where:** Game State Management

**Why:**
- Game has distinct states (RUNNING, PAUSED, GAME_OVER)
- Different behavior in each state
- Clean state transitions

**States:**
```
RUNNING:
  - Snake moves continuously
  - Process user input
  - Check collisions
  - Generate food

PAUSED:
  - Snake stops moving
  - Display pause menu
  - Wait for resume

GAME_OVER:
  - Display final score
  - Show restart option
  - No input processing
```

---

## **📐 Complete Architecture**

```
┌─────────────────────────────────────────────────────────┐
│                      SnakeGame                          │
│                     (Controller)                        │
│  - GameBoard board                                      │
│  - Snake snake                                          │
│  - FoodItem currentFood                                 │
│  - MovementStrategy playerStrategy                      │
│  - int score                                            │
│  - GameState state                                      │
└─────────────┬──────────────┬────────────────────────────┘
              │              │
     ┌────────┴────┐   ┌─────┴──────────┐
     │  GameBoard  │   │     Snake      │
     │             │   │  (LinkedList)  │
     │ - int size  │   │ - Deque<Pair>  │
     │ - cells[][] │   │ - Direction    │
     └─────────────┘   └────────────────┘
              │
     ┌────────┴────────────────────┐
     │                             │
┌────▼─────────┐         ┌────────▼─────────┐
│  FoodItem    │         │ MovementStrategy │
│  (Abstract)  │         │   (Interface)    │
└────┬─────────┘         └────────┬─────────┘
     │                            │
     ├─NormalFood                 ├─HumanMovementStrategy
     ├─BonusFood                  ├─AIMovementStrategy
     └─(Power-ups)                └─(Network, Replay)
```

---

## **🔑 Key Design Decisions**

### **Decision 1: Snake Representation**

**What:** Use Deque<Pair> to represent snake body

**Why:**
- Efficient head/tail operations O(1)
- Natural representation of snake segments
- Easy growth (add to head) and movement (remove tail)

```java
public class Snake {
    private Deque<Pair> body;
    private Direction currentDirection;

    public void move(Direction dir) {
        // Add new head
        Pair newHead = calculateNewHead(dir);
        body.addFirst(newHead);

        // Remove tail (unless growing)
        if (!isGrowing) {
            body.removeLast();
        }
    }

    public boolean isCollision(Pair position) {
        // O(n) check if position is in body
        return body.contains(position);
    }
}
```

**Interview Question:**
> "Why Deque instead of ArrayList?"

**Answer:**
> "Deque provides O(1) operations for both head (addFirst) and tail (removeLast), which are the primary operations in Snake movement. ArrayList would require O(n) for head insertion with shifting. Snake movement happens every frame, so O(1) is critical for performance."

---

### **Decision 2: Real-Time Movement**

**What:** Game loop with fixed time step

**Implementation:**
```java
public void gameLoop() {
    while (state == GameState.RUNNING) {
        long frameStart = System.currentTimeMillis();

        // Get next move
        String move = playerStrategy.getNextMove(board, snake);

        // Move snake
        snake.move(parseDirection(move));

        // Check collisions
        if (checkCollision()) {
            state = GameState.GAME_OVER;
            break;
        }

        // Check food consumption
        if (snake.getHead().equals(food.getPosition())) {
            score += food.getPoints();
            snake.grow();
            food = FoodFactory.createRandomFood(board);
        }

        // Sleep to maintain frame rate
        long elapsed = System.currentTimeMillis() - frameStart;
        Thread.sleep(Math.max(0, FRAME_DELAY - elapsed));
    }
}
```

**Why:**
- Consistent gameplay speed
- Predictable behavior across different hardware
- Easy to adjust difficulty by changing delay

**Interview Question:**
> "How to handle increasing speed as score increases?"

**Answer:**
```java
private int calculateFrameDelay() {
    int baseDelay = 200; // 200ms initial
    int reduction = score / 100 * 10; // Reduce 10ms per 100 points
    return Math.max(50, baseDelay - reduction); // Min 50ms (max speed)
}
```

---

### **Decision 3: Collision Detection**

**What:** Two-layer collision system

**Layer 1: Wall Collision**
```java
public boolean isWallCollision(Pair position) {
    return position.x < 0 || position.x >= boardSize ||
           position.y < 0 || position.y >= boardSize;
}
```

**Layer 2: Self-Collision**
```java
public boolean isSelfCollision(Pair head) {
    // Check if head collides with any body segment
    // Skip first element (it's the head we just added)
    return snake.body.stream()
                .skip(1)
                .anyMatch(segment -> segment.equals(head));
}
```

**Interview Question:**
> "How to implement board wrapping (Pac-Man style)?"

**Answer:**
```java
public Pair wrapPosition(Pair position) {
    int x = (position.x + boardSize) % boardSize;
    int y = (position.y + boardSize) % boardSize;
    return new Pair(x, y);
}

// In move logic:
Pair newHead = calculateNewHead(direction);
newHead = wrapPosition(newHead); // Wrap around edges
```

---

### **Decision 4: Food Placement**

**What:** Random placement avoiding snake body

**Implementation:**
```java
public Pair getRandomEmptyPosition() {
    List<Pair> emptyPositions = new ArrayList<>();

    for (int x = 0; x < boardSize; x++) {
        for (int y = 0; y < boardSize; y++) {
            Pair pos = new Pair(x, y);
            if (!snake.occupies(pos)) {
                emptyPositions.add(pos);
            }
        }
    }

    if (emptyPositions.isEmpty()) {
        return null; // Board full - win condition!
    }

    int randomIndex = (int)(Math.random() * emptyPositions.size());
    return emptyPositions.get(randomIndex);
}
```

**Trade-off:**
- O(n²) but only called once per food spawn
- Alternative: Keep set of empty positions (O(1) lookup, O(n) maintenance)

---

## **💡 SOLID Principles Applied**

### **S - Single Responsibility**
- `Snake` - Only manages snake state and movement
- `GameBoard` - Only manages board state
- `FoodFactory` - Only creates food
- `SnakeGame` - Only orchestrates game flow

### **O - Open/Closed**
- Add new food types without modifying factory
- Add new movement strategies without touching game logic
- Extend with power-ups without changing core

### **L - Liskov Substitution**
- Any `FoodItem` subclass can replace base class
- Any `MovementStrategy` implementation works interchangeably
- Polymorphism works correctly

### **I - Interface Segregation**
- `MovementStrategy` - Single method interface
- `FoodItem` - Minimal interface (position, points, type)
- No fat interfaces

### **D - Dependency Inversion**
- `SnakeGame` depends on `MovementStrategy` interface
- `SnakeGame` depends on `FoodItem` abstraction
- High-level doesn't depend on low-level implementations

---

## **🎭 Scenario Walkthrough**

### **Complete Game Flow**

```
Initial State:
┌─────────────────┐
│ . . . . . . . . │
│ . S → → . . . . │  S = Snake head
│ . . . . F . . . │  → = Snake body
│ . . . . . . . . │  F = Food
│ . . . . . . . . │  . = Empty
└─────────────────┘

Step 1: Move RIGHT
┌─────────────────┐
│ . . . . . . . . │
│ . . S → → . . . │  Head moves right
│ . . . . F . . . │  Tail removed
│ . . . . . . . . │
└─────────────────┘

Step 2: Move DOWN
┌─────────────────┐
│ . . . . . . . . │
│ . . . S → . . . │  Head moves down
│ . . . ↓ F . . . │  Body follows
│ . . . . . . . . │
└─────────────────┘

Step 3: Move RIGHT (eat food)
┌─────────────────┐
│ . . . . . . . . │
│ . . . . S . . . │  Head eats food
│ . . . ↓ → . . . │  Snake grows
│ . . . . . . F* . │  New food spawns
└─────────────────┘
Score: +10 points

Step 4: Move RIGHT (wall collision)
┌─────────────────┐
│ . . . . . . . . │
│ . . . . . S → X │  X = Collision!
│ . . . ↓ → → . . │  GAME OVER
│ . . . . . . F . │
└─────────────────┘
```

---

## **🚀 Extensions & Enhancements**

### **1. Power-Ups**

```java
public class PowerUpFood extends FoodItem {
    private PowerUpType type;

    enum PowerUpType {
        SPEED_BOOST,    // Temporary speed increase
        INVINCIBILITY,  // No collision for 5 seconds
        SCORE_MULTIPLIER, // 2x points for 10 seconds
        SHRINK          // Reduce snake length by 1
    }

    @Override
    public void applyEffect(SnakeGame game) {
        switch(type) {
            case SPEED_BOOST:
                game.setSpeedMultiplier(1.5, 5000); // 1.5x for 5 sec
                break;
            case INVINCIBILITY:
                game.setInvincible(true, 5000);
                break;
            // ... other effects
        }
    }
}
```

---

### **2. Obstacles**

```java
public class Obstacle {
    private Pair position;
    private boolean isDestructible;

    public boolean checkCollision(Pair snakeHead) {
        return position.equals(snakeHead);
    }
}

// In game logic:
if (obstacles.stream().anyMatch(obs -> obs.checkCollision(head))) {
    if (hasInvincibility) {
        // Destroy destructible obstacles
        obstacles.removeIf(obs ->
            obs.isDestructible() && obs.checkCollision(head));
    } else {
        gameOver();
    }
}
```

---

### **3. Multiplayer Snakes**

```java
public class MultiplayerSnakeGame extends SnakeGame {
    private List<Snake> snakes;
    private Map<Snake, Integer> scores;

    @Override
    public void update() {
        for (Snake snake : snakes) {
            snake.move();

            // Check collision with other snakes
            for (Snake other : snakes) {
                if (snake != other && snake.collidesWith(other)) {
                    // Handle collision (game over or point loss)
                    handleCollision(snake, other);
                }
            }
        }
    }
}
```

---

### **4. Advanced AI with A***

```java
public class AStarAIStrategy implements MovementStrategy {
    @Override
    public String getNextMove(GameBoard board, Snake snake) {
        Pair head = snake.getHead();
        Pair food = board.getFoodPosition();

        // A* pathfinding
        List<Pair> path = findPath(head, food, board, snake);

        if (path.isEmpty()) {
            // No path to food - survive mode
            return findSafestMove(board, snake);
        }

        Pair nextStep = path.get(0);
        return getDirection(head, nextStep);
    }

    private List<Pair> findPath(Pair start, Pair goal,
                                  GameBoard board, Snake snake) {
        // A* implementation
        PriorityQueue<Node> open = new PriorityQueue<>();
        Set<Pair> closed = new HashSet<>();

        // ... A* algorithm
        return reconstructPath(goal);
    }
}
```

---

## **🎯 Interview Questions & Answers**

### **Q1: How to implement board wrapping (Pac-Man style)?**

**Answer:**
```java
public class WrappingBoard extends GameBoard {
    @Override
    public Pair normalizePosition(Pair position) {
        // Modulo arithmetic for wrapping
        int x = ((position.x % boardSize) + boardSize) % boardSize;
        int y = ((position.y % boardSize) + boardSize) % boardSize;
        return new Pair(x, y);
    }

    @Override
    public boolean isWallCollision(Pair position) {
        return false; // No walls in wrapping mode!
    }
}
```

**Key Points:**
- Use modulo to wrap coordinates
- Positive modulo handles negative values correctly
- Disable wall collision check
- Add visual indicator for wrap tunnels

---

### **Q2: How to handle increasing speed difficulty?**

**Answer:**
```java
public class DynamicSpeedGame extends SnakeGame {
    private static final int BASE_DELAY = 200;
    private static final int MIN_DELAY = 50;
    private static final int SPEED_INCREASE_INTERVAL = 5; // Every 5 foods

    @Override
    protected int getFrameDelay() {
        int foodsEaten = score / FOOD_POINTS;
        int speedLevel = foodsEaten / SPEED_INCREASE_INTERVAL;
        int reduction = speedLevel * 20; // 20ms faster per level

        return Math.max(MIN_DELAY, BASE_DELAY - reduction);
    }
}
```

**Variants:**
- **Linear:** Speed increases uniformly
- **Exponential:** Speed doubles every N points
- **Score-based:** Faster at higher scores
- **Time-based:** Gets faster over time

---

### **Q3: How to add obstacles to the game?**

**Answer:**
```java
public class ObstacleGame extends SnakeGame {
    private List<Obstacle> obstacles;

    public void generateObstacles(int count) {
        obstacles = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            Pair pos = board.getRandomEmptyPosition();
            obstacles.add(new Obstacle(pos));
        }
    }

    @Override
    protected boolean checkCollision(Pair position) {
        // Check wall collision
        if (super.checkCollision(position)) return true;

        // Check obstacle collision
        return obstacles.stream()
            .anyMatch(obs -> obs.getPosition().equals(position));
    }

    @Override
    protected void render() {
        super.render();
        // Render obstacles
        for (Obstacle obs : obstacles) {
            board.setCellType(obs.getPosition(), CellType.OBSTACLE);
        }
    }
}
```

**Extensions:**
- Moving obstacles
- Destructible obstacles
- Teleportation portals
- Maze generation

---

### **Q4: How to implement multiplayer with multiple snakes?**

**Answer:**
```java
public class MultiplayerSnakeGame {
    private List<Player> players;

    static class Player {
        Snake snake;
        MovementStrategy strategy;
        int score;
        boolean isAlive;
    }

    public void update() {
        // Move all snakes
        for (Player player : players) {
            if (!player.isAlive) continue;

            String move = player.strategy.getNextMove(board, player.snake);
            player.snake.move(parseDirection(move));
        }

        // Check all collisions
        for (Player player : players) {
            if (!player.isAlive) continue;

            Pair head = player.snake.getHead();

            // Self collision
            if (player.snake.isSelfCollision(head)) {
                player.isAlive = false;
                continue;
            }

            // Collision with other snakes
            for (Player other : players) {
                if (player == other) continue;

                if (other.snake.occupies(head)) {
                    player.isAlive = false;
                    other.score += 50; // Kill bonus
                    break;
                }
            }
        }

        // Check if only one player remains
        long aliveCount = players.stream()
            .filter(p -> p.isAlive)
            .count();

        if (aliveCount <= 1) {
            gameOver();
        }
    }
}
```

**Considerations:**
- Head-to-head collision (both die)
- Scoring system (kills vs food)
- Team modes
- Network synchronization

---

### **Q5: How to implement undo/replay functionality?**

**Answer:**
```java
public class ReplayableGame extends SnakeGame {
    private Deque<GameSnapshot> history;

    static class GameSnapshot {
        Deque<Pair> snakeBody;
        Pair foodPosition;
        int score;
        Direction direction;

        public GameSnapshot(SnakeGame game) {
            this.snakeBody = new ArrayDeque<>(game.snake.getBody());
            this.foodPosition = game.food.getPosition();
            this.score = game.score;
            this.direction = game.snake.getDirection();
        }

        public void restore(SnakeGame game) {
            game.snake.setBody(new ArrayDeque<>(snakeBody));
            game.food.setPosition(foodPosition);
            game.score = score;
            game.snake.setDirection(direction);
        }
    }

    @Override
    public void update() {
        // Save state before move
        history.push(new GameSnapshot(this));

        // Limit history size
        if (history.size() > MAX_HISTORY) {
            history.removeLast();
        }

        super.update();
    }

    public void undo() {
        if (!history.isEmpty()) {
            GameSnapshot snapshot = history.pop();
            snapshot.restore(this);
        }
    }
}
```

**For replay:**
```java
public class GameRecorder {
    private List<String> moves;

    public void recordMove(String direction) {
        moves.add(direction);
    }

    public void playback(SnakeGame game) {
        for (String move : moves) {
            game.processInput(move);
            game.update();
            Thread.sleep(game.getFrameDelay());
        }
    }
}
```

---

### **Q6: How to handle thread safety for concurrent access?**

**Answer:**
```java
public class ThreadSafeSnakeGame extends SnakeGame {
    private final ReentrantLock gameLock = new ReentrantLock();

    @Override
    public void move(Direction direction) {
        gameLock.lock();
        try {
            super.move(direction);
        } finally {
            gameLock.unlock();
        }
    }

    @Override
    public GameState getGameState() {
        gameLock.lock();
        try {
            return super.getGameState();
        } finally {
            gameLock.unlock();
        }
    }
}
```

**Considerations:**
- Separate rendering thread from game logic
- Use synchronized methods for state access
- Consider lock-free data structures for high performance
- Use volatile for state flags

---

### **Q7: How to test Snake game logic?**

**Answer:**
```java
@Test
public void testSnakeGrowsWhenEatingFood() {
    // Arrange
    SnakeGame game = new SnakeGame(10);
    int initialLength = game.getSnake().getLength();

    // Place food at known position
    Pair foodPos = new Pair(5, 5);
    game.setFood(new NormalFood(foodPos));

    // Move snake to food
    game.getSnake().setHead(new Pair(4, 5));

    // Act
    game.move(Direction.RIGHT);

    // Assert
    assertEquals(initialLength + 1, game.getSnake().getLength());
    assertEquals(10, game.getScore()); // Normal food = 10 points
}

@Test
public void testGameOverOnSelfCollision() {
    // Arrange
    SnakeGame game = new SnakeGame(10);

    // Create snake that will collide with itself
    // Shape: →→
    //        ↓
    Deque<Pair> body = new ArrayDeque<>();
    body.add(new Pair(2, 0)); // Head
    body.add(new Pair(1, 0));
    body.add(new Pair(1, 1));
    game.getSnake().setBody(body);

    // Act - move left to collide with body
    game.move(Direction.LEFT);

    // Assert
    assertEquals(GameState.GAME_OVER, game.getState());
}
```

**Test Categories:**
- Unit tests: Individual methods
- Integration tests: Full game flow
- Edge cases: Boundary conditions
- Performance tests: Large boards, long snakes

---

## **⚠️ Known Limitations & Trade-offs**

1. **No Persistence**
   - Current: Game state lost on exit
   - Fix: Serialize game state to file/database

2. **Single Food at a Time**
   - Current: Only one food spawns
   - Fix: Support multiple food items simultaneously

3. **Simple Collision Detection**
   - Current: O(n) for self-collision check
   - Fix: Use HashSet for O(1) lookup

4. **No Network Support**
   - Current: Local multiplayer only
   - Fix: Add network layer with synchronization

5. **Basic AI**
   - Current: Simple greedy algorithm
   - Fix: Implement A* or Hamiltonian cycle algorithm

---

## **📚 Key Takeaways**

**Design Patterns Used:**
- ✅ Factory Pattern (Food creation)
- ✅ Strategy Pattern (Movement strategies)
- ✅ State Pattern (Game states - implicit)

**SOLID Principles:**
- ✅ All 5 principles demonstrated

**Extensibility:**
- ✅ Easy to add new food types
- ✅ Easy to add new player types (AI, Network)
- ✅ Easy to add game variations (wrapping, obstacles)

**Interview Focus Points:**
- Real-time game loop implementation
- Collision detection algorithms
- Board wrapping mechanics
- Difficulty progression (speed increase)
- Multiplayer coordination
- Thread safety considerations

---

## **🎓 What You Should Master**

Before interview, ensure you can:
1. ✅ Explain Factory Pattern benefits for food types
2. ✅ Implement basic collision detection (wall + self)
3. ✅ Add board wrapping in 2 minutes
4. ✅ Discuss speed increase strategies
5. ✅ Add obstacles extension
6. ✅ Implement simple AI (greedy towards food)
7. ✅ Explain thread safety concerns
8. ✅ Draw architecture diagram from memory
9. ✅ Code the game loop structure
10. ✅ Handle edge cases (board full, no valid moves)

**Practice Exercises:**
1. Implement board wrapping
2. Add power-up food (speed boost)
3. Create simple AI player
4. Add difficulty levels
5. Implement replay functionality

**Time to master:** 2-3 hours of practice

**Difficulty:** ⭐⭐ (Easy-Medium)

**Interview Frequency:** ⭐⭐⭐ (Medium - Good for demonstrating patterns)

---

## **💡 Pro Tips for Interview**

### **Common Follow-ups:**
1. "How would you optimize for a very large board?"
   - Use spatial hashing
   - Only render visible portion
   - Lazy evaluation of empty positions

2. "How to make the AI unbeatable?"
   - Hamiltonian cycle algorithm
   - Fill entire board without collision

3. "How to add different difficulty levels?"
   - Vary initial speed
   - Change AI intelligence
   - Add more obstacles

### **Red Flags to Avoid:**
- ❌ Using global variables
- ❌ No separation of concerns
- ❌ Hard-coded magic numbers
- ❌ No consideration for extensibility

### **Green Flags:**
- ✅ Clean separation of game logic and rendering
- ✅ Proper use of design patterns
- ✅ Discussion of trade-offs
- ✅ Consideration of edge cases
- ✅ Performance optimization ideas

**This is a great starter problem** for demonstrating OOP concepts and design pattern knowledge in a fun, relatable context! 🐍✨
