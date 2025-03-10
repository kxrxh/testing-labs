package ru.functions.logarithmic;

/**
 * Implementation of logarithm base 5 using natural logarithm and the identity
 * log_5(x) = ln(x) / ln(5)
 */
public class Log5Function implements LogarithmicFunction {
    private final LnFunction lnFunction;
    private final LogarithmicFunction log5Function;

    public Log5Function(LnFunction lnFunction) {
        this.lnFunction = lnFunction;
        // Use the changeBase method from LnFunction to create a logarithm with base 5
        this.log5Function = lnFunction.changeBase(5.0);
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
        if (newBase == 5.0) {
            return this;
        }
        return lnFunction.changeBase(newBase);
    }
}
