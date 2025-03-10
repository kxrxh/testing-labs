package ru.functions.trigonometric;

import ru.functions.utils.MathUtils;

/**
 * Stub implementation of secant function using the cosine function stub
 */
public class SecFunctionStub implements TrigonometricFunction {
    private final TrigonometricFunction cosFunctionStub;

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
        if (MathUtils.isZero(cosValue, epsilon)) {
            throw new IllegalArgumentException("Secant is undefined at x = " + x + " (cos(x) = 0)");
        }

        // sec(x) = 1/cos(x)
        return 1.0 / cosValue;
    }

    @Override
    public boolean isInDomain(double x) {
        // sec(x) is defined for all x where cos(x) != 0
        // cos(x) = 0 when x = π/2 + nπ for integer n
        return !MathUtils.isCloseToMultipleOfHalfPi(x - MathUtils.HALF_PI, 1e-10);
    }

    @Override
    public double getPeriod() {
        return cosFunctionStub.getPeriod();
    }

    @Override
    public int getParity() {
        return cosFunctionStub.getParity(); // sec(x) has the same parity as cos(x) (even)
    }

    @Override
    public TrigonometricFunction getDerivative() {
        // The derivative of sec(x) is sec(x) * tan(x)
        return new SecTanProduct(this, cosFunctionStub);
    }

    /**
     * Helper class to represent sec(x) * tan(x) for the derivative of secant
     */
    private static class SecTanProduct implements TrigonometricFunction {
        private final TrigonometricFunction secFunction;
        private final TrigonometricFunction cosFunction;

        public SecTanProduct(TrigonometricFunction secFunction, TrigonometricFunction cosFunction) {
            this.secFunction = secFunction;
            this.cosFunction = cosFunction;
        }

        @Override
        public double calculate(double x, double epsilon) throws IllegalArgumentException {
            if (!isInDomain(x)) {
                throw new IllegalArgumentException("Input value is outside the domain of sec(x)*tan(x)");
            }

            double sec = secFunction.calculate(x, epsilon);
            double cos = cosFunction.calculate(x, epsilon);
            double sin = cosFunction.calculate(x - MathUtils.HALF_PI, epsilon); // sin(x) = cos(x - π/2)

            // tan(x) = sin(x) / cos(x)
            double tan = sin / cos;

            return sec * tan;
        }

        @Override
        public boolean isInDomain(double x) {
            return secFunction.isInDomain(x);
        }

        @Override
        public double getPeriod() {
            return MathUtils.PI;
        }

        @Override
        public int getParity() {
            return 1; // sec(x)*tan(x) is odd
        }

        @Override
        public TrigonometricFunction getDerivative() {
            // The derivative is complex and not implemented
            return null;
        }
    }
}
