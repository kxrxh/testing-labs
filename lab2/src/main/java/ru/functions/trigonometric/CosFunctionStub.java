package ru.functions.trigonometric;

import ru.functions.utils.MathUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * Stub implementation of cosine function using predefined values
 */
public class CosFunctionStub implements TrigonometricFunction {
    private final Map<Double, Double> tableValues;

    public CosFunctionStub() {
        tableValues = new HashMap<>();

        // Initialize table with some key values for cos(x)
        tableValues.put(-MathUtils.PI, -1.0);
        tableValues.put(-3 * MathUtils.HALF_PI, 0.0);
        tableValues.put(-MathUtils.PI / 2, 0.0);
        tableValues.put(-MathUtils.PI / 3, 0.5);
        tableValues.put(-MathUtils.PI / 4, 0.7071);
        tableValues.put(-MathUtils.PI / 6, 0.866);
        tableValues.put(0.0, 1.0);
        tableValues.put(MathUtils.PI / 6, 0.866);
        tableValues.put(MathUtils.PI / 4, 0.7071);
        tableValues.put(MathUtils.PI / 3, 0.5);
        tableValues.put(MathUtils.HALF_PI, 0.0);
        tableValues.put(2 * MathUtils.PI / 3, -0.5);
        tableValues.put(3 * MathUtils.PI / 4, -0.7071);
        tableValues.put(5 * MathUtils.PI / 6, -0.866);
        tableValues.put(MathUtils.PI, -1.0);
        tableValues.put(7 * MathUtils.PI / 6, -0.866);
        tableValues.put(5 * MathUtils.PI / 4, -0.7071);
        tableValues.put(4 * MathUtils.PI / 3, -0.5);
        tableValues.put(3 * MathUtils.HALF_PI, 0.0);
        tableValues.put(2 * MathUtils.PI, 1.0);
    }

    @Override
    public double calculate(double x, double epsilon) throws IllegalArgumentException {
        if (!isInDomain(x)) {
            throw new IllegalArgumentException("Input value is outside the domain of cosine function");
        }

        // Normalize x to [-2π, 2π]
        double normalizedX = x % (2 * MathUtils.TWO_PI);
        if (normalizedX > MathUtils.TWO_PI) {
            normalizedX -= MathUtils.TWO_PI;
        } else if (normalizedX < -MathUtils.TWO_PI) {
            normalizedX += MathUtils.TWO_PI;
        }

        // Look for exact match
        if (tableValues.containsKey(normalizedX)) {
            return tableValues.get(normalizedX);
        }

        // Find closest values and interpolate
        double lowerKey = Double.NEGATIVE_INFINITY;
        double upperKey = Double.POSITIVE_INFINITY;

        for (Double key : tableValues.keySet()) {
            if (key < normalizedX && key > lowerKey) {
                lowerKey = key;
            }
            if (key > normalizedX && key < upperKey) {
                upperKey = key;
            }
        }

        // Linear interpolation
        if (lowerKey != Double.NEGATIVE_INFINITY && upperKey != Double.POSITIVE_INFINITY) {
            double lowerValue = tableValues.get(lowerKey);
            double upperValue = tableValues.get(upperKey);

            return lowerValue + (normalizedX - lowerKey) * (upperValue - lowerValue) / (upperKey - lowerKey);
        }

        // Fallback to Java's cos function if we can't interpolate
        return Math.cos(normalizedX);
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
    public TrigonometricFunction getDerivative() {
        // The derivative of cos(x) is -sin(x)
        return new NegatedTrigonometricFunction(new SinFunctionStub());
    }

    /**
     * Helper class to represent a negated trigonometric function
     */
    private static class NegatedTrigonometricFunction implements TrigonometricFunction {
        private final TrigonometricFunction baseFunction;

        public NegatedTrigonometricFunction(TrigonometricFunction baseFunction) {
            this.baseFunction = baseFunction;
        }

        @Override
        public double calculate(double x, double epsilon) throws IllegalArgumentException {
            return -baseFunction.calculate(x, epsilon);
        }

        @Override
        public boolean isInDomain(double x) {
            return baseFunction.isInDomain(x);
        }

        @Override
        public double getPeriod() {
            return baseFunction.getPeriod();
        }

        @Override
        public int getParity() {
            // Negating a function flips its parity
            int baseParity = baseFunction.getParity();
            return (baseParity != -1) ? -baseParity : -1;
        }

        @Override
        public TrigonometricFunction getDerivative() {
            // The derivative of -f(x) is -f'(x)
            return new NegatedTrigonometricFunction(baseFunction.getDerivative());
        }
    }
}
