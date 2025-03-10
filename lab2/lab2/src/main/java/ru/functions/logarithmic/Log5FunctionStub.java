package ru.functions.logarithmic;

import ru.functions.utils.MathUtils;

/**
 * Stub implementation of logarithm base 5 using the natural logarithm function
 * stub
 */
public class Log5FunctionStub implements LogarithmicFunction {
    private final LogarithmicFunction lnFunctionStub;
    private final LogarithmicFunction log5Function;

    public Log5FunctionStub(LnFunctionStub lnFunctionStub) {
        this.lnFunctionStub = lnFunctionStub;
        // Use the changeBase method to create a logarithm with base 5
        this.log5Function = lnFunctionStub.changeBase(5.0);
    }

    @Override
    public double calculate(double x, double epsilon) throws IllegalArgumentException {
        return log5Function.calculate(x, epsilon);
    }

    @Override
    public boolean isInDomain(double x) {
        return log5Function.isInDomain(x);
    }

    @Override
    public double getBase() {
        return 5.0;
    }

    @Override
    public LogarithmicFunction changeBase(double newBase) {
        if (MathUtils.areEqual(newBase, 5.0, 1e-10)) {
            return this;
        }
        return lnFunctionStub.changeBase(newBase);
    }
}
