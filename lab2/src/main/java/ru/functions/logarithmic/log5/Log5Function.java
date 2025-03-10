package ru.functions.logarithmic.log5;

import ru.functions.logarithmic.ln.LnFunction;
import ru.functions.utils.MathUtils;

/**
 * Implementation of logarithm base 5 using natural logarithm and the identity
 * log_5(x) = ln(x) / ln(5)
 */
public class Log5Function implements Log5FunctionInterface {
    private final LnFunction lnFunction;
    private static final double LN_5 = 1.6094379124341003; // ln(5)

    public Log5Function() {
        this.lnFunction = new LnFunction();
    }

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
    public Log5FunctionInterface changeBase(double newBase) {
        if (newBase <= 0 || newBase == 1) {
            throw new IllegalArgumentException("Logarithm base must be positive and not equal to 1");
        }

        if (MathUtils.areEqual(newBase, 5.0, 1e-10)) {
            return this; // Already base 5
        }

        // Create a new logarithm with the specified base
        return new LogarithmWithBase(this, newBase);
    }

    /**
     * Helper class to represent a logarithm with a different base
     */
    private static class LogarithmWithBase implements Log5FunctionInterface {
        private final Log5FunctionInterface baseLogFunction;
        private final double base;
        private final double baseLogarithm; // log_5(base)

        public LogarithmWithBase(Log5FunctionInterface baseLogFunction, double base) {
            this.baseLogFunction = baseLogFunction;
            this.base = base;
            this.baseLogarithm = baseLogFunction.calculate(base, 1e-15);
        }

        @Override
        public double calculate(double x, double epsilon) throws IllegalArgumentException {
            // log_b(x) = log_5(x) / log_5(b)
            double log5X = baseLogFunction.calculate(x, epsilon * baseLogarithm);
            return log5X / baseLogarithm;
        }

        @Override
        public boolean isInDomain(double x) {
            return baseLogFunction.isInDomain(x);
        }

        @Override
        public double getBase() {
            return base;
        }

        @Override
        public Log5FunctionInterface changeBase(double newBase) {
            if (MathUtils.areEqual(newBase, base, 1e-10)) {
                return this;
            }
            return baseLogFunction.changeBase(newBase);
        }
    }
}
