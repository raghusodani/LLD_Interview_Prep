# Design Elevator System - Comprehensive Solution 🏢

## **Problem Statement**

Design an intelligent elevator system that can:
- Handle multiple elevators in a building
- Accept requests from floors (up/down buttons)
- Accept requests from inside elevators (destination floors)
- Optimize elevator dispatch using scheduling algorithms
- Display real-time elevator status
- Minimize wait time and energy consumption
- Support different scheduling strategies (FCFS, LOOK, SCAN)

---

## **🎯 Our Approach**

### **1. Core Requirements Analysis**

**Functional Requirements:**
- ✅ Multiple elevators per building
- ✅ External requests (floor buttons: UP/DOWN)
- ✅ Internal requests (cabin buttons: destination floor)
- ✅ Elevator state management (IDLE, MOVING, STOPPED)
- ✅ Direction handling (UP, DOWN, IDLE)
- ✅ Real-time status display
- ✅ Request queuing and scheduling

**Non-Functional Requirements:**
- ✅ Efficient scheduling (minimize wait time)
- ✅ Pluggable algorithms (FCFS, SCAN, LOOK)
- ✅ Energy efficient (avoid unnecessary movements)
- ✅ Scalable (easy to add more elevators)
- ✅ Observable (status notifications)

---

## **🏗️ Architecture & Design Patterns**

### **Pattern 1: Strategy Pattern - Scheduling Algorithms**

**Where:** Elevator scheduling/dispatching

**Why:**
- Multiple algorithms with same goal: minimize wait time
- Business might want to switch algorithms based on traffic patterns
- Easy to test and compare algorithm performance

**Implementation:**

```java
public interface SchedulingStrategy {
    void scheduleRequest(ElevatorRequest request,
                        List<Elevator> elevators);
}

// FCFS: First-Come-First-Serve
public class FCFSSchedulingStrategy implements SchedulingStrategy {
    @Override
    public void scheduleRequest(ElevatorRequest request,
                               List<Elevator> elevators) {
        // Add request to queue in order received
        // Simplest but not optimal
    }
}

// SCAN: Elevator goes up collecting all requests, then down
public class ScanSchedulingStrategy implements SchedulingStrategy {
    @Override
    public void scheduleRequest(ElevatorRequest request,
                               List<Elevator> elevators) {
        // Like disk SCAN algorithm
        // Efficient for heavy traffic
    }
}

// LOOK: Similar to SCAN but reverses at last request, not end
public class LookSchedulingStrategy implements SchedulingStrategy {
    @Override
    public void scheduleRequest(ElevatorRequest request,
                               List<Elevator> elevators) {
        // More efficient than SCAN
        // Reverses at highest request, not top floor
    }
}
```

**Benefits:**
- ✅ Open/Closed Principle - Add new algorithms without modifying existing code
- ✅ Each algorithm independently testable
- ✅ Runtime algorithm switching possible

---

### **Pattern 2: Command Pattern - Request Handling**

**Where:** Elevator requests (button presses)

**Why:**
- Encapsulate requests as objects
- Queue requests for later execution
- Support undo/cancel operations
- Decouple invoker (button) from receiver (elevator)

**Implementation:**

```java
public interface ElevatorCommand {
    void execute();
    void undo();
}

public class ElevatorRequest implements ElevatorCommand {
    private int sourceFloor;
    private int destinationFloor;
    private Direction direction;
    private Elevator elevator;

    @Override
    public void execute() {
        elevator.addDestination(destinationFloor);
    }

    @Override
    public void undo() {
        elevator.removeDestination(destinationFloor);
    }
}
```

**Benefits:**
- ✅ Requests can be queued, logged, replayed
- ✅ Easy to implement undo/cancel
- ✅ Supports request prioritization

---

### **Pattern 3: Observer Pattern - Status Notifications**

**Where:** Elevator status updates

**Why:**
- Multiple displays need to show elevator status
- Monitoring systems need to track metrics
- Maintenance alerts need real-time data
- Decouple elevator from display logic

**Implementation:**

```java
public interface ElevatorObserver {
    void update(Elevator elevator);
}

public class ElevatorDisplay implements ElevatorObserver {
    @Override
    public void update(Elevator elevator) {
        System.out.println("Elevator " + elevator.getId() +
                          " at floor " + elevator.getCurrentFloor());
    }
}

public class Elevator {
    private List<ElevatorObserver> observers = new ArrayList<>();

    public void attach(ElevatorObserver observer) {
        observers.add(observer);
    }

    private void notifyObservers() {
        for (ElevatorObserver observer : observers) {
            observer.update(this);
        }
    }

    public void moveToFloor(int floor) {
        this.currentFloor = floor;
        notifyObservers(); // Notify all observers
    }
}
```

**Benefits:**
- ✅ Loose coupling between elevator and displays
- ✅ Easy to add new observers (analytics, alerts)
- ✅ Real-time updates automatically propagated

---

### **Pattern 4: State Pattern (Implicit)**

**Where:** Elevator state management

**Why:**
- Elevator behavior changes based on state
- Different states have different allowed operations

**States:**
```java
public enum ElevatorState {
    IDLE,      // Not moving, no requests
    MOVING,    // Traveling to destination
    STOPPED    // At floor, doors open
}

public enum Direction {
    UP,
    DOWN,
    IDLE
}
```

**State Transitions:**
```
IDLE → MOVING (when request arrives)
MOVING → STOPPED (reaches destination floor)
STOPPED → MOVING (more requests pending)
STOPPED → IDLE (no more requests)
```

---

## **📐 Complete Architecture Diagram**

```
┌──────────────────────────────────────────────────────┐
│                    Building                           │
│  ┌────────────────────────────────────────────────┐  │
│  │         ElevatorController                      │  │
│  │  - scheduleRequest(request)                     │  │
│  │  - uses SchedulingStrategy                      │  │
│  └────────────┬───────────────────────┬────────────┘  │
│               │                       │                │
│     ┌─────────▼────────┐    ┌────────▼───────────┐   │
│     │   Elevator 1     │    │   Elevator 2       │   │
│     │  - currentFloor  │    │  - currentFloor    │   │
│     │  - state         │    │  - state           │   │
│     │  - direction     │    │  - direction       │   │
│     │  - destinations  │    │  - destinations    │   │
│     └──────────────────┘    └────────────────────┘   │
│                                                        │
│     ┌──────────────────────────────────────────┐     │
│     │            Floor (1..N)                   │     │
│     │  - upButton, downButton                   │     │
│     └──────────────────────────────────────────┘     │
└──────────────────────────────────────────────────────┘

┌─────────────────────────────────────────────────────┐
│         SchedulingStrategy (Interface)               │
└──────────────┬──────────────────────────────────────┘
               │
    ┌──────────┼──────────┐
    │          │          │
┌───▼───┐ ┌────▼───┐ ┌───▼────┐
│ FCFS  │ │ SCAN   │ │ LOOK   │
└───────┘ └────────┘ └────────┘

┌─────────────────────────────────────────────────────┐
│         ElevatorObserver (Interface)                 │
└──────────────┬──────────────────────────────────────┘
               │
    ┌──────────┼──────────────┐
    │          │              │
┌───▼───────┐ ┌▼──────────┐ ┌▼──────────┐
│  Display  │ │ Analytics │ │  Alert    │
└───────────┘ └───────────┘ └───────────┘
```

---

## **🔑 Key Design Decisions**

### **Decision 1: Choosing LOOK over SCAN as Default**

**What:** LOOK algorithm as default scheduling strategy

**Algorithm Comparison:**

**FCFS (First-Come-First-Serve):**
```
Requests: [3, 7, 2, 9, 1]
Order: 3 → 7 → 2 → 9 → 1
- Simple but inefficient
- Elevator zigzags unnecessarily
- High wait time
```

**SCAN (Elevator Algorithm):**
```
Elevator at floor 5, going UP
Requests: [3, 7, 2, 9]

Path: 5 → 6 → 7 (serve) → 8 → 9 (serve) → TOP_FLOOR
      → 8 → 7 → ... → 3 (serve) → 2 (serve)

- Serves all UP requests first
- Goes to top floor even if no requests
- Then serves all DOWN requests
```

**LOOK (More Efficient):**
```
Elevator at floor 5, going UP
Requests: [3, 7, 2, 9]

Path: 5 → 6 → 7 (serve) → 8 → 9 (serve)
      → 8 → 7 → ... → 3 (serve) → 2 (serve)

- Serves all UP requests
- Reverses at HIGHEST REQUEST (9), not top floor
- More efficient than SCAN
```

**Why LOOK?**
- Reduces unnecessary travel
- Better energy efficiency
- Lower average wait time
- Used in real disk scheduling and elevators

**Interview Question:**
> "Why not just use FCFS? It's simpler."

**Answer:**
> "FCFS causes excessive elevator movement. Example: Floor 1 presses UP, then Floor 10 presses UP, then Floor 2 presses UP. FCFS goes 1→10→2, traveling 9+8=17 floors. LOOK goes 1→2→10, traveling 1+8=9 floors. In a busy building, this compounds to significant energy savings and reduced wait times."

---

### **Decision 2: Separate Internal vs External Requests**

**What:** Different handling for cabin buttons vs floor buttons

```java
// External request (floor button)
class ExternalRequest {
    int floor;
    Direction direction; // UP or DOWN only
}

// Internal request (cabin button)
class InternalRequest {
    int destinationFloor;
    // Direction implicit based on current floor
}
```

**Why:**
- External requests have direction (UP/DOWN)
- Internal requests only have destination
- Different scheduling logic for each

**Interview Question:**
> "How do you handle someone inside pressing multiple floors?"

**Answer:**
> "Add all destinations to a TreeSet sorted by floor number. If going UP, serve all destinations in ascending order. If going DOWN, serve in descending order. This naturally implements LOOK within a single elevator."

---

### **Decision 3: Controller as Dispatcher (Not Individual Elevators)**

**What:** Centralized ElevatorController makes decisions

```java
public class ElevatorController {
    private List<Elevator> elevators;
    private SchedulingStrategy strategy;

    public void handleRequest(ElevatorRequest request) {
        // Controller decides which elevator serves request
        Elevator bestElevator = strategy.selectElevator(request, elevators);
        bestElevator.addRequest(request);
    }
}
```

**Why:**
- Global optimization (see all elevators)
- Prevents multiple elevators responding to same request
- Can implement advanced strategies (zone control, traffic prediction)

**Alternative (Decentralized):**
- Each elevator decides independently
- Can lead to inefficiency (multiple elevators respond)
- Harder to optimize globally

---

### **Decision 4: Observer for Loose Coupling**

**What:** Elevators don't know about displays

**Why:**
- Elevator logic doesn't depend on display logic
- Easy to add monitoring, alerts, analytics
- Testable without UI

**Interview Question:**
> "What if the display update is slow? Will it block the elevator?"

**Answer:**
> "Good point! In production, we'd make observers asynchronous:
```java
private void notifyObservers() {
    for (ElevatorObserver observer : observers) {
        executor.submit(() -> observer.update(this));
    }
}
```
This way, elevator continues moving while displays update in background. We'd use a thread pool to prevent thread explosion."

---

## **💡 SOLID Principles Applied**

### **S - Single Responsibility**
- `Elevator` - Manages movement and state
- `SchedulingStrategy` - Only decides elevator assignment
- `ElevatorController` - Coordinates elevators
- `ElevatorDisplay` - Only handles display logic

### **O - Open/Closed**
- Adding new scheduling algorithm: Create new Strategy class
- Adding new observer: Implement ElevatorObserver
- No modification to existing code

### **L - Liskov Substitution**
- Any `SchedulingStrategy` can replace another
- All observers are interchangeable

### **I - Interface Segregation**
- `SchedulingStrategy` - Only scheduling method
- `ElevatorObserver` - Only update method
- Small, focused interfaces

### **D - Dependency Inversion**
- `ElevatorController` depends on `SchedulingStrategy` interface
- Not on concrete FCFS/SCAN/LOOK implementations

---

## **🎭 Scenario Walkthrough**

### **Scenario: Busy Morning Rush Hour**

```
Building: 15 floors, 3 elevators
Time: 8:00 AM
Situation: Everyone going UP from ground floor

Initial State:
Elevator 1: Floor 1, IDLE
Elevator 2: Floor 7, IDLE
Elevator 3: Floor 12, IDLE

Requests (rapid sequence):
1. Floor 1, UP → Destination 9
2. Floor 1, UP → Destination 5
3. Floor 1, UP → Destination 12
4. Floor 3, UP → Destination 8
5. Floor 2, UP → Destination 10

LOOK Scheduling Decision:
Step 1: Elevator 1 is closest to Floor 1 (distance = 0)
        Assign requests 1, 2, 3 to Elevator 1

Step 2: Elevator 2 at Floor 7, going DOWN to serve Floor 3
        Assign request 4 to Elevator 2
        Path: 7 → 6 → 5 → 4 → 3 (pick up) → 8 (drop off)

Step 3: Elevator 3 at Floor 12, going DOWN to serve Floor 2
        Assign request 5 to Elevator 3
        Path: 12 → ... → 2 (pick up) → 10 (drop off)

Elevator 1 Path:
Floor 1 (pick up all 3 passengers)
  → Ask each for destination
  → Destinations: [5, 9, 12]
  → Going UP, serve in order: 5 → 9 → 12
  → State changes: IDLE → MOVING → STOPPED(5) → MOVING → STOPPED(9) → MOVING → STOPPED(12) → IDLE

Total travel:
- Elevator 1: 11 floors (1→12)
- Elevator 2: 5 floors (7→3→8)
- Elevator 3: 10 floors (12→2→10)
- Total: 26 floors

FCFS would have been worse:
- All requests to nearest elevator
- Elevator 1 does: 1→9→1→5→1→12→3→8→2→10
- Total: 37 floors (42% more!)
```

---

## **🚀 Extensions & Enhancements**

### **1. Zone Control (Peak Hours)**

```java
public class ZoneSchedulingStrategy implements SchedulingStrategy {
    @Override
    public void scheduleRequest(ElevatorRequest request,
                               List<Elevator> elevators) {
        // Divide building into zones
        // Elevator 1: Floors 1-5
        // Elevator 2: Floors 6-10
        // Elevator 3: Floors 11-15

        int zone = request.getFloor() / 5;
        Elevator assignedElevator = elevators.get(zone);
        assignedElevator.addRequest(request);
    }
}
```

**When:** Rush hours, convention center floors

---

### **2. Energy-Efficient Mode (Off-Peak)**

```java
public class EnergyEfficientStrategy implements SchedulingStrategy {
    @Override
    public void scheduleRequest(ElevatorRequest request,
                               List<Elevator> elevators) {
        // Use only N-1 elevators
        // Park one elevator at ground floor
        // Reduces electricity consumption

        List<Elevator> activeElevators = elevators.subList(0, elevators.size() - 1);
        // Use LOOK on active elevators
    }
}
```

**When:** Nights, weekends, low traffic

---

### **3. Priority Requests (VIP, Emergency)**

```java
public class PriorityRequest extends ElevatorRequest {
    private int priority; // 1 = highest

    @Override
    public int compareTo(ElevatorRequest other) {
        if (other instanceof PriorityRequest) {
            PriorityRequest pr = (PriorityRequest) other;
            return Integer.compare(this.priority, pr.priority);
        }
        return -1; // Priority requests always first
    }
}
```

**When:** Fire alarm, medical emergency, VIP access

---

### **4. Predictive Dispatching (ML-based)**

```java
public class PredictiveSchedulingStrategy implements SchedulingStrategy {
    private MachineLearningModel model;

    @Override
    public void scheduleRequest(ElevatorRequest request,
                               List<Elevator> elevators) {
        // Learn patterns: 8 AM → ground floor busy going UP
        //                 6 PM → high floors busy going DOWN

        double[] features = extractFeatures(request, elevators);
        int bestElevatorIndex = model.predict(features);
        elevators.get(bestElevatorIndex).addRequest(request);
    }
}
```

**When:** Smart buildings, high-traffic buildings

---

## **🎯 Interview Questions & Answers**

### **Q1: How would you optimize for multiple elevators?**

**Answer:**
```
Optimization criteria:
1. Minimize wait time → Choose elevator closest to request floor
2. Minimize travel time → Choose elevator already moving in request direction
3. Load balancing → Avoid overloading one elevator

Algorithm:
For each elevator, calculate cost:
  cost = |elevator.floor - request.floor|
       + directionPenalty
       + queueSizePenalty

directionPenalty:
- If elevator moving towards request & same direction: 0
- If elevator idle: 1
- If elevator moving away: 3

queueSizePenalty:
- Number of pending destinations * 0.5

Select elevator with minimum cost.

Example:
Request: Floor 5, going UP
Elevator 1: Floor 3, going UP, 2 destinations → cost = 2 + 0 + 1 = 3 ✅
Elevator 2: Floor 5, IDLE, 0 destinations → cost = 0 + 1 + 0 = 1 ✅ BEST
Elevator 3: Floor 8, going DOWN, 1 destination → cost = 3 + 3 + 0.5 = 6.5
```

---

### **Q2: How to handle weight limits?**

**Answer:**
```
Add weight sensor:

public class Elevator {
    private double currentWeight;
    private double maxWeight;

    public boolean canAcceptPassenger(double weight) {
        return currentWeight + weight <= maxWeight;
    }
}

Scenario: Elevator full
1. Sensor detects overweight
2. Display shows "Overweight - Please Exit"
3. Elevator doors won't close
4. System dispatches another elevator to same floor
5. Observer pattern notifies controller:

   @Override
   public void update(Elevator elevator) {
       if (elevator.isOverweight()) {
           controller.dispatchAdditionalElevator(elevator.getCurrentFloor());
       }
   }
```

---

### **Q3: How to minimize energy consumption?**

**Answer:**
```
Energy-saving strategies:

1. **Idle elevators return to ground floor**
   - Most requests start from ground
   - Reduces average distance

2. **Sleep mode for low traffic**
   - Turn off lights, displays
   - Slower response time acceptable at 2 AM

3. **Regenerative braking**
   - Going down generates electricity
   - Schedule full elevators to go down when possible

4. **Batching strategy**
   - Wait 2-3 seconds before dispatch
   - Combine multiple requests
   - Example: Floor 1 UP, Floor 2 UP, Floor 3 UP
     → One elevator serves all instead of three

5. **Dynamic elevator count**
   - Morning: Use all 5 elevators
   - Midnight: Use only 1 elevator

Implementation:
public class EnergyAwareController extends ElevatorController {
    @Override
    public void scheduleRequest(ElevatorRequest request) {
        int activeElevators = calculateOptimalElevatorCount(
            currentTraffic, timeOfDay, dayOfWeek
        );

        // Park excess elevators at ground floor
        for (int i = activeElevators; i < elevators.size(); i++) {
            elevators.get(i).parkAtFloor(0);
        }
    }
}
```

---

### **Q4: How does LOOK handle direction changes?**

**Answer:**
```
LOOK direction logic:

Current: Floor 7, Direction UP
Destinations: [9, 11, 14] (going up)
              [5, 3, 2] (going down)

Step-by-step:
1. Continue UP: 7 → 8 → 9 (stop) → 10 → 11 (stop) → 12 → 13 → 14 (stop)
2. Check upward destinations: Empty
3. Reverse direction: Direction = DOWN
4. Continue DOWN: 14 → 13 → ... → 5 (stop) → 4 → 3 (stop) → 2 (stop)
5. Check downward destinations: Empty
6. State = IDLE, Direction = IDLE

Code:
public void move() {
    if (direction == Direction.UP) {
        moveUp();
        if (upDestinations.isEmpty()) {
            direction = Direction.DOWN;
        }
    } else if (direction == Direction.DOWN) {
        moveDown();
        if (downDestinations.isEmpty()) {
            direction = Direction.UP;
        }
    }

    if (upDestinations.isEmpty() && downDestinations.isEmpty()) {
        direction = Direction.IDLE;
        state = ElevatorState.IDLE;
    }
}
```

---

### **Q5: What about emergency situations (fire alarm)?**

**Answer:**
```
Emergency protocol:

1. **Immediate actions:**
   - All elevators return to ground floor
   - Ignore all pending requests
   - Doors open automatically
   - Display "OUT OF SERVICE - USE STAIRS"

2. **Firefighter mode:**
   - One elevator under manual control
   - Key-operated override
   - Bypass all safety features

Implementation:
public class EmergencyMode {
    public void activateFireMode(List<Elevator> elevators) {
        for (Elevator elevator : elevators) {
            elevator.clearAllRequests();
            elevator.setDestination(GROUND_FLOOR);
            elevator.setMode(Mode.EMERGENCY);
        }
    }
}

State changes:
MOVING → EMERGENCY → STOPPED(Ground) → OUT_OF_SERVICE

Observer notification:
- Fire alarm observer activates
- Notifies building management
- Logs all elevator positions
- Displays evacuation instructions
```

---

### **Q6: How to handle concurrent requests from multiple users?**

**Answer:**
```
Concurrency challenges:
1. Two users press button simultaneously
2. Multiple elevators checking same request
3. Elevator state changes during decision

Solutions:

1. **Synchronize request queue:**
   public class ElevatorController {
       private final BlockingQueue<ElevatorRequest> requestQueue
           = new LinkedBlockingQueue<>();

       public void handleRequest(ElevatorRequest request) {
           requestQueue.offer(request); // Thread-safe
       }

       // Dispatcher thread processes queue
       private void processRequests() {
           while (true) {
               ElevatorRequest request = requestQueue.take();
               synchronized(elevators) {
                   scheduleRequest(request);
               }
           }
       }
   }

2. **Atomic state updates:**
   public class Elevator {
       private volatile ElevatorState state;
       private final ReentrantLock lock = new ReentrantLock();

       public boolean tryAddDestination(int floor) {
           lock.lock();
           try {
               if (destinations.size() < MAX_DESTINATIONS) {
                   destinations.add(floor);
                   return true;
               }
               return false;
           } finally {
               lock.unlock();
           }
       }
   }

3. **Request deduplication:**
   - Use Set instead of List for destinations
   - TreeSet automatically handles duplicates
```

---

### **Q7: How to test elevator scheduling algorithms?**

**Answer:**
```
Testing strategies:

1. **Unit tests for each algorithm:**
   @Test
   public void testLookAlgorithm() {
       Elevator elevator = new Elevator(1, 10); // At floor 10
       elevator.setDirection(Direction.DOWN);

       elevator.addDestination(5);
       elevator.addDestination(3);
       elevator.addDestination(12); // Should wait until direction reverses

       elevator.move();
       assertEquals(9, elevator.getCurrentFloor());

       // Continue until floor 3
       while (elevator.getCurrentFloor() > 3) {
           elevator.move();
       }

       assertEquals(Direction.UP, elevator.getDirection()); // Should reverse
   }

2. **Simulation tests:**
   - Generate random request patterns
   - Compare FCFS vs SCAN vs LOOK
   - Metrics: Average wait time, total travel distance

3. **Load testing:**
   - 1000 requests in 1 minute
   - Measure throughput
   - Check for deadlocks

4. **Edge cases:**
   - All requests to same floor
   - Alternating up/down requests
   - Single elevator, 100 pending requests
   - Elevator at top floor, all requests going up
```

---

### **Q8: How to implement maintenance mode?**

**Answer:**
```
Maintenance requirements:
- Take elevator out of service
- Don't assign new requests
- Complete current passengers
- Move to maintenance floor

Implementation:
public class Elevator {
    private boolean maintenanceMode;

    public void enterMaintenanceMode() {
        this.maintenanceMode = true;

        // Complete current passengers
        while (!destinations.isEmpty()) {
            move();
        }

        // Move to maintenance floor (usually basement)
        addDestination(MAINTENANCE_FLOOR);
        while (currentFloor != MAINTENANCE_FLOOR) {
            move();
        }

        state = ElevatorState.MAINTENANCE;
        notifyObservers(); // Update displays
    }
}

Controller changes:
public List<Elevator> getAvailableElevators() {
    return elevators.stream()
        .filter(e -> !e.isInMaintenance())
        .collect(Collectors.toList());
}
```

---

### **Q9: What about double-decker elevators?**

**Answer:**
```
Double-decker = Two cabins stacked, moving together

Challenges:
- Upper cabin serves even floors (2, 4, 6, ...)
- Lower cabin serves odd floors (1, 3, 5, ...)
- Both cabins share same shaft and motor

Design:
public class DoubleDecker extends Elevator {
    private ElevatorCabin upperCabin;
    private ElevatorCabin lowerCabin;

    @Override
    public void addDestination(int floor) {
        if (floor % 2 == 0) {
            upperCabin.addDestination(floor);
        } else {
            lowerCabin.addDestination(floor);
        }
    }

    @Override
    public void move() {
        // Move both cabins simultaneously
        currentFloor++;
        upperCabin.setFloor(currentFloor);
        lowerCabin.setFloor(currentFloor - 1);
    }
}

Benefits:
- 2x capacity with same shaft
- Common in high-rise buildings
- Sky lobbies handle odd/even separation
```

---

### **Q10: How to monitor and analyze elevator performance?**

**Answer:**
```
Analytics implementation:

public class AnalyticsObserver implements ElevatorObserver {
    private Map<Integer, Metrics> elevatorMetrics = new HashMap<>();

    @Override
    public void update(Elevator elevator) {
        Metrics metrics = elevatorMetrics.get(elevator.getId());

        // Track metrics
        metrics.totalTrips++;
        metrics.totalFloorsTraveled += elevator.getDistance();
        metrics.totalPassengers += elevator.getPassengerCount();

        // Calculate derived metrics
        metrics.avgWaitTime = calculateAvgWaitTime();
        metrics.utilizationRate = calculateUtilization();
    }

    public Report generateReport(LocalDate date) {
        return new Report()
            .withMetric("Total Trips", getTotalTrips())
            .withMetric("Peak Hour", identifyPeakHour())
            .withMetric("Avg Wait Time", getAvgWaitTime())
            .withMetric("Energy Consumption", getEnergyUsage())
            .withRecommendation(getOptimizationSuggestions());
    }
}

Metrics to track:
1. Average wait time per request
2. Average travel time
3. Peak hours identification
4. Elevator utilization (% time moving vs idle)
5. Energy consumption per trip
6. Passenger throughput (passengers/hour)
7. Request abandonment rate (too long wait)

Optimization insights:
- If Elevator 1 utilization = 90%, others = 40% → Imbalanced scheduling
- If wait time spike at 8 AM → Add zone control during rush
- If energy peaks at noon → Implement batching strategy
```

---

## **⚠️ Known Limitations & Trade-offs**

### **1. No Dynamic Algorithm Switching**
- **Current:** Single strategy throughout operation
- **Fix:** Time-based strategy switching
  ```java
  if (isPeakHour()) {
      controller.setStrategy(new ZoneSchedulingStrategy());
  } else {
      controller.setStrategy(new LookSchedulingStrategy());
  }
  ```

### **2. No Request Timeout**
- **Risk:** Forgotten requests stay in queue
- **Fix:** Add timestamp and timeout
  ```java
  if (request.getAge() > MAX_WAIT_TIME) {
      escalateRequest(request); // VIP priority
  }
  ```

### **3. No Load Balancing Across Shafts**
- **Current:** Treats all elevators independently
- **Fix:** Group elevators by shaft, coordinate within group

### **4. No Predictive Maintenance**
- **Current:** Manual maintenance scheduling
- **Fix:** Track metrics (trips, distance) and predict failures
  ```java
  if (elevator.getTotalTrips() > MAINTENANCE_THRESHOLD) {
      scheduleMaintenanceCheck(elevator);
  }
  ```

### **5. Ignores Passenger Behavior**
- **Current:** Assumes all passengers get off at destination
- **Reality:** Some change minds, some get off early
- **Fix:** Add confidence scores to predictions

---

## **📚 Key Takeaways**

**Design Patterns Used:**
- ✅ Strategy Pattern (Scheduling algorithms)
- ✅ Command Pattern (Request handling)
- ✅ Observer Pattern (Status notifications)
- ✅ State Pattern (Elevator states)

**Algorithms:**
- ✅ FCFS (Simple but inefficient)
- ✅ SCAN (Efficient for heavy traffic)
- ✅ LOOK (Best balance of efficiency and simplicity)

**Optimization Techniques:**
- ✅ Zone control for peak hours
- ✅ Energy-efficient mode for off-peak
- ✅ Priority requests for emergencies
- ✅ Predictive dispatching with ML

**Interview Focus:**
- Algorithm comparison and trade-offs
- Concurrency handling
- Energy efficiency
- Emergency protocols
- Performance metrics

---

## **🎓 What You Should Master**

Before interview, ensure you can:
1. ✅ Explain FCFS vs SCAN vs LOOK with examples
2. ✅ Draw elevator state diagram
3. ✅ Implement basic LOOK algorithm in 10 minutes
4. ✅ Discuss energy optimization strategies
5. ✅ Handle emergency scenarios
6. ✅ Design concurrent request handling
7. ✅ Calculate elevator selection cost function
8. ✅ Explain Observer pattern benefits
9. ✅ Add new scheduling algorithm in 5 minutes
10. ✅ Answer all Q&A sections confidently

**Time to master:** 3-4 hours of practice

**Difficulty:** ⭐⭐⭐⭐ (High - Requires algorithms + design patterns)

**Interview Frequency:** ⭐⭐⭐⭐ (Very Common - Asked at Amazon, Google, Microsoft)
