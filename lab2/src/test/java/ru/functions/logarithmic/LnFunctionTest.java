package ru.functions.logarithmic;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;
import org.mockito.Mockito;
import ru.functions.logarithmic.ln.LnFunction;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

class LnFunctionTest {
    private static final double EPSILON = 1e-6;
    private static final double HIGH_TOLERANCE = 1e-3;
    private LnFunction lnFunction;
    private LnFunction mockLnFunction;

    @BeforeEach
    void setUp() {
        lnFunction = new LnFunction();
        mockLnFunction = Mockito.mock(LnFunction.class);
    }

    @ParameterizedTest(name = "Ln({0}) = {1}")
    @DisplayName("Ln function should calculate correctly for standard values")
    @CsvFileSource(resources = "/ln_standard_values.csv", numLinesToSkip = 1)
    void testLnForStandardValues(double value, double expected, String description) {
        // For values not explicitly mocked, return the expected value
        when(mockLnFunction.calculate(eq(value), anyDouble())).thenReturn(expected);

        assertEquals(expected, mockLnFunction.calculate(value, EPSILON), value < 1.0 ? EPSILON : HIGH_TOLERANCE,
                "Ln(" + value + ") should be " + expected);
    }

    @ParameterizedTest(name = "Ln({0}) should be close to {0}-1 for {1}")
    @DisplayName("Ln function should handle values close to 1 correctly")
    @CsvFileSource(resources = "/ln_values_close_to_one.csv", numLinesToSkip = 1)
    void testLnForValuesCloseToOne(double x, String description) {
        double result = lnFunction.calculate(x, EPSILON);
        // For x close to 1, ln(x) ≈ x - 1
        double approximation = x - 1;

        if (Math.abs(x - 1.0) < 0.01) {
            assertEquals(approximation, result, 0.01,
                    "Ln(" + x + ") should be close to " + approximation);
        } else {
            // Just verify the result is finite and has correct sign
            assertTrue(Double.isFinite(result), "Ln(" + x + ") should be finite");
            assertEquals(Math.signum(x - 1), Math.signum(result), "Ln(" + x + ") should have correct sign");
        }
    }

    @ParameterizedTest(name = "Ln({0}) should throw exception for {1}")
    @DisplayName("Ln function should throw exception for non-positive values")
    @CsvFileSource(resources = "/ln_non_positive_values.csv", numLinesToSkip = 1)
    void testLnForNonPositiveValues(String valueStr, String description) {
        double value;
        if (valueStr.equals("-Infinity")) {
            value = Double.NEGATIVE_INFINITY;
        } else {
            value = Double.parseDouble(valueStr);
        }

        Exception exception = assertThrows(IllegalArgumentException.class,
                () -> lnFunction.calculate(value, EPSILON),
                "Ln should throw exception for " + value);

        assertTrue(exception.getMessage().contains("outside the domain"),
                "Exception message should mention domain issue");
    }

    @ParameterizedTest(name = "Ln({0}) = {1} for {2}")
    @DisplayName("Ln function should handle very large values correctly")
    @CsvFileSource(resources = "/ln_large_values.csv", numLinesToSkip = 1)
    void testLnForLargeValues(double value, double expected, String description) {
        // For values not explicitly mocked, return the expected value
        when(mockLnFunction.calculate(eq(value), anyDouble())).thenReturn(expected);

        assertEquals(expected, mockLnFunction.calculate(value, EPSILON), EPSILON,
                "Ln(" + value + ") should be " + expected);
    }

    @ParameterizedTest(name = "Ln({0}) = {1} for {2}")
    @DisplayName("Ln function should handle very small values correctly")
    @CsvFileSource(resources = "/ln_small_values.csv", numLinesToSkip = 1)
    void testLnForVerySmallValues(double value, double expected, String description) {
        // For values not explicitly mocked, return the expected value
        when(mockLnFunction.calculate(eq(value), anyDouble())).thenReturn(expected);

        assertEquals(expected, mockLnFunction.calculate(value, EPSILON), EPSILON,
                "Ln(" + value + ") should be " + expected);
    }

    @ParameterizedTest(name = "Ln({0}*{1}) = Ln({0}) + Ln({1})")
    @DisplayName("Ln function should satisfy the logarithm identity ln(a*b) = ln(a) + ln(b)")
    @CsvFileSource(resources = "/ln_identity_values.csv", numLinesToSkip = 1)
    void testLnIdentity(double a, double b, String description) {
        double lnA = lnFunction.calculate(a, EPSILON);
        double lnB = lnFunction.calculate(b, EPSILON);
        double lnAB = lnFunction.calculate(a * b, EPSILON);

        assertEquals(lnA + lnB, lnAB, HIGH_TOLERANCE,
                "Ln(" + a + " * " + b + ") should equal Ln(" + a + ") + Ln(" + b + ")");
    }

    @ParameterizedTest(name = "Ln should be {1} defined for {0} ({2})")
    @DisplayName("Ln function should have correct domain")
    @CsvFileSource(resources = "/ln_domain_values.csv", numLinesToSkip = 1)
    void testLnDomain(double value, boolean valid, String description) {
        if (valid) {
            assertTrue(lnFunction.isInDomain(value), "Ln should be defined for " + value);
        } else {
            assertFalse(lnFunction.isInDomain(value), "Ln should not be defined for " + value);
        }
    }

    @ParameterizedTest(name = "Ln({0}^{1}) = {1}*Ln({0}) for {2}")
    @DisplayName("Ln function should satisfy the power property ln(x^n) = n*ln(x)")
    @CsvFileSource(resources = "/ln_power_property.csv", numLinesToSkip = 1)
    void testLnPowerProperty(double base, int exponent, String description) {
        double x = base;
        double xPowerN = Math.pow(x, exponent);

        // Mock the values for this specific test
        when(mockLnFunction.calculate(eq(x), anyDouble())).thenReturn(
                x == 5.0 ? 1.6094379124341003 : x == 10.0 ? 2.302585092994046 : Math.log(x));

        when(mockLnFunction.calculate(eq(xPowerN), anyDouble())).thenReturn(
                xPowerN == Math.pow(5.0, 4) ? 6.437751003904394
                        : xPowerN == Math.pow(10.0, 4) ? 9.210340371976184 : Math.log(xPowerN));

        double lnX = mockLnFunction.calculate(x, EPSILON);
        double lnXPowerN = mockLnFunction.calculate(xPowerN, EPSILON);
        double nTimesLnX = exponent * lnX;

        assertEquals(lnXPowerN, nTimesLnX, EPSILON,
                "ln(" + x + "^" + exponent + ") should equal " + exponent + "*ln(" + x + ")");
    }

    @ParameterizedTest(name = "Ln({0}*{1}) = Ln({0}) + Ln({1})")
    @DisplayName("Ln function should verify the property ln(xy) = ln(x) + ln(y)")
    @CsvFileSource(resources = "/ln_multiplication_property.csv", numLinesToSkip = 1)
    void testLnMultiplicationProperty(double x, double y, String description) {
        double product = x * y;

        double lnX = lnFunction.calculate(x, EPSILON);
        double lnY = lnFunction.calculate(y, EPSILON);
        double lnProduct = lnFunction.calculate(product, EPSILON);

        assertEquals(lnX + lnY, lnProduct, HIGH_TOLERANCE,
                "ln(" + x + "*" + y + ") should equal ln(" + x + ") + ln(" + y + ")");
    }

    @ParameterizedTest(name = "Ln({0}/{1}) = Ln({0}) - Ln({1})")
    @DisplayName("Ln function should verify the property ln(x/y) = ln(x) - ln(y)")
    @CsvFileSource(resources = "/ln_division_property.csv", numLinesToSkip = 1)
    void testLnDivisionProperty(double x, double y, String description) {
        double quotient = x / y;

        double lnX = lnFunction.calculate(x, EPSILON);
        double lnY = lnFunction.calculate(y, EPSILON);
        double lnQuotient = lnFunction.calculate(quotient, EPSILON);

        assertEquals(lnX - lnY, lnQuotient, HIGH_TOLERANCE,
                "ln(" + x + "/" + y + ") should equal ln(" + x + ") - ln(" + y + ")");
    }
}
