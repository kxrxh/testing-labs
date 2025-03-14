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

        return sinFunction.calculate(x + MathUtils.HALF_PI, epsilon);
    }

    @Override
    public boolean isInDomain(double x) {
        return !Double.isInfinite(x) && !Double.isNaN(x);
    }

    @Override
    public double getPeriod() {
        return MathUtils.TWO_PI;
    }

    @Override
    public int getParity() {
        return 0;
    }

    @Override
    public CosFunctionInterface getDerivative() {
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
            return 1;
        }

        @Override
        public CosFunctionInterface getDerivative() {
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
            return 0;
        }

        @Override
        public CosFunctionInterface getDerivative() {
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
            return 1;
        }

        @Override
        public CosFunctionInterface getDerivative() {
            return new CosFunction(sinFunction);
        }
    }
}
