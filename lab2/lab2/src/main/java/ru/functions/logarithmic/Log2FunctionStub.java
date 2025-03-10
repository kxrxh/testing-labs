package ru.functions.logarithmic;

import ru.functions.utils.MathUtils;

/**
 * Stub implementation of logarithm base 2 using the natural logarithm function
 * stub
 */
public class Log2FunctionStub implements LogarithmicFunction {
    private final LogarithmicFunction lnFunctionStub;
    private final LogarithmicFunction log2Function;

    public Log2FunctionStub(LnFunctionStub lnFunctionStub) {
        this.lnFunctionStub = lnFunctionStub;
        // Use the changeBase method to create a logarithm with base 2
        this.log2Function = lnFunctionStub.changeBase(2.0);
    }

    @Override
    public double calculate(double x, double epsilon) throws IllegalArgumentException {
        return log2Function.calculate(x, epsilon);
    }

    @Override
    public boolean isInDomain(double x) {
        return log2Function.isInDomain(x);
    }

    @Override
    public double getBase() {
        return 2.0;
    }

    @Override
    public LogarithmicFunction changeBase(double newBase) {
        if (MathUtils.areEqual(newBase, 2.0, 1e-10)) {
            return this;
        }
        return lnFunctionStub.changeBase(newBase);
    }
}
