# Design Smart Locker System - Comprehensive Solution 🔐

## **Problem Statement**

Design a smart locker system (like Amazon Locker, FedEx, or InPost) where:
- Delivery personnel deposit packages into available lockers
- System generates secure OTP codes for recipients
- Recipients retrieve packages using OTP and phone verification
- System handles different locker sizes smartly
- Packages are returned if not picked up within retention period
- Lockers can be put into maintenance mode

---

## **🎯 Our Approach**

### **Core Requirements Analysis**

**Functional Requirements:**
- ✅ Deposit package into appropriate locker
- ✅ Smart size matching (use larger locker if exact size unavailable)
- ✅ Generate and send OTP to recipient
- ✅ Validate OTP and phone number for pickup
- ✅ Handle expired packages (auto-return)
- ✅ Support locker maintenance mode
- ✅ Track locker availability in real-time

**Non-Functional Requirements:**
- ✅ Secure OTP generation (cryptographically strong)
- ✅ Efficient locker finding algorithm
- ✅ Extensible for new locker sizes
- ✅ Thread-safe for concurrent access
- ✅ Audit trail for security

---

## **🏗️ Architecture & Design Patterns**

### **Pattern 1: Chain of Responsibility**

**Where:** Size Matching Logic

**Why:**
- Need to try multiple locker sizes in priority order
- Exact match first, then progressively larger
- Clean separation of size-finding logic
- Easy to modify matching rules

**Implementation:**

```java
public Locker findSuitableLocker(List<Locker> lockers, LockerSize packageSize) {
    // Step 1: Try exact match
    Locker exactMatch = findExactMatch(lockers, packageSize);
    if (exactMatch != null) {
        return exactMatch;
    }

    // Step 2: Chain to next larger size
    return findNextLargerSize(lockers, packageSize);
    // SMALL → MEDIUM → LARGE → EXTRA_LARGE
}
```

**Visual Flow:**
```
Package (SMALL) arrives
    ↓
Try SMALL lockers → ❌ All occupied
    ↓
Try MEDIUM lockers → ✅ Found M2!
    ↓
Assign to M2 (larger locker accepted)
```

**Benefits:**
- ✅ Optimizes locker utilization
- ✅ Prevents delivery failures
- ✅ Easy to add priority rules
- ✅ Testable in isolation

---

### **Pattern 2: Strategy Pattern**

**Where:** Size Matching Strategy

**Why:**
- Different facilities might have different matching rules
- Airport: Strict matching (no size upgrades)
- Residential: Flexible matching (allow upgrades)
- Commercial: Premium matching (optimize for turnaround)

**Implementation:**

```java
public abstract class SizeMatchingStrategy {
    public abstract Locker findSuitableLocker(List<Locker> lockers,
                                               LockerSize packageSize);
}

public class DefaultSizeMatching extends SizeMatchingStrategy {
    // Implements Chain of Responsibility
}

public class StrictSizeMatching extends SizeMatchingStrategy {
    // Only exact matches allowed
    public Locker findSuitableLocker(...) {
        return findExactMatchOnly(lockers, packageSize);
    }
}
```

**Benefits:**
- ✅ Flexible matching rules per location
- ✅ Easy to A/B test different strategies
- ✅ Open/Closed Principle

---

### **Pattern 3: State Pattern (Implicit)**

**Where:** Locker Status Management

**States:**
- AVAILABLE → Can accept packages
- OCCUPIED → Has package, waiting for pickup
- MAINTENANCE → Temporarily unavailable
- OUT_OF_SERVICE → Permanently disabled

**State Transitions:**
```
AVAILABLE ─────deposit────→ OCCUPIED
    ↑                           │
    └──────pickup/expire────────┘

AVAILABLE ─────maintenance──→ MAINTENANCE
    ↑                              │
    └──────repair complete─────────┘
```

---

### **Pattern 4: Factory Pattern (Potential)**

**Where:** Could be used for Locker creation

```java
public class LockerFactory {
    public static Locker createLocker(String id, LockerSize size,
                                     String location, boolean hasRefrigeration) {
        if (hasRefrigeration) {
            return new RefrigeratedLocker(id, size, location);
        }
        return new StandardLocker(id, size, location);
    }
}
```

---

## **📐 Architecture Diagram**

```
┌─────────────────────────────────────────┐
│         LockerService (Facade)          │
│  - List<Locker>                         │
│  - SizeMatchingStrategy                 │
│  - Map<packageId, lockerId>             │
└────────┬────────────────────────────────┘
         │
    ┌────┴──────────────────┐
    │                       │
┌───▼─────────────┐   ┌────▼──────────────┐
│ SizeMatching    │   │   Security        │
│ Strategy        │   │   - OTPGenerator  │
│  - Chain of Resp│   │   - OTPValidator  │
└─────────────────┘   └───────────────────┘

┌──────────────────────────────────────────┐
│            Models                        │
├──────────────┬───────────────────────────┤
│   Locker     │     Package     │ Pickup  │
│  - size      │   - size        │ - time  │
│  - status    │   - otp         │ - status│
│  - package   │   - phone       │         │
└──────────────┴─────────────────┴─────────┘
```

---

## **🔑 Key Design Decisions**

### **Decision 1: Smart Size Matching**

**What:** Small package can use Medium/Large/XL locker if Small is full

**Why:**
- Maximize locker utilization
- Reduce delivery failures
- Better customer experience

**Trade-offs:**
```
✅ Pros:
- Higher successful delivery rate
- Flexible capacity management
- Handles peak demand

❌ Cons:
- Wastes larger locker space
- Might cause shortage for large packages
- Harder to predict capacity
```

**Interview Question:**
> "What if using larger lockers causes shortage for actual large packages?"

**Answer:**
> "Good point! We can add smart rules:
> 1. Reserve X% of large lockers for large-only packages
> 2. Implement urgency score (how long package waiting + size)
> 3. Dynamic pricing - charge less if upgraded to larger locker
> 4. Predictive analytics - if large package expected, hold large locker
> 5. Multi-location routing - suggest nearby lockers"

---

### **Decision 2: OTP Security**

**What:** 6-digit OTP with 24-hour validity

**Why:**
- Balance between security and usability
- 6 digits = 1 million combinations (sufficient for small lockers)
- 24 hours = reasonable pickup window

**Security Layers:**
```
Layer 1: OTP validation
Layer 2: Phone number verification
Layer 3: Locker access logging (audit trail)
Layer 4: Limited retry attempts (3 tries)
Layer 5: Time-based expiry
```

**Interview Question:**
> "6 digits can be brute-forced. How to prevent?"

**Answer:**
> "Multiple defenses:
> 1. Rate limiting - max 3 attempts per 10 minutes per locker
> 2. Lock locker after 3 failed attempts (requires manual unlock)
> 3. Alert security on suspicious activity
> 4. Use alphanumeric codes (36^6 = 2B combinations)
> 5. Two-factor: OTP + QR code scan
> 6. Biometric for high-value packages"

---

### **Decision 3: Return Policy (72 hours)**

**What:** Auto-return packages not picked up in 72 hours

**Why:**
- Prevent locker hoarding
- Maximize locker availability
- Customer gets multiple reminders

**Process:**
```
Hour 0:  Package delivered → OTP sent
Hour 24: Reminder 1 sent
Hour 48: Reminder 2 sent (urgent)
Hour 60: Final warning
Hour 72: Auto-return initiated
```

**Interview Question:**
> "What if customer was traveling and couldn't pick up?"

**Answer:**
> "Multiple options:
> 1. Extension request - customer can request 24hr extension (once)
> 2. Hold at facility - package moved to storage, longer retention
> 3. Delivery to neighbor - customer can designate backup recipient
> 4. Schedule redelivery - return to sender, schedule new delivery
> 5. Premium service - longer retention for fee"

---

### **Decision 4: Locker Status Management**

**What:** Four states: AVAILABLE, OCCUPIED, MAINTENANCE, OUT_OF_SERVICE

**Why:**
- Clear lifecycle management
- Prevents assignment to broken lockers
- Supports operational needs

**State Machine:**
```java
// ❌ Bad: Boolean flags everywhere
class Locker {
    boolean isAvailable;
    boolean isOccupied;
    boolean isBroken;
    boolean isUnderMaintenance;
    // Confusing! Can be both occupied AND available?
}

// ✅ Good: Clear enum states
enum LockerStatus {
    AVAILABLE,      // Ready for packages
    OCCUPIED,       // Has package
    MAINTENANCE,    // Temporarily unavailable
    OUT_OF_SERVICE  // Permanently disabled
}
```

---

## **💡 SOLID Principles Applied**

### **S - Single Responsibility**
- `OTPGenerator` - Only generates OTPs
- `OTPValidator` - Only validates OTPs
- `SizeMatchingStrategy` - Only finds suitable lockers
- `LockerService` - Orchestrates operations
- Each class has ONE reason to change

### **O - Open/Closed**
- New matching strategies: Extend `SizeMatchingStrategy`
- New locker types: Create subclass of `Locker`
- New security methods: Extend OTP generation

### **L - Liskov Substitution**
- Any `SizeMatchingStrategy` can replace base class
- Polymorphism works correctly

### **I - Interface Segregation**
- Clean, minimal interfaces
- Clients depend only on what they use

### **D - Dependency Inversion**
- `LockerService` depends on `SizeMatchingStrategy` abstraction
- Can swap strategies at runtime

---

## **🎭 Scenario Walkthrough**

### **Scenario 1: Smart Size Matching**

```
Initial State:
SMALL: [S1:Empty, S2:Empty]
MEDIUM: [M1:Empty, M2:Empty, M3:Empty]

Step 1: Package P1 (SMALL) arrives
→ Try SMALL → S1 available → Assign to S1 ✅

Step 2: Package P2 (SMALL) arrives
→ Try SMALL → S2 available → Assign to S2 ✅

Step 3: Package P3 (SMALL) arrives
→ Try SMALL → None available ❌
→ Chain to MEDIUM → M1 available → Assign to M1 ✅
   (Package P3 gets UPGRADED to larger locker!)

Result: Small package in Medium locker (smart!)
```

---

### **Scenario 2: Secure Pickup Flow**

```
1. Customer receives SMS:
   "Package PKG001 ready at Locker S1"
   "OTP: 426560"
   "Valid for 24 hours"

2. Customer arrives at locker:
   Inputs: Locker ID (S1), OTP (426560), Phone (9876543210)

3. System validates:
   ✅ Locker exists
   ✅ Locker has package
   ✅ Phone matches recipient
   ✅ OTP matches
   ✅ OTP not expired
   ✅ Within 24-hour window

4. Locker opens:
   Package removed from locker
   Locker status → AVAILABLE
   Audit log created

5. SMS confirmation:
   "Package PKG001 picked up at 2:30 PM"
```

---

### **Scenario 3: Failed Pickup Attempts**

```
Attempt 1: Wrong OTP
Input: OTP = "000000" (wrong!)
Result: ❌ Invalid OTP
Action: Log failed attempt, count = 1

Attempt 2: Wrong Phone
Input: Phone = "1111111111" (not recipient!)
Result: ❌ Phone mismatch
Action: Log failed attempt, count = 2

Attempt 3: Correct credentials
Input: OTP = "426560", Phone = "9876543210"
Result: ✅ Success!
Action: Package released, audit log

Security: If 5 failed attempts → Lock locker, alert security
```

---

## **🚀 Extensions & Enhancements**

### **1. Refrigerated Lockers**

```java
public class RefrigeratedLocker extends Locker {
    private double currentTemp;
    private double targetTemp;

    public RefrigeratedLocker(String id, LockerSize size, double targetTemp) {
        super(id, size);
        this.targetTemp = targetTemp;
    }

    @Override
    public boolean assignPackage(Package pkg) {
        if (!(pkg instanceof RefrigeratedPackage)) {
            return false; // Only food/medicine packages
        }
        return super.assignPackage(pkg);
    }
}

// Use case: Grocery delivery, medicine, flowers
```

---

### **2. QR Code Support**

```java
public class QRCodeGenerator {
    public static String generateQRPayload(Package pkg, Locker locker) {
        // Encode: lockerId|packageId|otp|timestamp|signature
        String payload = String.join("|",
            locker.getLockerId(),
            pkg.getPackageId(),
            pkg.getOtp(),
            String.valueOf(System.currentTimeMillis())
        );
        return Base64.encode(payload + "|" + sign(payload));
    }
}

// Customer scans QR code → Auto-fills all details → One-click pickup
```

---

### **3. Priority/Premium Lockers**

```java
public enum LockerPriority {
    STANDARD,
    PRIORITY,    // Closest to entrance
    PREMIUM      // Climate-controlled, secure
}

public class PriorityMatchingStrategy extends SizeMatchingStrategy {
    @Override
    public Locker findSuitableLocker(...) {
        // Try premium/priority lockers first for high-value packages
        if (pkg.isPremium()) {
            return findPremiumLocker(lockers, packageSize);
        }
        return super.findSuitableLocker(lockers, packageSize);
    }
}
```

---

### **4. Multi-Location Support**

```java
public class LockerNetwork {
    private Map<String, LockerService> locationToServiceMap;

    public String findNearestAvailableLocker(LatLng userLocation,
                                            LockerSize packageSize) {
        // Find all locations within 5km radius
        List<String> nearbyLocations = findLocationsWithinRadius(
            userLocation, 5.0);

        // Check availability at each location
        for (String location : nearbyLocations) {
            LockerService service = locationToServiceMap.get(location);
            if (service.hasAvailableLocker(packageSize)) {
                return location;
            }
        }

        return null; // No available lockers nearby
    }
}

// Use case: Suggest alternate pickup locations
```

---

## **🎯 Interview Questions & Answers**

### **Q1: How to make this thread-safe for concurrent deliveries?**

**Challenge:**
```java
// Race condition!
Thread1: Check locker S1 available → TRUE
Thread2: Check locker S1 available → TRUE  (Both see it available!)
Thread1: Assign package P1 to S1 → SUCCESS
Thread2: Assign package P2 to S1 → ERROR (already occupied!)
```

**Solution:**
```java
public synchronized String depositPackage(Package pkg) {
    // Option 1: Synchronize entire method (simple but slow)
}

// Better: Fine-grained locking
private final Object lockersLock = new Object();

public String depositPackage(Package pkg) {
    Locker suitableLocker;

    synchronized(lockersLock) {
        // Critical section - find and assign locker atomically
        suitableLocker = sizeMatchingStrategy.findSuitableLocker(
            lockers, pkg.getSize());
        if (suitableLocker != null) {
            suitableLocker.assignPackage(pkg);
        }
    }

    // OTP generation outside lock (doesn't need synchronization)
    if (suitableLocker != null) {
        String otp = OTPGenerator.generateOTP();
        pkg.setOtp(otp);
        sendOTPToUser(pkg.getRecipientPhone(), otp);
    }

    return suitableLocker != null ? suitableLocker.getLockerId() : null;
}

// Even Better: Use ConcurrentHashMap + AtomicReference
private Map<String, AtomicReference<LockerStatus>> lockerStatusMap;
```

**Interview Tip:** Discuss progression from simple to optimized!

---

### **Q2: What if OTP SMS fails to send?**

**Problem:**
- OTP generated and package deposited
- SMS gateway fails
- Customer can't pick up package!

**Solutions:**

**Immediate:**
```java
public String depositPackage(Package pkg) {
    Locker locker = findAndAssignLocker(pkg);
    String otp = OTPGenerator.generateOTP();

    try {
        smsService.sendOTP(pkg.getRecipientPhone(), otp);
        pkg.setOtp(otp);
        return locker.getLockerId();
    } catch (SMSException e) {
        // Rollback assignment
        locker.removePackage();
        throw new DeliveryException("Failed to send OTP");
    }
}
```

**Retry Mechanism:**
```java
// Exponential backoff retry
int maxRetries = 3;
for (int i = 0; i < maxRetries; i++) {
    try {
        smsService.sendOTP(phone, otp);
        break;
    } catch (SMSException e) {
        if (i == maxRetries - 1) {
            // Fall back to email
            emailService.sendOTP(pkg.getRecipientEmail(), otp);
        }
        Thread.sleep(1000 * Math.pow(2, i)); // Exponential backoff
    }
}
```

**Multi-Channel:**
```java
// Send via multiple channels
smsService.sendOTP(phone, otp);
emailService.sendOTP(email, otp);
pushNotificationService.send(userId, otp);
// Customer can use any channel
```

---

### **Q3: How to handle locker hardware failures?**

**Scenario:** Locker door won't open even with correct OTP

**Solution Architecture:**

**1. Manual Override:**
```java
public class LockerService {
    public String generateMasterOTP(String lockerId, String staffId) {
        // Requires staff authentication
        if (!isAuthorizedStaff(staffId)) {
            throw new UnauthorizedException();
        }

        // Generate time-limited master OTP
        String masterOTP = generateTimedOTP(lockerId, 15); // 15 min validity

        // Log for audit
        auditLog.record("Master OTP generated for " + lockerId +
                       " by staff " + staffId);

        return masterOTP;
    }
}
```

**2. Alternative Pickup Location:**
```java
// If locker broken, transfer package to nearby working locker
public String transferPackage(String fromLockerId, String toLockerId) {
    Locker fromLocker = findLocker(fromLockerId);
    Locker toLocker = findLocker(toLockerId);

    if (toLocker.getStatus() != LockerStatus.AVAILABLE) {
        return null;
    }

    Package pkg = fromLocker.removePackage();
    toLocker.assignPackage(pkg);

    // Update customer
    notifyCustomer(pkg, "Your package moved to locker " + toLockerId);

    // Mark broken locker
    fromLocker.setStatus(LockerStatus.MAINTENANCE);

    return toLockerId;
}
```

**3. Emergency Access:**
```java
// Physical override key + logging
public void emergencyOpen(String lockerId, String reason, String staffId) {
    Locker locker = findLocker(lockerId);

    // Create incident
    Incident incident = new Incident(lockerId, reason, staffId);
    incidentTracker.create(incident);

    // Take photo of package
    camera.captureImage(lockerId);

    // Manual extraction
    Package pkg = locker.removePackage();

    // Notify customer
    notifyCustomer(pkg, "Package retrieved via emergency access. " +
                       "Incident ID: " + incident.getId());
}
```

---

### **Q4: How to optimize locker utilization?**

**Metrics to Track:**
```java
public class LockerAnalytics {
    // Utilization rate
    public double getUtilizationRate() {
        int occupied = countByStatus(LockerStatus.OCCUPIED);
        int total = lockers.size();
        return (double) occupied / total * 100;
    }

    // Average pickup time
    public double getAveragePickupTime() {
        long totalTime = 0;
        for (Pickup pickup : pickupHistory) {
            Package pkg = pickup.getPackage();
            long pickupTime = pickup.getPickupTime() - pkg.getDeliveryTime();
            totalTime += pickupTime;
        }
        return totalTime / pickupHistory.size() / (1000.0 * 60 * 60); // Hours
    }

    // Size mismatch rate
    public double getSizeMismatchRate() {
        int mismatches = 0;
        for (Locker locker : lockers) {
            Package pkg = locker.getCurrentPackage();
            if (pkg != null && locker.getSize() != pkg.getSize()) {
                mismatches++;
            }
        }
        return (double) mismatches / countOccupied() * 100;
    }
}
```

**Optimization Strategies:**
```
1. Dynamic Capacity: Add more popular sizes based on data
2. Predictive Assignment: ML predicts pickup time, assigns closer lockers
3. Time-based Pricing: Charge more for longer retention
4. Smart Routing: Suggest less busy locker locations
5. Virtual Queuing: Hold package at facility if no locker available
```

---

### **Q5: How to handle high-value packages?**

**Solution: Enhanced Security Package**

```java
public class HighValuePackage extends Package {
    private String insuranceId;
    private double declaredValue;
    private boolean requiresSignature;
    private boolean requiresIDVerification;

    // Additional validation
    @Override
    public boolean canPickup(String otp, String phone, String idNumber) {
        if (!super.canPickup(otp, phone)) {
            return false;
        }

        // Require ID verification
        if (requiresIDVerification) {
            return verifyIDNumber(idNumber);
        }

        return true;
    }
}

public class SecureLocker extends Locker {
    private Camera camera;
    private WeightSensor sensor;

    @Override
    public boolean assignPackage(Package pkg) {
        // Take photo of package
        camera.captureImage();

        // Record weight
        double weight = sensor.measure();
        pkg.setInitialWeight(weight);

        return super.assignPackage(pkg);
    }

    @Override
    public Package removePackage() {
        Package pkg = getCurrentPackage();

        // Verify weight hasn't changed (tampering detection)
        double currentWeight = sensor.measure();
        if (Math.abs(currentWeight - pkg.getInitialWeight()) > 0.1) {
            alertSecurity("Weight mismatch detected in " + getLockerId());
        }

        // Take photo of recipient
        camera.captureImage();

        return super.removePackage();
    }
}
```

**Features:**
- Photo capture at deposit and pickup
- Weight verification (tampering detection)
- ID verification requirement
- Insurance tracking
- Enhanced audit trail

---

### **Q6: Design for multi-location (network of lockers)?**

**Architecture:**

```java
public class LockerLocation {
    private String locationId;
    private String address;
    private LatLng coordinates;
    private LockerService lockerService;
    private int totalCapacity;
}

public class LockerNetwork {
    private Map<String, LockerLocation> locations;
    private GeoSpatialIndex spatialIndex; // For nearby searches

    public List<LockerLocation> findNearbyLocations(LatLng userLocation,
                                                    double radiusKm) {
        return spatialIndex.findWithinRadius(userLocation, radiusKm);
    }

    public LockerLocation findBestLocation(Package pkg, LatLng preferredLocation) {
        List<LockerLocation> nearby = findNearbyLocations(preferredLocation, 5.0);

        // Score each location
        LockerLocation best = null;
        double bestScore = -1;

        for (LockerLocation location : nearby) {
            double score = calculateScore(location, pkg, preferredLocation);
            // Score factors:
            // - Distance from user (40%)
            // - Locker availability (30%)
            // - User rating of location (20%)
            // - Special features match (10%)

            if (score > bestScore) {
                bestScore = score;
                best = location;
            }
        }

        return best;
    }
}

// Spatial index for O(log n) nearby search
public interface GeoSpatialIndex {
    void insert(String locationId, LatLng coordinates);
    List<String> findWithinRadius(LatLng center, double radiusKm);
}

// Implementation: QuadTree, R-Tree, or Geohash
```

**Benefits:**
- Fast nearby location search
- Load balancing across locations
- Optimal location suggestion
- Real-time capacity visibility

---

### **Q7: How to test this system?**

**Unit Tests:**

```java
@Test
public void testSmartSizeMatching() {
    LockerService service = new LockerService(new DefaultSizeMatching());
    service.addLocker(new Locker("S1", LockerSize.SMALL));
    service.addLocker(new Locker("M1", LockerSize.MEDIUM));

    // Occupy small locker
    Package pkg1 = new Package("P1", "phone1", LockerSize.SMALL);
    String locker1 = service.depositPackage(pkg1);
    assertEquals("S1", locker1);

    // Next small package should get medium locker
    Package pkg2 = new Package("P2", "phone2", LockerSize.SMALL);
    String locker2 = service.depositPackage(pkg2);
    assertEquals("M1", locker2); // Smart upgrade!
}

@Test
public void testOTPValidation() {
    Package pkg = new Package("P1", "9876543210", LockerSize.SMALL);
    pkg.setOtp("123456");

    // Valid OTP
    assertTrue(OTPValidator.validateOTP(pkg, "123456"));

    // Invalid OTP
    assertFalse(OTPValidator.validateOTP(pkg, "000000"));

    // Null OTP
    assertFalse(OTPValidator.validateOTP(pkg, null));
}

@Test
public void testPhoneVerification() {
    // Test with correct and incorrect phone numbers
    // Test with null/empty phone numbers
    // Test with different formats
}

@Test
public void testExpiredPackageReturn() {
    // Mock time to simulate 73 hours passed
    // Verify package auto-returned
}
```

**Integration Tests:**

```java
@Test
public void testFullDepositPickupFlow() {
    // 1. Deposit package
    String lockerId = service.depositPackage(pkg);
    assertNotNull(lockerId);

    // 2. Verify locker occupied
    assertEquals(LockerStatus.OCCUPIED, locker.getStatus());

    // 3. Pickup with correct credentials
    assertTrue(service.pickupPackage(lockerId, otp, phone));

    // 4. Verify locker available again
    assertEquals(LockerStatus.AVAILABLE, locker.getStatus());
}
```

**Concurrency Tests:**

```java
@Test
public void testConcurrentDeposits() throws InterruptedException {
    // Create 10 threads trying to deposit simultaneously
    ExecutorService executor = Executors.newFixedThreadPool(10);
    List<Future<String>> futures = new ArrayList<>();

    for (int i = 0; i < 10; i++) {
        Package pkg = new Package("PKG" + i, "phone" + i, LockerSize.SMALL);
        futures.add(executor.submit(() -> service.depositPackage(pkg)));
    }

    // Wait for all
    executor.shutdown();
    executor.awaitTermination(10, TimeUnit.SECONDS);

    // Verify: No two packages in same locker
    Set<String> usedLockers = new HashSet<>();
    for (Future<String> future : futures) {
        String lockerId = future.get();
        if (lockerId != null) {
            assertFalse(usedLockers.contains(lockerId));
            usedLockers.add(lockerId);
        }
    }
}
```

---

### **Q8: How to scale to 10,000 locker locations?**

**Distributed Architecture:**

```
┌─────────────────────┐
│   Load Balancer     │
└──────────┬──────────┘
           │
    ┌──────┴──────┐
    │             │
┌───▼───┐    ┌───▼───┐
│Region │    │Region │
│North  │    │South  │
└───┬───┘    └───┬───┘
    │            │
┌───▼────────────▼───┐
│   Centralized DB   │
│  - Package data    │
│  - Locker status   │
│  - Audit logs      │
└────────────────────┘

┌────────────────────┐
│   Cache Layer      │
│   (Redis)          │
│ - Location → Status│
│ - PackageId → Info │
└────────────────────┘
```

**Implementation:**

```java
// Service per location, shared database
public class DistributedLockerService {
    private LockerService localService;
    private DatabaseClient dbClient;
    private CacheClient cacheClient;
    private String locationId;

    public String depositPackage(Package pkg) {
        // 1. Find locker locally
        Locker locker = localService.findAvailableLocker(pkg.getSize());

        // 2. Reserve in database (distributed lock)
        boolean reserved = dbClient.reserveLocker(
            locationId, locker.getLockerId(), pkg.getPackageId());

        if (!reserved) {
            return null; // Another location grabbed it
        }

        // 3. Assign locally
        locker.assignPackage(pkg);

        // 4. Update cache
        cacheClient.set("locker:" + locker.getLockerId(), "OCCUPIED");
        cacheClient.set("package:" + pkg.getPackageId(),
                       locationId + ":" + locker.getLockerId());

        // 5. Send OTP
        sendOTP(pkg);

        return locker.getLockerId();
    }

    public LockerLocation findPackageLocation(String packageId) {
        // Check cache first (fast!)
        String cached = cacheClient.get("package:" + packageId);
        if (cached != null) {
            return parseLocation(cached);
        }

        // Fallback to database
        return dbClient.findPackageLocation(packageId);
    }
}
```

**Sharding Strategy:**
```
- Shard by location (each location is independent)
- Global registry: packageId → locationId mapping
- Cache for hot data (recent packages)
- Message queue for notifications (decouple SMS sending)
```

---

### **Q9: How to prevent OTP brute-force attacks?**

**Multi-Layer Defense:**

**Layer 1: Rate Limiting**
```java
public class RateLimiter {
    private Map<String, AtomicInteger> attemptCounts;
    private Map<String, Long> lockoutExpiry;

    public boolean allowAttempt(String lockerId) {
        // Check if locked out
        Long expiry = lockoutExpiry.get(lockerId);
        if (expiry != null && System.currentTimeMillis() < expiry) {
            return false; // Still locked out
        }

        // Check attempt count
        AtomicInteger count = attemptCounts.computeIfAbsent(
            lockerId, k -> new AtomicInteger(0));

        if (count.incrementAndGet() > 3) {
            // Lock out for 30 minutes
            lockoutExpiry.put(lockerId,
                System.currentTimeMillis() + 30 * 60 * 1000);

            alertSecurity(lockerId);
            return false;
        }

        return true;
    }
}
```

**Layer 2: Progressive Delays**
```java
// Increase delay after each failed attempt
int failedAttempts = getFailedAttempts(lockerId);
long delay = (long) Math.pow(2, failedAttempts) * 1000; // Exponential
Thread.sleep(delay);

// Attempt 1: No delay
// Attempt 2: 2 second delay
// Attempt 3: 4 second delay
// Attempt 4: 8 second delay
// Makes brute force impractical
```

**Layer 3: Anomaly Detection**
```java
public class SecurityMonitor {
    public void detectAnomalies() {
        // Pattern 1: Multiple lockers, same phone (suspicious!)
        detectMultipleLockerAttempts();

        // Pattern 2: Sequential locker attempts (brute force!)
        detectSequentialAttempts();

        // Pattern 3: Off-hours attempts (3 AM access?)
        detectOffHoursActivity();

        // Pattern 4: Same OTP tried on multiple lockers
        detectOTPReuse();
    }
}
```

**Layer 4: Physical Security**
```java
// Camera captures face on failed attempt
if (!otpValid) {
    camera.captureFailedAttempt(lockerId, timestamp);
    securityLog.record("Failed OTP attempt at " + lockerId);

    if (failedCount > 3) {
        // Trigger alarm
        alarmSystem.trigger(locationId, lockerId);

        // Notify security staff
        notifySecurityTeam(locationId, "Potential brute force attack");
    }
}
```

---

### **Q10: How would you implement locker reservation?**

**Use Case:** Customer wants to ensure locker available at specific location

**Solution:**

```java
public class Reservation {
    private String reservationId;
    private String lockerId;
    private String userId;
    private long reservationTime;
    private long expiryTime;
    private ReservationStatus status;
}

public enum ReservationStatus {
    PENDING,     // Reserved but package not deposited
    CONFIRMED,   // Package deposited
    EXPIRED,     // Reservation expired
    CANCELLED    // Customer cancelled
}

public class ReservationService {
    private Map<String, Reservation> reservations;

    public Reservation reserveLocker(String userId, LockerSize size,
                                    String locationId, int durationMinutes) {
        // Find available locker
        Locker locker = lockerService.findAvailableLocker(locationId, size);
        if (locker == null) {
            return null;
        }

        // Create reservation
        Reservation reservation = new Reservation(
            generateReservationId(),
            locker.getLockerId(),
            userId,
            durationMinutes
        );

        // Mark locker as reserved (new state)
        locker.setStatus(LockerStatus.RESERVED);

        // Schedule expiry task
        scheduler.scheduleExpiry(reservation, durationMinutes);

        reservations.put(reservation.getId(), reservation);

        return reservation;
    }

    public boolean depositWithReservation(String reservationId, Package pkg) {
        Reservation reservation = reservations.get(reservationId);

        if (reservation == null || reservation.isExpired()) {
            return false;
        }

        // Verify user
        if (!reservation.getUserId().equals(pkg.getUserId())) {
            return false;
        }

        // Deposit in reserved locker
        Locker locker = findLocker(reservation.getLockerId());
        boolean success = locker.assignPackage(pkg);

        if (success) {
            reservation.setStatus(ReservationStatus.CONFIRMED);
        }

        return success;
    }
}
```

**Benefits:**
- Guaranteed locker availability
- Better user experience
- Revenue opportunity (charge for reservation)
- Predictable capacity planning

---

### **Q11: What about notification system?**

**Observer Pattern Implementation:**

```java
public interface LockerEventObserver {
    void onPackageDeposited(Package pkg, Locker locker);
    void onPackagePickedUp(Package pkg, Locker locker);
    void onPackageExpired(Package pkg, Locker locker);
    void onSecurityAlert(String lockerId, String reason);
}

public class NotificationObserver implements LockerEventObserver {
    private SMSService smsService;
    private EmailService emailService;
    private PushNotificationService pushService;

    @Override
    public void onPackageDeposited(Package pkg, Locker locker) {
        String message = String.format(
            "Your package is ready at locker %s. OTP: %s. Valid for 24 hours.",
            locker.getLockerId(), pkg.getOtp());

        // Send via multiple channels
        smsService.send(pkg.getRecipientPhone(), message);
        emailService.send(pkg.getRecipientEmail(), message);
        pushService.send(pkg.getUserId(), message);
    }

    @Override
    public void onPackageExpired(Package pkg, Locker locker) {
        String message = "Your package was returned (not picked up within 72 hours). " +
                        "Redelivery initiated.";
        notifyCustomer(pkg, message);
    }
}

public class AnalyticsObserver implements LockerEventObserver {
    @Override
    public void onPackagePickedUp(Package pkg, Locker locker) {
        long pickupTime = System.currentTimeMillis() - pkg.getDeliveryTime();
        metrics.recordPickupTime(pickupTime);
        metrics.incrementSuccessfulPickups();

        // Size mismatch tracking
        if (pkg.getSize() != locker.getSize()) {
            metrics.incrementSizeMismatch();
        }
    }
}

// LockerService now notifies all observers
public class LockerService {
    private List<LockerEventObserver> observers = new ArrayList<>();

    public void addObserver(LockerEventObserver observer) {
        observers.add(observer);
    }

    private void notifyPackageDeposited(Package pkg, Locker locker) {
        for (LockerEventObserver observer : observers) {
            observer.onPackageDeposited(pkg, locker);
        }
    }
}
```

---

### **Q12: How to handle returns and reverse logistics?**

**Complete Return Flow:**

```java
public class ReturnService {
    private LockerService lockerService;
    private LogisticsService logisticsService;

    public void processReturns() {
        // 1. Find expired packages
        List<Package> expiredPackages = lockerService.processReturns();

        for (Package pkg : expiredPackages) {
            // 2. Create return shipment
            ReturnShipment shipment = createReturnShipment(pkg);

            // 3. Schedule pickup
            logisticsService.schedulePickup(
                shipment,
                lockerService.getLocation(),
                Priority.NORMAL
            );

            // 4. Notify customer
            notifyCustomerAboutReturn(pkg, shipment);

            // 5. Process refund if applicable
            if (pkg.isRefundable()) {
                refundService.processRefund(pkg);
            }

            // 6. Update inventory
            inventoryService.markAsReturned(pkg);
        }
    }

    private void notifyCustomerAboutReturn(Package pkg, ReturnShipment shipment) {
        String message = String.format(
            "Your package %s was returned (not picked up). " +
            "Refund initiated. Tracking: %s",
            pkg.getPackageId(), shipment.getTrackingNumber()
        );

        smsService.send(pkg.getRecipientPhone(), message);
    }
}

// Scheduled job runs every hour
@Scheduled(cron = "0 0 * * * *") // Every hour
public void checkExpiredPackages() {
    returnService.processReturns();
}
```

**Metrics to Track:**
- Return rate (target: <5%)
- Average time to return
- Return reasons (timeout, refused, damaged)
- Cost per return
- Customer satisfaction impact

---

## **⚠️ Known Limitations & Trade-offs**

### **1. No Distributed Locking**
**Current:** In-memory only, single location
**Issue:** Concurrent access not handled
**Fix:** Redis distributed locks, database row-level locking

### **2. No Persistent Storage**
**Current:** Data lost on restart
**Issue:** Production requires durability
**Fix:** Database (PostgreSQL, DynamoDB), event sourcing

### **3. Simple OTP Generation**
**Current:** 6-digit numeric OTP
**Issue:** Could be brute-forced
**Fix:** Alphanumeric codes, rate limiting, biometric

### **4. No Real-time Notifications**
**Current:** Simulated SMS sending
**Issue:** Production needs actual SMS/Email/Push
**Fix:** Integrate Twilio, SendGrid, FCM

### **5. No Analytics Dashboard**
**Current:** Basic status printing
**Issue:** Operations need insights
**Fix:** Grafana dashboards, CloudWatch metrics, alerts

---

## **📚 Key Takeaways**

### **Design Patterns Used:**
- ✅ **Chain of Responsibility** - Size matching (primary pattern!)
- ✅ **Strategy Pattern** - Flexible matching rules
- ✅ **State Pattern** - Locker lifecycle management
- ✅ **Observer Pattern** - Notifications (extension)
- ✅ **Factory Pattern** - Locker creation (potential)

### **SOLID Principles:**
- ✅ Single Responsibility - Each class has one job
- ✅ Open/Closed - Easy to add new strategies
- ✅ Liskov Substitution - Polymorphism works correctly
- ✅ Interface Segregation - Minimal interfaces
- ✅ Dependency Inversion - Depend on abstractions

### **Key Concepts:**
- Smart size matching algorithm
- Secure OTP generation and validation
- Multi-factor verification (OTP + phone)
- Auto-return policy
- State management
- Audit trail for security

### **Interview Focus:**
- Explain Chain of Responsibility benefit
- Discuss security measures
- Handle edge cases (SMS failure, hardware failure)
- Scale to distributed system
- Testing strategies

---

## **🎓 What You Should Master**

### **Before Interview:**
1. ✅ Explain Chain of Responsibility with code example
2. ✅ Draw class diagram from memory
3. ✅ Code size matching algorithm in 5 minutes
4. ✅ Discuss 3 security measures for OTP
5. ✅ Explain smart size upgrade logic
6. ✅ Handle concurrent deposit scenario
7. ✅ Design distributed locker network
8. ✅ Implement one extension (refrigeration, QR code, etc.)
9. ✅ Answer all Q&A sections confidently
10. ✅ Discuss scalability to 10,000 locations

### **Practice Exercises:**
1. Implement StrictSizeMatching (no upgrades)
2. Add retry limit (3 attempts max)
3. Implement QR code generation
4. Add locker reservation system
5. Create analytics dashboard mock

### **Pro Tips:**
- Start with requirements clarification (sizes, retention policy, security)
- Draw state diagram early
- Emphasize Chain of Responsibility pattern
- Discuss trade-offs openly (size upgrade vs capacity)
- Mention production concerns (distributed locks, monitoring)
- Show security awareness (rate limiting, audit logs)

---

## **⏱️ Time to Master**

**Study Time:** 3-4 hours
**Practice Time:** 2-3 hours
**Total:** 5-7 hours

**Difficulty:** ⭐⭐⭐ (Medium)
**Interview Frequency:** ⭐⭐⭐ (High - Amazon, FedEx, delivery companies)

**Common Companies:** Amazon, FedEx, UPS, DHL, food delivery apps with locker pickup

---

## **✅ System is Interview-Ready!**

This smart locker system demonstrates:
- Clean architecture with clear separation of concerns
- Multiple design patterns working together
- Security-first approach
- Scalability considerations
- Real-world production thinking

**You're ready to ace this interview question!** 🚀
