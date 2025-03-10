package ru.functions.logarithmic;

/**
 * Implementation of logarithm base 10 using natural logarithm and the identity
 * log_10(x) = ln(x) / ln(10)
 */
public class Log10Function implements LogarithmicFunction {
    private final LnFunction lnFunction;
    private final LogarithmicFunction log10Function;

    public Log10Function(LnFunction lnFunction) {
        this.lnFunction = lnFunction;
        // Use the changeBase method from LnFunction to create a logarithm with base 10
        this.log10Function = lnFunction.changeBase(10.0);
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
        if (newBase == 10.0) {
            return this;
        }
        return lnFunction.changeBase(newBase);
    }
}
