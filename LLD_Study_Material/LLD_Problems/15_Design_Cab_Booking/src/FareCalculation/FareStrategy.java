package FareCalculation;

import Models.Trip;

public interface FareStrategy {
    double calculateFare(Trip trip);
}
