package CommandPatternIMP.ConcreteClasses;

import CommandPatternIMP.ElevatorCommand;
import CommonEnums.Direction;
import UtilityClasses.ElevatorController;

/**
 * Represents a request for an elevator, which can be either internal (from inside the elevator) or external (from a floor).
 */
public class ElevatorRequest implements ElevatorCommand {
    private int elevatorId; // ID of the elevator involved in the request
    private int floor; // Floor where the request is made
    private Direction requestDirection; // The direction of the elevator request
    private ElevatorController controller; // Reference to the ElevatorController to handle the request
    private boolean isInternalRequest; // Distinguishes internal vs external requests

    /**
     * Constructor to initialize the elevator request.
     * @param elevatorId The ID of the elevator.
     * @param floor The floor of the request.
     * @param isInternalRequest Whether the request is internal or not.
     * @param direction The direction of the request.
     */
    public ElevatorRequest(int elevatorId, int floor, boolean isInternalRequest,
                           Direction direction) {
        this.elevatorId = elevatorId;
        this.floor = floor;
        this.isInternalRequest = isInternalRequest;
        this.requestDirection = direction;
        this.controller = new ElevatorController();
    }

    /**
     * Execute method to process the request via the controller.
     */
    @Override
    public void execute() {
        if (isInternalRequest)
            controller.requestFloor(elevatorId, floor);
        else
            controller.requestElevator(elevatorId, floor, requestDirection);
    }

    /**
     * Getters and Setters for the ElevatorRequest.
     * @return The direction of the request.
     */
    public Direction getDirection() {
        return requestDirection;
    }

    /**
     * Gets the floor of the request.
     * @return The floor of the request.
     */
    public int getFloor() {
        return floor;
    }

    /**
     * Checks if the request is internal.
     * @return true if the request is internal, false otherwise.
     */
    public boolean checkIsInternalRequest(){
        return isInternalRequest;
    }
}
