package ru.functions.system;

import ru.functions.trigonometric.CosFunction;
import ru.functions.trigonometric.CscFunction;
import ru.functions.trigonometric.SecFunction;
import ru.functions.trigonometric.SinFunction;
import ru.functions.trigonometric.TrigonometricFunction;
import ru.functions.utils.Function;

/**
 * Implementation of the function for x ≤ 0: (((((sec(x) * csc(x)) / cos(x)) -
 * sec(x)) ^ 2) - sin(x))
 */
public class NegativeDomainFunction implements Function {
    private final TrigonometricFunction sinFunction;
    private final TrigonometricFunction cosFunction;
    private final TrigonometricFunction secFunction;
    private final TrigonometricFunction cscFunction;

    public NegativeDomainFunction(
            SinFunction sinFunction,
            CosFunction cosFunction,
            SecFunction secFunction,
            CscFunction cscFunction) {
        this.sinFunction = sinFunction;
        this.cosFunction = cosFunction;
        this.secFunction = secFunction;
        this.cscFunction = cscFunction;
    }

    @Override
    public double calculate(double x, double epsilon) throws IllegalArgumentException {
        if (!isInDomain(x)) {
            throw new IllegalArgumentException("Input value " + x + " is outside the domain of the function (x ≤ 0)");
        }

        // Calculation: (((((sec(x) * csc(x)) / cos(x)) - sec(x)) ^ 2) - sin(x))
        double sin = sinFunction.calculate(x, epsilon);
        double cos = cosFunction.calculate(x, epsilon);
        double sec = secFunction.calculate(x, epsilon);
        double csc = cscFunction.calculate(x, epsilon);

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
        return secFunction.isInDomain(x) && cscFunction.isInDomain(x);
    }
}
