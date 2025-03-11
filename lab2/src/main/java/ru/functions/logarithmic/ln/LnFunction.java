package ru.functions.logarithmic.ln;

import ru.functions.utils.MathUtils;

public class LnFunction implements LnFunctionInterface {
    private static final double E = 2.718281828459045;

    /**
     * For x close to 1, we use Taylor series expansion for ln(1+y)
     *
     * For other values, we use the identity ln(x) = ln((1+y)/(1-y)) = 2*artanh(y)
     * where y = (x-1)/(x+1) and artanh(y) has a simple Taylor expansion
     */
    @Override
    public double calculate(double x, double epsilon) throws IllegalArgumentException {
        if (!isInDomain(x)) {
            throw new IllegalArgumentException(
                    "Input value " + x + " is outside the domain of natural logarithm function");
        }

        if (MathUtils.areEqual(x, 1.0, epsilon)) {
            return 0.0;
        }

        if (x < 1e-5) {
            return -calculate(1 / x, epsilon);
        }

        if (x > 1e5) {
            // Use ln(x) = ln(x/a) + ln(a) where a is a power of 2
            double a = 1.0;
            while (a * 2 < x) {
                a *= 2;
            }
            return calculate(x / a, epsilon) + calculate(a, epsilon);
        }

        if (x > 0.5 && x < 1.5) {
            double y = x - 1.0;
            double result = 0.0;
            double term = y;
            int n = 1;

            // ln(1+y): y - y^2/2 + y^3/3 - y^4/4 + ...
            while (Math.abs(term) > epsilon / 10) {
                result += term;
                term = -term * y * (n) / (n + 1);
                n++;

                if (n > 200)
                    break;
            }

            return result;
        } else {
            // ln(x) = 2*artanh((x-1)/(x+1))
            double y = (x - 1.0) / (x + 1.0);
            double result = 0.0;
            double term = y;
            int n = 0;

            // artanh(y): y + y^3/3 + y^5/5 + ...
            while (Math.abs(term) > epsilon / 20) {
                result += term;
                n++;
                term = term * y * y * (2 * n - 1) / (2 * n + 1);

                if (n > 200)
                    break;
            }

            return 2.0 * result;
        }
    }

    @Override
    public boolean isInDomain(double x) {
        return x > 0;
    }

    @Override
    public double getBase() {
        return E;
    }

    @Override
    public LnFunctionInterface changeBase(double newBase) {
        if (newBase <= 0 || newBase == 1) {
            throw new IllegalArgumentException("Logarithm base must be positive and not equal to 1");
        }

        if (MathUtils.areEqual(newBase, E, 1e-10)) {
            return this;
        }

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
