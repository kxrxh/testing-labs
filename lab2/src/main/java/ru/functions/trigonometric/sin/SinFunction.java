package ru.functions.trigonometric.sin;

import ru.functions.utils.MathUtils;
import ru.functions.utils.Function;

/**
 * Implementation of sine function using Taylor series expansion
 */
public class SinFunction implements SinFunctionInterface {

    @Override
    public double calculate(double x, double epsilon) throws IllegalArgumentException {
        if (!isInDomain(x)) {
            throw new IllegalArgumentException("Input value is outside the domain of sine function");
        }

        // Normalize x to [-PI, PI] to improve convergence
        x = MathUtils.normalizeAngle(x);

        double result = 0.0;
        double term = x;
        int n = 1;

        // Taylor series for sin(x): x - x^3/3! + x^5/5! - x^7/7! + ...
        while (Math.abs(term) > epsilon) {
            result += term;

            // Calculate next term
            term = -term * x * x / ((2 * n) * (2 * n + 1));
            n++;
        }

        return result;
    }

    @Override
    public boolean isInDomain(double x) {
        // Sin(x) is defined for all real numbers
        return true;
    }

    @Override
    public double getPeriod() {
        return MathUtils.TWO_PI; // 2π
    }

    @Override
    public int getParity() {
        return 1; // Odd function: sin(-x) = -sin(x)
    }

    @Override
    public SinFunctionInterface getDerivative() {
        return new CosFunctionAdapter(); // The derivative of sin(x) is cos(x)
    }

    // An adapter to represent cos(x) in terms of the sin package interface
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
            return 0; // Even function
        }

        @Override
        public SinFunctionInterface getDerivative() {
            return new NegativeSinAdapter(); // Derivative of cos(x) is -sin(x)
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
            return 1; // Still odd function
        }

        @Override
        public SinFunctionInterface getDerivative() {
            return new NegativeCosFunctionAdapter(); // Derivative of -sin(x) is -cos(x)
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
            return 0; // Still even function
        }

        @Override
        public SinFunctionInterface getDerivative() {
            return new SinFunction(); // Derivative of -cos(x) is sin(x)
        }
    }
}
