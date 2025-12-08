package CommonEnums;

/**
 * Represents the state of the elevator.
 */
public enum ElevatorState {
    /**
     * The elevator is not moving, waiting for requests.
     */
    IDLE,
    /**
     * The elevator is in motion (either up or down).
     */
    MOVING,
    /**
     * The elevator has temporarily stopped (e.g., at a floor).
     */
    STOPPED,
    /**
     * The elevator is out of service and undergoing maintenance.
     */
    MAINTENANCE
}
