package ru.functions.logarithmic;

/**
 * Implementation of logarithm base 10 using natural logarithm and the identity
 * log_10(x) = ln(x) / ln(10)
 */
public class Log10Function implements LogarithmicFunction {
    private final LnFunction lnFunction;
    private static final double LN_10 = 2.302585092994046; // ln(10)

    public Log10Function(LnFunction lnFunction) {
        this.lnFunction = lnFunction;
    }

    @Override
    public double calculate(double x, double epsilon) throws IllegalArgumentException {
        if (!isInDomain(x)) {
            throw new IllegalArgumentException("Input value " + x + " is outside the domain of log base 10");
        }

        // log_10(x) = ln(x) / ln(10)
        // Using the precomputed value of ln(10) for efficiency
        return lnFunction.calculate(x, epsilon * LN_10) / LN_10;
    }

    @Override
    public boolean isInDomain(double x) {
        return lnFunction.isInDomain(x);
    }

    @Override
    public double getBase() {
        return 10.0;
    }

    @Override
    public LogarithmicFunction changeBase(double newBase) {
        if (newBase == 10.0) {
            return this;
        }
        return lnFunction.changeBase(newBase);
    }
}
