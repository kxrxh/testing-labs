package ru.functions.trigonometric;

import ru.functions.utils.MathUtils;

/**
 * Stub implementation of sine function
 */
public class SinFunctionStub implements TrigonometricFunction {

    // Lookup table for some common sine values
    private static final double[][] LOOKUP_TABLE = {
            { 0.0, 0.0 },
            { MathUtils.HALF_PI / 2, 0.7071067811865476 }, // sin(π/4) = 1/√2
            { MathUtils.HALF_PI, 1.0 },
            { MathUtils.PI / 2 + MathUtils.HALF_PI / 2, 0.7071067811865476 }, // sin(3π/4) = 1/√2
            { MathUtils.PI, 0.0 },
            { MathUtils.PI + MathUtils.HALF_PI / 2, -0.7071067811865476 }, // sin(5π/4) = -1/√2
            { MathUtils.PI + MathUtils.HALF_PI, -1.0 },
            { MathUtils.PI + MathUtils.HALF_PI + MathUtils.HALF_PI / 2, -0.7071067811865476 }, // sin(7π/4) = -1/√2
            { MathUtils.TWO_PI, 0.0 }
    };

    public SinFunctionStub() {
        // No-arg constructor for stub
    }

    @Override
    public double calculate(double x, double epsilon) throws IllegalArgumentException {
        if (!isInDomain(x)) {
            throw new IllegalArgumentException("Input value is outside the domain of sine function");
        }

        // Normalize x to [0, 2π) range for lookup
        double normalizedX = MathUtils.normalizeAngle(x);

        // Check lookup table for exact values
        for (double[] entry : LOOKUP_TABLE) {
            if (Math.abs(normalizedX - entry[0]) < epsilon) {
                return entry[1];
            }
        }

        // For other values, use standard sine approximation
        return Math.sin(x);
    }

    @Override
    public boolean isInDomain(double x) {
        // Sine is defined for all real numbers
        return true;
    }

    @Override
    public double getPeriod() {
        return MathUtils.TWO_PI; // 2π
    }

    @Override
    public int getParity() {
        return 1; // Odd: sin(-x) = -sin(x)
    }

    @Override
    public TrigonometricFunction getDerivative() {
        // The derivative of sin(x) is cos(x)
        return new CosFunctionStub(this);
    }
}
