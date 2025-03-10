package ru.functions.logarithmic.ln;

import ru.functions.utils.MathUtils;

/**
 * Implementation of natural logarithm function using Taylor series expansion
 */
public class LnFunction implements LnFunctionInterface {

    // Base of natural logarithm (e)
    private static final double E = 2.718281828459045;

    @Override
    public double calculate(double x, double epsilon) throws IllegalArgumentException {
        if (!isInDomain(x)) {
            throw new IllegalArgumentException(
                    "Input value " + x + " is outside the domain of natural logarithm function");
        }

        // For x close to 1, we use Taylor series expansion for ln(1+y)
        // For other values, we use the identity ln(x) = ln((1+y)/(1-y)) = 2*artanh(y)
        // where y = (x-1)/(x+1) and artanh(y) has a simple Taylor expansion

        if (MathUtils.areEqual(x, 1.0, epsilon)) {
            return 0.0;
        }

        if (x > 0.5 && x < 1.5) {
            // For x close to 1, use Taylor series for ln(1+y) where y = x-1
            double y = x - 1.0;
            double result = 0.0;
            double term = y;
            int n = 1;

            // Taylor series for ln(1+y): y - y^2/2 + y^3/3 - y^4/4 + ...
            while (Math.abs(term) > epsilon) {
                result += term;
                n++;
                term = -term * y * (n - 1) / n;
            }

            return result;
        } else {
            // For other values, use the identity ln(x) = 2*artanh((x-1)/(x+1))
            double y = (x - 1.0) / (x + 1.0);
            double result = 0.0;
            double term = y;
            int n = 0;

            // Taylor series for artanh(y): y + y^3/3 + y^5/5 + ...
            while (Math.abs(term) > epsilon / 2) { // epsilon/2 because we multiply by 2 at the end
                result += term;
                n++;
                term = term * y * y * (2 * n - 1) / (2 * n + 1);
            }

            return 2.0 * result;
        }
    }

    @Override
    public boolean isInDomain(double x) {
        // ln(x) is defined for x > 0
        return x > 0;
    }

    @Override
    public double getBase() {
        return E; // Base of natural logarithm is e
    }

    @Override
    public LnFunctionInterface changeBase(double newBase) {
        if (newBase <= 0 || newBase == 1) {
            throw new IllegalArgumentException("Logarithm base must be positive and not equal to 1");
        }

        if (MathUtils.areEqual(newBase, E, 1e-10)) {
            return this; // Already natural logarithm
        }

        // Create a new logarithm with the specified base
        return new LogarithmWithBase(this, newBase);
    }

    /**
     * Helper class to represent a logarithm with a different base
     */
    private static class LogarithmWithBase implements LnFunctionInterface {
        private final LnFunctionInterface baseLogFunction;
        private final double base;
        private final double baseLogarithm; // ln(base)

        public LogarithmWithBase(LnFunctionInterface baseLogFunction, double base) {
            this.baseLogFunction = baseLogFunction;
            this.base = base;
            this.baseLogarithm = baseLogFunction.calculate(base, 1e-15);
        }

        @Override
        public double calculate(double x, double epsilon) throws IllegalArgumentException {
            // log_b(x) = ln(x) / ln(b)
            double lnX = baseLogFunction.calculate(x, epsilon * baseLogarithm);
            return lnX / baseLogarithm;
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
        public LnFunctionInterface changeBase(double newBase) {
            if (MathUtils.areEqual(newBase, base, 1e-10)) {
                return this;
            }
            return baseLogFunction.changeBase(newBase);
        }
    }
}
