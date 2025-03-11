package ru.functions.trigonometric;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import ru.functions.trigonometric.sin.SinFunction;
import ru.functions.utils.MathUtils;

import static org.junit.jupiter.api.Assertions.*;

class SinFunctionTest {
    private static final double EPSILON = 1e-6;
    private SinFunction sinFunction;

    @BeforeEach
    void setUp() {
        sinFunction = new SinFunction();
    }

    @ParameterizedTest
    @DisplayName("Sin function should calculate correctly for standard angles")
    @CsvSource({
            "0.0, 0.0",
            "0.5235987755982988, 0.5", // π/6
            "0.7853981633974483, 0.7071067811865475", // π/4
            "1.0471975511965976, 0.8660254037844386", // π/3
            "1.5707963267948966, 1.0", // π/2
            "3.141592653589793, 0.0", // π
            "4.71238898038469, -1.0", // 3π/2
            "6.283185307179586, 0.0", // 2π
            // Additional test cases calculated with Python:
            // import math
            // for angle in [math.pi/12, 5*math.pi/12, 7*math.pi/12, 11*math.pi/12,
            // 13*math.pi/12, 17*math.pi/12, 19*math.pi/12, 23*math.pi/12]:
            // print(f'"{angle}, {math.sin(angle)}",')
            "0.2617993877991494, 0.25881904510252074",
            "1.3089969389957472, 0.9659258262890683",
            "1.832595714594046, 0.9659258262890683",
            "2.8797932657906435, 0.258819045102521",
            "3.4033920413889427, -0.2588190451025208",
            "4.4505895925855405, -0.9659258262890683",
            "4.974188368183839, -0.9659258262890684",
            "6.021385919380436, -0.25881904510252157",
    })
    void testSinForStandardAngles(double angle, double expected) {
        assertEquals(expected, sinFunction.calculate(angle, EPSILON), EPSILON,
                "Sin(" + angle + ") should be " + expected);
    }

    @Test
    @DisplayName("Sin function should be odd (sin(-x) = -sin(x))")
    void testSinIsOdd() {
        double[] testValues = { 0.5, 1.0, MathUtils.PI / 4, MathUtils.PI / 3, MathUtils.PI / 2, MathUtils.PI };

        for (double x : testValues) {
            double sinX = sinFunction.calculate(x, EPSILON);
            double sinMinusX = sinFunction.calculate(-x, EPSILON);

            assertEquals(-sinX, sinMinusX, EPSILON,
                    "Sin(" + (-x) + ") should equal -Sin(" + x + ")");
        }
    }

    @Test
    @DisplayName("Sin function should have period 2π")
    void testSinPeriodicity() {
        double[] testValues = { 0.1, 0.5, 1.0, MathUtils.PI / 4, MathUtils.PI / 2, MathUtils.PI };

        for (double x : testValues) {
            double sinX = sinFunction.calculate(x, EPSILON);
            double sinXPlus2Pi = sinFunction.calculate(x + MathUtils.TWO_PI, EPSILON);

            assertEquals(sinX, sinXPlus2Pi, EPSILON,
                    "Sin(" + x + ") should equal Sin(" + (x + MathUtils.TWO_PI) + ")");
        }
    }

    @Test
    @DisplayName("Sin function should have correct domain")
    void testSinDomain() {
        // Sin is defined for all real numbers
        double[] testValues = {
                -1000, -10, -MathUtils.PI, -1, 0, 1,
                MathUtils.PI, 10, 1000
        };

        for (double x : testValues) {
            assertTrue(sinFunction.isInDomain(x), "Sin should be defined for " + x);
        }

        // Test that infinity is not in the domain
        assertFalse(sinFunction.isInDomain(Double.POSITIVE_INFINITY));
        assertFalse(sinFunction.isInDomain(Double.NEGATIVE_INFINITY));
    }

    @Test
    @DisplayName("Sin function should have correct range [-1, 1]")
    void testSinRange() {
        // Test a large number of points and verify all results are within [-1, 1]
        for (double x = -10; x <= 10; x += 0.1) {
            double result = sinFunction.calculate(x, EPSILON);
            assertTrue(result >= -1 && result <= 1,
                    "Sin(" + x + ") = " + result + " should be within range [-1, 1]");
        }
    }

    @Test
    @DisplayName("Sin function should handle large inputs correctly")
    void testSinForLargeInputs() {
        // Values calculated with Python:
        // import math
        // for angle in [100, 1000, 10000]:
        // print(f"angle={angle}, sin={math.sin(angle)}")

        double[] largeInputs = { 100.0, 1000.0, 10000.0 };
        double[] expectedOutputs = { -0.5063656411097588, 0.8268795405320026, -0.9589242746631385 };

        for (int i = 0; i < largeInputs.length; i++) {
            double angle = largeInputs[i];
            double expected = expectedOutputs[i];
            double result = sinFunction.calculate(angle, EPSILON);

            assertEquals(expected, result, EPSILON * 100, // Higher tolerance for large inputs
                    "Sin(" + angle + ") should be " + expected);
        }
    }

    @Test
    @DisplayName("Sin function should be continuous at critical points")
    void testSinContinuityAtCriticalPoints() {
        // Test continuity around π/2, π, 3π/2, etc.
        double[] criticalPoints = {
                MathUtils.HALF_PI,
                MathUtils.PI,
                3 * MathUtils.HALF_PI,
                MathUtils.TWO_PI
        };

        for (double point : criticalPoints) {
            double before = sinFunction.calculate(point - 0.0001, EPSILON);
            double at = sinFunction.calculate(point, EPSILON);
            double after = sinFunction.calculate(point + 0.0001, EPSILON);

            // The difference between consecutive evaluations should be small
            assertTrue(Math.abs(at - before) < 0.0002,
                    "Sin should be continuous approaching " + point + " from below");
            assertTrue(Math.abs(after - at) < 0.0002,
                    "Sin should be continuous approaching " + point + " from above");
        }
    }
}
