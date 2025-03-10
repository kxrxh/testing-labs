package ru.functions.trigonometric.sin;

import ru.functions.utils.MathUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * Stub implementation of sine function using predefined table values
 */
public class SinFunctionStub implements SinFunctionInterface {
    private final Map<Double, Double> sinValues;

    public SinFunctionStub() {
        sinValues = new HashMap<>();

        // Special values for sin(x) at key angles
        sinValues.put(0.0, 0.0);
        sinValues.put(Math.PI / 6, 0.5);
        sinValues.put(Math.PI / 4, Math.sqrt(2) / 2);
        sinValues.put(Math.PI / 3, Math.sqrt(3) / 2);
        sinValues.put(Math.PI / 2, 1.0);
        sinValues.put(2 * Math.PI / 3, Math.sqrt(3) / 2);
        sinValues.put(3 * Math.PI / 4, Math.sqrt(2) / 2);
        sinValues.put(5 * Math.PI / 6, 0.5);
        sinValues.put(Math.PI, 0.0);
        sinValues.put(7 * Math.PI / 6, -0.5);
        sinValues.put(5 * Math.PI / 4, -Math.sqrt(2) / 2);
        sinValues.put(4 * Math.PI / 3, -Math.sqrt(3) / 2);
        sinValues.put(3 * Math.PI / 2, -1.0);
        sinValues.put(5 * Math.PI / 3, -Math.sqrt(3) / 2);
        sinValues.put(7 * Math.PI / 4, -Math.sqrt(2) / 2);
        sinValues.put(11 * Math.PI / 6, -0.5);
        sinValues.put(2 * Math.PI, 0.0);

        // Add more values for better coverage
        for (double x = -2 * Math.PI; x <= 2 * Math.PI; x += Math.PI / 12) {
            double roundedX = Math.round(x * 1000.0) / 1000.0; // Round to 3 decimal places
            if (!sinValues.containsKey(roundedX)) {
                sinValues.put(roundedX, Math.sin(roundedX));
            }
        }
    }

    @Override
    public double calculate(double x, double epsilon) throws IllegalArgumentException {
        if (!isInDomain(x)) {
            throw new IllegalArgumentException("Input value is outside the domain of sine function");
        }

        // Normalize to [-2π, 2π] range
        x = MathUtils.normalizeAngle(x);

        // Round to 3 decimal places for table lookup
        double roundedX = Math.round(x * 1000.0) / 1000.0;

        // Check if we have an exact value in our table
        if (sinValues.containsKey(roundedX)) {
            return sinValues.get(roundedX);
        }

        // Find closest values and perform linear interpolation
        double lowerKey = sinValues.keySet().stream()
                .filter(k -> k < roundedX)
                .max(Double::compare)
                .orElse(-2 * Math.PI);

        double upperKey = sinValues.keySet().stream()
                .filter(k -> k > roundedX)
                .min(Double::compare)
                .orElse(2 * Math.PI);

        double lowerValue = sinValues.get(lowerKey);
        double upperValue = sinValues.get(upperKey);

        // Linear interpolation: y = y1 + (x - x1) * (y2 - y1) / (x2 - x1)
        return lowerValue + (roundedX - lowerKey) * (upperValue - lowerValue) / (upperKey - lowerKey);
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
        return new CosFunctionStubAdapter(); // The derivative of sin(x) is cos(x)
    }

    // Adapter class to represent cosine as derivative of sine
    private static class CosFunctionStubAdapter implements SinFunctionInterface {
        @Override
        public double calculate(double x, double epsilon) throws IllegalArgumentException {
            // cos(x) = sin(x + π/2)
            return new SinFunctionStub().calculate(x + MathUtils.HALF_PI, epsilon);
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
            return new NegativeSinStubAdapter(); // Derivative of cos(x) is -sin(x)
        }
    }

    // Adapter for -sin(x)
    private static class NegativeSinStubAdapter implements SinFunctionInterface {
        @Override
        public double calculate(double x, double epsilon) throws IllegalArgumentException {
            return -new SinFunctionStub().calculate(x, epsilon);
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
            return new NegativeCosFunctionStubAdapter(); // Derivative of -sin(x) is -cos(x)
        }
    }

    // Adapter for -cos(x)
    private static class NegativeCosFunctionStubAdapter implements SinFunctionInterface {
        @Override
        public double calculate(double x, double epsilon) throws IllegalArgumentException {
            return -new CosFunctionStubAdapter().calculate(x, epsilon);
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
            return new SinFunctionStub(); // Derivative of -cos(x) is sin(x)
        }
    }
}
