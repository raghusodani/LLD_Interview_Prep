# Design Splitwise - Comprehensive Solution 💰

## **Problem Statement**

Design a bill-splitting application like Splitwise that can:
- Split expenses among multiple users (equal, percentage, exact amounts)
- Track who owes whom and how much
- Calculate individual user balances
- Simplify debts to minimize number of transactions
- Support multiple expense types
- Notify users when balances change (Observer pattern)

**Core Challenge:** Minimize the number of transactions needed to settle all debts (NP-Hard problem!)

---

## **🎯 Our Approach**

### **1. Core Requirements Analysis**

**Functional Requirements:**
- ✅ Add expenses with different split types (equal, percentage, exact)
- ✅ Track balances between users
- ✅ Calculate total balance per user
- ✅ Simplify debts (minimize transactions)
- ✅ Support notifications when expenses are added

**Non-Functional Requirements:**
- ✅ Extensible for new split types
- ✅ Efficient debt simplification algorithm
- ✅ Handle floating-point precision issues
- ✅ Scale to handle multiple users

---

## **🏗️ Architecture & Design Patterns**

### **Pattern 1: Strategy Pattern (Split Types)**

**Where:** Different ways to split expenses

**Why:**
- Multiple algorithms for splitting bills
- Easy to add new split types (exact, ratio, shares)
- Each split strategy is independent and testable

**Implementation:**

```java
// Strategy Interface
public interface Split {
    Map<User, Double> calculateSplit(
        double amount,
        List<User> participants,
        Map<String, Object> splitDetails
    );
}

// Concrete Strategy 1: Equal Split
public class EqualSplit implements Split {
    @Override
    public Map<User, Double> calculateSplit(double amount, List<User> participants,
                                           Map<String, Object> splitDetails) {
        double amountPerPerson = amount / participants.size();
        Map<User, Double> splits = new HashMap<>();
        for (User user : participants) {
            splits.put(user, amountPerPerson);
        }
        return splits;
    }
}

// Concrete Strategy 2: Percentage Split
public class PercentageSplit implements Split {
    @Override
    public Map<User, Double> calculateSplit(double amount, List<User> participants,
                                           Map<String, Object> splitDetails) {
        Map<User, Double> percentages = (Map<User, Double>) splitDetails.get("percentages");
        Map<User, Double> splits = new HashMap<>();

        for (User user : participants) {
            double userPercentage = percentages.getOrDefault(user, 0.0);
            splits.put(user, amount * userPercentage / 100.0);
        }
        return splits;
    }
}
```

**Benefits:**
- ✅ Open/Closed Principle - Add new split types without modifying existing code
- ✅ Each split type has its own validation logic
- ✅ Easy to test each strategy in isolation

---

### **Pattern 2: Factory Pattern (Split Creation)**

**Where:** Creating split objects

**Why:**
- Centralize creation logic
- Hide complexity from client
- Type-safe split creation

**Implementation:**

```java
public class SplitFactory {
    public static Split createSplit(String type) {
        switch(type.toUpperCase()) {
            case "EQUAL":
                return new EqualSplit();
            case "PERCENTAGE":
                return new PercentageSplit();
            case "EXACT":
                return new ExactSplit();
            default:
                throw new IllegalArgumentException("Unknown split type: " + type);
        }
    }
}
```

---

### **Pattern 3: Observer Pattern (Notifications)**

**Where:** BalanceSheet observes ExpenseManager

**Why:**
- Decouple expense management from balance calculation
- Multiple observers can react to expense changes
- Future: Add notification service, analytics, logging

**Implementation:**

```java
// Subject Interface
public interface ExpenseSubject {
    void addObserver(ExpenseObserver observer);
    void removeObserver(ExpenseObserver observer);
    void notifyObservers(Expense expense);
}

// Observer Interface
public interface ExpenseObserver {
    void onExpenseAdded(Expense expense);
    void onExpenseUpdated(Expense expense);
}

// Concrete Subject
public class ExpenseManager implements ExpenseSubject {
    private List<ExpenseObserver> observers = new ArrayList<>();

    public void addExpense(Expense expense) {
        expenses.add(expense);
        notifyObservers(expense); // Notify all observers
    }
}

// Concrete Observer
public class BalanceSheet implements ExpenseObserver {
    @Override
    public void onExpenseAdded(Expense expense) {
        updateBalances(expense); // Recalculate balances
    }
}
```

**Benefits:**
- ✅ Loose coupling between expense management and balance calculation
- ✅ Easy to add: EmailNotifier, PushNotifier, AnalyticsTracker
- ✅ Single Responsibility - Each observer has one job

---

## **📐 Class Diagram Overview**

```
┌──────────────────┐
│ ExpenseManager   │ (Subject)
│  (Observable)    │
└────────┬─────────┘
         │ notifies
         ▼
┌──────────────────┐        ┌─────────────────┐
│  BalanceSheet    │◄───────│ ExpenseObserver │
│   (Observer)     │        │   (Interface)   │
└────────┬─────────┘        └─────────────────┘
         │
         │ uses
         ▼
┌──────────────────┐
│  UserPair        │ (Stores balance between 2 users)
│  Map<UserPair,   │
│      Double>     │
└──────────────────┘

┌─────────────────┐        ┌──────────────────┐
│  SplitFactory   │───────>│      Split       │ (Strategy)
└─────────────────┘        └────────┬─────────┘
                                    │
                         ┌──────────┼──────────┐
                         │          │          │
                    EqualSplit  PercentageSplit ExactSplit
```

---

## **🔑 Key Design Decisions**

### **Decision 1: Debt Simplification Algorithm**

**The Problem:**
Given net balances, minimize the number of transactions to settle all debts.

**Example:**
```
Initial debts:
- Alice owes Bob $10
- Alice owes Charlie $15
- Bob owes Charlie $5

Without optimization: 3 transactions
With optimization: 2 transactions
- Alice pays Charlie $15
- Alice pays Bob $10
```

**Our Solution: Two Approaches**

#### **Approach 1: Greedy Algorithm (Simple)**

```java
public List<Transaction> getSimplifiedSettlements() {
    // 1. Calculate net balances
    Map<User, Double> netBalances = calculateNetBalances();

    // 2. Separate debtors (owe money) and creditors (owed money)
    List<User> debtors = new ArrayList<>();
    List<User> creditors = new ArrayList<>();
    for (Map.Entry<User, Double> entry : netBalances.entrySet()) {
        if (entry.getValue() < 0) {
            debtors.add(entry.getKey());
        } else if (entry.getValue() > 0) {
            creditors.add(entry.getKey());
        }
    }

    // 3. Match debtors with creditors
    List<Transaction> transactions = new ArrayList<>();
    int i = 0, j = 0;

    while (i < debtors.size() && j < creditors.size()) {
        User debtor = debtors.get(i);
        User creditor = creditors.get(j);

        double debtAmount = Math.abs(netBalances.get(debtor));
        double creditAmount = netBalances.get(creditor);

        // Transfer the minimum of the two amounts
        double transferAmount = Math.min(debtAmount, creditAmount);
        transactions.add(new Transaction(debtor, creditor, transferAmount));

        // Update balances
        netBalances.put(debtor, netBalances.get(debtor) + transferAmount);
        netBalances.put(creditor, netBalances.get(creditor) - transferAmount);

        // Move to next if settled
        if (Math.abs(netBalances.get(debtor)) < 0.001) i++;
        if (Math.abs(netBalances.get(creditor)) < 0.001) j++;
    }

    return transactions;
}
```

**Time Complexity:** O(n log n) for sorting + O(n) for matching = **O(n log n)**
**Result:** Good but not always optimal

---

#### **Approach 2: Dynamic Programming (Optimal)**

**The Challenge:** Finding the absolute minimum transactions is NP-Hard!

**Our Strategy:**
1. Find maximum number of "settled subgroups" (groups that sum to 0)
2. Minimum transactions = Total users - Maximum settled groups

**Algorithm:**

```java
public int getOptimalMinimumSettlements() {
    // 1. Calculate net balances
    List<Double> creditList = calculateNonZeroBalances();
    int n = creditList.size();

    // 2. Use bitmask DP
    int[] dp = new int[1 << n]; // 2^n possible subsets
    Arrays.fill(dp, -1);
    dp[0] = 0; // Base case

    // 3. Find maximum settled subgroups
    int maxSubGroups = dfs((1 << n) - 1, dp, creditList);

    // 4. Minimum transactions = n - maxSubGroups
    return n - maxSubGroups;
}

private int dfs(int mask, int[] dp, List<Double> creditList) {
    if (mask == 0) return 0;
    if (dp[mask] != -1) return dp[mask];

    int maxSubGroups = 0;

    // Try all subsets of current mask
    for (int submask = 1; submask < (1 << creditList.size()); submask++) {
        // Check if submask is subset of mask AND sums to 0
        if ((submask & mask) == submask &&
            Math.abs(sumOfMask(creditList, submask)) < 0.001) {
            // This subset can settle internally!
            maxSubGroups = Math.max(maxSubGroups,
                1 + dfs(mask ^ submask, dp, creditList));
        }
    }

    dp[mask] = maxSubGroups;
    return maxSubGroups;
}
```

**Time Complexity:** O(3^n) - Still exponential but uses memoization
**Space Complexity:** O(2^n)
**Result:** **Optimal minimum transactions!**

**Interview Note:** For 10-15 users, this is fast enough. For millions, use greedy.

---

### **Decision 2: UserPair for Balance Tracking**

**What:** Custom class to represent balance between two users

```java
public class UserPair {
    private User user1;
    private User user2;

    @Override
    public boolean equals(Object obj) {
        // Ensure (A, B) equals (B, A) for lookup
        UserPair other = (UserPair) obj;
        return (user1.equals(other.user1) && user2.equals(other.user2)) ||
               (user1.equals(other.user2) && user2.equals(other.user1));
    }

    @Override
    public int hashCode() {
        // Same hash for (A, B) and (B, A)
        return user1.hashCode() + user2.hashCode();
    }
}
```

**Why:**
- Need bidirectional lookup: balance(Alice, Bob) = -balance(Bob, Alice)
- HashMap key must handle symmetry
- Efficient O(1) lookup

**Alternative Considered:**
- `Map<User, Map<User, Double>>` - Nested maps, more memory
- Single string key "user1_user2" - String concatenation overhead

**Interview Question:**
> "Why not use nested Map<User, Map<User, Double>>?"

**Answer:**
> "Nested maps use more memory (2 maps per user pair) and have more null checks. UserPair with symmetric equals/hashCode gives O(1) lookup with less memory. Trade-off: Custom class complexity vs. cleaner data structure."

---

### **Decision 3: Floating-Point Precision**

**The Problem:**
```java
double a = 60.0 / 3;  // Should be 20.0
double b = 20.0;
if (a == b) // Might be false due to floating-point error!
```

**Our Solution:**
```java
// Use epsilon for comparisons
private static final double EPSILON = 0.001;

if (Math.abs(balance) < EPSILON) {
    // Consider as zero
}
```

**Why:**
- Floating-point arithmetic is imprecise
- Accumulated rounding errors in splits
- Edge cases: $100 / 3 = $33.33 + $33.33 + $33.34

**Interview Question:**
> "What if users dispute $0.01 differences?"

**Answer:**
> "Production solution: Use BigDecimal for exact decimal arithmetic. Trade-off: Performance vs. Precision. For financial apps, always use BigDecimal or store cents as integers."

---

## **💡 SOLID Principles Applied**

### **S - Single Responsibility**
- `Split` - Only calculates splits
- `BalanceSheet` - Only tracks balances
- `ExpenseManager` - Only manages expenses
- `SplitFactory` - Only creates split objects

### **O - Open/Closed**
- Adding new split type: Create new Split implementation, no modification
- Adding new observer: Implement ExpenseObserver, register with manager

### **L - Liskov Substitution**
- Any `Split` implementation can replace the interface
- Polymorphism works correctly: `Split s = new EqualSplit();`

### **I - Interface Segregation**
- `Split` - Only one method: calculateSplit
- `ExpenseObserver` - Only two methods: onExpenseAdded, onExpenseUpdated
- Clients don't depend on unused methods

### **D - Dependency Inversion**
- `ExpenseManager` depends on `ExpenseObserver` interface
- `BalanceSheet` depends on `Split` interface
- High-level modules don't depend on low-level implementations

---

## **🎭 Scenario Walkthrough**

### **Scenario: Three Friends Split Dinner**

```
Initial State:
- Alice, Bob, Charlie go to dinner
- Bill: $60
- Alice pays the full amount

Step 1: Create Expense
├─> Split type: Equal
├─> Calculate: $60 / 3 = $20 per person
└─> Shares: {Alice: $20, Bob: $20, Charlie: $20}

Step 2: Update Balances
├─> Alice paid $60, owes $20 (net: +$40)
├─> Bob paid $0, owes $20 (net: -$20)
└─> Charlie paid $0, owes $20 (net: -$20)

Step 3: Notify Observers
└─> BalanceSheet receives notification
    └─> Updates internal balance map

Result:
Bob owes Alice $20
Charlie owes Alice $20
```

### **Scenario: Complex Multi-Expense Settlement**

```
Expenses:
1. Alice pays $60 for dinner (equal split: 3 people)
   - Bob owes Alice $20
   - Charlie owes Alice $20

2. Bob pays $45 for movie (percentage split)
   - Alice (40%) owes Bob $18
   - Charlie (30%) owes Bob $13.5

Net Balances:
- Alice: +$60 - $20 - $18 = +$22 (owed)
- Bob: -$20 + $45 - $15 = +$10 (owed)
- Charlie: -$20 - $13.5 = -$33.5 (owes)

Simplified Settlements (Optimal):
Transaction 1: Charlie pays Alice $22
Transaction 2: Charlie pays Bob $11.5

Total: 2 transactions (optimal!)
```

---

## **🚀 Extensions & Enhancements**

### **1. Exact Split Type**
```java
public class ExactSplit implements Split {
    @Override
    public Map<User, Double> calculateSplit(double amount, List<User> participants,
                                           Map<String, Object> splitDetails) {
        Map<User, Double> exactAmounts =
            (Map<User, Double>) splitDetails.get("exactAmounts");

        // Validate: sum of exact amounts must equal total
        double sum = exactAmounts.values().stream().mapToDouble(d -> d).sum();
        if (Math.abs(sum - amount) > 0.01) {
            throw new IllegalArgumentException(
                "Exact amounts don't sum to total: " + sum + " vs " + amount
            );
        }

        return new HashMap<>(exactAmounts);
    }
}
```

### **2. Group Expenses**
```java
public class Group {
    private String groupId;
    private String name;
    private List<User> members;
    private List<Expense> expenses;

    public BalanceSheet getGroupBalanceSheet() {
        BalanceSheet bs = new BalanceSheet();
        for (Expense expense : expenses) {
            bs.onExpenseAdded(expense);
        }
        return bs;
    }
}
```

### **3. Email Notifications**
```java
public class EmailNotifier implements ExpenseObserver {
    @Override
    public void onExpenseAdded(Expense expense) {
        for (User participant : expense.getParticipants()) {
            if (!participant.equals(expense.getPayer())) {
                sendEmail(participant,
                    "You owe " + expense.getPayer().getName() +
                    " $" + expense.getShares().get(participant));
            }
        }
    }
}
```

---

## **🎯 Interview Questions & Answers**

### **Q1: Explain the debt simplification algorithm complexity**

**Answer:**
```
We implemented TWO algorithms:

1. GREEDY (Practical):
   - Time: O(n log n) - sorting debtors/creditors
   - Space: O(n)
   - Result: Near-optimal (not guaranteed minimum)
   - Use case: Production with 1000s of users

2. DYNAMIC PROGRAMMING (Optimal):
   - Time: O(3^n) with memoization
   - Space: O(2^n) for DP table
   - Result: Guaranteed minimum transactions
   - Use case: Groups with ≤15 users

Why exponential?
- It's a variant of the NP-Hard "subset sum" problem
- Must try all subsets that sum to zero
- With memoization, we avoid recomputing same states

Production choice: Use greedy for scale, DP for small groups.
```

### **Q2: How would you handle currency conversions?**

**Answer:**
```
Add Currency to Expense:

public class Expense {
    private String currency; // USD, EUR, INR
    private double amount;
    private ExchangeRateService exchangeService;
}

Two approaches:

1. Convert at expense creation:
   - Pros: Simple, no need to track multiple currencies
   - Cons: Exchange rates change, historical data lost

2. Store in original currency, convert when settling:
   - Pros: Accurate current rates, audit trail
   - Cons: More complex, need exchange rate API

Recommended: Store original + converted amount
- Track: originalAmount, originalCurrency, usdAmount
- Use USD (or base currency) for all calculations
- Show both currencies in UI for transparency
```

### **Q3: How to handle deleted/edited expenses?**

**Answer:**
```
Current limitation: No support for deletion/editing

Solution approach:

1. Add expense history:
   private List<ExpenseVersion> versions;

2. Soft delete:
   private boolean isDeleted;
   private Date deletedAt;

3. Reverse transaction on delete:
   public void deleteExpense(Expense expense) {
       // Create reverse expense
       Expense reversal = expense.reverse();
       expenseManager.addExpense(reversal);
       expense.setDeleted(true);
   }

4. Observer notifications:
   void onExpenseDeleted(Expense expense);
   void onExpenseEdited(Expense oldExpense, Expense newExpense);

Challenge: If balances already settled, need to:
- Recalculate from scratch
- Or apply differential update
```

### **Q4: How would you scale to millions of users?**

**Answer:**
```
Current bottlenecks:
1. In-memory storage
2. O(3^n) algorithm doesn't scale
3. No sharding strategy

Solutions:

1. Database layer:
   - Users table
   - Expenses table (partitioned by groupId)
   - Balances table (partitioned by userId pairs)
   - Use PostgreSQL with proper indexing

2. Caching:
   - Redis for user balances (most frequent read)
   - Cache key: "balance:{userId1}:{userId2}"
   - TTL: 5 minutes
   - Invalidate on expense add

3. Algorithm changes:
   - Always use greedy O(n log n) algorithm
   - For small groups, run DP asynchronously
   - Don't block on optimal calculation

4. Sharding:
   - Partition by groupId (groups are independent)
   - Each shard handles subset of groups
   - Load balancer routes by groupId hash

5. Async processing:
   - Add expense → publish to Kafka
   - Workers consume and update balances
   - Eventually consistent (acceptable for this use case)

6. Read replicas:
   - Master for writes
   - Multiple read replicas for balance queries
   - User reads from nearest replica
```

### **Q5: What if two users add expense simultaneously?**

**Answer:**
```
Race condition:

Thread 1: Alice adds expense A at t=0
Thread 2: Bob adds expense B at t=0
Both read balances → both update → last write wins (data loss!)

Solutions:

1. Optimistic Locking (Recommended):
   - Add version number to BalanceSheet
   - Each update checks version
   - If version changed, retry

   UPDATE balances
   SET amount = X, version = version + 1
   WHERE userId1 = ? AND userId2 = ? AND version = ?

2. Pessimistic Locking:
   - Lock balance row before update
   - SELECT ... FOR UPDATE
   - Slower but guaranteed consistency

3. Event Sourcing:
   - Store all expenses as immutable events
   - Rebuild balance sheet from events
   - No concurrent update conflicts

4. Application-level locking:
   synchronized(balanceSheet) {
       balanceSheet.updateBalances(expense);
   }

   Or use ReadWriteLock for better read concurrency

Production: Use optimistic locking with database transactions.
```

### **Q6: How to test the debt simplification algorithm?**

**Answer:**
```
Test cases:

1. Base cases:
   - No users → 0 transactions
   - All balanced (net = 0) → 0 transactions
   - One debtor, one creditor → 1 transaction

2. Triangle scenarios:
   - A owes B $10, B owes C $10, C owes A $10
   - Expected: 0 transactions (cycle cancels out)

3. Complex scenarios:
   - 5 users with random balances
   - Verify: sum of all transactions = sum of debts
   - Verify: greedy ≤ optimal

4. Edge cases:
   - Floating-point precision ($0.001 differences)
   - Very small amounts ($0.0001)
   - Very large amounts ($1,000,000)

5. Performance tests:
   - Greedy with 1000 users
   - DP with 15 users (upper limit)
   - Ensure DP completes in < 1 second

Test implementation:
@Test
public void testSimplification() {
    // Setup users with known balances
    // Run both algorithms
    // Verify greedy result <= DP result
    // Verify all balances sum to zero after transactions
}
```

### **Q7: What patterns would you add for production?**

**Answer:**
```
1. Repository Pattern (Data Access):
   interface ExpenseRepository {
       void save(Expense expense);
       List<Expense> findByGroupId(String groupId);
   }

   Separates business logic from data access.

2. Command Pattern (Undo/Redo):
   interface Command {
       void execute();
       void undo();
   }

   class AddExpenseCommand implements Command {
       private Expense expense;

       void execute() {
           expenseManager.addExpense(expense);
       }

       void undo() {
           expenseManager.deleteExpense(expense);
       }
   }

3. Builder Pattern (Complex Expense Creation):
   Expense expense = new ExpenseBuilder()
       .withId("e1")
       .withDescription("Dinner")
       .withAmount(60.0)
       .withPayer(alice)
       .withParticipants(alice, bob, charlie)
       .withSplit(new EqualSplit())
       .build();

4. Memento Pattern (Snapshots):
   class BalanceSheetMemento {
       private Map<UserPair, Double> balances;
       // Save/restore balance state
   }

   Useful for auditing and rollback.

5. Saga Pattern (Distributed Transactions):
   - Payment processing might fail
   - Need to rollback balance updates
   - Implement compensating transactions
```

### **Q8: Explain time complexity of balance lookup**

**Answer:**
```
Operations and complexity:

1. Add expense:
   - Update balances: O(p) where p = participants
   - Notify observers: O(o) where o = observers
   - Total: O(p + o)

2. Get balance between two users:
   - HashMap lookup: O(1)
   - Two lookups (bidirectional): O(1)
   - Total: O(1) ✅ Efficient!

3. Get user's total balance:
   - Iterate all pairs: O(n²) worst case
   - In practice: O(n) if user has n connections
   - Could optimize with separate Map<User, Double>

4. Simplify debts (greedy):
   - Calculate net balances: O(n²)
   - Sort debtors/creditors: O(n log n)
   - Match pairs: O(n)
   - Total: O(n²) dominated by balance calculation

5. Simplify debts (DP optimal):
   - Calculate net balances: O(n²)
   - DP with bitmask: O(3^n * n)
   - Total: O(3^n * n) - exponential!

Trade-off: Greedy is scalable, DP is optimal.
```

### **Q9: How would you handle disputes?**

**Answer:**
```
Feature: Dispute Resolution

1. Add dispute tracking:
   public class Expense {
       private ExpenseStatus status; // ACTIVE, DISPUTED, RESOLVED
       private List<Dispute> disputes;
   }

   public class Dispute {
       private User disputedBy;
       private String reason;
       private Date createdAt;
       private DisputeStatus status;
   }

2. Freeze disputed expenses:
   - Don't include disputed expenses in balance calculation
   - Show as "pending resolution" in UI

3. Dispute workflow:
   - User raises dispute
   - Notify payer and all participants
   - Group admin or payer resolves
   - Options: Accept change, Reject dispute, Edit expense

4. Implementation:
   public void disputeExpense(String expenseId, User disputedBy, String reason) {
       Expense expense = getExpense(expenseId);
       expense.setStatus(ExpenseStatus.DISPUTED);
       expense.addDispute(new Dispute(disputedBy, reason));

       // Recalculate balances excluding disputed expenses
       balanceSheet.recalculate();

       // Notify all participants
       notifyDisputeCreated(expense, disputedBy);
   }

5. Edge case: Multiple concurrent disputes
   - Lock expense row during dispute creation
   - Queue disputes if already disputed
```

### **Q10: Comparison with real Splitwise - what's missing?**

**Answer:**
```
Our implementation vs. Real Splitwise:

Missing features:

1. Groups:
   - Our: Single global space
   - Splitwise: Multiple groups (trips, roommates, etc.)
   - Add: Group entity with isolated expenses

2. Recurring expenses:
   - Our: One-time expenses only
   - Splitwise: Weekly/monthly recurring bills
   - Add: Cron job + expense template

3. Itemized bills:
   - Our: Total amount only
   - Splitwise: Per-item splits (I had pizza, you had salad)
   - Add: LineItem entity with individual splits

4. Settlement tracking:
   - Our: Calculate what should be paid
   - Splitwise: Record actual payments made
   - Add: Payment entity separate from Expense

5. Receipt scanning:
   - Our: Manual entry
   - Splitwise: OCR from receipt image
   - Add: Image processing + ML

6. Social features:
   - Our: Pure calculation
   - Splitwise: Friends, activity feed, reminders
   - Add: Social graph + notification system

Core algorithm strength:
✅ Our DP debt simplification is actually BETTER than Splitwise's greedy approach!
```

---

## **⚠️ Known Limitations & Trade-offs**

1. **Floating-Point Precision**
   - Current: Using double with epsilon
   - Issue: $100/3 = $33.33 + $33.33 + $33.34 (rounding)
   - Fix: Use BigDecimal for exact arithmetic

2. **No Persistence**
   - Current: In-memory only
   - Issue: Data lost on restart
   - Fix: Add database layer with Repository pattern

3. **DP Algorithm Scalability**
   - Current: O(3^n) - only works for n ≤ 15
   - Issue: Exponential time complexity
   - Fix: Use greedy for large groups, DP for small

4. **No Group Support**
   - Current: All expenses in single space
   - Issue: Can't separate trip expenses from roommate expenses
   - Fix: Add Group entity with isolated balance sheets

5. **No Payment Tracking**
   - Current: Calculate what should be paid
   - Issue: Don't track actual payments made
   - Fix: Add Payment entity separate from Expense

6. **No Concurrency Control**
   - Current: Not thread-safe
   - Issue: Race conditions with simultaneous expense adds
   - Fix: Add database transactions + optimistic locking

---

## **📚 Key Takeaways**

**Design Patterns Used:**
- ✅ Strategy Pattern (Split types)
- ✅ Factory Pattern (Split creation)
- ✅ Observer Pattern (Balance notifications)

**Algorithms:**
- ✅ Greedy debt simplification - O(n log n)
- ✅ DP debt simplification - O(3^n) - Optimal!
- ✅ Bitmask DP for subset sum problem

**SOLID Principles:**
- ✅ All 5 principles applied
- ✅ High cohesion, low coupling
- ✅ Easy to extend and test

**Interview Focus Points:**
- Debt simplification algorithm (greedy vs DP)
- Handling floating-point precision
- Scalability considerations
- Concurrency challenges
- Real-world extensions (groups, payments, disputes)

---

## **🎓 What You Should Master**

Before interview, ensure you can:
1. ✅ Explain both debt simplification algorithms
2. ✅ Prove why the problem is NP-Hard
3. ✅ Trace through DP bitmask algorithm with example
4. ✅ Discuss floating-point precision issues
5. ✅ Design scalable architecture for millions of users
6. ✅ Handle race conditions with concurrent updates
7. ✅ Add a new split type in 5 minutes
8. ✅ Explain O(3^n) time complexity derivation
9. ✅ Discuss trade-offs: greedy vs optimal
10. ✅ Answer all Q&A sections confidently

**Time to master:** 4-5 hours of practice

**Difficulty:** ⭐⭐⭐⭐ (Hard - Advanced algorithms + System design)

**Why this is great for interviews:**
- Combines algorithms (graph, DP) with design patterns
- Tests both coding and system design skills
- Has clear extensions for discussion
- Real-world product (Splitwise, Venmo, PayPal)
- Demonstrates understanding of NP-Hard problems
