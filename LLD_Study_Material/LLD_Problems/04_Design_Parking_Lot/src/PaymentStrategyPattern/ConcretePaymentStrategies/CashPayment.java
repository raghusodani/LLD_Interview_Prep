package PaymentStrategyPattern.ConcretePaymentStrategies;

import PaymentStrategyPattern.PaymentStrategy;

/**
 * Represents a cash payment strategy.
 */
public class CashPayment implements PaymentStrategy {
    /**
     * Constructs a new CashPayment.
     * @param fee The fee to be paid.
     */
    public CashPayment(double fee) {
    }

    /**
     * Processes a cash payment.
     * @param amount The amount to be paid.
     */
    @Override
    public void processPayment(double amount) {
            System.out.println("Processing cash payment of $" + amount);
            // Logic for cash payment processing
    }
}
