package Services;

import Models.Driver;
import Models.Location;
import Models.Rider;
import Models.Trip;
import CommonEnum.DriverStatus;
import CommonEnum.TripStatus;
import Matching.DriverMatchingStrategy;
import FareCalculation.FareStrategy;
import java.util.*;
import java.time.LocalDateTime;

public class CabService {
    private List<Driver> drivers;
    private List<Rider> riders;
    private List<Trip> trips;
    private DriverMatchingStrategy matchingStrategy;
    private FareStrategy fareStrategy;
    private int tripCounter;

    public CabService(DriverMatchingStrategy matchingStrategy, FareStrategy fareStrategy) {
        this.drivers = new ArrayList<>();
        this.riders = new ArrayList<>();
        this.trips = new ArrayList<>();
        this.matchingStrategy = matchingStrategy;
        this.fareStrategy = fareStrategy;
        this.tripCounter = 1;
    }

    public void registerDriver(Driver driver) {
        drivers.add(driver);
        System.out.println("✅ Driver registered: " + driver);
    }

    public void registerRider(Rider rider) {
        riders.add(rider);
        System.out.println("✅ Rider registered: " + rider);
    }

    public void setMatchingStrategy(DriverMatchingStrategy strategy) {
        this.matchingStrategy = strategy;
    }

    public void setFareStrategy(FareStrategy strategy) {
        this.fareStrategy = strategy;
    }

    // Request a ride
    public Trip requestRide(Rider rider, Location pickup, Location drop, double maxRadius) {
        System.out.println("\n🚕 " + rider.getName() + " requesting ride from " + pickup + " to " + drop);

        // Find best available driver
        Driver driver = matchingStrategy.findBestDriver(drivers, pickup, maxRadius);

        if (driver == null) {
            System.out.println("❌ No drivers available within " + maxRadius + " km radius");
            return null;
        }

        // Create trip
        Trip trip = new Trip("TRIP-" + (tripCounter++), rider, pickup, drop);
        trip.setDriver(driver);
        trip.setStatus(TripStatus.ACCEPTED);

        // Mark driver as busy
        driver.setStatus(DriverStatus.BUSY);

        trips.add(trip);

        System.out.println("✅ Trip accepted by " + driver.getName() +
                         " (Distance to rider: " + String.format("%.2f", driver.getCurrentLocation().distanceTo(pickup)) + " km)");
        System.out.println("   " + trip);

        return trip;
    }

    // Start trip
    public void startTrip(Trip trip) {
        if (trip.getStatus() != TripStatus.ACCEPTED) {
            System.out.println("❌ Cannot start trip. Current status: " + trip.getStatus());
            return;
        }

        trip.setStatus(TripStatus.STARTED);
        trip.setStartTime(LocalDateTime.now());

        System.out.println("\n🚗 Trip started: " + trip.getTripId());
    }

    // End trip
    public void endTrip(Trip trip) {
        if (trip.getStatus() != TripStatus.STARTED) {
            System.out.println("❌ Cannot end trip. Current status: " + trip.getStatus());
            return;
        }

        trip.setEndTime(LocalDateTime.now());
        trip.setStatus(TripStatus.COMPLETED);

        // Calculate fare
        double fare = fareStrategy.calculateFare(trip);
        trip.setFare(fare);

        // Update driver location to drop location
        trip.getDriver().setCurrentLocation(trip.getDropLocation());

        // Mark driver as available
        trip.getDriver().setStatus(DriverStatus.AVAILABLE);

        System.out.println("\n✅ Trip completed: " + trip.getTripId());
        System.out.println("   Distance: " + String.format("%.2f", trip.getDistance()) + " km");
        System.out.println("   Duration: " + trip.getDurationInMinutes() + " minutes");
        System.out.println("   💰 Fare: $" + trip.getFare());
    }

    // Toggle driver availability
    public void toggleDriverAvailability(Driver driver) {
        if (driver.getStatus() == DriverStatus.AVAILABLE) {
            driver.setStatus(DriverStatus.OFFLINE);
            System.out.println("🔴 " + driver.getName() + " is now OFFLINE");
        } else if (driver.getStatus() == DriverStatus.OFFLINE) {
            driver.setStatus(DriverStatus.AVAILABLE);
            System.out.println("🟢 " + driver.getName() + " is now AVAILABLE");
        } else {
            System.out.println("⚠️  " + driver.getName() + " is currently BUSY with a trip");
        }
    }

    // Get all trips
    public List<Trip> getAllTrips() {
        return new ArrayList<>(trips);
    }

    // Get driver stats
    public void printDriverStats() {
        System.out.println("\n📊 Driver Statistics:");
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");

        for (Driver driver : drivers) {
            long completedTrips = trips.stream()
                .filter(t -> t.getDriver() != null &&
                           t.getDriver().getDriverId().equals(driver.getDriverId()) &&
                           t.getStatus() == TripStatus.COMPLETED)
                .count();

            double totalEarnings = trips.stream()
                .filter(t -> t.getDriver() != null &&
                           t.getDriver().getDriverId().equals(driver.getDriverId()) &&
                           t.getStatus() == TripStatus.COMPLETED)
                .mapToDouble(Trip::getFare)
                .sum();

            System.out.printf("%s | Trips: %d | Earnings: $%.2f | Status: %s%n",
                driver.getName(), completedTrips, totalEarnings, driver.getStatus());
        }
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
    }
}
