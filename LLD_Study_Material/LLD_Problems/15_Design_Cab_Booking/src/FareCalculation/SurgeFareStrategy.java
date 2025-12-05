package FareCalculation;

import Models.Trip;

public class SurgeFareStrategy implements FareStrategy {
    private static final double BASE_FARE = 50.0;
    private static final double PER_KM_RATE = 10.0;
    private static final double PER_MINUTE_RATE = 2.0;
    private double surgeMultiplier;

    public SurgeFareStrategy(double surgeMultiplier) {
        this.surgeMultiplier = surgeMultiplier; // e.g., 1.5x, 2.0x during peak hours
    }

    @Override
    public double calculateFare(Trip trip) {
        double distance = trip.getDistance();
        long duration = trip.getDurationInMinutes();

        double baseFare = BASE_FARE + (distance * PER_KM_RATE) + (duration * PER_MINUTE_RATE);
        double surgeFare = baseFare * surgeMultiplier;

        return Math.round(surgeFare * 100.0) / 100.0;
    }

    public double getSurgeMultiplier() {
        return surgeMultiplier;
    }
}
