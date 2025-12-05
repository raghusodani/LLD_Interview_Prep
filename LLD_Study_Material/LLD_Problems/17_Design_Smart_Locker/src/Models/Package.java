package Models;

import CommonEnum.LockerSize;

public class Package {
    private String packageId;
    private String recipientPhone;
    private LockerSize size;
    private String otp;
    private long deliveryTime;

    public Package(String packageId, String recipientPhone, LockerSize size) {
        this.packageId = packageId;
        this.recipientPhone = recipientPhone;
        this.size = size;
        this.deliveryTime = System.currentTimeMillis();
    }

    // Getters and Setters
    public String getPackageId() { return packageId; }
    public String getRecipientPhone() { return recipientPhone; }
    public LockerSize getSize() { return size; }
    public String getOtp() { return otp; }
    public void setOtp(String otp) { this.otp = otp; }
    public long getDeliveryTime() { return deliveryTime; }

    @Override
    public String toString() {
        return "Package{" +
                "id='" + packageId + '\'' +
                ", phone='" + recipientPhone + '\'' +
                ", size=" + size +
                ", otp='" + otp + '\'' +
                '}';
    }
}
