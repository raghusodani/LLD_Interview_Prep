package Tasks;

import CommonEnum.TaskStatus;
import CommonEnum.TaskType;
import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;

public abstract class Task implements Delayed, Runnable {
    private final String id;
    private final String name;
    protected long executeTimeMs; // When to execute (absolute time)
    private final TaskType type;
    private TaskStatus status;

    public Task(String id, String name, long executeTimeMs, TaskType type) {
        this.id = id;
        this.name = name;
        this.executeTimeMs = executeTimeMs;
        this.type = type;
        this.status = TaskStatus.PENDING;
    }

    @Override
    public long getDelay(TimeUnit unit) {
        long delay = executeTimeMs - System.currentTimeMillis();
        return unit.convert(delay, TimeUnit.MILLISECONDS);
    }

    @Override
    public int compareTo(Delayed o) {
        if (this.executeTimeMs < ((Task) o).executeTimeMs) {
            return -1;
        }
        if (this.executeTimeMs > ((Task) o).executeTimeMs) {
            return 1;
        }
        return 0;
    }

    // Abstract method for task execution logic
    public abstract void execute();

    // Template method - final to ensure status management
    @Override
    public final void run() {
        try {
            this.status = TaskStatus.RUNNING;
            execute();
            this.status = TaskStatus.COMPLETED;
        } catch (Exception e) {
            this.status = TaskStatus.FAILED;
            System.err.println("Task " + id + " failed: " + e.getMessage());
        }
    }

    // Getters
    public String getId() { return id; }
    public String getName() { return name; }
    public long getExecuteTimeMs() { return executeTimeMs; }
    public TaskType getType() { return type; }
    public TaskStatus getStatus() { return status; }

    public void setStatus(TaskStatus status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return String.format("Task[id=%s, name=%s, type=%s, status=%s]",
            id, name, type, status);
    }
}
