package Security;

import Models.Package;

public class OTPValidator {
    private static final long OTP_VALIDITY_MS = 24 * 60 * 60 * 1000; // 24 hours

    public static boolean validateOTP(Package pkg, String inputOTP) {
        if (pkg == null || inputOTP == null) {
            return false;
        }

        // Check if OTP matches
        if (!inputOTP.equals(pkg.getOtp())) {
            return false;
        }

        // Check if OTP is still valid (within 24 hours)
        long currentTime = System.currentTimeMillis();
        long timeSinceDelivery = currentTime - pkg.getDeliveryTime();

        return timeSinceDelivery <= OTP_VALIDITY_MS;
    }

    public static boolean isExpired(Package pkg) {
        if (pkg == null) {
            return true;
        }
        long currentTime = System.currentTimeMillis();
        long timeSinceDelivery = currentTime - pkg.getDeliveryTime();
        return timeSinceDelivery > OTP_VALIDITY_MS;
    }
}
