package Matching;

import Models.Driver;
import Models.Location;
import CommonEnum.DriverStatus;
import java.util.List;

public class NearestDriverStrategy implements DriverMatchingStrategy {

    @Override
    public Driver findBestDriver(List<Driver> availableDrivers, Location riderLocation, double maxRadius) {
        Driver nearestDriver = null;
        double minDistance = Double.MAX_VALUE;

        for (Driver driver : availableDrivers) {
            if (driver.getStatus() == DriverStatus.AVAILABLE) {
                double distance = driver.getCurrentLocation().distanceTo(riderLocation);

                if (distance <= maxRadius && distance < minDistance) {
                    minDistance = distance;
                    nearestDriver = driver;
                }
            }
        }

        return nearestDriver;
    }
}
