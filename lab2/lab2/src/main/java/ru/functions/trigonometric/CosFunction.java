package ru.functions.trigonometric;

import ru.functions.utils.MathUtils;

/**
 * Implementation of cosine function using sine function and the identity cos(x)
 * = sin(x + π/2)
 */
public class CosFunction implements TrigonometricFunction {
    private final SinFunction sinFunction;

    public CosFunction(SinFunction sinFunction) {
        this.sinFunction = sinFunction;
    }

    @Override
    public double calculate(double x, double epsilon) throws IllegalArgumentException {
        if (!isInDomain(x)) {
            throw new IllegalArgumentException("Input value is outside the domain of cosine function");
        }

        // cos(x) = sin(x + π/2)
        return sinFunction.calculate(x + MathUtils.HALF_PI, epsilon);
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
        return new NegatedTrigonometricFunction(sinFunction);
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
