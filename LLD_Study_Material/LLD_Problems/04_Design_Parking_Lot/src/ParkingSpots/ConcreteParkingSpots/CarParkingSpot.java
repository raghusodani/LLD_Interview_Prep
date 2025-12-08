package ParkingSpots.ConcreteParkingSpots;

import ParkingSpots.ParkingSpot;
import VehicleFactoryPattern.Vehicle;

/**
 * Represents a parking spot for cars.
 */
public class CarParkingSpot extends ParkingSpot {
    /**
     * Constructs a new CarParkingSpot.
     * @param spotNumber The number of the parking spot.
     * @param spotType The type of the parking spot.
     */
    public CarParkingSpot(int spotNumber, String spotType) {
        super(spotNumber, spotType);
    }

    /**
     * Checks if a vehicle can be parked in this spot.
     * @param vehicle The vehicle to be parked.
     * @return true if the vehicle is a car, false otherwise.
     */
    @Override
    public boolean canParkVehicle(Vehicle vehicle) {
        return "Car".equalsIgnoreCase(vehicle.getVehicleType());
    }
}
