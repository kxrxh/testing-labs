package ru.functions.trigonometric.sin;

import ru.functions.utils.MathUtils;

/**
 * Implementation of sine function using Taylor series expansion
 */
public class SinFunction implements SinFunctionInterface {
    
    @Override
    public double calculate(double x, double epsilon) throws IllegalArgumentException {
        if (!isInDomain(x)) {
            throw new IllegalArgumentException("Input value is outside the domain of sine function");
        }

        x = MathUtils.normalizeAngle(x);

        double adjustedEpsilon = epsilon;
        if (Math.abs(x) > 100) {
            adjustedEpsilon = epsilon / 100;
        }

        double result = 0.0;
        double term = x;
        int n = 1;

        // Taylor series for sin(x): x - x^3/3! + x^5/5! - x^7/7! + ...
        while (Math.abs(term) > adjustedEpsilon) {
            result += term;

            term = -term * x * x / ((2 * n) * (2 * n + 1));
            n++;

            if (n > 100)
                break;
        }

        return result;
    }

    @Override
    public boolean isInDomain(double x) {
        return !Double.isInfinite(x) && !Double.isNaN(x);
    }

    @Override
    public double getPeriod() {
        return MathUtils.TWO_PI;
    }

    @Override
    public int getParity() {
        return 1;
    }

    @Override
    public SinFunctionInterface getDerivative() {
        return new CosFunctionAdapter();
    }

    private static class CosFunctionAdapter implements SinFunctionInterface {
        @Override
        public double calculate(double x, double epsilon) throws IllegalArgumentException {
            // cos(x) = sin(x + π/2)
            return new SinFunction().calculate(x + MathUtils.HALF_PI, epsilon);
        }

        @Override
        public boolean isInDomain(double x) {
            return true;
        }

        @Override
        public double getPeriod() {
            return MathUtils.TWO_PI;
        }

        @Override
        public int getParity() {
            return 0;
        }

        @Override
        public SinFunctionInterface getDerivative() {
            return new NegativeSinAdapter();
        }
    }

    // An adapter for -sin(x)
    private static class NegativeSinAdapter implements SinFunctionInterface {
        @Override
        public double calculate(double x, double epsilon) throws IllegalArgumentException {
            return -new SinFunction().calculate(x, epsilon);
        }

        @Override
        public boolean isInDomain(double x) {
            return true;
        }

        @Override
        public double getPeriod() {
            return MathUtils.TWO_PI;
        }

        @Override
        public int getParity() {
            return 1;
        }

        @Override
        public SinFunctionInterface getDerivative() {
            return new NegativeCosFunctionAdapter();
        }
    }

    // An adapter for -cos(x)
    private static class NegativeCosFunctionAdapter implements SinFunctionInterface {
        @Override
        public double calculate(double x, double epsilon) throws IllegalArgumentException {
            return -new CosFunctionAdapter().calculate(x, epsilon);
        }

        @Override
        public boolean isInDomain(double x) {
            return true;
        }

        @Override
        public double getPeriod() {
            return MathUtils.TWO_PI;
        }

        @Override
        public int getParity() {
            return 0;
        }

        @Override
        public SinFunctionInterface getDerivative() {
            return new SinFunction();
        }
    }
}
