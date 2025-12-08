package ParkingLotController;

import ParkingSpots.ParkingSpot;
import VehicleFactoryPattern.Vehicle;

import java.util.List;

/**
 * Represents a parking lot.
 */
public class ParkingLot {
    private List<ParkingSpot> parkingSpots;

    /**
     * Constructor to initialize the parking lot with parking spots.
     * @param parkingSpots The list of parking spots in the parking lot.
     */
    public ParkingLot(List<ParkingSpot> parkingSpots) {
        this.parkingSpots = parkingSpots;
    }

    /**
     * Method to find an available spot based on vehicle type.
     * @param vehicleType The type of the vehicle.
     * @return An available parking spot, or null if no spot is available.
     */
    public ParkingSpot findAvailableSpot(String vehicleType) {
        for (ParkingSpot spot : parkingSpots) {
            if (!spot.isOccupied() && spot.getSpotType().equals(vehicleType)) {
                return spot; // Found an available spot for the vehicle type
            }
        }
        return null; // No available spot found for the given vehicle type
    }

    /**
     * Method to park a vehicle.
     * @param vehicle The vehicle to park.
     * @return The parking spot where the vehicle is parked, or null if no spot is available.
     */
    public ParkingSpot parkVehicle(Vehicle vehicle) {
        ParkingSpot spot = findAvailableSpot(vehicle.getVehicleType());
        if (spot != null) {
            spot.parkVehicle(vehicle); // Mark the spot as occupied
            System.out.println(
                    "Vehicle parked successfully in spot: " + spot.getSpotNumber());
            return spot;
        }
        System.out.println(
                "No parking spots available for " + vehicle.getVehicleType() + "!");
        return null;
    }

    /**
     * Method to vacate a parking spot.
     * @param spot The parking spot to vacate.
     * @param vehicle The vehicle that is vacating the spot.
     */
    public void vacateSpot(ParkingSpot spot, Vehicle vehicle) {
        if (spot != null && spot.isOccupied()
                && spot.getVehicle().equals(vehicle)) {
            spot.vacate(); // Free the spot
            System.out.println(vehicle.getVehicleType()
                    + " vacated the spot: " + spot.getSpotNumber());
        } else {
            System.out.println("Invalid operation! Either the spot is already vacant "
                    + "or the vehicle does not match.");
        }
    }

    /**
     * Method to find a spot by its number.
     * @param spotNumber The number of the spot to find.
     * @return The parking spot with the given number, or null if not found.
     */
    public ParkingSpot getSpotByNumber(int spotNumber) {
        for (ParkingSpot spot : parkingSpots) {
            if (spot.getSpotNumber() == spotNumber) {
                return spot;
            }
        }
        return null; // Spot not found
    }

    /**
     * Getter for parking spots.
     * @return The list of parking spots.
     */
    public List<ParkingSpot> getParkingSpots() {
        return parkingSpots;
    }
}
