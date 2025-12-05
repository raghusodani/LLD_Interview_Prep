# Design ATM Machine - Comprehensive Solution 🏧

## **Problem Statement**

Design an ATM (Automated Teller Machine) system that can:
- Accept and validate ATM cards
- Authenticate users with PIN
- Support multiple operations (Balance inquiry, Cash withdrawal, Deposit)
- Dispense cash in optimal denominations
- Manage cash inventory
- Handle transaction failures gracefully
- Maintain account balances
- Track transaction history

---

## **🎯 Our Approach**

### **1. Core Requirements Analysis**

**Functional Requirements:**
- ✅ Card insertion and ejection
- ✅ PIN authentication
- ✅ Display balance
- ✅ Withdraw cash (with denomination optimization)
- ✅ Deposit cash/check
- ✅ Cash inventory management
- ✅ Transaction logging
- ✅ Error handling (insufficient funds, wrong PIN)

**Non-Functional Requirements:**
- ✅ Secure (PIN validation, card encryption)
- ✅ Available 24/7
- ✅ Fast response time (<3 seconds per transaction)
- ✅ Atomic transactions (all or nothing)
- ✅ Cash dispensing optimization
- ✅ Audit trail for compliance

---

## **🏗️ Architecture & Design Patterns**

### **Pattern 1: State Pattern** ⭐ Primary Pattern

**Where:** ATM Operation Flow

**Why:**
- ATM behavior changes drastically based on current state
- Each state has different allowed operations
- Clear state transitions with validation
- Prevents invalid operations (can't withdraw without card)

**States in ATM:**

```
1. IdleState → Waiting for card insertion
2. HasCardState → Card inserted, waiting for PIN
3. SelectOperationState → Authenticated, choose operation
4. TransactionState → Processing transaction (withdraw/deposit)
```

**State Transition Diagram:**

```
     ┌──────────┐
     │  Idle    │ ← No card inserted
     │  State   │
     └────┬─────┘
          │ insertCard()
          ▼
     ┌──────────┐
     │ HasCard  │ ← Card inserted, need PIN
     │  State   │
     └────┬─────┘
          │ enterPIN() [valid]
          ▼
     ┌──────────┐
     │  Select  │ ← Choose: Balance/Withdraw/Deposit
     │Operation │
     └────┬─────┘
          │ selectOperation()
          ▼
     ┌──────────┐
     │Transaction│ ← Processing withdrawal/deposit
     │   State   │
     └────┬─────┘
          │ complete()
          ▼
     Back to SelectOperation or Idle
```

**Implementation:**

```java
public abstract class ATMState {
    public abstract String getStateName();
    public abstract ATMState next(ATMMachineContext context);
}

public class IdleState extends ATMState {
    @Override
    public String getStateName() {
        return "IDLE - Insert Card";
    }

    @Override
    public ATMState next(ATMMachineContext context) {
        // User inserts card
        System.out.println("Card inserted");
        return new HasCardState();
    }
}

public class HasCardState extends ATMState {
    @Override
    public ATMState next(ATMMachineContext context) {
        System.out.println("Enter PIN:");
        // Validate PIN
        if (validatePIN(context.getCard())) {
            return new SelectOperationState();
        } else {
            System.out.println("Invalid PIN");
            return new IdleState(); // Eject card
        }
    }
}
```

**Benefits:**
- ✅ Eliminates complex if-else chains
- ✅ Each state is independent and testable
- ✅ Easy to add new states (e.g., MaintenanceState)
- ✅ State-specific validation logic
- ✅ Clear transition rules

---

### **Pattern 2: Chain of Responsibility** ⭐ Secondary Pattern

**Where:** Cash Dispensing Algorithm

**Why:**
- Need to dispense cash in optimal denominations
- Each denomination handler decides if it can handle part of the amount
- Flexible to add/remove denominations

**Problem to Solve:**

```
User wants: $270
Available: $100, $50, $20, $10

Optimal:
- 2 × $100 = $200
- 1 × $50  = $50
- 1 × $20  = $20
Total = $270 ✅
```

**Implementation:**

```java
public class ATMInventory {
    private Map<CashType, Integer> cashInventory;

    // CashType: HUNDRED(100), FIFTY(50), TWENTY(20), TEN(10)

    public Map<CashType, Integer> dispenseCash(int amount) {
        Map<CashType, Integer> result = new HashMap<>();

        // Chain: Start with highest denomination
        int remaining = amount;

        // Handler 1: $100 bills
        if (remaining >= 100 && cashInventory.get(CashType.HUNDRED) > 0) {
            int count = Math.min(
                remaining / 100,
                cashInventory.get(CashType.HUNDRED)
            );
            result.put(CashType.HUNDRED, count);
            remaining -= count * 100;
        }

        // Handler 2: $50 bills
        if (remaining >= 50 && cashInventory.get(CashType.FIFTY) > 0) {
            int count = Math.min(
                remaining / 50,
                cashInventory.get(CashType.FIFTY)
            );
            result.put(CashType.FIFTY, count);
            remaining -= count * 50;
        }

        // Handler 3: $20 bills
        // Handler 4: $10 bills
        // ... (similar logic)

        if (remaining > 0) {
            throw new InsufficientCashException(
                "Cannot dispense exact amount"
            );
        }

        return result;
    }
}
```

**Benefits:**
- ✅ Greedy algorithm for optimal dispensing
- ✅ Each denomination is independent
- ✅ Easy to add new denominations ($5, $1)
- ✅ Handles inventory constraints

---

### **Pattern 3: Factory Pattern**

**Where:** ATM State Creation

**Why:**
- Centralize state object creation
- Hide state construction complexity
- Ensure correct state initialization

**Implementation:**

```java
public class ATMStateFactory {
    public static ATMState getState(String stateName) {
        switch (stateName) {
            case "IDLE":
                return new IdleState();
            case "HAS_CARD":
                return new HasCardState();
            case "SELECT_OPERATION":
                return new SelectOperationState();
            case "TRANSACTION":
                return new TransactionState();
            default:
                return new IdleState();
        }
    }
}
```

---

### **Pattern 4: Context Object (State Pattern Component)**

**Where:** ATMMachineContext

**Why:**
- Maintains current state
- Stores transaction data (card, account, amount)
- Provides interface for state transitions

**Implementation:**

```java
public class ATMMachineContext {
    private ATMState currentState;
    private Card card;
    private Account account;
    private ATMInventory inventory;

    public ATMMachineContext() {
        this.currentState = new IdleState();
        this.inventory = new ATMInventory();
    }

    public void transitionToNextState() {
        currentState = currentState.next(this);
    }

    public String getCurrentState() {
        return currentState.getStateName();
    }
}
```

---

## **📐 Complete Architecture Diagram**

```
┌────────────────────────────────────────────────────┐
│            ATMMachineContext                       │
│  - currentState: ATMState                          │
│  - card: Card                                      │
│  - account: Account                                │
│  - inventory: ATMInventory                         │
│  + transitionToNextState()                         │
│  + getCurrentState()                               │
└────────────┬───────────────────────────────────────┘
             │
             │ has-a
             ▼
        ┌────────────┐
        │  ATMState  │ (Abstract)
        └──────┬─────┘
               │
    ┌──────────┼──────────┬──────────────┐
    │          │          │              │
┌───▼───┐ ┌───▼───┐ ┌────▼─────┐ ┌─────▼──────┐
│ Idle  │ │HasCard│ │  Select  │ │Transaction │
│ State │ │ State │ │Operation │ │   State    │
└───────┘ └───────┘ └────┬─────┘ └─────┬──────┘
                          │              │
                          └──────┬───────┘
                                 │ uses
                                 ▼
                        ┌─────────────────┐
                        │  ATMInventory   │
                        │  - cashInventory│
                        │  + dispenseCash()│
                        │  + addCash()    │
                        └─────────────────┘
                                 │ uses (Chain of Responsibility)
                                 ▼
                        ┌─────────────────┐
                        │    CashType     │ (Enum)
                        │  - HUNDRED: 100 │
                        │  - FIFTY: 50    │
                        │  - TWENTY: 20   │
                        │  - TEN: 10      │
                        └─────────────────┘
```

---

## **🔑 Key Design Decisions**

### **Decision 1: State Pattern over Giant If-Else**

**Without State Pattern (❌ Bad):**

```java
public class ATM {
    private String state = "IDLE";

    public void handleUserAction(String action) {
        if (state.equals("IDLE")) {
            if (action.equals("insertCard")) {
                state = "HAS_CARD";
            } else {
                System.out.println("Insert card first");
            }
        } else if (state.equals("HAS_CARD")) {
            if (action.equals("enterPIN")) {
                if (validatePIN()) {
                    state = "SELECT_OPERATION";
                } else {
                    state = "IDLE";
                }
            } else {
                System.out.println("Enter PIN first");
            }
        } else if (state.equals("SELECT_OPERATION")) {
            // More nested if-else...
        }
        // This becomes unmaintainable!
    }
}
```

**With State Pattern (✅ Good):**

```java
public class ATMMachineContext {
    private ATMState currentState;

    public void transitionToNextState() {
        currentState = currentState.next(this);
    }

    // Each state handles its own logic!
    // No nested if-else nightmare!
}
```

**Interview Question:**
> "Why use State Pattern instead of if-else?"

**Answer:**
> "If-else becomes unmaintainable with:
> 1. **Complexity**: Each new state adds nested conditionals
> 2. **Testing**: Hard to test individual states in isolation
> 3. **SRP Violation**: One class handles all states
> 4. **OCP Violation**: Can't add new states without modifying existing code
>
> State Pattern gives us:
> - Each state is a separate class (easy to test)
> - Adding new states = new class (OCP)
> - State-specific logic is encapsulated (SRP)
> - Clear state transitions"

---

### **Decision 2: Greedy Algorithm for Cash Dispensing**

**What:** Always try largest denomination first

**Why:**
- Minimizes number of bills dispensed
- Reduces mechanical wear on ATM
- Faster dispensing (fewer bills to count)

**Edge Case Handling:**

```
Problem: User wants $60
Inventory: $50 × 0, $20 × 2, $10 × 10

Greedy fails if we try $50 first (not available)
Solution: Skip unavailable denominations
Result: $20 × 3 = $60 ✅
```

**Interview Question:**
> "What if greedy algorithm can't find exact change?"

**Answer:**
> "Three strategies:
> 1. **Reject transaction**: 'Cannot dispense exact amount'
> 2. **Dispense more**: Give $70 for $60 request (customer gets change)
> 3. **Dynamic Programming**: Find optimal combination (slower, complex)
>
> We use strategy #1 (reject) because:
> - ATMs should dispense exact amounts
> - Strategy #2 requires coin dispensing
> - DP is overkill for 4-5 denominations"

---

### **Decision 3: Atomic Transactions**

**What:** Transaction either fully completes or fully rolls back

**Why:**
- Prevent partial withdrawals (money deducted but not dispensed)
- Maintain consistency between account and ATM inventory
- Critical for financial systems

**Implementation:**

```java
public void withdrawCash(int amount) {
    try {
        // 1. Check account balance
        if (account.getBalance() < amount) {
            throw new InsufficientFundsException();
        }

        // 2. Check ATM inventory
        if (!inventory.hasSufficientCash(amount)) {
            throw new InsufficientCashException();
        }

        // 3. Calculate denomination breakdown
        Map<CashType, Integer> bills = inventory.dispenseCash(amount);

        // 4. Deduct from account (point of no return)
        account.debit(amount);

        // 5. Update inventory
        inventory.deductCash(bills);

        // 6. Log transaction
        logTransaction(account, amount, bills);

        // 7. Physically dispense cash
        dispenseCash(bills);

        System.out.println("Transaction successful!");

    } catch (Exception e) {
        // Rollback: nothing was changed before exception
        System.out.println("Transaction failed: " + e.getMessage());
    }
}
```

**Interview Question:**
> "What if dispensing fails after deducting from account?"

**Answer:**
> "Disaster scenario! Solutions:
> 1. **Two-phase commit**:
>    - Phase 1: Reserve account balance (don't deduct yet)
>    - Phase 2: Dispense cash, then deduct
>    - If dispense fails, release reservation
>
> 2. **Compensating transaction**:
>    - Log the failure
>    - Auto-credit account
>    - Alert for manual intervention
>
> 3. **Hardware feedback**:
>    - ATM hardware confirms dispensing
>    - Only then mark transaction complete
>    - If no confirmation, automatic reversal"

---

### **Decision 4: Security Measures**

**What:** Multiple layers of security

**Implementation:**

1. **PIN Encryption:**
```java
public boolean validatePIN(Card card, String enteredPIN) {
    String hashedPIN = hashPIN(enteredPIN);
    return card.getPINHash().equals(hashedPIN);
}
```

2. **PIN Attempt Limits:**
```java
private int pinAttempts = 0;
private static final int MAX_ATTEMPTS = 3;

public boolean validatePIN(Card card, String pin) {
    pinAttempts++;
    if (pinAttempts > MAX_ATTEMPTS) {
        captureCard(card);
        return false;
    }
    return checkPIN(card, pin);
}
```

3. **Session Timeout:**
```java
private long sessionStartTime;
private static final long TIMEOUT = 60000; // 60 seconds

public void checkTimeout() {
    if (System.currentTimeMillis() - sessionStartTime > TIMEOUT) {
        ejectCard();
        transitionToIdleState();
    }
}
```

---

## **💡 SOLID Principles Applied**

### **S - Single Responsibility**
- `IdleState` - Only handles idle state logic
- `ATMInventory` - Only manages cash
- `Account` - Only manages account balance
- `Card` - Only holds card information

### **O - Open/Closed**
- Adding new state: Create new class extending `ATMState`
- Adding new transaction type: New state, no modification
- Adding new denomination: Update `CashType` enum

### **L - Liskov Substitution**
- Any `ATMState` subclass can replace base `ATMState`
- All states follow the same contract (`next()` method)

### **I - Interface Segregation**
- `ATMState` has minimal interface (just `next()`)
- Clients only depend on what they need

### **D - Dependency Inversion**
- `ATMMachineContext` depends on `ATMState` abstraction
- Not dependent on concrete states

---

## **🎭 Scenario Walkthrough**

### **Scenario: Complete Withdrawal Flow**

```
Step 1: User inserts card
├─ Current State: IdleState
├─ Action: insertCard()
├─ Next State: HasCardState
└─ Display: "Enter PIN"

Step 2: User enters PIN (1234)
├─ Current State: HasCardState
├─ Action: enterPIN("1234")
├─ Validation: PIN matches
├─ Next State: SelectOperationState
└─ Display: "Select Operation: [1] Balance [2] Withdraw [3] Deposit"

Step 3: User selects Withdraw
├─ Current State: SelectOperationState
├─ Action: selectOperation(WITHDRAW)
├─ Next State: TransactionState
└─ Display: "Enter amount"

Step 4: User enters $270
├─ Current State: TransactionState
├─ Action: processWithdrawal(270)
├─ Checks:
│   ├─ Account balance? $1000 ✅
│   ├─ ATM inventory? $5000 ✅
│   └─ Denomination breakdown? ✅
├─ Calculation:
│   ├─ 2 × $100 = $200
│   ├─ 1 × $50  = $50
│   └─ 1 × $20  = $20
├─ Execute:
│   ├─ Deduct from account: $1000 → $730
│   ├─ Update inventory: -2×$100, -1×$50, -1×$20
│   ├─ Log transaction: TX-12345, $270, SUCCESS
│   └─ Dispense cash
├─ Next State: SelectOperationState
└─ Display: "Take your cash. Another transaction?"

Step 5: User selects NO
├─ Current State: SelectOperationState
├─ Action: ejectCard()
├─ Next State: IdleState
└─ Display: "Thank you! Take your card."
```

---

## **🚀 Extensions & Enhancements**

### **1. Deposit Functionality**

```java
public class DepositState extends ATMState {
    @Override
    public ATMState next(ATMMachineContext context) {
        int amount = getDepositAmount();

        // Cash deposit
        context.getAccount().credit(amount);
        context.getInventory().addCash(amount);

        // Check deposit (needs verification)
        // Mark as pending, credit after 2-3 days

        return new SelectOperationState();
    }
}
```

### **2. Transfer Money**

```java
public class TransferState extends ATMState {
    @Override
    public ATMState next(ATMMachineContext context) {
        String toAccountNumber = getTargetAccount();
        int amount = getAmount();

        Account fromAccount = context.getAccount();
        Account toAccount = getAccount(toAccountNumber);

        // Atomic transfer
        fromAccount.debit(amount);
        toAccount.credit(amount);

        return new SelectOperationState();
    }
}
```

### **3. Mini Statement**

```java
public void printMiniStatement(Account account) {
    List<Transaction> recent = account.getRecentTransactions(10);

    System.out.println("=== MINI STATEMENT ===");
    System.out.println("Account: " + account.getAccountNumber());
    System.out.println("Balance: $" + account.getBalance());
    System.out.println("\nRecent Transactions:");

    for (Transaction tx : recent) {
        System.out.printf("%s | %s | $%d\n",
            tx.getDate(), tx.getType(), tx.getAmount());
    }
}
```

### **4. Multi-Currency Support**

```java
public class MultiCurrencyATM extends ATMMachineContext {
    private Map<Currency, ATMInventory> inventories;

    public void withdraw(int amount, Currency currency) {
        ATMInventory inv = inventories.get(currency);
        inv.dispenseCash(amount);
    }
}
```

---

## **🎯 Interview Questions & Answers**

### **Q1: How do you prevent race conditions when multiple users withdraw simultaneously?**

**Answer:**
```
Three-level locking strategy:

1. **Account-level lock** (pessimistic):
   synchronized(account) {
       if (account.getBalance() >= amount) {
           account.debit(amount);
       }
   }

2. **Inventory-level lock**:
   synchronized(inventory) {
       inventory.dispenseCash(amount);
   }

3. **Database transactions**:
   BEGIN TRANSACTION;
   SELECT balance FROM accounts WHERE id=? FOR UPDATE;
   UPDATE accounts SET balance = balance - ? WHERE id=?;
   COMMIT;

For distributed systems:
- Use distributed locks (Redis, Zookeeper)
- Two-phase commit protocol
- Optimistic locking with version numbers
```

---

### **Q2: How do you handle cash replenishment?**

**Answer:**
```
Cash Management System:

1. **Monitoring**:
   - Track inventory in real-time
   - Alert when below threshold (e.g., <20%)
   - Predict depletion time based on usage patterns

2. **Maintenance State**:
   public class MaintenanceState extends ATMState {
       // ATM is locked for authorized personnel only
       // Can add/remove cash
       // Run diagnostics
   }

3. **Replenishment Process**:
   - ATM goes to MaintenanceState
   - Technician authenticates with admin card
   - Add cash denominations
   - Update inventory
   - Run test transaction
   - Return to IdleState

4. **Audit Trail**:
   - Log all cash additions/removals
   - Compare physical count with system count
   - Alert on discrepancies
```

---

### **Q3: What if ATM runs out of specific denominations?**

**Answer:**
```
Strategies:

1. **Reject transactions that need unavailable denominations**:
   User wants $110, but no $10 bills available
   → Suggest $100 or $120 instead

2. **Dynamic denomination algorithm**:
   Instead of fixed greedy (100→50→20→10)
   Use backtracking to find ANY valid combination:

   For $110 with no $10s:
   Try: $100 + $10 → Fail (no $10)
   Try: $100 + $10 → Fail
   Try: $50 + $50 + $10 → Fail
   Try: $50 + $20×3 → Success!

3. **Denomination substitution rules**:
   If $20 unavailable, use 2×$10
   If $50 unavailable, use $20+$20+$10

4. **Predictive balancing**:
   Analyze withdrawal patterns
   Keep optimal denomination ratios
   Example: 40% $20s, 30% $100s, 20% $50s, 10% $10s
```

---

### **Q4: How do you secure the ATM against fraud?**

**Answer:**
```
Multi-layer security:

1. **Card Security**:
   - EMV chip validation (not just magnetic stripe)
   - CVV verification for online transactions
   - Card blacklist checking

2. **PIN Security**:
   - Encrypted PIN pad
   - PIN never stored in plain text
   - One-way hash (SHA-256 + salt)
   - 3 failed attempts → capture card

3. **Session Security**:
   - 60-second timeout
   - Shoulder-surfing prevention (screen angles)
   - Masked PIN entry (**** display)

4. **Physical Security**:
   - Tamper detection (alerts if opened)
   - GPS tracking
   - Camera recording
   - Alarm system

5. **Network Security**:
   - Encrypted communication (TLS/SSL)
   - VPN to bank servers
   - Firewall protection
   - Regular security patches

6. **Transaction Limits**:
   - Daily withdrawal limit ($500)
   - Per-transaction limit ($200)
   - Frequency limits (max 5 transactions/day)

7. **Fraud Detection**:
   - Unusual withdrawal patterns
   - Geographic anomalies (card used in 2 cities in 1 hour)
   - Rapid succession withdrawals
   - ML-based anomaly detection
```

---

### **Q5: How do you handle network failures?**

**Answer:**
```
Offline capability + Graceful degradation:

1. **Store-and-Forward**:
   - Queue transactions locally
   - Retry when network restored
   - Mark transactions as "pending"

2. **Offline Transaction Limits**:
   - Allow limited withdrawals offline
   - Check against cached balance
   - Lower limits in offline mode ($50 vs $200)

3. **Fallback to Phone Authorization**:
   - User provides account number + PIN
   - ATM calls authorization center
   - Human agent approves transaction
   - Record approval code

4. **Circuit Breaker Pattern**:
   if (networkFailures > 3) {
       enterOfflineMode();
       allowLimitedTransactions();
   }

5. **Status Display**:
   "Limited service due to network issues"
   "Balance inquiry unavailable"
   "Cash withdrawal available (reduced limit)"

6. **Reconciliation**:
   - When network restored, sync all transactions
   - Resolve conflicts (overdraft in offline mode)
   - Update account balances
```

---

### **Q6: How would you implement transaction rollback?**

**Answer:**
```
Saga Pattern for distributed transactions:

public class WithdrawalSaga {
    private List<CompensatingAction> compensations = new ArrayList<>();

    public boolean executeWithdrawal(Account account, int amount) {
        try {
            // Step 1: Check balance
            if (!checkBalance(account, amount)) {
                return false;
            }

            // Step 2: Reserve cash in inventory
            reserveCash(amount);
            compensations.add(() -> releaseCash(amount));

            // Step 3: Debit account
            account.debit(amount);
            compensations.add(() -> account.credit(amount));

            // Step 4: Dispense cash
            if (!dispenseCash(amount)) {
                throw new DispensingException();
            }
            compensations.add(() -> logDispensingFailure());

            // Step 5: Log transaction
            logTransaction(account, amount);

            return true;

        } catch (Exception e) {
            // Execute compensations in reverse order
            rollback();
            return false;
        }
    }

    private void rollback() {
        for (int i = compensations.size() - 1; i >= 0; i--) {
            compensations.get(i).execute();
        }
    }
}

This ensures:
- Partial failures don't leave system in inconsistent state
- All resources are properly released
- Account balance is correct even after failures
```

---

### **Q7: How do you monitor ATM health and uptime?**

**Answer:**
```
Comprehensive monitoring system:

1. **Health Metrics**:
   - Cash inventory levels
   - Transaction success rate
   - Average transaction time
   - Hardware status (card reader, cash dispenser, receipt printer)
   - Network connectivity

2. **Alerting System**:
   public class ATMMonitor {
       private static final int LOW_CASH_THRESHOLD = 20; // 20% of capacity

       public void checkHealth() {
           // Cash level check
           if (inventory.getTotalCash() < LOW_CASH_THRESHOLD) {
               sendAlert(AlertType.LOW_CASH, "ATM ID: " + atmId);
           }

           // Hardware check
           if (!cardReader.isWorking()) {
               sendAlert(AlertType.HARDWARE_FAILURE, "Card reader down");
           }

           // Transaction failure rate
           if (getFailureRate() > 0.05) { // >5% failures
               sendAlert(AlertType.HIGH_FAILURE_RATE);
           }
       }
   }

3. **Dashboard**:
   - Real-time ATM status map
   - Cash levels across all ATMs
   - Transaction volume trends
   - Downtime tracking
   - Maintenance schedule

4. **Predictive Maintenance**:
   - ML models predict component failures
   - Schedule maintenance before failure
   - Optimize technician routes

5. **SLA Tracking**:
   - Target: 99.9% uptime
   - Max downtime: 43 minutes/month
   - Automated failover to nearest ATM
```

---

### **Q8: How would you design for scalability (1000s of ATMs)?**

**Answer:**
```
Distributed architecture:

1. **Microservices**:
   ┌─────────────┐     ┌──────────────┐
   │     ATM     │────▶│ Auth Service │
   │  (Edge)     │     └──────────────┘
   └─────────────┘            │
          │                   ▼
          │           ┌──────────────┐
          └──────────▶│Account Service│
                      └──────────────┘
                              │
                              ▼
                      ┌──────────────┐
                      │ Transaction  │
                      │   Service    │
                      └──────────────┘

2. **Database Sharding**:
   - Shard by account number
   - Each shard handles subset of accounts
   - Reduces load on single database

3. **Caching**:
   - Cache account balances (Redis)
   - Cache ATM locations
   - Cache recent transactions
   - TTL: 30 seconds for balance

4. **Load Balancing**:
   - Round-robin across auth servers
   - Geographic routing (use nearest server)
   - Failover to backup regions

5. **Message Queue**:
   - ATM publishes transactions to queue (Kafka)
   - Backend processes asynchronously
   - Decouples ATM from backend processing

6. **CDN for ATM Locations**:
   - Cache ATM finder data
   - Reduce load on main servers

7. **Regional Data Centers**:
   - US-East, US-West, EU, Asia
   - Replicate data across regions
   - Eventual consistency is acceptable

This scales to:
- 10,000+ ATMs
- 1M+ transactions/day
- <100ms average response time
```

---

## **⚠️ Known Limitations & Trade-offs**

1. **Greedy Algorithm Limitation**
   - May not always find a solution even if one exists
   - Example: Want $30, have $20×0, $10×4
   - Greedy tries $20 first, fails
   - DP would find: $10×3 = $30
   - **Trade-off**: Speed vs optimality (greedy is O(n), DP is O(n×amount))

2. **No Multi-Account Support**
   - One card = one account
   - Real ATMs allow selecting from multiple accounts
   - **Fix**: Add `List<Account>` to Card class

3. **No Receipt Printing**
   - Current implementation only displays on screen
   - **Fix**: Add `ReceiptPrinter` class with State pattern integration

4. **Assumes Network Always Available**
   - No offline mode implementation
   - **Fix**: Add local transaction queue with sync when online

5. **No Admin/Technician Interface**
   - Can't replenish cash through code
   - **Fix**: Add `MaintenanceState` with admin authentication

---

## **📚 Key Takeaways**

**Design Patterns Used:**
- ✅ State Pattern (primary) - ATM states
- ✅ Chain of Responsibility - Cash dispensing
- ✅ Factory Pattern - State creation
- ✅ Saga Pattern - Transaction rollback

**SOLID Principles:**
- ✅ All 5 principles demonstrated

**Security Considerations:**
- ✅ PIN encryption and attempt limits
- ✅ Session timeout
- ✅ Transaction logging
- ✅ Atomic operations

**Concurrency Handling:**
- ✅ Account-level locking
- ✅ Inventory synchronization
- ✅ Database transactions

**Interview Focus Points:**
- State Pattern vs if-else comparison
- Cash dispensing algorithm
- Transaction atomicity
- Security measures
- Concurrency handling
- Rollback mechanisms
- Network failure handling
- Scalability design

---

## **🎓 What You Should Master**

Before interview, ensure you can:

1. ✅ Draw complete state transition diagram from memory
2. ✅ Explain State Pattern benefits over if-else (with code)
3. ✅ Implement cash dispensing algorithm on whiteboard
4. ✅ Discuss transaction atomicity and rollback
5. ✅ Explain 3 security measures in detail
6. ✅ Handle race condition scenario (2 users, 1 account)
7. ✅ Design denomination replenishment strategy
8. ✅ Propose scalability solution for 10,000 ATMs
9. ✅ Add a new operation (transfer/deposit) in 3 minutes
10. ✅ Discuss all 8 Q&A topics confidently

**Time to master:** 3-4 hours of practice

**Difficulty:** ⭐⭐⭐ (Medium-High - Multiple patterns, security considerations)

**Interview Frequency:** ⭐⭐⭐ (High - Common in system design rounds)

---

## **🏆 Pro Tips for Interview**

1. **Start with State Diagram**: Draw states first before diving into code
2. **Mention Security Early**: Shows you think about production systems
3. **Discuss Trade-offs**: Greedy vs DP, speed vs optimality
4. **Scale Gradually**: Start with single ATM, then discuss 1000s
5. **Ask Clarifying Questions**:
   - Multi-currency support?
   - Deposit functionality needed?
   - Network always available?
   - Security requirements?

6. **Code Incrementally**:
   - First: Basic State Pattern
   - Second: Add cash dispensing
   - Third: Add security
   - Fourth: Add concurrency

7. **Highlight Patterns**: Explicitly state "I'm using State Pattern here because..."

Good luck! 🚀
