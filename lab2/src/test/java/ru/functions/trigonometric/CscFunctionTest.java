package ru.functions.trigonometric;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.junit.jupiter.params.provider.CsvFileSource;
import ru.functions.trigonometric.csc.CscFunction;
import ru.functions.trigonometric.csc.CscFunctionInterface;
import ru.functions.utils.MathUtils;

import static org.junit.jupiter.api.Assertions.*;

class CscFunctionTest {
    private static final double EPSILON = 1e-6;
    private CscFunction cscFunction;

    @BeforeEach
    void setUp() {
        cscFunction = new CscFunction();
    }

    @ParameterizedTest(name = "Csc({0}) should be {1}")
    @DisplayName("Csc function should calculate correctly for standard angles")
    @CsvFileSource(resources = "/csc_standard_angles.csv", numLinesToSkip = 1)
    void testCscForStandardAngles(double angle, String expected, String description) {
        // Handle "Infinity" value from CSV
        if ("Infinity".equals(expected)) {
            assertThrows(IllegalArgumentException.class, () -> cscFunction.calculate(angle, EPSILON),
                    "Csc(" + angle + ") should throw IllegalArgumentException");
        } else {
            double expectedValue = Double.parseDouble(expected);
            assertEquals(expectedValue, cscFunction.calculate(angle, EPSILON), EPSILON,
                    "Csc(" + angle + ") should be " + expectedValue);
        }
    }

    @ParameterizedTest(name = "Csc(-{0}) should equal -Csc({0})")
    @DisplayName("Csc function should be odd (csc(-x) = -csc(x))")
    @ValueSource(doubles = { MathUtils.PI / 6, MathUtils.PI / 4, MathUtils.PI / 3, MathUtils.PI / 2,
            2 * MathUtils.PI / 3 })
    void testCscIsOdd(double x) {
        double cscX = cscFunction.calculate(x, EPSILON);
        double cscMinusX = cscFunction.calculate(-x, EPSILON);

        assertEquals(-cscX, cscMinusX, EPSILON,
                "Csc(" + (-x) + ") should equal -Csc(" + x + ")");
    }

    @ParameterizedTest(name = "Csc({0}) should equal Csc({0} + 2π)")
    @DisplayName("Csc function should have period 2π")
    @ValueSource(doubles = { MathUtils.PI / 6, MathUtils.PI / 4, MathUtils.PI / 3, MathUtils.PI / 2,
            2 * MathUtils.PI / 3 })
    void testCscPeriodicity(double x) {
        double cscX = cscFunction.calculate(x, EPSILON);
        double cscXPlus2Pi = cscFunction.calculate(x + MathUtils.TWO_PI, EPSILON);

        assertEquals(cscX, cscXPlus2Pi, EPSILON,
                "Csc(" + x + ") should equal Csc(" + (x + MathUtils.TWO_PI) + ")");
    }

    @ParameterizedTest(name = "Csc should not be defined at {0}")
    @DisplayName("Csc function should not be defined at multiples of π")
    @ValueSource(doubles = { 0.0, MathUtils.PI, 2 * MathUtils.PI, -MathUtils.PI, -2 * MathUtils.PI })
    void testCscDomainAtMultiplesOfPi(double x) {
        assertFalse(cscFunction.isInDomain(x),
                "Csc should not be defined at " + x);

        assertThrows(IllegalArgumentException.class, () -> cscFunction.calculate(x, EPSILON),
                "Csc(" + x + ") should throw IllegalArgumentException");
    }

    @ParameterizedTest(name = "Csc should be defined at {0}")
    @DisplayName("Csc function should be defined for values where sin(x) ≠ 0")
    @ValueSource(doubles = { MathUtils.PI / 6, MathUtils.PI / 4, MathUtils.PI / 3, MathUtils.PI / 2,
            3 * MathUtils.PI / 4, 5 * MathUtils.PI / 6, 7 * MathUtils.PI / 6 })
    void testCscDomain(double x) {
        assertTrue(cscFunction.isInDomain(x), "Csc should be defined for " + x);
    }

    @Test
    @DisplayName("Csc function should not be defined for infinity")
    void testCscDomainInfinity() {
        assertFalse(cscFunction.isInDomain(Double.POSITIVE_INFINITY));
        assertFalse(cscFunction.isInDomain(Double.NEGATIVE_INFINITY));
    }

    @ParameterizedTest(name = "Csc({0}) should equal 1/sin({0})")
    @DisplayName("Csc function should equal 1/sin(x)")
    @CsvFileSource(resources = "/csc_non_zero_sin_values.csv", numLinesToSkip = 1)
    void testCscEqualsOneOverSin(double x, String description) {
        double sinX = Math.sin(x);
        double cscX = cscFunction.calculate(x, EPSILON);

        assertEquals(1.0 / sinX, cscX, EPSILON,
                "Csc(" + x + ") should equal 1/sin(" + x + ")");
    }

    @ParameterizedTest(name = "Csc({0}) should never be in (-1, 1)")
    @DisplayName("Csc function should have range (-∞, -1] ∪ [1, ∞)")
    @CsvFileSource(resources = "/csc_non_zero_sin_values.csv", numLinesToSkip = 1)
    void testCscRange(double x, String description) {
        double result = cscFunction.calculate(x, EPSILON);
        assertTrue(result <= -1.0 || result >= 1.0,
                "Csc(" + x + ") = " + result + " should be outside (-1, 1)");
    }

    @ParameterizedTest(name = "Csc({0}) should be {1}")
    @DisplayName("Csc function should calculate correctly for special angles")
    @CsvFileSource(resources = "/csc_special_values.csv", numLinesToSkip = 1)
    void testCscSpecialValues(double angle, double expected, String description) {
        assertEquals(expected, cscFunction.calculate(angle, EPSILON), EPSILON, 
                "Csc(" + description + ") should be " + expected);
    }

    @Test
    @DisplayName("Csc function should have parity 1 (odd function)")
    void testCscParity() {
        assertEquals(1, cscFunction.getParity(),
                "Csc function should have parity 1 (odd function)");
    }

    @Test
    @DisplayName("Csc function should have period 2π")
    void testCscPeriod() {
        assertEquals(MathUtils.TWO_PI, cscFunction.getPeriod(),
                "Csc function should have period 2π");
    }

    @ParameterizedTest(name = "Derivative of csc({0}) should be -csc({0})cot({0})")
    @DisplayName("Derivative of csc(x) should be -csc(x)cot(x)")
    @CsvFileSource(resources = "/csc_derivative_test_points.csv", numLinesToSkip = 1)
    void testCscDerivative(double x, String description) {
        CscFunctionInterface derivative = cscFunction.getDerivative();

        double derivativeValue = derivative.calculate(x, EPSILON);

        // Calculate expected value: -csc(x)cot(x) = -cos(x)/sin²(x)
        double sinX = Math.sin(x);
        double cosX = Math.cos(x);
        double expectedValue = -cosX / (sinX * sinX);

        // Use a larger epsilon for derivative tests due to compound calculation errors
        double derivativeEpsilon = 1e-5;
        assertEquals(expectedValue, derivativeValue, derivativeEpsilon,
                "Derivative of csc(" + x + ") should be -csc(" + x + ")cot(" + x + ")");
    }

    @ParameterizedTest(name = "Csc function should throw exception for values close to singularities: {1}")
    @DisplayName("Csc function should throw exception for values close to multiples of π")
    @CsvFileSource(resources = "/csc_near_singularities.csv", numLinesToSkip = 1)
    void testCscThrowsExceptionNearSingularities(double x, String description) {
        assertThrows(IllegalArgumentException.class, () -> cscFunction.calculate(x, EPSILON),
                "Csc(" + x + ") should throw IllegalArgumentException");
    }

    @ParameterizedTest(name = "Csc should approach infinity as x approaches singularities with delta={0}")
    @DisplayName("Csc function should approach infinity near singularities")
    @CsvFileSource(resources = "/csc_infinity_approach_deltas.csv", numLinesToSkip = 1)
    void testCscApproachesInfinityNearSingularities(double delta, String description) {
        // Test approaching 0 from positive side
        double valueNearZero = cscFunction.calculate(delta, EPSILON);
        assertTrue(valueNearZero > 10.0,
                "Csc(" + delta + ") should be very large");

        // Test approaching π from below
        double valueNearPiBelow = cscFunction.calculate(MathUtils.PI - delta, EPSILON);
        assertTrue(Math.abs(valueNearPiBelow) > 10.0,
                "Csc(" + (MathUtils.PI - delta) + ") should be very large");

        // Test approaching π from above
        double valueNearPiAbove = cscFunction.calculate(MathUtils.PI + delta, EPSILON);
        assertTrue(Math.abs(valueNearPiAbove) > 10.0,
                "Csc(" + (MathUtils.PI + delta) + ") should be very large");
    }
}
