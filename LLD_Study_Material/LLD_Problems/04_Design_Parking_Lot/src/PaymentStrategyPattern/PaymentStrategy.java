package PaymentStrategyPattern;

/**
 * Interface for payment strategies.
 */
public interface PaymentStrategy {
    /**
     * Processes a payment.
     * @param amount The amount to be paid.
     */
    void processPayment(double amount);
}
