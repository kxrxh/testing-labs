package ru.functions.trigonometric;

import ru.functions.utils.MathUtils;

/**
 * Stub implementation of secant function
 */
public class SecFunctionStub implements TrigonometricFunction {
    private final CosFunctionStub cosFunctionStub;

    public SecFunctionStub(CosFunctionStub cosFunctionStub) {
        this.cosFunctionStub = cosFunctionStub;
    }

    @Override
    public double calculate(double x, double epsilon) throws IllegalArgumentException {
        if (!isInDomain(x)) {
            throw new IllegalArgumentException("Input value is outside the domain of secant function");
        }

        double cosValue = cosFunctionStub.calculate(x, epsilon);

        // Prevent division by zero
        if (Math.abs(cosValue) < epsilon) {
            throw new IllegalArgumentException("Secant is undefined for x = " + x + " (cos(x) = 0)");
        }

        // sec(x) = 1/cos(x)
        return 1.0 / cosValue;
    }

    @Override
    public boolean isInDomain(double x) {
        // sec(x) is defined for all x where cos(x) != 0
        // cos(x) = 0 at x = π/2 + nπ

        // Normalize angle to [0, 2π) for easier checking
        double normalizedX = MathUtils.normalizeAngle(x);

        // Check if x is close to π/2 or 3π/2 (where cos(x) = 0)
        return !MathUtils.isCloseToMultipleOfHalfPi(normalizedX - MathUtils.HALF_PI, 1e-10);
    }

    @Override
    public double getPeriod() {
        return MathUtils.TWO_PI; // Same as cosine
    }

    @Override
    public int getParity() {
        return 0; // Even: sec(-x) = sec(x)
    }

    @Override
    public TrigonometricFunction getDerivative() {
        // The derivative of sec(x) is sec(x) * tan(x)
        return null; // Simplified for stub
    }
}
