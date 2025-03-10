package ru.functions.logarithmic;

/**
 * Implementation of logarithm base 2 using natural logarithm and the identity
 * log_2(x) = ln(x) / ln(2)
 */
public class Log2Function implements LogarithmicFunction {
    private final LnFunction lnFunction;
    private final LogarithmicFunction log2Function;

    public Log2Function(LnFunction lnFunction) {
        this.lnFunction = lnFunction;
        // Use the changeBase method from LnFunction to create a logarithm with base 2
        this.log2Function = lnFunction.changeBase(2.0);
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
        if (newBase == 2.0) {
            return this;
        }
        return lnFunction.changeBase(newBase);
    }
}
