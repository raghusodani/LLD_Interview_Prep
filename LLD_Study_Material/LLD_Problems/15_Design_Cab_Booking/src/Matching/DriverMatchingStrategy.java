package Matching;

import Models.Driver;
import Models.Location;
import java.util.List;

public interface DriverMatchingStrategy {
    Driver findBestDriver(List<Driver> availableDrivers, Location riderLocation, double maxRadius);
}
