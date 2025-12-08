package UtilityClasses;

/**
 * Represents a floor in the building.
 */
public class Floor {
    private int floorNumber;

    /**
     * Constructs a new Floor.
     * @param floorNumber The number of the floor.
     */
    public Floor(int floorNumber) {
        this.floorNumber = floorNumber;
    }

    /**
     * Gets the floor number.
     * @return The floor number.
     */
    public int getFloorNumber() {
        return floorNumber;
    }
}
