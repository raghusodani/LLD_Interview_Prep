package UtilityClasses;

/**
 * Represents a building with floors and an elevator system.
 */
public class Building {
    private String name; // Name of the building
    private int numberOfFloors; // Total number of floors in the building
    private ElevatorController
            elevatorController; // Controller to manage all elevators in the building

    /**
     * Constructor to initialize the building's details and its elevator system.
     * @param name The name of the building.
     * @param numberOfFloors The number of floors in the building.
     * @param numberOfElevators The number of elevators in the building.
     */
    public Building(String name, int numberOfFloors, int numberOfElevators) {
        this.name = name; // Assign the building's name
        this.numberOfFloors = numberOfFloors; // Set the total number of floors
        // Initialize the elevator controller with the specified number of elevators
        // and floors
        this.elevatorController =
                new ElevatorController(numberOfElevators, numberOfFloors);
    }

    /**
     * Gets the name of the building.
     * @return The name of the building.
     */
    public String getName() {
        return name;
    }

    /**
     * Gets the number of floors in the building.
     * @return The number of floors in the building.
     */
    public int getNumberOfFloors() {
        return numberOfFloors;
    }

    /**
     * Gets the elevator controller for the building.
     * @return The elevator controller.
     */
    public ElevatorController getElevatorController() {
        return elevatorController;
    }
}
