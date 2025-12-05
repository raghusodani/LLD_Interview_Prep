# Design Car Rental System - Comprehensive Solution 🚗

## **Problem Statement**

Design a car rental system that can:
- Manage multiple rental locations
- Handle different vehicle types (Economy, SUV, Luxury)
- Support vehicle reservations with date ranges
- Track vehicle availability across locations
- Calculate rental fees dynamically
- Process payments through multiple methods
- Handle reservation conflicts
- Support pickup and drop-off at different locations

---

## **🎯 Our Approach**

### **1. Core Requirements Analysis**

**Functional Requirements:**
- ✅ Create and manage reservations
- ✅ Check vehicle availability by type and dates
- ✅ Support multiple vehicle types with different pricing
- ✅ Handle multiple payment methods
- ✅ Track vehicle status (Available, Rented, Maintenance)
- ✅ Support multiple rental locations
- ✅ Calculate rental fees based on duration and vehicle type
- ✅ Handle reservation cancellations

**Non-Functional Requirements:**
- ✅ Prevent double bookings (reservation conflicts)
- ✅ Extensible for new vehicle types
- ✅ Easy to add new payment methods
- ✅ Support dynamic pricing strategies
- ✅ Scalable across multiple locations
- ✅ Maintain booking history

---

## **🏗️ Architecture & Design Patterns**

### **Pattern 1: Strategy Pattern (Used 2x)**

**Where:** Payment Processing

**Why:**
- Different payment methods have different processing logic
- Easy to add new payment gateways (UPI, Crypto, etc.)
- Each payment method can have its own validation rules

**Implementation:**

```java
public interface PaymentStrategy {
    boolean processPayment(double amount);
}

public class CreditCardPayment implements PaymentStrategy {
    private String cardNumber;

    @Override
    public boolean processPayment(double amount) {
        // Credit card specific logic
        // - Validate card number
        // - Check credit limit
        // - Process transaction
        return true;
    }
}

public class PaypalPayment implements PaymentStrategy {
    private String email;

    @Override
    public boolean processPayment(double amount) {
        // PayPal specific logic
        // - Verify email
        // - Check PayPal balance
        // - Process via PayPal API
        return true;
    }
}

public class CashPayment implements PaymentStrategy {
    @Override
    public boolean processPayment(double amount) {
        // Cash payment logic
        // - Record cash received
        // - Generate receipt
        return true;
    }
}
```

**Benefits:**
- ✅ Open/Closed Principle - Add new payment methods without modifying existing code
- ✅ Easy testing - Each payment method tested independently
- ✅ Runtime flexibility - User chooses payment method at checkout

**Future Strategies:**
```java
public class DynamicPricingStrategy {
    public double calculatePrice(Vehicle vehicle,
                                 LocalDate start,
                                 LocalDate end) {
        double basePrice = vehicle.getDailyRate();
        long days = ChronoUnit.DAYS.between(start, end);

        // Weekend premium
        double weekendPremium = countWeekendDays(start, end) * 0.2;

        // Seasonal pricing
        double seasonalMultiplier = getSeasonalMultiplier(start);

        // Demand-based pricing
        double demandMultiplier = getDemandMultiplier(vehicle.getType());

        return basePrice * days * seasonalMultiplier * demandMultiplier;
    }
}
```

---

### **Pattern 2: Factory Pattern**

**Where:** Vehicle Creation

**Why:**
- Centralize vehicle instantiation logic
- Easy to add new vehicle categories
- Hide complexity of vehicle setup

**Implementation:**

```java
public class VehicleFactory {
    public static Vehicle createVehicle(VehicleType type,
                                       String licensePlate,
                                       String model) {
        switch(type) {
            case ECONOMY:
                return new EconomyVehicle(licensePlate, model);
            case SUV:
                return new SUVVehicle(licensePlate, model);
            case LUXURY:
                return new LuxuryVehicle(licensePlate, model);
            default:
                throw new IllegalArgumentException("Unknown vehicle type");
        }
    }
}

public class EconomyVehicle extends Vehicle {
    public EconomyVehicle(String licensePlate, String model) {
        super(licensePlate, model, VehicleType.ECONOMY);
        setDailyRate(50.0);  // Base rate $50/day
        setDeposit(200.0);
    }
}

public class LuxuryVehicle extends Vehicle {
    public LuxuryVehicle(String licensePlate, String model) {
        super(licensePlate, model, VehicleType.LUXURY);
        setDailyRate(200.0);  // Premium rate $200/day
        setDeposit(1000.0);
        setInsuranceRequired(true);
    }
}
```

**Benefits:**
- ✅ Single Responsibility - Factory handles creation, vehicles handle behavior
- ✅ Easy extension - Add new vehicle types (Sports, Electric, Van)
- ✅ Consistent initialization - All vehicles properly configured

---

### **Pattern 3: Facade Pattern**

**Where:** RentalSystem (Main Controller)

**Why:**
- Simplifies complex subsystem interactions
- Provides unified interface for clients
- Coordinates between multiple managers

**Implementation:**

```java
public class RentalSystem {
    private ReservationManager reservationManager;
    private Map<String, RentalStore> stores;

    public Reservation bookVehicle(User user,
                                   VehicleType type,
                                   String location,
                                   LocalDate startDate,
                                   LocalDate endDate) {
        // 1. Find store
        RentalStore store = stores.get(location);

        // 2. Check availability
        Vehicle vehicle = store.findAvailableVehicle(type, startDate, endDate);

        // 3. Create reservation
        Reservation reservation = reservationManager.createReservation(
            user, vehicle, startDate, endDate, store
        );

        // 4. Update vehicle status
        vehicle.setStatus(VehicleStatus.RESERVED);

        return reservation;
    }
}
```

---

## **📐 Class Diagram Overview**

```
┌──────────────────┐
│  RentalSystem    │ (Facade/Controller)
└────────┬─────────┘
         │
    ┌────┴────────────────────────┐
    │                             │
┌───▼──────────────┐      ┌──────▼──────────┐
│ ReservationMgr   │      │   RentalStore   │
└───┬──────────────┘      └──────┬──────────┘
    │                             │
    │                     ┌───────┴────────┐
    │                     │                │
┌───▼──────────┐   ┌─────▼─────┐   ┌─────▼──────┐
│ Reservation  │   │  Vehicle  │   │  Location  │
└──────────────┘   │ (Abstract) │   └────────────┘
                   └─────┬──────┘
                         │
                ┌────────┼────────┐
                │        │        │
           EconomyV   SUVV    LuxuryV

┌────────────────────┐
│ PaymentStrategy    │ (Interface)
└────────┬───────────┘
         │
    ┌────┼─────┬──────────┐
    │    │     │          │
  Cash  Card  PayPal  (Future)
```

---

## **🔑 Key Design Decisions**

### **Decision 1: Reservation Conflict Prevention**

**What:** Check availability before creating reservation

**Implementation:**
```java
public Vehicle findAvailableVehicle(VehicleType type,
                                   LocalDate start,
                                   LocalDate end) {
    for (Vehicle vehicle : inventory) {
        if (vehicle.getType() != type) continue;

        if (isAvailable(vehicle, start, end)) {
            return vehicle;
        }
    }
    return null;
}

private boolean isAvailable(Vehicle vehicle,
                           LocalDate start,
                           LocalDate end) {
    List<Reservation> reservations =
        reservationManager.getReservationsForVehicle(vehicle.getId());

    for (Reservation res : reservations) {
        // Check for overlap
        if (datesOverlap(res.getStartDate(), res.getEndDate(),
                        start, end)) {
            return false;
        }
    }
    return true;
}

private boolean datesOverlap(LocalDate start1, LocalDate end1,
                            LocalDate start2, LocalDate end2) {
    return !start1.isAfter(end2) && !start2.isAfter(end1);
}
```

**Interview Question:**
> "How do you prevent two users from booking the same car simultaneously?"

**Answer:**
> "We need both application-level and database-level protection:
>
> **Application Level:**
> ```java
> synchronized(vehicle) {
>     if (isAvailable(vehicle, start, end)) {
>         createReservation(vehicle, start, end);
>     }
> }
> ```
>
> **Database Level:**
> - Use optimistic locking with version field
> - Or pessimistic locking: `SELECT ... FOR UPDATE`
> - Add unique constraint on (vehicle_id, date_range)
>
> **Distributed System:**
> - Use Redis distributed lock
> - Lock key: `vehicle:{vehicle_id}:booking`
> - Set TTL to prevent deadlocks
> - Use Redlock algorithm for multi-node Redis"

---

### **Decision 2: Multi-Location Support**

**What:** Each location has independent inventory

**Why:**
- Different locations have different vehicle fleets
- Pricing may vary by location
- Supports one-way rentals (pickup at A, drop at B)

**Implementation:**
```java
public class Location {
    private String locationId;
    private String address;
    private String city;
    private double latitude;
    private double longitude;
}

public class RentalStore {
    private Location location;
    private List<Vehicle> inventory;

    public void transferVehicle(Vehicle vehicle, RentalStore destination) {
        this.inventory.remove(vehicle);
        destination.inventory.add(vehicle);
        vehicle.setCurrentLocation(destination.getLocation());
    }
}
```

**Interview Question:**
> "How would you handle one-way rentals (different pickup/drop-off locations)?"

**Answer:**
> "1. **Additional Fee:** Calculate distance between locations, add relocation fee
> 2. **Vehicle Transfer:** When car is returned, transfer it to destination store inventory
> 3. **Fleet Rebalancing:** Background job to move vehicles from surplus to deficit locations
> 4. **Pricing Strategy:**
>    - Higher fee for unpopular routes (small town → big city)
>    - Discount for popular routes needing rebalancing (big city → airport)
> 5. **Availability Check:** Search destination location's inventory, not pickup location"

---

### **Decision 3: Reservation Status Lifecycle**

**What:** Track reservation through states

**States:**
- PENDING → Payment not completed
- CONFIRMED → Payment received, booking confirmed
- IN_PROGRESS → Vehicle picked up, rental active
- COMPLETED → Vehicle returned
- CANCELLED → User cancelled
- NO_SHOW → User didn't pick up

**Why:**
- Clear state transitions
- Different business logic per state
- Audit trail for business analytics

**Implementation:**
```java
public enum ReservationStatus {
    PENDING,      // Created but not paid
    CONFIRMED,    // Paid and confirmed
    IN_PROGRESS,  // Vehicle picked up
    COMPLETED,    // Vehicle returned
    CANCELLED,    // User cancelled
    NO_SHOW       // Didn't show up
}

public class Reservation {
    public void confirmPayment(PaymentStrategy payment, double amount) {
        if (status != ReservationStatus.PENDING) {
            throw new IllegalStateException("Reservation already confirmed");
        }

        if (payment.processPayment(amount)) {
            this.status = ReservationStatus.CONFIRMED;
            // Send confirmation email
        }
    }

    public void startRental() {
        if (status != ReservationStatus.CONFIRMED) {
            throw new IllegalStateException("Cannot start unconfirmed rental");
        }
        this.status = ReservationStatus.IN_PROGRESS;
        this.vehicle.setStatus(VehicleStatus.RENTED);
    }
}
```

---

### **Decision 4: Pricing Strategy Architecture**

**What:** Separate pricing calculation from payment processing

**Why:**
- Pricing rules change frequently (promotions, seasons)
- Payment processing is stable
- Different teams may own pricing vs payments

**Current Implementation:**
```java
// Simple: Vehicle has fixed daily rate
double totalCost = vehicle.getDailyRate() * numberOfDays;
```

**Enhanced Version:**
```java
public interface PricingStrategy {
    double calculatePrice(Vehicle vehicle,
                         LocalDate start,
                         LocalDate end,
                         User user);
}

public class StandardPricingStrategy implements PricingStrategy {
    @Override
    public double calculatePrice(Vehicle vehicle,
                                LocalDate start,
                                LocalDate end,
                                User user) {
        long days = ChronoUnit.DAYS.between(start, end);
        return vehicle.getDailyRate() * days;
    }
}

public class DynamicPricingStrategy implements PricingStrategy {
    @Override
    public double calculatePrice(Vehicle vehicle,
                                LocalDate start,
                                LocalDate end,
                                User user) {
        double basePrice = vehicle.getDailyRate();
        long days = ChronoUnit.DAYS.between(start, end);

        // Apply multipliers
        double weekendPremium = calculateWeekendPremium(start, end);
        double loyaltyDiscount = calculateLoyaltyDiscount(user);
        double demandSurge = calculateDemandSurge(vehicle.getType(), start);

        return basePrice * days * weekendPremium * loyaltyDiscount * demandSurge;
    }
}
```

---

## **💡 SOLID Principles Applied**

### **S - Single Responsibility**
- `RentalStore` - Manages inventory at one location
- `ReservationManager` - Handles all reservations
- `PaymentStrategy` - Only payment processing
- Each class has ONE reason to change

### **O - Open/Closed**
- Adding new vehicle type: Create new subclass
- Adding payment method: Create new strategy
- Adding pricing rule: Create new pricing strategy
- No modification of existing code

### **L - Liskov Substitution**
- Any `Vehicle` subclass can replace base class
- Any `PaymentStrategy` can replace another
- Polymorphism works correctly

### **I - Interface Segregation**
- `PaymentStrategy` - Only payment method
- Clients don't depend on unused methods

### **D - Dependency Inversion**
- `RentalSystem` depends on `PaymentStrategy` interface
- Not tied to specific payment implementations

---

## **🎭 Scenario Walkthrough**

### **Scenario: Complete Rental Flow**

**Step 1: Search for Available Vehicle**
```
User: "I need an SUV in San Francisco from Jan 10-15"
   │
   ├─ RentalSystem.searchAvailableVehicles(SUV, SF, 01/10, 01/15)
   │
   ├─ Find San Francisco RentalStore
   │
   ├─ Check each SUV for availability
   │   ├─ Vehicle A: MAINTENANCE ❌
   │   ├─ Vehicle B: Check reservations → Overlap 01/12-01/14 ❌
   │   └─ Vehicle C: No conflicts ✅
   │
   └─ Return Vehicle C (Toyota RAV4)
```

**Step 2: Create Reservation**
```
User: "Book Vehicle C"
   │
   ├─ reservationManager.createReservation()
   │
   ├─ Create Reservation object
   │   ├─ ID: RES-001
   │   ├─ User: John Doe
   │   ├─ Vehicle: Toyota RAV4
   │   ├─ Dates: 01/10 - 01/15 (5 days)
   │   ├─ Price: $120/day × 5 = $600
   │   └─ Status: PENDING
   │
   └─ Reserve vehicle (temporary hold for 15 minutes)
```

**Step 3: Process Payment**
```
User: "Pay with Credit Card ending 1234"
   │
   ├─ Select PaymentStrategy: CreditCardPayment
   │
   ├─ payment.processPayment($600)
   │   ├─ Validate card
   │   ├─ Check fraud detection
   │   ├─ Process charge
   │   └─ Success! ✅
   │
   ├─ Update Reservation
   │   ├─ Status: PENDING → CONFIRMED
   │   ├─ Payment ID: PAY-12345
   │   └─ Confirmation Code: CONF-78901
   │
   ├─ Update Vehicle
   │   └─ Status: AVAILABLE → RESERVED
   │
   └─ Send confirmation email
```

**Step 4: Pick Up Vehicle**
```
User arrives at store on 01/10
   │
   ├─ Show confirmation code: CONF-78901
   │
   ├─ Staff verifies:
   │   ├─ Driver's license ✅
   │   ├─ Insurance proof ✅
   │   └─ Inspect vehicle (record mileage, damage)
   │
   ├─ reservation.startRental()
   │   ├─ Status: CONFIRMED → IN_PROGRESS
   │   ├─ Actual pickup time: 2024-01-10 10:00 AM
   │   └─ Odometer: 25,000 miles
   │
   ├─ Update Vehicle
   │   └─ Status: RESERVED → RENTED
   │
   └─ Hand over keys 🔑
```

**Step 5: Return Vehicle**
```
User returns on 01/15
   │
   ├─ Inspect vehicle
   │   ├─ Odometer: 25,750 miles (750 miles driven)
   │   ├─ Damage check: None ✅
   │   └─ Fuel level: Full ✅
   │
   ├─ reservation.completeRental()
   │   ├─ Status: IN_PROGRESS → COMPLETED
   │   ├─ Return time: 2024-01-15 11:00 AM
   │   └─ Calculate additional charges: $0
   │
   ├─ Update Vehicle
   │   ├─ Status: RENTED → AVAILABLE
   │   └─ Schedule next maintenance if needed
   │
   └─ Release deposit, generate receipt
```

---

## **🚀 Extensions & Enhancements**

### **1. Dynamic Pricing Strategy**
```java
public class SeasonalPricingStrategy implements PricingStrategy {
    @Override
    public double calculatePrice(Vehicle vehicle,
                                LocalDate start,
                                LocalDate end,
                                User user) {
        double basePrice = vehicle.getDailyRate();
        long days = ChronoUnit.DAYS.between(start, end);

        // Summer (June-Aug): 1.5x
        // Winter (Dec-Feb): 0.8x
        // Spring/Fall: 1.0x
        double seasonalMultiplier = getSeasonalMultiplier(start);

        // Weekend premium: 1.3x
        double weekendPremium = 1.0;
        if (isWeekend(start)) {
            weekendPremium = 1.3;
        }

        // Loyalty discount
        double loyaltyDiscount = 1.0;
        if (user.getRentalCount() > 10) {
            loyaltyDiscount = 0.9; // 10% off
        }

        return basePrice * days * seasonalMultiplier
               * weekendPremium * loyaltyDiscount;
    }
}
```

### **2. Insurance Add-on**
```java
public class InsurancePackage {
    private String packageName;
    private double dailyRate;
    private double coverageAmount;

    public static InsurancePackage BASIC =
        new InsurancePackage("Basic", 15.0, 50000);
    public static InsurancePackage PREMIUM =
        new InsurancePackage("Premium", 30.0, 150000);
}

public class Reservation {
    private InsurancePackage insurance;

    public void addInsurance(InsurancePackage insurance) {
        this.insurance = insurance;
        this.totalCost += insurance.getDailyRate() * numberOfDays;
    }
}
```

### **3. Loyalty Program**
```java
public class LoyaltyProgram {
    public int calculatePoints(Reservation reservation) {
        // 1 point per dollar spent
        return (int) reservation.getTotalCost();
    }

    public double getDiscount(User user) {
        int points = user.getLoyaltyPoints();

        if (points > 5000) return 0.15;  // 15% off
        if (points > 2000) return 0.10;  // 10% off
        if (points > 500) return 0.05;   // 5% off

        return 0.0;
    }
}
```

### **4. Fleet Rebalancing**
```java
public class FleetManager {
    public void rebalanceFleet() {
        // Identify surplus and deficit locations
        Map<Location, Integer> utilization = calculateUtilization();

        for (Location surplus : getSurplusLocations(utilization)) {
            for (Location deficit : getDeficitLocations(utilization)) {
                // Find idle vehicles at surplus location
                List<Vehicle> idleVehicles =
                    getIdleVehicles(surplus, 3); // idle for 3+ days

                // Transfer to deficit location
                for (Vehicle vehicle : idleVehicles) {
                    scheduleTransfer(vehicle, surplus, deficit);
                }
            }
        }
    }
}
```

---

## **🎯 Interview Questions & Answers**

### **Q1: How do you handle reservation conflicts?**

**Answer:**
```
Three-layer approach:

1. Application Layer (Optimistic):
   - Check availability before creating reservation
   - Use date overlap algorithm
   - Fast for read-heavy workloads

2. Database Layer (Pessimistic):
   - Use row-level locking: SELECT ... FOR UPDATE
   - Prevents concurrent bookings
   - Add unique constraint on (vehicle_id, date_range)

3. Distributed System:
   - Use Redis distributed lock
   - Lock pattern: {vehicle_id}:{start_date}:{end_date}
   - Set TTL to auto-release locks
   - Redlock algorithm for multi-node setup

Comparison:
┌──────────────┬───────────┬────────────┬──────────┐
│ Approach     │ Conflict  │ Performance│ Scalable │
│              │ Prevention│            │          │
├──────────────┼───────────┼────────────┼──────────┤
│ App Only     │ Poor      │ Fast       │ No       │
│ DB Lock      │ Good      │ Medium     │ Limited  │
│ Redis Lock   │ Excellent │ Fast       │ Yes      │
└──────────────┴───────────┴────────────┴──────────┘
```

---

### **Q2: How would you implement dynamic pricing?**

**Answer:**
```
Use Strategy Pattern with multiple factors:

public class DynamicPricingStrategy {
    public double calculatePrice(Vehicle vehicle,
                                 Reservation reservation,
                                 MarketData market) {
        double base = vehicle.getDailyRate();

        // 1. Demand multiplier (1.0 to 2.0)
        double demand = market.getDemandMultiplier(
            vehicle.getType(),
            reservation.getLocation(),
            reservation.getStartDate()
        );

        // 2. Supply factor (0.8 to 1.0)
        double supply = market.getSupplyFactor(
            vehicle.getType(),
            reservation.getLocation()
        );

        // 3. Time-based (0.8 to 1.5)
        double timeFactor = 1.0;
        if (isWeekend(reservation)) timeFactor *= 1.3;
        if (isHoliday(reservation)) timeFactor *= 1.5;
        if (isSummer(reservation)) timeFactor *= 1.2;

        // 4. Booking window (0.9 to 1.3)
        double advanceBooking = getAdvanceBookingFactor(
            daysUntilPickup(reservation)
        );

        // 5. Competition pricing
        double competition = getCompetitorPricingFactor(
            vehicle.getType(),
            reservation.getLocation()
        );

        return base * demand * supply * timeFactor
               * advanceBooking * competition;
    }
}

Real Example:
Base: $100/day
Demand: High (1.5x) → Weekend in Miami
Supply: Low (1.0x) → Only 2 SUVs available
Time: Weekend (1.3x)
Advance: Last minute (1.2x)
Competition: Market rate (1.0x)

Final: $100 × 1.5 × 1.0 × 1.3 × 1.2 × 1.0 = $234/day
```

---

### **Q3: How to optimize fleet allocation across locations?**

**Answer:**
```
Use predictive analytics + optimization algorithm:

Step 1: Data Collection
- Historical rental patterns per location
- Seasonal trends
- Event calendars (conferences, holidays)
- Weather forecasts

Step 2: Demand Forecasting
public class DemandForecaster {
    public Map<Location, Integer> forecastDemand(
        LocalDate start,
        LocalDate end,
        VehicleType type) {

        // Use time series model (ARIMA, LSTM)
        // Input: historical data, seasonality, events
        // Output: predicted demand per location
    }
}

Step 3: Optimization
public class FleetOptimizer {
    public List<Transfer> optimize() {
        // Linear programming problem
        // Minimize: transfer_cost + opportunity_cost
        // Subject to:
        //   - Supply >= Forecasted demand
        //   - Transfer time constraints
        //   - Vehicle availability

        // Use OR-Tools or similar library
    }
}

Step 4: Execution
- Schedule transfers during low-demand periods
- Use drivers or transporters
- Update inventory in real-time
- Monitor and adjust

Metrics to Track:
- Utilization rate per location
- Transfer costs vs revenue gain
- Vehicles idle for >3 days
- Unfulfilled demand (lost opportunities)
```

---

### **Q4: How would you support multi-location support with one-way rentals?**

**Answer:**
```
Implementation:

1. Add Drop-off Location to Reservation:
public class Reservation {
    private Location pickupLocation;
    private Location dropoffLocation;

    public double calculateOneWayFee() {
        if (pickupLocation.equals(dropoffLocation)) {
            return 0.0;
        }

        double distance = calculateDistance(
            pickupLocation,
            dropoffLocation
        );

        // Base relocation fee
        double baseFee = 50.0;

        // Distance charge
        double distanceFee = distance * 0.5; // $0.50/mile

        // Route popularity factor
        double popularity = getRoutePopularity(
            pickupLocation,
            dropoffLocation
        );

        // Popular routes (big city → airport): discount
        // Unpopular routes (suburb → rural): premium
        double multiplier = 2.0 - popularity; // 0.5x to 1.5x

        return baseFee + (distanceFee * multiplier);
    }
}

2. Vehicle Transfer Logic:
public void completeReturn(Reservation reservation) {
    Vehicle vehicle = reservation.getVehicle();

    if (needsRelocation(reservation)) {
        // Remove from pickup location
        pickupStore.removeVehicle(vehicle);

        // Add to dropoff location
        dropoffStore.addVehicle(vehicle);

        // Update vehicle location
        vehicle.setCurrentLocation(
            reservation.getDropoffLocation()
        );
    }

    reservation.setStatus(ReservationStatus.COMPLETED);
}

3. Smart Incentives:
public class RebalancingIncentives {
    public double calculateIncentive(Location from, Location to) {
        int surplus = getSurplusVehicles(from);
        int deficit = getDeficitVehicles(to);

        if (surplus > 0 && deficit > 0) {
            // Encourage this route with discount
            return -50.0; // $50 discount!
        }

        return 0.0;
    }
}

4. Availability Search:
When searching, check BOTH locations:
- Vehicles at pickup location: can be used immediately
- Vehicles at nearby locations: can be transferred if time permits
```

---

### **Q5: How do you handle late returns and overtime charges?**

**Answer:**
```
Implement grace period + progressive charges:

public class LateReturnHandler {
    private static final int GRACE_PERIOD_MINUTES = 30;
    private static final double HOURLY_OVERTIME = 15.0;
    private static final double DAILY_OVERTIME = 120.0;

    public double calculateOvertimeCharge(
        Reservation reservation,
        LocalDateTime actualReturn) {

        LocalDateTime expected = reservation.getEndDate()
                                           .atTime(12, 0); // Noon

        if (!actualReturn.isAfter(expected)) {
            return 0.0; // On time!
        }

        long minutesLate = ChronoUnit.MINUTES.between(
            expected,
            actualReturn
        );

        if (minutesLate <= GRACE_PERIOD_MINUTES) {
            return 0.0; // Grace period
        }

        minutesLate -= GRACE_PERIOD_MINUTES;

        long hoursLate = (minutesLate + 59) / 60; // Round up

        if (hoursLate <= 3) {
            // Hourly rate for <3 hours
            return hoursLate * HOURLY_OVERTIME;
        } else {
            // Full day rate for >3 hours
            return DAILY_OVERTIME;
        }
    }

    public void notifyUser(Reservation reservation) {
        // Send notifications:
        // - 1 day before: "Reminder: return by tomorrow noon"
        // - Day of: "Return by noon to avoid charges"
        // - 1 hour before: "Return within 1 hour"
        // - Grace period ending: "15 minutes until charges apply"
    }
}

Business Logic:
- Grace period: 30 min (customer-friendly)
- 1 hour late: $15
- 2 hours late: $30
- 3 hours late: $45
- 4+ hours late: $120 (full day rate)

Why?
- Protects next reservation (4 hour buffer for cleaning/prep)
- Fair to customer (grace period)
- Discourages abuse (escalating charges)
```

---

### **Q6: How would you handle vehicle damage and insurance claims?**

**Answer:**
```
Multi-step verification process:

1. Pre-rental Inspection:
public class VehicleInspection {
    private List<DamageRecord> priorDamages;

    public InspectionReport createReport(Vehicle vehicle) {
        InspectionReport report = new InspectionReport();

        // Capture photos (6 angles + odometer)
        report.addPhotos(capturePhotos(vehicle));

        // Document existing damage
        report.setExistingDamages(
            vehicle.getDamageHistory()
        );

        // Record mileage and fuel
        report.setOdometer(vehicle.getCurrentMileage());
        report.setFuelLevel(vehicle.getFuelLevel());

        // Customer signature
        report.requireSignature();

        return report;
    }
}

2. Post-rental Inspection:
public class DamageAssessment {
    public DamageReport assessDamage(
        InspectionReport preRental,
        Vehicle vehicle) {

        InspectionReport postRental =
            new VehicleInspection().createReport(vehicle);

        // Compare photos
        List<Damage> newDamages =
            detectNewDamages(preRental, postRental);

        if (newDamages.isEmpty()) {
            return DamageReport.NO_DAMAGE;
        }

        // Estimate repair cost
        double repairCost = estimateRepairCost(newDamages);

        return new DamageReport(newDamages, repairCost);
    }
}

3. Insurance Processing:
public class InsuranceClaim {
    public void processClaim(Reservation reservation,
                            DamageReport damage) {

        if (reservation.hasInsurance()) {
            InsurancePackage insurance =
                reservation.getInsurance();

            if (damage.getCost() <= insurance.getCoverage()) {
                // Fully covered
                chargeTo(insurance.getProvider(), damage.getCost());
            } else {
                // Partially covered
                double covered = insurance.getCoverage();
                double remaining = damage.getCost() - covered;

                chargeTo(insurance.getProvider(), covered);
                chargeTo(reservation.getUser(), remaining);
            }
        } else {
            // No insurance - charge user
            double deductible = 500.0;
            chargeTo(reservation.getUser(),
                    Math.min(damage.getCost(), deductible));
        }

        // Schedule vehicle for repair
        vehicle.setStatus(VehicleStatus.MAINTENANCE);
        scheduleRepair(vehicle, damage);
    }
}

4. Dispute Resolution:
- Customer can dispute within 48 hours
- Provide evidence (their own photos)
- Review by manager
- Third-party inspection if needed
- Refund if ruling in customer's favor
```

---

### **Q7: How to scale to millions of users and thousands of locations?**

**Answer:**
```
Multi-tier architecture:

1. Database Sharding:
- Shard by location/region
- Each region has own DB
- Cross-region queries via federation

Sharding Strategy:
┌─────────┬──────────────────────────────┐
│ Shard 1 │ West Coast (CA, OR, WA)      │
│ Shard 2 │ East Coast (NY, FL, MA)      │
│ Shard 3 │ Central (TX, IL, MO)         │
│ Shard 4 │ International (per country)  │
└─────────┴──────────────────────────────┘

2. Caching:
- Redis for hot data:
  * Available vehicles (TTL: 5 min)
  * Pricing cache (TTL: 1 hour)
  * User sessions
  * Location data (rarely changes)

- Cache keys:
  * vehicles:available:{location}:{type}:{date}
  * pricing:{vehicle_id}:{date_range}
  * user:session:{user_id}

3. Microservices:
┌──────────────────┐
│  API Gateway     │
└────────┬─────────┘
         │
    ┌────┴────────────────────────┐
    │                             │
┌───▼──────────┐      ┌───────────▼────┐
│ Inventory    │      │ Reservation    │
│ Service      │      │ Service        │
└──────────────┘      └────────────────┘
    │                      │
┌───▼──────────┐      ┌───▼────────────┐
│ Pricing      │      │ Payment        │
│ Service      │      │ Service        │
└──────────────┘      └────────────────┘

Each service:
- Independently deployable
- Own database
- Horizontal scaling
- Circuit breaker for resilience

4. Event-Driven Architecture:
- Kafka/SQS for async operations
- Events:
  * VehicleReserved
  * PaymentProcessed
  * VehicleReturned
  * MaintenanceRequired

5. CDN for Static Assets:
- Vehicle images
- Location maps
- UI assets

6. Load Balancing:
- Round-robin across regions
- Geo-routing (user to nearest region)
- Health checks
- Auto-scaling based on load

7. Search Optimization:
- Elasticsearch for vehicle search
- Filters: location, type, price, features
- Full-text search
- Sub-second response times

Performance Targets:
- Search: <200ms
- Booking: <500ms
- Payment: <2s
- 99.9% uptime
```

---

### **Q8: How would you implement a waiting list for high-demand periods?**

**Answer:**
```
Queue-based system with priority:

public class WaitlistManager {
    private PriorityQueue<WaitlistEntry> waitlist;

    public void addToWaitlist(User user,
                              VehicleType type,
                              LocalDate start,
                              LocalDate end,
                              Location location) {

        WaitlistEntry entry = new WaitlistEntry(
            user, type, start, end, location,
            calculatePriority(user)
        );

        waitlist.offer(entry);

        // Notify user
        sendNotification(user,
            "Added to waitlist. Position: " +
            getPosition(entry));
    }

    private int calculatePriority(User user) {
        int priority = 0;

        // Loyalty points
        priority += user.getLoyaltyPoints() / 100;

        // Premium member
        if (user.isPremium()) priority += 1000;

        // Rental history
        priority += user.getRentalCount() * 10;

        return priority;
    }

    public void processWaitlist() {
        // When vehicle becomes available
        while (!waitlist.isEmpty()) {
            WaitlistEntry entry = waitlist.poll();

            // Check if still available
            Vehicle vehicle = findAvailableVehicle(
                entry.getType(),
                entry.getStart(),
                entry.getEnd(),
                entry.getLocation()
            );

            if (vehicle != null) {
                // Offer to user (24-hour window)
                sendOffer(entry.getUser(), vehicle, entry);

                // Set timeout
                scheduleOfferExpiry(entry, 24);

                break; // Wait for response
            }
        }
    }
}

Notifications:
- SMS/Email when spot available
- 24-hour window to accept
- Auto-move to next if declined/expired
- Show position in waitlist

Business Logic:
- Priority: Premium > Loyalty > FIFO
- Automatic refund if not fulfilled
- Option to modify dates while waiting
```

---

## **⚠️ Known Limitations & Trade-offs**

1. **No Real-time Inventory Sync**
   - Current: Each location manages own inventory
   - Risk: Stale data if vehicle transferred
   - Fix: Event-driven updates with Kafka/Redis pub-sub

2. **Simple Pricing Model**
   - Current: Fixed daily rate per vehicle type
   - Missing: Dynamic pricing based on demand
   - Fix: Implement DynamicPricingStrategy

3. **No Maintenance Scheduling**
   - Current: Manual status update
   - Missing: Automatic scheduling based on mileage/time
   - Fix: Add MaintenanceScheduler with rules engine

4. **Limited Concurrency Control**
   - Current: Application-level checks
   - Risk: Race conditions in high-traffic
   - Fix: Database-level locking + distributed locks

5. **No Analytics Dashboard**
   - Current: No business intelligence
   - Missing: Revenue tracking, utilization rates
   - Fix: Add AnalyticsService with Observer pattern

---

## **📚 Key Takeaways**

**Design Patterns Used:**
- ✅ Strategy Pattern (Payments, Pricing)
- ✅ Factory Pattern (Vehicle creation)
- ✅ Facade Pattern (RentalSystem controller)

**SOLID Principles:**
- ✅ All 5 principles demonstrated

**Extensibility:**
- ✅ Easy to add vehicle types
- ✅ Easy to add payment methods
- ✅ Easy to add pricing strategies
- ✅ Support for multi-location

**Real-World Considerations:**
- ✅ Reservation conflict prevention
- ✅ Multi-location support
- ✅ One-way rentals
- ✅ Dynamic pricing
- ✅ Fleet optimization

**Interview Focus Points:**
- Reservation conflict handling
- Dynamic pricing strategies
- Fleet optimization algorithms
- Multi-location architecture
- Scalability to millions of users

---

## **🎓 What You Should Master**

Before interview, ensure you can:
1. ✅ Explain date overlap algorithm for conflicts
2. ✅ Design dynamic pricing with multiple factors
3. ✅ Discuss distributed locking (Redis Redlock)
4. ✅ Propose fleet optimization strategy
5. ✅ Handle edge cases (late returns, damage, cancellations)
6. ✅ Scale to millions of users (sharding, caching, microservices)
7. ✅ Draw architecture diagram from memory
8. ✅ Add new vehicle type in 2 minutes
9. ✅ Explain SOLID principles with code examples
10. ✅ Discuss all 8 Q&A topics confidently

**Practice Exercises:**
1. Implement one-way rental fee calculation
2. Code the date overlap algorithm
3. Design the waitlist priority queue
4. Create dynamic pricing strategy
5. Write concurrency test for double booking

**Time to master:** 2-3 hours of practice

**Difficulty:** ⭐⭐⭐ (Medium)

**Interview Frequency:** ⭐⭐⭐⭐ (Very High - Similar to hotel booking, flight booking)

---

**Pro Tip:** Car Rental is similar to many booking systems (hotels, flights, event tickets). Master this, and you can adapt to any booking system design! 🚀
