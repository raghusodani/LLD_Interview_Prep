package VehicleFactoryPattern;

import FareStrategyPattern.ParkingFeeStrategy;
import VehicleFactoryPattern.ConcreteVehicles.BikeVehicle;
import VehicleFactoryPattern.ConcreteVehicles.CarVehicle;
import VehicleFactoryPattern.ConcreteVehicles.OtherVehicle;

/**
 * A factory for creating vehicles.
 */
public class VehicleFactory {
    /**
     * Creates a new vehicle.
     * @param vehicleType The type of the vehicle to create.
     * @param licensePlate The license plate of the vehicle.
     * @param feeStrategy The fee strategy for the vehicle.
     * @return The created vehicle.
     */
    public static Vehicle createVehicle(String vehicleType, String licensePlate, ParkingFeeStrategy feeStrategy) {
        if (vehicleType.equalsIgnoreCase("Car")) {
            return new CarVehicle(licensePlate, vehicleType, feeStrategy);
        } else if (vehicleType.equalsIgnoreCase("Bike")) {
            return new BikeVehicle(licensePlate, vehicleType, feeStrategy);
        }
        return new OtherVehicle(licensePlate, vehicleType, feeStrategy); // For unsupported vehicle types
    }
}
