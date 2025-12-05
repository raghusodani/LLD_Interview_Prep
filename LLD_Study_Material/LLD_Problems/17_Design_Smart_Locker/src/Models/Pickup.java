package Models;

public class Pickup {
    private String lockerId;
    private String packageId;
    private String recipientPhone;
    private long pickupTime;
    private boolean success;

    public Pickup(String lockerId, String packageId, String recipientPhone, boolean success) {
        this.lockerId = lockerId;
        this.packageId = packageId;
        this.recipientPhone = recipientPhone;
        this.pickupTime = System.currentTimeMillis();
        this.success = success;
    }

    // Getters
    public String getLockerId() { return lockerId; }
    public String getPackageId() { return packageId; }
    public String getRecipientPhone() { return recipientPhone; }
    public long getPickupTime() { return pickupTime; }
    public boolean isSuccess() { return success; }

    @Override
    public String toString() {
        return "Pickup{" +
                "locker='" + lockerId + '\'' +
                ", package='" + packageId + '\'' +
                ", phone='" + recipientPhone + '\'' +
                ", success=" + success +
                '}';
    }
}
