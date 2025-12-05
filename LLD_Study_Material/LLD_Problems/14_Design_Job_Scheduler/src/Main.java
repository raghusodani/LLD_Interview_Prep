import Scheduler.JobScheduler;
import Tasks.*;

public class Main {
    public static void main(String[] args) throws InterruptedException {
        System.out.println("═══════════════════════════════════════════════════════");
        System.out.println("     IN-MEMORY JOB SCHEDULER - DelayQueue Demo");
        System.out.println("═══════════════════════════════════════════════════════\n");

        // Create scheduler with 3 worker threads
        JobScheduler scheduler = new JobScheduler(3);
        scheduler.start();

        long now = System.currentTimeMillis();

        System.out.println("\n📋 SCENARIO 1: One-Time Tasks");
        System.out.println("─────────────────────────────────────────────────────\n");

        // One-time task in 2 seconds
        Task task1 = new OneTimeTask(
            "T1",
            "Send Email",
            now + 2000,
            () -> System.out.println("  ✉️  Email sent successfully!")
        );
        scheduler.scheduleTask(task1);

        // One-time task in 5 seconds
        Task task2 = new OneTimeTask(
            "T2",
            "Generate Report",
            now + 5000,
            () -> System.out.println("  📊 Report generated!")
        );
        scheduler.scheduleTask(task2);

        // One-time task in 10 seconds
        Task task3 = new OneTimeTask(
            "T3",
            "Cleanup Database",
            now + 10000,
            () -> System.out.println("  🗑️  Database cleaned!")
        );
        scheduler.scheduleTask(task3);

        System.out.println("\n📋 SCENARIO 2: Recurring Tasks");
        System.out.println("─────────────────────────────────────────────────────\n");

        // Recurring task every 3 seconds
        RecurringTask task4 = new RecurringTask(
            "T4",
            "Health Check",
            now + 3000,  // First execution in 3 seconds
            3000,        // Repeat every 3 seconds
            () -> System.out.println("  💚 System healthy!")
        );
        scheduler.scheduleTask(task4);

        // Recurring task every 4 seconds
        RecurringTask task5 = new RecurringTask(
            "T5",
            "Sync Data",
            now + 4000,  // First execution in 4 seconds
            4000,        // Repeat every 4 seconds
            () -> System.out.println("  🔄 Data synced!")
        );
        scheduler.scheduleTask(task5);

        System.out.println("\n⏰ Waiting for tasks to execute...");
        System.out.println("   (Watch them execute at scheduled times - NO POLLING!)\n");
        System.out.println("═══════════════════════════════════════════════════════\n");

        // Let tasks run for 15 seconds to see recurring tasks execute multiple times
        Thread.sleep(15000);

        // Shutdown
        scheduler.shutdown();

        System.out.println("\n═══════════════════════════════════════════════════════");
        System.out.println("📊 KEY OBSERVATIONS:");
        System.out.println("═══════════════════════════════════════════════════════");
        System.out.println("✅ DelayQueue.take() blocks efficiently (no polling!)");
        System.out.println("✅ Tasks execute at exact scheduled times");
        System.out.println("✅ Recurring tasks auto-reschedule after execution");
        System.out.println("✅ Thread pool handles concurrent execution");
        System.out.println("✅ Timestamps show precise execution timing");
        System.out.println("═══════════════════════════════════════════════════════\n");
    }
}
