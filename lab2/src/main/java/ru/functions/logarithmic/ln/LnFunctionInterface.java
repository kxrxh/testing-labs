package ru.functions.logarithmic.ln;

import ru.functions.utils.Function;

public interface LnFunctionInterface extends Function {
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
    LnFunctionInterface changeBase(double newBase) throws IllegalArgumentException;
}
