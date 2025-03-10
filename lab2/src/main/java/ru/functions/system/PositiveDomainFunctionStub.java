package ru.functions.system;

import ru.functions.logarithmic.log10.Log10FunctionInterface;
import ru.functions.logarithmic.log2.Log2FunctionInterface;
import ru.functions.logarithmic.log5.Log5FunctionInterface;
import ru.functions.utils.Function;

/**
 * Stub implementation of the function for x > 0: (((((log_2(x) + log_10(x)) ^
 * 2) - log_2(x)) - log_10(x)) - log_5(x))
 */
public class PositiveDomainFunctionStub implements Function {
    private final Log2FunctionInterface log2FunctionStub;
    private final Log10FunctionInterface log10FunctionStub;
    private final Log5FunctionInterface log5FunctionStub;

    public PositiveDomainFunctionStub(
            Log2FunctionInterface log2FunctionStub,
            Log10FunctionInterface log10FunctionStub,
            Log5FunctionInterface log5FunctionStub) {
        this.log2FunctionStub = log2FunctionStub;
        this.log10FunctionStub = log10FunctionStub;
        this.log5FunctionStub = log5FunctionStub;
    }

    @Override
    public double calculate(double x, double epsilon) throws IllegalArgumentException {
        if (!isInDomain(x)) {
            throw new IllegalArgumentException("Input value " + x + " is outside the domain of the function (x > 0)");
        }

        // Calculate: (((((log_2(x) + log_10(x)) ^ 2) - log_2(x)) - log_10(x)) -
        // log_5(x))
        double log2 = log2FunctionStub.calculate(x, epsilon);
        double log10 = log10FunctionStub.calculate(x, epsilon);
        double log5 = log5FunctionStub.calculate(x, epsilon);

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
