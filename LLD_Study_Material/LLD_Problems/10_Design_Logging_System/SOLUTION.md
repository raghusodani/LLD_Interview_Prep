# Design Logging System - Comprehensive Solution 📝

## **Problem Statement**

Design a flexible, extensible logging system that can:
- Support multiple log levels (DEBUG, INFO, WARN, ERROR)
- Log to different outputs (Console, File, Database, etc.)
- Filter logs based on severity
- Be used as a single instance across the application
- Handle concurrent logging requests safely
- Be easily extendable for new log levels and outputs

**Real-World Examples:** Log4j, SLF4J, java.util.logging

---

## **🎯 Our Approach**

### **Requirements Analysis**

**Functional Requirements:**
- ✅ Log messages with different severity levels
- ✅ Configure minimum log level (e.g., only ERROR and above)
- ✅ Support multiple output destinations
- ✅ Format log messages with timestamp, level, message
- ✅ Chain log handlers by priority

**Non-Functional Requirements:**
- ✅ Thread-safe (multiple threads logging simultaneously)
- ✅ Single instance per application (Singleton)
- ✅ Extensible for new log levels
- ✅ Extensible for new output types
- ✅ Minimal performance overhead

---

## **🏗️ Architecture & Design Patterns**

This problem demonstrates **THREE** major design patterns working together!

### **Pattern 1: Singleton Pattern** 🎯

**Where:** Logger class

**Why:**
- Only ONE logger instance should exist in application
- Centralized configuration
- Consistent logging behavior
- Shared state across all modules

**Thread-Safe Implementation:**

```java
public class Logger {
    private static volatile Logger instance;
    private LoggerConfig config;

    // Private constructor prevents instantiation
    private Logger() {}

    // Double-Check Locking for thread safety
    public static Logger getInstance(LogLevel logLevel, LogAppender logAppender) {
        if (instance == null) {                          // Check 1 (no lock)
            synchronized (Logger.class) {                 // Lock
                if (instance == null) {                   // Check 2 (with lock)
                    instance = new Logger();
                    instance.config = new LoggerConfig(logLevel, logAppender);
                }
            }
        }
        return instance;
    }

    public void log(LogLevel level, String message) {
        if (config != null) {
            config.getLogHandler().logMessage(level.ordinal(), message);
        }
    }
}
```

**Key Points:**
- `volatile` keyword ensures visibility across threads
- Double-check locking minimizes synchronization overhead
- Private constructor prevents `new Logger()`
- Lazy initialization - created only when first accessed

---

### **Pattern 2: Chain of Responsibility (CoR)** ⛓️

**Where:** LogHandler hierarchy (DebugLogger → InfoLogger → ErrorLogger)

**Why:**
- Decouple sender (Logger) from receivers (handlers)
- Each handler decides if it should process the log
- Handlers form a chain - log passes through until handled
- Easy to add/remove/reorder handlers

**Implementation:**

```java
public abstract class LogHandler {
    protected LogLevel level;
    protected LogHandler nextLogger;
    protected LogAppender appender;

    // Chain setup
    public void setNextLogger(LogHandler nextLogger) {
        this.nextLogger = nextLogger;
    }

    // Chain processing
    public void logMessage(int level, String message) {
        LogLevel logLevel = intToLogLevel(level);

        // If this handler's level matches or is lower, process it
        if (this.level.ordinal() <= logLevel.ordinal()) {
            write(message);
        }

        // Pass to next handler in chain
        if (nextLogger != null) {
            nextLogger.logMessage(level, message);
        }
    }

    // Abstract method - each handler implements its own logic
    abstract protected void write(String message);
}
```

**Concrete Handlers:**

```java
public class DebugLogger extends LogHandler {
    public DebugLogger(LogAppender appender) {
        this.level = LogLevel.DEBUG;
        this.appender = appender;
    }

    @Override
    protected void write(String message) {
        appender.append("DEBUG: " + message);
    }
}

public class InfoLogger extends LogHandler {
    public InfoLogger(LogAppender appender) {
        this.level = LogLevel.INFO;
        this.appender = appender;
    }

    @Override
    protected void write(String message) {
        appender.append("INFO: " + message);
    }
}

public class ErrorLogger extends LogHandler {
    public ErrorLogger(LogAppender appender) {
        this.level = LogLevel.ERROR;
        this.appender = appender;
    }

    @Override
    protected void write(String message) {
        appender.append("ERROR: " + message);
    }
}
```

**Chain Setup:**

```java
LogHandler debugLogger = new DebugLogger(consoleAppender);
LogHandler infoLogger = new InfoLogger(consoleAppender);
LogHandler errorLogger = new ErrorLogger(fileAppender);

// Create chain: DEBUG → INFO → ERROR
debugLogger.setNextLogger(infoLogger);
infoLogger.setNextLogger(errorLogger);
```

**How it Works:**

```
Log message with level INFO:
  ↓
DebugLogger (level=DEBUG)
  - DEBUG <= INFO? YES → Write to console
  - Pass to next
  ↓
InfoLogger (level=INFO)
  - INFO <= INFO? YES → Write to console
  - Pass to next
  ↓
ErrorLogger (level=ERROR)
  - ERROR <= INFO? NO → Skip
  - No next handler
```

---

### **Pattern 3: Strategy Pattern** 💡

**Where:** LogAppender (ConsoleAppender, FileAppender)

**Why:**
- Different output strategies (console, file, database, network)
- Easy to switch appenders at runtime
- Easy to add new appenders without modifying handlers

**Implementation:**

```java
public interface LogAppender {
    void append(String message);
}

public class ConsoleAppender implements LogAppender {
    @Override
    public void append(String message) {
        System.out.println(message);
    }
}

public class FileAppender implements LogAppender {
    private String filename;

    public FileAppender(String filename) {
        this.filename = filename;
    }

    @Override
    public void append(String message) {
        // Write to file
        try (FileWriter fw = new FileWriter(filename, true)) {
            fw.write(message + "\n");
        } catch (IOException e) {
            System.err.println("Failed to write to file: " + e.getMessage());
        }
    }
}
```

---

## **📐 Architecture Diagram**

```
┌──────────────────────────────────────────────────┐
│                   Logger                         │
│              (Singleton Pattern)                 │
│  - static volatile instance                      │
│  - getInstance() with double-check locking       │
│  - debug(), info(), error() methods              │
└──────────────────┬───────────────────────────────┘
                   │
                   │ uses
                   ▼
┌──────────────────────────────────────────────────┐
│              LoggerConfig                        │
│  - LogLevel minLevel                             │
│  - LogHandler chainHead                          │
└──────────────────┬───────────────────────────────┘
                   │
                   │ contains
                   ▼
┌─────────────────────────────────────────────────────┐
│             LogHandler                              │
│       (Chain of Responsibility)                     │
│  - LogLevel level                                   │
│  - LogHandler nextLogger                            │
│  - logMessage() [chain processing]                  │
│  - abstract write()                                 │
└─────────────┬───────────────────────────────────────┘
              │
       ┌──────┴───────┬──────────┐
       │              │          │
  ┌────▼────┐   ┌────▼────┐  ┌──▼─────┐
  │ Debug   │   │  Info   │  │ Error  │
  │ Logger  │   │ Logger  │  │ Logger │
  └────┬────┘   └────┬────┘  └───┬────┘
       │             │           │
       └─────────┬───┴───────────┘
                 │ uses
                 ▼
┌─────────────────────────────────────────┐
│         LogAppender                     │
│      (Strategy Pattern)                 │
│  - append(String message)               │
└──────────┬──────────────────────────────┘
           │
    ┌──────┴─────┬───────────────┐
    │            │               │
┌───▼──────┐  ┌──▼────────┐  ┌──▼─────────┐
│ Console  │  │   File    │  │  Database  │
│ Appender │  │ Appender  │  │  Appender  │
└──────────┘  └───────────┘  └────────────┘
```

---

## **🔑 Key Design Decisions**

### **Decision 1: Singleton with Double-Check Locking**

**What:** Thread-safe lazy initialization

```java
// ❌ Not thread-safe
public static Logger getInstance() {
    if (instance == null) {
        instance = new Logger();  // Race condition!
    }
    return instance;
}

// ❌ Thread-safe but slow (locks every time)
public static synchronized Logger getInstance() {
    if (instance == null) {
        instance = new Logger();
    }
    return instance;
}

// ✅ Best: Double-check locking + volatile
private static volatile Logger instance;

public static Logger getInstance() {
    if (instance == null) {              // Fast path - no lock
        synchronized (Logger.class) {
            if (instance == null) {      // Second check with lock
                instance = new Logger();
            }
        }
    }
    return instance;
}
```

**Why volatile?**
- Without `volatile`, thread might see partially constructed object
- Ensures happens-before relationship
- Prevents instruction reordering

**Interview Question:**
> "Why do we need the second null check inside synchronized block?"

**Answer:**
> "Two threads could pass the first check simultaneously. Without the second check, both would create instances. The synchronized block ensures only one thread enters, but we still need to verify instance is null because the other thread might have created it while we were waiting for the lock."

---

### **Decision 2: Chain of Responsibility over If-Else**

**What:** Use handler chain instead of conditional logic

```java
// ❌ Bad: If-else soup
public void log(LogLevel level, String message) {
    if (level == LogLevel.DEBUG && minLevel.ordinal() <= 0) {
        System.out.println("DEBUG: " + message);
    }
    if (level == LogLevel.INFO && minLevel.ordinal() <= 1) {
        System.out.println("INFO: " + message);
        writeToFile(message);
    }
    if (level == LogLevel.ERROR && minLevel.ordinal() <= 2) {
        System.err.println("ERROR: " + message);
        writeToFile(message);
        sendAlert(message);
    }
    // Adding new level requires modifying this method!
}

// ✅ Good: Chain of Responsibility
public void log(LogLevel level, String message) {
    chainHead.logMessage(level.ordinal(), message);
    // New handlers added to chain, no modification here!
}
```

**Benefits:**
- Open/Closed Principle - open for extension, closed for modification
- Each handler is independent and testable
- Easy to add new log levels (create new handler, add to chain)
- Flexible routing (different handlers for different levels)

---

### **Decision 3: Strategy Pattern for Appenders**

**What:** Separate appending logic from logging logic

**Why:**
- Appending to file is different from console
- Appending to database needs connection management
- Network appending needs retry logic
- Each strategy is independent

**Benefits:**
- Easy to test (mock appenders)
- Easy to add new destinations (S3, CloudWatch, Elasticsearch)
- Can change appender at runtime
- Multiple appenders per handler

---

### **Decision 4: Enum for Log Levels**

**What:** Use enum with ordinal values

```java
public enum LogLevel {
    DEBUG(0),    // Lowest severity
    INFO(1),
    WARN(2),
    ERROR(3);    // Highest severity

    private final int value;

    LogLevel(int value) {
        this.value = value;
    }
}
```

**Why:**
- Type-safe (can't pass invalid level)
- Natural ordering with ordinal()
- Can compare levels easily
- Can add metadata (color codes, prefixes)

---

## **💡 SOLID Principles Applied**

### **S - Single Responsibility**
- `Logger` - Provides logging interface
- `LogHandler` - Processes logs at specific level
- `LogAppender` - Writes to output destination
- `LoggerConfig` - Configuration management

### **O - Open/Closed**
- Adding new log level: Create new `LogHandler` subclass
- Adding new output: Create new `LogAppender` implementation
- No modification of existing classes

### **L - Liskov Substitution**
- Any `LogHandler` subclass can replace base class
- Any `LogAppender` implementation can replace interface
- Chain works with any handler

### **I - Interface Segregation**
- `LogAppender` has single method `append()`
- No unnecessary methods forced on implementers

### **D - Dependency Inversion**
- `LogHandler` depends on `LogAppender` interface, not concrete classes
- High-level (Logger) doesn't depend on low-level (FileWriter)

---

## **🎭 Scenario Walkthrough**

### **Scenario: Log Different Severity Levels**

**Setup:**
```java
// Create appenders
LogAppender consoleAppender = new ConsoleAppender();
LogAppender fileAppender = new FileAppender("app.log");

// Create handler chain
LogHandler debugLogger = new DebugLogger(consoleAppender);
LogHandler infoLogger = new InfoLogger(consoleAppender);
LogHandler errorLogger = new ErrorLogger(fileAppender);

debugLogger.setNextLogger(infoLogger);
infoLogger.setNextLogger(errorLogger);

// Configure logger
LoggerConfig config = new LoggerConfig(LogLevel.INFO, debugLogger);
Logger logger = Logger.getInstance(LogLevel.INFO, consoleAppender);
logger.setConfig(config);
```

**Log Messages:**

```java
// 1. DEBUG message
logger.debug("Starting application");

Flow:
  DebugLogger: level=DEBUG, message level=DEBUG
    - DEBUG <= DEBUG? YES → Write to console ✓
    - Pass to InfoLogger
  InfoLogger: level=INFO, message level=DEBUG
    - INFO <= DEBUG? NO → Skip
    - Pass to ErrorLogger
  ErrorLogger: level=ERROR, message level=DEBUG
    - ERROR <= DEBUG? NO → Skip
    - End of chain

Output: "DEBUG: Starting application" (console only)

// 2. INFO message
logger.info("User logged in");

Flow:
  DebugLogger: level=DEBUG, message level=INFO
    - DEBUG <= INFO? YES → Write to console ✓
    - Pass to InfoLogger
  InfoLogger: level=INFO, message level=INFO
    - INFO <= INFO? YES → Write to console ✓
    - Pass to ErrorLogger
  ErrorLogger: level=ERROR, message level=INFO
    - ERROR <= INFO? NO → Skip
    - End of chain

Output:
  Console: "DEBUG: User logged in"
  Console: "INFO: User logged in"

// 3. ERROR message
logger.error("Database connection failed");

Flow:
  DebugLogger: level=DEBUG, message level=ERROR
    - DEBUG <= ERROR? YES → Write to console ✓
    - Pass to InfoLogger
  InfoLogger: level=INFO, message level=ERROR
    - INFO <= ERROR? YES → Write to console ✓
    - Pass to ErrorLogger
  ErrorLogger: level=ERROR, message level=ERROR
    - ERROR <= ERROR? YES → Write to file ✓
    - End of chain

Output:
  Console: "DEBUG: Database connection failed"
  Console: "INFO: Database connection failed"
  File: "ERROR: Database connection failed"
```

---

## **🚀 Extensions & Enhancements**

### **1. Add WARN Level**

```java
public class WarnLogger extends LogHandler {
    public WarnLogger(LogAppender appender) {
        this.level = LogLevel.WARN;
        this.appender = appender;
    }

    @Override
    protected void write(String message) {
        appender.append("WARN: " + message);
    }
}

// Insert into chain: DEBUG → INFO → WARN → ERROR
infoLogger.setNextLogger(warnLogger);
warnLogger.setNextLogger(errorLogger);
```

### **2. Add Database Appender**

```java
public class DatabaseAppender implements LogAppender {
    private Connection connection;

    public DatabaseAppender(String jdbcUrl) {
        this.connection = DriverManager.getConnection(jdbcUrl);
    }

    @Override
    public void append(String message) {
        String sql = "INSERT INTO logs (timestamp, message) VALUES (?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setTimestamp(1, new Timestamp(System.currentTimeMillis()));
            stmt.setString(2, message);
            stmt.executeUpdate();
        } catch (SQLException e) {
            // Fallback to console
            System.err.println("DB logging failed: " + e.getMessage());
        }
    }
}
```

### **3. Add Async Logging**

```java
public class AsyncAppender implements LogAppender {
    private BlockingQueue<String> queue = new LinkedBlockingQueue<>();
    private ExecutorService executor = Executors.newSingleThreadExecutor();
    private LogAppender delegateAppender;

    public AsyncAppender(LogAppender delegate) {
        this.delegateAppender = delegate;
        startProcessor();
    }

    @Override
    public void append(String message) {
        queue.offer(message);  // Non-blocking
    }

    private void startProcessor() {
        executor.submit(() -> {
            while (true) {
                try {
                    String message = queue.take();  // Blocking
                    delegateAppender.append(message);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        });
    }
}
```

### **4. Add Log Rotation**

```java
public class RotatingFileAppender implements LogAppender {
    private String baseFilename;
    private long maxFileSize;
    private int currentFile = 0;

    @Override
    public void append(String message) {
        String filename = baseFilename + "." + currentFile;
        File file = new File(filename);

        if (file.length() > maxFileSize) {
            currentFile++;
            filename = baseFilename + "." + currentFile;
        }

        // Write to file
        try (FileWriter fw = new FileWriter(filename, true)) {
            fw.write(message + "\n");
        } catch (IOException e) {
            System.err.println("Failed to write: " + e.getMessage());
        }
    }
}
```

---

## **🎯 Interview Questions & Answers**

### **Q1: Explain double-check locking in Singleton. Why is volatile needed?**

**Answer:**
```
Double-check locking optimizes performance:

1. First check (no lock): Fast path - if instance exists, return it
2. Synchronized block: Only one thread enters if instance is null
3. Second check (with lock): Prevents multiple instances if two threads
   passed first check simultaneously

volatile is CRITICAL:
- Without it, thread might see partially constructed object
- Java memory model allows reordering:
  instance = new Logger() actually does:
    1. Allocate memory
    2. Initialize object
    3. Assign reference to instance

  Without volatile, steps 2 and 3 can be reordered:
    1. Allocate memory
    2. Assign reference to instance (instance != null now!)
    3. Initialize object (not done yet!)

  Another thread sees instance != null but object is not fully initialized!

volatile ensures:
- No instruction reordering
- Changes visible to all threads immediately
- Happens-before guarantee
```

### **Q2: What are alternatives to Singleton pattern for Logger?**

**Answer:**
```
1. Dependency Injection (Better for testing):
   - Pass Logger instance to classes that need it
   - Easier to mock in tests
   - More explicit dependencies

   class UserService {
       private Logger logger;
       public UserService(Logger logger) {
           this.logger = logger;
       }
   }

2. Static Factory Method:
   - LoggerFactory.getLogger(ClassName.class)
   - Used by SLF4J, Log4j
   - Can return different instances per class
   - Better for distributed systems

3. Enum Singleton (Joshua Bloch's recommendation):
   public enum Logger {
       INSTANCE;
       public void log(String message) { ... }
   }
   - Thread-safe by JVM
   - Prevents reflection attacks
   - Serialization safe

4. Thread-Local Logger:
   - Different instance per thread
   - No synchronization needed
   - Good for per-thread log files
```

### **Q3: How would you implement log rotation?**

**Answer:**
```
Log rotation strategies:

1. Size-based rotation:
   - Check file size before write
   - If > maxSize, rename app.log → app.log.1
   - Create new app.log
   - Keep last N files

2. Time-based rotation:
   - New file per day/hour: app-2024-12-05.log
   - Scheduled job to archive old logs
   - Easy to grep by date

3. Combined approach:
   - Rotate on size OR time, whichever comes first
   - app-2024-12-05-001.log, app-2024-12-05-002.log

Implementation considerations:
- Thread-safety: synchronize file switching
- Atomic rename operations
- Handle errors during rotation
- Compression of old logs (gzip)
- Purging very old logs

Production tools:
- logrotate (Linux)
- Log4j RollingFileAppender
- SLF4J TimeBasedRollingPolicy
```

### **Q4: How to handle logging in distributed systems?**

**Answer:**
```
Challenges:
1. Logs scattered across multiple servers
2. Hard to trace requests across services
3. Clock synchronization issues
4. Massive log volume

Solutions:

1. Centralized Logging:
   - All services send logs to central system
   - Tools: ELK Stack (Elasticsearch, Logstash, Kibana)
   - Splunk, CloudWatch Logs, Datadog

2. Correlation IDs:
   - Generate UUID for each request
   - Pass in HTTP headers (X-Request-ID)
   - Include in all log messages
   - Trace entire request flow

   Example:
   Service A: [req-123] User login request received
   Service B: [req-123] Validating credentials
   Service C: [req-123] Loading user profile

3. Structured Logging:
   - JSON format instead of plain text
   - Easy to parse and search
   - {
       "timestamp": "2024-12-05T10:30:00Z",
       "level": "ERROR",
       "service": "user-service",
       "requestId": "req-123",
       "message": "Database timeout",
       "userId": 456
     }

4. Sampling:
   - Log all ERRORs
   - Sample only 1% of INFO logs
   - Reduces volume while keeping critical info

5. Distributed Tracing:
   - Tools: Jaeger, Zipkin, AWS X-Ray
   - Visualize request flow across services
   - Identify bottlenecks
```

### **Q5: How would you make logging thread-safe?**

**Answer:**
```
Issues:
1. Multiple threads logging simultaneously
2. File write race conditions
3. Corrupted log lines
4. Performance bottleneck

Solutions:

1. Synchronized File Access:
   synchronized (fileWriterLock) {
       fileWriter.write(message);
       fileWriter.flush();
   }
   Pros: Simple
   Cons: Serializes all writes (slow)

2. Async Logging (Best approach):
   - Main thread adds message to BlockingQueue
   - Background thread processes queue
   - No blocking on main thread
   - Example implementation in Extension #3 above

3. ThreadLocal Buffers:
   - Each thread has own buffer
   - Periodic flush to file
   - Reduces contention
   - Risk: Lost logs if JVM crashes before flush

4. Lock-Free Queues:
   - ConcurrentLinkedQueue
   - AtomicInteger for sequencing
   - High performance
   - Complex to implement correctly

Recommendation:
- Use async logging (BlockingQueue + background thread)
- Buffer size: 10,000 messages
- Flush on buffer full or every 1 second
- Use disruptor library for ultra-high performance
```

### **Q6: Compare Chain of Responsibility vs Template Method for log handling**

**Answer:**
```
Chain of Responsibility (Current):
✅ Dynamic chain - add/remove handlers at runtime
✅ Flexible routing - each handler decides
✅ Multiple handlers can process same message
✅ Easy to test handlers independently
❌ More objects (one per handler)
❌ Harder to debug (trace through chain)

Template Method (Alternative):
public abstract class LogHandler {
    public final void log(LogLevel level, String message) {
        if (shouldLog(level)) {
            preProcess();
            write(message);
            postProcess();
        }
    }

    abstract boolean shouldLog(LogLevel level);
    abstract void write(String message);
    void preProcess() {}  // Hook
    void postProcess() {} // Hook
}

✅ Clear algorithm structure
✅ Guaranteed order of operations
✅ Less objects
❌ Fixed algorithm - hard to change at runtime
❌ Inheritance-based (less flexible)

Use Chain of Responsibility when:
- Multiple handlers might process same message
- Dynamic configuration needed
- Order of handlers changes at runtime

Use Template Method when:
- Algorithm steps are fixed
- Subclasses customize specific steps
- Single handler per message
```

### **Q7: How to test a logging system?**

**Answer:**
```
Unit Tests:

1. Test Singleton:
   - getInstance() returns same instance
   - Thread-safe (multiple threads call getInstance)
   - Configuration persists

2. Test Chain of Responsibility:
   - Message passes through entire chain
   - Each handler processes at correct level
   - Chain stops when needed

3. Test Appenders:
   // Use mock appender
   class MockAppender implements LogAppender {
       List<String> messages = new ArrayList<>();
       @Override
       public void append(String message) {
           messages.add(message);
       }
   }

   @Test
   public void testInfoLogging() {
       MockAppender mock = new MockAppender();
       Logger logger = Logger.getInstance(...);
       logger.info("test message");
       assertEquals(1, mock.messages.size());
       assertTrue(mock.messages.get(0).contains("INFO"));
   }

Integration Tests:
- Write to actual file
- Verify file contents
- Test rotation
- Test concurrent writes

Performance Tests:
- Measure throughput (messages/second)
- Test under load (1000 threads logging)
- Memory usage over time
- CPU usage

Common Issues to Test:
- Null messages
- Very long messages (truncation?)
- Special characters (\n, \t, unicode)
- Concurrent access
- Appender failures (disk full, network down)
```

---

## **⚠️ Known Limitations & Trade-offs**

### **1. No Log Buffering**
- **Current:** Each log writes immediately
- **Issue:** High I/O overhead, slow performance
- **Fix:** Add buffering layer (see Async Logging extension)

### **2. No Log Filtering**
- **Current:** All logs at or above min level are logged
- **Issue:** Can't filter by class, package, or custom criteria
- **Fix:** Add Filter interface with implementations

### **3. No Structured Logging**
- **Current:** Plain text strings
- **Issue:** Hard to parse, search, analyze
- **Fix:** Use JSON format with fields (timestamp, level, class, message, context)

### **4. No MDC (Mapped Diagnostic Context)**
- **Current:** No way to add contextual info (userId, requestId)
- **Issue:** Hard to correlate logs for same user/request
- **Fix:** Add ThreadLocal map for context

```java
class MDC {
    private static ThreadLocal<Map<String, String>> context =
        ThreadLocal.withInitial(HashMap::new);

    public static void put(String key, String value) {
        context.get().put(key, value);
    }

    public static String get(String key) {
        return context.get().get(key);
    }
}

// Usage:
MDC.put("userId", "123");
logger.info("User action");  // Automatically includes userId
```

### **5. Not Distributed-System Ready**
- **Current:** Logs to local file
- **Issue:** Hard to debug in microservices
- **Fix:** Add network appender, correlation IDs

---

## **📚 Key Takeaways**

**Design Patterns Used:**
- ✅ Singleton (thread-safe with double-check locking)
- ✅ Chain of Responsibility (handler chain)
- ✅ Strategy (appender interface)

**SOLID Principles:**
- ✅ All 5 demonstrated

**Concurrency:**
- ✅ Thread-safe Singleton
- ✅ Volatile keyword usage
- ✅ Synchronization considerations

**Extensibility:**
- ✅ Easy to add log levels
- ✅ Easy to add output destinations
- ✅ Easy to customize formatting

**Interview Focus:**
- Double-check locking internals
- Chain of Responsibility benefits
- Distributed logging strategies
- Thread safety mechanisms

---

## **🎓 What You Should Master**

Before interview, ensure you can:

1. ✅ Explain double-check locking step-by-step with memory model
2. ✅ Draw the architecture diagram from memory
3. ✅ Code Singleton getInstance() method correctly
4. ✅ Explain Chain of Responsibility vs if-else
5. ✅ Discuss thread-safety approaches (sync vs async)
6. ✅ Propose 3 extensions (rotation, distributed, structured)
7. ✅ Answer all Q&A confidently
8. ✅ Compare with real frameworks (Log4j, SLF4J)

**Time to master:** 1.5-2 hours

**Difficulty:** ⭐⭐ (Easy - Good starting point for design patterns!)

**Interview Frequency:** ⭐⭐⭐ (Medium - Often asked as warm-up question)

---

## **💡 Pro Tips for Interviews**

1. **Start with requirements** - Ask about scale, log volume, destinations
2. **Mention trade-offs** - Sync vs async, memory vs speed
3. **Show pattern knowledge** - Name patterns and explain why
4. **Discuss production concerns** - Rotation, monitoring, performance
5. **Compare with real systems** - "This is similar to Log4j because..."
6. **Code carefully** - Double-check locking is easy to get wrong!
7. **Think extensibility** - "We could add X by creating a new Y..."

**Common Follow-ups:**
- "How would Log4j/SLF4J differ from this?"
- "Design logging for 1000 microservices"
- "How to handle log storms (millions of logs/sec)?"
- "Implement log sampling (only log 1% of requests)"
