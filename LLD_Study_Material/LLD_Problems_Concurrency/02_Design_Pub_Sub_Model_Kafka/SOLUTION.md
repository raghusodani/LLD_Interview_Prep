# Design Pub-Sub Model (Kafka-like) - Comprehensive Solution 📡

## **Problem Statement**

Design a high-throughput, asynchronous message queue system similar to Apache Kafka that can:
- Handle multiple publishers sending messages concurrently
- Support multiple subscribers consuming messages independently
- Organize messages into topics
- Process messages asynchronously (non-blocking)
- Scale to handle thousands of messages per second
- Guarantee message delivery to all subscribers
- Support at-least-once delivery semantics

---

## **🎯 Our Approach**

### **Core Requirements Analysis**

**Functional Requirements:**
- ✅ Create topics for message organization
- ✅ Publishers can send messages to topics
- ✅ Subscribers can consume messages from topics
- ✅ Multiple subscribers on same topic (broadcast)
- ✅ Asynchronous message processing
- ✅ Message ordering per topic
- ✅ Subscriber independence (one slow subscriber doesn't block others)

**Non-Functional Requirements:**
- ✅ **High Throughput** - Handle thousands of messages/sec
- ✅ **Low Latency** - Fast message delivery
- ✅ **Scalability** - Add publishers/subscribers dynamically
- ✅ **Fault Tolerance** - Handle subscriber failures gracefully
- ✅ **Thread Safety** - Concurrent access from multiple threads
- ✅ **Decoupling** - Publishers don't know about subscribers

---

## **🏗️ Architecture & Design Patterns**

### **Pattern 1: Producer-Consumer Pattern** ⭐ **CORE**

**Where:** Publisher → Topic → Subscriber relationship

**Why:**
- Decouple message producers from consumers
- Buffer messages in queue for async processing
- Handle different processing speeds
- Enable scalability and fault tolerance

**Implementation:**

```java
// Topic acts as the buffer (queue)
public class Topic {
    private BlockingQueue<Message> messageQueue;

    public synchronized void addMessage(Message message) {
        messageQueue.offer(message); // Non-blocking add
    }

    public synchronized List<Message> getMessages() {
        List<Message> messages = new ArrayList<>();
        messageQueue.drainTo(messages); // Batch retrieval
        return messages;
    }
}

// Publisher (Producer)
public class TopicPublisher implements Runnable {
    @Override
    public void run() {
        while (running) {
            Message msg = publisher.publish();
            topic.addMessage(msg); // Async, doesn't wait
        }
    }
}

// Subscriber (Consumer)
public class TopicSubscriber implements Runnable {
    @Override
    public void run() {
        while (running) {
            List<Message> messages = topic.getMessages();
            for (Message msg : messages) {
                subscriber.consume(msg); // Process independently
            }
            Thread.sleep(interval); // Polling interval
        }
    }
}
```

**Benefits:**
- ✅ Publishers never wait for subscribers
- ✅ Subscribers process at their own pace
- ✅ Easy to add/remove subscribers
- ✅ Natural load balancing

---

### **Pattern 2: Observer Pattern**

**Where:** Topic notifies all subscribers

**Why:**
- One-to-many relationship
- Topic doesn't need to know subscriber details
- Dynamic subscription management

**Implementation:**

```java
// Topic is the Subject
public class Topic {
    private List<TopicSubscriber> subscribers = new ArrayList<>();

    public void addSubscriber(TopicSubscriber subscriber) {
        subscribers.add(subscriber);
    }

    // Each subscriber observes and pulls messages independently
}
```

**Benefits:**
- ✅ Loose coupling between topic and subscribers
- ✅ Easy to add new subscriber types
- ✅ Follows Open/Closed Principle

---

### **Pattern 3: Thread Pool (ExecutorService)**

**Where:** Managing publisher and subscriber threads

**Why:**
- Efficient thread management
- Avoid thread creation overhead
- Control resource usage
- Graceful shutdown

**Implementation:**

```java
public class KafkaController {
    private ExecutorService publisherPool;
    private ExecutorService subscriberPool;

    public void startPublisher(IPublisher publisher, Topic topic) {
        TopicPublisher topicPublisher = new TopicPublisher(topic, publisher);
        publisherPool.execute(topicPublisher); // Managed by pool
    }

    public void startSubscriber(ISubscriber subscriber, Topic topic) {
        TopicSubscriber topicSubscriber = new TopicSubscriber(topic, subscriber);
        subscriberPool.execute(topicSubscriber); // Managed by pool
    }
}
```

**Benefits:**
- ✅ Thread reuse (no creation overhead)
- ✅ Bounded resources (max thread limit)
- ✅ Graceful shutdown with `shutdown()` and `awaitTermination()`
- ✅ Exception handling per task

---

### **Pattern 4: Blocking Queue**

**Where:** Message buffer in Topic

**Why:**
- Thread-safe out of the box
- Built-in coordination primitives
- Handles producer-consumer synchronization
- Supports backpressure

**Implementation:**

```java
public class Topic {
    // BlockingQueue provides thread-safe operations
    private BlockingQueue<Message> messageQueue = new LinkedBlockingQueue<>();

    public synchronized void addMessage(Message message) {
        messageQueue.offer(message); // Returns false if full
    }

    public synchronized List<Message> getMessages() {
        List<Message> messages = new ArrayList<>();
        messageQueue.drainTo(messages); // Atomic batch retrieval
        return messages;
    }
}
```

**Why BlockingQueue over ArrayList:**
- ✅ Thread-safe (no external synchronization needed)
- ✅ Blocking operations (`take()`, `put()`)
- ✅ Capacity limits for backpressure
- ✅ Atomic operations

---

## **📐 Complete Architecture**

```
┌─────────────────────────────────────────────────────────┐
│                    KafkaController                      │
│  (Orchestrator - Manages Topics, Publishers, Subscribers)│
└──────────────┬──────────────────────────┬───────────────┘
               │                          │
       ┌───────▼────────┐         ┌──────▼────────┐
       │ Publisher Pool │         │Subscriber Pool│
       │ ExecutorService│         │ExecutorService│
       └───────┬────────┘         └──────┬────────┘
               │                          │
       ┌───────▼────────────┐    ┌────────▼──────────────┐
       │  TopicPublisher    │    │  TopicSubscriber      │
       │  (Runnable)        │    │  (Runnable)           │
       │  - Polls publisher │    │  - Polls topic        │
       │  - Adds to topic   │    │  - Calls subscriber   │
       └───────┬────────────┘    └────────┬──────────────┘
               │                          │
               │      ┌───────────┐       │
               └─────►│   Topic   │◄──────┘
                      │           │
                      │ BlockingQueue<Message>
                      │ - Thread-safe buffer
                      │ - FIFO ordering
                      └───────────┘
                             ▲
                             │
                      ┌──────┴──────┐
                      │   Message   │
                      │ - id        │
                      │ - content   │
                      │ - timestamp │
                      └─────────────┘

┌──────────────────────────────────────────────────────────┐
│              Producer-Consumer Flow                      │
│                                                          │
│  Publisher1 ──┐                                         │
│  Publisher2 ──┼──► [Topic Queue] ──┬─► Subscriber1     │
│  Publisher3 ──┘                     ├─► Subscriber2     │
│                                     └─► Subscriber3     │
│                                                          │
│  - Publishers write asynchronously                      │
│  - Subscribers read independently at their own pace     │
│  - Queue provides buffering and decoupling              │
└──────────────────────────────────────────────────────────┘
```

---

## **🔑 Key Design Decisions**

### **Decision 1: Pull Model vs Push Model**

**What:** Subscribers PULL messages from topic (polling)

```java
// Pull Model (Our Implementation)
while (running) {
    List<Message> messages = topic.getMessages(); // Poll
    for (Message msg : messages) {
        subscriber.consume(msg);
    }
    Thread.sleep(100); // Polling interval
}

// Alternative: Push Model
topic.addMessage(msg);
// Topic immediately pushes to all subscribers
for (Subscriber s : subscribers) {
    s.onMessage(msg); // Blocking!
}
```

**Why Pull Over Push:**
- ✅ Subscribers control processing rate (backpressure)
- ✅ Slow subscriber doesn't block fast ones
- ✅ Subscribers can batch process messages
- ✅ Easy to implement consumer lag

**Trade-off:**
- ❌ Polling overhead (empty polls)
- ❌ Higher latency (up to polling interval)
- ✅ Better for high-throughput scenarios

**Interview Question:**
> "Why use pull model instead of push?"

**Answer:**
> "Pull model gives subscribers control over their consumption rate, which is critical for scalability. If we push messages, a slow subscriber blocks the publisher or requires complex buffering per subscriber. With pull, each subscriber manages its own pace, and we get natural backpressure. This is how Kafka works in production."

---

### **Decision 2: Thread Per Topic Publisher/Subscriber**

**What:** Each publisher-topic and subscriber-topic pair gets its own thread

```java
// One thread per TopicPublisher
TopicPublisher tp1 = new TopicPublisher(topic1, publisher1);
publisherPool.execute(tp1);

// One thread per TopicSubscriber
TopicSubscriber ts1 = new TopicSubscriber(topic1, subscriber1);
subscriberPool.execute(ts1);
```

**Why:**
- Publishers/subscribers have independent lifecycle
- No coordination needed between publishers
- Easy to start/stop individual workers
- Natural isolation (failure doesn't cascade)

**Interview Question:**
> "Why not one publisher thread for all topics?"

**Answer:**
> "Separate threads provide isolation and simplify the design. If one topic has a slow subscriber, it doesn't affect other topics. We use thread pools to manage resources efficiently. In production Kafka, this is taken further with partition-level parallelism."

---

### **Decision 3: Synchronized Methods on Topic**

**What:** Topic operations are synchronized

```java
public synchronized void addMessage(Message message) {
    messageQueue.offer(message);
}

public synchronized List<Message> getMessages() {
    List<Message> messages = new ArrayList<>();
    messageQueue.drainTo(messages);
    return messages;
}
```

**Why:**
- BlockingQueue is already thread-safe
- But we want atomic batch operations
- Prevent interleaving during drain operation
- Simple to reason about

**Trade-off:**
- Global lock on topic (could be bottleneck)
- Better: Fine-grained locking or lock-free structures
- Good enough for learning/moderate load

**Interview Question:**
> "Isn't synchronized too coarse-grained?"

**Answer:**
> "Yes, for production scale. We'd use ConcurrentLinkedQueue or partition the topic. Kafka solves this with partitioning - multiple independent queues per topic, enabling parallel consumption. Our design is a learning foundation that shows the core concepts before adding complexity."

---

### **Decision 4: Message Ordering**

**What:** FIFO ordering within a topic using BlockingQueue

**Why:**
- Natural ordering for queue data structure
- Matches user expectations
- Simplifies debugging

**Limitation:**
- Only guarantees ordering per topic, not across topics
- With partitions, ordering is only per partition

**Interview Question:**
> "How would you guarantee total ordering across all messages?"

**Answer:**
> "Total ordering requires a single queue with a global lock, which doesn't scale. Better approach: (1) Assign sequence numbers to all messages with a monotonic counter, (2) Use a single-writer pattern, or (3) Accept that distributed systems sacrifice total ordering for scalability (Kafka's approach with partitions)."

---

## **💡 SOLID Principles Applied**

### **S - Single Responsibility**
- `Topic` - Only manages message queue
- `TopicPublisher` - Only publishes to topic
- `TopicSubscriber` - Only consumes from topic
- `KafkaController` - Only orchestrates components
- Each class has one reason to change

### **O - Open/Closed**
- `IPublisher` interface - Add new publisher types without modifying framework
- `ISubscriber` interface - Add new subscriber types without modifying framework
- Easy to extend with new features (persistence, compression)

### **L - Liskov Substitution**
- Any `IPublisher` implementation can replace another
- Any `ISubscriber` implementation can replace another
- Polymorphism works correctly

### **I - Interface Segregation**
- `IPublisher` - Only `publish()` method
- `ISubscriber` - Only `consume()` method
- Clients depend on minimal interfaces

### **D - Dependency Inversion**
- `TopicPublisher` depends on `IPublisher` interface, not concrete publishers
- `TopicSubscriber` depends on `ISubscriber` interface, not concrete subscribers
- High-level modules don't depend on low-level details

---

## **🎭 Scenario Walkthrough**

### **Scenario: Publishing and Consuming Messages**

```
=== Setup Phase ===
1. Create Topic("order-events")
2. Create Publishers (OrderService, InventoryService)
3. Create Subscribers (EmailService, AnalyticsService, BillingService)
4. Register publishers and subscribers with KafkaController

=== Publishing Phase (Publisher Thread) ===
5. Publisher1 thread wakes up
   │
6. publisher.publish() → Message("order-123", "Order placed")
   │
7. topic.addMessage(msg)
   │   - Acquires synchronized lock
   │   - messageQueue.offer(msg)
   │   - Releases lock
   │
8. Sleep 100ms, repeat

=== Consumption Phase (Subscriber Thread) ===
9. Subscriber1 thread wakes up
   │
10. topic.getMessages()
   │   - Acquires synchronized lock
   │   - Drains all available messages
   │   - Releases lock
   │   - Returns [msg1, msg2, msg3]
   │
11. For each message:
   │   subscriber.consume(msg)
   │   - EmailService sends order confirmation
   │
12. Sleep 200ms (polling interval), repeat

=== Parallel Processing ===
- Publisher1, Publisher2, Publisher3 all publish concurrently
- Subscriber1, Subscriber2, Subscriber3 all consume concurrently
- Each operates independently
- No blocking between publishers and subscribers
```

### **Timeline Visualization**

```
Time  Publisher1         Publisher2         Subscriber1        Subscriber2
----  ----------------   ----------------   ----------------   ----------------
T0    Publish msg1       Publish msg2
T1    [Queue: msg1,2]    [Queue: msg1,2]
T2                                          Pull [msg1, msg2]
T3                                          Process msg1       Pull [empty]
T4    Publish msg3                          Process msg2
T5    [Queue: msg3]
T6                       Publish msg4
T7    [Queue: msg3,4]                                          Pull [msg3, msg4]
T8                                          Pull [empty]       Process msg3
T9                                                             Process msg4
```

**Key Observations:**
- Publishers never wait for subscribers
- Subscribers may get empty polls (acceptable overhead)
- Each subscriber sees all messages (broadcast)
- Slow Subscriber2 doesn't affect Subscriber1

---

## **🚀 Extensions & Enhancements**

### **1. Message Partitioning (Like Kafka)**

```java
public class PartitionedTopic {
    private List<BlockingQueue<Message>> partitions;

    public void addMessage(Message msg) {
        int partition = hash(msg.getKey()) % partitions.size();
        partitions.get(partition).offer(msg);
    }

    // Benefits:
    // - Parallel consumption (one consumer per partition)
    // - Ordering within partition
    // - Better throughput
}
```

### **2. Message Persistence**

```java
public class PersistentTopic extends Topic {
    private FileWriter log;

    @Override
    public void addMessage(Message msg) {
        log.append(msg); // Write to disk
        super.addMessage(msg); // Also keep in memory
    }

    // Benefits:
    // - Survive crashes
    // - Replay messages
    // - Audit trail
}
```

### **3. Consumer Groups**

```java
public class ConsumerGroup {
    private List<TopicSubscriber> consumers;

    // Round-robin message distribution
    public void distribute(Message msg) {
        int idx = msgCount++ % consumers.size();
        consumers.get(idx).addMessage(msg);
    }

    // Benefits:
    // - Load balancing
    // - Parallel processing
    // - Each message consumed once per group
}
```

### **4. Message Acknowledgment**

```java
public class ReliableSubscriber {
    public void consume(Message msg) {
        try {
            process(msg);
            topic.ack(msg.getId()); // Success
        } catch (Exception e) {
            topic.nack(msg.getId()); // Retry later
        }
    }
}
```

### **5. Dead Letter Queue (DLQ)**

```java
public class TopicWithDLQ {
    private Topic mainTopic;
    private Topic deadLetterQueue;
    private Map<String, Integer> retryCount;

    public void handleFailure(Message msg) {
        int count = retryCount.getOrDefault(msg.getId(), 0);
        if (count >= MAX_RETRIES) {
            deadLetterQueue.addMessage(msg); // Give up
        } else {
            retryCount.put(msg.getId(), count + 1);
            mainTopic.addMessage(msg); // Retry
        }
    }
}
```

---

## **🎯 Interview Questions & Answers**

### **Q1: How do you guarantee message ordering?**

**Answer:**
```
Current Implementation:
- FIFO ordering within a single topic (BlockingQueue)
- No ordering guarantee across topics

Production Approach:
1. Partition by key (e.g., user_id, order_id)
2. All messages with same key go to same partition
3. Single consumer per partition maintains order
4. Trade-off: Ordering per partition, not globally

Example:
Topic: "user-events" with 3 partitions
Messages for user_123 → always Partition 2
Messages consumed in order for user_123
Different users may be in different partitions (parallel processing)
```

### **Q2: At-least-once vs Exactly-once delivery?**

**Answer:**
```
At-Least-Once (Our implementation):
- Message delivered 1+ times
- Happens when: subscriber crashes after processing but before ack
- Solution: Idempotent consumers (safe to process twice)

Exactly-Once (Complex!):
- Message processed exactly once
- Requires:
  1. Transactional writes (database + message queue)
  2. Deduplication (track message IDs)
  3. Distributed transactions (2-phase commit)

Trade-off:
- At-least-once: Simple, fast, requires idempotency
- Exactly-once: Complex, slow, but guarantees no duplicates

Kafka approach: Exactly-once within Kafka (using transactions)
```

### **Q3: How to handle backpressure?**

**Answer:**
```
Problem: Publishers producing faster than subscribers can consume

Solutions:
1. Bounded Queue (Our approach):
   BlockingQueue<Message> queue = new ArrayBlockingQueue<>(1000);
   - Queue fills up → publishers block or drop messages

2. Rate Limiting:
   RateLimiter limiter = RateLimiter.create(100); // 100 msgs/sec
   limiter.acquire();
   publisher.publish();

3. Dynamic Batching:
   - Subscriber processes larger batches when queue grows
   - Reduces per-message overhead

4. Priority Consumers:
   - Critical subscribers get higher priority
   - Less important subscribers lag

5. Horizontal Scaling:
   - Add more subscriber instances
   - Use consumer groups for load balancing

Production: Combination of all approaches
```

### **Q4: What about consumer lag?**

**Answer:**
```
Consumer Lag: Gap between latest message and consumer position

Monitoring:
class ConsumerLagMonitor {
    long getlag(Topic topic, Subscriber s) {
        return topic.getLatestOffset() - s.getCurrentOffset();
    }
}

Causes:
1. Slow processing (complex logic, DB calls)
2. Insufficient consumers
3. Unbalanced partitions

Solutions:
1. Scale Out:
   - Add more consumer instances
   - Use consumer groups

2. Optimize Processing:
   - Batch processing
   - Async I/O
   - Reduce per-message work

3. Prioritize:
   - Process critical messages first
   - Drop or sample non-critical messages

4. Rebalance:
   - Redistribute partitions across consumers
   - Ensure even load distribution

Alert if lag > threshold (e.g., 1 million messages or 1 hour)
```

### **Q5: How to make this distributed?**

**Answer:**
```
Single-Machine → Distributed Pub-Sub:

1. Topic Sharding:
   - Divide topic into partitions
   - Distribute partitions across servers
   - Partition by key for ordering

2. Broker Cluster:
   - Multiple broker servers
   - Leader-follower replication
   - ZooKeeper/Raft for coordination

3. Producer Changes:
   - Knows all brokers
   - Routes messages to correct partition
   - Handles broker failures

4. Consumer Changes:
   - Tracks offset per partition
   - Rebalancing when consumers join/leave
   - Heartbeat mechanism

5. Consistency:
   - Replicate messages (3x typically)
   - Leader handles writes
   - Followers sync asynchronously
   - ISR (In-Sync Replicas) for reliability

Architecture:
┌──────────┐     ┌──────────┐     ┌──────────┐
│ Broker 1 │────►│ Broker 2 │────►│ Broker 3 │
│ Leader   │     │ Follower │     │ Follower │
│ Part 0,1 │     │ Part 0,1 │     │ Part 0,1 │
└──────────┘     └──────────┘     └──────────┘
     ▲
     │
┌─────────────┐
│ ZooKeeper   │  (Leader election,
│ Cluster     │   metadata, coordination)
└─────────────┘
```

### **Q6: Kafka vs RabbitMQ - When to use each?**

**Answer:**
```
Kafka (Our design is Kafka-like):
✅ Use when:
  - High throughput (millions of msgs/sec)
  - Message replay needed (stored on disk)
  - Event sourcing / stream processing
  - Multiple consumers need all messages
  - Ordering important

❌ Avoid when:
  - Need complex routing (topic exchanges)
  - Require strict message priorities
  - Small scale (<10k msgs/sec)

RabbitMQ:
✅ Use when:
  - Complex routing (topic, fanout, direct)
  - Task queue (work distribution)
  - Priority queues needed
  - Request-response patterns
  - Strict delivery guarantees

❌ Avoid when:
  - Need extreme throughput
  - Want long-term message storage
  - Stream processing workloads

Examples:
- Kafka: User activity logs, metrics, event streams
- RabbitMQ: Task queues, RPC, job processing
```

### **Q7: How to handle poison messages?**

**Answer:**
```
Poison Message: Message that always fails processing

Detection:
class PoisonMessageDetector {
    Map<String, Integer> failureCount = new HashMap<>();

    void onFailure(Message msg) {
        int count = failureCount.merge(msg.getId(), 1, Integer::sum);
        if (count >= MAX_RETRIES) {
            handlePoison(msg);
        }
    }
}

Handling Strategies:
1. Dead Letter Queue:
   - Move to separate DLQ topic
   - Human intervention or separate process

2. Skip & Log:
   - Log error details
   - Move to next message
   - Alert operations team

3. Quarantine:
   - Store in database for later analysis
   - Include full stack trace

4. Circuit Breaker:
   - If too many failures, pause consumer
   - Alert and investigate

5. Schema Validation:
   - Validate before processing
   - Reject invalid messages early

Best Practice: DLQ + Monitoring + Alerts
```

### **Q8: What about message duplication?**

**Answer:**
```
Causes:
1. Network retry (publisher thinks failed, but succeeded)
2. Consumer crash after processing, before ack
3. Rebalancing during processing

Solutions:
1. Idempotent Consumers (Best):
   class IdempotentConsumer {
       Set<String> processed = new HashSet<>();

       void consume(Message msg) {
           if (processed.contains(msg.getId())) {
               return; // Already processed
           }
           process(msg);
           processed.add(msg.getId());
       }
   }

2. Database Constraints:
   INSERT INTO events (msg_id, data) VALUES (?, ?)
   ON CONFLICT (msg_id) DO NOTHING
   // Unique constraint prevents duplicates

3. Message Deduplication Window:
   - Keep last N message IDs in memory
   - Check before processing
   - Trade-off: Memory vs false negatives

4. Transactional Processing:
   BEGIN TRANSACTION
     - Process message
     - Mark as processed
   COMMIT
   // Atomic: either both or neither

Best Practice: Design for idempotency at application level
```

### **Q9: How would you test this system?**

**Answer:**
```
Unit Tests:
1. Topic Operations:
   - Add/retrieve messages
   - Concurrent access
   - Queue capacity limits

2. Publisher/Subscriber:
   - Message flow
   - Error handling
   - Lifecycle (start/stop)

Integration Tests:
1. End-to-End Flow:
   @Test
   void testPublishConsume() {
       // Publish 100 messages
       // Verify all received by subscribers
       // Check ordering
   }

2. Concurrent Scenarios:
   - Multiple publishers
   - Multiple subscribers
   - Race conditions

3. Failure Scenarios:
   - Subscriber crashes
   - Publisher crashes
   - Queue full

Performance Tests:
1. Throughput:
   - Measure msgs/second
   - Vary publisher/subscriber count

2. Latency:
   - Publish to consume time
   - P50, P95, P99 percentiles

3. Load Testing:
   - Sustained high load
   - Burst traffic
   - Memory usage under load

Chaos Engineering:
- Random subscriber failures
- Network delays
- Slow consumers
- Measure recovery time

Monitoring:
- Message count per topic
- Consumer lag
- Error rates
- Throughput graphs
```

### **Q10: How to ensure high availability?**

**Answer:**
```
Current: Single-machine, no HA

Production HA Architecture:
1. Broker Replication:
   - 3+ broker instances
   - Replicate each partition 3x
   - Leader + 2 followers

2. Leader Election:
   - If leader fails, elect new leader
   - Use consensus (Raft/Paxos)
   - Sub-second failover

3. Client Resilience:
   - Retry failed requests
   - Connection pool to multiple brokers
   - Circuit breaker pattern

4. Data Persistence:
   - Write to disk (not just memory)
   - fsync for durability
   - Replicate before ack

5. Monitoring:
   - Health checks every 5 seconds
   - Alert if broker down
   - Auto-scaling consumers

6. Graceful Degradation:
   - Read from followers if leader slow
   - Drop non-critical messages if queue full
   - Degrade rather than fail completely

Kafka HA Setup:
- 3 ZooKeeper nodes (quorum)
- 3 Kafka brokers (replication factor 3)
- Min in-sync replicas = 2
- Clients connect to all brokers
- Automatic rebalancing

SLA: 99.99% uptime (52 minutes downtime/year)
```

### **Q11: What about message priority?**

**Answer:**
```
Current: FIFO (no priority)

Adding Priority:
1. Multiple Queues Approach:
   class PriorityTopic {
       BlockingQueue<Message> high;
       BlockingQueue<Message> medium;
       BlockingQueue<Message> low;

       Message poll() {
           Message msg = high.poll();
           if (msg != null) return msg;

           msg = medium.poll();
           if (msg != null) return msg;

           return low.poll();
       }
   }

2. PriorityBlockingQueue:
   BlockingQueue<Message> queue = new PriorityBlockingQueue<>(
       1000,
       Comparator.comparing(Message::getPriority)
   );
   // Messages consumed in priority order

3. Separate Topics:
   Topic criticalTopic; // Process first
   Topic normalTopic;   // Process second
   Topic lowTopic;      // Process if time permits

Trade-offs:
✅ Important messages processed first
❌ Starvation of low-priority messages
❌ Head-of-line blocking
❌ More complex

Best Practice:
- Use separate topics for truly critical messages
- Avoid priority queues unless necessary
- Can break ordering guarantees
```

### **Q12: How to handle schema evolution?**

**Answer:**
```
Problem: Message format changes over time

Solutions:
1. Versioned Messages:
   class Message {
       int version;
       String payload; // JSON/Protobuf
   }

   Subscriber:
   switch (msg.version) {
       case 1: parseV1(msg); break;
       case 2: parseV2(msg); break;
   }

2. Schema Registry:
   - Central repository of schemas
   - Publishers register new schemas
   - Subscribers fetch schema by ID
   - Enforce compatibility rules

3. Backward Compatible:
   // V1
   {\"userId\": 123, \"action\": \"click\"}

   // V2 - Add optional field
   {\"userId\": 123, \"action\": \"click\", \"timestamp\": 1234567890}

   // Old subscribers still work!

4. Protocol Buffers:
   - Built-in versioning
   - Forward/backward compatible
   - Compact binary format

Best Practice:
- Schema Registry (Confluent Schema Registry)
- Avro/Protobuf for serialization
- Compatibility checks on publish
- Version messages explicitly
```

---

## **⚠️ Known Limitations & Trade-offs**

### **1. No Persistence**
- **Current:** In-memory only (messages lost on crash)
- **Fix:** Add write-ahead log to disk before acking
- **Trade-off:** Durability vs throughput

### **2. No Partitioning**
- **Current:** Single queue per topic (scaling bottleneck)
- **Fix:** Partition topics, multiple queues
- **Trade-off:** Ordering within partition, not globally

### **3. Pull-Based (Polling Overhead)**
- **Current:** Subscribers poll, may waste cycles
- **Fix:** Long polling or push with backpressure
- **Trade-off:** Complexity vs efficiency

### **4. Global Lock on Topic**
- **Current:** `synchronized` methods (contention)
- **Fix:** Fine-grained locking or lock-free structures
- **Trade-off:** Simplicity vs scalability

### **5. No Message Expiry (TTL)**
- **Current:** Messages never expire
- **Fix:** Add timestamp and reaper thread
- **Trade-off:** Memory usage vs complexity

### **6. No Consumer Groups**
- **Current:** All subscribers get all messages
- **Fix:** Add consumer groups for load balancing
- **Trade-off:** Broadcast vs work distribution

---

## **📚 Key Takeaways**

**Design Patterns Used:**
- ✅ Producer-Consumer Pattern (core architecture)
- ✅ Observer Pattern (topic-subscriber relationship)
- ✅ Thread Pool Pattern (resource management)
- ✅ Blocking Queue Pattern (thread-safe coordination)

**Concurrency Techniques:**
- ✅ ExecutorService for thread pools
- ✅ BlockingQueue for coordination
- ✅ Synchronized methods for atomicity
- ✅ Independent threads for decoupling

**Key Innovations:**
- ✅ Asynchronous processing (non-blocking)
- ✅ Subscriber independence (slow doesn't affect others)
- ✅ Natural backpressure (pull model)
- ✅ Broadcast to all subscribers

**Interview Focus Points:**
- Pull vs Push model
- At-least-once vs exactly-once
- Partitioning for scalability
- Ordering guarantees
- Distributed pub-sub architecture
- Comparison with real systems (Kafka, RabbitMQ)

---

## **🎓 What You Should Master**

Before interview, ensure you can:

1. ✅ Explain Producer-Consumer pattern with code
2. ✅ Draw the complete architecture diagram
3. ✅ Discuss pull vs push trade-offs
4. ✅ Explain how Kafka uses partitions
5. ✅ Describe at-least-once vs exactly-once
6. ✅ Handle backpressure questions
7. ✅ Design for distributed deployment
8. ✅ Compare Kafka vs RabbitMQ
9. ✅ Discuss ordering guarantees
10. ✅ Explain consumer lag and solutions
11. ✅ Propose extensions (DLQ, consumer groups, persistence)
12. ✅ Answer all 12 Q&A sections confidently

**Practice Exercises:**
1. Add message partitioning (30 min)
2. Implement consumer groups (45 min)
3. Add persistence layer (60 min)
4. Implement exactly-once delivery (90 min)

**Time to master:** 4-6 hours of practice

**Difficulty:** ⭐⭐⭐⭐⭐ (Hard - Staff+ level interview)

**Real-World Usage:** Understanding this design is essential for:
- Apache Kafka
- AWS Kinesis
- Google Pub/Sub
- RabbitMQ
- Any event-driven architecture

**Pro Tip:** In interviews, start with the simple design (our implementation), then progressively add complexity based on requirements. Don't jump to Kafka-level complexity immediately!
