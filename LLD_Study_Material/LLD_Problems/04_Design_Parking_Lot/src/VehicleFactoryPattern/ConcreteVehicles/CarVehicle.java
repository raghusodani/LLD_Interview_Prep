package VehicleFactoryPattern.ConcreteVehicles;

import FareStrategyPattern.ParkingFeeStrategy;
import VehicleFactoryPattern.Vehicle;

/**
 * Represents a car vehicle.
 */
public class CarVehicle extends Vehicle {
    /**
     * Constructs a new CarVehicle.
     * @param licensePlate The license plate of the car.
     * @param vehicleType The type of the vehicle.
     * @param feeStrategy The fee strategy for the car.
     */
    public CarVehicle(String licensePlate, String vehicleType, ParkingFeeStrategy feeStrategy) {
        super(licensePlate, vehicleType, feeStrategy);
    }
}
