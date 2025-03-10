package ru.functions.system;

import ru.functions.utils.Function;

/**
 * Interface representing a composite system function that combines multiple
 * sub-functions
 * with different domains
 */
public interface SystemFunctionInterface extends Function {

    /**
     * Returns the number of sub-functions in the system
     *
     * @return the number of sub-functions
     */
    int getSubFunctionCount();

    /**
     * Determines which sub-function applies to the given input
     *
     * @param x the input value
     * @return the index of the applicable sub-function, or -1 if x is outside the
     *         domain
     */
    int getApplicableSubFunction(double x);

    /**
     * Gets the domain description for a specific sub-function
     *
     * @param subFunctionIndex the index of the sub-function
     * @return a string description of the domain (e.g., "x ≤ 0", "x > 0")
     * @throws IndexOutOfBoundsException if the index is invalid
     */
    String getSubFunctionDomain(int subFunctionIndex) throws IndexOutOfBoundsException;

    /**
     * Gets the formula description for a specific sub-function
     *
     * @param subFunctionIndex the index of the sub-function
     * @return a string representation of the formula
     * @throws IndexOutOfBoundsException if the index is invalid
     */
    String getSubFunctionFormula(int subFunctionIndex) throws IndexOutOfBoundsException;
}
