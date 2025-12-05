package Tasks;

import CommonEnum.TaskType;

public class RecurringTask extends Task {
    private final Runnable action;
    private final long intervalMs;
    private int executionCount = 0;

    public RecurringTask(String id, String name, long firstExecutionTimeMs, long intervalMs, Runnable action) {
        super(id, name, firstExecutionTimeMs, TaskType.RECURRING);
        this.intervalMs = intervalMs;
        this.action = action;
    }

    @Override
    public void execute() {
        executionCount++;
        System.out.println("[" + System.currentTimeMillis() + "] Executing RECURRING task: " + getName() + " (execution #" + executionCount + ")");
        action.run();
    }

    // Reschedule for next execution
    public void reschedule() {
        this.executeTimeMs = System.currentTimeMillis() + intervalMs;
    }

    public long getIntervalMs() {
        return intervalMs;
    }

    public int getExecutionCount() {
        return executionCount;
    }
}
