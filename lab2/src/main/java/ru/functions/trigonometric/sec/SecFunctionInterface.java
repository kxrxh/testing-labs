package ru.functions.trigonometric.sec;

import ru.functions.utils.Function;

/**
 * Interface for secant function and related functions in the sec package
 */
public interface SecFunctionInterface extends Function {
    /**
     * Gets the period of the trigonometric function
     *
     * @return the period of the function in radians
     */
    double getPeriod();

    /**
     * Determines if the function is odd, even, or neither
     *
     * @return 1 if the function is odd, 0 if even, -1 if neither
     */
    int getParity();

    /**
     * Converts the angle from degrees to radians and calculates the result
     *
     * @param degrees The angle in degrees
     * @param epsilon The desired accuracy of the calculation
     * @return The calculated function value
     * @throws IllegalArgumentException if the input is outside the domain of the
     *                                  function
     */
    default double calculateInDegrees(double degrees, double epsilon) throws IllegalArgumentException {
        double radians = Math.toRadians(degrees);
        return calculate(radians, epsilon);
    }

    /**
     * Returns the derivative function of this trigonometric function
     *
     * @return the derivative function
     */
    SecFunctionInterface getDerivative();
}
