package ru.functions.trigonometric;

import ru.functions.utils.MathUtils;

/**
 * Implementation of sine function using Taylor series expansion
 */
public class SinFunction implements TrigonometricFunction {

    @Override
    public double calculate(double x, double epsilon) throws IllegalArgumentException {
        if (!isInDomain(x)) {
            throw new IllegalArgumentException("Input value is outside the domain of sine function");
        }

        // Normalize x to [-PI, PI] to improve convergence
        x = MathUtils.normalizeAngle(x);

        double result = 0.0;
        double term = x;
        int n = 1;

        // Taylor series for sin(x): x - x^3/3! + x^5/5! - x^7/7! + ...
        while (Math.abs(term) > epsilon) {
            result += term;

            // Calculate next term
            term = -term * x * x / ((2 * n) * (2 * n + 1));
            n++;
        }

        return result;
    }

    @Override
    public boolean isInDomain(double x) {
        // Sin(x) is defined for all real numbers
        return true;
    }

    @Override
    public double getPeriod() {
        return MathUtils.TWO_PI; // 2π
    }

    @Override
    public int getParity() {
        return 1; // Odd function: sin(-x) = -sin(x)
    }

    @Override
    public TrigonometricFunction getDerivative() {
        return new CosFunction(this); // The derivative of sin(x) is cos(x)
    }
}
