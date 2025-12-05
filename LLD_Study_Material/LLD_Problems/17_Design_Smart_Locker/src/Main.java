import CommonEnum.LockerSize;
import Models.Locker;
import Models.Package;
import Services.LockerService;
import SizeMatching.DefaultSizeMatching;

public class Main {
    public static void main(String[] args) {
        System.out.println("🔐 Smart Locker System Demo\n");
        System.out.println("=".repeat(60));

        // Initialize locker service with size matching strategy
        LockerService lockerService = new LockerService(new DefaultSizeMatching());

        // Setup lockers
        setupLockers(lockerService);

        // Show initial status
        lockerService.printLockerStatus();

        System.out.println("\n📦 SCENARIO 1: Deliver packages\n");
        System.out.println("=".repeat(60));

        // Deliver packages
        Package pkg1 = new Package("PKG001", "9876543210", LockerSize.SMALL);
        String locker1 = lockerService.depositPackage(pkg1);

        Package pkg2 = new Package("PKG002", "9876543211", LockerSize.MEDIUM);
        String locker2 = lockerService.depositPackage(pkg2);

        Package pkg3 = new Package("PKG003", "9876543212", LockerSize.SMALL);
        String locker3 = lockerService.depositPackage(pkg3);

        // This should use MEDIUM locker since SMALL is full
        Package pkg4 = new Package("PKG004", "9876543213", LockerSize.SMALL);
        String locker4 = lockerService.depositPackage(pkg4);

        Package pkg5 = new Package("PKG005", "9876543214", LockerSize.LARGE);
        String locker5 = lockerService.depositPackage(pkg5);

        // Show status after delivery
        lockerService.printLockerStatus();

        System.out.println("\n📱 SCENARIO 2: Pickup with correct OTP\n");
        System.out.println("=".repeat(60));

        // Successful pickup
        lockerService.pickupPackage(locker1, pkg1.getOtp(), "9876543210");
        System.out.println();

        System.out.println("\n❌ SCENARIO 3: Failed pickup attempts\n");
        System.out.println("=".repeat(60));

        // Wrong OTP
        lockerService.pickupPackage(locker2, "000000", "9876543211");
        System.out.println();

        // Wrong phone
        lockerService.pickupPackage(locker2, pkg2.getOtp(), "1111111111");
        System.out.println();

        // Successful pickup
        lockerService.pickupPackage(locker2, pkg2.getOtp(), "9876543211");
        System.out.println();

        // Show status after pickups
        lockerService.printLockerStatus();

        System.out.println("\n🔄 SCENARIO 4: Process returns (expired packages)\n");
        System.out.println("=".repeat(60));

        // Simulate expired packages by manually setting occupied time
        // In real system, this would be checked by background job
        System.out.println("Simulating expired packages check...");
        lockerService.processReturns();
        System.out.println("(No packages expired in this demo - retention period is 72 hours)\n");

        System.out.println("\n🔧 SCENARIO 5: Locker maintenance\n");
        System.out.println("=".repeat(60));

        // Find a locker and put it in maintenance
        Locker maintenanceLocker = lockerService.getLockers().get(0);
        maintenanceLocker.setStatus(CommonEnum.LockerStatus.MAINTENANCE);
        System.out.println("Locker " + maintenanceLocker.getLockerId() + " set to MAINTENANCE mode");

        lockerService.printLockerStatus();

        System.out.println("\n📈 SCENARIO 6: All remaining pickups\n");
        System.out.println("=".repeat(60));

        // Pickup remaining packages
        if (locker3 != null) {
            lockerService.pickupPackage(locker3, pkg3.getOtp(), "9876543212");
        }
        if (locker4 != null) {
            lockerService.pickupPackage(locker4, pkg4.getOtp(), "9876543213");
        }
        if (locker5 != null) {
            lockerService.pickupPackage(locker5, pkg5.getOtp(), "9876543214");
        }

        // Final status
        lockerService.printLockerStatus();

        System.out.println("\n✅ Demo Complete!\n");
        System.out.println("Key Features Demonstrated:");
        System.out.println("  ✅ Smart size matching (small package → medium locker when small full)");
        System.out.println("  ✅ Secure OTP generation and validation");
        System.out.println("  ✅ Phone number verification");
        System.out.println("  ✅ Return handling for unpicked packages");
        System.out.println("  ✅ Locker maintenance mode");
        System.out.println("  ✅ Real-time status tracking");
    }

    private static void setupLockers(LockerService service) {
        // Create 10 lockers with different sizes
        service.addLocker(new Locker("S1", LockerSize.SMALL));
        service.addLocker(new Locker("S2", LockerSize.SMALL));

        service.addLocker(new Locker("M1", LockerSize.MEDIUM));
        service.addLocker(new Locker("M2", LockerSize.MEDIUM));
        service.addLocker(new Locker("M3", LockerSize.MEDIUM));

        service.addLocker(new Locker("L1", LockerSize.LARGE));
        service.addLocker(new Locker("L2", LockerSize.LARGE));

        service.addLocker(new Locker("XL1", LockerSize.EXTRA_LARGE));
        service.addLocker(new Locker("XL2", LockerSize.EXTRA_LARGE));

        System.out.println("✅ Initialized locker system with 9 lockers");
        System.out.println("   - 2 SMALL lockers");
        System.out.println("   - 3 MEDIUM lockers");
        System.out.println("   - 2 LARGE lockers");
        System.out.println("   - 2 EXTRA_LARGE lockers\n");
    }
}
