package Services;

import CommonEnum.LockerSize;
import CommonEnum.LockerStatus;
import Models.Locker;
import Models.Package;
import Models.Pickup;
import SizeMatching.SizeMatchingStrategy;
import Security.OTPGenerator;
import Security.OTPValidator;

import java.util.*;

public class LockerService {
    private List<Locker> lockers;
    private SizeMatchingStrategy sizeMatchingStrategy;
    private Map<String, String> packageToLockerMap; // packageId -> lockerId
    private List<Pickup> pickupHistory;
    private static final long MAX_RETENTION_HOURS = 72; // 3 days

    public LockerService(SizeMatchingStrategy sizeMatchingStrategy) {
        this.lockers = new ArrayList<>();
        this.sizeMatchingStrategy = sizeMatchingStrategy;
        this.packageToLockerMap = new HashMap<>();
        this.pickupHistory = new ArrayList<>();
    }

    public void addLocker(Locker locker) {
        lockers.add(locker);
    }

    public String depositPackage(Package pkg) {
        // Find suitable locker using strategy
        Locker suitableLocker = sizeMatchingStrategy.findSuitableLocker(lockers, pkg.getSize());

        if (suitableLocker == null) {
            System.out.println("❌ No suitable locker available for package " + pkg.getPackageId());
            return null;
        }

        // Generate OTP
        String otp = OTPGenerator.generateOTP();
        pkg.setOtp(otp);

        // Assign package to locker
        if (suitableLocker.assignPackage(pkg)) {
            packageToLockerMap.put(pkg.getPackageId(), suitableLocker.getLockerId());
            System.out.println("✅ Package " + pkg.getPackageId() + " deposited in locker " +
                             suitableLocker.getLockerId() + " (Size: " + suitableLocker.getSize() + ")");
            System.out.println("   📱 OTP sent to " + pkg.getRecipientPhone() + ": " + otp);
            return suitableLocker.getLockerId();
        }

        return null;
    }

    public boolean pickupPackage(String lockerId, String otp, String phone) {
        Locker locker = findLockerById(lockerId);

        if (locker == null) {
            System.out.println("❌ Locker not found: " + lockerId);
            return false;
        }

        Package pkg = locker.getCurrentPackage();
        if (pkg == null) {
            System.out.println("❌ Locker is empty");
            return false;
        }

        // Validate phone number
        if (!pkg.getRecipientPhone().equals(phone)) {
            System.out.println("❌ Phone number mismatch");
            recordPickup(lockerId, pkg.getPackageId(), phone, false);
            return false;
        }

        // Validate OTP
        if (!OTPValidator.validateOTP(pkg, otp)) {
            System.out.println("❌ Invalid or expired OTP");
            recordPickup(lockerId, pkg.getPackageId(), phone, false);
            return false;
        }

        // Successful pickup
        locker.removePackage();
        packageToLockerMap.remove(pkg.getPackageId());
        System.out.println("✅ Package " + pkg.getPackageId() + " picked up successfully from locker " + lockerId);
        recordPickup(lockerId, pkg.getPackageId(), phone, true);
        return true;
    }

    public List<Package> processReturns() {
        List<Package> returnedPackages = new ArrayList<>();

        for (Locker locker : lockers) {
            if (locker.isExpired(MAX_RETENTION_HOURS)) {
                Package pkg = locker.removePackage();
                if (pkg != null) {
                    returnedPackages.add(pkg);
                    packageToLockerMap.remove(pkg.getPackageId());
                    System.out.println("🔄 Package " + pkg.getPackageId() +
                                     " returned to sender (not picked up within " +
                                     MAX_RETENTION_HOURS + " hours)");
                }
            }
        }

        return returnedPackages;
    }

    public void printLockerStatus() {
        System.out.println("\n📊 LOCKER STATUS:");
        System.out.println("─".repeat(60));

        Map<LockerSize, Integer> availableCount = new EnumMap<>(LockerSize.class);
        Map<LockerSize, Integer> occupiedCount = new EnumMap<>(LockerSize.class);

        for (LockerSize size : LockerSize.values()) {
            availableCount.put(size, 0);
            occupiedCount.put(size, 0);
        }

        for (Locker locker : lockers) {
            if (locker.getStatus() == LockerStatus.AVAILABLE) {
                availableCount.put(locker.getSize(), availableCount.get(locker.getSize()) + 1);
            } else if (locker.getStatus() == LockerStatus.OCCUPIED) {
                occupiedCount.put(locker.getSize(), occupiedCount.get(locker.getSize()) + 1);
            }
        }

        for (LockerSize size : LockerSize.values()) {
            int total = availableCount.get(size) + occupiedCount.get(size);
            System.out.println(String.format("%-12s: %d available, %d occupied (Total: %d)",
                size, availableCount.get(size), occupiedCount.get(size), total));
        }
        System.out.println("─".repeat(60) + "\n");
    }

    private Locker findLockerById(String lockerId) {
        for (Locker locker : lockers) {
            if (locker.getLockerId().equals(lockerId)) {
                return locker;
            }
        }
        return null;
    }

    private void recordPickup(String lockerId, String packageId, String phone, boolean success) {
        pickupHistory.add(new Pickup(lockerId, packageId, phone, success));
    }

    public List<Locker> getLockers() {
        return lockers;
    }

    public List<Pickup> getPickupHistory() {
        return pickupHistory;
    }
}
