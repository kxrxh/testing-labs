package ru.functions.trigonometric.cos;

import ru.functions.trigonometric.sin.SinFunctionStub;
import ru.functions.utils.MathUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * Stub implementation of cosine function using predefined table values
 */
public class CosFunctionStub implements CosFunctionInterface {
    private final Map<Double, Double> cosValues;
    private SinFunctionStub sinFunctionStub;

    public CosFunctionStub() {
        this.sinFunctionStub = new SinFunctionStub();
        cosValues = new HashMap<>();

        // Special values for cos(x) at key angles
        cosValues.put(0.0, 1.0);
        cosValues.put(Math.PI / 6, Math.sqrt(3) / 2);
        cosValues.put(Math.PI / 4, Math.sqrt(2) / 2);
        cosValues.put(Math.PI / 3, 0.5);
        cosValues.put(Math.PI / 2, 0.0);
        cosValues.put(2 * Math.PI / 3, -0.5);
        cosValues.put(3 * Math.PI / 4, -Math.sqrt(2) / 2);
        cosValues.put(5 * Math.PI / 6, -Math.sqrt(3) / 2);
        cosValues.put(Math.PI, -1.0);
        cosValues.put(7 * Math.PI / 6, -Math.sqrt(3) / 2);
        cosValues.put(5 * Math.PI / 4, -Math.sqrt(2) / 2);
        cosValues.put(4 * Math.PI / 3, -0.5);
        cosValues.put(3 * Math.PI / 2, 0.0);
        cosValues.put(5 * Math.PI / 3, 0.5);
        cosValues.put(7 * Math.PI / 4, Math.sqrt(2) / 2);
        cosValues.put(11 * Math.PI / 6, Math.sqrt(3) / 2);
        cosValues.put(2 * Math.PI, 1.0);

        // Add more values for better coverage
        for (double x = -2 * Math.PI; x <= 2 * Math.PI; x += Math.PI / 12) {
            double roundedX = Math.round(x * 1000.0) / 1000.0; // Round to 3 decimal places
            if (!cosValues.containsKey(roundedX)) {
                cosValues.put(roundedX, Math.cos(roundedX));
            }
        }
    }

    public CosFunctionStub(SinFunctionStub sinFunctionStub) {
        this();
        this.sinFunctionStub = sinFunctionStub;
    }

    @Override
    public double calculate(double x, double epsilon) throws IllegalArgumentException {
        if (!isInDomain(x)) {
            throw new IllegalArgumentException("Input value is outside the domain of cosine function");
        }

        // Normalize to [-2π, 2π] range
        x = MathUtils.normalizeAngle(x);

        // Round to 3 decimal places for table lookup
        double roundedX = Math.round(x * 1000.0) / 1000.0;

        // Check if we have an exact value in our table
        if (cosValues.containsKey(roundedX)) {
            return cosValues.get(roundedX);
        }

        // Find closest values and perform linear interpolation
        double lowerKey = cosValues.keySet().stream()
                .filter(k -> k < roundedX)
                .max(Double::compare)
                .orElse(-2 * Math.PI);

        double upperKey = cosValues.keySet().stream()
                .filter(k -> k > roundedX)
                .min(Double::compare)
                .orElse(2 * Math.PI);

        double lowerValue = cosValues.get(lowerKey);
        double upperValue = cosValues.get(upperKey);

        // Linear interpolation: y = y1 + (x - x1) * (y2 - y1) / (x2 - x1)
        return lowerValue + (roundedX - lowerKey) * (upperValue - lowerValue) / (upperKey - lowerKey);
    }

    @Override
    public boolean isInDomain(double x) {
        // Cos(x) is defined for all real numbers
        return true;
    }

    @Override
    public double getPeriod() {
        return MathUtils.TWO_PI; // 2π
    }

    @Override
    public int getParity() {
        return 0; // Even function: cos(-x) = cos(x)
    }

    @Override
    public CosFunctionInterface getDerivative() {
        // The derivative of cos(x) is -sin(x)
        return new NegativeSinStubAdapter(sinFunctionStub);
    }

    /**
     * Gets the sine function stub that this cosine function depends on
     *
     * @return the sine function stub
     */
    public SinFunctionStub getSinFunctionStub() {
        return sinFunctionStub;
    }

    /**
     * Helper class to represent a negated sine function as a cosine function
     */
    private static class NegativeSinStubAdapter implements CosFunctionInterface {
        private final SinFunctionStub sinFunctionStub;

        public NegativeSinStubAdapter(SinFunctionStub sinFunctionStub) {
            this.sinFunctionStub = sinFunctionStub;
        }

        @Override
        public double calculate(double x, double epsilon) throws IllegalArgumentException {
            return -sinFunctionStub.calculate(x, epsilon);
        }

        @Override
        public boolean isInDomain(double x) {
            return sinFunctionStub.isInDomain(x);
        }

        @Override
        public double getPeriod() {
            return sinFunctionStub.getPeriod();
        }

        @Override
        public int getParity() {
            return 1; // Odd function: -sin(-x) = sin(x)
        }

        @Override
        public CosFunctionInterface getDerivative() {
            // The derivative of -sin(x) is -cos(x)
            return new NegativeCosStubAdapter();
        }
    }

    /**
     * Helper class to represent a negated cosine function
     */
    private static class NegativeCosStubAdapter implements CosFunctionInterface {
        private final CosFunctionStub cosFunctionStub = new CosFunctionStub();

        @Override
        public double calculate(double x, double epsilon) throws IllegalArgumentException {
            return -cosFunctionStub.calculate(x, epsilon);
        }

        @Override
        public boolean isInDomain(double x) {
            return cosFunctionStub.isInDomain(x);
        }

        @Override
        public double getPeriod() {
            return cosFunctionStub.getPeriod();
        }

        @Override
        public int getParity() {
            return 0; // Even function: -cos(-x) = -cos(x)
        }

        @Override
        public CosFunctionInterface getDerivative() {
            // The derivative of -cos(x) is sin(x)
            return new SinStubAdapter(cosFunctionStub.getSinFunctionStub());
        }
    }

    /**
     * Helper class to represent a sine function as a cosine function
     */
    private static class SinStubAdapter implements CosFunctionInterface {
        private final SinFunctionStub sinFunctionStub;

        public SinStubAdapter(SinFunctionStub sinFunctionStub) {
            this.sinFunctionStub = sinFunctionStub;
        }

        @Override
        public double calculate(double x, double epsilon) throws IllegalArgumentException {
            return sinFunctionStub.calculate(x, epsilon);
        }

        @Override
        public boolean isInDomain(double x) {
            return sinFunctionStub.isInDomain(x);
        }

        @Override
        public double getPeriod() {
            return sinFunctionStub.getPeriod();
        }

        @Override
        public int getParity() {
            return 1; // Odd function: sin(-x) = -sin(x)
        }

        @Override
        public CosFunctionInterface getDerivative() {
            // The derivative of sin(x) is cos(x)
            return new CosFunctionStub(sinFunctionStub);
        }
    }
}
