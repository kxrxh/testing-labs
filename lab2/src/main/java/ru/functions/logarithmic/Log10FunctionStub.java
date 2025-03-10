package ru.functions.logarithmic;

import ru.functions.utils.MathUtils;

/**
 * Stub implementation of logarithm base 10 using the natural logarithm function
 * stub
 */
public class Log10FunctionStub implements LogarithmicFunction {
    private final LogarithmicFunction lnFunctionStub;
    private final LogarithmicFunction log10Function;

    public Log10FunctionStub(LnFunctionStub lnFunctionStub) {
        this.lnFunctionStub = lnFunctionStub;
        // Use the changeBase method to create a logarithm with base 10
        this.log10Function = lnFunctionStub.changeBase(10.0);
    }

    @Override
    public double calculate(double x, double epsilon) throws IllegalArgumentException {
        return log10Function.calculate(x, epsilon);
    }

    @Override
    public boolean isInDomain(double x) {
        return log10Function.isInDomain(x);
    }

    @Override
    public double getBase() {
        return 10.0;
    }

    @Override
    public LogarithmicFunction changeBase(double newBase) {
        if (MathUtils.areEqual(newBase, 10.0, 1e-10)) {
            return this;
        }
        return lnFunctionStub.changeBase(newBase);
    }
}
