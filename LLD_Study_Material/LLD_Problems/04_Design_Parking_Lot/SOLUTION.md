# Design Parking Lot - Comprehensive Solution 🚗

## **Problem Statement**

Design a parking lot system that can:
- Handle multiple vehicle types (Car, Bike, Others)
- Support different parking spot types
- Calculate fees based on different strategies
- Process payments through multiple methods
- Track entry/exit times
- Find available spots efficiently

---

## **🎯 Our Approach**

### **1. Core Requirements Analysis**

**Functional Requirements:**
- ✅ Park vehicle (assign spot)
- ✅ Unpark vehicle (free spot, calculate fee)
- ✅ Support multiple vehicle types
- ✅ Different pricing strategies
- ✅ Multiple payment methods
- ✅ Track occupied/available spots

**Non-Functional Requirements:**
- ✅ Extensible for new vehicle types
- ✅ Easy to add new pricing strategies
- ✅ Support new payment methods
- ✅ Thread-safe (future enhancement)

---

## **🏗️ Architecture & Design Patterns**

### **Pattern 1: Strategy Pattern (Used 2x)**

**Where:** Pricing & Payment

**Why:**
- Different pricing algorithms (hourly, premium, weekend)
- Different payment methods (cash, card, UPI)
- Easy to add new strategies without modifying existing code

**Implementation:**

```java
// Pricing Strategy
public interface ParkingFeeStrategy {
    double calculateFee(long duration, DurationType durationType);
}

public class BasicHourlyRateStrategy implements ParkingFeeStrategy {
    @Override
    public double calculateFee(long duration, DurationType durationType) {
        return duration * 10.0; // $10/hour
    }
}

public class PremiumRateStrategy implements ParkingFeeStrategy {
    @Override
    public double calculateFee(long duration, DurationType durationType) {
        return duration * 20.0; // $20/hour premium
    }
}
```

**Benefits:**
- ✅ Open/Closed Principle - Open for extension, closed for modification
- ✅ Easy to add dynamic pricing (surge pricing, holidays)
- ✅ Each strategy is independent and testable

---

### **Pattern 2: Factory Pattern**

**Where:** Vehicle Creation

**Why:**
- Centralize vehicle object creation
- Hide complexity of vehicle instantiation
- Easy to add new vehicle types

**Implementation:**

```java
public class VehicleFactory {
    public static Vehicle createVehicle(String type, String licensePlate) {
        switch(type.toLowerCase()) {
            case "car":
                return new CarVehicle(licensePlate);
            case "bike":
                return new BikeVehicle(licensePlate);
            case "other":
                return new OtherVehicle(licensePlate);
            default:
                throw new IllegalArgumentException("Unknown vehicle type");
        }
    }
}
```

**Benefits:**
- ✅ Single Responsibility - Factory handles creation logic
- ✅ Client code doesn't need to know concrete classes
- ✅ Easy to add new vehicle types (truck, bus, etc.)

---

### **Pattern 3: Abstract Class (Template Method-like)**

**Where:** ParkingSpot hierarchy

**Why:**
- Common behavior in base class
- Specific behavior in subclasses
- Type safety for different spot types

**Implementation:**

```java
public abstract class ParkingSpot {
    private String spotId;
    private boolean isOccupied;
    private Vehicle currentVehicle;

    public abstract String getSpotType();

    public boolean assignVehicle(Vehicle vehicle) {
        if (!isOccupied) {
            this.currentVehicle = vehicle;
            this.isOccupied = true;
            return true;
        }
        return false;
    }
}

public class CarParkingSpot extends ParkingSpot {
    @Override
    public String getSpotType() {
        return "CAR";
    }
}
```

---

## **📐 Class Diagram Overview**

```
┌─────────────────┐
│   ParkingLot    │ (Controller/Facade)
└────────┬────────┘
         │
    ┌────┴────────────────────┐
    │                         │
┌───▼──────────┐      ┌──────▼──────────┐
│ ParkingSpot  │      │     Vehicle     │
│  (Abstract)  │      │    (Abstract)   │
└───┬──────────┘      └──────┬──────────┘
    │                        │
    ├─CarParkingSpot        ├─CarVehicle
    ├─BikeParkingSpot       ├─BikeVehicle
    └─(Other spots)         └─OtherVehicle

┌──────────────────────┐     ┌────────────────────┐
│ ParkingFeeStrategy   │     │  PaymentStrategy   │
│    (Interface)       │     │    (Interface)     │
└──────┬───────────────┘     └────────┬───────────┘
       │                              │
       ├─BasicHourlyRate              ├─CashPayment
       ├─PremiumRate                  ├─CreditCardPayment
       └─(Future strategies)          └─(Future methods)
```

---

## **🔑 Key Design Decisions**

### **Decision 1: Separation of Concerns**

**What:** Split into packages by responsibility
- `ParkingLotController/` - Main logic
- `ParkingSpots/` - Spot management
- `VehicleFactoryPattern/` - Vehicle creation
- `FareStrategyPattern/` - Pricing
- `PaymentStrategyPattern/` - Payment processing

**Why:**
- High cohesion, low coupling
- Easy to locate and modify code
- Independent testing of modules

**Interview Question:**
> "Why did you separate pricing and payment strategies?"

**Answer:**
> "They have different reasons to change. Pricing might change based on business rules (holidays, demand), while payment methods change based on integrations (new payment gateways). Separating them follows Single Responsibility Principle and makes the system more maintainable."

---

### **Decision 2: Strategy Pattern over If-Else**

**What:** Use polymorphism instead of conditionals

```java
// ❌ Bad: Hard-coded if-else
public double calculateFee(String type, long duration) {
    if (type.equals("basic")) {
        return duration * 10;
    } else if (type.equals("premium")) {
        return duration * 20;
    } else if (type.equals("weekend")) {
        return duration * 15;
    }
    // Adding new type requires modifying this method!
}

// ✅ Good: Strategy pattern
ParkingFeeStrategy strategy = getStrategy(type);
double fee = strategy.calculateFee(duration, durationType);
// Adding new strategy = new class, no modification!
```

**Why:**
- Violates Open/Closed Principle
- Hard to test individual strategies
- Becomes unmaintainable with many types

**Interview Question:**
> "What if we only have 2 payment types? Is Strategy overkill?"

**Answer:**
> "Even with 2 types, Strategy provides benefits: (1) Each payment method might have complex logic (validation, API calls), (2) Testing is easier with separate classes, (3) When product says 'add UPI', we just add a new class without touching existing code. The upfront cost is minimal for long-term maintainability."

---

### **Decision 3: Abstract vs Interface for ParkingSpot**

**What:** Used abstract class instead of interface

**Why:**
- Need to share common state (spotId, isOccupied, currentVehicle)
- Need to share common behavior (assignVehicle, removeVehicle)
- Only spotType varies by subclass

**Interview Question:**
> "Why not use interface with default methods (Java 8+)?"

**Answer:**
> "Default methods can't have state. ParkingSpot needs shared fields like isOccupied, currentVehicle. Abstract class is the right choice when we need both shared state and behavior. If we only had behavior differences, interface would work."

---

### **Decision 4: Composition over Inheritance**

**What:** ParkingLot *has-a* FeeStrategy, not *is-a*

```java
public class ParkingLot {
    private ParkingFeeStrategy feeStrategy; // Composition

    public void setFeeStrategy(ParkingFeeStrategy strategy) {
        this.feeStrategy = strategy;
    }
}
```

**Why:**
- Runtime flexibility - can change strategy dynamically
- A parking lot isn't a "kind of" strategy
- Follows "favor composition over inheritance"

---

## **💡 SOLID Principles Applied**

### **S - Single Responsibility**
- `ParkingLot` - Manages spots and vehicles
- `ParkingFeeStrategy` - Only calculates fees
- `PaymentStrategy` - Only processes payments
- Each class has ONE reason to change

### **O - Open/Closed**
- Adding new vehicle type: Create new subclass, no modification
- Adding new pricing: Create new strategy, no modification
- Adding payment method: Create new strategy, no modification

### **L - Liskov Substitution**
- Any `ParkingSpot` subclass can replace base class
- Any `Vehicle` subclass can replace base class
- Polymorphism works correctly

### **I - Interface Segregation**
- `ParkingFeeStrategy` - Only fee calculation
- `PaymentStrategy` - Only payment processing
- Clients depend on minimal interfaces

### **D - Dependency Inversion**
- `ParkingLot` depends on `ParkingFeeStrategy` interface, not concrete strategies
- High-level modules don't depend on low-level implementations

---

## **🎭 Scenario Walkthrough**

### **Scenario: Park a Car**

```
1. User arrives with Car (license: ABC-123)
   │
2. VehicleFactory.createVehicle("car", "ABC-123")
   │
3. ParkingLot.parkVehicle(vehicle)
   │
4. Find available CarParkingSpot
   │
5. Assign vehicle to spot
   │
6. Record entry time
   │
7. Return ticket with spot ID
```

### **Scenario: Calculate Fee & Pay**

```
1. User returns after 3 hours
   │
2. ParkingLot.unparkVehicle(spotId)
   │
3. Calculate duration = exitTime - entryTime = 3 hours
   │
4. feeStrategy.calculateFee(3, HOURS)
   │   (uses PremiumRateStrategy: 3 * 20 = $60)
   │
5. User selects payment method (Credit Card)
   │
6. paymentStrategy.processPayment(60)
   │   (processes credit card payment)
   │
7. Release spot, mark available
   │
8. Return receipt
```

---

## **🚀 Extensions & Enhancements**

### **Easy to Add:**

1. **New Vehicle Type (Truck)**
```java
public class TruckVehicle extends Vehicle {
    public TruckVehicle(String licensePlate) {
        super(licensePlate, "TRUCK");
    }
}

public class TruckParkingSpot extends ParkingSpot {
    @Override
    public String getSpotType() { return "TRUCK"; }
}
```

2. **Surge Pricing Strategy**
```java
public class SurgePricingStrategy implements ParkingFeeStrategy {
    @Override
    public double calculateFee(long duration, DurationType type) {
        double baseFee = duration * 10.0;
        if (isPeakHour()) {
            return baseFee * 1.5; // 50% surge
        }
        return baseFee;
    }
}
```

3. **Monthly Pass Payment**
```java
public class MonthlyPassPayment implements PaymentStrategy {
    @Override
    public boolean processPayment(double amount) {
        // Check if user has valid monthly pass
        return validatePass(userId);
    }
}
```

---

## **🎯 Interview Questions & Answers**

### **Q1: How would you handle multiple floors?**

**Answer:**
```
Add Floor abstraction:
- ParkingLot contains List<Floor>
- Each Floor contains List<ParkingSpot>
- findAvailableSpot() searches across floors
- Consider: Nearest floor to entrance, elevator optimization
```

### **Q2: How to handle reserved parking?**

**Answer:**
```
Add ReservationSystem:
- New class: Reservation(userId, spotId, startTime, endTime)
- Before assigning spot, check if reserved
- Add method: ParkingLot.reserveSpot(...)
- Could use Observer pattern to notify when reservation expires
```

### **Q3: What if payment fails?**

**Answer:**
```
Current: Payment happens at exit
Better:
1. Pre-authorize payment at entry (hold amount)
2. If payment fails at exit, mark vehicle as "payment pending"
3. Don't open exit gate until payment succeeds
4. Add penalty fee for delayed payment
5. Log failed payments for manual follow-up
```

### **Q4: How to make it thread-safe?**

**Answer:**
```
Concurrency issues:
1. Two cars assigned to same spot (race condition)
2. Fee calculation during state change

Solutions:
1. Synchronize spot assignment:
   synchronized(spot) {
       if (!spot.isOccupied()) {
           spot.assignVehicle(vehicle);
       }
   }

2. Use ConcurrentHashMap for spot management
3. Use ReadWriteLock for spot availability queries
4. Consider database-level locking for production
```

### **Q5: How would you test this?**

**Answer:**
```
Unit Tests:
- Test each strategy independently
- Test vehicle factory with all types
- Test spot assignment logic
- Test fee calculation with various durations

Integration Tests:
- Full park-unpark flow
- Multiple vehicles, multiple spot types
- Payment failure scenarios
- Edge cases (negative duration, null inputs)

Mock objects:
- Mock payment gateway
- Mock time provider (to test duration calculation)
```

### **Q6: How to optimize for high traffic?**

**Answer:**
```
Performance optimizations:
1. Cache available spots by type
2. Use separate data structures:
   - Map<VehicleType, Queue<ParkingSpot>> for quick lookup
3. Load balance across entrance gates
4. Pre-fetch empty spots during low traffic
5. Use database indexing on spotId, vehicleType
6. Consider Redis for real-time spot availability
```

### **Q7: What about monitoring & analytics?**

**Answer:**
```
Add Observer pattern:
- interface ParkingLotObserver {
      void onVehicleParked(...);
      void onVehicleUnparked(...);
  }

Observers:
- AnalyticsObserver - track metrics
- NotificationObserver - send alerts
- BillingObserver - generate invoices
- AuditObserver - log all transactions

Metrics:
- Average occupancy rate
- Peak hours
- Revenue per hour
- Average parking duration
- Spot type utilization
```

---

## **⚠️ Known Limitations & Trade-offs**

1. **No Persistence**
   - Current: In-memory only
   - Fix: Add database layer with Repository pattern

2. **No Reservation System**
   - Current: First-come-first-serve only
   - Fix: Add Reservation with time slots

3. **Single Payment at Exit**
   - Risk: Payment failure blocks exit
   - Fix: Pre-authorization at entry

4. **No Multi-floor Support**
   - Current: Assumes single level
   - Fix: Add Floor abstraction

5. **No Thread Safety**
   - Risk: Race conditions in concurrent access
   - Fix: Add synchronization (see Q4 above)

---

## **📚 Key Takeaways**

**Design Patterns Used:**
- ✅ Strategy Pattern (Pricing & Payment)
- ✅ Factory Pattern (Vehicle creation)
- ✅ Template Method (ParkingSpot hierarchy)

**SOLID Principles:**
- ✅ All 5 principles demonstrated

**Extensibility:**
- ✅ Easy to add new vehicle types
- ✅ Easy to add new pricing strategies
- ✅ Easy to add new payment methods

**Interview Focus Points:**
- Strategy Pattern usage and benefits
- SOLID principles application
- Handling extensions (floors, reservations)
- Concurrency considerations
- Testing strategies

---

## **🎓 What You Should Master**

Before interview, ensure you can:
1. ✅ Explain why Strategy > If-Else
2. ✅ Draw the class diagram from memory
3. ✅ Add a new vehicle type in 2 minutes
4. ✅ Explain each SOLID principle with code example
5. ✅ Discuss concurrency challenges and solutions
6. ✅ Propose 3 ways to extend the system
7. ✅ Answer all Q&A sections confidently

**Time to master:** 2-3 hours of practice

**Difficulty:** ⭐⭐⭐ (Medium - High interview frequency)
