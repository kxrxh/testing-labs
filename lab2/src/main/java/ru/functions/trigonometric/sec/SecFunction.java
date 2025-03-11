package ru.functions.trigonometric.sec;

import ru.functions.trigonometric.cos.CosFunction;
import ru.functions.utils.MathUtils;

/**
 * Implementation of secant function using cosine function and the identity
 * sec(x) = 1/cos(x)
 */
public class SecFunction implements SecFunctionInterface {
    private final CosFunction cosFunction;

    public SecFunction() {
        this.cosFunction = new CosFunction();
    }

    public SecFunction(CosFunction cosFunction) {
        this.cosFunction = cosFunction;
    }

    @Override
    public double calculate(double x, double epsilon) throws IllegalArgumentException {
        if (!isInDomain(x)) {
            throw new IllegalArgumentException("Input value is outside the domain of secant function");
        }

        double cosValue = cosFunction.calculate(x, epsilon);

        // Avoid division by zero
        if (Math.abs(cosValue) < epsilon) {
            throw new IllegalArgumentException("Secant is undefined at x = " + x + " (cos(x) ≈ 0)");
        }

        return 1.0 / cosValue;
    }

    @Override
    public boolean isInDomain(double x) {
        // Sec(x) is defined for all x where cos(x) ≠ 0
        // This means x ≠ (2n+1)π/2 for integer n
        double normalizedX = MathUtils.normalizeAngle(x);
        double tolerance = 1e-10;

        if (Math.abs(Math.abs(normalizedX) - MathUtils.HALF_PI) < tolerance) {
            return false;
        }

        try {
            double cosValue = cosFunction.calculate(x, tolerance);
            return Math.abs(cosValue) > tolerance;
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
        return 0;
    }

    @Override
    public SecFunctionInterface getDerivative() {
        return new SecTanProductAdapter();
    }

    /**
     * Gets the cosine function that this secant function depends on
     *
     * @return the cosine function
     */
    public CosFunction getCosFunction() {
        return cosFunction;
    }

    /**
     * Helper class to represent the product of secant and tangent functions
     */
    private class SecTanProductAdapter implements SecFunctionInterface {
        @Override
        public double calculate(double x, double epsilon) throws IllegalArgumentException {
            if (!isInDomain(x)) {
                throw new IllegalArgumentException("Input value is outside the domain of sec(x)tan(x)");
            }

            double cosValue = cosFunction.calculate(x, epsilon);
            double sinValue = cosFunction.getSinFunction().calculate(x, epsilon);

            // Avoid division by zero
            if (Math.abs(cosValue) < epsilon) {
                throw new IllegalArgumentException("sec(x)tan(x) is undefined at x = " + x + " (cos(x) ≈ 0)");
            }

            // sec(x)tan(x) = sin(x)/(cos(x)^2)
            return sinValue / (cosValue * cosValue);
        }

        @Override
        public boolean isInDomain(double x) {
            return SecFunction.this.isInDomain(x);
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
        public SecFunctionInterface getDerivative() {
            return new SecDerivativeAdapter();
        }
    }

    /**
     * Helper class to represent the second derivative of secant
     */
    private class SecDerivativeAdapter implements SecFunctionInterface {
        @Override
        public double calculate(double x, double epsilon) throws IllegalArgumentException {
            if (!isInDomain(x)) {
                throw new IllegalArgumentException(
                        "Input value is outside the domain of the second derivative of sec(x)");
            }

            double cosValue = cosFunction.calculate(x, epsilon);
            double sinValue = cosFunction.getSinFunction().calculate(x, epsilon);

            // Avoid division by zero
            if (Math.abs(cosValue) < epsilon) {
                throw new IllegalArgumentException(
                        "Second derivative of sec(x) is undefined at x = " + x + " (cos(x) ≈ 0)");
            }

            // sec^3(x) + sec(x)tan^2(x) = (1 + 2sin^2(x))/cos^3(x)
            return (1 + 2 * sinValue * sinValue) / (cosValue * cosValue * cosValue);
        }

        @Override
        public boolean isInDomain(double x) {
            return SecFunction.this.isInDomain(x);
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
        public SecFunctionInterface getDerivative() {
            return null;
        }
    }
}
