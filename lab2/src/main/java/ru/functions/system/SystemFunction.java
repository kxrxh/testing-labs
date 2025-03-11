package ru.functions.system;

import ru.functions.utils.Function;
import ru.functions.utils.MathUtils;

/**
 * The main system function that combines both domains:
 * - For x ≤ 0: (((((sec(x) * csc(x)) / cos(x)) - sec(x)) ^ 2) - sin(x))
 * - For x > 0: (((((log_2(x) + log_10(x)) ^ 2) - log_2(x)) - log_10(x)) -
 * log_5(x))
 */
public class SystemFunction implements SystemFunctionInterface {
    private final Function negativeDomainFunction;
    private final Function positiveDomainFunction;

    private static final String[] DOMAIN_DESCRIPTIONS = {
            "x ≤ 0, x ≠ -π/2, -π, -3π/2, ...",
            "x > 0"
    };

    private static final String[] FORMULA_DESCRIPTIONS = {
            "(((((sec(x) * csc(x)) / cos(x)) - sec(x)) ^ 2) - sin(x))",
            "(((((log_2(x) + log_10(x)) ^ 2) - log_2(x)) - log_10(x)) - log_5(x))"
    };

    public SystemFunction(
            Function negativeDomainFunction,
            Function positiveDomainFunction) {
        this.negativeDomainFunction = negativeDomainFunction;
        this.positiveDomainFunction = positiveDomainFunction;
    }

    public SystemFunction(
            NegativeDomainFunction negativeDomainFunction,
            PositiveDomainFunction positiveDomainFunction) {
        this((Function) negativeDomainFunction, (Function) positiveDomainFunction);
    }

    public SystemFunction(
            NegativeDomainFunction negativeDomainFunction,
            PositiveDomainFunctionStub positiveDomainFunctionStub) {
        this((Function) negativeDomainFunction, (Function) positiveDomainFunctionStub);
    }

    public SystemFunction(
            NegativeDomainFunctionStub negativeDomainFunctionStub,
            PositiveDomainFunction positiveDomainFunction) {
        this((Function) negativeDomainFunctionStub, (Function) positiveDomainFunction);
    }

    @Override
    public double calculate(double x, double epsilon) throws IllegalArgumentException {
        if (!isInDomain(x)) {
            throw new IllegalArgumentException("Input value " + x + " is outside the domain of the system function");
        }

        if (x <= 0) {
            return negativeDomainFunction.calculate(x, epsilon);
        } else {
            return positiveDomainFunction.calculate(x, epsilon);
        }
    }

    @Override
    public boolean isInDomain(double x) {
        // The system function is defined in the union of both domains,
        // excluding x = 0 and x = -π/2, -π, -3π/2, ...

        if (x == 0.0) {
            return false;
        }

        if (Math.abs(x) < 1e-10) {
            return x < 0;
        }

        if (x < 0) {
            return negativeDomainFunction.isInDomain(x);
        } else {
            return positiveDomainFunction.isInDomain(x);
        }
    }

    @Override
    public int getSubFunctionCount() {
        return 2;
    }

    @Override
    public int getApplicableSubFunction(double x) {
        if (!isInDomain(x)) {
            return -1;
        }

        return (x <= 0) ? 0 : 1; // 0 for negative domain, 1 for positive domain
    }

    @Override
    public String getSubFunctionDomain(int subFunctionIndex) throws IndexOutOfBoundsException {
        if (subFunctionIndex < 0 || subFunctionIndex >= getSubFunctionCount()) {
            throw new IndexOutOfBoundsException("Sub-function index out of range: " + subFunctionIndex);
        }

        return DOMAIN_DESCRIPTIONS[subFunctionIndex];
    }

    @Override
    public String getSubFunctionFormula(int subFunctionIndex) throws IndexOutOfBoundsException {
        if (subFunctionIndex < 0 || subFunctionIndex >= getSubFunctionCount()) {
            throw new IndexOutOfBoundsException("Sub-function index out of range: " + subFunctionIndex);
        }

        return FORMULA_DESCRIPTIONS[subFunctionIndex];
    }

    @Override
    public Function getNegativeDomainFunction() {
        return negativeDomainFunction;
    }

    @Override
    public Function getPositiveDomainFunction() {
        return positiveDomainFunction;
    }

    @Override
    public boolean isUsingStubs() {
        return false;
    }
}
