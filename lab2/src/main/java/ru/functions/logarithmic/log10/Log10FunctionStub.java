package ru.functions.logarithmic.log10;

import ru.functions.utils.MathUtils;
import ru.functions.logarithmic.ln.LnFunctionStub;

import java.util.HashMap;
import java.util.Map;

public class Log10FunctionStub implements Log10FunctionInterface {
    private final Map<Double, Double> log10Values;
    private final LnFunctionStub lnFunctionStub;

    public Log10FunctionStub() {
        this(new LnFunctionStub());
    }

    public Log10FunctionStub(LnFunctionStub lnFunctionStub) {
        this.lnFunctionStub = lnFunctionStub;
        log10Values = new HashMap<>();

        log10Values.put(1.0, 0.0);
        log10Values.put(10.0, 1.0);
        log10Values.put(100.0, 2.0);
        log10Values.put(1000.0, 3.0);
        log10Values.put(10000.0, 4.0);

        log10Values.put(0.1, -1.0);
        log10Values.put(0.01, -2.0);
        log10Values.put(0.001, -3.0);
        log10Values.put(0.0001, -4.0);

        log10Values.put(2.0, 0.301029995663981);
        log10Values.put(3.0, 0.47712125471966244);
        log10Values.put(4.0, 0.6020599913279624);
        log10Values.put(5.0, 0.6989700043360189);
        log10Values.put(6.0, 0.7781512503836436);
        log10Values.put(7.0, 0.8450980400142568);
        log10Values.put(8.0, 0.9030899869919435);
        log10Values.put(9.0, 0.9542425094393249);

        for (double x = 0.1; x <= 10.0; x += 0.1) {
            double roundedX = Math.round(x * 100.0) / 100.0;
            if (!log10Values.containsKey(roundedX)) {
                log10Values.put(roundedX, Math.log10(roundedX));
            }
        }
    }

    @Override
    public double calculate(double x, double epsilon) throws IllegalArgumentException {
        if (!isInDomain(x)) {
            throw new IllegalArgumentException("Input value " + x + " is outside the domain of log base 10");
        }

        double roundedX = Math.round(x * 100.0) / 100.0;

        if (log10Values.containsKey(roundedX)) {
            return log10Values.get(roundedX);
        }

        // Use ln(x)/ln(10) for values not in the table
        return lnFunctionStub.calculate(x, epsilon) / lnFunctionStub.calculate(10.0, epsilon);
    }

    @Override
    public boolean isInDomain(double x) {
        return x > 0;
    }

    @Override
    public double getBase() {
        return 10.0;
    }

    @Override
    public Log10FunctionInterface changeBase(double newBase) {
        if (newBase <= 0 || newBase == 1) {
            throw new IllegalArgumentException("Logarithm base must be positive and not equal to 1");
        }

        if (MathUtils.areEqual(newBase, 10.0, 1e-10)) {
            return this;
        }

        return new LogarithmWithBaseStub(this, newBase);
    }

    /**
     * Helper class to represent a logarithm with a different base
     */
    private static class LogarithmWithBaseStub implements Log10FunctionInterface {
        private final Log10FunctionStub baseLogFunction;
        private final double base;
        private final double baseLogarithm; // log_10(base)

        public LogarithmWithBaseStub(Log10FunctionStub baseLogFunction, double base) {
            this.baseLogFunction = baseLogFunction;
            this.base = base;
            this.baseLogarithm = baseLogFunction.calculate(base, 1e-15);
        }

        @Override
        public double calculate(double x, double epsilon) throws IllegalArgumentException {
            // log_b(x) = log_10(x) / log_10(b)
            double log10X = baseLogFunction.calculate(x, epsilon);
            return log10X / baseLogarithm;
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
        public Log10FunctionInterface changeBase(double newBase) {
            if (MathUtils.areEqual(newBase, base, 1e-10)) {
                return this;
            }
            return baseLogFunction.changeBase(newBase);
        }
    }
}
