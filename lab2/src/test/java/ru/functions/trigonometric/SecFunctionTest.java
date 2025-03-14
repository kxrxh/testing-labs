package ru.functions.trigonometric;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ValueSource;
import org.junit.jupiter.params.provider.MethodSource;
import ru.functions.trigonometric.sec.SecFunction;
import ru.functions.trigonometric.sec.SecFunctionInterface;
import ru.functions.utils.MathUtils;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class SecFunctionTest {
    private static final double EPSILON = 1e-6;
    private SecFunction secFunction;

    @BeforeEach
    void setUp() {
        secFunction = new SecFunction();
    }

    static Stream<Arguments> standardAnglesValues() {
        return Stream.of(
                // angle, expected sec value
                Arguments.of(0.0, 1.0),
                Arguments.of(MathUtils.PI / 6, 2.0 / Math.sqrt(3)),
                Arguments.of(MathUtils.PI / 4, Math.sqrt(2)),
                Arguments.of(MathUtils.PI / 3, 2.0),
                Arguments.of(MathUtils.PI / 2, Double.POSITIVE_INFINITY), // Undefined at π/2
                Arguments.of(2 * MathUtils.PI / 3, -2.0),
                Arguments.of(3 * MathUtils.PI / 4, -Math.sqrt(2)),
                Arguments.of(5 * MathUtils.PI / 6, -2.0 / Math.sqrt(3)),
                Arguments.of(MathUtils.PI, -1.0),
                Arguments.of(7 * MathUtils.PI / 6, -2.0 / Math.sqrt(3)),
                Arguments.of(5 * MathUtils.PI / 4, -Math.sqrt(2)),
                Arguments.of(4 * MathUtils.PI / 3, -2.0),
                Arguments.of(3 * MathUtils.PI / 2, Double.POSITIVE_INFINITY), // Undefined at 3π/2
                Arguments.of(5 * MathUtils.PI / 3, 2.0),
                Arguments.of(7 * MathUtils.PI / 4, Math.sqrt(2)),
                Arguments.of(11 * MathUtils.PI / 6, 2.0 / Math.sqrt(3)));
    }

    @ParameterizedTest(name = "Sec({0}) should be {1}")
    @DisplayName("Sec function should calculate correctly for standard angles")
    @MethodSource("standardAnglesValues")
    void testSecForStandardAngles(double angle, double expected) {
        // Skip test for values where sec is undefined (cos(x) = 0)
        if (Double.isInfinite(expected)) {
            assertThrows(IllegalArgumentException.class, () -> secFunction.calculate(angle, EPSILON),
                    "Sec(" + angle + ") should throw IllegalArgumentException");
        } else {
            assertEquals(expected, secFunction.calculate(angle, EPSILON), EPSILON,
                    "Sec(" + angle + ") should be " + expected);
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

    static Stream<Double> nonZeroCosValues() {
        return Stream.of(
                0.0,
                MathUtils.PI / 6,
                MathUtils.PI / 4,
                MathUtils.PI / 3,
                2 * MathUtils.PI / 3,
                3 * MathUtils.PI / 4,
                5 * MathUtils.PI / 6,
                MathUtils.PI,
                7 * MathUtils.PI / 6,
                5 * MathUtils.PI / 4,
                4 * MathUtils.PI / 3,
                5 * MathUtils.PI / 3,
                7 * MathUtils.PI / 4,
                11 * MathUtils.PI / 6);
    }

    @ParameterizedTest(name = "Sec({0}) should equal 1/cos({0})")
    @DisplayName("Sec function should equal 1/cos(x)")
    @MethodSource("nonZeroCosValues")
    void testSecEqualsOneOverCos(double x) {
        double cosX = Math.cos(x);
        double secX = secFunction.calculate(x, EPSILON);

        assertEquals(1.0 / cosX, secX, EPSILON,
                "Sec(" + x + ") should equal 1/cos(" + x + ")");
    }

    @ParameterizedTest(name = "Sec({0}) should never be in (-1, 1)")
    @DisplayName("Sec function should have range (-∞, -1] ∪ [1, ∞)")
    @MethodSource("nonZeroCosValues")
    void testSecRange(double x) {
        double result = secFunction.calculate(x, EPSILON);
        assertTrue(result <= -1.0 || result >= 1.0,
                "Sec(" + x + ") = " + result + " should be outside (-1, 1)");
    }

    static Stream<Arguments> specialSecValues() {
        return Stream.of(
                Arguments.of(0.0, 1.0, "Sec(0) should be 1"),
                Arguments.of(MathUtils.PI, -1.0, "Sec(π) should be -1"),
                Arguments.of(2 * MathUtils.PI, 1.0, "Sec(2π) should be 1"));
    }

    @ParameterizedTest(name = "Sec({0}) should be {1}")
    @DisplayName("Sec function should calculate correctly for special angles")
    @MethodSource("specialSecValues")
    void testSecSpecialValues(double angle, double expected, String message) {
        assertEquals(expected, secFunction.calculate(angle, EPSILON), EPSILON, message);
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

    @Test
    @DisplayName("Derivative of sec(x) should be sec(x)tan(x)")
    void testSecDerivative() {
        SecFunctionInterface derivative = secFunction.getDerivative();

        // Test at several points
        double[] testPoints = { 0.0, MathUtils.PI / 4, MathUtils.PI / 3, 2 * MathUtils.PI / 3, MathUtils.PI };

        for (double x : testPoints) {
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
    }

    @Test
    @DisplayName("Sec function should throw exception for values close to odd multiples of π/2")
    void testSecThrowsExceptionNearSingularities() {
        double[] nearSingularities = {
                MathUtils.HALF_PI - 1e-10,
                MathUtils.HALF_PI + 1e-10,
                3 * MathUtils.HALF_PI - 1e-10,
                3 * MathUtils.HALF_PI + 1e-10
        };

        for (double x : nearSingularities) {
            assertThrows(IllegalArgumentException.class, () -> secFunction.calculate(x, EPSILON),
                    "Sec(" + x + ") should throw IllegalArgumentException");
        }
    }

    @ParameterizedTest(name = "Sec should approach infinity as x approaches {0}")
    @DisplayName("Sec function should approach infinity near singularities")
    @ValueSource(doubles = { 0.001, 0.01, 0.1 })
    void testSecApproachesInfinityNearSingularities(double delta) {
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
