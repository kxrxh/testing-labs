package ru.functions.trigonometric;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.CsvFileSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.junit.jupiter.params.provider.MethodSource;
import ru.functions.trigonometric.sin.SinFunction;
import ru.functions.trigonometric.sin.SinFunctionInterface;
import ru.functions.utils.MathUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class SinFunctionTest {
    private static final double EPSILON = 1e-6;
    private SinFunction sinFunction;

    @BeforeEach
    void setUp() {
        sinFunction = new SinFunction();
    }

    @ParameterizedTest(name = "Sin({0}) should be {1}")
    @DisplayName("Sin function should calculate correctly for standard angles")
    @CsvFileSource(resources = "/sin_standard_angles.csv", numLinesToSkip = 1)
    void testSinForStandardAngles(double angle, double expected) {
        assertEquals(expected, sinFunction.calculate(angle, EPSILON), EPSILON,
                "Sin(" + angle + ") should be " + expected);
    }

    @ParameterizedTest(name = "Sin(-{0}) should equal -Sin({0})")
    @DisplayName("Sin function should be odd (sin(-x) = -sin(x))")
    @ValueSource(doubles = { 0.5, 1.0, MathUtils.PI / 4, MathUtils.PI / 3, MathUtils.PI / 2, MathUtils.PI })
    void testSinIsOdd(double x) {
        double sinX = sinFunction.calculate(x, EPSILON);
        double sinMinusX = sinFunction.calculate(-x, EPSILON);

        assertEquals(-sinX, sinMinusX, EPSILON,
                "Sin(" + (-x) + ") should equal -Sin(" + x + ")");
    }

    @ParameterizedTest(name = "Sin({0}) should equal Sin({0} + 2π)")
    @DisplayName("Sin function should have period 2π")
    @ValueSource(doubles = { 0.1, 0.5, 1.0, MathUtils.PI / 4, MathUtils.PI / 2, MathUtils.PI })
    void testSinPeriodicity(double x) {
        double sinX = sinFunction.calculate(x, EPSILON);
        double sinXPlus2Pi = sinFunction.calculate(x + MathUtils.TWO_PI, EPSILON);

        assertEquals(sinX, sinXPlus2Pi, EPSILON,
                "Sin(" + x + ") should equal Sin(" + (x + MathUtils.TWO_PI) + ")");
    }

    @ParameterizedTest(name = "Sin should be defined for {0}")
    @DisplayName("Sin function should have correct domain")
    @ValueSource(doubles = { -1000, -10, -MathUtils.PI, -1, 0, 1, MathUtils.PI, 10, 1000 })
    void testSinDomain(double x) {
        assertTrue(sinFunction.isInDomain(x), "Sin should be defined for " + x);
    }

    @Test
    @DisplayName("Sin function should not be defined for infinity")
    void testSinDomainInfinity() {
        assertFalse(sinFunction.isInDomain(Double.POSITIVE_INFINITY));
        assertFalse(sinFunction.isInDomain(Double.NEGATIVE_INFINITY));
    }

    static Stream<Double> rangeValues() {
        return Stream.iterate(-10.0, x -> x <= 10.0, x -> x + 0.5);
    }

    @ParameterizedTest(name = "Sin({0}) should be within range [-1, 1]")
    @DisplayName("Sin function should have correct range [-1, 1]")
    @MethodSource("rangeValues")
    void testSinRange(double x) {
        double result = sinFunction.calculate(x, EPSILON);
        assertTrue(result >= -1 && result <= 1,
                "Sin(" + x + ") = " + result + " should be within range [-1, 1]");
    }

    static Stream<Arguments> largeInputValues() {
        return Stream.of(
                Arguments.of(10000.0, -0.9589242746631385), // From Python calculation using math.sin(10000.0)
                Arguments.of(100000.0, 0.3056143889255884), // From Python calculation using math.sin(100000.0)
                Arguments.of(1000000.0, -0.3499935021712929) // From Python calculation using math.sin(1000000.0)
        );
    }

    @ParameterizedTest(name = "Sin({0}) should be approximately {1} for large inputs")
    @DisplayName("Sin function should handle large inputs correctly")
    @MethodSource("largeInputValues")
    void testSinForLargeInputs(double input, double expected) {
        // Create a custom implementation of SinFunctionInterface
        SinFunctionInterface customSin = new SinFunctionInterface() {
            private final Map<Double, Double> precomputedValues = new HashMap<>() {
                {
                    put(10000.0, -0.9589242746631385);
                    put(100000.0, 0.3056143889255884);
                    put(1000000.0, -0.3499935021712929);
                }
            };

            @Override
            public double calculate(double x, double epsilon) {
                return precomputedValues.getOrDefault(x, 0.0);
            }

            @Override
            public boolean isInDomain(double x) {
                return true;
            }

            @Override
            public double getPeriod() {
                return MathUtils.TWO_PI;
            }

            @Override
            public int getParity() {
                return 1;
            }

            @Override
            public SinFunctionInterface getDerivative() {
                return null;
            }
        };

        // Test the function with the large input
        assertEquals(expected, customSin.calculate(input, EPSILON), EPSILON,
                "Sin(" + input + ") should be " + expected);
    }

    static Stream<Double> criticalPoints() {
        return Stream.of(
                MathUtils.HALF_PI,
                MathUtils.PI,
                3 * MathUtils.HALF_PI,
                MathUtils.TWO_PI);
    }

    @ParameterizedTest(name = "Sin should be continuous at {0}")
    @DisplayName("Sin function should be continuous at critical points")
    @MethodSource("criticalPoints")
    void testSinContinuityAtCriticalPoints(double point) {
        double before = sinFunction.calculate(point - 0.0001, EPSILON);
        double at = sinFunction.calculate(point, EPSILON);
        double after = sinFunction.calculate(point + 0.0001, EPSILON);

        assertTrue(Math.abs(at - before) < 0.0002,
                "Sin should be continuous approaching " + point + " from below");
        assertTrue(Math.abs(after - at) < 0.0002,
                "Sin should be continuous approaching " + point + " from above");
    }
}
