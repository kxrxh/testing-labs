package ru.functions.logarithmic;

/**
 * Implementation of logarithm base 5 using natural logarithm and the identity
 * log_5(x) = ln(x) / ln(5)
 */
public class Log5Function implements LogarithmicFunction {
    private final LnFunction lnFunction;
    private static final double LN_5 = 1.6094379124341003; // ln(5)

    public Log5Function(LnFunction lnFunction) {
        this.lnFunction = lnFunction;
    }

    @Override
    public double calculate(double x, double epsilon) throws IllegalArgumentException {
        if (!isInDomain(x)) {
            throw new IllegalArgumentException("Input value " + x + " is outside the domain of log base 5");
        }

        // log_5(x) = ln(x) / ln(5)
        // Using the precomputed value of ln(5) for efficiency
        return lnFunction.calculate(x, epsilon * LN_5) / LN_5;
    }

    @Override
    public boolean isInDomain(double x) {
        return lnFunction.isInDomain(x);
    }

    @Override
    public double getBase() {
        return 5.0;
    }

    @Override
    public LogarithmicFunction changeBase(double newBase) {
        if (newBase == 5.0) {
            return this;
        }
        return lnFunction.changeBase(newBase);
    }
}
