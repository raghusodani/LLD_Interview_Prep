package FareCalculation;

import Models.Trip;

public class BaseFareStrategy implements FareStrategy {
    private static final double BASE_FARE = 50.0;
    private static final double PER_KM_RATE = 10.0;
    private static final double PER_MINUTE_RATE = 2.0;

    @Override
    public double calculateFare(Trip trip) {
        double distance = trip.getDistance();
        long duration = trip.getDurationInMinutes();

        double fare = BASE_FARE + (distance * PER_KM_RATE) + (duration * PER_MINUTE_RATE);
        return Math.round(fare * 100.0) / 100.0; // Round to 2 decimal places
    }
}
