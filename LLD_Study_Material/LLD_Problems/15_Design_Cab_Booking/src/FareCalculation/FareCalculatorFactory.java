package FareCalculation;

public class FareCalculatorFactory {

    public enum FareType {
        BASE,
        SURGE,
        PREMIUM
    }

    public static FareStrategy getFareCalculator(FareType type, double surgeMultiplier) {
        switch (type) {
            case BASE:
                return new BaseFareStrategy();
            case SURGE:
                return new SurgeFareStrategy(surgeMultiplier);
            case PREMIUM:
                return new PremiumFareStrategy();
            default:
                return new BaseFareStrategy();
        }
    }

    public static FareStrategy getFareCalculator(FareType type) {
        return getFareCalculator(type, 1.0);
    }
}
