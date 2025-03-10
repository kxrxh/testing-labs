package ru.functions.logarithmic;

/**
 * Stub implementation of logarithm base 5
 */
public class Log5FunctionStub implements LogarithmicFunction {

    // Lookup table for some common log5 values
    private static final double[][] LOOKUP_TABLE = {
            { 0.2, -1.0 },
            { 0.04, -2.0 },
            { 1.0, 0.0 },
            { 5.0, 1.0 },
            { 25.0, 2.0 },
            { 125.0, 3.0 },
            { 625.0, 4.0 }
    };

    public Log5FunctionStub() {
        // No-arg constructor for stub
    }

    @Override
    public double calculate(double x, double epsilon) throws IllegalArgumentException {
        if (!isInDomain(x)) {
            throw new IllegalArgumentException("Input value " + x + " is outside the domain of log base 5");
        }

        // Check lookup table for exact values
        for (double[] entry : LOOKUP_TABLE) {
            if (Math.abs(x - entry[0]) < epsilon) {
                return entry[1];
            }
        }

        // For values not in the lookup table, use a simple approximation
        return Math.log(x) / Math.log(5.0);
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
    public LogarithmicFunction changeBase(double newBase) {
        if (Math.abs(newBase - 5.0) < 1e-10) {
            return this;
        }

        if (newBase == 10.0) {
            return new Log10FunctionStub();
        }

        if (newBase == 2.0) {
            return new Log2FunctionStub();
        }

        // For other bases, return a simple stub
        return new LogarithmicFunction() {
            @Override
            public double calculate(double x, double epsilon) {
                if (!isInDomain(x)) {
                    throw new IllegalArgumentException("Input value is outside the domain");
                }
                return Math.log(x) / Math.log(newBase);
            }

            @Override
            public boolean isInDomain(double x) {
                return x > 0;
            }

            @Override
            public double getBase() {
                return newBase;
            }

            @Override
            public LogarithmicFunction changeBase(double b) {
                return Log5FunctionStub.this.changeBase(b);
            }
        };
    }
}
