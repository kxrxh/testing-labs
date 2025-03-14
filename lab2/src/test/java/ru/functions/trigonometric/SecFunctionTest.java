package ru.functions.trigonometric;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.junit.jupiter.params.provider.CsvFileSource;
import ru.functions.trigonometric.sec.SecFunction;
import ru.functions.trigonometric.sec.SecFunctionInterface;
import ru.functions.utils.MathUtils;

import static org.junit.jupiter.api.Assertions.*;

class SecFunctionTest {
    private static final double EPSILON = 1e-6;
    private SecFunction secFunction;

    @BeforeEach
    void setUp() {
        secFunction = new SecFunction();
    }

    @ParameterizedTest(name = "Sec({0}) should be {1}")
    @DisplayName("Sec function should calculate correctly for standard angles")
    @CsvFileSource(resources = "/sec_standard_angles.csv", numLinesToSkip = 1)
    void testSecForStandardAngles(double angle, String expected, String description) {
        // Handle "Infinity" value from CSV
        if ("Infinity".equals(expected)) {
            assertThrows(IllegalArgumentException.class, () -> secFunction.calculate(angle, EPSILON),
                    "Sec(" + angle + ") should throw IllegalArgumentException");
        } else {
            double expectedValue = Double.parseDouble(expected);
            assertEquals(expectedValue, secFunction.calculate(angle, EPSILON), EPSILON,
                    "Sec(" + angle + ") should be " + expectedValue);
        }
    }

    @ParameterizedTest(name = "Sec(-{0}) should equal Sec({0})")
    @DisplayName("Sec function should be even (sec(-x) = sec(x))")
    @ValueSource(doubles = { 0.0, MathUtils.PI / 6, MathUtils.PI / 4, MathUtils.PI / 3, MathUtils.PI })
    void testSecIsEven(double x) {
        double secX = secFunction.calculate(x, EPSILON);
        double secMinusX = secFunction.calculate(-x, EPSILON);

        // Use a larger epsilon for even function test due to compound calculation
        // errors
        double evenTestEpsilon = 1e-5;
        assertEquals(secX, secMinusX, evenTestEpsilon,
                "Sec(" + (-x) + ") should equal Sec(" + x + ")");
    }

    @ParameterizedTest(name = "Sec({0}) should equal Sec({0} + 2π)")
    @DisplayName("Sec function should have period 2π")
    @ValueSource(doubles = { 0.0, MathUtils.PI / 6, MathUtils.PI / 4, MathUtils.PI / 3, MathUtils.PI })
    void testSecPeriodicity(double x) {
        double secX = secFunction.calculate(x, EPSILON);
        double secXPlus2Pi = secFunction.calculate(x + MathUtils.TWO_PI, EPSILON);

        assertEquals(secX, secXPlus2Pi, EPSILON,
                "Sec(" + x + ") should equal Sec(" + (x + MathUtils.TWO_PI) + ")");
    }

    @ParameterizedTest(name = "Sec should not be defined at {0}")
    @DisplayName("Sec function should not be defined at odd multiples of π/2")
    @ValueSource(doubles = {
            MathUtils.PI / 2,
            3 * MathUtils.PI / 2,
            5 * MathUtils.PI / 2,
            -MathUtils.PI / 2,
            -3 * MathUtils.PI / 2
    })
    void testSecDomainAtOddMultiplesOfHalfPi(double x) {
        assertFalse(secFunction.isInDomain(x),
                "Sec should not be defined at " + x);

        assertThrows(IllegalArgumentException.class, () -> secFunction.calculate(x, EPSILON),
                "Sec(" + x + ") should throw IllegalArgumentException");
    }

    @ParameterizedTest(name = "Sec should be defined at {0}")
    @DisplayName("Sec function should be defined for values where cos(x) ≠ 0")
    @ValueSource(doubles = {
            0.0,
            MathUtils.PI / 6,
            MathUtils.PI / 4,
            MathUtils.PI / 3,
            2 * MathUtils.PI / 3,
            MathUtils.PI
    })
    void testSecDomain(double x) {
        assertTrue(secFunction.isInDomain(x), "Sec should be defined for " + x);
    }

    @Test
    @DisplayName("Sec function should not be defined for infinity")
    void testSecDomainInfinity() {
        assertFalse(secFunction.isInDomain(Double.POSITIVE_INFINITY));
        assertFalse(secFunction.isInDomain(Double.NEGATIVE_INFINITY));
    }

    @ParameterizedTest(name = "Sec({0}) should equal 1/cos({0})")
    @DisplayName("Sec function should equal 1/cos(x)")
    @CsvFileSource(resources = "/sec_non_zero_cos_values.csv", numLinesToSkip = 1)
    void testSecEqualsOneOverCos(double x, String description) {
        double cosX = Math.cos(x);
        double secX = secFunction.calculate(x, EPSILON);

        assertEquals(1.0 / cosX, secX, EPSILON,
                "Sec(" + x + ") should equal 1/cos(" + x + ")");
    }

    @ParameterizedTest(name = "Sec({0}) should never be in (-1, 1)")
    @DisplayName("Sec function should have range (-∞, -1] ∪ [1, ∞)")
    @CsvFileSource(resources = "/sec_non_zero_cos_values.csv", numLinesToSkip = 1)
    void testSecRange(double x, String description) {
        double result = secFunction.calculate(x, EPSILON);
        assertTrue(result <= -1.0 || result >= 1.0,
                "Sec(" + x + ") = " + result + " should be outside (-1, 1)");
    }

    @ParameterizedTest(name = "Sec({0}) should be {1}")
    @DisplayName("Sec function should calculate correctly for special angles")
    @CsvFileSource(resources = "/sec_special_values.csv", numLinesToSkip = 1)
    void testSecSpecialValues(double angle, double expected, String description) {
        assertEquals(expected, secFunction.calculate(angle, EPSILON), EPSILON,
                "Sec(" + description + ") should be " + expected);
    }

    @Test
    @DisplayName("Sec function should have parity 0 (even function)")
    void testSecParity() {
        assertEquals(0, secFunction.getParity(),
                "Sec function should have parity 0 (even function)");
    }

    @Test
    @DisplayName("Sec function should have period 2π")
    void testSecPeriod() {
        assertEquals(MathUtils.TWO_PI, secFunction.getPeriod(),
                "Sec function should have period 2π");
    }

    @ParameterizedTest(name = "Derivative of sec({0}) should be sec({0})tan({0})")
    @DisplayName("Derivative of sec(x) should be sec(x)tan(x)")
    @CsvFileSource(resources = "/sec_derivative_test_points.csv", numLinesToSkip = 1)
    void testSecDerivative(double x, String description) {
        SecFunctionInterface derivative = secFunction.getDerivative();

        double derivativeValue = derivative.calculate(x, EPSILON);

        // Calculate expected value: sec(x)tan(x) = sin(x)/cos²(x)
        double sinX = Math.sin(x);
        double cosX = Math.cos(x);
        double expectedValue = sinX / (cosX * cosX);

        // Use a larger epsilon for derivative tests due to compound calculation errors
        double derivativeEpsilon = 1e-5;
        assertEquals(expectedValue, derivativeValue, derivativeEpsilon,
                "Derivative of sec(" + x + ") should be sec(" + x + ")tan(" + x + ")");
    }

    @ParameterizedTest(name = "Sec function should throw exception for values close to singularities: {1}")
    @DisplayName("Sec function should throw exception for values close to odd multiples of π/2")
    @CsvFileSource(resources = "/sec_near_singularities.csv", numLinesToSkip = 1)
    void testSecThrowsExceptionNearSingularities(double x, String description) {
        assertThrows(IllegalArgumentException.class, () -> secFunction.calculate(x, EPSILON),
                "Sec(" + x + ") should throw IllegalArgumentException");
    }

    @ParameterizedTest(name = "Sec should approach infinity as x approaches singularities with delta={0}")
    @DisplayName("Sec function should approach infinity near singularities")
    @CsvFileSource(resources = "/sec_infinity_approach_deltas.csv", numLinesToSkip = 1)
    void testSecApproachesInfinityNearSingularities(double delta, String description) {
        // Test approaching π/2 from below
        double valueNearHalfPiBelow = secFunction.calculate(MathUtils.HALF_PI - delta, EPSILON);
        assertTrue(Math.abs(valueNearHalfPiBelow) > 10.0,
                "Sec(" + (MathUtils.HALF_PI - delta) + ") should be very large");

        // Test approaching π/2 from above
        double valueNearHalfPiAbove = secFunction.calculate(MathUtils.HALF_PI + delta, EPSILON);
        assertTrue(Math.abs(valueNearHalfPiAbove) > 10.0,
                "Sec(" + (MathUtils.HALF_PI + delta) + ") should be very large");

        // Test approaching 3π/2 from below
        double valueNear3HalfPiBelow = secFunction.calculate(3 * MathUtils.HALF_PI - delta, EPSILON);
        assertTrue(Math.abs(valueNear3HalfPiBelow) > 10.0,
                "Sec(" + (3 * MathUtils.HALF_PI - delta) + ") should be very large");

        // Test approaching 3π/2 from above
        double valueNear3HalfPiAbove = secFunction.calculate(3 * MathUtils.HALF_PI + delta, EPSILON);
        assertTrue(Math.abs(valueNear3HalfPiAbove) > 10.0,
                "Sec(" + (3 * MathUtils.HALF_PI + delta) + ") should be very large");
    }
}
