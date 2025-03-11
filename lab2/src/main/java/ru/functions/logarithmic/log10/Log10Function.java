package ru.functions.logarithmic.log10;

import ru.functions.logarithmic.ln.LnFunction;
import ru.functions.utils.MathUtils;

/**
 * Implementation of logarithm base 10 using natural logarithm and the identity
 * log_10(x) = ln(x) / ln(10)
 */
public class Log10Function implements Log10FunctionInterface {
    private final LnFunction lnFunction;

    private static final double LN_10 = 2.302585092994045684017991454684364207601101488628772976033;

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

        if (isPowerOfTen(x)) {
            return getExactLog10(x);
        }

        // log_10(x) = ln(x) / ln(10)
        double result = lnFunction.calculate(x, epsilon / 10) / LN_10;

        double rounded = Math.round(result);
        if (Math.abs(result - rounded) < epsilon && isPowerOfTen(Math.pow(10, rounded))) {
            return rounded;
        }

        return result;
    }

    private boolean isPowerOfTen(double x) {
        if (x <= 0)
            return false;

        if (x == 1.0 || x == 10.0 || x == 100.0 || x == 1000.0 ||
                x == 10000.0 || x == 100000.0 || x == 1000000.0 ||
                x == 10000000.0 || x == 100000000.0 || x == 1000000000.0 ||
                x == 10000000000.0 || x == 100000000000.0 || x == 1000000000000.0 ||
                x == 10000000000000.0 || x == 100000000000000.0 || x == 1000000000000000.0) {
            return true;
        }

        return false;
    }

    private double getExactLog10(double x) {
        if (x == 1.0)
            return 0.0;
        if (x == 10.0)
            return 1.0;
        if (x == 100.0)
            return 2.0;
        if (x == 1000.0)
            return 3.0;
        if (x == 10000.0)
            return 4.0;
        if (x == 100000.0)
            return 5.0;
        if (x == 1000000.0)
            return 6.0;
        if (x == 10000000.0)
            return 7.0;
        if (x == 100000000.0)
            return 8.0;
        if (x == 1000000000.0)
            return 9.0;
        if (x == 10000000000.0)
            return 10.0;
        if (x == 100000000000.0)
            return 11.0;
        if (x == 1000000000000.0)
            return 12.0;
        if (x == 10000000000000.0)
            return 13.0;
        if (x == 100000000000000.0)
            return 14.0;
        if (x == 1000000000000000.0)
            return 15.0;

        double temp = x;
        int exponent = 0;

        // Scale down until we have a value between 1 and 10
        while (temp >= 10.0) {
            temp /= 10.0;
            exponent++;
        }

        // Scale up if less than 1
        while (temp < 1.0 && temp > 0.0) {
            temp *= 10.0;
            exponent--;
        }

        // If we have exactly 1.0, we have a power of 10
        if (Math.abs(temp - 1.0) < 1e-10) {
            return exponent;
        }

        return lnFunction.calculate(x, 1e-15) / LN_10;
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
            return this;
        }

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
