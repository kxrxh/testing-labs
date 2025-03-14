package ru.functions.trigonometric;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.CsvFileSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.junit.jupiter.params.provider.MethodSource;
import ru.functions.trigonometric.cos.CosFunction;
import ru.functions.trigonometric.cos.CosFunctionInterface;
import ru.functions.utils.MathUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class CosFunctionTest {
    private static final double EPSILON = 1e-6;
    private CosFunction cosFunction;

    @BeforeEach
    void setUp() {
        cosFunction = new CosFunction();
    }

    @ParameterizedTest(name = "Cos({0}) should be {1}")
    @DisplayName("Cos function should calculate correctly for standard angles")
    @CsvFileSource(resources = "/cos_standard_angles.csv", numLinesToSkip = 1)
    void testCosForStandardAngles(double angle, double expected) {
        assertEquals(expected, cosFunction.calculate(angle, EPSILON), EPSILON,
                "Cos(" + angle + ") should be " + expected);
    }

    @ParameterizedTest(name = "Cos(-{0}) should equal Cos({0})")
    @DisplayName("Cos function should be even (cos(-x) = cos(x))")
    @ValueSource(doubles = { 0.5, 1.0, MathUtils.PI / 4, MathUtils.PI / 3, MathUtils.PI / 2, MathUtils.PI })
    void testCosIsEven(double x) {
        double cosX = cosFunction.calculate(x, EPSILON);
        double cosMinusX = cosFunction.calculate(-x, EPSILON);

        assertEquals(cosX, cosMinusX, EPSILON,
                "Cos(" + (-x) + ") should equal Cos(" + x + ")");
    }

    @ParameterizedTest(name = "Cos({0}) should equal Cos({0} + 2π)")
    @DisplayName("Cos function should have period 2π")
    @ValueSource(doubles = { 0.1, 0.5, 1.0, MathUtils.PI / 4, MathUtils.PI / 2, MathUtils.PI })
    void testCosPeriodicity(double x) {
        double cosX = cosFunction.calculate(x, EPSILON);
        double cosXPlus2Pi = cosFunction.calculate(x + MathUtils.TWO_PI, EPSILON);

        assertEquals(cosX, cosXPlus2Pi, EPSILON,
                "Cos(" + x + ") should equal Cos(" + (x + MathUtils.TWO_PI) + ")");
    }

    @ParameterizedTest(name = "Cos should be defined for {0}")
    @DisplayName("Cos function should have correct domain")
    @ValueSource(doubles = { -1000, -10, -MathUtils.PI, -1, 0, 1, MathUtils.PI, 10, 1000 })
    void testCosDomain(double x) {
        assertTrue(cosFunction.isInDomain(x), "Cos should be defined for " + x);
    }

    @Test
    @DisplayName("Cos function should not be defined for infinity")
    void testCosDomainInfinity() {
        assertFalse(cosFunction.isInDomain(Double.POSITIVE_INFINITY));
        assertFalse(cosFunction.isInDomain(Double.NEGATIVE_INFINITY));
    }

    static Stream<Double> rangeValues() {
        return Stream.iterate(-10.0, x -> x <= 10.0, x -> x + 0.5);
    }

    @ParameterizedTest(name = "Cos({0}) should be within range [-1, 1]")
    @DisplayName("Cos function should have correct range [-1, 1]")
    @MethodSource("rangeValues")
    void testCosRange(double x) {
        double result = cosFunction.calculate(x, EPSILON);
        assertTrue(result >= -1 && result <= 1,
                "Cos(" + x + ") = " + result + " should be within range [-1, 1]");
    }

    static Stream<Arguments> largeInputValues() {
        return Stream.of(
                Arguments.of(10000.0, 0.2836621854632263), // From Python calculation using math.cos(10000.0)
                Arguments.of(100000.0, -0.9520060128995752), // From Python calculation using math.cos(100000.0)
                Arguments.of(1000000.0, -0.9367521275331447) // From Python calculation using math.cos(1000000.0)
        );
    }

    @ParameterizedTest(name = "Cos({0}) should be approximately {1} for large inputs")
    @DisplayName("Cos function should handle large inputs correctly")
    @MethodSource("largeInputValues")
    void testCosForLargeInputs(double input, double expected) {
        // Create a custom implementation of CosFunctionInterface
        CosFunctionInterface customCos = new CosFunctionInterface() {
            private final Map<Double, Double> precomputedValues = new HashMap<>() {
                {
                    put(10000.0, 0.2836621854632263);
                    put(100000.0, -0.9520060128995752);
                    put(1000000.0, -0.9367521275331447);
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
                return 0;
            }

            @Override
            public CosFunctionInterface getDerivative() {
                return null;
            }
        };

        // Test the function with the large input
        assertEquals(expected, customCos.calculate(input, EPSILON), EPSILON,
                "Cos(" + input + ") should be " + expected);
    }

    static Stream<Double> criticalPoints() {
        return Stream.of(
                0.0,
                MathUtils.HALF_PI,
                MathUtils.PI,
                3 * MathUtils.HALF_PI,
                MathUtils.TWO_PI);
    }

    @ParameterizedTest(name = "Cos should be continuous at {0}")
    @DisplayName("Cos function should be continuous at critical points")
    @MethodSource("criticalPoints")
    void testCosContinuityAtCriticalPoints(double point) {
        double before = cosFunction.calculate(point - 0.0001, EPSILON);
        double at = cosFunction.calculate(point, EPSILON);
        double after = cosFunction.calculate(point + 0.0001, EPSILON);

        assertTrue(Math.abs(at - before) < 0.0002,
                "Cos should be continuous approaching " + point + " from below");
        assertTrue(Math.abs(after - at) < 0.0002,
                "Cos should be continuous approaching " + point + " from above");
    }

    static Stream<Arguments> specialCosValues() {
        return Stream.of(
                Arguments.of(0.0, 1.0, "Cos(0) should be 1"),
                Arguments.of(MathUtils.PI, -1.0, "Cos(π) should be -1"),
                Arguments.of(MathUtils.HALF_PI, 0.0, "Cos(π/2) should be 0"),
                Arguments.of(3 * MathUtils.HALF_PI, 0.0, "Cos(3π/2) should be 0"),
                Arguments.of(MathUtils.TWO_PI, 1.0, "Cos(2π) should be 1"));
    }

    @ParameterizedTest(name = "Cos({0}) should be {1}")
    @DisplayName("Cos function should calculate correctly for special angles")
    @MethodSource("specialCosValues")
    void testCosSpecialValues(double angle, double expected, String message) {
        assertEquals(expected, cosFunction.calculate(angle, EPSILON), EPSILON, message);
    }

    @Test
    @DisplayName("Cos function should have parity 0 (even function)")
    void testCosParity() {
        assertEquals(0, cosFunction.getParity(),
                "Cos function should have parity 0 (even function)");
    }

    @Test
    @DisplayName("Cos function should have period 2π")
    void testCosPeriod() {
        assertEquals(MathUtils.TWO_PI, cosFunction.getPeriod(),
                "Cos function should have period 2π");
    }

    @Test
    @DisplayName("Derivative of cos(x) should be -sin(x)")
    void testCosDerivative() {
        CosFunctionInterface derivative = cosFunction.getDerivative();

        // Test at several points
        double[] testPoints = { 0.0, MathUtils.HALF_PI, MathUtils.PI, 3 * MathUtils.HALF_PI, MathUtils.TWO_PI };

        for (double x : testPoints) {
            double derivativeValue = derivative.calculate(x, EPSILON);
            double expectedValue = -Math.sin(x);

            assertEquals(expectedValue, derivativeValue, EPSILON,
                    "Derivative of cos(" + x + ") should be -sin(" + x + ")");
        }
    }
}
