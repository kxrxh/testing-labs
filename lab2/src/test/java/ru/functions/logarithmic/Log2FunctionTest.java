package ru.functions.logarithmic;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;
import ru.functions.logarithmic.log2.Log2Function;

import static org.junit.jupiter.api.Assertions.*;

class Log2FunctionTest {
    private static final double EPSILON = 1e-6;
    private static final double HIGH_TOLERANCE = 1e-3;
    private Log2Function log2Function;

    @BeforeEach
    void setUp() {
        log2Function = new Log2Function();
    }

    @ParameterizedTest(name = "Log2({0}) = {1}")
    @DisplayName("Log2 function should calculate correctly for standard values")
    @CsvFileSource(resources = "/log2_standard_values.csv", numLinesToSkip = 1)
    void testLog2ForStandardValues(double value, double expected, String description) {
        assertEquals(expected, log2Function.calculate(value, EPSILON), value < 1.0 ? EPSILON : HIGH_TOLERANCE,
                "Log2(" + value + ") should be " + expected);
    }

    @ParameterizedTest(name = "Log2({0}) should be close to {0}-1 / ln(2) for {1}")
    @DisplayName("Log2 function should handle values close to 1 correctly")
    @CsvFileSource(resources = "/log2_values_close_to_one.csv", numLinesToSkip = 1)
    void testLog2ForValuesCloseToOne(double x, String description) {
        double result = log2Function.calculate(x, EPSILON);
        // For x close to 1, log2(x) ≈ (x - 1) / ln(2)
        double approximation = (x - 1) / 0.693147180559945;

        if (Math.abs(x - 1.0) < 0.01) {
            assertEquals(approximation, result, 0.01,
                    "Log2(" + x + ") should be close to " + approximation);
        } else {
            assertTrue(Double.isFinite(result), "Log2(" + x + ") should be finite");
            assertEquals(Math.signum(x - 1), Math.signum(result), "Log2(" + x + ") should have correct sign");
        }
    }

    @ParameterizedTest(name = "Log2({0}) should throw exception for {1}")
    @DisplayName("Log2 function should throw exception for non-positive values")
    @CsvFileSource(resources = "/log2_non_positive_values.csv", numLinesToSkip = 1)
    void testLog2ForNonPositiveValues(String valueStr, String description) {
        double value;
        if (valueStr.equals("-Infinity")) {
            value = Double.NEGATIVE_INFINITY;
        } else {
            value = Double.parseDouble(valueStr);
        }

        Exception exception = assertThrows(IllegalArgumentException.class,
                () -> log2Function.calculate(value, EPSILON),
                "Log2 should throw exception for " + value);

        assertTrue(exception.getMessage().contains("outside the domain"),
                "Exception message should mention domain issue");
    }

    @ParameterizedTest(name = "Log2({0}*{1}) = Log2({0}) + Log2({1})")
    @DisplayName("Log2 function should satisfy the logarithm identity log2(a*b) = log2(a) + log2(b)")
    @CsvFileSource(resources = "/log2_identity_values.csv", numLinesToSkip = 1)
    void testLog2Identity(double a, double b, String description) {
        double log2A = log2Function.calculate(a, EPSILON);
        double log2B = log2Function.calculate(b, EPSILON);
        double log2AB = log2Function.calculate(a * b, EPSILON);

        assertEquals(log2A + log2B, log2AB, HIGH_TOLERANCE,
                "Log2(" + a + " * " + b + ") should equal Log2(" + a + ") + Log2(" + b + ")");
    }

    @ParameterizedTest(name = "Log2 should be {1} defined for {0} ({2})")
    @DisplayName("Log2 function should have correct domain")
    @CsvFileSource(resources = "/log2_domain_values.csv", numLinesToSkip = 1)
    void testLog2Domain(double value, boolean valid, String description) {
        if (valid) {
            assertTrue(log2Function.isInDomain(value), "Log2 should be defined for " + value);
        } else {
            assertFalse(log2Function.isInDomain(value), "Log2 should not be defined for " + value);
        }
    }

    @ParameterizedTest(name = "Log2({0}^{1}) = {1}*Log2({0}) for {2}")
    @DisplayName("Log2 function should satisfy the power property log2(x^n) = n*log2(x)")
    @CsvFileSource(resources = "/log2_power_property.csv", numLinesToSkip = 1)
    void testLog2PowerProperty(double base, int exponent, String description) {
        double x = base;
        double xPowerN = Math.pow(x, exponent);

        double log2X = log2Function.calculate(x, EPSILON);
        double log2XPowerN = log2Function.calculate(xPowerN, EPSILON);
        double nTimesLog2X = exponent * log2X;

        assertEquals(log2XPowerN, nTimesLog2X, EPSILON,
                "log2(" + x + "^" + exponent + ") should equal " + exponent + "*log2(" + x + ")");
    }

    @ParameterizedTest(name = "Log2({0}*{1}) = Log2({0}) + Log2({1})")
    @DisplayName("Log2 function should verify the property log2(xy) = log2(x) + log2(y)")
    @CsvFileSource(resources = "/log2_multiplication_property.csv", numLinesToSkip = 1)
    void testLog2MultiplicationProperty(double x, double y, String description) {
        double product = x * y;

        double log2X = log2Function.calculate(x, EPSILON);
        double log2Y = log2Function.calculate(y, EPSILON);
        double log2Product = log2Function.calculate(product, EPSILON);

        assertEquals(log2X + log2Y, log2Product, HIGH_TOLERANCE,
                "log2(" + x + "*" + y + ") should equal log2(" + x + ") + log2(" + y + ")");
    }

    @ParameterizedTest(name = "Log2({0}/{1}) = Log2({0}) - Log2({1})")
    @DisplayName("Log2 function should verify the property log2(x/y) = log2(x) - log2(y)")
    @CsvFileSource(resources = "/log2_division_property.csv", numLinesToSkip = 1)
    void testLog2DivisionProperty(double x, double y, String description) {
        double quotient = x / y;

        double log2X = log2Function.calculate(x, EPSILON);
        double log2Y = log2Function.calculate(y, EPSILON);
        double log2Quotient = log2Function.calculate(quotient, EPSILON);

        assertEquals(log2X - log2Y, log2Quotient, HIGH_TOLERANCE,
                "log2(" + x + "/" + y + ") should equal log2(" + x + ") - log2(" + y + ")");
    }
}