package Executors;

import Tasks.Task;
import Tasks.RecurringTask;
import Scheduler.JobScheduler;

public class TaskExecutor implements Runnable {
    private final Task task;
    private final JobScheduler scheduler;

    public TaskExecutor(Task task, JobScheduler scheduler) {
        this.task = task;
        this.scheduler = scheduler;
    }

    @Override
    public void run() {
        try {
            // Execute the task
            task.run();

            // If recurring, reschedule it
            if (task instanceof RecurringTask) {
                RecurringTask recurringTask = (RecurringTask) task;
                recurringTask.reschedule();
                scheduler.scheduleTask(recurringTask);
                System.out.println("  → Rescheduled " + task.getName() + " for " +
                    (recurringTask.getIntervalMs() / 1000) + "s later");
            }
        } catch (Exception e) {
            System.err.println("Error executing task " + task.getId() + ": " + e.getMessage());
            e.printStackTrace();
        }
    }
}
