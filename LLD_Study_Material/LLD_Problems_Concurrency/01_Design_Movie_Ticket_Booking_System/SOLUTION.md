# Movie Ticket Booking System - Comprehensive Concurrency Solution 🎬🔐

## **Problem Statement**

Design a movie ticket booking system (like BookMyShow/Fandango) that handles:
- Multiple users booking seats simultaneously
- Prevent double booking (race condition)
- Temporary seat locking during payment
- Timeout-based lock release
- Multiple payment methods
- Thread-safe operations

**The Core Challenge:** When 1000 users try to book the last seat simultaneously, exactly ONE should succeed. How do you guarantee this?

---

## **🚨 The Concurrency Problem**

### **Scenario: The Race Condition**

```
Time    User A                  User B                  Seat State
────────────────────────────────────────────────────────────────
T0      Select Seat 5A          Select Seat 5A          Available
T1      Check available: YES    -                       Available
T2      -                       Check available: YES    Available
T3      Book Seat 5A            -                       Booked by A
T4      -                       Book Seat 5A            Booked by B ❌

Result: DOUBLE BOOKING! Both users think they got the seat!
```

**Without proper locking, this WILL happen in production.**

---

## **🎯 Our Solution: Pessimistic Locking**

### **Why Pessimistic Locking?**

**Options Considered:**

1. **Optimistic Locking (version-based)**
   - Check version before commit
   - ❌ Too many conflicts for hot seats
   - ❌ Poor user experience (failed bookings)

2. **Database-level Locking**
   - SELECT FOR UPDATE
   - ❌ Holds DB connections during user payment
   - ❌ Doesn't scale well

3. **Pessimistic Locking (Our Choice)** ✅
   - Lock seat immediately when selected
   - Hold lock during payment (with timeout)
   - Release on success/failure/timeout
   - ✅ Clear user feedback
   - ✅ Prevents conflicts upfront

---

## **🏗️ Architecture & Design Patterns**

### **Pattern 1: Pessimistic Locking with Timeout**

**Core Classes:**

```java
public class SeatLock {
    private Seat seat;
    private Show show;
    private User lockedBy;
    private Date lockTime;
    private Integer timeoutInSeconds;  // Auto-release

    public boolean isLockExpired() {
        long currentTime = System.currentTimeMillis();
        long lockTime = this.lockTime.getTime();
        long lockDuration = (currentTime - lockTime) / 1000;
        return lockDuration >= timeoutInSeconds;
    }
}
```

**Locking Strategy:**

```java
public class SeatLockProvider implements ISeatLockProvider {
    private final Map<Show, Map<Seat, SeatLock>> locks = new HashMap<>();

    public synchronized void lockSeats(Show show, List<Seat> seats, User user)
        throws Exception {

        // 1. Check if ANY seat already locked
        for (Seat seat : seats) {
            if (isSeatLocked(show, seat)) {
                throw new Exception("Seat already locked by another user");
            }
        }

        // 2. Lock ALL seats atomically
        for (Seat seat : seats) {
            SeatLock lock = new SeatLock(seat, show, 300); // 5 min timeout
            lock.setLockedBy(user);
            lock.setLockTime(new Date());
            locks.get(show).put(seat, lock);
        }
    }

    public synchronized void unlockSeats(Show show, List<Seat> seats, User user) {
        for (Seat seat : seats) {
            if (validateLock(show, seat, user)) {
                locks.get(show).remove(seat);
            }
        }
    }
}
```

**Key Points:**
- `synchronized` keyword ensures atomic operations
- All-or-nothing locking (transaction semantics)
- Timeout prevents indefinite locks
- User validation prevents lock stealing

---

### **Pattern 2: Strategy Pattern (Payment)**

```java
public interface PaymentStrategy {
    boolean processPayment(double amount);
}

public class DebitCardStrategy implements PaymentStrategy {
    @Override
    public boolean processPayment(double amount) {
        // Debit card processing logic
        return true;
    }
}

public class UpiStrategy implements PaymentStrategy {
    @Override
    public boolean processPayment(double amount) {
        // UPI processing logic
        return true;
    }
}
```

---

### **Pattern 3: Service Layer (Separation of Concerns)**

**BookingService:**
```java
public class BookingService {
    private final Map<String, Booking> bookings = new HashMap<>();

    // Step 1: Create booking (locks seats)
    public Booking createBooking(User user, Show show, List<Seat> seats)
        throws Exception {

        // Check if any seat already booked
        if (isAnySeatAlreadyBooked(show, seats)) {
            throw new Exception("Seats already booked");
        }

        // Create booking with PENDING status
        String bookingId = UUID.randomUUID().toString();
        Booking booking = new Booking(bookingId, show, seats, user);
        booking.setStatus(BookingStatus.PENDING);

        bookings.put(bookingId, booking);
        return booking;
    }

    // Step 2: Confirm booking (after payment)
    public void confirmBooking(Booking booking, User user) throws Exception {
        if (!validateLock(booking.getShow(), booking.getSeats(), user)) {
            throw new Exception("Lock expired or invalid");
        }

        // Mark seats as booked
        for (Seat seat : booking.getSeats()) {
            seat.setBooked(true);
        }

        booking.setStatus(BookingStatus.CONFIRMED);

        // Release locks
        unlockSeats(booking.getShow(), booking.getSeats(), user);
    }
}
```

---

## **📐 Complete Flow Diagram**

```
┌──────────┐
│   User   │
└────┬─────┘
     │
     ▼
┌─────────────────────┐
│ 1. Select Seats     │
│    (UI)             │
└──────┬──────────────┘
       │
       ▼
┌─────────────────────────────┐
│ 2. BookingService           │
│    createBooking()          │
│    ├─ Check availability    │
│    ├─ Lock seats (5 min)    │◄─── SeatLockProvider
│    └─ Create PENDING booking│
└──────┬──────────────────────┘
       │
       ▼
┌─────────────────────┐
│ 3. User Pays        │
│    (0-5 minutes)    │
└──────┬──────────────┘
       │
   ┌───┴────┐
   │        │
   ▼        ▼
SUCCESS   TIMEOUT
   │        │
   │        └──► Lock expires → Seats released
   │
   ▼
┌─────────────────────────────┐
│ 4. confirmBooking()         │
│    ├─ Validate lock         │
│    ├─ Process payment       │◄─── PaymentStrategy
│    ├─ Mark seats BOOKED     │
│    ├─ Release locks         │
│    └─ Status: CONFIRMED     │
└─────────────────────────────┘
```

---

## **🔑 Key Design Decisions**

### **Decision 1: Synchronized Methods vs Locks**

**What:** Use `synchronized` keyword instead of `ReentrantLock`

**Why:**
```java
// Option 1: synchronized (simpler)
public synchronized void lockSeats(...) {
    // Atomic operation
}

// Option 2: ReentrantLock (more flexible)
private final ReentrantLock lock = new ReentrantLock();
public void lockSeats(...) {
    lock.lock();
    try {
        // Operation
    } finally {
        lock.unlock();  // Must handle in finally!
    }
}
```

**Choice:** `synchronized` for this problem
- ✅ Simpler, less error-prone
- ✅ Sufficient for coarse-grained locking
- ✅ JVM handles lock release automatically
- ❌ Less flexible (no try-lock, no interruptibility)

**When to use ReentrantLock:**
- Need try-lock with timeout
- Need fairness guarantees
- Need lock interruptibility

---

### **Decision 2: Timeout-Based Lock Release**

**What:** Locks auto-expire after 5 minutes

**Why:**
```java
// Without timeout:
User locks seat → Network fails → Lock held forever ❌

// With timeout:
User locks seat → Network fails → Lock expires after 5 min → Seat available ✅
```

**Benefits:**
- Prevents indefinite locks (network failures, browser crashes)
- Balances user experience (enough time to pay)
- System automatically recovers

**Trade-off:**
- If payment takes > 5 min → Lock expires → Booking fails
- Solution: Show countdown timer to user, allow lock extension

---

### **Decision 3: Two-Phase Booking**

**What:** Separate "create" and "confirm" steps

**Phase 1: createBooking()**
- Lock seats
- Create PENDING booking
- Return booking ID

**Phase 2: confirmBooking()**
- Validate lock still held
- Process payment
- Mark CONFIRMED
- Release locks

**Why:**
- Payment can fail → Don't book seats for failed payments
- Network timeout → Lock expires automatically
- User can cancel → Easy to unlock seats
- Clear audit trail (PENDING → CONFIRMED/FAILED)

**Alternative (Rejected):**
```java
// ❌ Single-phase: Lock + Book + Pay in one call
public void bookSeats(...) {
    lockSeats();      // Point of no return!
    processPayment(); // If this fails, seats are stuck!
}
```

---

### **Decision 4: Show-Level Lock Isolation**

**What:** Separate lock maps per show

```java
Map<Show, Map<Seat, SeatLock>> locks;
```

**Why:**
- Locks for Show A don't block Show B
- Better concurrency (parallel bookings for different shows)
- Memory efficient (only active shows in memory)

**Interview Question:**
> "What if two users book the same seat in different shows?"

**Answer:**
> "Impossible! The Show is part of the lock key. The same physical seat can exist in multiple shows (different timings), but each (Show, Seat) pair has its own lock. Locks for Show1 at 3pm and Show2 at 6pm are completely independent."

---

## **💡 SOLID Principles Applied**

### **S - Single Responsibility**
- `SeatLockProvider` - Only manages locks
- `BookingService` - Only manages bookings
- `PaymentController` - Only processes payments
- `Show`, `Seat`, `Booking` - Data models only

### **O - Open/Closed**
- Adding new payment method: Create new `PaymentStrategy` implementation
- Adding new seat category: Extend `SeatCategory` enum
- No modification to existing code

### **L - Liskov Substitution**
- Any `PaymentStrategy` can replace another
- Client code works with interface, not concrete classes

### **I - Interface Segregation**
- `ISeatLockProvider` - Only lock operations
- `PaymentStrategy` - Only payment processing
- No fat interfaces

### **D - Dependency Inversion**
- `BookingService` depends on `ISeatLockProvider` interface
- High-level booking logic doesn't depend on low-level lock implementation
- Can swap in-memory locks for Redis-based distributed locks

---

## **🎭 Scenario Walkthroughs**

### **Scenario 1: Successful Booking (No Conflicts)**

```
User: Alice
Show: Avengers Endgame, 7:00 PM
Seats: A1, A2

Step 1: Select seats (t=0s)
  ├─ BookingService.createBooking(alice, show, [A1, A2])
  ├─ Check: A1 available? YES
  ├─ Check: A2 available? YES
  ├─ Lock A1 (expires at t=300s)
  ├─ Lock A2 (expires at t=300s)
  └─ Return booking ID: "abc-123"

Step 2: Payment page (t=10s)
  └─ Alice enters card details

Step 3: Process payment (t=60s)
  ├─ PaymentController.processPayment(booking, DebitCard)
  ├─ Validate locks still held: YES (60s < 300s)
  ├─ Charge card: SUCCESS
  ├─ BookingService.confirmBooking(booking, alice)
  ├─ Mark A1 as BOOKED
  ├─ Mark A2 as BOOKED
  ├─ Status: CONFIRMED
  └─ Release locks on A1, A2

Step 4: Confirmation (t=65s)
  └─ Send confirmation email with ticket
```

---

### **Scenario 2: Race Condition Prevented**

```
User A: Alice             User B: Bob
Show: Same show, 7:00 PM
Both want: Seat A1

Timeline:
────────────────────────────────────────────────────────
T0:  Alice: Select A1
     ├─ Check A1 available: YES
     ├─ Lock A1 by Alice
     └─ Create booking-1

T1:  Bob: Select A1
     ├─ Check A1 available: NO (locked by Alice!) ❌
     └─ Error: "Seat already locked"

T2:  Bob: Select A2 instead
     ├─ Check A2 available: YES
     ├─ Lock A2 by Bob
     └─ Create booking-2

T60: Alice: Pay & confirm
     └─ A1 marked BOOKED, lock released

T70: Bob: Pay & confirm
     └─ A2 marked BOOKED, lock released

Result: ✅ No double booking! Race condition prevented.
```

---

### **Scenario 3: Lock Timeout (Payment Too Slow)**

```
User: Charlie
Show: Spider-Man, 9:00 PM
Seat: B5

Timeline:
────────────────────────────────────────────────────────
T0:   Select B5
      ├─ Lock B5 (expires at T=300)
      └─ Booking ID: "xyz-789"

T10:  Payment page loads

T50:  Charlie goes to make coffee ☕

T310: Charlie returns, clicks "Pay"
      ├─ Validate lock: EXPIRED ❌
      ├─ Error: "Booking expired, please try again"
      └─ Status: FAILED

T320: Another user (David) selects B5
      ├─ Check B5: Lock expired, now available ✅
      ├─ Lock B5 by David
      └─ Success!

Result: ✅ System recovered automatically. No indefinite lock.
```

---

### **Scenario 4: Concurrent Bookings (Different Seats)**

```
Thread 1: Alice → A1
Thread 2: Bob   → A2
Thread 3: Carol → A3

Time     Thread 1              Thread 2              Thread 3
──────────────────────────────────────────────────────────────
T0       Lock A1 ✅           -                     -
T1       -                     Lock A2 ✅           -
T2       -                     -                     Lock A3 ✅
T3       Payment...            Payment...            Payment...
T60      Confirm A1 ✅        -                     -
T65      -                     Confirm A2 ✅        -
T70      -                     -                     Confirm A3 ✅

Result: ✅ All succeed in parallel! No blocking.
```

**Why No Blocking?**
- Different seats = Different lock objects
- `synchronized` on `lockSeats()` is brief (just to update map)
- No long-held locks during payment
- High concurrency for different seats

---

## **🚀 Extensions & Enhancements**

### **1. Distributed Lock (Redis-based)**

**Problem:** Current solution only works for single server

**Solution:**
```java
public class RedisLockProvider implements ISeatLockProvider {
    private final RedisClient redis;

    @Override
    public void lockSeats(Show show, List<Seat> seats, User user)
        throws Exception {

        String lockKey = "lock:" + show.getId() + ":" + seat.getId();

        // Try to acquire lock with expiry
        boolean acquired = redis.set(
            lockKey,
            user.getId(),
            SetParams.setParams().nx().ex(300) // 5 min expiry
        );

        if (!acquired) {
            throw new Exception("Seat already locked");
        }
    }

    @Override
    public void unlockSeats(Show show, List<Seat> seats, User user) {
        for (Seat seat : seats) {
            String lockKey = "lock:" + show.getId() + ":" + seat.getId();

            // Only unlock if still owned by this user (Lua script for atomicity)
            String script =
                "if redis.call('get', KEYS[1]) == ARGV[1] then " +
                "  return redis.call('del', KEYS[1]) " +
                "else " +
                "  return 0 " +
                "end";

            redis.eval(script, List.of(lockKey), List.of(user.getId()));
        }
    }
}
```

**Benefits:**
- ✅ Works across multiple servers
- ✅ Redis handles expiry automatically
- ✅ Lua script ensures atomic unlock
- ✅ High performance

---

### **2. Lock Extension (User Needs More Time)**

```java
public boolean extendLock(Show show, Seat seat, User user, int extraSeconds) {
    synchronized(locks) {
        SeatLock lock = locks.get(show).get(seat);

        if (lock == null || !lock.getLockedBy().equals(user)) {
            return false; // Not locked by this user
        }

        if (lock.isLockExpired()) {
            return false; // Already expired
        }

        // Add extra time (max 5 minutes total extension)
        int currentTimeout = lock.getTimeoutInSeconds();
        lock.setTimeoutInSeconds(Math.min(currentTimeout + extraSeconds, 600));
        return true;
    }
}
```

**UI:**
```
┌────────────────────────────────┐
│ Time remaining: 1:23           │
│ [Extend by 2 minutes]          │
└────────────────────────────────┘
```

---

### **3. Notification System (Observer Pattern)**

```java
public interface BookingObserver {
    void onBookingCreated(Booking booking);
    void onBookingConfirmed(Booking booking);
    void onBookingFailed(Booking booking);
    void onLockExpired(SeatLock lock);
}

public class EmailNotificationObserver implements BookingObserver {
    @Override
    public void onBookingConfirmed(Booking booking) {
        sendEmail(booking.getUser(), "Booking confirmed!", generateTicket(booking));
    }

    @Override
    public void onLockExpired(SeatLock lock) {
        sendEmail(lock.getLockedBy(), "Booking expired - seats released");
    }
}

public class SMSNotificationObserver implements BookingObserver {
    @Override
    public void onBookingConfirmed(Booking booking) {
        sendSMS(booking.getUser(), "Ticket booked! Show ID: " + booking.getShow().getId());
    }
}
```

---

### **4. Seat Hold Queue (Waitlist)**

```java
public class SeatWaitlistService {
    private final Map<Show, Map<Seat, Queue<User>>> waitlist = new HashMap<>();

    public void addToWaitlist(Show show, Seat seat, User user) {
        waitlist
            .computeIfAbsent(show, k -> new HashMap<>())
            .computeIfAbsent(seat, k -> new LinkedList<>())
            .offer(user);
    }

    public void onLockExpired(Show show, Seat seat) {
        Queue<User> queue = waitlist.get(show).get(seat);

        if (queue != null && !queue.isEmpty()) {
            User nextUser = queue.poll();
            notifyUser(nextUser, "Seat " + seat.getId() + " is now available!");
        }
    }
}
```

---

## **🎯 Interview Questions & Answers**

### **Q1: How do you prevent double booking? Walk me through the concurrency mechanism.**

**Answer:**
```
We use pessimistic locking with the synchronized keyword. Here's the critical section:

public synchronized void lockSeats(Show show, List<Seat> seats, User user) {
    // Step 1: Check if ANY seat is already locked
    for (Seat seat : seats) {
        if (isSeatLocked(show, seat)) {
            throw new Exception("Already locked");
        }
    }

    // Step 2: If check passes, lock ALL seats atomically
    for (Seat seat : seats) {
        locks.get(show).put(seat, new SeatLock(seat, user, 300));
    }
}

The 'synchronized' keyword ensures that only ONE thread can execute this method at a time.
This makes the check-then-lock operation atomic:

Thread A acquires lock → checks → locks seat → releases method lock
Only now can Thread B acquire lock → checks (sees seat locked) → throws exception

Without synchronized, both threads could check simultaneously and both see "available".
```

---

### **Q2: What if the database write fails after you've locked the seat?**

**Answer:**
```
Great question! This is a transaction management issue. We handle it with a two-phase approach:

Phase 1: In-memory lock (fast, immediately reserved for user)
Phase 2: Database write (slower, durable storage)

If DB write fails in Phase 2:
1. Release the in-memory lock immediately
2. Throw exception to user
3. Seat becomes available again for others

Code:
public Booking createBooking(...) throws Exception {
    // Phase 1: Lock in memory
    lockSeats(show, seats, user);

    try {
        // Phase 2: Persist to database
        booking = bookingRepository.save(new Booking(...));
        return booking;
    } catch (DatabaseException e) {
        // Rollback: Release locks
        unlockSeats(show, seats, user);
        throw new Exception("Booking failed - please try again");
    }
}

This ensures consistency: Memory locks always match database state.
```

---

### **Q3: How would you handle deadlocks?**

**Answer:**
```
Deadlock Scenario:
User A: Wants seats [A1, A2]
User B: Wants seats [A2, A1]  (different order!)

Timeline:
T0: Thread A locks A1
T1: Thread B locks A2
T2: Thread A waits for A2 (held by B) 🔒
T3: Thread B waits for A1 (held by A) 🔒
→ DEADLOCK!

Solution 1: Lock Ordering (Best for our case)
Always lock seats in sorted order (by seat ID):

public void lockSeats(Show show, List<Seat> seats, User user) {
    // Sort seats by ID before locking!
    seats.sort(Comparator.comparing(Seat::getId));

    for (Seat seat : seats) {
        acquireLock(seat);
    }
}

Now:
Thread A locks: A1 → A2
Thread B locks: A1 (waits) → A2
No cycle, no deadlock!

Solution 2: Timeout (Backup)
Try to acquire lock with timeout:

if (!tryLock(seat, 5, TimeUnit.SECONDS)) {
    releaseAllLocks();
    throw new Exception("Timeout - please retry");
}

Solution 3: All-or-nothing (Current approach)
Our synchronized method locks ALL seats atomically.
Can't have partial locks → no deadlock possible.
```

---

### **Q4: How do you test this for race conditions?**

**Answer:**
```
Testing concurrent code requires stress testing with multiple threads:

@Test
public void testConcurrentBooking_SameSeat() throws Exception {
    // Setup
    Show show = new Show(...);
    Seat seat = new Seat("A1");
    User user1 = new User("Alice");
    User user2 = new User("Bob");

    ExecutorService executor = Executors.newFixedThreadPool(2);
    CountDownLatch latch = new CountDownLatch(1);

    // Track results
    AtomicInteger successCount = new AtomicInteger(0);
    AtomicInteger failureCount = new AtomicInteger(0);

    // Thread 1: Alice books
    executor.submit(() -> {
        try {
            latch.await(); // Wait for simultaneous start
            bookingService.createBooking(user1, show, List.of(seat));
            successCount.incrementAndGet();
        } catch (Exception e) {
            failureCount.incrementAndGet();
        }
    });

    // Thread 2: Bob books
    executor.submit(() -> {
        try {
            latch.await(); // Wait for simultaneous start
            bookingService.createBooking(user2, show, List.of(seat));
            successCount.incrementAndGet();
        } catch (Exception e) {
            failureCount.incrementAndGet();
        }
    });

    // Start both threads simultaneously
    latch.countDown();
    executor.shutdown();
    executor.awaitTermination(5, TimeUnit.SECONDS);

    // Assert
    assertEquals(1, successCount.get(), "Exactly one booking should succeed");
    assertEquals(1, failureCount.get(), "Exactly one booking should fail");
}

Run this test 1000 times to catch rare race conditions!

Additional tests:
- 100 threads booking different seats (all should succeed)
- 50 threads booking same seat (only 1 succeeds)
- Lock timeout test (wait 6 minutes, lock should expire)
- Deadlock test (multiple seats, random order)
```

---

### **Q5: How would you scale this to multiple servers (distributed system)?**

**Answer:**
```
Current problem: In-memory locks don't work across servers

Server 1: User A locks Seat A1 (in Server 1's memory)
Server 2: User B locks Seat A1 (in Server 2's memory)
→ Both think they have the lock! ❌

Solution: Distributed Locking with Redis

Architecture:
┌─────────┐     ┌─────────┐     ┌─────────┐
│ Server1 │────►│  Redis  │◄────│ Server2 │
└─────────┘     │ (Single │     └─────────┘
                │  Source │
                │ of Truth)│
                └─────────┘

Implementation:
public class RedisLockProvider implements ISeatLockProvider {

    @Override
    public void lockSeats(Show show, List<Seat> seats, User user) {
        for (Seat seat : seats) {
            String key = "lock:" + show.getId() + ":" + seat.getId();

            // SET key value NX EX 300
            // NX = Only set if not exists
            // EX = Expire in 300 seconds
            boolean locked = redis.set(key, user.getId(), "NX", "EX", 300);

            if (!locked) {
                // Another server already locked this seat
                rollbackLocks(seats, user);
                throw new Exception("Seat locked by another user");
            }
        }
    }
}

Redis guarantees:
1. Atomic SET NX (only one server succeeds)
2. Automatic expiry (no manual cleanup needed)
3. High availability (Redis Sentinel/Cluster)

Trade-offs:
✅ Works across multiple servers
✅ Automatic timeout handling
✅ High performance (Redis is in-memory)
❌ Network latency (~1ms per lock)
❌ Single point of failure (mitigated by Redis Cluster)
❌ Complexity increases

Alternative: Database-level locking (pessimistic row locks)
- SELECT * FROM seats WHERE id = ? FOR UPDATE
- Works but holds DB connections during payment (poor scalability)
```

---

### **Q6: What if payment takes longer than the lock timeout?**

**Answer:**
```
Scenario:
User locks seat (5 min timeout) → Payment gateway slow → Takes 6 minutes → Lock expired!

Solutions:

1. Extend Lock (User-initiated)
   UI shows countdown: "Time remaining: 2:00"
   Button: "Need more time? +2 minutes"

   Backend:
   public boolean extendLock(Booking booking, User user) {
       if (validateLock(booking)) {
           lock.setTimeoutInSeconds(lock.getTimeout() + 120);
           return true;
       }
       return false;
   }

2. Smart Timeout (Adaptive)
   Track average payment time per gateway:
   - Credit card: 30 seconds
   - UPI: 2 minutes
   - Net banking: 5 minutes

   Adjust timeout based on payment method:
   public int getTimeoutForPaymentMethod(PaymentMethod method) {
       return switch(method) {
           case CREDIT_CARD -> 120;  // 2 min
           case UPI -> 300;          // 5 min
           case NET_BANKING -> 600;  // 10 min
       };
   }

3. Grace Period (Production approach)
   Lock expires at T=300, but allow payment until T=330 (30s grace)

   public void confirmBooking(Booking booking) {
       if (isLockExpired(booking)) {
           if (isWithinGracePeriod(booking)) {
               // Allow, but log warning
               logger.warn("Payment during grace period: " + booking.getId());
           } else {
               throw new Exception("Booking expired");
           }
       }
       // Proceed with booking
   }

4. Pre-authorization (Best for critical systems)
   Authorize card at lock time (hold amount, don't charge)
   Lock timeout = 5 min
   Authorization timeout = 10 min

   If lock expires but auth is valid → Can still complete booking
   No money charged yet, so safe to extend

Our recommendation: Combination of #1 (extend) + #3 (grace period)
```

---

### **Q7: How do you ensure lock release on server crash?**

**Answer:**
```
Problem: Server crashes while holding locks → Locks never released → Seats unavailable forever

Solutions:

1. Timeout-based expiry (Current approach) ✅
   Lock has expiry time → Auto-released even if server crashes

   SeatLock.isExpired() checks current time vs lock time
   Background job periodically cleans expired locks:

   @Scheduled(fixedRate = 60000) // Every minute
   public void cleanExpiredLocks() {
       for (Show show : locks.keySet()) {
           locks.get(show).entrySet()
               .removeIf(entry -> entry.getValue().isExpired());
       }
   }

2. Database-backed locks (Durable)
   Write lock state to database with expiry timestamp

   CREATE TABLE seat_locks (
       show_id INT,
       seat_id INT,
       user_id INT,
       lock_time TIMESTAMP,
       expires_at TIMESTAMP,
       PRIMARY KEY (show_id, seat_id)
   );

   On server restart:
   - Read locks from database
   - Remove expired locks
   - Restore active locks to memory

   public void restoreLocksOnStartup() {
       List<SeatLock> dbLocks = lockRepository.findAll();
       for (SeatLock lock : dbLocks) {
           if (lock.isExpired()) {
               lockRepository.delete(lock);
           } else {
               locks.put(lock.getShow(), lock.getSeat(), lock);
           }
       }
   }

3. Redis-based locks (Production)
   Redis automatically removes expired keys (EXPIRE command)
   Even if application crashes, Redis cleans up

   redis.setex("lock:show123:seatA1", 300, "user456");
   // Automatically deleted after 300 seconds, no cleanup needed

4. Health check + Failover
   Load balancer monitors server health
   If server unresponsive for 30 seconds → Mark as dead
   Transfer its locks to healthy server

   But simple timeout is usually sufficient!

Our approach: Timeout (#1) + Redis for production (#3)
No manual cleanup needed, system self-heals
```

---

### **Q8: How would you handle partial payment failures (transaction rollback)?**

**Answer:**
```
Scenario: User books 5 seats, payment succeeds for 3, fails for 2

Problem: What do we do?
A) Book 3 seats, release 2? ❌ User expects all 5
B) Fail entire booking, release all 5? ✅ All-or-nothing

Solution: Transaction semantics (ACID)

Code:
public Booking confirmBooking(Booking booking, User user) throws Exception {
    // Start transaction
    Transaction tx = transactionManager.begin();

    try {
        // Step 1: Validate locks
        if (!validateLocks(booking)) {
            throw new Exception("Lock expired");
        }

        // Step 2: Process payment
        boolean paymentSuccess = paymentService.charge(
            user,
            booking.getTotalAmount()
        );

        if (!paymentSuccess) {
            throw new PaymentException("Payment failed");
        }

        // Step 3: Mark seats as booked (database write)
        for (Seat seat : booking.getSeats()) {
            seatRepository.markBooked(seat);
        }

        // Step 4: Update booking status
        booking.setStatus(BookingStatus.CONFIRMED);
        bookingRepository.save(booking);

        // Step 5: Release locks
        lockProvider.unlockSeats(booking.getShow(), booking.getSeats(), user);

        // All steps succeeded → Commit transaction
        tx.commit();
        return booking;

    } catch (Exception e) {
        // ANY step failed → Rollback everything
        tx.rollback();

        // Ensure locks are released (cleanup)
        try {
            lockProvider.unlockSeats(
                booking.getShow(),
                booking.getSeats(),
                user
            );
        } catch (Exception unlockEx) {
            logger.error("Failed to release locks", unlockEx);
        }

        throw new Exception("Booking failed: " + e.getMessage());
    }
}

Key principles:
1. All-or-nothing: Either all seats booked or none
2. Consistent state: Database and locks always in sync
3. Automatic cleanup: Rollback releases all resources
4. Idempotent: Can retry safely (same booking ID)

Database transaction ensures:
- Atomicity: All DB writes succeed or all fail
- Consistency: Constraints maintained
- Isolation: Concurrent bookings don't interfere
- Durability: Once committed, data persists

If payment gateway is down:
→ Exception thrown → Rollback → Locks released → User can retry
```

---

### **Q9: Compare pessimistic vs optimistic locking for this problem**

**Answer:**
```
┌─────────────────────────────────────────────────────────────┐
│                    Pessimistic Locking                      │
├─────────────────────────────────────────────────────────────┤
│ Lock seat IMMEDIATELY when user selects                     │
│ Hold lock during payment                                    │
│ Release on success/failure/timeout                          │
│                                                              │
│ Pros:                                                        │
│ ✅ Clear feedback ("seat locked by another user")           │
│ ✅ No wasted work (don't process if can't book)            │
│ ✅ Better UX (user knows immediately)                       │
│ ✅ Works well for hot items (last few seats)               │
│                                                              │
│ Cons:                                                        │
│ ❌ Holds resources (locks) during long operations           │
│ ❌ Lower theoretical max throughput                         │
│ ❌ Timeout management complexity                            │
└─────────────────────────────────────────────────────────────┘

┌─────────────────────────────────────────────────────────────┐
│                    Optimistic Locking                       │
├─────────────────────────────────────────────────────────────┤
│ No locks! User proceeds with booking                        │
│ At commit: Check if seat still available + version match    │
│ If conflict: Retry or fail                                  │
│                                                              │
│ Pros:                                                        │
│ ✅ No locks held during payment (better scalability)        │
│ ✅ Higher theoretical max throughput                        │
│ ✅ Simpler implementation (no timeout management)           │
│                                                              │
│ Cons:                                                        │
│ ❌ Wasted work (payment processed, then booking fails)      │
│ ❌ Poor UX (user pays, then "sorry, seat taken")           │
│ ❌ High conflict rate for popular shows                     │
│ ❌ Payment gateway charges (even for failed bookings)       │
└─────────────────────────────────────────────────────────────┘

Example with optimistic locking:

User A selects Seat A1 (version=1)
User B selects Seat A1 (version=1)  [Both see same version!]

User A pays $10
User B pays $10  [Both payments succeed]

User A commits: UPDATE seats SET booked=true, version=2
                WHERE id='A1' AND version=1
                → SUCCESS! (version matched)

User B commits: UPDATE seats SET booked=true, version=2
                WHERE id='A1' AND version=1
                → FAILED! (version is now 2, not 1)
                → User B's payment needs refund ❌

Recommendation: PESSIMISTIC for ticket booking!

When to use optimistic:
- Low contention scenarios (many available seats)
- Read-heavy workloads (few writes)
- No user interaction during transaction
- Examples: Shopping cart, Wikipedia edits

When to use pessimistic:
- High contention (last few seats)
- Long-running user interactions (payment)
- Clear winner needed (only one can succeed)
- Examples: Ticket booking, hotel rooms, limited edition sales
```

---

### **Q10: How would you monitor and alert for booking system health?**

**Answer:**
```
Metrics to Track:

1. Lock Contention Metrics
   - Lock acquisition failures per minute
   - Average lock wait time
   - Lock timeout rate (%)

   Alert: If lock timeout > 5% → High contention, scale up servers

2. Booking Funnel Metrics
   - Seats selected → Seats locked → Payment initiated → Booking confirmed
   - Drop-off rate at each step

   Example:
   1000 selects → 800 locked (200 conflicts) → 600 payments → 550 confirmed

   Alert: If lock success rate < 80% → Add more shows/seats

3. Payment Performance
   - Average payment processing time
   - Payment success rate
   - Payment gateway latency (p50, p95, p99)

   Alert: If payment time > 60s → Investigate gateway issues

4. Lock Expiry Metrics
   - Number of locks expired per hour
   - Revenue lost due to timeouts

   Alert: If expiry rate > 10% → Increase timeout or optimize payment flow

5. Database Performance
   - Query latency for seat availability checks
   - Connection pool utilization
   - Deadlock occurrences

   Alert: If query latency > 100ms → Add indexes or cache

6. Business Metrics
   - Bookings per minute (throughput)
   - Revenue per minute
   - Popular shows (for capacity planning)

   Dashboard Example:
   ┌─────────────────────────────────────┐
   │ Real-time Booking Dashboard         │
   ├─────────────────────────────────────┤
   │ Bookings/min:    247 ✅             │
   │ Lock success:    87% ⚠️             │
   │ Payment success: 94% ✅             │
   │ Avg lock time:   2.3s ✅            │
   │ Lock timeouts:   23 (4%) ⚠️        │
   │ Active locks:    1,247              │
   │ Revenue/hour:    $45,678 ✅         │
   └─────────────────────────────────────┘

Implementation with Prometheus + Grafana:

// Metrics collection
@Component
public class BookingMetrics {
    private final Counter bookingsTotal;
    private final Counter lockFailures;
    private final Histogram paymentDuration;
    private final Gauge activeLocks;

    public BookingMetrics(MeterRegistry registry) {
        bookingsTotal = Counter.builder("bookings_total")
            .tag("status", "confirmed")
            .register(registry);

        lockFailures = Counter.builder("lock_failures_total")
            .register(registry);

        paymentDuration = Histogram.builder("payment_duration_seconds")
            .register(registry);

        activeLocks = Gauge.builder("active_locks", this::countActiveLocks)
            .register(registry);
    }

    public void recordBooking() {
        bookingsTotal.increment();
    }

    public void recordLockFailure() {
        lockFailures.increment();
    }
}

Alerting Rules:
- Lock failure rate > 15% for 5 minutes → Page on-call engineer
- Payment latency p95 > 10s → Send Slack alert
- Booking rate drops 50% suddenly → Critical alert (potential outage)

```

---

## **⚠️ Known Limitations & Trade-offs**

### **1. In-Memory Locks (Single Server)**
**Limitation:** Locks stored in HashMap → Only works on one server

**Impact:** Can't horizontally scale

**Solution:** Redis-based distributed locks (see Q5)

---

### **2. No Lock Priority/Queue**
**Limitation:** First-come-first-serve, no VIP priority

**Impact:** Regular users and VIP users treated equally

**Solution:** Add priority queue system
```java
public enum UserPriority { VIP, PREMIUM, REGULAR }

public void lockSeats(Show show, List<Seat> seats, User user) {
    if (user.getPriority() == UserPriority.VIP) {
        // Force lock (kick out lower priority)
        forceLock(show, seats, user);
    } else {
        // Regular lock attempt
        normalLock(show, seats, user);
    }
}
```

---

### **3. Fixed Timeout (Not Adaptive)**
**Limitation:** All users get same 5-minute timeout

**Impact:** Some payment methods are faster (UPI) vs slower (Net Banking)

**Solution:** Dynamic timeout based on payment method (see Q6)

---

### **4. No Partial Booking**
**Limitation:** Want 5 seats, only 3 available → All or nothing

**Impact:** User might be okay with 3 seats, but can't book

**Solution:** Add "flexible booking" option
```java
public Booking createFlexibleBooking(
    User user,
    Show show,
    List<Seat> requestedSeats,
    int minimumSeats
) {
    List<Seat> availableSeats = findAvailable(requestedSeats);

    if (availableSeats.size() >= minimumSeats) {
        return createBooking(user, show, availableSeats);
    }

    throw new Exception("Not enough seats available");
}
```

---

### **5. No Retry Mechanism**
**Limitation:** Lock fails → User sees error, must manually retry

**Impact:** Poor UX, lost bookings

**Solution:** Exponential backoff retry
```java
public Booking bookWithRetry(User user, Show show, List<Seat> seats) {
    int maxRetries = 3;
    int backoff = 100; // ms

    for (int i = 0; i < maxRetries; i++) {
        try {
            return createBooking(user, show, seats);
        } catch (SeatLockedException e) {
            if (i == maxRetries - 1) throw e;

            Thread.sleep(backoff);
            backoff *= 2; // 100ms, 200ms, 400ms
        }
    }
}
```

---

## **📚 Key Takeaways**

**Concurrency Patterns:**
- ✅ Pessimistic Locking with timeout
- ✅ Synchronized methods for atomicity
- ✅ Two-phase commit (lock → confirm)
- ✅ Transaction management

**Design Patterns:**
- ✅ Strategy (Payment methods)
- ✅ Service Layer (Separation of concerns)
- ✅ Observer (Notifications - extension)

**Concurrency Guarantees:**
- ✅ No double booking (synchronized prevents race)
- ✅ Automatic recovery (timeout releases locks)
- ✅ Transaction consistency (all-or-nothing)

**Production Considerations:**
- ⚠️ Need Redis for multi-server
- ⚠️ Need monitoring/metrics
- ⚠️ Need database transactions
- ⚠️ Need lock cleanup job

---

## **🎓 What You Should Master**

Before the interview, you should be able to:

1. ✅ **Explain race condition** with timeline diagram (T0, T1, T2...)
2. ✅ **Justify pessimistic over optimistic** for this use case
3. ✅ **Walk through synchronized keyword** and how it prevents double booking
4. ✅ **Design distributed locking** using Redis (SET NX EX)
5. ✅ **Handle edge cases** (timeout, payment failure, server crash)
6. ✅ **Explain deadlock** and prevention (lock ordering)
7. ✅ **Describe testing strategy** for concurrent code
8. ✅ **Discuss trade-offs** (scalability vs consistency)
9. ✅ **Design extensions** (priority queue, retry, flexible booking)
10. ✅ **Answer all 10 Q&A** confidently

**Practice Exercises:**
1. Draw sequence diagram: Two users booking same seat
2. Code review: Spot the race condition in unlocked code
3. Design: Distributed lock with Redis (pseudocode)
4. Debug: Why is my lock never released? (missing timeout)
5. Scale: How to handle 10,000 bookings/second?

**Time to master:** 3-4 hours (this is ADVANCED concurrency!)

**Difficulty:** ⭐⭐⭐⭐ (Senior+ level, concurrency-heavy)

**Interview Frequency:** ⭐⭐⭐ (Very common for ticket booking systems)

---

## **🎤 Pro Tips for Interview**

1. **Start with the race condition** - Draw timeline to show the problem
2. **Explain atomicity** - Why synchronized is crucial
3. **Discuss timeout** - Shows you think about edge cases
4. **Mention distributed locking** - Shows production thinking
5. **Talk about monitoring** - Shows operational maturity
6. **Acknowledge trade-offs** - Pessimistic vs optimistic
7. **Suggest improvements** - Retry, priority queue, flexible booking

**Common Follow-ups:**
- "How would you test this?" → Multi-threaded test with CountDownLatch
- "How to scale to multiple servers?" → Redis distributed locks
- "What if Redis fails?" → Fallback to database locks, eventual consistency
- "Performance optimization?" → Cache seat availability, batch lock checks

Good luck! 🎬🚀
