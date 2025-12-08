package ObserverPatternIMP;

import CommonEnums.ElevatorState;
import UtilityClasses.Elevator;

/**
 * Represents a display that observes and shows the status of an elevator.
 */
public class ElevatorDisplay implements ElevatorObserver{
    /**
     * Called when the state of an elevator changes.
     * @param elevator The elevator whose state has changed.
     * @param state The new state of the elevator.
     */
    @Override
    public void onElevatorStateChange(Elevator elevator, ElevatorState state) {
        // Display the new state of the elevator
        System.out.println("Elevator " + elevator.getId() + " state changed to " + state);
    }

    /**
     * Called when the floor of an elevator changes.
     * @param elevator The elevator whose floor has changed.
     * @param floor The new floor of the elevator.
     */
    @Override
    public void onElevatorFloorChange(Elevator elevator, int floor) {
        // Display the elevator's movement to a new floor
        System.out.println("Elevator " + elevator.getId() + " moved to floor " + floor);
    }
}
