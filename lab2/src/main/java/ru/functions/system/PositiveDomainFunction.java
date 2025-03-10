package ru.functions.system;

import ru.functions.logarithmic.log10.Log10FunctionInterface;
import ru.functions.logarithmic.log2.Log2FunctionInterface;
import ru.functions.logarithmic.log5.Log5FunctionInterface;
import ru.functions.utils.Function;

/**
 * Implementation of the function for x > 0: (((((log_2(x) + log_10(x)) ^ 2) -
 * log_2(x)) - log_10(x)) - log_5(x))
 */
public class PositiveDomainFunction implements Function {
    private final Log2FunctionInterface log2Function;
    private final Log10FunctionInterface log10Function;
    private final Log5FunctionInterface log5Function;

    public PositiveDomainFunction(
            Log2FunctionInterface log2Function,
            Log10FunctionInterface log10Function,
            Log5FunctionInterface log5Function) {
        this.log2Function = log2Function;
        this.log10Function = log10Function;
        this.log5Function = log5Function;
    }

    @Override
    public double calculate(double x, double epsilon) throws IllegalArgumentException {
        if (!isInDomain(x)) {
            throw new IllegalArgumentException("Input value " + x + " is outside the domain of the function (x > 0)");
        }

        // Calculate: (((((log_2(x) + log_10(x)) ^ 2) - log_2(x)) - log_10(x)) -
        // log_5(x))
        double log2 = log2Function.calculate(x, epsilon);
        double log10 = log10Function.calculate(x, epsilon);
        double log5 = log5Function.calculate(x, epsilon);

        // Perform the step-by-step calculation with proper precision management
        double log2PlusLog10 = log2 + log10;
        double squared = log2PlusLog10 * log2PlusLog10;
        double squaredMinusLog2 = squared - log2;
        double squaredMinusLog2MinusLog10 = squaredMinusLog2 - log10;
        double result = squaredMinusLog2MinusLog10 - log5;

        return result;
    }

    @Override
    public boolean isInDomain(double x) {
        // Domain: x > 0
        return x > 0;
    }
}
