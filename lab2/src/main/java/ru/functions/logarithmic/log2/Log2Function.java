package ru.functions.logarithmic.log2;

import ru.functions.logarithmic.ln.LnFunction;
import ru.functions.utils.MathUtils;

/**
 * Implementation of logarithm base 2 using natural logarithm and the identity
 * log_2(x) = ln(x) / ln(2)
 */
public class Log2Function implements Log2FunctionInterface {
    private final LnFunction lnFunction;

    private static final double LN_2 = 0.693147180559945309417232121458176568075500134360255254120;

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

        if (isPowerOfTwo(x)) {
            return getExactLog2(x);
        }

        // log_2(x) = ln(x) / ln(2)
        double result = lnFunction.calculate(x, epsilon / 10) / LN_2;

        double rounded = Math.round(result);
        if (Math.abs(result - rounded) < epsilon && isPowerOfTwo(Math.pow(2, rounded))) {
            return rounded;
        }

        return result;
    }

    private boolean isPowerOfTwo(double x) {
        if (x > 0 && x <= (1L << 53) && x == Math.round(x)) {
            long longX = (long) x;
            return (longX & (longX - 1)) == 0;
        }
        return false;
    }

    private double getExactLog2(double x) {
        if (x == 1.0)
            return 0.0;
        if (x == 2.0)
            return 1.0;
        if (x == 4.0)
            return 2.0;
        if (x == 8.0)
            return 3.0;
        if (x == 16.0)
            return 4.0;
        if (x == 32.0)
            return 5.0;
        if (x == 64.0)
            return 6.0;
        if (x == 128.0)
            return 7.0;
        if (x == 256.0)
            return 8.0;
        if (x == 512.0)
            return 9.0;
        if (x == 1024.0)
            return 10.0;

        long bits = Double.doubleToRawLongBits(x);
        int exponent = (int) ((bits >>> 52) & 0x7ffL) - 1023;
        return exponent;
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
            return this;
        }

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
