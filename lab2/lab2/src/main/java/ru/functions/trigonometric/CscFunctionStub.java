package ru.functions.trigonometric;

import ru.functions.utils.MathUtils;

/**
 * Stub implementation of cosecant function using the sine function stub
 */
public class CscFunctionStub implements TrigonometricFunction {
    private final TrigonometricFunction sinFunctionStub;

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
        if (MathUtils.isZero(sinValue, epsilon)) {
            throw new IllegalArgumentException("Cosecant is undefined at x = " + x + " (sin(x) = 0)");
        }

        // csc(x) = 1/sin(x)
        return 1.0 / sinValue;
    }

    @Override
    public boolean isInDomain(double x) {
        // csc(x) is defined for all x where sin(x) != 0
        // sin(x) = 0 when x = nπ for integer n
        return !MathUtils.isCloseToMultipleOfHalfPi(x, 1e-10) ||
                !MathUtils.isCloseToMultipleOfHalfPi(x - MathUtils.PI, 1e-10);
    }

    @Override
    public double getPeriod() {
        return sinFunctionStub.getPeriod();
    }

    @Override
    public int getParity() {
        return sinFunctionStub.getParity(); // csc(x) has the same parity as sin(x) (odd)
    }

    @Override
    public TrigonometricFunction getDerivative() {
        // The derivative of csc(x) is -csc(x) * cot(x)
        return new NegatedCscCotProduct(this, sinFunctionStub);
    }

    /**
     * Helper class to represent -csc(x) * cot(x) for the derivative of cosecant
     */
    private static class NegatedCscCotProduct implements TrigonometricFunction {
        private final TrigonometricFunction cscFunction;
        private final TrigonometricFunction sinFunction;

        public NegatedCscCotProduct(TrigonometricFunction cscFunction, TrigonometricFunction sinFunction) {
            this.cscFunction = cscFunction;
            this.sinFunction = sinFunction;
        }

        @Override
        public double calculate(double x, double epsilon) throws IllegalArgumentException {
            if (!isInDomain(x)) {
                throw new IllegalArgumentException("Input value is outside the domain of -csc(x)*cot(x)");
            }

            double csc = cscFunction.calculate(x, epsilon);
            double sin = sinFunction.calculate(x, epsilon);
            double cos = sinFunction.calculate(x + MathUtils.HALF_PI, epsilon); // cos(x) = sin(x + π/2)

            // cot(x) = cos(x) / sin(x)
            double cot = cos / sin;

            return -csc * cot;
        }

        @Override
        public boolean isInDomain(double x) {
            return cscFunction.isInDomain(x);
        }

        @Override
        public double getPeriod() {
            return MathUtils.PI;
        }

        @Override
        public int getParity() {
            return 0; // -csc(x)*cot(x) is even
        }

        @Override
        public TrigonometricFunction getDerivative() {
            // The derivative is complex and not implemented
            return null;
        }
    }
}
