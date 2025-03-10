package ru.functions.trigonometric.sec;

import ru.functions.trigonometric.cos.CosFunctionStub;
import ru.functions.utils.MathUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * Stub implementation of secant function using predefined table values
 */
public class SecFunctionStub implements SecFunctionInterface {
    private final Map<Double, Double> secValues;
    private CosFunctionStub cosFunctionStub;

    public SecFunctionStub() {
        this.cosFunctionStub = new CosFunctionStub();
        secValues = new HashMap<>();

        // Special values for sec(x) at key angles
        secValues.put(0.0, 1.0);
        secValues.put(Math.PI / 6, 2.0 / Math.sqrt(3));
        secValues.put(Math.PI / 4, Math.sqrt(2));
        secValues.put(Math.PI / 3, 2.0);
        // secValues.put(Math.PI / 2, Double.POSITIVE_INFINITY); // Undefined
        secValues.put(2 * Math.PI / 3, -2.0);
        secValues.put(3 * Math.PI / 4, -Math.sqrt(2));
        secValues.put(5 * Math.PI / 6, -2.0 / Math.sqrt(3));
        secValues.put(Math.PI, -1.0);
        secValues.put(7 * Math.PI / 6, -2.0 / Math.sqrt(3));
        secValues.put(5 * Math.PI / 4, -Math.sqrt(2));
        secValues.put(4 * Math.PI / 3, -2.0);
        // secValues.put(3 * Math.PI / 2, Double.POSITIVE_INFINITY); // Undefined
        secValues.put(5 * Math.PI / 3, 2.0);
        secValues.put(7 * Math.PI / 4, Math.sqrt(2));
        secValues.put(11 * Math.PI / 6, 2.0 / Math.sqrt(3));
        secValues.put(2 * Math.PI, 1.0);

        // Add more values for better coverage
        for (double x = -2 * Math.PI; x <= 2 * Math.PI; x += Math.PI / 12) {
            double roundedX = Math.round(x * 1000.0) / 1000.0; // Round to 3 decimal places

            // Skip values where cos(x) is close to 0
            if (!isInDomain(roundedX)) {
                continue;
            }

            if (!secValues.containsKey(roundedX)) {
                double cosValue = Math.cos(roundedX);
                if (Math.abs(cosValue) > 1e-10) {
                    secValues.put(roundedX, 1.0 / cosValue);
                }
            }
        }
    }

    public SecFunctionStub(CosFunctionStub cosFunctionStub) {
        this();
        this.cosFunctionStub = cosFunctionStub;
    }

    @Override
    public double calculate(double x, double epsilon) throws IllegalArgumentException {
        if (!isInDomain(x)) {
            throw new IllegalArgumentException("Input value is outside the domain of secant function");
        }

        // Normalize to [-2π, 2π] range
        x = MathUtils.normalizeAngle(x);

        // Round to 3 decimal places for table lookup
        double roundedX = Math.round(x * 1000.0) / 1000.0;

        // Check if we have an exact value in our table
        if (secValues.containsKey(roundedX)) {
            return secValues.get(roundedX);
        }

        // Use the cosine function stub to calculate sec(x) = 1/cos(x)
        double cosValue = cosFunctionStub.calculate(roundedX, epsilon);

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
        return Math.abs(Math.abs(normalizedX) - MathUtils.HALF_PI) > 1e-10 &&
                Math.abs(Math.abs(normalizedX) - 3 * MathUtils.HALF_PI) > 1e-10;
    }

    @Override
    public double getPeriod() {
        return MathUtils.TWO_PI; // 2π
    }

    @Override
    public int getParity() {
        return 0; // Even function: sec(-x) = sec(x)
    }

    @Override
    public SecFunctionInterface getDerivative() {
        // The derivative of sec(x) is sec(x)tan(x)
        return new SecTanProductStubAdapter();
    }

    /**
     * Gets the cosine function stub that this secant function depends on
     *
     * @return the cosine function stub
     */
    public CosFunctionStub getCosFunctionStub() {
        return cosFunctionStub;
    }

    /**
     * Helper class to represent the product of secant and tangent functions
     */
    private class SecTanProductStubAdapter implements SecFunctionInterface {
        @Override
        public double calculate(double x, double epsilon) throws IllegalArgumentException {
            if (!isInDomain(x)) {
                throw new IllegalArgumentException("Input value is outside the domain of sec(x)tan(x)");
            }

            double cosValue = cosFunctionStub.calculate(x, epsilon);
            double sinValue = cosFunctionStub.getSinFunctionStub().calculate(x, epsilon);

            // Avoid division by zero
            if (Math.abs(cosValue) < epsilon) {
                throw new IllegalArgumentException("sec(x)tan(x) is undefined at x = " + x + " (cos(x) ≈ 0)");
            }

            // sec(x)tan(x) = sin(x)/(cos(x)^2)
            return sinValue / (cosValue * cosValue);
        }

        @Override
        public boolean isInDomain(double x) {
            return SecFunctionStub.this.isInDomain(x);
        }

        @Override
        public double getPeriod() {
            return MathUtils.TWO_PI;
        }

        @Override
        public int getParity() {
            return 1; // Odd function: sec(-x)tan(-x) = -sec(x)tan(x)
        }

        @Override
        public SecFunctionInterface getDerivative() {
            // For simplicity, we'll return null for the second derivative
            return null;
        }
    }
}
