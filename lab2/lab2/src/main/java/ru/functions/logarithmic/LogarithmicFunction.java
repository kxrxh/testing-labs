package ru.functions.logarithmic;

import ru.functions.utils.Function;

/**
 * Interface representing a logarithmic function
 */
public interface LogarithmicFunction extends Function {
    /**
     * Gets the base of the logarithm
     *
     * @return the base of the logarithm
     */
    double getBase();

    /**
     * Calculates the logarithm of the product: log(a*b) = log(a) + log(b)
     *
     * @param a       first argument
     * @param b       second argument
     * @param epsilon The desired accuracy of the calculation
     * @return The logarithm of the product
     * @throws IllegalArgumentException if any input is outside the domain of the
     *                                  function
     */
    default double calculateProduct(double a, double b, double epsilon) throws IllegalArgumentException {
        if (!isInDomain(a) || !isInDomain(b)) {
            throw new IllegalArgumentException("Inputs must be within the domain of the logarithm function");
        }
        return calculate(a, epsilon) + calculate(b, epsilon);
    }

    /**
     * Calculates the logarithm of the quotient: log(a/b) = log(a) - log(b)
     *
     * @param a       numerator
     * @param b       denominator
     * @param epsilon The desired accuracy of the calculation
     * @return The logarithm of the quotient
     * @throws IllegalArgumentException if any input is outside the domain of the
     *                                  function
     */
    default double calculateQuotient(double a, double b, double epsilon) throws IllegalArgumentException {
        if (!isInDomain(a) || !isInDomain(b)) {
            throw new IllegalArgumentException("Inputs must be within the domain of the logarithm function");
        }
        return calculate(a, epsilon) - calculate(b, epsilon);
    }

    /**
     * Calculates the logarithm of the power: log(a^n) = n*log(a)
     *
     * @param a       the base
     * @param n       the exponent
     * @param epsilon The desired accuracy of the calculation
     * @return The logarithm of the power
     * @throws IllegalArgumentException if the input is outside the domain of the
     *                                  function
     */
    default double calculatePower(double a, double n, double epsilon) throws IllegalArgumentException {
        if (!isInDomain(a)) {
            throw new IllegalArgumentException("Input must be within the domain of the logarithm function");
        }
        return n * calculate(a, epsilon);
    }

    /**
     * Converts to a different logarithm base
     *
     * @param newBase the new logarithm base
     * @return a logarithm function with the new base
     */
    LogarithmicFunction changeBase(double newBase);
}
