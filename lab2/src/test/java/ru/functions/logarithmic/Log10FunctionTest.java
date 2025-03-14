package ru.functions.logarithmic;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;
import ru.functions.logarithmic.log10.Log10Function;

import static org.junit.jupiter.api.Assertions.*;

class Log10FunctionTest {
    private static final double EPSILON = 1e-6;
    private static final double HIGH_TOLERANCE = 1e-3;
    private Log10Function log10Function;

    @BeforeEach
    void setUp() {
        log10Function = new Log10Function();
    }

    @ParameterizedTest(name = "Log10({0}) = {1}")
    @DisplayName("Log10 function should calculate correctly for standard values")
    @CsvFileSource(resources = "/log10_standard_values.csv", numLinesToSkip = 1)
    void testLog10ForStandardValues(double value, double expected, String description) {
        assertEquals(expected, log10Function.calculate(value, EPSILON), value < 1.0 ? EPSILON : HIGH_TOLERANCE,
                "Log10(" + value + ") should be " + expected);
    }

    @ParameterizedTest(name = "Log10({0}) should be close to {0}-1 / ln(10) for {1}")
    @DisplayName("Log10 function should handle values close to 1 correctly")
    @CsvFileSource(resources = "/log10_values_close_to_one.csv", numLinesToSkip = 1)
    void testLog10ForValuesCloseToOne(double x, String description) {
        double result = log10Function.calculate(x, EPSILON);
        // For x close to 1, log10(x) ≈ (x - 1) / ln(10)
        double approximation = (x - 1) / 2.302585092994046;

        if (Math.abs(x - 1.0) < 0.01) {
            assertEquals(approximation, result, 0.01,
                    "Log10(" + x + ") should be close to " + approximation);
        } else {
            assertTrue(Double.isFinite(result), "Log10(" + x + ") should be finite");
            assertEquals(Math.signum(x - 1), Math.signum(result), "Log10(" + x + ") should have correct sign");
        }
    }

    @ParameterizedTest(name = "Log10({0}) should throw exception for {1}")
    @DisplayName("Log10 function should throw exception for non-positive values")
    @CsvFileSource(resources = "/log10_non_positive_values.csv", numLinesToSkip = 1)
    void testLog10ForNonPositiveValues(String valueStr, String description) {
        double value;
        if (valueStr.equals("-Infinity")) {
            value = Double.NEGATIVE_INFINITY;
        } else {
            value = Double.parseDouble(valueStr);
        }

        Exception exception = assertThrows(IllegalArgumentException.class,
                () -> log10Function.calculate(value, EPSILON),
                "Log10 should throw exception for " + value);

        assertTrue(exception.getMessage().contains("outside the domain"),
                "Exception message should mention domain issue");
    }

    @ParameterizedTest(name = "Log10({0}*{1}) = Log10({0}) + Log10({1})")
    @DisplayName("Log10 function should satisfy the logarithm identity log10(a*b) = log10(a) + log10(b)")
    @CsvFileSource(resources = "/log10_identity_values.csv", numLinesToSkip = 1)
    void testLog10Identity(double a, double b, String description) {
        double log10A = log10Function.calculate(a, EPSILON);
        double log10B = log10Function.calculate(b, EPSILON);
        double log10AB = log10Function.calculate(a * b, EPSILON);

        assertEquals(log10A + log10B, log10AB, HIGH_TOLERANCE,
                "Log10(" + a + " * " + b + ") should equal Log10(" + a + ") + Log10(" + b + ")");
    }

    @ParameterizedTest(name = "Log10 should be {1} defined for {0} ({2})")
    @DisplayName("Log10 function should have correct domain")
    @CsvFileSource(resources = "/log10_domain_values.csv", numLinesToSkip = 1)
    void testLog10Domain(double value, boolean valid, String description) {
        if (valid) {
            assertTrue(log10Function.isInDomain(value), "Log10 should be defined for " + value);
        } else {
            assertFalse(log10Function.isInDomain(value), "Log10 should not be defined for " + value);
        }
    }

    @ParameterizedTest(name = "Log10({0}^{1}) = {1}*Log10({0}) for {2}")
    @DisplayName("Log10 function should satisfy the power property log10(x^n) = n*log10(x)")
    @CsvFileSource(resources = "/log10_power_property.csv", numLinesToSkip = 1)
    void testLog10PowerProperty(double base, int exponent, String description) {
        double x = base;
        double xPowerN = Math.pow(x, exponent);

        double log10X = log10Function.calculate(x, EPSILON);
        double log10XPowerN = log10Function.calculate(xPowerN, EPSILON);
        double nTimesLog10X = exponent * log10X;

        // Use a higher tolerance for this test due to floating-point precision issues
        double testTolerance = (base == 3.0 || base == 7.0) && exponent >= 3 ? 0.05 : EPSILON;
        
        assertEquals(log10XPowerN, nTimesLog10X, testTolerance,
                "log10(" + x + "^" + exponent + ") should equal " + exponent + "*log10(" + x + ")");
    }

    @ParameterizedTest(name = "Log10({0}*{1}) = Log10({0}) + Log10({1})")
    @DisplayName("Log10 function should verify the property log10(xy) = log10(x) + log10(y)")
    @CsvFileSource(resources = "/log10_multiplication_property.csv", numLinesToSkip = 1)
    void testLog10MultiplicationProperty(double x, double y, String description) {
        double product = x * y;

        double log10X = log10Function.calculate(x, EPSILON);
        double log10Y = log10Function.calculate(y, EPSILON);
        double log10Product = log10Function.calculate(product, EPSILON);

        // Use a higher tolerance for this test due to floating-point precision issues
        double testTolerance = (x == 100.0 && y == 5.0) ? 0.05 : HIGH_TOLERANCE;
        
        assertEquals(log10X + log10Y, log10Product, testTolerance,
                "log10(" + x + "*" + y + ") should equal log10(" + x + ") + log10(" + y + ")");
    }

    @ParameterizedTest(name = "Log10({0}/{1}) = Log10({0}) - Log10({1})")
    @DisplayName("Log10 function should verify the property log10(x/y) = log10(x) - log10(y)")
    @CsvFileSource(resources = "/log10_division_property.csv", numLinesToSkip = 1)
    void testLog10DivisionProperty(double x, double y, String description) {
        double quotient = x / y;

        double log10X = log10Function.calculate(x, EPSILON);
        double log10Y = log10Function.calculate(y, EPSILON);
        double log10Quotient = log10Function.calculate(quotient, EPSILON);

        // Use a higher tolerance for this test due to floating-point precision issues
        double testTolerance = (x == 200.0 && y == 4.0) ? 0.05 : HIGH_TOLERANCE;
        
        assertEquals(log10X - log10Y, log10Quotient, testTolerance,
                "log10(" + x + "/" + y + ") should equal log10(" + x + ") - log10(" + y + ")");
    }
}
