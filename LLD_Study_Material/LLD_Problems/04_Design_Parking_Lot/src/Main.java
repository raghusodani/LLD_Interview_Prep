import CommonEnum.DurationType;
import FareStrategyPattern.ConcreteStrategies.BasicHourlyRateStrategy;
import FareStrategyPattern.ConcreteStrategies.PremiumRateStrategy;
import FareStrategyPattern.ParkingFeeStrategy;
import ParkingLotController.ParkingLot;
import ParkingSpots.ConcreteParkingSpots.BikeParkingSpot;
import ParkingSpots.ConcreteParkingSpots.CarParkingSpot;
import ParkingSpots.ParkingSpot;
import PaymentStrategyPattern.ConcretePaymentStrategies.CashPayment;
import PaymentStrategyPattern.ConcretePaymentStrategies.CreditCardPayment;
import PaymentStrategyPattern.PaymentStrategy;
import VehicleFactoryPattern.Vehicle;
import VehicleFactoryPattern.VehicleFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * The main class to run the parking lot simulation.
 */
public class Main {
    /**
     * The main method to run the parking lot simulation.
     * @param args Command line arguments (not used).
     */
    public static void main(String[] args) {
        // Initialize parking spots
        List<ParkingSpot> parkingSpots = new ArrayList<>();
        parkingSpots.add(new CarParkingSpot(1, "Car"));
        parkingSpots.add(new CarParkingSpot(2,"Car"));
        parkingSpots.add(new BikeParkingSpot(3, "Bike"));
        parkingSpots.add(new BikeParkingSpot(4, "Bike"));
        // Initialize parking lot
        ParkingLot parkingLot = new ParkingLot(parkingSpots);
        // Create fee strategies
        ParkingFeeStrategy basicHourlyRateStrategy = new BasicHourlyRateStrategy();
        ParkingFeeStrategy premiumRateStrategy = new PremiumRateStrategy();
        // Create vehicles using Factory Pattern with fee strategies
        Vehicle car1 = VehicleFactory.createVehicle("Car", "CAR123", basicHourlyRateStrategy);
        Vehicle car2 = VehicleFactory.createVehicle("Car", "CAR345", basicHourlyRateStrategy);

        Vehicle bike1 = VehicleFactory.createVehicle("Bike", "BIKE456", premiumRateStrategy);
        Vehicle bike2 = VehicleFactory.createVehicle("Bike", "BIKE123", premiumRateStrategy);


        // Park vehicles
        ParkingSpot carSpot = parkingLot.parkVehicle(car1);
        ParkingSpot bikeSpot = parkingLot.parkVehicle(bike1);

        ParkingSpot carSpot2 = parkingLot.parkVehicle(car2);
        ParkingSpot bikeSpot2 = parkingLot.parkVehicle(bike2);

        Scanner scanner = new Scanner(System.in);
        System.out.println("Select payment method for your vehicle:");
        System.out.println("1. Credit Card");
        System.out.println("2. Cash");
        int paymentMethod = scanner.nextInt();
        // Process payments using Strategy Patterns
        if (carSpot != null) {
            // Calculate fee using the specific strategy for the vehicle
            double carFee = car1.calculateFee(2, DurationType.HOURS);
            PaymentStrategy carPaymentStrategy =
                    getPaymentStrategy(paymentMethod, carFee);
            carPaymentStrategy.processPayment(carFee);
            parkingLot.vacateSpot(carSpot, car1);
        }
        if (bikeSpot != null) {
            // Calculate fee using the specific strategy for the vehicle
            double bikeFee = bike1.calculateFee(3, DurationType.HOURS);
            PaymentStrategy bikePaymentStrategy =
                    getPaymentStrategy(paymentMethod, bikeFee);
            bikePaymentStrategy.processPayment(bikeFee);
            parkingLot.vacateSpot(bikeSpot, bike1);
        }
        scanner.close();
    }

    /**
     * Gets the payment strategy based on the user's choice.
     * @param paymentMethod The payment method chosen by the user.
     * @param fee The fee to be paid.
     * @return The payment strategy.
     */
    private static PaymentStrategy getPaymentStrategy(
            int paymentMethod, double fee) {
        switch (paymentMethod) {
            case 1:
                return new CreditCardPayment(fee);
            case 2:
                return new CashPayment(fee);
            default:
                System.out.println("Invalid choice! Default to Credit card payment.");
                return new CreditCardPayment(fee);
        }
    }
    }