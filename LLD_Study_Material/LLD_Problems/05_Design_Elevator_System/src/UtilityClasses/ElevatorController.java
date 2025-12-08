package UtilityClasses;

import CommandPatternIMP.ConcreteClasses.ElevatorRequest;
import CommonEnums.Direction;
import SchedulingAlgoStrategyPattern.ConcreteStrategies.ScanSchedulingStrategy;
import SchedulingAlgoStrategyPattern.SchedulingStrategy;

import java.util.ArrayList;
import java.util.List;

/**
 * Controls the elevators in the building.
 */
public class ElevatorController {
    // List of all elevators in the system
    private List<Elevator> elevators;
    // List of all floors in the building
    private List<Floor> floors;
    // Strategy to determine the scheduling of elevators
    private SchedulingStrategy schedulingStrategy;
    // ID of the current elevator (used for internal operations)
    private int currentElevatorId;

    /**
     * Default constructor.
     */
    public ElevatorController(){

    }

    /**
     * Constructor to initialize elevators and floors.
     * @param numberOfElevators The number of elevators in the building.
     * @param numberOfFloors The number of floors in the building.
     */
    public ElevatorController(int numberOfElevators, int numberOfFloors) {
        this.elevators = new ArrayList<>();
        this.floors = new ArrayList<>();
        this.schedulingStrategy = new ScanSchedulingStrategy(); // Default strategy
        // Initialize elevators with unique IDs
        for (int i = 1; i <= numberOfElevators; i++) {
            elevators.add(new Elevator(i));
        }
        // Initialize floors
        for (int i = 1; i <= numberOfFloors; i++) {
            floors.add(new Floor(i));
        }
    }

    /**
     * Set the scheduling strategy dynamically.
     * @param strategy The scheduling strategy to use.
     */
    public void setSchedulingStrategy(SchedulingStrategy strategy) {
        this.schedulingStrategy = strategy;
    }

    /**
     * Handle external elevator requests from a specific floor.
     * @param elevatorId The ID of the elevator being requested.
     * @param floorNumber The floor where the request is made.
     * @param direction The direction of the request.
     */
    public void requestElevator(int elevatorId, int floorNumber, Direction direction) {
        System.out.println(
                "External request: Floor " + floorNumber + ", Direction " + direction);
        // Find the elevator by its ID
        Elevator selectedElevator = getElevatorById(elevatorId);
        if (selectedElevator != null) {
            // Add the request to the selected elevator
            selectedElevator.addRequest(
                    new ElevatorRequest(elevatorId, floorNumber, false, direction));
            System.out.println("Assigned elevator " + selectedElevator.getId()
                    + " to floor " + floorNumber);
        } else {
            // If no suitable elevator is found
            System.out.println("No elevator available for floor " + floorNumber);
        }
    }

    /**
     * Handle internal elevator requests to a specific floor.
     * @param elevatorId The ID of the elevator.
     * @param floorNumber The destination floor.
     */
    public void requestFloor(int elevatorId, int floorNumber) {
        // Find the elevator by its ID
        Elevator elevator = getElevatorById(elevatorId);
        System.out.println("Internal request: Elevator " + elevator.getId()
                + " to floor " + floorNumber);
        // Determine the direction of the request
        Direction direction = floorNumber > elevator.getCurrentFloor()
                ? Direction.UP
                : Direction.DOWN;
        // Add the request to the elevator
        elevator.addRequest(
                new ElevatorRequest(elevatorId, floorNumber, true, direction));
    }

    /**
     * Find an elevator by its ID.
     * @param elevatorId The ID of the elevator to find.
     * @return The elevator with the given ID, or null if not found.
     */
    private Elevator getElevatorById(int elevatorId) {
        for (Elevator elevator : elevators) {
            if (elevator.getId() == elevatorId)
                return elevator;
        }
        return null; // Return null if no matching elevator is found
    }

    /**
     * Perform a simulation step by moving all elevators.
     */
    public void step() {
        // Iterate through all elevators
        for (Elevator elevator : elevators) {
            // Only process elevators with pending requests
            if (!elevator.getRequestsQueue().isEmpty()) {
                // Use the scheduling strategy to find the next stop
                int nextStop = schedulingStrategy.getNextStop(elevator);


                // Move the elevator to the next stop if needed
                if (elevator.getCurrentFloor() != nextStop)
                    elevator.moveToNextStop(nextStop);
            }
        }
    }

    /**
     * Get the list of all elevators.
     * @return The list of all elevators.
     */
    public List<Elevator> getElevators() {
        return elevators;
    }

    /**
     * Get the list of all floors.
     * @return The list of all floors.
     */
    public List<Floor> getFloors() {
        return floors;
    }

    /**
     * Set the ID of the current elevator.
     * @param elevatorId The ID of the current elevator.
     */
    public void setCurrentElevator(int elevatorId) {
        this.currentElevatorId = elevatorId;
    }
}
