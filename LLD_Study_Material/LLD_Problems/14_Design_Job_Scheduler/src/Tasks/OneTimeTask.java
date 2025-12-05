package Tasks;

import CommonEnum.TaskType;

public class OneTimeTask extends Task {
    private final Runnable action;

    public OneTimeTask(String id, String name, long executeTimeMs, Runnable action) {
        super(id, name, executeTimeMs, TaskType.ONE_TIME);
        this.action = action;
    }

    @Override
    public void execute() {
        System.out.println("[" + System.currentTimeMillis() + "] Executing ONE_TIME task: " + getName());
        action.run();
    }
}
