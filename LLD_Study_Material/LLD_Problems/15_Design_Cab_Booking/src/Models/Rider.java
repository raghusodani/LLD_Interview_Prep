package Models;

public class Rider {
    private String riderId;
    private String name;
    private String phone;
    private double rating;

    public Rider(String riderId, String name, String phone, double rating) {
        this.riderId = riderId;
        this.name = name;
        this.phone = phone;
        this.rating = rating;
    }

    public String getRiderId() {
        return riderId;
    }

    public String getName() {
        return name;
    }

    public String getPhone() {
        return phone;
    }

    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }

    @Override
    public String toString() {
        return String.format("Rider[%s, %s, Rating: %.1f]", riderId, name, rating);
    }
}
