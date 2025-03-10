package ru.functions.system;

import ru.functions.trigonometric.cos.CosFunctionInterface;
import ru.functions.trigonometric.csc.CscFunctionInterface;
import ru.functions.trigonometric.sec.SecFunctionInterface;
import ru.functions.trigonometric.sin.SinFunctionInterface;
import ru.functions.utils.Function;

/**
 * Stub implementation of the function for x ≤ 0: (((((sec(x) * csc(x)) /
 * cos(x)) - sec(x)) ^ 2) - sin(x))
 */
public class NegativeDomainFunctionStub implements Function {
    private final SinFunctionInterface sinFunctionStub;
    private final CosFunctionInterface cosFunctionStub;
    private final SecFunctionInterface secFunctionStub;
    private final CscFunctionInterface cscFunctionStub;

    public NegativeDomainFunctionStub(
            SinFunctionInterface sinFunctionStub,
            CosFunctionInterface cosFunctionStub,
            SecFunctionInterface secFunctionStub,
            CscFunctionInterface cscFunctionStub) {
        this.sinFunctionStub = sinFunctionStub;
        this.cosFunctionStub = cosFunctionStub;
        this.secFunctionStub = secFunctionStub;
        this.cscFunctionStub = cscFunctionStub;
    }

    @Override
    public double calculate(double x, double epsilon) throws IllegalArgumentException {
        if (!isInDomain(x)) {
            throw new IllegalArgumentException("Input value " + x + " is outside the domain of the function (x ≤ 0)");
        }

        // Calculation: (((((sec(x) * csc(x)) / cos(x)) - sec(x)) ^ 2) - sin(x))
        double sin = sinFunctionStub.calculate(x, epsilon);
        double cos = cosFunctionStub.calculate(x, epsilon);
        double sec = secFunctionStub.calculate(x, epsilon);
        double csc = cscFunctionStub.calculate(x, epsilon);

        // Perform the step-by-step calculation with proper precision management
        double secTimesCsc = sec * csc;
        double secTimesCscDividedByCos = secTimesCsc / cos;
        double secTimesCscDividedByCosMinusSec = secTimesCscDividedByCos - sec;
        double squared = secTimesCscDividedByCosMinusSec * secTimesCscDividedByCosMinusSec;
        double result = squared - sin;

        return result;
    }

    @Override
    public boolean isInDomain(double x) {
        // Domain: x ≤ 0 AND not a multiple of π/2 (for sec and csc)
        if (x > 0) {
            return false;
        }

        // Check if x is in the domain of all component functions
        return secFunctionStub.isInDomain(x) && cscFunctionStub.isInDomain(x);
    }
}
