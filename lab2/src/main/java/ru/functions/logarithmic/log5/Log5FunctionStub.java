package ru.functions.logarithmic.log5;

import ru.functions.utils.MathUtils;
import ru.functions.logarithmic.ln.LnFunctionStub;

import java.util.HashMap;
import java.util.Map;

public class Log5FunctionStub implements Log5FunctionInterface {
    private final Map<Double, Double> log5Values;
    private final LnFunctionStub lnFunctionStub;

    public Log5FunctionStub() {
        this(new LnFunctionStub());
    }

    public Log5FunctionStub(LnFunctionStub lnFunctionStub) {
        this.lnFunctionStub = lnFunctionStub;
        log5Values = new HashMap<>();

        log5Values.put(1.0, 0.0);
        log5Values.put(5.0, 1.0);
        log5Values.put(25.0, 2.0);
        log5Values.put(125.0, 3.0);
        log5Values.put(625.0, 4.0);

        log5Values.put(0.2, -1.0);
        log5Values.put(0.04, -2.0);
        log5Values.put(0.008, -3.0);
        log5Values.put(0.0016, -4.0);

        log5Values.put(2.0, 0.43067655807339306);
        log5Values.put(3.0, 0.6826061944859288);
        log5Values.put(4.0, 0.8613531161467861);
        log5Values.put(6.0, 1.1132827525593785);
        log5Values.put(7.0, 1.2089538714394876);
        log5Values.put(8.0, 1.2920296722235293);
        log5Values.put(9.0, 1.3650127013394794);
        log5Values.put(10.0, 1.4306765580733931);

        for (double x = 0.1; x <= 10.0; x += 0.1) {
            double roundedX = Math.round(x * 100.0) / 100.0;
            if (!log5Values.containsKey(roundedX)) {
                log5Values.put(roundedX, Math.log(roundedX) / Math.log(5));
            }
        }
    }

    @Override
    public double calculate(double x, double epsilon) throws IllegalArgumentException {
        if (!isInDomain(x)) {
            throw new IllegalArgumentException("Input value " + x + " is outside the domain of log base 5");
        }

        double roundedX = Math.round(x * 100.0) / 100.0;

        if (log5Values.containsKey(roundedX)) {
            return log5Values.get(roundedX);
        }

        // Use ln(x)/ln(5) for values not in the table
        return lnFunctionStub.calculate(x, epsilon) / lnFunctionStub.calculate(5.0, epsilon);
    }

    @Override
    public boolean isInDomain(double x) {
        return x > 0;
    }

    @Override
    public double getBase() {
        return 5.0;
    }

    @Override
    public Log5FunctionInterface changeBase(double newBase) {
        if (newBase <= 0 || newBase == 1) {
            throw new IllegalArgumentException("Logarithm base must be positive and not equal to 1");
        }

        if (MathUtils.areEqual(newBase, 5.0, 1e-10)) {
            return this;
        }

        return new LogarithmWithBaseStub(this, newBase);
    }

    /**
     * Helper class to represent a logarithm with a different base
     */
    private static class LogarithmWithBaseStub implements Log5FunctionInterface {
        private final Log5FunctionStub baseLogFunction;
        private final double base;
        private final double baseLogarithm; // log_5(base)

        public LogarithmWithBaseStub(Log5FunctionStub baseLogFunction, double base) {
            this.baseLogFunction = baseLogFunction;
            this.base = base;
            this.baseLogarithm = baseLogFunction.calculate(base, 1e-15);
        }

        @Override
        public double calculate(double x, double epsilon) throws IllegalArgumentException {
            // log_b(x) = log_5(x) / log_5(b)
            double log5X = baseLogFunction.calculate(x, epsilon);
            return log5X / baseLogarithm;
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
        public Log5FunctionInterface changeBase(double newBase) {
            if (MathUtils.areEqual(newBase, base, 1e-10)) {
                return this;
            }
            return baseLogFunction.changeBase(newBase);
        }
    }
}
