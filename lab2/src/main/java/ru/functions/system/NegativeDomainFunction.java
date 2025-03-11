package ru.functions.system;

import ru.functions.trigonometric.cos.CosFunctionInterface;
import ru.functions.trigonometric.csc.CscFunctionInterface;
import ru.functions.trigonometric.sec.SecFunctionInterface;
import ru.functions.trigonometric.sin.SinFunctionInterface;
import ru.functions.utils.Function;

/**
 * Implementation of the function for x ≤ 0: (((((sec(x) * csc(x)) / cos(x)) -
 * sec(x)) ^ 2) - sin(x))
 */
public class NegativeDomainFunction implements Function {
    private final SinFunctionInterface sinFunction;
    private final CosFunctionInterface cosFunction;
    private final SecFunctionInterface secFunction;
    private final CscFunctionInterface cscFunction;

    public NegativeDomainFunction(
            SinFunctionInterface sinFunction,
            CosFunctionInterface cosFunction,
            SecFunctionInterface secFunction,
            CscFunctionInterface cscFunction) {
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

        // Formula: (((((sec(x) * csc(x)) / cos(x)) - sec(x)) ^ 2) - sin(x))
        double tightEpsilon = epsilon / 100;

        double sin = sinFunction.calculate(x, tightEpsilon);
        double cos = cosFunction.calculate(x, tightEpsilon);
        double sec = secFunction.calculate(x, tightEpsilon);
        double csc = cscFunction.calculate(x, tightEpsilon);

        double secTimesCsc = sec * csc; // sec(x) * csc(x)
        double secTimesCscDividedByCos = secTimesCsc / cos; // (sec(x) * csc(x)) / cos(x)
        double secTimesCscDividedByCosMinusSec = secTimesCscDividedByCos - sec; // ((sec(x) * csc(x)) / cos(x)) - sec(x)
        double squared = secTimesCscDividedByCosMinusSec * secTimesCscDividedByCosMinusSec; // (((sec(x) * csc(x)) /
                                                                                            // cos(x)) - sec(x))^2
        double result = squared - sin; // (((sec(x) * csc(x)) / cos(x)) - sec(x))^2 - sin(x)

        return result;
    }

    @Override
    public boolean isInDomain(double x) {
        if (x > 0) {
            return false;
        }

        if (x > -1e-8) {
            return true;
        }

        return secFunction.isInDomain(x) && cscFunction.isInDomain(x);
    }
}
