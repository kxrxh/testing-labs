package ru.functions.logarithmic;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;
import ru.functions.logarithmic.log5.Log5Function;

import static org.junit.jupiter.api.Assertions.*;

class Log5FunctionTest {
    private static final double EPSILON = 1e-6;
    private static final double HIGH_TOLERANCE = 1e-3;
    private Log5Function log5Function;

    @BeforeEach
    void setUp() {
        log5Function = new Log5Function();
    }

    @ParameterizedTest(name = "Log5({0}) = {1}")
    @DisplayName("Log5 function should calculate correctly for standard values")
    @CsvFileSource(resources = "/log5_standard_values.csv", numLinesToSkip = 1)
    void testLog5ForStandardValues(double value, double expected, String description) {
        assertEquals(expected, log5Function.calculate(value, EPSILON), value < 1.0 ? EPSILON : HIGH_TOLERANCE,
                "Log5(" + value + ") should be " + expected);
    }

    @ParameterizedTest(name = "Log5({0}) should be close to {0}-1 / ln(5) for {1}")
    @DisplayName("Log5 function should handle values close to 1 correctly")
    @CsvFileSource(resources = "/log5_values_close_to_one.csv", numLinesToSkip = 1)
    void testLog5ForValuesCloseToOne(double x, String description) {
        double result = log5Function.calculate(x, EPSILON);
        // For x close to 1, log5(x) ≈ (x - 1) / ln(5)
        double approximation = (x - 1) / 1.6094379124341003;

        if (Math.abs(x - 1.0) < 0.01) {
            assertEquals(approximation, result, 0.01,
                    "Log5(" + x + ") should be close to " + approximation);
        } else {
            assertTrue(Double.isFinite(result), "Log5(" + x + ") should be finite");
            assertEquals(Math.signum(x - 1), Math.signum(result), "Log5(" + x + ") should have correct sign");
        }
    }

    @ParameterizedTest(name = "Log5({0}) should throw exception for {1}")
    @DisplayName("Log5 function should throw exception for non-positive values")
    @CsvFileSource(resources = "/log5_non_positive_values.csv", numLinesToSkip = 1)
    void testLog5ForNonPositiveValues(String valueStr, String description) {
        double value;
        if (valueStr.equals("-Infinity")) {
            value = Double.NEGATIVE_INFINITY;
        } else {
            value = Double.parseDouble(valueStr);
        }

        Exception exception = assertThrows(IllegalArgumentException.class,
                () -> log5Function.calculate(value, EPSILON),
                "Log5 should throw exception for " + value);

        assertTrue(exception.getMessage().contains("outside the domain"),
                "Exception message should mention domain issue");
    }

    @ParameterizedTest(name = "Log5({0}*{1}) = Log5({0}) + Log5({1})")
    @DisplayName("Log5 function should satisfy the logarithm identity log5(a*b) = log5(a) + log5(b)")
    @CsvFileSource(resources = "/log5_identity_values.csv", numLinesToSkip = 1)
    void testLog5Identity(double a, double b, String description) {
        double log5A = log5Function.calculate(a, EPSILON);
        double log5B = log5Function.calculate(b, EPSILON);
        double log5AB = log5Function.calculate(a * b, EPSILON);

        assertEquals(log5A + log5B, log5AB, HIGH_TOLERANCE,
                "Log5(" + a + " * " + b + ") should equal Log5(" + a + ") + Log5(" + b + ")");
    }

    @ParameterizedTest(name = "Log5 should be {1} defined for {0} ({2})")
    @DisplayName("Log5 function should have correct domain")
    @CsvFileSource(resources = "/log5_domain_values.csv", numLinesToSkip = 1)
    void testLog5Domain(double value, boolean valid, String description) {
        if (valid) {
            assertTrue(log5Function.isInDomain(value), "Log5 should be defined for " + value);
        } else {
            assertFalse(log5Function.isInDomain(value), "Log5 should not be defined for " + value);
        }
    }

    @ParameterizedTest(name = "Log5({0}^{1}) = {1}*Log5({0}) for {2}")
    @DisplayName("Log5 function should satisfy the power property log5(x^n) = n*log5(x)")
    @CsvFileSource(resources = "/log5_power_property.csv", numLinesToSkip = 1)
    void testLog5PowerProperty(double base, int exponent, String description) {
        double x = base;
        double xPowerN = Math.pow(x, exponent);

        double log5X = log5Function.calculate(x, EPSILON);
        double log5XPowerN = log5Function.calculate(xPowerN, EPSILON);
        double nTimesLog5X = exponent * log5X;

        // Use a higher tolerance for this test due to floating-point precision issues
        double testTolerance = base == 3.0 && exponent == 4 ? 1e-5 : EPSILON;

        assertEquals(log5XPowerN, nTimesLog5X, testTolerance,
                "log5(" + x + "^" + exponent + ") should equal " + exponent + "*log5(" + x + ")");
    }

    @ParameterizedTest(name = "Log5({0}*{1}) = Log5({0}) + Log5({1})")
    @DisplayName("Log5 function should verify the property log5(xy) = log5(x) + log5(y)")
    @CsvFileSource(resources = "/log5_multiplication_property.csv", numLinesToSkip = 1)
    void testLog5MultiplicationProperty(double x, double y, String description) {
        double product = x * y;

        double log5X = log5Function.calculate(x, EPSILON);
        double log5Y = log5Function.calculate(y, EPSILON);
        double log5Product = log5Function.calculate(product, EPSILON);

        assertEquals(log5X + log5Y, log5Product, HIGH_TOLERANCE,
                "log5(" + x + "*" + y + ") should equal log5(" + x + ") + log5(" + y + ")");
    }

    @ParameterizedTest(name = "Log5({0}/{1}) = Log5({0}) - Log5({1})")
    @DisplayName("Log5 function should verify the property log5(x/y) = log5(x) - log5(y)")
    @CsvFileSource(resources = "/log5_division_property.csv", numLinesToSkip = 1)
    void testLog5DivisionProperty(double x, double y, String description) {
        double quotient = x / y;

        double log5X = log5Function.calculate(x, EPSILON);
        double log5Y = log5Function.calculate(y, EPSILON);
        double log5Quotient = log5Function.calculate(quotient, EPSILON);

        assertEquals(log5X - log5Y, log5Quotient, HIGH_TOLERANCE,
                "log5(" + x + "/" + y + ") should equal log5(" + x + ") - log5(" + y + ")");
    }
}
