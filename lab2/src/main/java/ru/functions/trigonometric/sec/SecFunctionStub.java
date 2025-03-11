package ru.functions.trigonometric.sec;

import ru.functions.trigonometric.cos.CosFunctionStub;
import ru.functions.utils.MathUtils;

import java.util.HashMap;
import java.util.Map;

public class SecFunctionStub implements SecFunctionInterface {
    private final Map<Double, Double> secValues;
    private CosFunctionStub cosFunctionStub;

    public SecFunctionStub() {
        this.cosFunctionStub = new CosFunctionStub();
        secValues = new HashMap<>();

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

        for (double x = -2 * Math.PI; x <= 2 * Math.PI; x += Math.PI / 12) {
            double roundedX = Math.round(x * 1000.0) / 1000.0;

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

        x = MathUtils.normalizeAngle(x);

        double roundedX = Math.round(x * 1000.0) / 1000.0;

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
        return MathUtils.TWO_PI;
    }

    @Override
    public int getParity() {
        return 0;
    }

    @Override
    public SecFunctionInterface getDerivative() {
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
            return 1;
        }

        @Override
        public SecFunctionInterface getDerivative() {
            return null;
        }
    }
}
