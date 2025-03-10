package ru.functions.trigonometric;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
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
            "6.283185307179586, 0.0" // 2π
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
                Double.NEGATIVE_INFINITY, -1000, -10, -MathUtils.PI, -1, 0, 1,
                MathUtils.PI, 10, 1000, Double.POSITIVE_INFINITY
        };

        for (double x : testValues) {
            if (Double.isInfinite(x)) {
                assertThrows(IllegalArgumentException.class, () -> sinFunction.calculate(x, EPSILON),
                        "Sin should throw exception for " + x);
            } else {
                assertTrue(sinFunction.isInDomain(x), "Sin should be defined for " + x);
            }
        }
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
}
