package ru.functions.trigonometric;

import ru.functions.utils.MathUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * Stub implementation of sine function using predefined values
 */
public class SinFunctionStub implements TrigonometricFunction {
    private final Map<Double, Double> tableValues;

    public SinFunctionStub() {
        tableValues = new HashMap<>();

        // Initialize table with some key values for sin(x)
        tableValues.put(-MathUtils.PI, 0.0);
        tableValues.put(-3 * MathUtils.HALF_PI, -1.0);
        tableValues.put(-MathUtils.PI / 3, -0.866);
        tableValues.put(-MathUtils.PI / 4, -0.7071);
        tableValues.put(-MathUtils.PI / 6, -0.5);
        tableValues.put(0.0, 0.0);
        tableValues.put(MathUtils.PI / 6, 0.5);
        tableValues.put(MathUtils.PI / 4, 0.7071);
        tableValues.put(MathUtils.PI / 3, 0.866);
        tableValues.put(MathUtils.HALF_PI, 1.0);
        tableValues.put(2 * MathUtils.PI / 3, 0.866);
        tableValues.put(3 * MathUtils.PI / 4, 0.7071);
        tableValues.put(5 * MathUtils.PI / 6, 0.5);
        tableValues.put(MathUtils.PI, 0.0);
        tableValues.put(7 * MathUtils.PI / 6, -0.5);
        tableValues.put(5 * MathUtils.PI / 4, -0.7071);
        tableValues.put(4 * MathUtils.PI / 3, -0.866);
        tableValues.put(3 * MathUtils.HALF_PI, -1.0);
        tableValues.put(2 * MathUtils.PI, 0.0);
    }

    @Override
    public double calculate(double x, double epsilon) throws IllegalArgumentException {
        if (!isInDomain(x)) {
            throw new IllegalArgumentException("Input value is outside the domain of sine function");
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

        // Fallback to Java's sin function if we can't interpolate
        return Math.sin(normalizedX);
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
    public TrigonometricFunction getDerivative() {
        // The derivative of sin(x) is cos(x)
        return new CosFunctionStub();
    }
}
