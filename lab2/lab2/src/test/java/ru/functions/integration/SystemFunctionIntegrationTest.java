package ru.functions.integration;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import ru.functions.logarithmic.LnFunction;
import ru.functions.logarithmic.Log10Function;
import ru.functions.logarithmic.Log2Function;
import ru.functions.logarithmic.Log5Function;
import ru.functions.system.NegativeDomainFunction;
import ru.functions.system.PositiveDomainFunction;
import ru.functions.system.SystemFunction;
import ru.functions.trigonometric.CosFunction;
import ru.functions.trigonometric.CscFunction;
import ru.functions.trigonometric.SecFunction;
import ru.functions.trigonometric.SinFunction;
import ru.functions.utils.MathUtils;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration test for the system function that combines both the negative and
 * positive domain functions.
 * This test verifies that the system function correctly integrates all its
 * components.
 */
class SystemFunctionIntegrationTest {
    private static final double EPSILON = 1e-6;

    private SystemFunction systemFunction;
    private NegativeDomainFunction negativeDomainFunction;
    private PositiveDomainFunction positiveDomainFunction;

    @BeforeEach
    void setUp() {
        // Base functions
        SinFunction sinFunction = new SinFunction();
        CosFunction cosFunction = new CosFunction(sinFunction);
        SecFunction secFunction = new SecFunction(cosFunction);
        CscFunction cscFunction = new CscFunction(sinFunction);

        LnFunction lnFunction = new LnFunction();
        Log2Function log2Function = new Log2Function(lnFunction);
        Log10Function log10Function = new Log10Function(lnFunction);
        Log5Function log5Function = new Log5Function(lnFunction);

        // Domain-specific functions
        negativeDomainFunction = new NegativeDomainFunction(
                sinFunction, cosFunction, secFunction, cscFunction);

        positiveDomainFunction = new PositiveDomainFunction(
                log2Function, log10Function, log5Function);

        // Main system function
        systemFunction = new SystemFunction(
                negativeDomainFunction, positiveDomainFunction);
    }

    @Test
    @DisplayName("System function should correctly delegate to negativeDomainFunction for x ≤ 0")
    void testNegativeDomainDelegation() {
        double[] testValues = { -0.1, -0.5, -1.0, -2.0 };

        for (double x : testValues) {
            if (negativeDomainFunction.isInDomain(x)) {
                double expectedResult = negativeDomainFunction.calculate(x, EPSILON);
                double actualResult = systemFunction.calculate(x, EPSILON);

                assertEquals(expectedResult, actualResult, EPSILON,
                        "System function should delegate to negative domain function for x = " + x);
            }
        }
    }

    @Test
    @DisplayName("System function should correctly delegate to positiveDomainFunction for x > 0")
    void testPositiveDomainDelegation() {
        double[] testValues = { 0.1, 0.5, 1.0, 2.0, 5.0, 10.0 };

        for (double x : testValues) {
            if (positiveDomainFunction.isInDomain(x)) {
                double expectedResult = positiveDomainFunction.calculate(x, EPSILON);
                double actualResult = systemFunction.calculate(x, EPSILON);

                assertEquals(expectedResult, actualResult, EPSILON,
                        "System function should delegate to positive domain function for x = " + x);
            }
        }
    }

    @Test
    @DisplayName("System function should handle domain boundaries correctly")
    void testDomainBoundaries() {
        // x = 0 is outside the domain
        assertFalse(systemFunction.isInDomain(0.0),
                "x = 0 should be outside the domain");

        assertThrows(IllegalArgumentException.class,
                () -> systemFunction.calculate(0.0, EPSILON),
                "System function should throw exception for x = 0");

        // x values close to multiples of π/2 in the negative domain
        double[] criticalPoints = {
                -MathUtils.HALF_PI, -MathUtils.PI, -3 * MathUtils.HALF_PI, -2 * MathUtils.PI
        };

        for (double x : criticalPoints) {
            assertFalse(systemFunction.isInDomain(x),
                    "x = " + x + " should be outside the domain");

            assertThrows(IllegalArgumentException.class,
                    () -> systemFunction.calculate(x, EPSILON),
                    "System function should throw exception for x = " + x);
        }
    }

    @ParameterizedTest
    @DisplayName("System function should calculate correctly for specific test cases")
    @CsvSource({
            "-0.25, -0.7467997596", // example value for negative domain
            "2.0, 2.7067137" // example value for positive domain
    })
    void testSpecificTestCases(double x, double expected) {
        double result = systemFunction.calculate(x, EPSILON);
        assertEquals(expected, result, 0.001,
                "System function should calculate correctly for x = " + x);
    }

    @Test
    @DisplayName("System function should handle edge cases correctly")
    void testEdgeCases() {
        // Verify that very small negative numbers close to zero work
        double smallNegative = -1e-10;
        if (systemFunction.isInDomain(smallNegative)) {
            double result = systemFunction.calculate(smallNegative, EPSILON);
            assertTrue(Double.isFinite(result),
                    "Result should be finite for x = " + smallNegative);
        }

        // Verify that very small positive numbers close to zero work
        double smallPositive = 1e-10;
        if (systemFunction.isInDomain(smallPositive)) {
            double result = systemFunction.calculate(smallPositive, EPSILON);
            assertTrue(Double.isFinite(result),
                    "Result should be finite for x = " + smallPositive);
        }

        // Verify that very large positive numbers work
        double largePositive = 1e10;
        if (systemFunction.isInDomain(largePositive)) {
            double result = systemFunction.calculate(largePositive, EPSILON);
            assertTrue(Double.isFinite(result),
                    "Result should be finite for x = " + largePositive);
        }
    }
}
