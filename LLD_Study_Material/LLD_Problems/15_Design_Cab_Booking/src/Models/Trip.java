package Models;

import CommonEnum.TripStatus;
import java.time.LocalDateTime;
import java.time.Duration;

public class Trip {
    private String tripId;
    private Rider rider;
    private Driver driver;
    private Location pickupLocation;
    private Location dropLocation;
    private TripStatus status;
    private LocalDateTime requestTime;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private double fare;
    private double distance;

    public Trip(String tripId, Rider rider, Location pickupLocation, Location dropLocation) {
        this.tripId = tripId;
        this.rider = rider;
        this.pickupLocation = pickupLocation;
        this.dropLocation = dropLocation;
        this.status = TripStatus.REQUESTED;
        this.requestTime = LocalDateTime.now();
        this.distance = pickupLocation.distanceTo(dropLocation);
    }

    public String getTripId() {
        return tripId;
    }

    public Rider getRider() {
        return rider;
    }

    public Driver getDriver() {
        return driver;
    }

    public void setDriver(Driver driver) {
        this.driver = driver;
    }

    public Location getPickupLocation() {
        return pickupLocation;
    }

    public Location getDropLocation() {
        return dropLocation;
    }

    public TripStatus getStatus() {
        return status;
    }

    public void setStatus(TripStatus status) {
        this.status = status;
    }

    public LocalDateTime getRequestTime() {
        return requestTime;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    public double getFare() {
        return fare;
    }

    public void setFare(double fare) {
        this.fare = fare;
    }

    public double getDistance() {
        return distance;
    }

    public long getDurationInMinutes() {
        if (startTime != null && endTime != null) {
            return Duration.between(startTime, endTime).toMinutes();
        }
        return 0;
    }

    @Override
    public String toString() {
        return String.format("Trip[%s, %s, Driver: %s, Status: %s, Distance: %.2f km, Fare: $%.2f]",
                tripId, rider.getName(),
                driver != null ? driver.getName() : "Not Assigned",
                status, distance, fare);
    }
}
