package Scheduler;

import Tasks.Task;
import Executors.TaskExecutor;
import java.util.concurrent.*;

public class JobScheduler {
    private final DelayQueue<Task> taskQueue;
    private final ExecutorService executorService;
    private final Thread schedulerThread;
    private volatile boolean running;

    public JobScheduler(int numWorkerThreads) {
        this.taskQueue = new DelayQueue<>();
        this.executorService = Executors.newFixedThreadPool(numWorkerThreads);
        this.running = true;
        this.schedulerThread = new Thread(this::run, "SchedulerThread");
    }

    public void start() {
        schedulerThread.start();
        System.out.println("✅ Job Scheduler started with DelayQueue and worker threads");
    }

    public void scheduleTask(Task task) {
        taskQueue.offer(task);
        System.out.println("📅 Scheduled: " + task);
    }

    private void run() {
        while (running) {
            try {
                // Blocks until a task is ready (no polling!)
                Task task = taskQueue.take();

                // Submit to thread pool for execution
                executorService.submit(new TaskExecutor(task, this));

            } catch (InterruptedException e) {
                if (!running) {
                    break;
                }
                Thread.currentThread().interrupt();
            }
        }
    }

    public void shutdown() {
        System.out.println("\n🛑 Shutting down scheduler...");
        running = false;
        schedulerThread.interrupt();
        executorService.shutdown();
        try {
            if (!executorService.awaitTermination(5, TimeUnit.SECONDS)) {
                executorService.shutdownNow();
            }
        } catch (InterruptedException e) {
            executorService.shutdownNow();
        }
        System.out.println("✅ Scheduler shut down gracefully");
    }

    public int getPendingTaskCount() {
        return taskQueue.size();
    }
}
