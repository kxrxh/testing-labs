package ru.functions.system;

import ru.functions.utils.Function;

/**
 * Interface for a system function composed of multiple sub-functions
 */
public interface SystemFunctionInterface extends Function {

    /**
     * Gets the number of sub-functions in the system
     *
     * @return the number of sub-functions
     */
    int getSubFunctionCount();

    /**
     * Gets the index of the sub-function applicable for a given input value
     *
     * @param x the input value
     * @return the index of the applicable sub-function, or -1 if no sub-function is
     *         applicable
     */
    int getApplicableSubFunction(double x);

    /**
     * Gets the domain description for a sub-function
     *
     * @param subFunctionIndex the index of the sub-function
     * @return a string describing the domain of the sub-function
     * @throws IndexOutOfBoundsException if the sub-function index is out of range
     */
    String getSubFunctionDomain(int subFunctionIndex) throws IndexOutOfBoundsException;

    /**
     * Gets the formula description for a sub-function
     *
     * @param subFunctionIndex the index of the sub-function
     * @return a string representing the formula of the sub-function
     * @throws IndexOutOfBoundsException if the sub-function index is out of range
     */
    String getSubFunctionFormula(int subFunctionIndex) throws IndexOutOfBoundsException;

    /**
     * Gets the negative domain function component
     *
     * @return the negative domain function
     */
    Function getNegativeDomainFunction();

    /**
     * Gets the positive domain function component
     *
     * @return the positive domain function
     */
    Function getPositiveDomainFunction();

    /**
     * Checks if the implementation uses stub functions
     *
     * @return true if the implementation uses stub functions, false otherwise
     */
    boolean isUsingStubs();
}
