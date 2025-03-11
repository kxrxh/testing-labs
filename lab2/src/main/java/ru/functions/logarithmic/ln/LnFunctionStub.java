package ru.functions.logarithmic.ln;

import ru.functions.utils.MathUtils;

import java.util.HashMap;
import java.util.Map;

public class LnFunctionStub implements LnFunctionInterface {
    private final Map<Double, Double> lnValues;
    private static final double E = 2.718281828459045;

    public LnFunctionStub() {
        lnValues = new HashMap<>();

        lnValues.put(1.0, 0.0);
        lnValues.put(E, 1.0);
        lnValues.put(E * E, 2.0);
        lnValues.put(1.0 / E, -1.0);
        lnValues.put(1.0 / (E * E), -2.0);

        lnValues.put(2.0, 0.6931471805599453);
        lnValues.put(3.0, 1.0986122886681098);
        lnValues.put(5.0, 1.6094379124341003);
        lnValues.put(7.0, 1.9459101490553132);
        lnValues.put(10.0, 2.302585092994046);

        lnValues.put(0.1, -2.3025850929940455);
        lnValues.put(0.2, -1.6094379124341003);
        lnValues.put(0.5, -0.6931471805599453);
        lnValues.put(1.5, 0.4054651081081644);
        lnValues.put(2.5, 0.9162907318741551);

        for (double x = 0.1; x <= 10.0; x += 0.1) {
            double roundedX = Math.round(x * 100.0) / 100.0;
            if (!lnValues.containsKey(roundedX)) {
                lnValues.put(roundedX, Math.log(roundedX));
            }
        }
    }

    @Override
    public double calculate(double x, double epsilon) throws IllegalArgumentException {
        if (!isInDomain(x)) {
            throw new IllegalArgumentException(
                    "Input value " + x + " is outside the domain of natural logarithm function");
        }

        double roundedX = Math.round(x * 100.0) / 100.0;

        if (lnValues.containsKey(roundedX)) {
            return lnValues.get(roundedX);
        }

        // Find closest values and perform linear interpolation
        double lowerKey = lnValues.keySet().stream()
                .filter(k -> k < roundedX)
                .max(Double::compare)
                .orElse(0.1);

        double upperKey = lnValues.keySet().stream()
                .filter(k -> k > roundedX)
                .min(Double::compare)
                .orElse(10.0);

        double lowerValue = lnValues.get(lowerKey);
        double upperValue = lnValues.get(upperKey);

        // Linear interpolation: y = y1 + (x - x1) * (y2 - y1) / (x2 - x1)
        return lowerValue + (roundedX - lowerKey) * (upperValue - lowerValue) / (upperKey - lowerKey);
    }

    @Override
    public boolean isInDomain(double x) {
        return x > 0;
    }

    @Override
    public double getBase() {
        return E;
    }

    @Override
    public LnFunctionInterface changeBase(double newBase) {
        if (newBase <= 0 || newBase == 1) {
            throw new IllegalArgumentException("Logarithm base must be positive and not equal to 1");
        }

        if (MathUtils.areEqual(newBase, E, 1e-10)) {
            return this;
        }

        return new LogarithmWithBaseStub(this, newBase);
    }

    /**
     * Helper class to represent a logarithm with a different base
     */
    private static class LogarithmWithBaseStub implements LnFunctionInterface {
        private final LnFunctionStub baseLogFunction;
        private final double base;
        private final double baseLogarithm; // ln(base)

        public LogarithmWithBaseStub(LnFunctionStub baseLogFunction, double base) {
            this.baseLogFunction = baseLogFunction;
            this.base = base;
            this.baseLogarithm = baseLogFunction.calculate(base, 1e-15);
        }

        @Override
        public double calculate(double x, double epsilon) throws IllegalArgumentException {
            // log_b(x) = ln(x) / ln(b)
            double lnX = baseLogFunction.calculate(x, epsilon);
            return lnX / baseLogarithm;
        }

        @Override
        public boolean isInDomain(double x) {
            return baseLogFunction.isInDomain(x);
        }

        @Override
        public double getBase() {
            return base;
        }

        @Override
        public LnFunctionInterface changeBase(double newBase) {
            if (MathUtils.areEqual(newBase, base, 1e-10)) {
                return this;
            }
            return baseLogFunction.changeBase(newBase);
        }
    }
}
