# Design Inventory Management System - Comprehensive Solution 📦

## **Problem Statement**

Design an inventory management system that can:
- Manage multiple product categories (Electronics, Clothing, Grocery)
- Track stock levels across warehouses
- Support CRUD operations on inventory
- Automatically trigger replenishment when stock is low
- Handle different replenishment strategies (Just-In-Time, Bulk Order)
- Provide real-time inventory visibility
- Support batch operations for efficiency

---

## **🎯 Our Approach**

### **1. Core Requirements Analysis**

**Functional Requirements:**
- ✅ Add/Remove/Update products
- ✅ Track stock quantity per product
- ✅ Support multiple product categories
- ✅ Automatic low-stock detection
- ✅ Replenishment with different strategies
- ✅ Query inventory by category
- ✅ Batch operations for performance

**Non-Functional Requirements:**
- ✅ Extensible for new product types
- ✅ Easy to add new replenishment strategies
- ✅ Thread-safe for concurrent operations
- ✅ Scalable to multiple warehouses
- ✅ Real-time inventory updates

---

## **🏗️ Architecture & Design Patterns**

### **Pattern 1: Factory Pattern**

**Where:** Product Creation

**Why:**
- Centralize product instantiation logic
- Each product category may have specific initialization
- Easy to add new product types (Books, Furniture, etc.)
- Encapsulate product-specific attributes

**Implementation:**

```java
public abstract class Product {
    protected String productId;
    protected String name;
    protected ProductCategory category;
    protected int quantity;
    protected double price;

    public abstract String getProductDetails();
}

public class ElectronicsProduct extends Product {
    private String warrantyPeriod;

    public ElectronicsProduct(String id, String name, double price) {
        this.productId = id;
        this.name = name;
        this.category = ProductCategory.ELECTRONICS;
        this.price = price;
        this.warrantyPeriod = "1 year";
    }

    @Override
    public String getProductDetails() {
        return name + " [Electronics] - Warranty: " + warrantyPeriod;
    }
}

public class ProductFactory {
    public static Product createProduct(ProductCategory category,
                                       String id, String name, double price) {
        switch(category) {
            case ELECTRONICS:
                return new ElectronicsProduct(id, name, price);
            case CLOTHING:
                return new ClothingProduct(id, name, price);
            case GROCERY:
                return new GroceryProduct(id, name, price);
            default:
                throw new IllegalArgumentException("Unknown category");
        }
    }
}
```

**Benefits:**
- ✅ Single Responsibility - Factory handles creation
- ✅ Open/Closed - Add new products without modifying client code
- ✅ Hides complexity of product initialization

---

### **Pattern 2: Strategy Pattern**

**Where:** Replenishment Strategies

**Why:**
- Different business models need different ordering strategies
- Just-In-Time (JIT) - Order small quantities frequently
- Bulk Order - Order large quantities less often
- Easy to add seasonal strategies, predictive ML strategies

**Implementation:**

```java
public interface ReplenishmentStrategy {
    int calculateReorderQuantity(Product product, int currentStock);
    boolean shouldReorder(Product product, int currentStock);
}

public class JustInTimeStrategy implements ReplenishmentStrategy {
    private final int REORDER_THRESHOLD = 10;
    private final int REORDER_QUANTITY = 20;

    @Override
    public boolean shouldReorder(Product product, int currentStock) {
        return currentStock < REORDER_THRESHOLD;
    }

    @Override
    public int calculateReorderQuantity(Product product, int currentStock) {
        return REORDER_QUANTITY; // Small, frequent orders
    }
}

public class BulkOrderStrategy implements ReplenishmentStrategy {
    private final int REORDER_THRESHOLD = 50;
    private final int REORDER_QUANTITY = 500;

    @Override
    public boolean shouldReorder(Product product, int currentStock) {
        return currentStock < REORDER_THRESHOLD;
    }

    @Override
    public int calculateReorderQuantity(Product product, int currentStock) {
        return REORDER_QUANTITY; // Large, infrequent orders
    }
}
```

**Benefits:**
- ✅ Runtime flexibility - change strategy per product
- ✅ Testable - each strategy can be unit tested independently
- ✅ Extensible - add ML-based forecasting, seasonal adjustments

---

### **Pattern 3: Observer Pattern (Implicit)**

**Where:** Low Stock Alerts

**Why:**
- Multiple stakeholders need to know about low stock
- Inventory manager shouldn't know about all notification channels
- Easy to add email, SMS, dashboard notifications

**Current Implementation:**
```java
public class InventoryManager {
    public void updateStock(String productId, int quantity,
                           InventoryOperation operation) {
        // ... update logic ...

        if (product.getQuantity() < LOW_STOCK_THRESHOLD) {
            notifyLowStock(product); // Implicit observer
        }
    }
}
```

**Enhanced Implementation (Full Observer):**
```java
public interface InventoryObserver {
    void onLowStock(Product product);
    void onOutOfStock(Product product);
}

public class EmailNotificationObserver implements InventoryObserver {
    @Override
    public void onLowStock(Product product) {
        sendEmail("Low stock alert: " + product.getName());
    }
}

public class InventoryManager {
    private List<InventoryObserver> observers = new ArrayList<>();

    public void addObserver(InventoryObserver observer) {
        observers.add(observer);
    }

    private void notifyLowStock(Product product) {
        for (InventoryObserver observer : observers) {
            observer.onLowStock(product);
        }
    }
}
```

---

### **Pattern 4: Repository Pattern**

**Where:** Data Access Layer (Warehouse)

**Why:**
- Separate business logic from data access
- Easy to swap storage backend (in-memory → database)
- Centralize query logic

**Implementation:**

```java
public class Warehouse {
    private Map<String, Product> inventory = new HashMap<>();

    // CRUD Operations
    public void addProduct(Product product) {
        inventory.put(product.getProductId(), product);
    }

    public Product getProduct(String productId) {
        return inventory.get(productId);
    }

    public void removeProduct(String productId) {
        inventory.remove(productId);
    }

    public List<Product> getProductsByCategory(ProductCategory category) {
        return inventory.values().stream()
            .filter(p -> p.getCategory() == category)
            .collect(Collectors.toList());
    }
}
```

---

## **📐 Class Diagram Overview**

```
┌─────────────────────┐
│  InventoryManager   │ (Facade/Controller)
└──────────┬──────────┘
           │
    ┌──────┴─────────────────────────┐
    │                                │
┌───▼────────┐              ┌───────▼────────────┐
│  Warehouse │              │ReplenishmentStrategy│
│(Repository)│              │    (Interface)      │
└───┬────────┘              └───────┬────────────┘
    │                               │
    │                        ┌──────┴──────────┐
    │                        │                 │
    │                 JustInTimeStrategy  BulkOrderStrategy
    │
┌───▼────────────┐
│ProductFactory  │
└───┬────────────┘
    │
    │ creates
    │
┌───▼─────────┐
│   Product   │ (Abstract)
└───┬─────────┘
    │
    ├─ ElectronicsProduct
    ├─ ClothingProduct
    └─ GroceryProduct
```

---

## **🔑 Key Design Decisions**

### **Decision 1: Factory Pattern for Product Creation**

**What:** Use Factory instead of direct instantiation

**Why:**
- Each product type may need category-specific initialization
- Clothing needs size/color, Electronics needs warranty, Grocery needs expiry
- Client code shouldn't know about product-specific constructors

**Interview Question:**
> "Why not just use `new ElectronicsProduct()`?"

**Answer:**
> "Factory provides several benefits: (1) If product initialization becomes complex (e.g., validation, default values), it's centralized in one place. (2) Adding a new category (Books) requires one new class + one line in factory, no client code changes. (3) We can later add caching or object pooling in the factory transparently. The upfront cost is minimal for long-term maintainability."

---

### **Decision 2: Strategy Pattern for Replenishment**

**What:** Pluggable replenishment algorithms

**Why:**
- Different products need different strategies
  - Electronics: JIT (expensive, less storage)
  - Grocery: Bulk (perishable, high turnover)
  - Clothing: Seasonal (predict demand)
- Business rules change frequently
- Each strategy can be A/B tested

**Interview Question:**
> "Can't we just have a `reorderQuantity` field in Product?"

**Answer:**
> "That only handles quantity, not the decision logic. Replenishment strategies include: (1) When to reorder (threshold), (2) How much to reorder, (3) Where to reorder from (supplier selection), (4) Demand forecasting (ML models). Strategy Pattern allows complex, testable logic per product type. A simple field would lead to if-else soup in the manager class."

---

### **Decision 3: Separation of Warehouse and InventoryManager**

**What:** Split storage from business logic

**Warehouse:**
- CRUD operations (add, get, remove)
- Data persistence
- Query operations

**InventoryManager:**
- Business rules (low stock detection)
- Replenishment orchestration
- Transaction management

**Why:**
- Single Responsibility - each class has one reason to change
- Testable - can mock Warehouse for testing InventoryManager
- Swappable storage - can replace HashMap with database

**Interview Question:**
> "Why not combine them into one InventoryService?"

**Answer:**
> "They have different responsibilities and change for different reasons. Warehouse changes when we switch from in-memory to database. InventoryManager changes when business rules change (new threshold, different replenishment logic). Combining them violates SRP and makes testing harder. We'd need a real database to test business logic."

---

### **Decision 4: Enum for Categories vs String**

**What:** Use `enum ProductCategory` instead of `String`

```java
// ✅ Good
public enum ProductCategory {
    ELECTRONICS, CLOTHING, GROCERY
}

// ❌ Bad
String category = "electronics"; // Typo-prone, no compile-time safety
```

**Why:**
- Type safety - compiler catches typos
- IDE autocomplete - developer productivity
- Exhaustive switch - compiler ensures all cases handled
- Cannot pass invalid values

---

## **💡 SOLID Principles Applied**

### **S - Single Responsibility**
- `ProductFactory` - Only creates products
- `Warehouse` - Only manages storage
- `InventoryManager` - Only enforces business rules
- `ReplenishmentStrategy` - Only calculates reorder logic

### **O - Open/Closed**
- Adding new product type: Create subclass, update factory
- Adding new replenishment strategy: Create new strategy class
- No modification to existing code

### **L - Liskov Substitution**
- Any `Product` subclass works wherever `Product` is expected
- Any `ReplenishmentStrategy` can replace another
- Polymorphism works correctly

### **I - Interface Segregation**
- `ReplenishmentStrategy` - Only 2 methods (focused interface)
- Clients depend only on methods they use

### **D - Dependency Inversion**
- `InventoryManager` depends on `ReplenishmentStrategy` interface, not concrete strategies
- Can inject any strategy implementation at runtime

---

## **🎭 Scenario Walkthrough**

### **Scenario: Add Product and Auto-Replenishment**

```
1. Create Product using Factory
   │
   ProductFactory.createProduct(ELECTRONICS, "E001", "Laptop", 999.99)
   │
   → ElectronicsProduct instance created

2. Add to Warehouse
   │
   warehouse.addProduct(product)
   │
   → Stored in HashMap

3. Set Replenishment Strategy
   │
   inventoryManager.setReplenishmentStrategy(new JustInTimeStrategy())
   │
   → Strategy configured

4. Stock Decreases (Sales)
   │
   inventoryManager.updateStock("E001", -5, REMOVE)
   │
   → Quantity: 15 → 10

5. Check if Reorder Needed
   │
   if (strategy.shouldReorder(product, 10)) { // threshold = 10
       int qty = strategy.calculateReorderQuantity(...); // qty = 20
       // Trigger purchase order
   }
   │
   → Low stock detected! Order 20 units

6. Notify Observers (if implemented)
   │
   notifyLowStock(product)
   │
   → Email sent to procurement team
```

---

## **🚀 Extensions & Enhancements**

### **1. Full Observer Pattern**

```java
public interface InventoryObserver {
    void onLowStock(Product product);
    void onOutOfStock(Product product);
    void onRestocked(Product product);
}

public class ProcurementObserver implements InventoryObserver {
    @Override
    public void onLowStock(Product product) {
        createPurchaseOrder(product);
    }
}

public class DashboardObserver implements InventoryObserver {
    @Override
    public void onLowStock(Product product) {
        updateDashboard("Low Stock: " + product.getName());
    }
}
```

### **2. Batch Operations**

```java
public class InventoryManager {
    // Instead of updating one at a time
    public void updateStockBatch(List<StockUpdate> updates) {
        // Group by product
        Map<String, Integer> aggregated = new HashMap<>();

        for (StockUpdate update : updates) {
            aggregated.merge(update.productId, update.quantity, Integer::sum);
        }

        // Single transaction
        for (Map.Entry<String, Integer> entry : aggregated.entrySet()) {
            updateStock(entry.getKey(), entry.getValue(), ADD);
        }
    }
}
```

### **3. Multi-Warehouse Support**

```java
public class WarehouseNetwork {
    private Map<String, Warehouse> warehouses = new HashMap<>();

    public void transferStock(String productId,
                             String fromWarehouse,
                             String toWarehouse,
                             int quantity) {
        Warehouse source = warehouses.get(fromWarehouse);
        Warehouse dest = warehouses.get(toWarehouse);

        // Atomic transfer
        synchronized(this) {
            source.removeStock(productId, quantity);
            dest.addStock(productId, quantity);
        }
    }

    public int getTotalStock(String productId) {
        return warehouses.values().stream()
            .mapToInt(w -> w.getStock(productId))
            .sum();
    }
}
```

### **4. Predictive Replenishment with ML**

```java
public class MLPredictiveStrategy implements ReplenishmentStrategy {
    private MLModel demandForecast;

    @Override
    public int calculateReorderQuantity(Product product, int currentStock) {
        // Predict demand for next 30 days
        int predictedDemand = demandForecast.predict(
            product.getHistoricalSales(),
            product.getSeasonality(),
            upcomingHolidays()
        );

        // Safety stock (buffer)
        int safetyStock = (int)(predictedDemand * 0.2);

        return predictedDemand + safetyStock - currentStock;
    }
}
```

---

## **🎯 Interview Questions & Answers**

### **Q1: How would you handle stock reservation for pending orders?**

**Answer:**
```
Add reservation layer:

public class Warehouse {
    private Map<String, Product> inventory;
    private Map<String, Integer> reserved; // productId → reserved qty

    public boolean reserveStock(String productId, int quantity) {
        Product product = inventory.get(productId);
        int available = product.getQuantity() - reserved.getOrDefault(productId, 0);

        if (available >= quantity) {
            reserved.merge(productId, quantity, Integer::sum);
            return true;
        }
        return false;
    }

    public void commitReservation(String reservationId) {
        // Actually deduct from inventory
        int qty = reservations.get(reservationId);
        updateStock(productId, -qty, REMOVE);
        reserved.merge(productId, -qty, Integer::sum);
    }

    public void releaseReservation(String reservationId) {
        // Timeout or cancellation
        reserved.merge(productId, -qty, Integer::sum);
    }
}

Benefits:
- Prevents overselling
- Handles pending orders
- Supports order timeouts
```

**Follow-up:** What if reservation expires?
```
Use ScheduledExecutorService:

scheduler.schedule(() -> {
    releaseReservation(reservationId);
}, 10, TimeUnit.MINUTES);
```

---

### **Q2: How to handle concurrent stock updates?**

**Answer:**
```
Three approaches:

1. Pessimistic Locking (Database):
   UPDATE products
   SET quantity = quantity - ?
   WHERE product_id = ?
   AND quantity >= ?  -- Atomic check

   if (rowsAffected == 0) {
       throw new InsufficientStockException();
   }

2. Optimistic Locking (Version field):
   public class Product {
       private int version;
   }

   UPDATE products
   SET quantity = ?, version = version + 1
   WHERE product_id = ? AND version = ?

   if (rowsAffected == 0) {
       throw new ConcurrentModificationException();
       // Retry
   }

3. In-Memory (Synchronized):
   public synchronized void updateStock(String productId, int delta) {
       Product p = inventory.get(productId);
       if (p.getQuantity() + delta < 0) {
           throw new InsufficientStockException();
       }
       p.setQuantity(p.getQuantity() + delta);
   }

Recommendation: Use pessimistic for critical operations (sales),
optimistic for reporting, synchronization for in-memory prototypes.
```

---

### **Q3: How would you implement stock forecasting?**

**Answer:**
```
Multi-layer approach:

1. Historical Sales Analysis:
   - Moving average (30-day, 90-day)
   - Seasonal trends (holiday spikes)
   - Year-over-year growth

2. External Factors:
   - Upcoming holidays
   - Marketing campaigns
   - Competitor pricing
   - Economic indicators

3. ML Models:
   - Time series forecasting (ARIMA, Prophet)
   - Features: day-of-week, month, promotions
   - Training data: 2+ years of sales

Implementation:
public class ForecastingService {
    public Forecast predictDemand(String productId, int days) {
        HistoricalSales history = getHistory(productId);

        // Simple moving average as baseline
        double avgDailySales = history.averageDailySales(30);

        // Seasonal adjustment
        double seasonalFactor = getSeasonalFactor(LocalDate.now());

        // Predicted demand
        double predictedDaily = avgDailySales * seasonalFactor;
        int totalDemand = (int)(predictedDaily * days);

        return new Forecast(totalDemand, confidenceInterval);
    }
}

Metrics to track:
- Forecast accuracy (MAPE - Mean Absolute Percentage Error)
- Stock-out incidents
- Overstock costs
```

---

### **Q4: How to implement distributed inventory across data centers?**

**Answer:**
```
Challenges:
1. Consistency - How to keep inventory in sync?
2. Availability - What if DC goes down?
3. Partition tolerance - Network split between DCs

Solutions:

1. Eventually Consistent (AP from CAP):
   - Each DC has local inventory
   - Async replication between DCs
   - Use CRDT (Conflict-free Replicated Data Types)
   - Accept temporary inconsistencies

   Example:
   DC1: Sold 5 units (qty: 100 → 95)
   DC2: Sold 3 units (qty: 100 → 97)

   After sync: qty = 100 - 5 - 3 = 92 ✓

2. Strongly Consistent (CP from CAP):
   - Single source of truth (master DC)
   - Distributed transactions (2PC - Two-Phase Commit)
   - High latency, but always consistent

   Example:
   DC1: Lock inventory → Check quantity → Deduct → Commit
   DC2: Waits for DC1 lock to release

3. Hybrid (Recommended):
   - Critical operations: Strong consistency (purchases)
   - Read operations: Eventual consistency (browsing)
   - Regional inventory with global overflow

   public class DistributedInventory {
       private Warehouse localWarehouse;
       private List<Warehouse> remoteWarehouses;

       public boolean checkAvailability(String productId, int qty) {
           // Fast local check (eventual)
           int local = localWarehouse.getStock(productId);
           if (local >= qty) return true;

           // Check remote (may be stale)
           int global = getTotalStock(productId);
           return global >= qty;
       }

       public void reserveStock(String productId, int qty) {
           // Distributed transaction (strong)
           try {
               distributedLock.acquire(productId);
               // 2PC across warehouses
               preparePhase();
               commitPhase();
           } finally {
               distributedLock.release(productId);
           }
       }
   }

Technologies:
- Redis for distributed locking
- Kafka for event streaming (inventory updates)
- DynamoDB or Cassandra for multi-region storage
```

---

### **Q5: How to handle bulk import of 1 million products?**

**Answer:**
```
Performance optimizations:

1. Batch Processing:
   Instead of: 1M individual inserts (slow)
   Do: Batches of 1000 (fast)

   public void bulkImport(List<Product> products) {
       int BATCH_SIZE = 1000;

       for (int i = 0; i < products.size(); i += BATCH_SIZE) {
           List<Product> batch = products.subList(i,
               Math.min(i + BATCH_SIZE, products.size()));

           // Single database transaction
           insertBatch(batch);
       }
   }

2. Parallel Processing:
   ExecutorService executor = Executors.newFixedThreadPool(10);

   List<Future<?>> futures = new ArrayList<>();
   for (List<Product> batch : batches) {
       futures.add(executor.submit(() -> insertBatch(batch)));
   }

   // Wait for all
   for (Future<?> f : futures) {
       f.get();
   }

3. Database Optimizations:
   - Disable indexes during import
   - Use COPY/LOAD DATA for CSV
   - Increase batch size for database

   BEGIN TRANSACTION;
   COPY products FROM 'file.csv' WITH CSV HEADER;
   COMMIT;

4. Validation Strategy:
   - Pre-validate file format (fail fast)
   - Validate in parallel with import
   - Log errors, don't fail entire import

   public BulkImportResult importProducts(File csv) {
       List<Product> valid = new ArrayList<>();
       List<String> errors = new ArrayList<>();

       for (String line : readLines(csv)) {
           try {
               Product p = parseProduct(line);
               validateProduct(p);
               valid.add(p);
           } catch (ValidationException e) {
               errors.add("Line " + lineNum + ": " + e.getMessage());
           }
       }

       insertBatch(valid);
       return new BulkImportResult(valid.size(), errors);
   }

5. Progress Tracking:
   - Use CompletableFuture for async with progress
   - Update progress bar
   - Support pause/resume

   AtomicInteger progress = new AtomicInteger(0);

   batches.parallelStream().forEach(batch -> {
       insertBatch(batch);
       progress.addAndGet(batch.size());
       updateProgress(progress.get(), total);
   });

Performance:
- Single inserts: 1M products = 1000 seconds (1000/sec)
- Batched: 1M products = 100 seconds (10,000/sec)
- Parallel batched: 1M products = 20 seconds (50,000/sec)
```

---

### **Q6: How to implement audit trail for inventory changes?**

**Answer:**
```
Track who, what, when, why for compliance:

1. Event Sourcing Approach:
   public class InventoryEvent {
       private String eventId;
       private String productId;
       private InventoryOperation operation; // ADD, REMOVE, ADJUST
       private int quantity;
       private int previousQuantity;
       private int newQuantity;
       private String userId;
       private Instant timestamp;
       private String reason;
       private String source; // "SALE", "RETURN", "ADJUSTMENT", "THEFT"
   }

   public class InventoryManager {
       private List<InventoryEvent> eventLog = new ArrayList<>();

       public void updateStock(String productId, int delta,
                              String userId, String reason) {
           Product p = warehouse.getProduct(productId);
           int oldQty = p.getQuantity();
           int newQty = oldQty + delta;

           // Record event BEFORE change
           InventoryEvent event = new InventoryEvent(
               UUID.randomUUID(),
               productId,
               delta > 0 ? ADD : REMOVE,
               Math.abs(delta),
               oldQty,
               newQty,
               userId,
               Instant.now(),
               reason
           );
           eventLog.add(event);

           // Apply change
           p.setQuantity(newQty);

           // Persist event to database
           eventRepository.save(event);
       }

       // Audit queries
       public List<InventoryEvent> getHistory(String productId) {
           return eventLog.stream()
               .filter(e -> e.getProductId().equals(productId))
               .collect(Collectors.toList());
       }

       public List<InventoryEvent> getAdjustmentsByUser(String userId) {
           return eventLog.stream()
               .filter(e -> e.getUserId().equals(userId))
               .filter(e -> e.getSource().equals("ADJUSTMENT"))
               .collect(Collectors.toList());
       }
   }

2. Benefits:
   - Complete audit trail (regulatory compliance)
   - Can reconstruct inventory state at any point in time
   - Detect fraud or errors
   - Analytics on inventory movements

3. Queries enabled:
   - "What was stock of product X on date Y?"
   - "Who adjusted inventory for product Z?"
   - "Show all theft-related deductions this month"
   - "Calculate shrinkage (unexplained inventory loss)"

4. Performance consideration:
   - Event log grows forever (archive old events)
   - Use time-series database (InfluxDB, TimescaleDB)
   - Snapshot current state periodically to avoid full replay
```

---

### **Q7: How to handle returns and damaged goods?**

**Answer:**
```
Add product quality states:

public enum ProductCondition {
    NEW,           // Sellable
    LIKE_NEW,      // Refurbished, sellable
    DAMAGED,       // Not sellable, repairable
    DEFECTIVE,     // Not sellable, disposal
    RETURNED       // Pending inspection
}

public class Product {
    private int quantityNew;
    private int quantityReturned;
    private int quantityDamaged;

    public int getAvailableQuantity() {
        return quantityNew + quantityLikeNew;
    }
}

public class InventoryManager {
    public void processReturn(String productId, int quantity) {
        Product p = warehouse.getProduct(productId);

        // Move to returned (quarantine)
        p.setQuantityReturned(p.getQuantityReturned() + quantity);

        // Schedule inspection
        inspectionQueue.add(new InspectionTask(productId, quantity));
    }

    public void inspectReturns(String productId) {
        Product p = warehouse.getProduct(productId);
        int returned = p.getQuantityReturned();

        // Inspection results (could be manual or automated)
        InspectionResult result = performInspection(productId);

        // Categorize
        p.setQuantityNew(p.getQuantityNew() + result.resellable);
        p.setQuantityDamaged(p.getQuantityDamaged() + result.damaged);
        p.setQuantityReturned(0);

        // Trigger replenishment if needed
        checkReplenishment(p);
    }
}

Flows:
1. Customer returns item
   → quantityReturned++
   → Available quantity doesn't change yet

2. Inspection
   → Resellable: quantityNew++, quantityReturned--
   → Damaged: quantityDamaged++, quantityReturned--

3. Damaged item repair
   → After repair: quantityNew++, quantityDamaged--
   → Unrepairable: dispose, quantityDamaged--
```

---

## **⚠️ Known Limitations & Trade-offs**

1. **No Persistence Layer**
   - Current: In-memory HashMap
   - Fix: Add JPA/Hibernate, PostgreSQL/MySQL
   - Trade-off: Complexity vs data durability

2. **No Multi-Warehouse Support**
   - Current: Single warehouse assumed
   - Fix: Add WarehouseNetwork class (shown in extensions)
   - Trade-off: Simple vs distributed complexity

3. **Simplistic Replenishment Logic**
   - Current: Fixed thresholds
   - Fix: ML-based forecasting, seasonal adjustments
   - Trade-off: Predictable vs optimal

4. **No Transaction Management**
   - Current: No rollback on partial failures
   - Fix: Use Spring @Transactional or manual rollback
   - Trade-off: Simpler code vs data consistency

5. **No Audit Trail**
   - Current: No history of changes
   - Fix: Event sourcing (shown in Q6)
   - Trade-off: Storage cost vs compliance

6. **No Access Control**
   - Current: Anyone can update inventory
   - Fix: Add authentication, authorization
   - Trade-off: Ease of use vs security

---

## **📚 Key Takeaways**

**Design Patterns Used:**
- ✅ Factory Pattern (Product creation)
- ✅ Strategy Pattern (Replenishment algorithms)
- ✅ Observer Pattern (Low stock notifications)
- ✅ Repository Pattern (Data access)

**SOLID Principles:**
- ✅ All 5 principles demonstrated
- ✅ Clear separation of concerns

**Extensibility:**
- ✅ Easy to add new product types
- ✅ Easy to add new replenishment strategies
- ✅ Easy to add new notification channels

**Interview Focus Points:**
- Factory vs direct instantiation
- Strategy benefits over if-else
- Concurrent stock updates (race conditions)
- Distributed inventory challenges
- Bulk operations optimization
- Audit trail for compliance

---

## **🎓 What You Should Master**

Before interview, ensure you can:
1. ✅ Explain Factory Pattern benefits for product creation
2. ✅ Implement a new ReplenishmentStrategy in 3 minutes
3. ✅ Discuss concurrency issues and solutions (optimistic vs pessimistic locking)
4. ✅ Design distributed inventory system (CAP theorem trade-offs)
5. ✅ Implement Observer Pattern for notifications
6. ✅ Optimize bulk import of 1M products
7. ✅ Design audit trail with event sourcing
8. ✅ Handle edge cases (returns, damaged goods, reservations)
9. ✅ Scale to multiple warehouses with stock transfers
10. ✅ Answer all Q&A sections confidently

**Practice Exercises:**
1. Add a new product type (Books) with ISBN field
2. Implement ML-based replenishment strategy
3. Add email/SMS observers for low stock alerts
4. Implement stock reservation for 10-minute timeout
5. Design database schema for persistence
6. Write unit tests for concurrent stock updates
7. Implement batch operations for performance

**Time to master:** 2-3 hours of practice

**Difficulty:** ⭐⭐ (Easy-Medium)

**Interview Frequency:** ⭐⭐⭐ (Common, especially for e-commerce companies)

---

## **💡 Pro Tips for Interview**

1. **Start simple** - Begin with single warehouse, expand to distributed
2. **Mention trade-offs** - Always discuss pros/cons of design decisions
3. **Think production** - Bring up monitoring, alerting, audit trails
4. **Show extensibility** - Demonstrate how to add new features without breaking existing code
5. **Consider scale** - Discuss how system handles 1M products, 1000 warehouses
6. **Security mindset** - Mention authentication, authorization, audit logs
7. **Performance aware** - Discuss batch operations, caching, database indexes

**Common Follow-ups:**
- "How would you add multi-currency support?"
- "Design an analytics dashboard for inventory trends"
- "How to handle inventory sync between online and physical stores?"
- "Implement automatic reordering with supplier integration"
- "Design a dead stock identification system"

Ready to ace the Inventory Management System interview! 📦🚀
