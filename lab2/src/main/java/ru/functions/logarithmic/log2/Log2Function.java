package ru.functions.logarithmic.log2;

import ru.functions.logarithmic.ln.LnFunction;
import ru.functions.utils.MathUtils;

/**
 * Implementation of logarithm base 2 using natural logarithm and the identity
 * log_2(x) = ln(x) / ln(2)
 */
public class Log2Function implements Log2FunctionInterface {
    private final LnFunction lnFunction;
    private static final double LN_2 = 0.6931471805599453; // ln(2)

    public Log2Function() {
        this.lnFunction = new LnFunction();
    }

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
    public Log2FunctionInterface changeBase(double newBase) {
        if (newBase <= 0 || newBase == 1) {
            throw new IllegalArgumentException("Logarithm base must be positive and not equal to 1");
        }

        if (MathUtils.areEqual(newBase, 2.0, 1e-10)) {
            return this; // Already base 2
        }

        // Create a new logarithm with the specified base
        return new LogarithmWithBase(this, newBase);
    }

    /**
     * Helper class to represent a logarithm with a different base
     */
    private static class LogarithmWithBase implements Log2FunctionInterface {
        private final Log2FunctionInterface baseLogFunction;
        private final double base;
        private final double baseLogarithm; // log_2(base)

        public LogarithmWithBase(Log2FunctionInterface baseLogFunction, double base) {
            this.baseLogFunction = baseLogFunction;
            this.base = base;
            this.baseLogarithm = baseLogFunction.calculate(base, 1e-15);
        }

        @Override
        public double calculate(double x, double epsilon) throws IllegalArgumentException {
            // log_b(x) = log_2(x) / log_2(b)
            double log2X = baseLogFunction.calculate(x, epsilon * baseLogarithm);
            return log2X / baseLogarithm;
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
        public Log2FunctionInterface changeBase(double newBase) {
            if (MathUtils.areEqual(newBase, base, 1e-10)) {
                return this;
            }
            return baseLogFunction.changeBase(newBase);
        }
    }
}
