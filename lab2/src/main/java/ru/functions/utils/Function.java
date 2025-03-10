package ru.functions.utils;

/**
 * Top-level interface representing a mathematical function with a single input
 */
public interface Function {
    /**
     * Calculates the result of the function for the given input
     *
     * @param x       The input value
     * @param epsilon The desired accuracy of the calculation
     * @return The calculated function value
     * @throws IllegalArgumentException if the input is outside the domain of the
     *                                  function
     */
    double calculate(double x, double epsilon) throws IllegalArgumentException;

    /**
     * Checks if the given input is within the domain of the function
     *
     * @param x The input value to check
     * @return true if the input is within the domain, false otherwise
     */
    boolean isInDomain(double x);
}
