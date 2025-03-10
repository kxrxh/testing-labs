package ru.functions.logarithmic;

/**
 * Stub implementation of logarithm base 10
 */
public class Log10FunctionStub implements LogarithmicFunction {

    // Lookup table for some common log10 values
    private static final double[][] LOOKUP_TABLE = {
            { 0.1, -1.0 },
            { 0.01, -2.0 },
            { 0.001, -3.0 },
            { 1.0, 0.0 },
            { 10.0, 1.0 },
            { 100.0, 2.0 },
            { 1000.0, 3.0 },
            { 10000.0, 4.0 }
    };

    public Log10FunctionStub() {
        // No-arg constructor for stub
    }

    @Override
    public double calculate(double x, double epsilon) throws IllegalArgumentException {
        if (!isInDomain(x)) {
            throw new IllegalArgumentException("Input value " + x + " is outside the domain of log base 10");
        }

        // Check lookup table for exact values
        for (double[] entry : LOOKUP_TABLE) {
            if (Math.abs(x - entry[0]) < epsilon) {
                return entry[1];
            }
        }

        // For values not in the lookup table, use a simple approximation
        return Math.log10(x);
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
    public LogarithmicFunction changeBase(double newBase) {
        if (newBase == 10.0) {
            return this;
        }

        if (newBase == 2.0) {
            return new Log2FunctionStub();
        }

        if (Math.abs(newBase - 5.0) < 1e-10) {
            return new Log5FunctionStub();
        }

        // For other bases, return a simple stub
        return new LogarithmicFunction() {
            @Override
            public double calculate(double x, double epsilon) {
                if (!isInDomain(x)) {
                    throw new IllegalArgumentException("Input value is outside the domain");
                }
                return Math.log10(x) / Math.log10(newBase);
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
                return Log10FunctionStub.this.changeBase(b);
            }
        };
    }
}
