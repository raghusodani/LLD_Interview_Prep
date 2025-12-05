package FareCalculation;

import Models.Trip;

public class PremiumFareStrategy implements FareStrategy {
    private static final double BASE_FARE = 100.0; // Higher base for premium
    private static final double PER_KM_RATE = 15.0; // Higher per km
    private static final double PER_MINUTE_RATE = 3.0;
    private static final double PREMIUM_BONUS = 50.0; // Fixed premium charge

    @Override
    public double calculateFare(Trip trip) {
        double distance = trip.getDistance();
        long duration = trip.getDurationInMinutes();

        double fare = BASE_FARE + PREMIUM_BONUS + (distance * PER_KM_RATE) + (duration * PER_MINUTE_RATE);
        return Math.round(fare * 100.0) / 100.0;
    }
}
