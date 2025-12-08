package VehicleFactoryPattern.ConcreteVehicles;

import FareStrategyPattern.ParkingFeeStrategy;
import VehicleFactoryPattern.Vehicle;

/**
 * Represents a bike vehicle.
 */
public class BikeVehicle extends Vehicle {
    /**
     * Constructs a new BikeVehicle.
     * @param licensePlate The license plate of the bike.
     * @param vehicleType The type of the vehicle.
     * @param feeStrategy The fee strategy for the bike.
     */
    public BikeVehicle(String licensePlate, String vehicleType, ParkingFeeStrategy feeStrategy) {
        super(licensePlate, vehicleType, feeStrategy);
    }
}
