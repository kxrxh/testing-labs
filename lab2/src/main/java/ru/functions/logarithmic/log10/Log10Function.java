package ru.functions.logarithmic.log10;

import ru.functions.logarithmic.ln.LnFunction;
import ru.functions.utils.MathUtils;

/**
 * Implementation of logarithm base 10 using natural logarithm and the identity
 * log_10(x) = ln(x) / ln(10)
 */
public class Log10Function implements Log10FunctionInterface {
    private final LnFunction lnFunction;
    private static final double LN_10 = 2.302585092994046; // ln(10)

    public Log10Function() {
        this.lnFunction = new LnFunction();
    }

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
    public Log10FunctionInterface changeBase(double newBase) {
        if (newBase <= 0 || newBase == 1) {
            throw new IllegalArgumentException("Logarithm base must be positive and not equal to 1");
        }

        if (MathUtils.areEqual(newBase, 10.0, 1e-10)) {
            return this; // Already base 10
        }

        // Create a new logarithm with the specified base
        return new LogarithmWithBase(this, newBase);
    }

    /**
     * Helper class to represent a logarithm with a different base
     */
    private static class LogarithmWithBase implements Log10FunctionInterface {
        private final Log10FunctionInterface baseLogFunction;
        private final double base;
        private final double baseLogarithm; // log_10(base)

        public LogarithmWithBase(Log10FunctionInterface baseLogFunction, double base) {
            this.baseLogFunction = baseLogFunction;
            this.base = base;
            this.baseLogarithm = baseLogFunction.calculate(base, 1e-15);
        }

        @Override
        public double calculate(double x, double epsilon) throws IllegalArgumentException {
            // log_b(x) = log_10(x) / log_10(b)
            double log10X = baseLogFunction.calculate(x, epsilon * baseLogarithm);
            return log10X / baseLogarithm;
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
        public Log10FunctionInterface changeBase(double newBase) {
            if (MathUtils.areEqual(newBase, base, 1e-10)) {
                return this;
            }
            return baseLogFunction.changeBase(newBase);
        }
    }
}
