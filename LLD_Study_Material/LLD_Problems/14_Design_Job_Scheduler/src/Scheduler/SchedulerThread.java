package Scheduler;

import Tasks.Task;

public class SchedulerThread extends Thread {
    private JobScheduler scheduler;

    public SchedulerThread(JobScheduler scheduler) {
        this.scheduler = scheduler;
        setDaemon(true);
        setName("SchedulerThread");
    }

    @Override
    public void run() {
        while (scheduler.isRunning() && !Thread.currentThread().isInterrupted()) {
            try {
                // DelayQueue.take() blocks until a task is ready
                // This is efficient - NO POLLING!
                Task task = scheduler.getTaskQueue().take();

                System.out.println("⏰ Task ready for execution: " + task.getTaskName());
                scheduler.executeTask(task);

            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                System.out.println("SchedulerThread interrupted");
                break;
            }
        }
    }
}
