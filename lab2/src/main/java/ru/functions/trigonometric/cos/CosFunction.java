package ru.functions.trigonometric.cos;

import ru.functions.trigonometric.sin.SinFunction;
import ru.functions.utils.MathUtils;

/**
 * Implementation of cosine function using sine function and the identity cos(x)
 * = sin(x + π/2)
 */
public class CosFunction implements CosFunctionInterface {
    private final SinFunction sinFunction;

    public CosFunction() {
        this.sinFunction = new SinFunction();
    }

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
    public CosFunctionInterface getDerivative() {
        // The derivative of cos(x) is -sin(x)
        return new NegativeSinAdapter(sinFunction);
    }

    /**
     * Gets the sine function that this cosine function depends on
     *
     * @return the sine function
     */
    public SinFunction getSinFunction() {
        return sinFunction;
    }

    /**
     * Helper class to represent a negated sine function as a cosine function
     */
    private static class NegativeSinAdapter implements CosFunctionInterface {
        private final SinFunction sinFunction;

        public NegativeSinAdapter(SinFunction sinFunction) {
            this.sinFunction = sinFunction;
        }

        @Override
        public double calculate(double x, double epsilon) throws IllegalArgumentException {
            return -sinFunction.calculate(x, epsilon);
        }

        @Override
        public boolean isInDomain(double x) {
            return sinFunction.isInDomain(x);
        }

        @Override
        public double getPeriod() {
            return sinFunction.getPeriod();
        }

        @Override
        public int getParity() {
            return 1; // Odd function: -sin(-x) = sin(x)
        }

        @Override
        public CosFunctionInterface getDerivative() {
            // The derivative of -sin(x) is -cos(x)
            return new NegativeCosAdapter();
        }
    }

    /**
     * Helper class to represent a negated cosine function
     */
    private static class NegativeCosAdapter implements CosFunctionInterface {
        private final CosFunction cosFunction = new CosFunction();

        @Override
        public double calculate(double x, double epsilon) throws IllegalArgumentException {
            return -cosFunction.calculate(x, epsilon);
        }

        @Override
        public boolean isInDomain(double x) {
            return cosFunction.isInDomain(x);
        }

        @Override
        public double getPeriod() {
            return cosFunction.getPeriod();
        }

        @Override
        public int getParity() {
            return 0; // Even function: -cos(-x) = -cos(x)
        }

        @Override
        public CosFunctionInterface getDerivative() {
            // The derivative of -cos(x) is sin(x)
            return new SinAdapter(cosFunction.getSinFunction());
        }
    }

    /**
     * Helper class to represent a sine function as a cosine function
     */
    private static class SinAdapter implements CosFunctionInterface {
        private final SinFunction sinFunction;

        public SinAdapter(SinFunction sinFunction) {
            this.sinFunction = sinFunction;
        }

        @Override
        public double calculate(double x, double epsilon) throws IllegalArgumentException {
            return sinFunction.calculate(x, epsilon);
        }

        @Override
        public boolean isInDomain(double x) {
            return sinFunction.isInDomain(x);
        }

        @Override
        public double getPeriod() {
            return sinFunction.getPeriod();
        }

        @Override
        public int getParity() {
            return 1; // Odd function: sin(-x) = -sin(x)
        }

        @Override
        public CosFunctionInterface getDerivative() {
            // The derivative of sin(x) is cos(x)
            return new CosFunction(sinFunction);
        }
    }
}
