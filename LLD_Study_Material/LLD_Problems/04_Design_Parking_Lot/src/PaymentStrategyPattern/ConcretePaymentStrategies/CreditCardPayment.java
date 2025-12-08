package PaymentStrategyPattern.ConcretePaymentStrategies;

import PaymentStrategyPattern.PaymentStrategy;

/**
 * Represents a credit card payment strategy.
 */
public class CreditCardPayment implements PaymentStrategy {
    /**
     * Constructs a new CreditCardPayment.
     * @param fee The fee to be paid.
     */
    public CreditCardPayment(double fee) {
    }

    /**
     * Processes a credit card payment.
     * @param amount The amount to be paid.
     */
    @Override
    public void processPayment(double amount) {
        System.out.println("Processing credit card payment of $" + amount);
    }
}
