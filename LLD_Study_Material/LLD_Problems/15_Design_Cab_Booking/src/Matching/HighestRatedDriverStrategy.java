package Matching;

import Models.Driver;
import Models.Location;
import CommonEnum.DriverStatus;
import java.util.List;

public class HighestRatedDriverStrategy implements DriverMatchingStrategy {

    @Override
    public Driver findBestDriver(List<Driver> availableDrivers, Location riderLocation, double maxRadius) {
        Driver bestDriver = null;
        double highestRating = 0.0;

        for (Driver driver : availableDrivers) {
            if (driver.getStatus() == DriverStatus.AVAILABLE) {
                double distance = driver.getCurrentLocation().distanceTo(riderLocation);

                // Driver must be within radius AND have higher rating
                if (distance <= maxRadius && driver.getRating() > highestRating) {
                    highestRating = driver.getRating();
                    bestDriver = driver;
                }
            }
        }

        return bestDriver;
    }
}
