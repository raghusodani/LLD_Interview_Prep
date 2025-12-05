# Design Chess Game - Comprehensive Solution ♟️

## **Problem Statement**

Design a fully functional chess game system that can:
- Support standard 8x8 chess board with all pieces
- Implement movement rules for all 6 piece types
- Validate moves (legal positions, piece-specific rules)
- Detect check, checkmate, and stalemate
- Support special moves (castling, en passant, pawn promotion)
- Track game state and move history
- Handle turn-based gameplay
- Extensible for AI opponent or online multiplayer

---

## **🎯 Our Approach**

### **1. Core Requirements Analysis**

**Functional Requirements:**
- ✅ 8x8 board with 32 pieces (16 white, 16 black)
- ✅ 6 piece types with unique movement patterns
- ✅ Move validation (legal squares, path blocking, captures)
- ✅ Check/checkmate/stalemate detection
- ✅ Special moves (castling, en passant, pawn promotion)
- ✅ Turn management (alternating white/black)
- ✅ Game state tracking (in-progress, checkmate, stalemate, draw)
- ✅ Move history for undo/replay

**Non-Functional Requirements:**
- ✅ Extensible for new game modes (Chess960, 3-player chess)
- ✅ Easy to add AI opponent
- ✅ Testable piece movement logic
- ✅ Clean separation of concerns
- ✅ Performance: Fast move validation (< 10ms)

**Complexity:**
- 6 different piece types with unique rules
- Special move handling (4 types)
- Check detection algorithm
- Stalemate vs checkmate distinction
- Most complex LLD problem due to chess rules!

---

## **🏗️ Architecture & Design Patterns**

### **Pattern 1: Strategy Pattern (Movement Strategies) ⭐**

**Where:** Piece movement logic

**Why:**
- Each piece has different movement rules
- Knight moves in L-shape, Bishop moves diagonally, Rook straight lines, etc.
- Behavior varies dramatically by piece type
- Easy to test each movement strategy independently

**The Challenge:**
```java
// ❌ BAD: Giant if-else nightmare
public boolean canMove(Piece piece, Position from, Position to) {
    if (piece.getType() == "PAWN") {
        // 50 lines of pawn logic (forward, capture diagonally, en passant)
    } else if (piece.getType() == "KNIGHT") {
        // L-shaped movement logic
    } else if (piece.getType() == "BISHOP") {
        // Diagonal movement logic
    } else if (piece.getType() == "ROOK") {
        // Straight line logic
    } else if (piece.getType() == "QUEEN") {
        // Copy bishop + rook logic (duplication!)
    } else if (piece.getType() == "KING") {
        // One square + castling logic
    }
    // This method would be 300+ lines and impossible to maintain!
}
```

**✅ GOOD: Strategy Pattern**
```java
// Strategy Interface
public interface MovementStrategy {
    List<Position> getPossibleMoves(Position current, Board board, Piece piece);
}

// Each piece has its own strategy
public class KnightMovementStrategy implements MovementStrategy {
    @Override
    public List<Position> getPossibleMoves(Position current, Board board, Piece piece) {
        List<Position> moves = new ArrayList<>();

        // Knight moves in L-shape: 2 squares in one direction, 1 in perpendicular
        int[][] offsets = {
            {2, 1}, {2, -1}, {-2, 1}, {-2, -1},
            {1, 2}, {1, -2}, {-1, 2}, {-1, -2}
        };

        for (int[] offset : offsets) {
            int newRow = current.getRow() + offset[0];
            int newCol = current.getCol() + offset[1];

            if (isValidPosition(newRow, newCol)) {
                Position newPos = new Position(newRow, newCol);
                Cell targetCell = board.getCell(newPos);

                // Can move to empty cell or capture opponent's piece
                if (targetCell.isEmpty() || targetCell.getPiece().getColor() != piece.getColor()) {
                    moves.add(newPos);
                }
            }
        }

        return moves;
    }
}

// Piece composition
public abstract class Piece {
    private MovementStrategy movementStrategy;

    public Piece(MovementStrategy strategy) {
        this.movementStrategy = strategy;
    }

    public List<Position> getPossibleMoves(Position current, Board board) {
        return movementStrategy.getPossibleMoves(current, board, this);
    }
}

public class Knight extends Piece {
    public Knight(String color) {
        super(new KnightMovementStrategy());
        this.color = color;
    }
}
```

**Benefits:**
- ✅ Each strategy is ~30-50 lines (manageable!)
- ✅ Easy to test each piece type independently
- ✅ Adding new piece type = new strategy class
- ✅ Open/Closed Principle - no modification needed
- ✅ Single Responsibility - each class handles one piece type

---

### **Pattern 2: Factory Pattern (Piece Creation)**

**Where:** Creating chess pieces at game start

**Why:**
- Need to create 32 pieces with correct types, colors, and positions
- Centralize piece instantiation logic
- Hide complexity of assigning movement strategies
- Easy to add new piece types (for chess variants)

**Implementation:**

```java
public class PieceFactory {
    public static Piece createPiece(String type, String color) {
        switch(type.toUpperCase()) {
            case "PAWN":
                return new Pawn(color);
            case "ROOK":
                return new Rook(color);
            case "KNIGHT":
                return new Knight(color);
            case "BISHOP":
                return new Bishop(color);
            case "QUEEN":
                return new Queen(color);
            case "KING":
                return new King(color);
            default:
                throw new IllegalArgumentException("Unknown piece type: " + type);
        }
    }

    // Convenience method to setup initial board
    public static void initializeBoard(Board board) {
        // Setup white pieces (rows 0-1)
        setupPawns(board, 1, "WHITE");
        setupBackRank(board, 0, "WHITE");

        // Setup black pieces (rows 6-7)
        setupPawns(board, 6, "BLACK");
        setupBackRank(board, 7, "BLACK");
    }

    private static void setupBackRank(Board board, int row, String color) {
        board.placePiece(createPiece("ROOK", color), new Position(row, 0));
        board.placePiece(createPiece("KNIGHT", color), new Position(row, 1));
        board.placePiece(createPiece("BISHOP", color), new Position(row, 2));
        board.placePiece(createPiece("QUEEN", color), new Position(row, 3));
        board.placePiece(createPiece("KING", color), new Position(row, 4));
        board.placePiece(createPiece("BISHOP", color), new Position(row, 5));
        board.placePiece(createPiece("KNIGHT", color), new Position(row, 6));
        board.placePiece(createPiece("ROOK", color), new Position(row, 7));
    }
}
```

**Benefits:**
- ✅ Single place to create all pieces
- ✅ Client code doesn't know about concrete piece classes
- ✅ Easy to add new piece types for variants (Archbishop in Chess960)

---

### **Pattern 3: Template Method (Abstract Piece Class)**

**Where:** Piece hierarchy

**Why:**
- Common behavior (getColor, getPosition, hasMoved) in base class
- Specific behavior (getPossibleMoves) in subclasses
- Type safety and polymorphism

**Implementation:**

```java
public abstract class Piece {
    protected String color;
    protected MovementStrategy movementStrategy;
    protected boolean hasMoved = false;

    public Piece(MovementStrategy strategy, String color) {
        this.movementStrategy = strategy;
        this.color = color;
    }

    // Template method - common to all pieces
    public List<Position> getPossibleMoves(Position current, Board board) {
        return movementStrategy.getPossibleMoves(current, board, this);
    }

    // Common behavior
    public String getColor() { return color; }
    public boolean hasMoved() { return hasMoved; }
    public void setMoved() { this.hasMoved = true; }

    // Force subclasses to implement
    public abstract String getType();
    public abstract String getSymbol(); // For display (♔ ♕ ♖ ♗ ♘ ♙)
}

public class King extends Piece {
    public King(String color) {
        super(new KingMovementStrategy(), color);
    }

    @Override
    public String getType() { return "KING"; }

    @Override
    public String getSymbol() {
        return color.equals("WHITE") ? "♔" : "♚";
    }
}
```

---

### **Pattern 4: State Pattern (Game State Management)**

**Where:** Game flow control

**Why:**
- Game has different states: In-Progress, Check, Checkmate, Stalemate, Draw
- Behavior changes based on state
- Different validation rules in different states

**Implementation:**

```java
public enum GameState {
    IN_PROGRESS,
    CHECK,          // King is under attack
    CHECKMATE,      // King is under attack and no legal moves
    STALEMATE,      // No legal moves but king is safe
    DRAW            // 50-move rule, insufficient material, etc.
}

public class ChessGame {
    private GameState currentState;
    private Player currentPlayer;

    public boolean makeMove(Move move) {
        // Validate move
        if (!isValidMove(move)) {
            return false;
        }

        // Execute move
        executeMove(move);

        // Update game state
        updateGameState();

        // Switch player if game continues
        if (currentState == GameState.IN_PROGRESS || currentState == GameState.CHECK) {
            switchPlayer();
        }

        return true;
    }

    private void updateGameState() {
        Player opponent = getOpponent();

        if (isKingInCheck(opponent)) {
            if (hasNoLegalMoves(opponent)) {
                currentState = GameState.CHECKMATE;
            } else {
                currentState = GameState.CHECK;
            }
        } else {
            if (hasNoLegalMoves(opponent)) {
                currentState = GameState.STALEMATE;
            } else {
                currentState = GameState.IN_PROGRESS;
            }
        }
    }
}
```

---

## **📐 Class Diagram Overview**

```
┌─────────────────┐
│   ChessGame     │ (Controller)
│  (GameState)    │
└────────┬────────┘
         │
    ┌────┴─────────────────────┐
    │                          │
┌───▼──────┐           ┌──────▼──────┐
│  Board   │           │   Player    │
│ (8x8)    │           │  (White/    │
└───┬──────┘           │   Black)    │
    │                  └─────────────┘
    │ contains
    │
┌───▼──────────┐
│     Cell     │
│  (Position)  │
└───┬──────────┘
    │ may contain
    │
┌───▼──────────────┐
│     Piece        │ (Abstract)
│   ┌────────────┐ │
│   │ Movement   │ │ (Composition)
│   │ Strategy   │ │
│   └────────────┘ │
└───┬──────────────┘
    │
    ├─ King
    ├─ Queen
    ├─ Rook
    ├─ Bishop
    ├─ Knight
    └─ Pawn

MovementStrategy (Interface)
    │
    ├─ KingMovementStrategy
    ├─ QueenMovementStrategy (Rook + Bishop)
    ├─ RookMovementStrategy (Straight lines)
    ├─ BishopMovementStrategy (Diagonals)
    ├─ KnightMovementStrategy (L-shape)
    └─ PawnMovementStrategy (Complex!)
```

---

## **🔑 Key Design Decisions**

### **Decision 1: Strategy Pattern for Movement vs Inheritance**

**What:** Use composition (Strategy) instead of pure inheritance

**Why:**
```java
// ❌ Alternative: Pure Inheritance
public abstract class Piece {
    public abstract List<Position> getPossibleMoves(...);
}

public class Queen extends Piece {
    @Override
    public List<Position> getPossibleMoves(...) {
        // Queen = Rook + Bishop
        // Need to duplicate code from both!
        // Or create complex inheritance hierarchy
    }
}

// ✅ Our Approach: Strategy Pattern
public class Queen extends Piece {
    public Queen(String color) {
        super(new QueenMovementStrategy(), color);
        // QueenMovementStrategy reuses Rook + Bishop logic
    }
}
```

**Interview Question:**
> "Why not just override getPossibleMoves() in each Piece subclass?"

**Answer:**
> "Strategy Pattern offers several advantages: (1) **Testability** - test movement logic independently of Piece class, (2) **Reusability** - Queen can combine Rook + Bishop strategies without code duplication, (3) **Runtime flexibility** - could swap strategies for chess variants, (4) **Separation of Concerns** - Piece handles state (color, position), Strategy handles behavior (movement), (5) **Open/Closed** - adding new piece type doesn't modify existing code."

---

### **Decision 2: Check Detection Algorithm**

**What:** Efficient check detection without expensive board scanning

**Algorithm:**
```java
public boolean isKingInCheck(Player player) {
    // 1. Find king's position (O(1) with position tracking)
    Position kingPosition = board.findKing(player.getColor());

    // 2. Check if any opponent piece can attack king position
    for (Piece opponentPiece : board.getPieces(opponent.getColor())) {
        List<Position> possibleMoves = opponentPiece.getPossibleMoves(
            board.getPosition(opponentPiece),
            board
        );

        if (possibleMoves.contains(kingPosition)) {
            return true; // King is under attack!
        }
    }

    return false;
}
```

**Optimization:**
- Cache king position (don't scan board)
- Only check opponent's pieces
- Short-circuit on first threat found

**Interview Question:**
> "How to optimize check detection for performance?"

**Answer:**
> "Several optimizations: (1) **Cache king position** - update only when king moves (O(1) lookup), (2) **Attack map** - maintain data structure of all attacked squares, update incrementally (trade memory for speed), (3) **Piece indexing** - group pieces by type, check threatening pieces first (Queen, Rook, Bishop before Pawns), (4) **Early termination** - return true on first threat found, (5) **Bitboards** (advanced) - use 64-bit integers to represent board state, use bitwise operations for super-fast calculations."

---

### **Decision 3: Validation Layers**

**What:** Multi-layer validation for move legality

**Layers:**
```java
public boolean isValidMove(Move move) {
    // Layer 1: Basic validation
    if (!isWithinBoard(move.getTo())) return false;
    if (!isPlayersPiece(move.getPiece())) return false;

    // Layer 2: Piece-specific movement rules
    List<Position> possibleMoves = move.getPiece().getPossibleMoves(
        move.getFrom(),
        board
    );
    if (!possibleMoves.contains(move.getTo())) return false;

    // Layer 3: Path obstruction check
    if (!isPathClear(move.getFrom(), move.getTo())) return false;

    // Layer 4: Check prevention (CRITICAL!)
    // A move is illegal if it leaves your own king in check
    if (leavesKingInCheck(move)) return false;

    return true;
}

// Most important validation!
private boolean leavesKingInCheck(Move move) {
    // Simulate move
    Board tempBoard = board.clone();
    tempBoard.executeMove(move);

    // Check if king is now in check
    boolean kingInCheck = isKingInCheck(currentPlayer, tempBoard);

    return kingInCheck;
}
```

**Why Layer 4 matters:**
```
Scenario: Your king is in check from opponent's rook

  a  b  c  d  e  f  g  h
8 ♜  .  .  .  ♚  .  .  .  8
7 .  .  .  .  .  .  .  .  7
6 .  .  .  .  .  .  .  .  6
5 .  .  .  .  .  .  .  .  5
4 .  .  .  .  .  .  .  .  4
3 .  .  .  .  .  .  .  .  3
2 .  .  .  .  ♔  .  .  .  2
1 .  .  .  .  .  .  .  .  1
  a  b  c  d  e  f  g  h

Black Rook on a8 attacks White King on e2 (same file)

Legal moves for White King:
- d1, d2, d3, f1, f2, f3 ✅ (moves out of check)
- e1, e3 ❌ (still in check from rook!)

You MUST simulate the move and verify king is safe!
```

---

### **Decision 4: Special Moves Handling**

**What:** Handle 4 special chess moves

**1. Castling (King + Rook)**
```java
public boolean canCastle(King king, Rook rook) {
    // Conditions:
    // 1. Neither king nor rook has moved
    if (king.hasMoved() || rook.hasMoved()) return false;

    // 2. Path between king and rook is clear
    if (!isPathClear(king.getPosition(), rook.getPosition())) return false;

    // 3. King is not in check
    if (isKingInCheck(currentPlayer)) return false;

    // 4. King doesn't pass through check
    Position intermediatePos = getIntermediatePosition(king, rook);
    if (isSquareUnderAttack(intermediatePos, opponent)) return false;

    // 5. King doesn't end in check
    Position finalPos = getCastlingFinalPosition(king, rook);
    if (isSquareUnderAttack(finalPos, opponent)) return false;

    return true;
}
```

**2. En Passant (Pawn capture)**
```java
public boolean canEnPassant(Pawn pawn, Position targetPosition) {
    // Conditions:
    // 1. Pawn is on 5th rank (white) or 4th rank (black)
    // 2. Opponent pawn just moved 2 squares forward
    // 3. Opponent pawn is adjacent
    // 4. Must capture immediately (next move)

    Move lastMove = moveHistory.getLastMove();

    if (lastMove.getPiece() instanceof Pawn &&
        lastMove.isDoublePawnMove() &&
        isAdjacentPawn(pawn, lastMove.getPiece())) {
        return true;
    }

    return false;
}
```

**3. Pawn Promotion**
```java
public void promotePawn(Pawn pawn, Position position) {
    // When pawn reaches opposite end (rank 8 for white, rank 1 for black)
    if ((pawn.getColor().equals("WHITE") && position.getRow() == 7) ||
        (pawn.getColor().equals("BLACK") && position.getRow() == 0)) {

        // Prompt user for promotion choice
        String choice = getUserPromotion(); // "QUEEN", "ROOK", "BISHOP", "KNIGHT"

        // Replace pawn with chosen piece
        Piece newPiece = PieceFactory.createPiece(choice, pawn.getColor());
        board.replacePiece(position, newPiece);
    }
}
```

**4. Checkmate vs Stalemate**
```java
public GameState determineGameEnd() {
    Player currentPlayer = getCurrentPlayer();

    // Find all legal moves for current player
    List<Move> legalMoves = getAllLegalMoves(currentPlayer);

    if (legalMoves.isEmpty()) {
        // No legal moves available
        if (isKingInCheck(currentPlayer)) {
            return GameState.CHECKMATE; // Lose
        } else {
            return GameState.STALEMATE; // Draw
        }
    }

    return GameState.IN_PROGRESS;
}
```

---

## **💡 SOLID Principles Applied**

### **S - Single Responsibility**
- `Piece` - Represents a chess piece (state)
- `MovementStrategy` - Handles movement logic (behavior)
- `Board` - Manages 8x8 grid and piece positions
- `ChessGame` - Game flow and rule enforcement
- `Move` - Encapsulates a single move

### **O - Open/Closed**
- Adding new piece type: Create new `Piece` subclass + `MovementStrategy`
- Adding new game variant: Create new `ChessGame` subclass
- No modification to existing classes

### **L - Liskov Substitution**
- Any `Piece` subclass can replace base `Piece`
- Any `MovementStrategy` can replace another
- Polymorphism works correctly

### **I - Interface Segregation**
- `MovementStrategy` - Only getPossibleMoves()
- Pieces don't depend on methods they don't use

### **D - Dependency Inversion**
- `Piece` depends on `MovementStrategy` interface, not concrete strategies
- `ChessGame` depends on abstract `Piece`, not concrete pieces

---

## **🎭 Scenario Walkthrough**

### **Scenario: Complete Turn with Check Detection**

**Initial Position:**
```
  a  b  c  d  e  f  g  h
8 ♜  ♞  ♝  ♛  ♚  ♝  ♞  ♜  8
7 ♟  ♟  ♟  ♟  ♟  ♟  ♟  ♟  7
6 .  .  .  .  .  .  .  .  6
5 .  .  .  .  .  .  .  .  5
4 .  .  .  .  ♙  .  .  .  4  ← White pawn moved
3 .  .  .  .  .  .  .  .  3
2 ♙  ♙  ♙  ♙  .  ♙  ♙  ♙  2
1 ♖  ♘  ♗  ♕  ♔  ♗  ♘  ♖  1
  a  b  c  d  e  f  g  h
```

**Step 1: White moves pawn e2 → e4**
```java
Move move = new Move(whitePawn, new Position(1, 4), new Position(3, 4));

// Validation
1. isWithinBoard(e4) → true ✅
2. isPlayersPiece(whitePawn) → true ✅
3. whitePawn.getPossibleMoves() → [e3, e4] ✅
4. isPathClear(e2, e4) → true ✅
5. leavesKingInCheck(move) → false ✅

// Execute
board.movePiece(e2, e4);
whitePawn.setMoved();
moveHistory.add(move);
```

**Step 2: Black moves pawn e7 → e5**
```java
Move move = new Move(blackPawn, new Position(6, 4), new Position(4, 4));

// Similar validation...
// Execute
board.movePiece(e7, e5);
```

**Step 3: White moves bishop f1 → c4 (Scholar's Mate setup)**
```
  a  b  c  d  e  f  g  h
8 ♜  ♞  ♝  ♛  ♚  ♝  ♞  ♜  8
7 ♟  ♟  ♟  ♟  .  ♟  ♟  ♟  7
6 .  .  .  .  .  .  .  .  6
5 .  .  .  .  ♟  .  .  .  5
4 .  .  ♗  .  ♙  .  .  .  4  ← White bishop
3 .  .  .  .  .  .  .  .  3
2 ♙  ♙  ♙  ♙  .  ♙  ♙  ♙  2
1 ♖  ♘  .  ♕  ♔  .  ♘  ♖  1
  a  b  c  d  e  f  g  h

Bishop on c4 now attacks f7 (black king's weak spot!)
```

**Step 4: Black moves pawn a7 → a6 (mistake!)**

**Step 5: White moves queen d1 → f3**
```
Queen on f3 also attacks f7!
Two pieces attacking f7, only defended by king!
```

**Step 6: White moves queen f3 → f7 (CHECKMATE!)**
```java
Move move = new Move(whiteQueen, new Position(2, 5), new Position(6, 5));

// Execute move
board.movePiece(f3, f7);
board.capturePiece(f7); // Capture black pawn

// Update game state
updateGameState();

// Check detection
isKingInCheck(blackPlayer) → true ✅
  Black king on e8 is attacked by white queen on f7

// Checkmate detection
hasNoLegalMoves(blackPlayer) → true ✅
  - King can't move to d8 (attacked by queen)
  - King can't move to e7 (attacked by queen)
  - King can't move to f8 (attacked by bishop on c4)
  - No piece can block
  - No piece can capture queen

currentState = GameState.CHECKMATE
```

**Final Position:**
```
  a  b  c  d  e  f  g  h
8 ♜  ♞  ♝  ♛  ♚  ♝  ♞  ♜  8
7 ♟  ♟  ♟  ♟  .  ♕  ♟  ♟  7  ← White queen delivers checkmate
6 ♟  .  .  .  .  .  .  .  6
5 .  .  .  .  ♟  .  .  .  5
4 .  .  ♗  .  ♙  .  .  .  4
3 .  .  .  .  .  .  .  .  3
2 ♙  ♙  ♙  ♙  .  ♙  ♙  ♙  2
1 ♖  ♘  .  .  ♔  .  ♘  ♖  1
  a  b  c  d  e  f  g  h

WHITE WINS! (Scholar's Mate in 4 moves)
```

---

## **🚀 Extensions & Enhancements**

### **1. Move History & Undo**
```java
public class MoveHistory {
    private Stack<Move> moves = new Stack<>();
    private Stack<Piece> capturedPieces = new Stack<>();

    public void addMove(Move move, Piece captured) {
        moves.push(move);
        capturedPieces.push(captured);
    }

    public void undo() {
        if (moves.isEmpty()) return;

        Move lastMove = moves.pop();
        Piece captured = capturedPieces.pop();

        // Reverse the move
        board.movePiece(lastMove.getTo(), lastMove.getFrom());

        // Restore captured piece
        if (captured != null) {
            board.placePiece(captured, lastMove.getTo());
        }
    }
}
```

### **2. AI Opponent (Minimax Algorithm)**
```java
public class ChessAI {
    private static final int MAX_DEPTH = 4;

    public Move getBestMove(Board board, String aiColor) {
        return minimax(board, MAX_DEPTH, Integer.MIN_VALUE, Integer.MAX_VALUE, true);
    }

    private int minimax(Board board, int depth, int alpha, int beta, boolean maximizing) {
        if (depth == 0 || isGameOver(board)) {
            return evaluateBoard(board);
        }

        if (maximizing) {
            int maxEval = Integer.MIN_VALUE;
            for (Move move : getAllLegalMoves(board, aiColor)) {
                Board tempBoard = board.clone();
                tempBoard.executeMove(move);
                int eval = minimax(tempBoard, depth - 1, alpha, beta, false);
                maxEval = Math.max(maxEval, eval);
                alpha = Math.max(alpha, eval);
                if (beta <= alpha) break; // Alpha-beta pruning
            }
            return maxEval;
        } else {
            // Similar for minimizing player
        }
    }

    private int evaluateBoard(Board board) {
        // Material value: Pawn=1, Knight=3, Bishop=3, Rook=5, Queen=9
        // + Positional bonuses (center control, king safety, etc.)
        int score = 0;
        for (Piece piece : board.getAllPieces()) {
            int value = getPieceValue(piece);
            score += piece.getColor().equals(aiColor) ? value : -value;
        }
        return score;
    }
}
```

### **3. Time Control**
```java
public class TimeControl {
    private long whiteTimeLeft; // milliseconds
    private long blackTimeLeft;
    private long increment; // Fischer increment (add time per move)

    public boolean startTurn(String color) {
        long startTime = System.currentTimeMillis();
        // Wait for move...
        long elapsed = System.currentTimeMillis() - startTime;

        if (color.equals("WHITE")) {
            whiteTimeLeft -= elapsed;
            whiteTimeLeft += increment;
            return whiteTimeLeft > 0;
        } else {
            blackTimeLeft -= elapsed;
            blackTimeLeft += increment;
            return blackTimeLeft > 0;
        }
    }
}
```

### **4. Save/Load Game (PGN Format)**
```java
public class PGNExporter {
    public String exportGame(ChessGame game) {
        StringBuilder pgn = new StringBuilder();
        pgn.append("[Event \"Casual Game\"]\n");
        pgn.append("[Date \"" + LocalDate.now() + "\"]\n");
        pgn.append("[White \"Player 1\"]\n");
        pgn.append("[Black \"Player 2\"]\n\n");

        List<Move> moves = game.getMoveHistory();
        for (int i = 0; i < moves.size(); i += 2) {
            int moveNum = (i / 2) + 1;
            pgn.append(moveNum + ". ");
            pgn.append(toAlgebraicNotation(moves.get(i)) + " ");
            if (i + 1 < moves.size()) {
                pgn.append(toAlgebraicNotation(moves.get(i + 1)) + " ");
            }
            pgn.append("\n");
        }

        return pgn.toString();
    }
}
```

---

## **🎯 Interview Questions & Answers**

### **Q1: How do you handle castling?**

**Answer:**
```
Castling has 5 conditions:

1. Neither king nor rook has moved (track with hasMoved flag)
2. No pieces between king and rook
3. King is not currently in check
4. King doesn't pass through a square under attack
5. King doesn't land on a square under attack

Implementation:
- Check conditions 1-3 first (cheap checks)
- For condition 4: simulate king moving one square, check if attacked
- For condition 5: simulate final position, check if attacked
- If all pass: move king 2 squares toward rook, move rook next to king

Kingside castling:  King e1→g1, Rook h1→f1
Queenside castling: King e1→c1, Rook a1→d1

Edge case: Can't castle out of check, but CAN castle after being in check (if king hasn't moved)
```

### **Q2: Explain en passant implementation**

**Answer:**
```
En passant is a special pawn capture that can only happen under specific conditions:

Conditions:
1. Your pawn must be on 5th rank (white) or 4th rank (black)
2. Opponent's pawn must be directly adjacent (same rank)
3. Opponent's pawn must have just moved 2 squares forward (previous move)
4. You must capture immediately (en passant opportunity expires after one move)

Implementation:
- Track last move in game state
- When validating pawn moves, check if:
  a) Target square is diagonally adjacent (normal capture square)
  b) Target square is empty (special case for en passant!)
  c) Last move was double pawn push by opponent
  d) Opponent pawn is in correct adjacent position

- If valid: move pawn diagonally, remove opponent pawn from adjacent square

Key insight: The captured pawn is NOT on the square where your pawn lands!

Example:
  White pawn on e5, Black pawn just moved g7→g5
  White can capture: e5→f6, and black pawn on f5 is removed
```

### **Q3: How to distinguish stalemate from checkmate?**

**Answer:**
```
Both situations have NO legal moves, but differ in check status:

CHECKMATE:
- King IS in check (under attack)
- No legal moves available
- Result: Loss for player

STALEMATE:
- King is NOT in check (safe)
- No legal moves available
- Result: Draw (neither player wins)

Algorithm:
```java
if (hasNoLegalMoves(currentPlayer)) {
    if (isKingInCheck(currentPlayer)) {
        return CHECKMATE; // Lose
    } else {
        return STALEMATE; // Draw
    }
}
```

Famous stalemate example:
  White King on a8, Black King on a6, Black Queen on c7
  White has no legal moves (king trapped) but is NOT in check
  = STALEMATE (Draw)

Common mistake: Assuming "no moves = checkmate"
Correct: Must verify king is actually under attack
```

### **Q4: How to implement move history for undo?**

**Answer:**
```
Use Command Pattern with memento:

class Move {
    Piece piece;
    Position from;
    Position to;
    Piece capturedPiece;  // null if no capture
    boolean wasFirstMove; // for castling rights
}

class MoveHistory {
    Stack<Move> moves;

    void addMove(Move move) {
        // Save complete state
        move.capturedPiece = board.getPieceAt(move.to);
        move.wasFirstMove = !move.piece.hasMoved();
        moves.push(move);
    }

    void undo() {
        Move lastMove = moves.pop();

        // Reverse piece position
        board.movePiece(lastMove.to, lastMove.from);

        // Restore captured piece
        if (lastMove.capturedPiece != null) {
            board.placePiece(lastMove.capturedPiece, lastMove.to);
        }

        // Restore first-move status (for castling)
        if (lastMove.wasFirstMove) {
            lastMove.piece.resetMoved();
        }
    }
}

Special cases to handle:
- Castling: undo both king AND rook moves
- En passant: restore pawn to different square than capture square
- Pawn promotion: convert queen back to pawn
```

### **Q5: How would you add an AI opponent?**

**Answer:**
```
Use Minimax algorithm with alpha-beta pruning:

1. Evaluate board position (material + positional factors)
2. Look ahead N moves (depth = 4-6 for reasonable performance)
3. Assume opponent plays optimally
4. Choose move leading to best position

Key components:

class ChessAI {
    int minimax(Board, depth, alpha, beta, maximizing) {
        // Base case: depth = 0 or game over
        if (depth == 0) return evaluateBoard();

        // Recursive case: try all moves
        for (Move move : getAllLegalMoves()) {
            score = minimax(after_move, depth-1, ...);
            // Update alpha/beta for pruning
        }
    }

    int evaluateBoard() {
        // Material: P=1, N=3, B=3, R=5, Q=9, K=1000
        // + Positional: center control, king safety, pawn structure
        // + Mobility: number of legal moves
    }
}

Optimizations:
- Alpha-beta pruning (skip branches that can't improve)
- Move ordering (try captures first, then checks, then others)
- Transposition table (cache previously evaluated positions)
- Iterative deepening (gradually increase search depth)
- Quiescence search (resolve captures before evaluating)

Performance: Depth 4 = ~10,000 positions, ~100ms
           Depth 6 = ~1,000,000 positions, ~10s
```

### **Q6: How to handle pawn promotion?**

**Answer:**
```
Pawn promotion occurs when pawn reaches opposite end:
- White pawn reaches rank 8 (row 7)
- Black pawn reaches rank 1 (row 0)

Player chooses replacement piece: Queen, Rook, Bishop, or Knight
(Almost always Queen, but underpromotion to Knight can avoid stalemate!)

Implementation:

class PawnMovementStrategy {
    List<Position> getPossibleMoves(...) {
        // ... normal pawn moves ...

        // Check for promotion
        if (isPromotionRank(targetRow, piece.getColor())) {
            moves.addAll(getPromotionMoves(position));
        }
    }
}

class ChessGame {
    void executePawnPromotion(Move move) {
        // 1. Remove pawn from board
        board.removePiece(move.to);

        // 2. Prompt player for choice
        String choice = promptPromotionChoice(); // "QUEEN" / "ROOK" / ...

        // 3. Create new piece
        Piece promotedPiece = PieceFactory.createPiece(
            choice,
            move.piece.getColor()
        );

        // 4. Place new piece
        board.placePiece(promotedPiece, move.to);

        // 5. Set hasMoved flag
        promotedPiece.setMoved();
    }
}

Edge cases:
- Promotion during capture (pawn takes piece on 8th rank)
- Promotion giving check/checkmate
- Multiple pawns promoting in same game
- Undo pawn promotion (must restore pawn, not keep queen)
```

### **Q7: How to optimize check detection?**

**Answer:**
```
Naive approach: O(n²) - check every piece against every opponent piece
Optimized approach: O(n) - only check opponent pieces against king

Optimization techniques:

1. Cache king position (O(1) lookup):
   class Board {
       Map<String, Position> kingPositions;
       // Update only when king moves
   }

2. Incremental attack maps:
   class Board {
       Set<Position> whiteAttackedSquares;
       Set<Position> blackAttackedSquares;
       // Update only when pieces move
   }

   isKingInCheck(color) {
       Position kingPos = kingPositions.get(color);
       Set<Position> opponentAttacks = getOpponentAttacks(color);
       return opponentAttacks.contains(kingPos);
   }

3. Bitboards (advanced, 10x faster):
   Use 64-bit integers to represent board state
   long whitePawns = 0x000000000000FF00L;  // Binary board
   Use bitwise operations for super-fast calculations

4. Piece-type prioritization:
   Check queens first (most dangerous)
   Skip pawns initially (least likely)
   Short-circuit on first threat

5. Direction-based checks:
   From king's position, scan outward in 8 directions
   Stop at first piece found in each direction
   Check if that piece can attack along that direction

Performance comparison:
- Naive: ~5ms per check (scan all 32 pieces)
- Cached king: ~1ms per check (only scan 16 opponent pieces)
- Attack maps: ~0.1ms per check (simple set lookup)
- Bitboards: ~0.01ms per check (bitwise operations)
```

### **Q8: How to handle threefold repetition and 50-move rule?**

**Answer:**
```
Both are draw conditions in chess:

1. Threefold Repetition:
   - Same position occurs 3 times (not necessarily consecutive)
   - Position includes: piece positions, castling rights, en passant opportunity

   Implementation:
   class PositionHistory {
       Map<String, Integer> positionCounts;

       String hashPosition(Board board) {
           // Create unique hash for current position
           StringBuilder hash = new StringBuilder();

           // Include piece positions
           for (int row = 0; row < 8; row++) {
               for (int col = 0; col < 8; col++) {
                   Piece p = board.getPiece(row, col);
                   hash.append(p != null ? p.getSymbol() : ".");
               }
           }

           // Include castling rights
           hash.append(canWhiteKingsideCastle ? "K" : "-");
           hash.append(canWhiteQueensideCastle ? "Q" : "-");
           hash.append(canBlackKingsideCastle ? "k" : "-");
           hash.append(canBlackQueensideCastle ? "q" : "-");

           // Include en passant target square
           hash.append(enPassantSquare != null ? enPassantSquare : "-");

           return hash.toString();
       }

       boolean checkThreefoldRepetition(Board board) {
           String posHash = hashPosition(board);
           int count = positionCounts.getOrDefault(posHash, 0) + 1;
           positionCounts.put(posHash, count);
           return count >= 3;
       }
   }

2. 50-Move Rule:
   - Draw if 50 moves (100 half-moves) with no capture or pawn move

   Implementation:
   class ChessGame {
       int halfMoveClock = 0;

       void executeMove(Move move) {
           // Reset clock on pawn move or capture
           if (move.piece instanceof Pawn || move.isCapture()) {
               halfMoveClock = 0;
           } else {
               halfMoveClock++;
           }

           if (halfMoveClock >= 100) {
               currentState = GameState.DRAW;
           }
       }
   }

Combined check:
if (positionHistory.checkThreefoldRepetition(board) ||
    halfMoveClock >= 100) {
    return GameState.DRAW;
}
```

### **Q9: How to make the system thread-safe for online multiplayer?**

**Answer:**
```
Concurrency challenges:
1. Two players making moves simultaneously
2. One player makes move while other is viewing board
3. Spectators watching game
4. Time control ticking for both players

Solutions:

1. Pessimistic Locking (recommended for chess):
```java
class ChessGame {
    private ReentrantLock gameLock = new ReentrantLock();

    public boolean makeMove(Move move, String playerColor) {
        gameLock.lock();
        try {
            // Verify it's this player's turn
            if (!currentPlayer.getColor().equals(playerColor)) {
                return false;
            }

            // Validate and execute move atomically
            if (isValidMove(move)) {
                executeMove(move);
                switchPlayer();
                return true;
            }
            return false;
        } finally {
            gameLock.unlock();
        }
    }
}
```

2. Board state immutability:
```java
class ImmutableBoard {
    private final Piece[][] pieces; // Defensive copy

    public ImmutableBoard executeMove(Move move) {
        // Create new board with move applied
        return new ImmutableBoard(newPieces);
    }
}
// Spectators get snapshot, no locking needed
```

3. Event-driven architecture:
```java
interface GameObserver {
    void onMoveMade(Move move);
    void onGameStateChanged(GameState state);
}

class ChessGame {
    List<GameObserver> observers;

    void notifyObservers(Move move) {
        for (GameObserver observer : observers) {
            observer.onMoveMade(move);
        }
    }
}
```

4. Database-level locking for persistence:
```sql
BEGIN TRANSACTION;

-- Lock the game row
SELECT * FROM games
WHERE game_id = ?
FOR UPDATE;

-- Verify player's turn
-- Execute move
-- Update game state

COMMIT;
```

5. Redis for distributed systems:
```java
Jedis redis = new Jedis("localhost");
String lockKey = "game:" + gameId + ":lock";

if (redis.setnx(lockKey, playerId) == 1) {
    redis.expire(lockKey, 5); // 5 second timeout
    try {
        // Execute move
    } finally {
        redis.del(lockKey);
    }
}
```

Best practices:
- Lock at game level (not board level)
- Use timeouts to prevent deadlocks
- Atomic move validation + execution
- Immutable board for reads
- Event notification for UI updates
```

### **Q10: How to test chess game logic?**

**Answer:**
```
Testing strategy with examples:

1. Unit Tests - Individual components:
```java
@Test
public void testKnightMovement() {
    Board board = new Board();
    Knight knight = new Knight("WHITE");
    board.placePiece(knight, new Position(4, 4)); // e4

    List<Position> moves = knight.getPossibleMoves(
        new Position(4, 4), board
    );

    // Knight should have 8 possible moves from center
    assertEquals(8, moves.size());
    assertTrue(moves.contains(new Position(6, 5))); // f6
    assertTrue(moves.contains(new Position(6, 3))); // d6
    // ... test all 8 L-shaped moves
}

@Test
public void testCheckDetection() {
    Board board = new Board();
    board.placePiece(new King("WHITE"), new Position(0, 4));
    board.placePiece(new Rook("BLACK"), new Position(0, 0));

    assertTrue(game.isKingInCheck(whitePlayer));
}
```

2. Integration Tests - Full game flows:
```java
@Test
public void testScholarsMate() {
    ChessGame game = new ChessGame();

    // 1. e4 e5
    assertTrue(game.makeMove("e2", "e4"));
    assertTrue(game.makeMove("e7", "e5"));

    // 2. Bc4 Nc6
    assertTrue(game.makeMove("f1", "c4"));
    assertTrue(game.makeMove("b8", "c6"));

    // 3. Qf3 Nf6
    assertTrue(game.makeMove("d1", "f3"));
    assertTrue(game.makeMove("g8", "f6"));

    // 4. Qxf7# (checkmate)
    assertTrue(game.makeMove("f3", "f7"));
    assertEquals(GameState.CHECKMATE, game.getState());
}
```

3. Edge Case Tests:
```java
@Test
public void testCastlingAfterCheck() {
    // Setup: White king in check, then moves out
    // Verify: Can still castle (wasn't in check, just WAS)
}

@Test
public void testEnPassantExpiry() {
    // Setup: Opportunity for en passant
    // Make different move
    // Verify: En passant no longer allowed
}

@Test
public void testStalemateNotCheckmate() {
    // Setup: King has no moves but is not in check
    // Verify: Game is STALEMATE (draw), not CHECKMATE
}
```

4. Property-Based Tests:
```java
@Test
public void testAllMovesLeavingKingInCheckAreIllegal() {
    Board board = setupRandomPosition();
    Player player = getCurrentPlayer();

    for (Piece piece : player.getPieces()) {
        for (Move move : getPseudoLegalMoves(piece)) {
            if (leavesKingInCheck(move)) {
                assertFalse(game.isValidMove(move));
            }
        }
    }
}
```

5. Performance Tests:
```java
@Test
public void testMoveValidationPerformance() {
    ChessGame game = new ChessGame();

    long start = System.currentTimeMillis();
    for (int i = 0; i < 1000; i++) {
        game.getAllLegalMoves(currentPlayer);
    }
    long elapsed = System.currentTimeMillis() - start;

    // Should complete 1000 iterations in < 100ms
    assertTrue(elapsed < 100);
}
```

6. Famous Positions (Test Cases):
```java
@Test
public void testBackRankMate() {
    // Load FEN: "6k1/5ppp/8/8/8/8/5PPP/4R1K1 b - - 0 1"
    // Black to move, trapped by own pawns
    // Verify: Checkmate
}

@Test
public void testPerpetualCheck() {
    // Setup position where queen can check infinitely
    // Verify: Draw by repetition
}
```

Test Coverage Targets:
- Movement rules: 100% (each piece type)
- Special moves: 100% (castling, en passant, promotion)
- Check/Checkmate: 100%
- Edge cases: 90%+
- Integration flows: Key scenarios
```

---

## **⚠️ Known Limitations & Trade-offs**

1. **No Move Pruning**
   - Current: Generate all pseudo-legal moves, then filter
   - Better: Generate only legal moves directly (faster)
   - Trade-off: More complex move generation logic

2. **No Opening Book**
   - Current: AI evaluates from scratch every time
   - Better: Use pre-computed opening database for first ~10 moves
   - Impact: Faster early game, stronger opening play

3. **Simple Board Evaluation**
   - Current: Material + basic positional factors
   - Better: Advanced evaluation (pawn structure, king safety, mobility)
   - Trade-off: Slower evaluation, stronger play

4. **No Persistent Storage**
   - Current: In-memory only
   - Better: Database with game state serialization
   - Impact: Can't save/load games, no game history

5. **Single-threaded**
   - Current: One thread evaluates all positions
   - Better: Parallel minimax with work-stealing
   - Impact: Could use multiple CPU cores for stronger AI

---

## **📚 Key Takeaways**

**Design Patterns Used:**
- ✅ Strategy Pattern (movement logic) ⭐ PRIMARY PATTERN
- ✅ Factory Pattern (piece creation)
- ✅ Template Method (piece hierarchy)
- ✅ State Pattern (game state management)

**SOLID Principles:**
- ✅ All 5 principles demonstrated
- ✅ Extensible design for variants and AI

**Algorithm Complexity:**
- ✅ Check detection: O(n) with optimizations
- ✅ Legal move generation: O(n²) worst case
- ✅ Checkmate detection: O(n³) (try all moves, check each result)
- ✅ Minimax AI: O(b^d) where b=branching factor (~30), d=depth

**Interview Focus Points:**
- Strategy Pattern for complex behaviors
- Special move handling (castling, en passant)
- Check/checkmate vs stalemate distinction
- Move validation layers
- AI implementation basics

---

## **🎓 What You Should Master**

Before interview, ensure you can:
1. ✅ Explain why Strategy Pattern is essential for chess
2. ✅ Implement knight/rook/bishop movement from scratch
3. ✅ Write check detection algorithm
4. ✅ Explain castling conditions (all 5!)
5. ✅ Distinguish checkmate vs stalemate
6. ✅ Handle en passant edge case
7. ✅ Implement pawn promotion
8. ✅ Describe minimax algorithm at high level
9. ✅ Draw class diagram from memory
10. ✅ Add new piece type in 5 minutes

**Practice Exercises:**
1. Implement Queen movement (combine Rook + Bishop)
2. Add threefold repetition detection
3. Write tests for Scholar's Mate
4. Implement FEN notation parsing
5. Add move timer with increment

**Time to master:** 4-5 hours of practice

**Difficulty:** ⭐⭐⭐⭐ (Hard - complex rules, but common in interviews)

**Interview Frequency:** ⭐⭐⭐⭐ (Very high at FAANG/gaming companies)

---

## **💡 Pro Tips**

**In Interview:**
1. **Start simple** - Implement basic movement first, add special moves later
2. **Clarify scope** - Ask if you need castling/en passant or just basic rules
3. **Test as you go** - Validate each piece type before moving to next
4. **Think incrementally** - Board → Pieces → Movement → Validation → Game state
5. **Show pattern knowledge** - Mention Strategy Pattern explicitly
6. **Discuss trade-offs** - Acknowledge what you'd improve with more time

**Common Follow-ups:**
- "Add AI opponent" → Mention minimax, don't implement fully
- "Optimize check detection" → Discuss caching, bitboards
- "Handle online multiplayer" → Discuss locking, event-driven architecture
- "Add time control" → Discuss timers, increment, timeout handling

**Red Flags to Avoid:**
- ❌ Giant if-else for piece types (shows lack of OOP knowledge)
- ❌ Not validating moves properly (missing check detection)
- ❌ Confusing checkmate and stalemate
- ❌ Forgetting special moves exist (even if you don't implement them)

**Success Signals:**
- ✅ Mentions Strategy Pattern unprompted
- ✅ Implements clean movement validation
- ✅ Handles check detection correctly
- ✅ Aware of all special moves
- ✅ Tests edge cases
- ✅ Discusses extensibility

This is one of the **most comprehensive LLD problems** - mastering it demonstrates strong OOP skills and pattern knowledge! 🎯
