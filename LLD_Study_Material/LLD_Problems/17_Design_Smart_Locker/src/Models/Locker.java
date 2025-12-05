package Models;

import CommonEnum.LockerSize;
import CommonEnum.LockerStatus;

public class Locker {
    private String lockerId;
    private LockerSize size;
    private LockerStatus status;
    private Package currentPackage;
    private long occupiedSince;

    public Locker(String lockerId, LockerSize size) {
        this.lockerId = lockerId;
        this.size = size;
        this.status = LockerStatus.AVAILABLE;
        this.currentPackage = null;
        this.occupiedSince = 0;
    }

    public boolean assignPackage(Package pkg) {
        if (status != LockerStatus.AVAILABLE) {
            return false;
        }
        if (!size.canFit(pkg.getSize())) {
            return false;
        }
        this.currentPackage = pkg;
        this.status = LockerStatus.OCCUPIED;
        this.occupiedSince = System.currentTimeMillis();
        return true;
    }

    public Package removePackage() {
        Package pkg = this.currentPackage;
        this.currentPackage = null;
        this.status = LockerStatus.AVAILABLE;
        this.occupiedSince = 0;
        return pkg;
    }

    public boolean isExpired(long maxRetentionHours) {
        if (status != LockerStatus.OCCUPIED || currentPackage == null) {
            return false;
        }
        long currentTime = System.currentTimeMillis();
        long hoursOccupied = (currentTime - occupiedSince) / (1000 * 60 * 60);
        return hoursOccupied > maxRetentionHours;
    }

    // Getters
    public String getLockerId() { return lockerId; }
    public LockerSize getSize() { return size; }
    public LockerStatus getStatus() { return status; }
    public Package getCurrentPackage() { return currentPackage; }
    public long getOccupiedSince() { return occupiedSince; }

    public void setStatus(LockerStatus status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "Locker{" +
                "id='" + lockerId + '\'' +
                ", size=" + size +
                ", status=" + status +
                ", hasPackage=" + (currentPackage != null) +
                '}';
    }
}
