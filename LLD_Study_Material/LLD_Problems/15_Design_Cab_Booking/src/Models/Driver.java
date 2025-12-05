package Models;

import CommonEnum.DriverStatus;

public class Driver {
    private String driverId;
    private String name;
    private String vehicleNumber;
    private Location currentLocation;
    private DriverStatus status;
    private double rating;

    public Driver(String driverId, String name, String vehicleNumber, Location location, double rating) {
        this.driverId = driverId;
        this.name = name;
        this.vehicleNumber = vehicleNumber;
        this.currentLocation = location;
        this.status = DriverStatus.AVAILABLE;
        this.rating = rating;
    }

    public String getDriverId() {
        return driverId;
    }

    public String getName() {
        return name;
    }

    public String getVehicleNumber() {
        return vehicleNumber;
    }

    public Location getCurrentLocation() {
        return currentLocation;
    }

    public void setCurrentLocation(Location location) {
        this.currentLocation = location;
    }

    public DriverStatus getStatus() {
        return status;
    }

    public void setStatus(DriverStatus status) {
        this.status = status;
    }

    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }

    @Override
    public String toString() {
        return String.format("Driver[%s, %s, %s, Rating: %.1f, Status: %s, Location: %s]",
                driverId, name, vehicleNumber, rating, status, currentLocation);
    }
}
