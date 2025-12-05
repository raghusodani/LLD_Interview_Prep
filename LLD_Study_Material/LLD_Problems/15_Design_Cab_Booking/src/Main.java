import Models.*;
import CommonEnum.*;
import Services.CabService;
import Matching.*;
import FareCalculation.*;

public class Main {
    public static void main(String[] args) throws InterruptedException {
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        System.out.println("       🚕 CAB BOOKING SYSTEM DEMO 🚕");
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");

        // Initialize system with nearest driver matching and base fare
        CabService cabService = new CabService(
            new NearestDriverStrategy(),
            new BaseFareStrategy()
        );

        // Register Drivers
        System.out.println("\n📋 Registering Drivers...");
        Driver d1 = new Driver("D001", "John Doe", "KA-01-1234", new Location(10.0, 10.0), 4.5);
        Driver d2 = new Driver("D002", "Jane Smith", "KA-02-5678", new Location(10.5, 10.5), 4.8);
        Driver d3 = new Driver("D003", "Bob Wilson", "KA-03-9012", new Location(15.0, 15.0), 4.2);
        Driver d4 = new Driver("D004", "Alice Brown", "KA-04-3456", new Location(20.0, 20.0), 4.9);

        cabService.registerDriver(d1);
        cabService.registerDriver(d2);
        cabService.registerDriver(d3);
        cabService.registerDriver(d4);

        // Register Riders
        System.out.println("\n📋 Registering Riders...");
        Rider r1 = new Rider("R001", "Alex Johnson", "+1-555-0001", 4.7);
        Rider r2 = new Rider("R002", "Sarah Williams", "+1-555-0002", 4.9);

        cabService.registerRider(r1);
        cabService.registerRider(r2);

        System.out.println("\n" + "=".repeat(50));
        System.out.println("SCENARIO 1: Nearest Driver Matching");
        System.out.println("=".repeat(50));

        // Request ride 1 - Alex needs a ride
        Trip trip1 = cabService.requestRide(r1,
            new Location(10.2, 10.2),  // Pickup
            new Location(12.0, 12.0),  // Drop
            5.0  // Max 5 km radius
        );

        if (trip1 != null) {
            cabService.startTrip(trip1);
            Thread.sleep(100); // Simulate trip duration
            cabService.endTrip(trip1);
        }

        System.out.println("\n" + "=".repeat(50));
        System.out.println("SCENARIO 2: Driver Availability Toggle");
        System.out.println("=".repeat(50));

        // Bob goes offline
        cabService.toggleDriverAvailability(d3);

        System.out.println("\n" + "=".repeat(50));
        System.out.println("SCENARIO 3: Surge Pricing");
        System.out.println("=".repeat(50));

        // Switch to surge pricing (2x multiplier)
        cabService.setFareStrategy(new SurgeFareStrategy(2.0));
        System.out.println("⚠️  SURGE PRICING ACTIVATED: 2.0x multiplier");

        Trip trip2 = cabService.requestRide(r2,
            new Location(15.2, 15.2),  // Pickup (far from d1/d2, close to d3)
            new Location(18.0, 18.0),  // Drop
            10.0
        );

        if (trip2 != null) {
            cabService.startTrip(trip2);
            Thread.sleep(100);
            cabService.endTrip(trip2);
        }

        System.out.println("\n" + "=".repeat(50));
        System.out.println("SCENARIO 4: Highest Rated Driver Strategy");
        System.out.println("=".repeat(50));

        // Switch to highest rated driver matching
        cabService.setMatchingStrategy(new HighestRatedDriverStrategy());
        cabService.setFareStrategy(new BaseFareStrategy());
        System.out.println("🌟 Switched to HIGHEST RATED driver matching");

        Trip trip3 = cabService.requestRide(r1,
            new Location(19.0, 19.0),  // Pickup
            new Location(22.0, 22.0),  // Drop
            5.0
        );

        if (trip3 != null) {
            cabService.startTrip(trip3);
            Thread.sleep(100);
            cabService.endTrip(trip3);
        }

        System.out.println("\n" + "=".repeat(50));
        System.out.println("SCENARIO 5: Premium Fare Strategy");
        System.out.println("=".repeat(50));

        cabService.setFareStrategy(new PremiumFareStrategy());
        cabService.setMatchingStrategy(new NearestDriverStrategy());
        System.out.println("💎 Switched to PREMIUM fare strategy");

        Trip trip4 = cabService.requestRide(r2,
            new Location(10.0, 10.5),
            new Location(11.0, 11.5),
            3.0
        );

        if (trip4 != null) {
            cabService.startTrip(trip4);
            Thread.sleep(100);
            cabService.endTrip(trip4);
        }

        System.out.println("\n" + "=".repeat(50));
        System.out.println("SCENARIO 6: No Driver Available (Out of Radius)");
        System.out.println("=".repeat(50));

        Trip trip5 = cabService.requestRide(r1,
            new Location(50.0, 50.0),  // Very far location
            new Location(55.0, 55.0),
            2.0  // Small radius
        );

        // Final statistics
        cabService.printDriverStats();

        System.out.println("\n━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        System.out.println("       ✅ CAB BOOKING SYSTEM DEMO COMPLETE");
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
    }
}
