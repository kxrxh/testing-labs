package ru.functions.trigonometric.csc;

import ru.functions.trigonometric.sin.SinFunctionStub;
import ru.functions.utils.MathUtils;

import java.util.HashMap;
import java.util.Map;

public class CscFunctionStub implements CscFunctionInterface {
    private final Map<Double, Double> cscValues;
    private SinFunctionStub sinFunctionStub;

    public CscFunctionStub() {
        this.sinFunctionStub = new SinFunctionStub();
        cscValues = new HashMap<>();

        // Special values for csc(x) at key angles
        // cscValues.put(0.0, Double.POSITIVE_INFINITY); // Undefined
        cscValues.put(Math.PI / 6, 2.0);
        cscValues.put(Math.PI / 4, Math.sqrt(2));
        cscValues.put(Math.PI / 3, 2.0 / Math.sqrt(3));
        cscValues.put(Math.PI / 2, 1.0);
        cscValues.put(2 * Math.PI / 3, 2.0 / Math.sqrt(3));
        cscValues.put(3 * Math.PI / 4, Math.sqrt(2));
        cscValues.put(5 * Math.PI / 6, 2.0);
        // cscValues.put(Math.PI, Double.POSITIVE_INFINITY); // Undefined
        cscValues.put(7 * Math.PI / 6, -2.0);
        cscValues.put(5 * Math.PI / 4, -Math.sqrt(2));
        cscValues.put(4 * Math.PI / 3, -2.0 / Math.sqrt(3));
        cscValues.put(3 * Math.PI / 2, -1.0);
        cscValues.put(5 * Math.PI / 3, -2.0 / Math.sqrt(3));
        cscValues.put(7 * Math.PI / 4, -Math.sqrt(2));
        cscValues.put(11 * Math.PI / 6, -2.0);
        // cscValues.put(2 * Math.PI, Double.POSITIVE_INFINITY); // Undefined

        for (double x = -2 * Math.PI; x <= 2 * Math.PI; x += Math.PI / 12) {
            double roundedX = Math.round(x * 1000.0) / 1000.0;

            if (!isInDomain(roundedX)) {
                continue;
            }

            if (!cscValues.containsKey(roundedX)) {
                double sinValue = Math.sin(roundedX);
                if (Math.abs(sinValue) > 1e-10) {
                    cscValues.put(roundedX, 1.0 / sinValue);
                }
            }
        }
    }

    public CscFunctionStub(SinFunctionStub sinFunctionStub) {
        this();
        this.sinFunctionStub = sinFunctionStub;
    }

    @Override
    public double calculate(double x, double epsilon) throws IllegalArgumentException {
        if (!isInDomain(x)) {
            throw new IllegalArgumentException("Input value is outside the domain of cosecant function");
        }

        x = MathUtils.normalizeAngle(x);

        double roundedX = Math.round(x * 1000.0) / 1000.0;

        if (cscValues.containsKey(roundedX)) {
            return cscValues.get(roundedX);
        }

        // Use the sine function stub to calculate csc(x) = 1/sin(x)
        double sinValue = sinFunctionStub.calculate(roundedX, epsilon);

        // Avoid division by zero
        if (Math.abs(sinValue) < epsilon) {
            throw new IllegalArgumentException("Cosecant is undefined at x = " + x + " (sin(x) ≈ 0)");
        }

        return 1.0 / sinValue;
    }

    @Override
    public boolean isInDomain(double x) {
        // Csc(x) is defined for all x where sin(x) ≠ 0
        // This means x ≠ nπ for integer n
        double normalizedX = MathUtils.normalizeAngle(x);
        return Math.abs(normalizedX) > 1e-10 &&
                Math.abs(Math.abs(normalizedX) - Math.PI) > 1e-10;
    }

    @Override
    public double getPeriod() {
        return MathUtils.TWO_PI;
    }

    @Override
    public int getParity() {
        return 1;
    }

    @Override
    public CscFunctionInterface getDerivative() {
        return new NegativeCscCotProductStubAdapter();
    }

    /**
     * Gets the sine function stub that this cosecant function depends on
     *
     * @return the sine function stub
     */
    public SinFunctionStub getSinFunctionStub() {
        return sinFunctionStub;
    }

    /**
     * Helper class to represent the negative product of cosecant and cotangent
     * functions
     */
    private class NegativeCscCotProductStubAdapter implements CscFunctionInterface {
        @Override
        public double calculate(double x, double epsilon) throws IllegalArgumentException {
            if (!isInDomain(x)) {
                throw new IllegalArgumentException("Input value is outside the domain of -csc(x)cot(x)");
            }

            double sinValue = sinFunctionStub.calculate(x, epsilon);
            double cosValue = sinFunctionStub.getDerivative().calculate(x, epsilon); // cos(x)

            // Avoid division by zero
            if (Math.abs(sinValue) < epsilon) {
                throw new IllegalArgumentException("-csc(x)cot(x) is undefined at x = " + x + " (sin(x) ≈ 0)");
            }

            // -csc(x)cot(x) = -cos(x)/(sin(x)^2)
            return -cosValue / (sinValue * sinValue);
        }

        @Override
        public boolean isInDomain(double x) {
            return CscFunctionStub.this.isInDomain(x);
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
        public CscFunctionInterface getDerivative() {
            return null;
        }
    }
}
