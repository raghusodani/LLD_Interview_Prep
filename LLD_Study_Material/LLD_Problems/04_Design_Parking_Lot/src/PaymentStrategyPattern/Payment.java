package PaymentStrategyPattern;

/**
 * Represents a payment to be processed.
 */
public class Payment {
    private double amount;
    private PaymentStrategy paymentStrategy; // Payment strategy interface

    /**
     * Constructor to initialize the payment amount and payment strategy.
     * @param amount The amount of the payment.
     * @param paymentStrategy The strategy to be used for the payment.
     */
    public Payment(double amount, PaymentStrategy paymentStrategy) {
        this.amount = amount;
        this.paymentStrategy = paymentStrategy;
    }

    /**
     * Process the payment using the assigned strategy.
     */
    public void processPayment() {
        if (amount > 0) {
            paymentStrategy.processPayment(amount);  // Delegating to strategy
        } else {
            System.out.println("Invalid payment amount.");
        }
    }
}
