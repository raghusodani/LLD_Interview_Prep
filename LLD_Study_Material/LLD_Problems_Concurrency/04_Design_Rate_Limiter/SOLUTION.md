# Design Rate Limiter - Comprehensive Solution ⏱️

## **Problem Statement**

Design a thread-safe rate limiter system that can:
- Limit the number of requests per time window (e.g., 5 requests per second)
- Support both global rate limiting and per-user rate limiting
- Handle high concurrency with thousands of simultaneous requests
- Automatically refill tokens/capacity over time
- Be extensible for different rate limiting algorithms
- Provide real-time decision making (allow/block requests instantly)

**Real-World Use Cases:**
- API Gateway rate limiting (AWS API Gateway, Kong)
- DDoS protection
- Resource quota management
- Cost control for expensive operations
- Fair usage enforcement

---

## **🎯 Our Approach**

### **1. Algorithm Selection: Token Bucket**

We chose **Token Bucket** over other algorithms because:

| Algorithm | Pros | Cons | Best For |
|-----------|------|------|----------|
| **Token Bucket** ✅ | Allows bursts, Smooth over time, Simple to implement | Needs background refill | API rate limiting, Burst traffic |
| Fixed Window | Simple, Memory efficient | Burst at window edges | Simple counters |
| Sliding Window Log | Precise, No burst issues | Memory intensive (stores timestamps) | Strict limits |
| Leaky Bucket | Smooth output rate | No burst support | Traffic shaping |

**Why Token Bucket?**
1. ✅ **Allows Bursts** - Users can consume 5 tokens instantly (good UX)
2. ✅ **Smooth Long-term Rate** - Averages out to configured rate over time
3. ✅ **Simple State** - Just track token count (not full request history)
4. ✅ **Production-proven** - Used by AWS, Stripe, GitHub APIs

---

## **🏗️ Architecture & Design Patterns**

### **Pattern 1: Strategy Pattern**

**Where:** `IRateLimiter` interface with `TokenBucketStrategy` implementation

**Why:**
- Easy to add new algorithms (Sliding Window, Leaky Bucket)
- Each algorithm is independently testable
- Runtime algorithm switching possible

**Implementation:**
```java
public interface IRateLimiter {
    boolean giveAccess(String rateLimitKey);
    void updateConfiguration(Map<String, Object> config);
    void shutdown();
}

public class TokenBucketStrategy implements IRateLimiter {
    // Token bucket specific implementation
}

// Future: Easy to add
public class SlidingWindowStrategy implements IRateLimiter {
    // Sliding window implementation
}
```

---

### **Pattern 2: Factory Pattern**

**Where:** `RateLimiterFactory` creates appropriate limiter

**Why:**
- Centralized creation logic
- Easy to configure different algorithms
- Hides implementation details from clients

**Implementation:**
```java
public class RateLimiterFactory {
    public static IRateLimiter createLimiter(RateLimiterType type, Map<String, Object> config) {
        switch(type) {
            case TOKEN_BUCKET:
                return new TokenBucketStrategy(
                    (int) config.get("capacity"),
                    (int) config.get("refreshRate")
                );
            // Easy to add new types
        }
    }
}
```

---

### **Pattern 3: Asynchronous Request Processing**

**Where:** `RateLimiterController` uses `CompletableFuture`

**Why:**
- Non-blocking request handling
- Better throughput under load
- Parallel processing of rate limit checks

**Implementation:**
```java
public CompletableFuture<Boolean> processRequest(String rateLimitKey) {
    return CompletableFuture.supplyAsync(() -> {
        return rateLimiter.giveAccess(rateLimitKey);
    }, executor);
}
```

---

## **🔒 Concurrency Design**

### **Thread Safety Mechanisms**

#### **1. ReentrantLock for Token Operations**

**Why not synchronized?**
```java
// ❌ synchronized - coarse-grained, less flexible
synchronized(bucket) {
    if (tokens > 0) tokens--;
}

// ✅ ReentrantLock - more control, better fairness
lock.lock();
try {
    if (tokens > 0) {
        tokens--;
        return true;
    }
    return false;
} finally {
    lock.unlock();
}
```

**Benefits:**
- Explicit lock/unlock (better for complex logic)
- Try-lock with timeout possible
- Fairness policy available
- Interruptible lock acquisition

---

#### **2. ConcurrentHashMap for Per-User Buckets**

**Why ConcurrentHashMap?**
```java
// ✅ Thread-safe map for user buckets
private final ConcurrentHashMap<String, Bucket> userBuckets;

// Atomic get-or-create operation
Bucket bucket = userBuckets.computeIfAbsent(
    rateLimitKey,
    key -> new Bucket(bucketCapacity)
);
```

**Benefits:**
- Lock-free reads (multiple threads can read simultaneously)
- Segment-level locking for writes (better than full map lock)
- `computeIfAbsent` is atomic (no race condition in bucket creation)

---

#### **3. Volatile for Refresh Rate**

```java
private volatile int refreshRate;
```

**Why volatile?**
- Ensures visibility across threads
- Background refill thread sees updated rate immediately
- Lightweight compared to locks for simple reads

---

#### **4. Scheduled Background Refill**

```java
scheduler.scheduleAtFixedRate(() -> {
    globalBucket.refill();
    userBuckets.values().forEach(Bucket::refill);
}, 1000, 1000, TimeUnit.MILLISECONDS);
```

**Why separate thread?**
- Decouples refill from request processing
- Consistent timing regardless of request rate
- No overhead on request path

---

## **🪣 Token Bucket Algorithm Explained**

### **Conceptual Model:**

```
Bucket State at t=0:
┌─────────────────────┐
│  [●●●●●]  (5/5)     │  ← Bucket full with 5 tokens
└─────────────────────┘

Request 1 arrives:
┌─────────────────────┐
│  [●●●●○]  (4/5)     │  ← Consumed 1 token, ✅ ALLOWED
└─────────────────────┘

Request 2 arrives:
┌─────────────────────┐
│  [●●●○○]  (3/5)     │  ← Consumed 1 token, ✅ ALLOWED
└─────────────────────┘

... 3 more requests ...
┌─────────────────────┐
│  [○○○○○]  (0/5)     │  ← All tokens consumed
└─────────────────────┘

Request 6 arrives:
┌─────────────────────┐
│  [○○○○○]  (0/5)     │  ← No tokens! ❌ BLOCKED
└─────────────────────┘

Wait 1 second (refill +1 token/sec):
┌─────────────────────┐
│  [●○○○○]  (1/5)     │  ← Refilled 1 token
└─────────────────────┘

Request 7 arrives:
┌─────────────────────┐
│  [○○○○○]  (0/5)     │  ← Consumed refilled token, ✅ ALLOWED
└─────────────────────┘
```

### **Key Parameters:**

1. **Bucket Capacity (5)** - Maximum tokens that can accumulate
2. **Refill Rate (1/sec)** - Tokens added per second
3. **Consumption (1/request)** - Tokens consumed per request

---

## **🔑 Key Design Decisions**

### **Decision 1: Per-User vs Global Buckets**

**What:** Support both patterns with same code

```java
public boolean giveAccess(String rateLimitKey) {
    if (rateLimitKey != null && !rateLimitKey.isEmpty()) {
        // Per-user bucket (fair quotas)
        Bucket bucket = userBuckets.computeIfAbsent(
            rateLimitKey, key -> new Bucket(bucketCapacity)
        );
        return bucket.tryConsume();
    } else {
        // Global bucket (total system limit)
        return globalBucket.tryConsume();
    }
}
```

**Why both?**
- Global: Protect system resources (total throughput limit)
- Per-user: Prevent single user from hogging resources (fairness)
- Real systems use **both** (global AND per-user limits)

**Interview Question:**
> "Should we use global or per-user rate limiting?"

**Answer:**
> "In production, you use **both**. Global limit protects your infrastructure (e.g., max 10,000 req/sec total). Per-user limit ensures fairness (e.g., max 100 req/sec per user). This prevents one bad actor from consuming all global capacity while still protecting your servers."

---

### **Decision 2: Background Refill vs Lazy Refill**

**Our Choice: Background Refill**
```java
scheduler.scheduleAtFixedRate(() -> {
    globalBucket.refill();
    // ... refill all buckets
}, 1000, 1000, TimeUnit.MILLISECONDS);
```

**Alternative: Lazy Refill**
```java
public boolean tryConsume() {
    long now = System.currentTimeMillis();
    long elapsed = now - lastRefillTime;
    int tokensToAdd = (int) (elapsed / refillInterval);
    tokens = Math.min(capacity, tokens + tokensToAdd);
    lastRefillTime = now;
    // ... consume logic
}
```

**Comparison:**

| Approach | Pros | Cons |
|----------|------|------|
| Background Refill ✅ | Simple logic, Consistent timing, No math on hot path | Extra thread, Memory for scheduler |
| Lazy Refill | No background thread, Memory efficient | Complex calculation on each request, Time sync issues |

**Why Background?**
- Request path is **critical** - must be fast
- Refill overhead on separate thread (doesn't slow requests)
- Simpler to reason about (no time calculations)

---

### **Decision 3: ReentrantLock vs synchronized vs Atomic**

**Option Analysis:**

```java
// Option 1: synchronized
synchronized(bucket) {
    if (tokens > 0) tokens--;
}
// ✅ Simple
// ❌ Less control, no fairness guarantees

// Option 2: AtomicInteger
AtomicInteger tokens;
tokens.compareAndSet(current, current - 1);
// ✅ Lock-free, fast
// ❌ Complex for multi-field operations, retry loops

// Option 3: ReentrantLock ✅ (our choice)
lock.lock();
try {
    if (tokens > 0) {
        tokens--;
        return true;
    }
} finally {
    lock.unlock();
}
// ✅ Explicit control, fairness option
// ✅ Works well with complex logic
// ✅ Try-lock with timeout possible
```

**Interview Question:**
> "Why not use AtomicInteger for lock-free implementation?"

**Answer:**
> "AtomicInteger works for single-field operations, but our bucket might need multiple fields (tokens, last refill time, rate limits). With multiple fields, you'd need retry loops with CAS (compare-and-swap), which gets complex. ReentrantLock keeps the code simple while providing good performance. For extreme scale, lock-free might be worth the complexity, but for most cases, ReentrantLock is the sweet spot."

---

### **Decision 4: ExecutorService for Request Processing**

**What:** Process requests asynchronously with thread pool

```java
private final ExecutorService executor;

public CompletableFuture<Boolean> processRequest(String key) {
    return CompletableFuture.supplyAsync(
        () -> rateLimiter.giveAccess(key),
        executor
    );
}
```

**Why?**
- Non-blocking: Caller doesn't wait for rate limit check
- Parallel processing: Handle multiple requests simultaneously
- Backpressure: Thread pool size limits concurrent operations

---

## **🎭 Scenario Walkthrough**

### **Scenario 1: Burst Traffic**

```
Configuration: capacity=5, refillRate=1/sec

Timeline:
t=0ms:  10 requests arrive simultaneously
        Bucket: [●●●●●] (5/5)

t=1ms:  Request 1 → [●●●●○] (4/5) ✅ ALLOWED
t=2ms:  Request 2 → [●●●○○] (3/5) ✅ ALLOWED
t=3ms:  Request 3 → [●●○○○] (2/5) ✅ ALLOWED
t=4ms:  Request 4 → [●○○○○] (1/5) ✅ ALLOWED
t=5ms:  Request 5 → [○○○○○] (0/5) ✅ ALLOWED
t=6ms:  Request 6 → [○○○○○] (0/5) ❌ BLOCKED
t=7ms:  Request 7 → [○○○○○] (0/5) ❌ BLOCKED
t=8ms:  Request 8 → [○○○○○] (0/5) ❌ BLOCKED
t=9ms:  Request 9 → [○○○○○] (0/5) ❌ BLOCKED
t=10ms: Request 10 → [○○○○○] (0/5) ❌ BLOCKED

Result: 5 allowed, 5 blocked ✅
```

### **Scenario 2: Refill and Recovery**

```
t=1000ms: Refill triggers (+1 token)
          Bucket: [●○○○○] (1/5)

t=1001ms: Request 11 → [○○○○○] (0/5) ✅ ALLOWED

t=2000ms: Refill triggers (+1 token)
          Bucket: [●○○○○] (1/5)

t=3000ms: Refill triggers (+1 token)
          Bucket: [●●○○○] (2/5)

Conclusion: System gradually recovers at refillRate
```

### **Scenario 3: Per-User Isolation**

```
User A: Makes 5 requests
        Bucket A: [●●●●●] → [○○○○○]
        Result: All 5 allowed

User B: Makes 3 requests (simultaneously with A)
        Bucket B: [●●●●●] → [●●○○○]
        Result: All 3 allowed

Key Insight: Users don't affect each other's quotas!
```

---

## **🚀 Extensions & Algorithm Comparisons**

### **Extension 1: Sliding Window Counter**

**How it works:**
- Divide time into windows (e.g., 1-second windows)
- Track request count in current + previous window
- Weighted average for smooth transitions

**Implementation Idea:**
```java
public class SlidingWindowStrategy implements IRateLimiter {
    private AtomicInteger currentWindowCount;
    private AtomicInteger previousWindowCount;
    private long currentWindowStart;

    public boolean giveAccess(String key) {
        long now = System.currentTimeMillis();
        updateWindows(now);

        double weightedCount = previousWindowCount.get() *
            (1 - (now - currentWindowStart) / windowSize) +
            currentWindowCount.get();

        if (weightedCount < limit) {
            currentWindowCount.incrementAndGet();
            return true;
        }
        return false;
    }
}
```

**Pros:** More precise than fixed window, no burst at edges
**Cons:** More complex, higher memory

---

### **Extension 2: Leaky Bucket**

**Difference from Token Bucket:**
- Token Bucket: "Can I spend a token?" (allows bursts)
- Leaky Bucket: "Add to queue, process at fixed rate" (smooth output)

**Use Case:** Traffic shaping, outbound rate limiting

**Implementation:**
```java
public class LeakyBucketStrategy implements IRateLimiter {
    private final BlockingQueue<Request> queue;

    public LeakyBucketStrategy() {
        queue = new ArrayBlockingQueue<>(capacity);
        startLeakingThread(); // Process queue at fixed rate
    }

    public boolean giveAccess(String key) {
        return queue.offer(new Request(key)); // False if full
    }

    private void startLeakingThread() {
        scheduler.scheduleAtFixedRate(() -> {
            Request req = queue.poll();
            if (req != null) processRequest(req);
        }, 0, leakRate, TimeUnit.MILLISECONDS);
    }
}
```

---

### **Extension 3: Distributed Rate Limiting**

**Current Limitation:** Single JVM only

**Solution 1: Redis-based Token Bucket**
```java
public class RedisTokenBucketStrategy implements IRateLimiter {
    private final RedisTemplate<String, Integer> redis;

    public boolean giveAccess(String key) {
        String bucketKey = "ratelimit:" + key;

        // Lua script for atomic operation
        String script =
            "local tokens = redis.call('get', KEYS[1]) " +
            "if tokens == false then tokens = ARGV[1] end " +
            "if tonumber(tokens) > 0 then " +
            "  redis.call('decr', KEYS[1]) " +
            "  return 1 " +
            "else return 0 end";

        Long result = redis.execute(
            new DefaultRedisScript<>(script, Long.class),
            Collections.singletonList(bucketKey),
            capacity
        );

        return result == 1;
    }
}
```

**Solution 2: Distributed Lock (Slower)**
```java
public boolean giveAccess(String key) {
    RLock lock = redisson.getLock("lock:" + key);
    try {
        lock.lock();
        int tokens = getTokens(key);
        if (tokens > 0) {
            setTokens(key, tokens - 1);
            return true;
        }
        return false;
    } finally {
        lock.unlock();
    }
}
```

**Comparison:**

| Approach | Latency | Consistency | Complexity |
|----------|---------|-------------|------------|
| Lua Script ✅ | ~1ms | Strong | Medium |
| Distributed Lock | ~10ms | Strong | High |
| Local + Gossip | ~0.1ms | Eventual | High |

---

## **🎯 Interview Questions & Answers**

### **Q1: How would you implement distributed rate limiting across multiple servers?**

**Answer:**
```
Approaches:

1. **Redis with Lua Script** (Best for most cases)
   - Use Redis as central token store
   - Atomic operations via Lua scripts
   - Pros: Fast (~1ms), strongly consistent
   - Cons: Single point of failure (mitigate with Redis Cluster)

2. **Token Bucket + Sticky Sessions**
   - Route same user to same server (load balancer)
   - Each server has local rate limiter
   - Pros: Fast, no network calls
   - Cons: Uneven load, failover issues

3. **Distributed Counter (Cassandra/DynamoDB)**
   - Use distributed DB with atomic increments
   - Each server increments counter
   - Pros: Highly available, scalable
   - Cons: Higher latency (~5-10ms), eventual consistency

4. **Rate Limit Service (Microservice)**
   - Dedicated service for rate limiting
   - HTTP API: GET /check-limit/{userId}
   - Pros: Centralized logic, easy updates
   - Cons: Network hop, latency

Production Choice: Redis + Lua Script
- Fast enough for APIs
- Strongly consistent (no double-spending)
- Easy to implement and debug
```

---

### **Q2: What if Redis goes down?**

**Answer:**
```
Failure Mode Strategies:

1. **Fail-Open (Allow All)**
   try {
       boolean allowed = redisRateLimiter.giveAccess(key);
   } catch (RedisConnectionException e) {
       return true; // Allow request during outage
   }
   Pros: Service stays up
   Cons: No rate limiting during outage

2. **Fail-Closed (Block All)**
   Pros: Protects backend
   Cons: Bad UX, availability impact

3. **Fallback to Local Rate Limiter**
   try {
       return redisRateLimiter.giveAccess(key);
   } catch (RedisConnectionException e) {
       return localRateLimiter.giveAccess(key); // Loose limits
   }
   Pros: Some protection, service stays up
   Cons: Limits not globally enforced

4. **Redis Cluster with Failover**
   - Use Redis Sentinel/Cluster
   - Automatic failover to replica
   - Pros: High availability
   - Cons: Complexity, cost

Production: Combination of #3 (local fallback) + #4 (Redis HA)
```

---

### **Q3: How to handle different tiers (free vs paid users)?**

**Answer:**
```java
public class TieredRateLimiter {
    private Map<String, RateLimitConfig> tierConfigs;
    private Map<String, IRateLimiter> limiters;

    public boolean giveAccess(String userId) {
        String tier = getUserTier(userId); // "free" / "premium"
        RateLimitConfig config = tierConfigs.get(tier);

        // Lazy create limiter for this tier
        IRateLimiter limiter = limiters.computeIfAbsent(
            tier,
            t -> new TokenBucketStrategy(
                config.getCapacity(),
                config.getRefillRate()
            )
        );

        return limiter.giveAccess(userId);
    }
}

// Configuration
tierConfigs.put("free", new Config(10, 1));      // 10/sec
tierConfigs.put("premium", new Config(100, 10)); // 100/sec
tierConfigs.put("enterprise", new Config(1000, 100)); // 1000/sec
```

**Variations:**
- Separate buckets per tier
- Hierarchical limits (user < tier < global)
- Dynamic tier upgrades
- Burst allowances for paid users

---

### **Q4: What about Rate Limiting by IP vs User ID?**

**Answer:**
```
Multi-Dimensional Rate Limiting:

1. **By IP (DDoS Protection)**
   - Catches unauthenticated attacks
   - Problem: Shared IPs (corporate NAT, mobile networks)

2. **By User ID (Fair Usage)**
   - Authenticated users only
   - Problem: Can't protect login endpoint

3. **By API Key (Service-to-Service)**
   - Each client app gets key
   - Fine-grained tracking

4. **Layered Approach** ✅ (Production)
   - IP-based (loose): 1000/min per IP
   - User-based (strict): 100/min per user
   - Endpoint-based: 10/min for /expensive-operation

Implementation:
boolean allowed =
    ipRateLimiter.giveAccess(request.getIp()) &&
    userRateLimiter.giveAccess(request.getUserId()) &&
    endpointRateLimiter.giveAccess(request.getPath());

All must pass for request to proceed.
```

---

### **Q5: How to handle rate limit exceeded responses?**

**Answer:**
```java
public Response handleRequest(Request request) {
    if (!rateLimiter.giveAccess(request.getUserId())) {
        // Return 429 Too Many Requests
        return Response.status(429)
            .header("X-RateLimit-Limit", "100")
            .header("X-RateLimit-Remaining", "0")
            .header("X-RateLimit-Reset", nextResetTime)
            .header("Retry-After", "60") // Seconds
            .body("Rate limit exceeded. Try again in 60 seconds.")
            .build();
    }

    // Process request normally
    return processRequest(request);
}
```

**Standard Headers:**
- `X-RateLimit-Limit`: Total allowed
- `X-RateLimit-Remaining`: Remaining in window
- `X-RateLimit-Reset`: Unix timestamp of reset
- `Retry-After`: Seconds until next attempt

**Client-Side Handling:**
```javascript
async function makeRequest() {
    const response = await fetch('/api/data');

    if (response.status === 429) {
        const retryAfter = response.headers.get('Retry-After');
        console.log(`Rate limited. Retry after ${retryAfter}s`);

        // Exponential backoff
        await sleep(retryAfter * 1000);
        return makeRequest(); // Retry
    }

    return response.json();
}
```

---

### **Q6: Comparison of Rate Limiting Algorithms**

**Answer:**

| Algorithm | Memory | Precision | Burst | Complexity |
|-----------|--------|-----------|-------|------------|
| **Token Bucket** ✅ | O(1) | Good | Yes | Low |
| **Leaky Bucket** | O(n) | Excellent | No | Medium |
| **Fixed Window** | O(1) | Poor | Edge bursts | Low |
| **Sliding Window Log** | O(n) | Excellent | No | High |
| **Sliding Window Counter** | O(1) | Good | Controlled | Medium |

**When to use each:**

**Token Bucket**: General-purpose API rate limiting
- Example: GitHub API, Stripe API

**Leaky Bucket**: Traffic shaping, smooth output
- Example: Video streaming, network QoS

**Fixed Window**: Simple counters, analytics
- Example: "Views per day" counters

**Sliding Window Log**: Strict limits, audit trails
- Example: Banking transactions, compliance

**Sliding Window Counter**: Better than fixed window, less memory than log
- Example: High-traffic APIs with strict limits

---

### **Q7: How to test rate limiters?**

**Answer:**
```java
@Test
public void testBurstScenario() {
    // Config: 5 tokens, 1 per second
    TokenBucketStrategy limiter = new TokenBucketStrategy(5, 1);

    // Burst: First 5 should pass
    for (int i = 0; i < 5; i++) {
        assertTrue(limiter.giveAccess("user1"));
    }

    // 6th should fail (no tokens left)
    assertFalse(limiter.giveAccess("user1"));

    // Wait for refill
    Thread.sleep(1000);

    // Should pass after refill
    assertTrue(limiter.giveAccess("user1"));
}

@Test
public void testConcurrency() throws Exception {
    TokenBucketStrategy limiter = new TokenBucketStrategy(100, 10);
    ExecutorService executor = Executors.newFixedThreadPool(50);

    AtomicInteger allowed = new AtomicInteger(0);
    AtomicInteger blocked = new AtomicInteger(0);

    // 200 concurrent requests
    CountDownLatch latch = new CountDownLatch(200);
    for (int i = 0; i < 200; i++) {
        executor.submit(() -> {
            if (limiter.giveAccess("user1")) {
                allowed.incrementAndGet();
            } else {
                blocked.incrementAndGet();
            }
            latch.countDown();
        });
    }

    latch.await();

    // Should allow exactly 100 (capacity)
    assertEquals(100, allowed.get());
    assertEquals(100, blocked.get());
}

@Test
public void testPerUserIsolation() {
    TokenBucketStrategy limiter = new TokenBucketStrategy(5, 1);

    // User1 exhausts quota
    for (int i = 0; i < 5; i++) {
        limiter.giveAccess("user1");
    }
    assertFalse(limiter.giveAccess("user1"));

    // User2 should still have quota
    assertTrue(limiter.giveAccess("user2"));
}
```

---

### **Q8: Rate Limiting vs Circuit Breaker - What's the difference?**

**Answer:**
```
Rate Limiter vs Circuit Breaker:

┌─────────────────────────────────────────────────┐
│ RATE LIMITER (Incoming Protection)              │
│                                                  │
│ Purpose: Protect YOUR service from overload     │
│ Trigger: Request rate exceeds limit             │
│ Action: Reject request immediately (429)        │
│ Recovery: Gradual (tokens refill over time)     │
│                                                  │
│ Example: "Max 100 API calls/min per user"       │
└─────────────────────────────────────────────────┘

┌─────────────────────────────────────────────────┐
│ CIRCUIT BREAKER (Outgoing Protection)           │
│                                                  │
│ Purpose: Protect DOWNSTREAM service from calls  │
│ Trigger: Error rate exceeds threshold           │
│ Action: Stop calling failing service            │
│ Recovery: Periodic retry (half-open state)      │
│                                                  │
│ Example: "Stop calling DB if 50% errors"        │
└─────────────────────────────────────────────────┘

Use Both Together:
[Client] → [Rate Limiter] → [Your API] → [Circuit Breaker] → [Database]
           ↑ Protects you                 ↑ Protects downstream
```

---

### **Q9: How do cloud providers implement rate limiting?**

**Answer:**
```
AWS API Gateway Rate Limiting:

1. **Token Bucket Algorithm** (same as ours!)
   - Burst limit (bucket capacity)
   - Steady-state rate (refill rate)

2. **Distributed via Edge Locations**
   - CloudFront edge caches rate limits
   - DynamoDB for global state
   - Eventually consistent

3. **Multiple Levels**
   - Account-level limits
   - API-level limits
   - Stage-level limits
   - Method-level limits

4. **Configuration**
   ```json
   {
     "throttle": {
       "burstLimit": 5000,
       "rateLimit": 10000  // req/sec steady state
     }
   }
   ```

5. **Response Headers**
   - X-Amzn-RateLimit-Limit
   - X-Amzn-RateLimit-Remaining
   - 429 with retry-after header

Stripe API:
- Uses Redis + Token Bucket
- Per-key rate limiting
- Different limits per API tier
- Implements leaky bucket for burst protection

GitHub API:
- 5000 req/hour (authenticated)
- 60 req/hour (unauthenticated)
- Separate limits for GraphQL
- Rate limit reflected in headers
```

---

### **Q10: Cost-Benefit: When NOT to use rate limiting?**

**Answer:**
```
Don't Rate Limit When:

1. **Internal Microservices**
   - Within same VPC/trust boundary
   - Use Circuit Breaker instead
   - Overhead not worth it

2. **Real-Time Critical Systems**
   - Emergency services (911)
   - Healthcare monitoring
   - Safety systems
   - Availability > fairness

3. **Batch Processing**
   - Offline jobs
   - Use queue with concurrency control instead

4. **Infinite Capacity**
   - Static file serving (CDN)
   - Already auto-scaling
   - Cost is not a concern

DO Rate Limit When:
✅ Public APIs (prevent abuse)
✅ Expensive operations (DB writes, AI calls)
✅ Limited resources (connections, licenses)
✅ Cost control (pay-per-use APIs)
✅ Fair usage enforcement

Trade-offs:
- Latency: +1-5ms per request
- Complexity: State management
- Availability: Rate limiter must be HA
- False positives: Legitimate burst traffic blocked
```

---

## **⚠️ Known Limitations & Trade-offs**

1. **Single JVM Only**
   - Current: Works only in one application instance
   - Fix: Use Redis or distributed cache

2. **No Persistence**
   - Restart = Lost all token counts
   - Fix: Periodically save state to DB

3. **Fixed Refill Rate**
   - Can't do dynamic pricing (peak hours)
   - Fix: Add time-based rate adjustment

4. **Memory Growth**
   - User buckets never cleaned up
   - Fix: Add TTL for inactive users

5. **No Request Priority**
   - All requests treated equally
   - Fix: Add priority queue for requests

---

## **📚 Key Takeaways**

**Algorithm:**
- ✅ Token Bucket for API rate limiting
- ✅ Allows bursts, smooth long-term rate
- ✅ Simple state (just token count)

**Concurrency:**
- ✅ ReentrantLock for atomic operations
- ✅ ConcurrentHashMap for per-user buckets
- ✅ Background refill thread
- ✅ Async request processing

**Design Patterns:**
- ✅ Strategy (easy to add algorithms)
- ✅ Factory (centralized creation)
- ✅ CompletableFuture (async processing)

**Production Considerations:**
- ✅ Distributed via Redis
- ✅ Multi-tier (IP + User + Endpoint)
- ✅ Proper HTTP headers (429, Retry-After)
- ✅ Monitoring and alerting

---

## **🎓 What You Should Master**

Before interview, ensure you can:
1. ✅ Explain Token Bucket algorithm with diagram
2. ✅ Compare 4+ rate limiting algorithms
3. ✅ Design distributed rate limiter with Redis
4. ✅ Handle multi-tier scenarios (free vs paid)
5. ✅ Discuss concurrency mechanisms (locks, atomic, ConcurrentHashMap)
6. ✅ Explain failure modes (Redis down, network partition)
7. ✅ Answer all 10 Q&A questions confidently
8. ✅ Write pseudo-code for Sliding Window algorithm
9. ✅ Discuss trade-offs (latency vs accuracy vs complexity)
10. ✅ Propose monitoring and alerting strategy

**Time to master:** 3-4 hours of practice

**Difficulty:** ⭐⭐⭐⭐ (Hard - Common in senior+ interviews)

**Interview Frequency:** ⭐⭐⭐⭐⭐ (Very High - Asked at FAANG, Stripe, etc.)
