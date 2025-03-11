package ru.functions.trigonometric.csc;

import ru.functions.trigonometric.sin.SinFunction;
import ru.functions.utils.MathUtils;

/**
 * Implementation of cosecant function using sine function and the identity
 * csc(x) = 1/sin(x)
 */
public class CscFunction implements CscFunctionInterface {
    private final SinFunction sinFunction;

    public CscFunction() {
        this.sinFunction = new SinFunction();
    }

    public CscFunction(SinFunction sinFunction) {
        this.sinFunction = sinFunction;
    }

    @Override
    public double calculate(double x, double epsilon) throws IllegalArgumentException {
        if (!isInDomain(x)) {
            throw new IllegalArgumentException("Input value is outside the domain of cosecant function");
        }

        double sinValue = sinFunction.calculate(x, epsilon);

        // Avoid division by zero
        if (Math.abs(sinValue) < epsilon) {
            throw new IllegalArgumentException("Cosecant is undefined at x = " + x + " (sin(x) ≈ 0)");
        }

        return 1.0 / sinValue;
    }

    @Override
    public boolean isInDomain(double x) {
        // Csc(x) is defined for all x where sin(x) ≠ 0
        // This means x ≠ nπ for integer n
        double normalizedX = MathUtils.normalizeAngle(x);

        double tolerance = 1e-10;

        if (Math.abs(normalizedX) < tolerance) {
            return false;
        }

        if (Math.abs(Math.abs(normalizedX) - Math.PI) < tolerance) {
            return false;
        }

        try {
            double sinValue = sinFunction.calculate(x, tolerance);
            return Math.abs(sinValue) > tolerance;
        } catch (Exception e) {
            return false;
        }
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
    public CscFunctionInterface getDerivative() {
        return new NegativeCscCotProductAdapter();
    }

    /**
     * Gets the sine function that this cosecant function depends on
     *
     * @return the sine function
     */
    public SinFunction getSinFunction() {
        return sinFunction;
    }

    /**
     * Helper class to represent the negative product of cosecant and cotangent
     * functions
     */
    private class NegativeCscCotProductAdapter implements CscFunctionInterface {
        @Override
        public double calculate(double x, double epsilon) throws IllegalArgumentException {
            if (!isInDomain(x)) {
                throw new IllegalArgumentException("Input value is outside the domain of -csc(x)cot(x)");
            }

            double sinValue = sinFunction.calculate(x, epsilon);
            double cosValue = sinFunction.getDerivative().calculate(x, epsilon); // cos(x)

            // Avoid division by zero
            if (Math.abs(sinValue) < epsilon) {
                throw new IllegalArgumentException("-csc(x)cot(x) is undefined at x = " + x + " (sin(x) ≈ 0)");
            }

            // -csc(x)cot(x) = -cos(x)/(sin(x)^2)
            return -cosValue / (sinValue * sinValue);
        }

        @Override
        public boolean isInDomain(double x) {
            return CscFunction.this.isInDomain(x);
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
        public CscFunctionInterface getDerivative() {
            return new CscDerivativeAdapter();
        }
    }

    /**
     * Helper class to represent the second derivative of cosecant
     */
    private class CscDerivativeAdapter implements CscFunctionInterface {
        @Override
        public double calculate(double x, double epsilon) throws IllegalArgumentException {
            if (!isInDomain(x)) {
                throw new IllegalArgumentException(
                        "Input value is outside the domain of the second derivative of csc(x)");
            }

            double sinValue = sinFunction.calculate(x, epsilon);
            double cosValue = sinFunction.getDerivative().calculate(x, epsilon); // cos(x)

            // Avoid division by zero
            if (Math.abs(sinValue) < epsilon) {
                throw new IllegalArgumentException(
                        "Second derivative of csc(x) is undefined at x = " + x + " (sin(x) ≈ 0)");
            }

            // -csc^3(x) - csc(x)cot^2(x) = -(1 + 2cos^2(x))/sin^3(x)
            return -(1 + 2 * cosValue * cosValue) / (sinValue * sinValue * sinValue);
        }

        @Override
        public boolean isInDomain(double x) {
            return CscFunction.this.isInDomain(x);
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
        public CscFunctionInterface getDerivative() {
            return null;
        }
    }
}
