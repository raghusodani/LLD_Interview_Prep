package ObserverPatternIMP;

import CommonEnums.ElevatorState;
import UtilityClasses.Elevator;

/**
 * Interface for an observer that monitors elevator events.
 */
public interface ElevatorObserver {
    /**
     * Called when an elevator's state changes.
     * @param elevator The elevator whose state has changed.
     * @param state The new state of the elevator.
     */
    void onElevatorStateChange(Elevator elevator, ElevatorState state);

    /**
     * Called when an elevator changes its current floor.
     * @param elevator The elevator whose floor has changed.
     * @param floor The new floor of the elevator.
     */
    void onElevatorFloorChange(Elevator elevator, int floor);
}
