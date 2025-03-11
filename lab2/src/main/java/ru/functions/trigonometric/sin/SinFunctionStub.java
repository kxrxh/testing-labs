package ru.functions.trigonometric.sin;

import ru.functions.utils.MathUtils;

import java.util.HashMap;
import java.util.Map;

public class SinFunctionStub implements SinFunctionInterface {
    private final Map<Double, Double> sinValues;

    public SinFunctionStub() {
        sinValues = new HashMap<>();

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

        for (double x = -2 * Math.PI; x <= 2 * Math.PI; x += Math.PI / 12) {
            double roundedX = Math.round(x * 1000.0) / 1000.0;
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

        x = MathUtils.normalizeAngle(x);

        double roundedX = Math.round(x * 1000.0) / 1000.0;

        if (sinValues.containsKey(roundedX)) {
            return sinValues.get(roundedX);
        }

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
        return new CosFunctionStubAdapter();
    }

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
            return 0;
        }

        @Override
        public SinFunctionInterface getDerivative() {
            return new NegativeSinStubAdapter();
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
            return 1;
        }

        @Override
        public SinFunctionInterface getDerivative() {
            return new NegativeCosFunctionStubAdapter();
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
            return 0;
        }

        @Override
        public SinFunctionInterface getDerivative() {
            return new SinFunctionStub();
        }
    }
}
