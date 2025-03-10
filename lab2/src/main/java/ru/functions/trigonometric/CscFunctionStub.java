package ru.functions.trigonometric;

import ru.functions.utils.MathUtils;

/**
 * Stub implementation of cosecant function
 */
public class CscFunctionStub implements TrigonometricFunction {
    private final SinFunctionStub sinFunctionStub;

    public CscFunctionStub(SinFunctionStub sinFunctionStub) {
        this.sinFunctionStub = sinFunctionStub;
    }

    @Override
    public double calculate(double x, double epsilon) throws IllegalArgumentException {
        if (!isInDomain(x)) {
            throw new IllegalArgumentException("Input value is outside the domain of cosecant function");
        }

        double sinValue = sinFunctionStub.calculate(x, epsilon);

        // Prevent division by zero
        if (Math.abs(sinValue) < epsilon) {
            throw new IllegalArgumentException("Cosecant is undefined for x = " + x + " (sin(x) = 0)");
        }

        // csc(x) = 1/sin(x)
        return 1.0 / sinValue;
    }

    @Override
    public boolean isInDomain(double x) {
        // csc(x) is defined for all x where sin(x) != 0
        // sin(x) = 0 at x = nπ

        // Normalize angle to [0, 2π) range for easier checking
        double normalizedX = MathUtils.normalizeAngle(x);

        // Check if x is close to 0 or π (where sin(x) = 0)
        return !(MathUtils.isClose(normalizedX, 0.0, 1e-10) ||
                MathUtils.isClose(normalizedX, MathUtils.PI, 1e-10));
    }

    @Override
    public double getPeriod() {
        return MathUtils.TWO_PI; // Same as sine
    }

    @Override
    public int getParity() {
        return 1; // Odd: csc(-x) = -csc(x)
    }

    @Override
    public TrigonometricFunction getDerivative() {
        // The derivative of csc(x) is -csc(x) * cot(x)
        return null; // Simplified for stub
    }
}
