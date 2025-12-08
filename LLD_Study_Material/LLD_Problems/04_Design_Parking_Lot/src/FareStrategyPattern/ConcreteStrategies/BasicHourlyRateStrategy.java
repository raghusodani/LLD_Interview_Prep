package FareStrategyPattern.ConcreteStrategies;

import CommonEnum.DurationType;
import FareStrategyPattern.ParkingFeeStrategy;

/**
 * A basic hourly rate strategy for calculating parking fees.
 */
public class BasicHourlyRateStrategy implements ParkingFeeStrategy {
    /**
     * Calculates the parking fee based on the vehicle type, duration, and duration type.
     * @param vehicleType The type of the vehicle.
     * @param duration The duration of parking.
     * @param durationType The type of the duration (HOURS or DAYS).
     * @return The calculated parking fee.
     */
    @Override
    public double calculateFee(String vehicleType, int duration, DurationType durationType) {
        // Different rates for different vehicle types
        switch (vehicleType.toLowerCase()) {
            case "car":
                return durationType == DurationType.HOURS
                        ? duration * 10.0   // $10 per hour for cars
                        : duration * 10.0 * 24;  // Daily rate

            case "bike":
                return durationType == DurationType.HOURS
                        ? duration * 5.0    // $5 per hour for bikes
                        : duration * 5.0 * 24;  // Daily rate

            case "auto":
                return durationType == DurationType.HOURS
                        ? duration * 8.0    // $8 per hour for autos
                        : duration * 8.0 * 24;  // Daily rate

            default:
                return durationType == DurationType.HOURS
                        ? duration * 15.0   // $15 per hour for other vehicles
                        : duration * 15.0 * 24;  // Daily rate
        }
    }
}
