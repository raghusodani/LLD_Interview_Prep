package VehicleFactoryPattern;

import CommonEnum.DurationType;
import FareStrategyPattern.ParkingFeeStrategy;

/**
 * Represents a vehicle.
 */
public abstract class Vehicle {
    private String licensePlate; // Stores the vehicle's license plate number
    private String vehicleType; // Stores the type of vehicle (e.g., car, bike, truck)
    private ParkingFeeStrategy feeStrategy; // Strategy for calculating parking fees

    /**
     * Constructor to initialize a vehicle with its license plate, type, and fee strategy.
     * @param licensePlate The license plate of the vehicle.
     * @param vehicleType The type of the vehicle.
     * @param feeStrategy The fee strategy for the vehicle.
     */
    public Vehicle(String licensePlate, String vehicleType, ParkingFeeStrategy feeStrategy) {
        this.licensePlate = licensePlate;
        this.vehicleType = vehicleType;
        this.feeStrategy = feeStrategy;
    }

    /**
     * Getter method to retrieve the vehicle type.
     * @return The type of the vehicle.
     */
    public String getVehicleType() {
        return vehicleType;
    }

    /**
     * Getter method to retrieve the vehicle's license plate number.
     * @return The license plate of the vehicle.
     */
    public String getLicensePlate() {
        return licensePlate;
    }

    /**
     * Method to calculate parking fee based on duration and duration type.
     * @param duration The duration of parking.
     * @param durationType The type of the duration (HOURS or DAYS).
     * @return The calculated parking fee.
     */
    public double calculateFee(int duration, DurationType durationType) {
        return feeStrategy.calculateFee(vehicleType, duration, durationType);
    }
}
