package ru.functions.logarithmic.log5;

import ru.functions.utils.Function;

/**
 * Interface for logarithm base 5 function and related functions in the log5
 * package
 */
public interface Log5FunctionInterface extends Function {
    /**
     * Gets the base of the logarithm
     *
     * @return the base of the logarithm
     */
    double getBase();

    /**
     * Changes the base of the logarithm
     *
     * @param newBase the new base to use
     * @return a logarithm function with the new base
     * @throws IllegalArgumentException if the new base is not positive or is equal
     *                                  to 1
     */
    Log5FunctionInterface changeBase(double newBase) throws IllegalArgumentException;
}
