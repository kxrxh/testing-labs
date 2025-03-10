package ru.functions.logarithmic;

/**
 * Stub implementation of logarithm base 2
 */
public class Log2FunctionStub implements LogarithmicFunction {

    // Lookup table for some common log2 values
    private static final double[][] LOOKUP_TABLE = {
            { 0.25, -2.0 },
            { 0.5, -1.0 },
            { 1.0, 0.0 },
            { 2.0, 1.0 },
            { 4.0, 2.0 },
            { 8.0, 3.0 },
            { 16.0, 4.0 },
            { 32.0, 5.0 },
            { 64.0, 6.0 },
            { 128.0, 7.0 },
            { 256.0, 8.0 }
    };

    public Log2FunctionStub() {
        // No-arg constructor for stub
    }

    @Override
    public double calculate(double x, double epsilon) throws IllegalArgumentException {
        if (!isInDomain(x)) {
            throw new IllegalArgumentException("Input value " + x + " is outside the domain of log base 2");
        }

        // Check lookup table for exact values
        for (double[] entry : LOOKUP_TABLE) {
            if (Math.abs(x - entry[0]) < epsilon) {
                return entry[1];
            }
        }

        // For values not in the lookup table, use a simple approximation
        return Math.log(x) / Math.log(2.0);
    }

    @Override
    public boolean isInDomain(double x) {
        return x > 0;
    }

    @Override
    public double getBase() {
        return 2.0;
    }

    @Override
    public LogarithmicFunction changeBase(double newBase) {
        if (newBase == 2.0) {
            return this;
        }

        if (newBase == 10.0) {
            return new Log10FunctionStub();
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
                return Log2FunctionStub.this.changeBase(b);
            }
        };
    }
}
