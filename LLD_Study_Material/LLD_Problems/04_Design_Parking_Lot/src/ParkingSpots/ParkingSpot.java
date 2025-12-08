package ParkingSpots;

import VehicleFactoryPattern.Vehicle;

/**
 * Represents a parking spot.
 */
public abstract class ParkingSpot {
    private int spotNumber;
    private boolean isOccupied;
    private Vehicle vehicle;
    private String spotType;

    /**
     * Constructor to initialize parking spot with spot number and type.
     * @param spotNumber The number of the parking spot.
     * @param spotType The type of the parking spot.
     */
    public ParkingSpot(int spotNumber, String spotType) {
        this.spotNumber = spotNumber;
        this.isOccupied = false;
        this.spotType = spotType;
    }

    /**
     * Method to check if the spot is occupied.
     * @return true if the spot is occupied, false otherwise.
     */
    public boolean isOccupied() {
        return isOccupied;
    }

    /**
     * Abstract method to check if a vehicle can park in this spot.
     * @param vehicle The vehicle to be parked.
     * @return true if the vehicle can be parked, false otherwise.
     */
    public abstract boolean canParkVehicle(Vehicle vehicle);

    /**
     * Method to park a vehicle in the spot.
     * @param vehicle The vehicle to be parked.
     * @throws IllegalStateException if the spot is already occupied.
     * @throws IllegalArgumentException if the vehicle cannot be parked in this spot.
     */
    public void parkVehicle(Vehicle vehicle) {
        // Check if the spot is already occupied
        if (isOccupied) {
            throw new IllegalStateException("Spot is already occupied.");
        }
        // Check if the vehicle can be parked in this spot
        if (!canParkVehicle(vehicle)) {
            throw new IllegalArgumentException(
                    "This spot is not suitable for" + vehicle.getVehicleType());
        }
        this.vehicle = vehicle;
        this.isOccupied = true;
    }

    /**
     * Method to vacate the parking spot.
     * @throws IllegalStateException if the spot is already vacant.
     */
    public void vacate() {
        // Check if the spot is already vacant
        if (!isOccupied) {
            throw new IllegalStateException("Spot is already vacant.");
        }
        this.vehicle = null;
        this.isOccupied = false;
    }

    /**
     * Getter for spot number.
     * @return The number of the parking spot.
     */
    public int getSpotNumber() {
        return spotNumber;
    }

    /**
     * Getter for the vehicle parked in the spot.
     * @return The vehicle parked in the spot.
     */
    public Vehicle getVehicle() {
        return vehicle;
    }

    /**
     * Getter for spot type.
     * @return The type of the parking spot.
     */
    public String getSpotType() {
        return spotType;
    }
}
