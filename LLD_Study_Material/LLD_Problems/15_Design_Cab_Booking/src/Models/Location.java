package Models;

public class Location {
    private double latitude;
    private double longitude;

    public Location(double latitude, double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    // Calculate Euclidean distance to another location
    public double distanceTo(Location other) {
        double latDiff = this.latitude - other.latitude;
        double lonDiff = this.longitude - other.longitude;
        return Math.sqrt(latDiff * latDiff + lonDiff * lonDiff);
    }

    @Override
    public String toString() {
        return String.format("(%.2f, %.2f)", latitude, longitude);
    }
}
