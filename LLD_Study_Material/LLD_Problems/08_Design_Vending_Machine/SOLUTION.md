# Design Vending Machine - Comprehensive Solution 🥤

## **Problem Statement**

Design a vending machine that can:
- Accept coins (various denominations)
- Display available products with prices
- Allow product selection
- Dispense products
- Return change
- Handle out-of-stock scenarios
- Manage inventory refills

**The Key Challenge:** The vending machine behaves differently based on its current state (Idle, HasMoney, Selection, Dispensing, OutOfStock).

---

## **🎯 Our Approach**

### **Core Requirements Analysis**

**Functional Requirements:**
- ✅ Accept multiple coin insertions
- ✅ Track total money inserted
- ✅ Allow product selection by code
- ✅ Validate sufficient funds
- ✅ Dispense product
- ✅ Return change
- ✅ Handle out-of-stock items
- ✅ Support inventory updates

**Non-Functional Requirements:**
- ✅ Clear state transitions
- ✅ Prevent invalid operations (e.g., dispense without payment)
- ✅ Maintainable and extensible
- ✅ Easy to add new states

---

## **🏗️ Architecture & Design Patterns**

### **Primary Pattern: State Pattern** ⭐

**Why State Pattern is PERFECT for Vending Machines:**

The vending machine is a **state machine**. Its behavior completely changes based on current state:
- In **Idle State**: Accept coins, allow inventory updates
- In **HasMoney State**: Accept more coins, start product selection
- In **Selection State**: Accept product code, validate payment
- In **Dispense State**: Dispense product, return change, reset
- In **OutOfStock State**: Reject all operations, await refill

**The Problem State Pattern Solves:**

```java
// ❌ WITHOUT State Pattern: Nightmare if-else soup!
public class VendingMachine {
    private String currentState = "IDLE";

    public void insertCoin(Coin coin) {
        if (currentState.equals("IDLE")) {
            // Accept coin, change to HAS_MONEY
        } else if (currentState.equals("HAS_MONEY")) {
            // Accept more coins
        } else if (currentState.equals("SELECTION")) {
            // Reject - wrong state!
        } else if (currentState.equals("DISPENSE")) {
            // Reject - wrong state!
        } else if (currentState.equals("OUT_OF_STOCK")) {
            // Reject - no items!
        }
    }

    public void selectProduct(int code) {
        if (currentState.equals("IDLE")) {
            // Reject - no money!
        } else if (currentState.equals("HAS_MONEY")) {
            // Accept, change to SELECTION
        } else if (currentState.equals("SELECTION")) {
            // Already selecting...
        }
        // ... MORE IF-ELSE HELL!
    }

    // Every method has this if-else soup!
    // Adding new state = modifying ALL methods!
    // UNMAINTAINABLE! 😱
}
```

```java
// ✅ WITH State Pattern: Clean & Extensible!
public interface VendingMachineState {
    String getStateName();
    VendingMachineState next(VendingMachineContext context);
}

public class IdleState implements VendingMachineState {
    @Override
    public VendingMachineState next(VendingMachineContext context) {
        if (!context.getCoinList().isEmpty()) {
            return new HasMoneyState();  // Transition
        }
        return this;  // Stay in same state
    }
}

// Adding new state = new class, no modification!
// Each state encapsulates its own behavior!
// MAINTAINABLE! ✅
```

---

## **📐 State Transition Diagram**

```
                    ┌──────────────┐
                    │  Idle State  │ ◀── START
                    └──────┬───────┘
                           │
                   insertCoin()
                           │
                           ▼
                  ┌─────────────────┐
                  │ HasMoney State  │ ◀─┐
                  └────────┬────────┘   │
                           │            │
                  insertCoin() (more)   │
                           │            │
                  clickProductSelection │
                           │            │
                           ▼            │
                  ┌─────────────────┐   │
                  │ Selection State │   │
                  └────────┬────────┘   │
                           │            │
                    selectProduct()     │
                           │            │
                           ▼            │
                  ┌─────────────────┐   │
                  │ Dispense State  │   │
                  └────────┬────────┘   │
                           │            │
                      dispenseItem()    │
                           │            │
                           ▼            │
                      ┌─────────┐       │
                      │  Reset  │───────┘
                      └─────────┘


          ┌────────────────────┐
          │ OutOfStock State   │ (from any state)
          └────────────────────┘
                    │
              refillInventory()
                    │
                    ▼
              Back to Idle
```

**State Transitions:**

| From State | Event | To State | Condition |
|------------|-------|----------|-----------|
| Idle | insertCoin() | HasMoney | coin inserted |
| HasMoney | insertCoin() | HasMoney | more coins |
| HasMoney | clickProductSelection() | Selection | user ready to select |
| Selection | selectProduct() | Dispense | valid code + sufficient funds |
| Dispense | dispenseItem() | Idle | item dispensed, change returned |
| Any | checkInventory() | OutOfStock | no items left |
| OutOfStock | refillInventory() | Idle | items added |

---

## **🔑 Key Design Decisions**

### **Decision 1: State Pattern vs If-Else**

**What:** Use State Pattern to manage machine behavior

**Why:**
1. **Open/Closed Principle**: Add new states without modifying existing code
2. **Single Responsibility**: Each state handles its own transitions
3. **Readability**: Clear state-specific behavior
4. **Testability**: Test each state independently

**The If-Else Alternative Would Be:**
```java
// Every method would have:
if (state == IDLE) { ... }
else if (state == HAS_MONEY) { ... }
else if (state == SELECTION) { ... }
else if (state == DISPENSE) { ... }
else if (state == OUT_OF_STOCK) { ... }

// Problems:
// 1. 5 states × 6 methods = 30+ if-else blocks!
// 2. Adding state means updating ALL methods
// 3. Hard to track which state allows which operation
// 4. Violates Open/Closed Principle
```

**Interview Question:**
> "What if we only had 2 states? Is State Pattern overkill?"

**Answer:**
> "Even with 2 states, State Pattern provides value: (1) Clear separation of concerns, (2) Easy to add 3rd state later (requirements always grow), (3) Each state is independently testable, (4) No magic strings or enums to maintain. The pattern's overhead is minimal compared to maintainability gains."

---

### **Decision 2: Context Object**

**What:** `VendingMachineContext` holds state and shared data

**Why:**
- States are stateless (no fields)
- Context holds: inventory, coinList, selectedItem
- States can access context to make decisions
- Enables state transition logic

```java
public class VendingMachineContext {
    private VendingMachineState currentState;
    private Inventory inventory;
    private List<Coin> coinList;
    private int selectedItemCode;

    public void advanceState() {
        VendingMachineState nextState = currentState.next(this);
        currentState = nextState;
    }
}
```

**Interview Question:**
> "Why not make states hold data like coinList?"

**Answer:**
> "States should be lightweight and potentially reusable. If IdleState held data, we'd need new instances every time. With stateless states and a context, we can reuse state objects or even make them singletons. Context acts as shared memory accessible to all states."

---

### **Decision 3: State Transition Responsibility**

**What:** Each state decides its next state

```java
public class IdleState implements VendingMachineState {
    @Override
    public VendingMachineState next(VendingMachineContext context) {
        if (!context.getInventory().hasItems()) {
            return new OutOfStockState();
        }
        if (!context.getCoinList().isEmpty()) {
            return new HasMoneyState();
        }
        return this;  // Stay in Idle
    }
}
```

**Why:**
- Each state knows its valid transitions
- Encapsulates transition logic with state behavior
- Context doesn't need to know transition rules

**Alternative Approach:**
```java
// ❌ Context controls transitions
public void advanceState() {
    if (currentState == IDLE && !coinList.isEmpty()) {
        currentState = new HasMoneyState();
    } else if (currentState == HAS_MONEY && selectionStarted) {
        currentState = new SelectionState();
    }
    // Context becomes bloated with transition logic!
}
```

---

### **Decision 4: Inventory as Separate Class**

**What:** `Inventory` manages items, not the state machine

**Why:**
- **Single Responsibility**: Inventory handles storage, state machine handles behavior
- **Reusability**: Inventory class can be reused in other systems
- **Testability**: Can test inventory logic independently
- **Separation of Concerns**: Data management ≠ state management

---

## **💡 SOLID Principles Applied**

### **S - Single Responsibility**
- `IdleState` - Only handles idle state behavior
- `HasMoneyState` - Only handles money-received behavior
- `SelectionState` - Only handles product selection
- `DispenseState` - Only handles dispensing
- `Inventory` - Only manages items
- `VendingMachineContext` - Coordinates states

### **O - Open/Closed**
- Adding new state (e.g., `MaintenanceState`):
  ```java
  public class MaintenanceState implements VendingMachineState {
      // New state without modifying existing states!
  }
  ```
- No modification to existing states needed

### **L - Liskov Substitution**
- Any `VendingMachineState` implementation can replace the interface
- Context works with any state implementation
- Polymorphism enables state transitions

### **I - Interface Segregation**
- `VendingMachineState` has only 2 methods: `getStateName()`, `next()`
- States don't depend on methods they don't use
- Minimal interface keeps implementations focused

### **D - Dependency Inversion**
- Context depends on `VendingMachineState` interface, not concrete states
- High-level module (Context) doesn't depend on low-level (concrete states)
- Both depend on abstraction (interface)

---

## **🎭 Scenario Walkthrough**

### **Scenario: Complete Purchase Flow**

```
Step 1: Initial State
─────────────────────
Machine: IdleState
Inventory: 50 items (Coke, Pepsi, Juice, Soda)
CoinList: []
Balance: $0

Step 2: User inserts $10 coin
─────────────────────────────
Action: vendingMachine.clickOnInsertCoinButton(TEN_RUPEES)
Context checks: currentState instanceof IdleState? ✓
Context: coinList.add(TEN_RUPEES)
Context: advanceState()
  IdleState.next() checks:
    - inventory.hasItems()? YES
    - coinList.isEmpty()? NO → return HasMoneyState
Machine: HasMoneyState
Balance: $10

Step 3: User inserts $5 coin
────────────────────────────
Action: vendingMachine.clickOnInsertCoinButton(FIVE_RUPEES)
Context checks: currentState instanceof HasMoneyState? ✓
Context: coinList.add(FIVE_RUPEES)
Context: advanceState()
  HasMoneyState.next() checks:
    - Still in HasMoneyState → return SelectionState
Machine: SelectionState
Balance: $15

Step 4: User clicks product selection button
─────────────────────────────────────────────
Action: vendingMachine.clickOnStartProductSelectionButton(102)
Context checks: currentState instanceof HasMoneyState? ✗
  (We're in SelectionState now!)
Message: "Product selection button can only be clicked in HasMoney state"
[This shows state validation working!]

Step 5: User selects product (code 102)
────────────────────────────────────────
Action: vendingMachine.selectProduct(102)
Context checks: currentState instanceof SelectionState? ✓
Context: item = inventory.getItem(102) → Coke, price=$12
Context: balance=$15, price=$12 → sufficient ✓
Context: setSelectedItemCode(102)
Context: advanceState()
  SelectionState.next() checks:
    - selectedItemCode > 0? YES → return DispenseState
Machine: DispenseState

Step 6: Dispense item
──────────────────────
Action: vendingMachine.dispenseItem(102)
Context checks: currentState instanceof DispenseState? ✓
Output: "Dispensing: COKE"
Context: inventory.removeItem(102)
Context: Change = $15 - $12 = $3
Output: "Returning change: $3"
Context: resetBalance() → coinList.clear()
Context: resetSelection() → selectedItemCode=0
Context: advanceState()
  DispenseState.next() → return IdleState
Machine: IdleState
Balance: $0

Transaction Complete! ✅
```

---

## **🚀 Extensions & Enhancements**

### **Easy to Add:**

#### **1. Refund/Cancel Mechanism**

```java
public class HasMoneyState implements VendingMachineState {
    // Add refund capability
    public void refundCoins(VendingMachineContext context) {
        int refundAmount = context.getBalance();
        System.out.println("Refunding: $" + refundAmount);
        context.resetBalance();
        return new IdleState();  // Back to idle
    }
}

// In Context:
public void clickCancelButton() {
    if (currentState instanceof HasMoneyState) {
        currentState.refundCoins(this);
        currentState = new IdleState();
    }
}
```

#### **2. Maintenance State**

```java
public class MaintenanceState implements VendingMachineState {
    @Override
    public String getStateName() {
        return "MaintenanceState";
    }

    @Override
    public VendingMachineState next(VendingMachineContext context) {
        // Only admin can exit maintenance
        if (context.isMaintenanceComplete()) {
            return new IdleState();
        }
        return this;
    }
}

// Context method:
public void enterMaintenanceMode() {
    currentState = new MaintenanceState();
    System.out.println("Entering maintenance mode");
}
```

#### **3. Product Expiry Tracking**

```java
public class Item {
    private ItemType type;
    private int price;
    private LocalDate expiryDate;  // NEW

    public boolean isExpired() {
        return LocalDate.now().isAfter(expiryDate);
    }
}

// In Inventory:
public Item getItem(int code) throws Exception {
    Item item = // get from shelf
    if (item.isExpired()) {
        removeItem(code);  // Auto-remove expired
        throw new Exception("Item expired");
    }
    return item;
}
```

#### **4. Multi-Currency Support**

```java
public enum Currency {
    USD(1.0),
    EUR(0.85),
    INR(83.0);

    private double exchangeRate;

    Currency(double rate) {
        this.exchangeRate = rate;
    }
}

public class Coin {
    private int value;
    private Currency currency;

    public int getValueInUSD() {
        return (int)(value / currency.exchangeRate);
    }
}
```

---

## **🎯 Interview Questions & Answers**

### **Q1: Why is State Pattern better than using an enum + switch?**

**Answer:**
```
Enum + Switch approach:
public enum State { IDLE, HAS_MONEY, SELECTION, DISPENSE }
private State currentState;

public void insertCoin(Coin coin) {
    switch(currentState) {
        case IDLE:
            // logic
            break;
        case HAS_MONEY:
            // logic
            break;
        // ... more cases
    }
}

Problems:
1. Every method has switch statement (code duplication)
2. Adding new state = updating EVERY switch
3. Violates Open/Closed Principle
4. Hard to test individual state logic
5. Transition logic scattered across methods

State Pattern:
1. Each state is a class (Single Responsibility)
2. Adding state = new class, no modification
3. Transition logic in state itself
4. Easy to unit test each state
5. No duplicate switch statements
6. Follows Open/Closed Principle

Conclusion: State Pattern wins for anything beyond 2-3 simple states.
```

### **Q2: How would you handle concurrent users?**

**Answer:**
```
Current Issue:
If two users access same machine simultaneously:
User A: Insert coin, select product
User B: Insert coin, select different product
→ Race condition! Both might get wrong product.

Solutions:

1. Simple: Lock entire machine
   synchronized(vendingMachine) {
       // All operations
   }
   Con: Poor throughput

2. Better: Transaction-based
   - Generate unique transactionId per user
   - Each transaction has own coinList, selection
   - Lock only during dispense

   public class Transaction {
       String id;
       List<Coin> coins;
       int selectedProduct;
   }

   Map<String, Transaction> activeTransactions;

3. Real-world: Physical machines don't have this issue!
   - One user at a time (physical access)
   - If we're simulating multiple machines, each is separate object
```

### **Q3: What if payment is insufficient?**

**Answer:**
```
Current flow:
1. User inserts $10
2. Selects $15 item
3. Check: balance < price? → Reject
4. Return to HasMoneyState (keep coins)

Better flow options:

Option A: Immediate validation
- Before entering SelectionState, check max affordable item
- If no items affordable, reject and return to IdleState

Option B: Show affordable items
public List<Item> getAffordableItems(int balance) {
    return inventory.getAllItems()
        .stream()
        .filter(item -> item.getPrice() <= balance)
        .collect(Collectors.toList());
}

Option C: Suggest additional amount needed
if (balance < price) {
    int needed = price - balance;
    System.out.println("Insert " + needed + " more");
    // Stay in HasMoneyState
}

Current implementation: Simple rejection (sufficient for MVP)
```

### **Q4: How to handle "sold out" for a specific item?**

**Answer:**
```
Current:
- OutOfStockState = entire machine empty
- Doesn't handle single item sold out

Better approach:

1. Check at selection time:
public void selectProduct(int code) {
    ItemShelf shelf = inventory.getShelf(code);
    if (shelf.checkIsSoldOut()) {
        System.out.println("Item sold out, select another");
        return;  // Stay in SelectionState
    }
    // Continue with purchase
}

2. Show only available items:
public List<Integer> getAvailableItemCodes() {
    return inventory.getShelves()
        .stream()
        .filter(shelf -> !shelf.checkIsSoldOut())
        .map(ItemShelf::getCode)
        .collect(Collectors.toList());
}

3. Recommend alternative:
if (selectedItemSoldOut) {
    Item alternative = findSimilarItem(selectedItem);
    System.out.println("Sold out. Try " + alternative.getCode());
}
```

### **Q5: How would you add a "discount" feature?**

**Answer:**
```
Approach 1: Discount State (not recommended)
- Adds too many states (IdleState, DiscountIdleState, etc.)
- State explosion problem

Approach 2: Decorator Pattern (recommended)
public interface PriceCalculator {
    int getPrice(Item item);
}

public class BasePriceCalculator implements PriceCalculator {
    public int getPrice(Item item) {
        return item.getPrice();
    }
}

public class DiscountDecorator implements PriceCalculator {
    private PriceCalculator wrapped;
    private double discount;

    public int getPrice(Item item) {
        int basePrice = wrapped.getPrice(item);
        return (int)(basePrice * (1 - discount));
    }
}

// Usage:
PriceCalculator calculator = new DiscountDecorator(
    new BasePriceCalculator(), 0.1  // 10% off
);

Approach 3: Strategy Pattern
public interface DiscountStrategy {
    int applyDiscount(int price);
}

// HappyHourDiscount, StudentDiscount, etc.
```

### **Q6: What if dispense mechanism fails?**

**Answer:**
```
Current: No failure handling

Better:

1. Add error state:
public class ErrorState implements VendingMachineState {
    private String errorMessage;

    @Override
    public VendingMachineState next(VendingMachineContext context) {
        // Admin must acknowledge error
        if (context.isErrorResolved()) {
            return new IdleState();
        }
        return this;
    }
}

2. Retry mechanism:
public void dispenseItem(int code) {
    int maxRetries = 3;
    for (int i = 0; i < maxRetries; i++) {
        try {
            mechanicalDispense(code);
            return;  // Success
        } catch (MechanicalException e) {
            if (i == maxRetries - 1) {
                // Failed all retries
                refundMoney();
                currentState = new ErrorState("Dispense failed");
            }
        }
    }
}

3. Logging & alerts:
public void dispenseItem(int code) {
    try {
        mechanicalDispense(code);
        logger.log("Dispensed: " + code);
    } catch (Exception e) {
        logger.error("Dispense failed: " + code);
        alertService.notifyAdmin(e);
        refundMoney();
    }
}
```

### **Q7: How to test state transitions?**

**Answer:**
```
Unit Testing Approach:

@Test
public void testIdleToHasMoneyTransition() {
    // Arrange
    VendingMachineContext context = new VendingMachineContext();
    context.clickOnInsertCoinButton(Coin.TEN_RUPEES);

    // Act
    VendingMachineState state = context.getCurrentState();

    // Assert
    assertTrue(state instanceof HasMoneyState);
    assertEquals(10, context.getBalance());
}

@Test
public void testInsufficientFunds() {
    // Arrange
    VendingMachineContext context = new VendingMachineContext();
    context.clickOnInsertCoinButton(Coin.FIVE_RUPEES);  // $5

    // Act
    context.clickOnStartProductSelectionButton(102);  // $12 item
    context.selectProduct(102);

    // Assert
    assertTrue(context.getCurrentState() instanceof SelectionState);
    assertEquals(5, context.getBalance());  // Money not consumed
}

@Test
public void testCompleteTransaction() {
    // Arrange
    VendingMachineContext context = setupMachineWithInventory();

    // Act
    context.clickOnInsertCoinButton(Coin.TEN_RUPEES);
    context.clickOnInsertCoinButton(Coin.FIVE_RUPEES);
    context.clickOnStartProductSelectionButton(102);
    context.selectProduct(102);
    context.dispenseItem(102);

    // Assert
    assertTrue(context.getCurrentState() instanceof IdleState);
    assertEquals(0, context.getBalance());
    assertEquals(4, context.getInventory().getItemCount(102));
}

Integration Testing:
- Test full flow: coin → selection → dispense → idle
- Test error paths: insufficient funds, sold out
- Test edge cases: exact change, overpayment
```

### **Q8: When would you NOT use State Pattern?**

**Answer:**
```
Don't use State Pattern when:

1. Only 2 simple states (Boolean flag sufficient)
   Example: Light (ON/OFF)
   private boolean isOn;  // Simpler than 2 state classes

2. States don't affect behavior
   Example: Tracking user status
   enum Status { ACTIVE, INACTIVE }
   // Just need to store status, not change behavior

3. State transitions are trivial
   Example: Next button (State1 → State2 → State3)
   private int currentStep;  // Simpler than 3 state classes

Use State Pattern when:
✓ 3+ states with complex transitions
✓ State affects behavior significantly
✓ Need to add states frequently
✓ Each state has substantial logic
✓ Transitions depend on multiple conditions

Vending Machine: Perfect candidate!
- 5 states
- Complex transitions
- Different behavior per state
- Likely to add more states (maintenance, etc.)
```

---

## **⚠️ Known Limitations & Trade-offs**

### **1. State Creation Overhead**
- Current: New state object on every transition
- Fix: Use Flyweight pattern or singleton states
  ```java
  public class StateFactory {
      private static final Map<Class, VendingMachineState> states = new HashMap<>();

      public static VendingMachineState getState(Class<? extends VendingMachineState> clazz) {
          return states.computeIfAbsent(clazz, k -> {
              try { return k.newInstance(); }
              catch (Exception e) { throw new RuntimeException(e); }
          });
      }
  }
  ```

### **2. No Persistent Storage**
- Current: In-memory only, lost on restart
- Fix: Add database layer with Repository pattern

### **3. No Transaction History**
- Current: No audit trail
- Fix: Add Observer pattern for logging
  ```java
  public interface VendingMachineObserver {
      void onCoinInserted(Coin coin);
      void onProductDispensed(Item item);
      void onStateChanged(VendingMachineState from, VendingMachineState to);
  }
  ```

### **4. Simplified Change Return**
- Current: Returns exact change amount (assumes unlimited denominations)
- Real-world: Limited coins of each denomination
- Fix: Add change dispenser with denomination tracking

### **5. No Multi-Machine Management**
- Current: Single machine
- Scale: Need central server to manage multiple machines

---

## **📚 Key Takeaways**

**Design Pattern:**
- ✅ State Pattern is the star (perfect fit for vending machines)
- ✅ Each state encapsulates its behavior and transitions
- ✅ Context coordinates states without knowing transition rules

**SOLID Principles:**
- ✅ All 5 principles demonstrated
- ✅ Open/Closed particularly well-demonstrated

**State Pattern Benefits:**
- ✅ Eliminates if-else soup
- ✅ Easy to add new states
- ✅ Clear state-specific behavior
- ✅ Testable in isolation

**Interview Focus:**
- State Pattern advantages over if-else/switch
- State transition logic
- Handling edge cases (insufficient funds, sold out)
- Concurrency considerations
- Extension points

---

## **🎓 What You Should Master**

Before interview, ensure you can:
1. ✅ Draw complete state diagram from memory
2. ✅ Explain why State Pattern > if-else (with examples)
3. ✅ Add a new state (MaintenanceState) in 3 minutes
4. ✅ Walk through complete transaction flow
5. ✅ Discuss at least 3 extensions (refund, expiry, discount)
6. ✅ Handle all 8 Q&A scenarios confidently
7. ✅ Explain when NOT to use State Pattern
8. ✅ Describe concurrency challenges and solutions

**Time to master:** 2-3 hours of practice

**Difficulty:** ⭐⭐⭐ (Medium - Excellent State Pattern example)

**Interview Frequency:** ⭐⭐⭐⭐ (Very common - classic State Pattern problem)

---

## **🔥 Pro Tips for Interview**

1. **Start with State Diagram**: Draw it first, shows you understand the problem
2. **Mention If-Else Alternative**: Show you know WHY State Pattern is better
3. **Talk About Extensions**: Shows systems thinking
4. **Discuss Trade-offs**: Shows maturity (when to use, when not to)
5. **Consider Concurrency**: Even if not asked, shows awareness

**Common Follow-up Questions:**
- "Add a refund button" → Show code in 2 minutes
- "How to handle multiple users?" → Discuss transactions/locking
- "What if dispense fails?" → Error handling strategy
- "Scale to 1000 machines" → Central management, monitoring

Master this, and you'll ace any State Pattern question! 💪
