package ru.functions.utils;

/**
 * Utility class containing math constants and helper methods
 */
public class MathUtils {
    // Constants
    public static final double PI = Math.PI;
    public static final double HALF_PI = PI / 2;
    public static final double TWO_PI = 2 * PI;

    /**
     * Normalizes angle to [-PI, PI] range
     *
     * @param angle The angle to normalize (in radians)
     * @return The normalized angle in [-PI, PI] range
     */
    public static double normalizeAngle(double angle) {
        angle = angle % TWO_PI;
        if (angle > PI) {
            angle -= TWO_PI;
        } else if (angle < -PI) {
            angle += TWO_PI;
        }
        return angle;
    }

    /**
     * Checks if two double values are equal within the given epsilon
     *
     * @param a       First value
     * @param b       Second value
     * @param epsilon The maximum allowed difference
     * @return true if the values are equal within epsilon, false otherwise
     */
    public static boolean areEqual(double a, double b, double epsilon) {
        return Math.abs(a - b) < epsilon;
    }

    /**
     * Checks if a value is approximately zero
     *
     * @param value   The value to check
     * @param epsilon The maximum allowed difference from zero
     * @return true if the value is approximately zero, false otherwise
     */
    public static boolean isZero(double value, double epsilon) {
        return areEqual(value, 0.0, epsilon);
    }

    /**
     * Checks if a value is close to a multiple of PI/2
     *
     * @param value   The value to check
     * @param epsilon The maximum allowed difference
     * @return true if the value is close to a multiple of PI/2, false otherwise
     */
    public static boolean isCloseToMultipleOfHalfPi(double value, double epsilon) {
        double normalized = Math.abs(value) % PI;
        return areEqual(normalized, 0.0, epsilon) || areEqual(normalized, HALF_PI, epsilon);
    }

    /**
     * Checks if the given angle is close to a multiple of π
     *
     * @param angle   the angle to check, in radians
     * @param epsilon the tolerance
     * @return true if the angle is close to nπ for some integer n
     */
    public static boolean isCloseToMultipleOfPi(double angle, double epsilon) {
        // Normalize to [0, 2π)
        double normalizedAngle = normalizeAngle(angle);

        // Check if close to 0 or π
        return isClose(normalizedAngle, 0.0, epsilon) ||
                isClose(normalizedAngle, PI, epsilon);
    }

    /**
     * Checks if two values are close to each other within the given epsilon
     *
     * @param a       first value
     * @param b       second value
     * @param epsilon the tolerance
     * @return true if the absolute difference between a and b is less than epsilon
     */
    public static boolean isClose(double a, double b, double epsilon) {
        return Math.abs(a - b) < epsilon;
    }
}
