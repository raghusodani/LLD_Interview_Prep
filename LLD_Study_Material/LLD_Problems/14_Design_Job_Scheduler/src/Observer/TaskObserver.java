package Observer;

import Tasks.Task;
import CommonEnum.TaskStatus;

public interface TaskObserver {
    void onTaskStatusChanged(Task task, TaskStatus oldStatus, TaskStatus newStatus);
}
