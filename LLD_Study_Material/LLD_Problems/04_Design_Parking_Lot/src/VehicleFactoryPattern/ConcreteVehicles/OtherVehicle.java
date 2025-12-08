package VehicleFactoryPattern.ConcreteVehicles;

import FareStrategyPattern.ParkingFeeStrategy;
import VehicleFactoryPattern.Vehicle;

/**
 * Represents a vehicle of a type other than car or bike.
 */
public class OtherVehicle extends Vehicle {
    /**
     * Constructs a new OtherVehicle.
     * @param licensePlate The license plate of the vehicle.
     * @param vehicleType The type of the vehicle.
     * @param feeStrategy The fee strategy for the vehicle.
     */
    public OtherVehicle(String licensePlate, String vehicleType, ParkingFeeStrategy feeStrategy) {
        super(licensePlate, vehicleType, feeStrategy);
    }
}
