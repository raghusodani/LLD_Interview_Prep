package ParkingSpots.ConcreteParkingSpots;

import ParkingSpots.ParkingSpot;
import VehicleFactoryPattern.Vehicle;

/**
 * Represents a parking spot for bikes.
 */
public class BikeParkingSpot extends ParkingSpot {
    /**
     * Constructs a new BikeParkingSpot.
     * @param spotNumber The number of the parking spot.
     * @param spotType The type of the parking spot.
     */
    public BikeParkingSpot(int spotNumber, String spotType) {
        super(spotNumber, spotType);
    }

    /**
     * Checks if a vehicle can be parked in this spot.
     * @param vehicle The vehicle to be parked.
     * @return true if the vehicle is a bike, false otherwise.
     */
    @Override
    public boolean canParkVehicle(Vehicle vehicle) {
        return "Bike".equalsIgnoreCase(vehicle.getVehicleType());
    }
}
