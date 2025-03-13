package ru.functions.logarithmic.log2;

import ru.functions.utils.MathUtils;
import ru.functions.logarithmic.ln.LnFunctionStub;

import java.util.HashMap;
import java.util.Map;

public class Log2FunctionStub implements Log2FunctionInterface {
    private final Map<Double, Double> log2Values;
    private final LnFunctionStub lnFunctionStub;

    public Log2FunctionStub() {
        this(new LnFunctionStub());
    }

    public Log2FunctionStub(LnFunctionStub lnFunctionStub) {
        this.lnFunctionStub = lnFunctionStub;
        log2Values = new HashMap<>();

        log2Values.put(1.0, 0.0);
        log2Values.put(2.0, 1.0);
        log2Values.put(4.0, 2.0);
        log2Values.put(8.0, 3.0);
        log2Values.put(16.0, 4.0);
        log2Values.put(32.0, 5.0);
        log2Values.put(64.0, 6.0);
        log2Values.put(128.0, 7.0);
        log2Values.put(256.0, 8.0);
        log2Values.put(512.0, 9.0);
        log2Values.put(1024.0, 10.0);

        log2Values.put(0.5, -1.0);
        log2Values.put(0.25, -2.0);
        log2Values.put(0.125, -3.0);
        log2Values.put(0.0625, -4.0);

        log2Values.put(3.0, 1.5849625007211563);
        log2Values.put(5.0, 2.321928094887362);
        log2Values.put(6.0, 2.584962500721156);
        log2Values.put(7.0, 2.807354922057604);
        log2Values.put(9.0, 3.1699250014423126);
        log2Values.put(10.0, 3.321928094887362);

        for (double x = 0.1; x <= 10.0; x += 0.1) {
            double roundedX = Math.round(x * 100.0) / 100.0;
            if (!log2Values.containsKey(roundedX)) {
                log2Values.put(roundedX, Math.log(roundedX) / Math.log(2));
            }
        }
    }

    @Override
    public double calculate(double x, double epsilon) throws IllegalArgumentException {
        if (!isInDomain(x)) {
            throw new IllegalArgumentException("Input value " + x + " is outside the domain of log base 2");
        }

        double roundedX = Math.round(x * 100.0) / 100.0;

        if (log2Values.containsKey(roundedX)) {
            return log2Values.get(roundedX);
        }

        // Use ln(x)/ln(2) for values not in the table
        return lnFunctionStub.calculate(x, epsilon) / lnFunctionStub.calculate(2.0, epsilon);
    }

    @Override
    public boolean isInDomain(double x) {
        // log2(x) is defined for x > 0
        return x > 0;
    }

    @Override
    public double getBase() {
        return 2.0;
    }

    @Override
    public Log2FunctionInterface changeBase(double newBase) {
        if (newBase <= 0 || newBase == 1) {
            throw new IllegalArgumentException("Logarithm base must be positive and not equal to 1");
        }

        if (MathUtils.areEqual(newBase, 2.0, 1e-10)) {
            return this;
        }

        return new LogarithmWithBaseStub(this, newBase);
    }

    /**
     * Helper class to represent a logarithm with a different base
     */
    private static class LogarithmWithBaseStub implements Log2FunctionInterface {
        private final Log2FunctionStub baseLogFunction;
        private final double base;
        private final double baseLogarithm; // log_2(base)

        public LogarithmWithBaseStub(Log2FunctionStub baseLogFunction, double base) {
            this.baseLogFunction = baseLogFunction;
            this.base = base;
            this.baseLogarithm = baseLogFunction.calculate(base, 1e-15);
        }

        @Override
        public double calculate(double x, double epsilon) throws IllegalArgumentException {
            // log_b(x) = log_2(x) / log_2(b)
            double log2X = baseLogFunction.calculate(x, epsilon);
            return log2X / baseLogarithm;
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
        public Log2FunctionInterface changeBase(double newBase) {
            if (MathUtils.areEqual(newBase, base, 1e-10)) {
                return this;
            }
            return baseLogFunction.changeBase(newBase);
        }
    }
}
