package ru.functions.logarithmic;

/**
 * Implementation of logarithm base 2 using natural logarithm and the identity
 * log_2(x) = ln(x) / ln(2)
 */
public class Log2Function implements LogarithmicFunction {
    private final LnFunction lnFunction;
    private static final double LN_2 = 0.6931471805599453; // ln(2)

    public Log2Function(LnFunction lnFunction) {
        this.lnFunction = lnFunction;
    }

    @Override
    public double calculate(double x, double epsilon) throws IllegalArgumentException {
        if (!isInDomain(x)) {
            throw new IllegalArgumentException("Input value " + x + " is outside the domain of log base 2");
        }

        // log_2(x) = ln(x) / ln(2)
        // Using the precomputed value of ln(2) for efficiency
        return lnFunction.calculate(x, epsilon * LN_2) / LN_2;
    }

    @Override
    public boolean isInDomain(double x) {
        return lnFunction.isInDomain(x);
    }

    @Override
    public double getBase() {
        return 2.0;
    }

    @Override
    public LogarithmicFunction changeBase(double newBase) {
        if (newBase == 2.0) {
            return this;
        }
        return lnFunction.changeBase(newBase);
    }
}
