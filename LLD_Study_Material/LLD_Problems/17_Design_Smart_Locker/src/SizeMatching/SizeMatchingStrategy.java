package SizeMatching;

import CommonEnum.LockerSize;
import CommonEnum.LockerStatus;
import Models.Locker;
import java.util.List;

public abstract class SizeMatchingStrategy {

    /**
     * Find suitable locker for package
     * Chain of Responsibility pattern:
     * 1. Try exact size match
     * 2. If not available, try next larger size
     * 3. Continue until found or no options
     */
    public Locker findSuitableLocker(List<Locker> lockers, LockerSize packageSize) {
        // Try exact match first
        Locker exactMatch = findExactMatch(lockers, packageSize);
        if (exactMatch != null) {
            return exactMatch;
        }

        // Try larger sizes (Chain of Responsibility)
        return findNextLargerSize(lockers, packageSize);
    }

    private Locker findExactMatch(List<Locker> lockers, LockerSize packageSize) {
        for (Locker locker : lockers) {
            if (locker.getStatus() == LockerStatus.AVAILABLE &&
                locker.getSize() == packageSize) {
                return locker;
            }
        }
        return null;
    }

    private Locker findNextLargerSize(List<Locker> lockers, LockerSize packageSize) {
        // Try sizes in order: packageSize+1, packageSize+2, etc.
        LockerSize[] allSizes = LockerSize.values();
        int startIndex = packageSize.ordinal() + 1;

        for (int i = startIndex; i < allSizes.length; i++) {
            LockerSize trySize = allSizes[i];
            for (Locker locker : lockers) {
                if (locker.getStatus() == LockerStatus.AVAILABLE &&
                    locker.getSize() == trySize) {
                    return locker;
                }
            }
        }

        return null; // No suitable locker found
    }
}
