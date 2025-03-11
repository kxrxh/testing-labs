package ru.functions.logarithmic.log5;

import ru.functions.logarithmic.ln.LnFunction;
import ru.functions.utils.MathUtils;

/**
 * Implementation of logarithm base 5 using natural logarithm and the identity
 * log_5(x) = ln(x) / ln(5)
 */
public class Log5Function implements Log5FunctionInterface {
    private final LnFunction lnFunction;
    // More precise value of ln(5)
    private static final double LN_5 = 1.6094379124341003746007593332261876395256013542685177219426;

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

        // Special cases for exact powers of 5
        if (isPowerOfFive(x)) {
            return getExactLog5(x);
        }

        // log_5(x) = ln(x) / ln(5)
        // Use a tighter epsilon for ln(x) calculation
        double result = lnFunction.calculate(x, epsilon / 10) / LN_5;

        // For values that should produce integer results (powers of 5)
        // round to the nearest integer if we're very close
        double rounded = Math.round(result);
        if (Math.abs(result - rounded) < epsilon && isPowerOfFive(Math.pow(5, rounded))) {
            return rounded;
        }

        return result;
    }

    // Helper method to check if a number is a power of 5
    private boolean isPowerOfFive(double x) {
        if (x <= 0)
            return false;

        // Check exact powers of 5 up to 5^13 (representable in double)
        if (x == 1.0 || x == 5.0 || x == 25.0 || x == 125.0 ||
                x == 625.0 || x == 3125.0 || x == 15625.0 ||
                x == 78125.0 || x == 390625.0 || x == 1953125.0 ||
                x == 9765625.0 || x == 48828125.0 || x == 244140625.0 ||
                x == 1220703125.0) {
            return true;
        }

        return false;
    }

    // For exact powers of 5, return the exact logarithm
    private double getExactLog5(double x) {
        if (x == 1.0)
            return 0.0;
        if (x == 5.0)
            return 1.0;
        if (x == 25.0)
            return 2.0;
        if (x == 125.0)
            return 3.0;
        if (x == 625.0)
            return 4.0;
        if (x == 3125.0)
            return 5.0;
        if (x == 15625.0)
            return 6.0;
        if (x == 78125.0)
            return 7.0;
        if (x == 390625.0)
            return 8.0;
        if (x == 1953125.0)
            return 9.0;
        if (x == 9765625.0)
            return 10.0;
        if (x == 48828125.0)
            return 11.0;
        if (x == 244140625.0)
            return 12.0;
        if (x == 1220703125.0)
            return 13.0;

        // Calculate manually if we somehow got a different power of 5
        double temp = x;
        int exponent = 0;

        // Scale down until we have a value between 1 and 5
        while (temp >= 5.0) {
            temp /= 5.0;
            exponent++;
        }

        // Scale up if less than 1
        while (temp < 1.0 && temp > 0.0) {
            temp *= 5.0;
            exponent--;
        }

        // If we have exactly 1.0, we have a power of 5
        if (Math.abs(temp - 1.0) < 1e-10) {
            return exponent;
        }

        // If we get here, use ln method with high precision
        return lnFunction.calculate(x, 1e-15) / LN_5;
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
