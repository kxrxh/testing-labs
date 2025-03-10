package ru.functions.logarithmic;

import ru.functions.utils.MathUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * Stub implementation of natural logarithm function using predefined values
 */
public class LnFunctionStub implements LogarithmicFunction {
    private final Map<Double, Double> tableValues;
    private static final double E = 2.718281828459045; // Base of natural logarithm

    public LnFunctionStub() {
        tableValues = new HashMap<>();

        // Initialize table with some key values for ln(x)
        tableValues.put(0.1, -2.3026);
        tableValues.put(0.2, -1.6094);
        tableValues.put(0.25, -1.3863);
        tableValues.put(0.5, -0.6931);
        tableValues.put(0.75, -0.2877);
        tableValues.put(1.0, 0.0);
        tableValues.put(1.5, 0.4055);
        tableValues.put(2.0, 0.6931);
        tableValues.put(2.5, 0.9163);
        tableValues.put(E, 1.0); // e
        tableValues.put(3.0, 1.0986);
        tableValues.put(4.0, 1.3863);
        tableValues.put(5.0, 1.6094);
        tableValues.put(7.5, 2.0149);
        tableValues.put(10.0, 2.3026);
        tableValues.put(20.0, 2.9957);
        tableValues.put(50.0, 3.9120);
        tableValues.put(100.0, 4.6052);
        tableValues.put(1000.0, 6.9078);
    }

    @Override
    public double calculate(double x, double epsilon) throws IllegalArgumentException {
        if (!isInDomain(x)) {
            throw new IllegalArgumentException("Input value is outside the domain of natural logarithm function");
        }

        // Look for exact match
        if (tableValues.containsKey(x)) {
            return tableValues.get(x);
        }

        // Find closest values and interpolate
        double lowerKey = Double.NEGATIVE_INFINITY;
        double upperKey = Double.POSITIVE_INFINITY;

        for (Double key : tableValues.keySet()) {
            if (key < x && key > lowerKey) {
                lowerKey = key;
            }
            if (key > x && key < upperKey) {
                upperKey = key;
            }
        }

        // Linear interpolation
        if (lowerKey != Double.NEGATIVE_INFINITY && upperKey != Double.POSITIVE_INFINITY) {
            double lowerValue = tableValues.get(lowerKey);
            double upperValue = tableValues.get(upperKey);

            return lowerValue + (x - lowerKey) * (upperValue - lowerValue) / (upperKey - lowerKey);
        }

        // Fallback: use ln(x) identity properties if x is large
        // ln(a*b) = ln(a) + ln(b)
        if (x > tableValues.keySet().stream().max(Double::compare).orElse(0.0)) {
            double closestValue = tableValues.keySet().stream().max(Double::compare).orElse(1.0);
            double factor = x / closestValue;

            // Recursively calculate ln(x) = ln(closestValue * factor) = ln(closestValue) +
            // ln(factor)
            return tableValues.get(closestValue) + calculate(factor, epsilon);
        }

        // Fallback to Java's log function if we can't interpolate (should not reach
        // here)
        return Math.log(x);
    }

    @Override
    public boolean isInDomain(double x) {
        // ln(x) is defined for x > 0
        return x > 0;
    }

    @Override
    public double getBase() {
        return E;
    }

    @Override
    public LogarithmicFunction changeBase(double newBase) {
        if (newBase <= 0 || newBase == 1) {
            throw new IllegalArgumentException("Logarithm base must be positive and not equal to 1");
        }

        if (MathUtils.areEqual(newBase, E, 1e-10)) {
            return this; // Already natural logarithm
        }

        // Create a new logarithm with the specified base
        return new LogarithmWithBase(this, newBase);
    }

    /**
     * Helper class to represent a logarithm with a different base
     */
    private static class LogarithmWithBase implements LogarithmicFunction {
        private final LogarithmicFunction baseLogFunction;
        private final double base;
        private final double baseLogarithm; // ln(base)

        public LogarithmWithBase(LogarithmicFunction baseLogFunction, double base) {
            this.baseLogFunction = baseLogFunction;
            this.base = base;
            this.baseLogarithm = baseLogFunction.calculate(base, 1e-15);
        }

        @Override
        public double calculate(double x, double epsilon) throws IllegalArgumentException {
            // log_b(x) = ln(x) / ln(b)
            double lnX = baseLogFunction.calculate(x, epsilon * baseLogarithm);
            return lnX / baseLogarithm;
        }

        @Override
        public boolean isInDomain(double x) {
            return baseLogFunction.isInDomain(x);
        }

        @Override
        public double getBase() {
            return base;
        }

        @Override
        public LogarithmicFunction changeBase(double newBase) {
            if (MathUtils.areEqual(newBase, base, 1e-10)) {
                return this;
            }
            return baseLogFunction.changeBase(newBase);
        }
    }
}
