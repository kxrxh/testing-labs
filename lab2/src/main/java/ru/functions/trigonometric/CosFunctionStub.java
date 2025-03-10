package ru.functions.trigonometric;

import ru.functions.utils.MathUtils;

/**
 * Stub implementation of cosine function
 */
public class CosFunctionStub implements TrigonometricFunction {
    private final SinFunctionStub sinFunctionStub;

    // Lookup table for some common cosine values
    private static final double[][] LOOKUP_TABLE = {
            { 0.0, 1.0 },
            { MathUtils.HALF_PI / 2, 0.7071067811865476 }, // cos(π/4) = 1/√2
            { MathUtils.HALF_PI, 0.0 },
            { MathUtils.PI / 2 + MathUtils.HALF_PI / 2, -0.7071067811865476 }, // cos(3π/4) = -1/√2
            { MathUtils.PI, -1.0 },
            { MathUtils.PI + MathUtils.HALF_PI / 2, -0.7071067811865476 }, // cos(5π/4) = -1/√2
            { MathUtils.PI + MathUtils.HALF_PI, 0.0 },
            { MathUtils.PI + MathUtils.HALF_PI + MathUtils.HALF_PI / 2, 0.7071067811865476 }, // cos(7π/4) = 1/√2
            { MathUtils.TWO_PI, 1.0 }
    };

    public CosFunctionStub(SinFunctionStub sinFunctionStub) {
        this.sinFunctionStub = sinFunctionStub;
    }

    @Override
    public double calculate(double x, double epsilon) throws IllegalArgumentException {
        if (!isInDomain(x)) {
            throw new IllegalArgumentException("Input value is outside the domain of cosine function");
        }

        // Normalize x to [0, 2π) range for lookup
        double normalizedX = MathUtils.normalizeAngle(x);

        // Check lookup table for exact values
        for (double[] entry : LOOKUP_TABLE) {
            if (Math.abs(normalizedX - entry[0]) < epsilon) {
                return entry[1];
            }
        }

        // For other values, use standard cosine approximation
        return Math.cos(x);
    }

    @Override
    public boolean isInDomain(double x) {
        // Cosine is defined for all real numbers
        return true;
    }

    @Override
    public double getPeriod() {
        return MathUtils.TWO_PI; // 2π
    }

    @Override
    public int getParity() {
        return 0; // Even: cos(-x) = cos(x)
    }

    @Override
    public TrigonometricFunction getDerivative() {
        // The derivative of cos(x) is -sin(x)
        return new NegatedTrigStub(sinFunctionStub);
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
     * Helper class to represent negated stub trigonometric functions
     */
    private static class NegatedTrigStub implements TrigonometricFunction {
        private final TrigonometricFunction baseFunction;

        public NegatedTrigStub(TrigonometricFunction baseFunction) {
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
            return new NegatedTrigStub(baseFunction.getDerivative());
        }
    }
}
