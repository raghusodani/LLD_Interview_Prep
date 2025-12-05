# Design Tic Tac Toe - Comprehensive Solution ⭕❌

## **Problem Statement**

Design a Tic Tac Toe game system that:
- Supports two players (X and O)
- 3×3 board with alternating turns
- Win detection (3 in a row/column/diagonal)
- Draw detection (board full, no winner)
- Extensible for different player types (Human, AI)
- Clean state management without if-else soup

---

## **🎯 Our Approach**

### **1. Core Requirements Analysis**

**Functional Requirements:**
- ✅ Initialize 3×3 board
- ✅ Alternate turns between players
- ✅ Place symbol at valid position
- ✅ Detect win (row, column, diagonal)
- ✅ Detect draw (board full)
- ✅ Display board state
- ✅ Handle invalid moves

**Non-Functional Requirements:**
- ✅ Extensible for AI players
- ✅ Clean state transitions
- ✅ Scalable to N×N board
- ✅ Testable components

---

## **🏗️ Architecture & Design Patterns**

### **Pattern 1: State Pattern** 🔄

**Where:** Game state management

**The Problem:**
Without State Pattern, game logic becomes if-else soup:

```java
// ❌ BAD: Nightmare if-else code
public void makeMove(Player player, Position pos) {
    if (currentState == "X_TURN") {
        if (isValidMove(pos)) {
            placeSymbol(X, pos);
            if (checkWin(X)) {
                currentState = "X_WON";
                System.out.println("X wins!");
            } else if (isBoardFull()) {
                currentState = "DRAW";
                System.out.println("Draw!");
            } else {
                currentState = "O_TURN";
            }
        }
    } else if (currentState == "O_TURN") {
        // Duplicate logic for O...
    } else if (currentState == "X_WON") {
        System.out.println("Game over!");
    } else if (currentState == "O_WON") {
        System.out.println("Game over!");
    }
    // Gets worse with more states!
}
```

**The Solution: State Pattern**

```java
// ✅ GOOD: Clean state pattern
public interface GameState {
    void next(GameContext context, Player player, boolean hasWon);
    boolean isGameOver();
}

public class XTurnState implements GameState {
    @Override
    public void next(GameContext context, Player player, boolean hasWon) {
        if (hasWon) {
            context.setState(new XWonState());
        } else if (context.getBoard().isFull()) {
            context.setState(new DrawState());
        } else {
            context.setState(new OTurnState());
        }
    }

    @Override
    public boolean isGameOver() { return false; }
}

public class XWonState implements GameState {
    @Override
    public void next(GameContext context, Player player, boolean hasWon) {
        System.out.println("Game already over! X won.");
    }

    @Override
    public boolean isGameOver() { return true; }
}

// Similar: OTurnState, OWonState, DrawState
```

**Benefits:**
- ✅ Each state is a separate class with its own logic
- ✅ Adding new states = new class, no modification
- ✅ State transitions are explicit and clear
- ✅ Testable in isolation

**State Diagram:**

```
         ┌─────────┐
         │  START  │
         └────┬────┘
              │
        ┌─────▼─────┐
        │  XTurnState │◄────┐
        └─────┬─────┘      │
              │            │
         Make Move         │
              │            │
        ┌─────▼─────┐      │
        │  OTurnState │────┘
        └─────┬─────┘
              │
         Win/Draw?
              │
        ┌─────┴─────┐
        │           │
   ┌────▼────┐ ┌───▼────┐
   │ XWonState│ │OWonState│
   └─────────┘ └────────┘
        │           │
        └─────┬─────┘
              │
        ┌─────▼─────┐
        │ DrawState  │
        └───────────┘
```

---

### **Pattern 2: Strategy Pattern** 🎮

**Where:** Player move logic

**Why:**
- Different player types (Human, AI)
- Same interface, different implementations
- Easy to add new player types

**Implementation:**

```java
// Strategy interface
public interface PlayerStrategy {
    Position makeMove(Board board);
}

// Human player - gets input from user
public class HumanPlayerStrategy implements PlayerStrategy {
    private Scanner scanner;

    @Override
    public Position makeMove(Board board) {
        System.out.print("Enter row (0-2): ");
        int row = scanner.nextInt();
        System.out.print("Enter col (0-2): ");
        int col = scanner.nextInt();
        return new Position(row, col);
    }
}

// AI player - uses algorithm (future)
public class AIPlayerStrategy implements PlayerStrategy {
    @Override
    public Position makeMove(Board board) {
        // Minimax algorithm
        return findBestMove(board);
    }
}

// Random player - for testing
public class RandomPlayerStrategy implements PlayerStrategy {
    private Random random = new Random();

    @Override
    public Position makeMove(Board board) {
        List<Position> available = board.getAvailablePositions();
        return available.get(random.nextInt(available.size()));
    }
}
```

**Usage:**

```java
public class Player {
    private Symbol symbol;
    private PlayerStrategy strategy;

    public Position getMove(Board board) {
        return strategy.makeMove(board);
    }
}

// Create players
Player playerX = new Player(Symbol.X, new HumanPlayerStrategy());
Player playerO = new Player(Symbol.O, new AIPlayerStrategy());
```

**Benefits:**
- ✅ Player logic separated from game logic
- ✅ Easy to test different strategies
- ✅ Can mix player types (Human vs AI, AI vs AI)
- ✅ New strategy = new class

---

### **Pattern 3: Context Object Pattern**

**Where:** GameContext holds state and board

**Why:**
- State needs access to board and players
- Encapsulates game state management
- Clean interface for state transitions

**Implementation:**

```java
public class GameContext {
    private GameState currentState;
    private Board board;
    private Player[] players;

    public GameContext(Board board, Player[] players) {
        this.board = board;
        this.players = players;
        this.currentState = new XTurnState(); // Initial state
    }

    public void setState(GameState state) {
        this.currentState = state;
    }

    public void makeMove(Player player, Position pos) {
        if (!currentState.isGameOver()) {
            boolean placed = board.placeSymbol(player.getSymbol(), pos);
            if (placed) {
                boolean hasWon = checkWin(player.getSymbol());
                currentState.next(this, player, hasWon);
            }
        }
    }

    public boolean isGameOver() {
        return currentState.isGameOver();
    }
}
```

---

## **🎲 Win Detection Algorithm**

### **Efficient O(1) Win Check**

Instead of checking entire board every move, track counts:

```java
public class Board {
    private Symbol[][] grid;
    private int[] rowCounts;      // Count per row
    private int[] colCounts;      // Count per column
    private int diagCount;        // Main diagonal count
    private int antiDiagCount;    // Anti-diagonal count
    private int size;

    public boolean placeSymbol(Symbol symbol, Position pos) {
        int row = pos.getRow();
        int col = pos.getCol();

        if (grid[row][col] != Symbol.EMPTY) {
            return false; // Invalid move
        }

        grid[row][col] = symbol;

        // Update counts (use +1 for X, -1 for O)
        int value = (symbol == Symbol.X) ? 1 : -1;

        rowCounts[row] += value;
        colCounts[col] += value;

        if (row == col) {
            diagCount += value;
        }
        if (row + col == size - 1) {
            antiDiagCount += value;
        }

        return true;
    }

    public boolean hasWon(Symbol symbol) {
        int target = (symbol == Symbol.X) ? size : -size;

        // Check rows and columns
        for (int i = 0; i < size; i++) {
            if (rowCounts[i] == target || colCounts[i] == target) {
                return true;
            }
        }

        // Check diagonals
        if (diagCount == target || antiDiagCount == target) {
            return true;
        }

        return false;
    }
}
```

**Time Complexity:**
- Place symbol: O(1)
- Check win: O(n) where n = board size
- Total per move: O(n)

**Alternative: O(1) Win Check**

Track only the move just played:

```java
public boolean hasWonAt(Symbol symbol, Position pos) {
    int row = pos.getRow();
    int col = pos.getCol();

    // Check row
    if (checkLine(symbol, row, 0, 0, 1)) return true;

    // Check column
    if (checkLine(symbol, 0, col, 1, 0)) return true;

    // Check main diagonal (if applicable)
    if (row == col && checkLine(symbol, 0, 0, 1, 1)) return true;

    // Check anti-diagonal (if applicable)
    if (row + col == size - 1 && checkLine(symbol, 0, size-1, 1, -1)) return true;

    return false;
}

private boolean checkLine(Symbol symbol, int startRow, int startCol,
                          int deltaRow, int deltaCol) {
    int count = 0;
    int row = startRow;
    int col = startCol;

    for (int i = 0; i < size; i++) {
        if (grid[row][col] == symbol) {
            count++;
        }
        row += deltaRow;
        col += deltaCol;
    }

    return count == size;
}
```

**Best Approach:** O(1) check only relevant lines for the last move!

---

## **📐 Class Diagram Overview**

```
┌──────────────────┐
│   GameContext    │ (holds state & board)
└────────┬─────────┘
         │
    ┌────┴─────────┐
    │              │
┌───▼──────┐  ┌───▼─────┐
│GameState │  │  Board  │
│Interface │  └─────────┘
└───┬──────┘
    │
    ├─ XTurnState
    ├─ OTurnState
    ├─ XWonState
    ├─ OWonState
    └─ DrawState

┌──────────────────┐
│  PlayerStrategy  │ (Interface)
└────────┬─────────┘
         │
         ├─ HumanPlayerStrategy
         ├─ AIPlayerStrategy
         └─ RandomPlayerStrategy

┌──────────────────┐
│     Player       │ (uses strategy)
└──────────────────┘
```

---

## **🔑 Key Design Decisions**

### **Decision 1: State Pattern vs Enum + Switch**

**Interview Question:**
> "Why use State Pattern? Isn't enum + switch simpler?"

**Answer:**
```java
// ❌ Enum + switch still has problems:
public enum GamePhase { X_TURN, O_TURN, X_WON, O_WON, DRAW }

public void makeMove(Position pos) {
    switch(currentPhase) {
        case X_TURN:
            // 20 lines of logic
            break;
        case O_TURN:
            // 20 lines of logic (duplicate!)
            break;
        case X_WON:
        case O_WON:
        case DRAW:
            System.out.println("Game over!");
            break;
    }
}

// Problems:
// 1. All logic in one method (100+ lines)
// 2. Duplicate code for X_TURN and O_TURN
// 3. Hard to test individual states
// 4. Violates Open/Closed Principle
```

**State Pattern Wins:**
- Each state is independently testable
- No code duplication
- Easy to add new states (Paused, Replay, etc.)
- Follows Open/Closed Principle

---

### **Decision 2: Strategy Pattern for Players**

**Interview Question:**
> "Why separate PlayerStrategy? Could just have Player class with if-else."

**Answer:**
```java
// ❌ Without Strategy:
public class Player {
    private PlayerType type; // HUMAN or AI

    public Position getMove(Board board) {
        if (type == PlayerType.HUMAN) {
            // Get input from user
        } else if (type == PlayerType.AI) {
            // Run minimax
        }
        // Adding new type = modifying this method!
    }
}

// ✅ With Strategy:
public class Player {
    private PlayerStrategy strategy;

    public Position getMove(Board board) {
        return strategy.makeMove(board);
    }
}
// Adding new type = new class, no modification!
// Can even change strategy at runtime!
```

---

### **Decision 3: Separate GameContext from Game Logic**

**Why:**
- GameContext manages state transitions
- TicTacToeGame handles game loop
- Clean separation of concerns

```java
public class TicTacToeGame {
    private GameContext context;

    public void play() {
        while (!context.isGameOver()) {
            Player current = getCurrentPlayer();
            Position move = current.getMove(context.getBoard());
            context.makeMove(current, move);
            displayBoard();
        }
        displayResult();
    }
}
```

---

## **💡 SOLID Principles Applied**

### **S - Single Responsibility**
- `GameState` - Only manages state transitions
- `PlayerStrategy` - Only decides move
- `Board` - Only manages grid and win detection
- `Player` - Only represents player with symbol

### **O - Open/Closed**
- Adding new state: Create new `GameState` class
- Adding new player type: Create new `PlayerStrategy` class
- No modification to existing code

### **L - Liskov Substitution**
- Any `GameState` implementation works in `GameContext`
- Any `PlayerStrategy` implementation works in `Player`

### **I - Interface Segregation**
- `GameState` has only 2 methods: `next()` and `isGameOver()`
- `PlayerStrategy` has only 1 method: `makeMove()`
- Minimal interfaces, no unnecessary methods

### **D - Dependency Inversion**
- `GameContext` depends on `GameState` interface
- `Player` depends on `PlayerStrategy` interface
- High-level modules don't depend on concrete implementations

---

## **🎭 Scenario Walkthrough**

### **Complete Game Flow:**

```
1. Initialize game
   ├─ Create 3×3 board
   ├─ Create Player X (Human)
   ├─ Create Player O (Human)
   └─ GameContext starts in XTurnState

2. Turn 1: X plays (0,0)
   ├─ GameContext.makeMove(playerX, (0,0))
   ├─ Board places X at (0,0)
   ├─ Check win: No
   ├─ XTurnState.next() → Transition to OTurnState
   └─ Display board

3. Turn 2: O plays (1,1)
   ├─ GameContext.makeMove(playerO, (1,1))
   ├─ Board places O at (1,1)
   ├─ Check win: No
   ├─ OTurnState.next() → Transition to XTurnState
   └─ Display board

4. ... (more turns)

5. Turn 5: X plays (0,2) - Winning move!
   ├─ GameContext.makeMove(playerX, (0,2))
   ├─ Board places X at (0,2)
   ├─ Check win: YES! (top row: X-X-X)
   ├─ XTurnState.next() → Transition to XWonState
   ├─ Display "X wins!"
   └─ Game loop exits (isGameOver() = true)
```

**Board States:**

```
Turn 1:        Turn 2:        Turn 5:
X| | |         X| | |         X|O|X
-----          -----          -----
 | | |          |O| |         O|O|
-----          -----          -----
 | | |          | | |          |X|
```

---

## **🚀 Extensions & Enhancements**

### **1. N×N Board**

```java
public class Board {
    private int size; // Make configurable!

    public Board(int size) {
        this.size = size;
        this.grid = new Symbol[size][size];
        this.rowCounts = new int[size];
        this.colCounts = new int[size];
        // Initialize all to EMPTY
    }

    // Win detection scales with size
    public boolean hasWon(Symbol symbol) {
        int target = (symbol == Symbol.X) ? size : -size;
        // ... same logic
    }
}

// Create 5×5 board
Board board = new Board(5);
```

### **2. AI Player with Minimax**

```java
public class MinimaxAIStrategy implements PlayerStrategy {

    @Override
    public Position makeMove(Board board) {
        Position bestMove = null;
        int bestScore = Integer.MIN_VALUE;

        for (Position pos : board.getAvailablePositions()) {
            board.placeSymbol(mySymbol, pos);
            int score = minimax(board, false, 0);
            board.undo(pos);

            if (score > bestScore) {
                bestScore = score;
                bestMove = pos;
            }
        }

        return bestMove;
    }

    private int minimax(Board board, boolean isMaximizing, int depth) {
        // Terminal conditions
        if (board.hasWon(mySymbol)) return 10 - depth;
        if (board.hasWon(opponentSymbol)) return depth - 10;
        if (board.isFull()) return 0;

        if (isMaximizing) {
            int maxScore = Integer.MIN_VALUE;
            for (Position pos : board.getAvailablePositions()) {
                board.placeSymbol(mySymbol, pos);
                int score = minimax(board, false, depth + 1);
                board.undo(pos);
                maxScore = Math.max(maxScore, score);
            }
            return maxScore;
        } else {
            int minScore = Integer.MAX_VALUE;
            for (Position pos : board.getAvailablePositions()) {
                board.placeSymbol(opponentSymbol, pos);
                int score = minimax(board, true, depth + 1);
                board.undo(pos);
                minScore = Math.min(minScore, score);
            }
            return minScore;
        }
    }
}
```

**Time Complexity:** O(9!) for 3×3 ≈ 362,880 positions (manageable!)

**With Alpha-Beta Pruning:** O(b^(d/2)) ≈ 2,654 positions (much faster!)

### **3. Undo Functionality**

```java
public class Board {
    private Stack<Move> moveHistory;

    public boolean placeSymbol(Symbol symbol, Position pos) {
        // ... place symbol
        moveHistory.push(new Move(symbol, pos));
        return true;
    }

    public void undo() {
        if (!moveHistory.isEmpty()) {
            Move lastMove = moveHistory.pop();
            grid[lastMove.pos.row][lastMove.pos.col] = Symbol.EMPTY;

            // Revert counts
            int value = (lastMove.symbol == Symbol.X) ? -1 : 1;
            rowCounts[lastMove.pos.row] += value;
            colCounts[lastMove.pos.col] += value;
            // ... revert diagonals too
        }
    }
}

// Add UndoState
public class UndoCommand {
    private GameContext context;

    public void execute() {
        context.getBoard().undo();
        // Also need to undo state transition!
        context.setState(getPreviousState());
    }
}
```

### **4. Multiplayer Support (>2 Players)**

```java
// Generalize for N players
public class GameContext {
    private List<Player> players;
    private int currentPlayerIndex;

    public Player getCurrentPlayer() {
        return players.get(currentPlayerIndex);
    }

    public void nextPlayer() {
        currentPlayerIndex = (currentPlayerIndex + 1) % players.size();
    }
}

// States become more generic
public class PlayerTurnState implements GameState {
    private int playerIndex;

    @Override
    public void next(GameContext context, Player player, boolean hasWon) {
        if (hasWon) {
            context.setState(new PlayerWonState(playerIndex));
        } else {
            int nextIndex = (playerIndex + 1) % context.getPlayers().size();
            context.setState(new PlayerTurnState(nextIndex));
        }
    }
}
```

---

## **🎯 Interview Questions & Answers**

### **Q1: How would you extend to N×N board with M-in-a-row to win?**

**Answer:**
```
Make size and winCondition configurable:

public class Board {
    private int size;           // Board size (N)
    private int winCondition;   // M symbols in a row to win

    public Board(int size, int winCondition) {
        if (winCondition > size) {
            throw new IllegalArgumentException();
        }
        this.size = size;
        this.winCondition = winCondition;
    }

    // Win detection needs to check windows of size M
    public boolean hasWon(Symbol symbol, Position lastMove) {
        return checkRow(symbol, lastMove) ||
               checkCol(symbol, lastMove) ||
               checkDiag(symbol, lastMove) ||
               checkAntiDiag(symbol, lastMove);
    }

    private boolean checkRow(Symbol symbol, Position pos) {
        int count = 1; // Count the symbol just placed

        // Check left
        int col = pos.col - 1;
        while (col >= 0 && grid[pos.row][col] == symbol) {
            count++;
            col--;
        }

        // Check right
        col = pos.col + 1;
        while (col < size && grid[pos.row][col] == symbol) {
            count++;
            col++;
        }

        return count >= winCondition;
    }
    // Similar for column and diagonals
}

// Create 5×5 with 4-in-a-row to win
Board board = new Board(5, 4);
```

**Time Complexity:** O(n) per move where n = board size

---

### **Q2: How to implement AI player with different difficulty levels?**

**Answer:**
```
Use Strategy Pattern with different AI implementations:

// Easy: Random moves
public class EasyAIStrategy implements PlayerStrategy {
    public Position makeMove(Board board) {
        List<Position> available = board.getAvailablePositions();
        return available.get(random.nextInt(available.size()));
    }
}

// Medium: Block opponent + take winning move
public class MediumAIStrategy implements PlayerStrategy {
    public Position makeMove(Board board) {
        // 1. Check if AI can win in next move
        Position winMove = findWinningMove(board, mySymbol);
        if (winMove != null) return winMove;

        // 2. Check if need to block opponent
        Position blockMove = findWinningMove(board, opponentSymbol);
        if (blockMove != null) return blockMove;

        // 3. Take center if available
        if (board.isEmpty(center)) return center;

        // 4. Take corner
        for (Position corner : corners) {
            if (board.isEmpty(corner)) return corner;
        }

        // 5. Random
        return getRandomAvailable(board);
    }
}

// Hard: Full Minimax with alpha-beta pruning
public class HardAIStrategy implements PlayerStrategy {
    public Position makeMove(Board board) {
        return minimaxWithAlphaBeta(board, Integer.MIN_VALUE, Integer.MAX_VALUE);
    }
}

// Usage:
Player easy = new Player(Symbol.O, new EasyAIStrategy());
Player medium = new Player(Symbol.O, new MediumAIStrategy());
Player hard = new Player(Symbol.O, new HardAIStrategy());
```

---

### **Q3: How would you implement undo/redo functionality?**

**Answer:**
```
Use Command Pattern + Memento Pattern:

// Command interface
public interface GameCommand {
    void execute();
    void undo();
}

// Move command
public class MoveCommand implements GameCommand {
    private GameContext context;
    private Player player;
    private Position position;
    private GameState previousState;

    @Override
    public void execute() {
        previousState = context.getCurrentState();
        context.makeMove(player, position);
    }

    @Override
    public void undo() {
        context.getBoard().removeSymbol(position);
        context.setState(previousState);
    }
}

// Command manager with history
public class GameHistory {
    private Stack<GameCommand> undoStack;
    private Stack<GameCommand> redoStack;

    public void executeCommand(GameCommand command) {
        command.execute();
        undoStack.push(command);
        redoStack.clear(); // Clear redo on new move
    }

    public void undo() {
        if (!undoStack.isEmpty()) {
            GameCommand command = undoStack.pop();
            command.undo();
            redoStack.push(command);
        }
    }

    public void redo() {
        if (!redoStack.isEmpty()) {
            GameCommand command = redoStack.pop();
            command.execute();
            undoStack.push(command);
        }
    }
}
```

---

### **Q4: How to support multiplayer (>2 players)?**

**Answer:**
```
Generalize to N players:

public class GameContext {
    private List<Player> players; // Instead of just 2
    private int currentPlayerIndex;

    public Player getCurrentPlayer() {
        return players.get(currentPlayerIndex);
    }

    public void advanceTurn() {
        currentPlayerIndex = (currentPlayerIndex + 1) % players.size();
    }
}

// State pattern becomes simpler - no need for specific XTurnState, OTurnState
public class PlayingState implements GameState {
    @Override
    public void next(GameContext context, Player player, boolean hasWon) {
        if (hasWon) {
            context.setState(new GameOverState(player));
        } else if (context.getBoard().isFull()) {
            context.setState(new DrawState());
        } else {
            context.advanceTurn(); // Stay in PlayingState, just change player
        }
    }
}

// Usage for 3-player game:
List<Player> players = Arrays.asList(
    new Player(Symbol.X, new HumanPlayerStrategy()),
    new Player(Symbol.O, new HumanPlayerStrategy()),
    new Player(Symbol.Z, new AIPlayerStrategy())
);
GameContext context = new GameContext(new Board(4), players);
```

---

### **Q5: How to make the game thread-safe for concurrent access?**

**Answer:**
```
Add synchronization at key points:

public class GameContext {
    private GameState currentState;
    private Board board;
    private final Object lock = new Object();

    public void makeMove(Player player, Position pos) {
        synchronized(lock) {
            if (!currentState.isGameOver()) {
                boolean placed = board.placeSymbol(player.getSymbol(), pos);
                if (placed) {
                    boolean hasWon = board.hasWon(player.getSymbol());
                    currentState.next(this, player, hasWon);
                }
            }
        }
    }

    public boolean isGameOver() {
        synchronized(lock) {
            return currentState.isGameOver();
        }
    }
}

// For networked multiplayer, use distributed locking:
public class NetworkedGameContext {
    private RedissonClient redisson;

    public void makeMove(String gameId, Player player, Position pos) {
        RLock lock = redisson.getLock("game:" + gameId);
        try {
            lock.lock(10, TimeUnit.SECONDS);
            // Make move atomically
        } finally {
            lock.unlock();
        }
    }
}
```

---

### **Q6: How would you test this system?**

**Answer:**
```
Unit Tests:
1. Board Tests:
   - Test win detection for all rows
   - Test win detection for all columns
   - Test win detection for diagonals
   - Test draw detection
   - Test invalid move handling

2. State Tests:
   - Test XTurnState → OTurnState transition
   - Test win transition (XTurnState → XWonState)
   - Test draw transition
   - Test game over states don't allow moves

3. Strategy Tests:
   - Test HumanPlayerStrategy input handling
   - Test AIStrategy finds winning move
   - Test AIStrategy blocks opponent

Integration Tests:
- Full game flow (X wins)
- Full game flow (O wins)
- Full game flow (draw)
- Invalid move handling
- Multiple games in sequence

Example Test:
public class BoardTest {
    @Test
    public void testHorizontalWin() {
        Board board = new Board(3);
        board.placeSymbol(Symbol.X, new Position(0, 0));
        board.placeSymbol(Symbol.X, new Position(0, 1));
        board.placeSymbol(Symbol.X, new Position(0, 2));

        assertTrue(board.hasWon(Symbol.X));
        assertFalse(board.hasWon(Symbol.O));
    }

    @Test
    public void testDiagonalWin() {
        Board board = new Board(3);
        board.placeSymbol(Symbol.O, new Position(0, 0));
        board.placeSymbol(Symbol.O, new Position(1, 1));
        board.placeSymbol(Symbol.O, new Position(2, 2));

        assertTrue(board.hasWon(Symbol.O));
    }
}
```

---

### **Q7: How to add replay/spectator mode?**

**Answer:**
```
Add Observer Pattern + Event Recording:

// Observer interface
public interface GameObserver {
    void onMoveMade(Player player, Position pos);
    void onGameOver(Player winner);
}

// Spectator implementation
public class SpectatorObserver implements GameObserver {
    @Override
    public void onMoveMade(Player player, Position pos) {
        System.out.println("Spectator sees: " + player + " played " + pos);
    }

    @Override
    public void onGameOver(Player winner) {
        System.out.println("Spectator sees: Game over! Winner: " + winner);
    }
}

// Replay recorder
public class ReplayRecorder implements GameObserver {
    private List<GameEvent> events = new ArrayList<>();

    @Override
    public void onMoveMade(Player player, Position pos) {
        events.add(new MoveEvent(player, pos, System.currentTimeMillis()));
    }

    public void replay() {
        Board board = new Board(3);
        for (GameEvent event : events) {
            Thread.sleep(1000); // Pause between moves
            board.placeSymbol(event.player.getSymbol(), event.position);
            board.display();
        }
    }
}

// GameContext notifies observers
public class GameContext {
    private List<GameObserver> observers = new ArrayList<>();

    public void addObserver(GameObserver observer) {
        observers.add(observer);
    }

    public void makeMove(Player player, Position pos) {
        // ... make move
        notifyObservers(player, pos);
    }

    private void notifyObservers(Player player, Position pos) {
        for (GameObserver observer : observers) {
            observer.onMoveMade(player, pos);
        }
    }
}
```

---

## **⚠️ Known Limitations & Trade-offs**

1. **No Persistence**
   - Current: In-memory only
   - Fix: Add GameRepository for database persistence

2. **No Network Play**
   - Current: Local only
   - Fix: Add networking layer with WebSockets

3. **Simple AI**
   - Current: No AI implementation in base code
   - Fix: Add minimax with alpha-beta pruning

4. **Fixed 3×3 Board**
   - Current: Hard-coded size
   - Fix: Make size configurable (shown in extensions)

5. **No Timer/Time Limits**
   - Current: Players can think indefinitely
   - Fix: Add turn timer with timeout handling

---

## **📚 Key Takeaways**

**Design Patterns Used:**
- ✅ State Pattern (game states)
- ✅ Strategy Pattern (player types)
- ✅ Context Object Pattern (game context)

**SOLID Principles:**
- ✅ All 5 principles demonstrated

**Algorithms:**
- ✅ Win detection: O(n) per move
- ✅ Minimax for AI: O(9!) with pruning to O(b^(d/2))

**Interview Focus Points:**
- State Pattern eliminates if-else soup
- Strategy Pattern for extensibility
- Win detection algorithm efficiency
- Extensions (N×N, AI, undo, multiplayer)

---

## **🎓 What You Should Master**

Before interview, ensure you can:

1. ✅ **Explain State Pattern benefits** vs enum/if-else
2. ✅ **Draw state transition diagram** from memory
3. ✅ **Explain win detection algorithm** with complexity
4. ✅ **Code minimax algorithm** on whiteboard
5. ✅ **Extend to N×N board** in 5 minutes
6. ✅ **Add new player strategy** in 2 minutes
7. ✅ **Discuss all 5 SOLID principles** with examples
8. ✅ **Handle edge cases**: invalid moves, concurrent access
9. ✅ **Design multiplayer extension** (>2 players)
10. ✅ **Implement undo/redo** using Command Pattern

**Practice Exercises:**
- Implement 4×4 board with 3-in-a-row
- Add Medium AI that blocks opponent
- Add timer with 30-second turn limit
- Implement game replay functionality
- Add network play with sockets

**Time to master:** 2-3 hours

**Difficulty:** ⭐⭐ (Easy-Medium, great for learning patterns)

**Interview Frequency:** ⭐⭐⭐ (Very common as warmup question)

---

## **🎯 Pro Tips for Interview**

1. **Start Simple**: Implement basic 3×3 first, then extend
2. **Ask Clarifications**: Board size? Player types? Time limits?
3. **State Pattern is Key**: This is THE learning point
4. **Show Extensibility**: Demonstrate easy additions
5. **Mention Optimizations**: O(1) win check, minimax pruning
6. **SOLID Principles**: Call them out as you code
7. **Test Cases**: Mention them even if not asked

**Common Follow-ups:**
- "Add AI player" → Minimax
- "Make it N×N" → Show configurable Board
- "Add undo" → Command Pattern
- "Support 3+ players" → Show generalization

**This problem tests:**
- ✅ Design pattern knowledge (State, Strategy)
- ✅ SOLID principles understanding
- ✅ Algorithm skills (minimax, win detection)
- ✅ Extensibility thinking
- ✅ Clean code practices

Master this problem and you'll ace similar questions like Connect-4, Chess, or any turn-based game! 🎮✨
