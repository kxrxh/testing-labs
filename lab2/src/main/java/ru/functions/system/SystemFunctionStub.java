package ru.functions.system;

import ru.functions.utils.MathUtils;

/**
 * Stub implementation of the main system function that combines both domains:
 * - For x ≤ 0: (((((sec(x) * csc(x)) / cos(x)) - sec(x)) ^ 2) - sin(x))
 * - For x > 0: (((((log_2(x) + log_10(x)) ^ 2) - log_2(x)) - log_10(x)) -
 * log_5(x))
 */
public class SystemFunctionStub implements SystemFunctionInterface {
    private final NegativeDomainFunctionStub negativeDomainFunctionStub;
    private final PositiveDomainFunctionStub positiveDomainFunctionStub;

    // Domain and formula descriptions
    private static final String[] DOMAIN_DESCRIPTIONS = {
            "x ≤ 0, x ≠ -π/2, -π, -3π/2, ...",
            "x > 0"
    };

    private static final String[] FORMULA_DESCRIPTIONS = {
            "(((((sec(x) * csc(x)) / cos(x)) - sec(x)) ^ 2) - sin(x))",
            "(((((log_2(x) + log_10(x)) ^ 2) - log_2(x)) - log_10(x)) - log_5(x))"
    };

    public SystemFunctionStub(
            NegativeDomainFunctionStub negativeDomainFunctionStub,
            PositiveDomainFunctionStub positiveDomainFunctionStub) {
        this.negativeDomainFunctionStub = negativeDomainFunctionStub;
        this.positiveDomainFunctionStub = positiveDomainFunctionStub;
    }

    @Override
    public double calculate(double x, double epsilon) throws IllegalArgumentException {
        if (!isInDomain(x)) {
            throw new IllegalArgumentException("Input value " + x + " is outside the domain of the system function");
        }

        // Use the appropriate function based on the domain
        if (x <= 0) {
            return negativeDomainFunctionStub.calculate(x, epsilon);
        } else {
            return positiveDomainFunctionStub.calculate(x, epsilon);
        }
    }

    @Override
    public boolean isInDomain(double x) {
        // The system function is defined in the union of both domains,
        // excluding x = 0 and x = -π/2, -π, -3π/2, ...

        if (MathUtils.isZero(x, 1e-10)) {
            return false; // x = 0 is not in the domain
        }

        if (x <= 0) {
            return negativeDomainFunctionStub.isInDomain(x);
        } else {
            return positiveDomainFunctionStub.isInDomain(x);
        }
    }

    @Override
    public int getSubFunctionCount() {
        return 2; // Two sub-functions: negative domain and positive domain
    }

    @Override
    public int getApplicableSubFunction(double x) {
        if (!isInDomain(x)) {
            return -1; // Outside domain
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
}
