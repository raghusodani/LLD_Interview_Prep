# Design In-Memory Job Scheduler ⏰

## **Quick Start**

```bash
cd src
javac Main.java
java Main
```

## **What This Problem Teaches**

✅ **DelayQueue** - Efficient scheduling without polling
✅ **Command Pattern** - Task as first-class object
✅ **Template Method** - Common workflow in base class
✅ **Observer Pattern** - Status change notifications
✅ **Thread Pools** - Concurrent task execution
✅ **Recurring Tasks** - Auto-rescheduling mechanism

## **Key Features**

- ⏰ Schedule one-time tasks at specific times
- 🔄 Schedule recurring tasks with intervals
- 🧵 Concurrent execution with thread pool
- 📊 Task status tracking (Scheduled → Running → Completed)
- ❌ Cancel tasks before execution
- 🔔 Observer notifications on status changes

## **Architecture**

```
┌──────────────────┐
│   JobScheduler   │
└─────────┬────────┘
          │
    ┌─────┴─────────────────┐
    │                       │
┌───▼──────────┐    ┌──────▼────────┐
│ DelayQueue   │    │  TaskExecutor │
│ (No polling!)│    │  (Thread Pool)│
└───┬──────────┘    └───────────────┘
    │
┌───▼──────────┐
│SchedulerThread│ (Blocks on take())
└──────────────┘
```

## **Design Patterns**

1. **Command Pattern** - Task encapsulation
2. **Template Method** - Task.run() workflow
3. **Observer Pattern** - Status notifications
4. **Strategy Pattern** - OneTimeTask vs RecurringTask

## **Demo Output**

```
✅ Job Scheduler started

📅 Task scheduled: Send Welcome Email at 09:26:18
📅 Task scheduled: Generate Daily Report at 09:26:20
📅 Task scheduled: Cleanup Temp Files at 09:26:19

⏰ Task ready for execution: Send Welcome Email
💌 Executing: Sending welcome email to user@example.com
✓ Email sent successfully!

🔄 Recurring task rescheduled: Health Check for 09:26:24
💚 Executing: System health check
   CPU: 45%, Memory: 60%, Disk: 75% - All systems operational ✓
```

## **Key Concepts**

### **Why DelayQueue?**
- ✅ NO polling (blocks until task ready)
- ✅ wait/notify mechanism
- ✅ Automatic ordering by execution time
- ✅ Thread-safe by design

### **Recurring Tasks**
- Execute → Check if should reschedule → Calculate next time → Re-enqueue
- Supports max execution count
- Automatic rescheduling

### **Concurrency**
- Separate scheduler thread (dispatch) and worker threads (execute)
- Thread pool prevents resource exhaustion
- ConcurrentHashMap for thread-safe task tracking

## **Read SOLUTION.md**

Comprehensive 29K character guide covering:
- DelayQueue internals
- Distributed scheduling strategies
- Retry logic with exponential backoff
- Task dependencies
- Production considerations
- 10 interview Q&A

**Difficulty:** ⭐⭐⭐⭐ (Advanced Concurrency)
**Time to Master:** 6-8 hours
**Interview Frequency:** ⭐⭐⭐ (Common)
