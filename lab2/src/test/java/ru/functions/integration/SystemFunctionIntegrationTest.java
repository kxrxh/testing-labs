package ru.functions.integration;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import ru.functions.logarithmic.LnFunction;
import ru.functions.logarithmic.LnFunctionStub;
import ru.functions.logarithmic.Log10Function;
import ru.functions.logarithmic.Log10FunctionStub;
import ru.functions.logarithmic.Log2Function;
import ru.functions.logarithmic.Log2FunctionStub;
import ru.functions.logarithmic.Log5Function;
import ru.functions.logarithmic.Log5FunctionStub;
import ru.functions.system.NegativeDomainFunction;
import ru.functions.system.NegativeDomainFunctionStub;
import ru.functions.system.PositiveDomainFunction;
import ru.functions.system.PositiveDomainFunctionStub;
import ru.functions.system.SystemFunction;
import ru.functions.system.SystemFunctionInterface;
import ru.functions.system.SystemFunctionStub;
import ru.functions.trigonometric.CosFunction;
import ru.functions.trigonometric.CosFunctionStub;
import ru.functions.trigonometric.CscFunction;
import ru.functions.trigonometric.CscFunctionStub;
import ru.functions.trigonometric.SecFunction;
import ru.functions.trigonometric.SecFunctionStub;
import ru.functions.trigonometric.SinFunction;
import ru.functions.trigonometric.SinFunctionStub;
import ru.functions.utils.MathUtils;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration tests for the System Function, following a bottom-up integration
 * approach.
 *
 * The integration strategy proceeds in phases:
 * 1. Test base functions (Sin, Ln) using their real implementations (previously
 * unit tested)
 * 2. Test derived trigonometric functions (Cos, Sec, Csc) integrating with Sin
 * 3. Test derived logarithmic functions (Log2, Log10, Log5) integrating with Ln
 * 4. Test domain-specific functions integrating with their respective component
 * functions
 * 5. Test the complete system function with all real implementations
 *
 * This approach validates each integration level before proceeding to higher
 * levels.
 */
@DisplayName("System Function Integration Test")
class SystemFunctionIntegrationTest {
    private static final double EPSILON = 1e-6;

    /**
     * Phase 1: Testing base functions integration
     */
    @Nested
    @DisplayName("Phase 1: Base Functions Integration")
    class BaseFunctionsIntegration {
        // Base functions should be thoroughly unit tested before integration

        @Test
        @DisplayName("Verify Sin function implementation")
        void testSinBaseFunction() {
            SinFunction sinFunction = new SinFunction();

            // Test a few key values
            assertEquals(0.0, sinFunction.calculate(0.0, EPSILON), EPSILON);
            assertEquals(1.0, sinFunction.calculate(MathUtils.HALF_PI, EPSILON), EPSILON);
            assertEquals(0.0, sinFunction.calculate(MathUtils.PI, EPSILON), EPSILON);
            assertEquals(-1.0, sinFunction.calculate(3 * MathUtils.HALF_PI, EPSILON), EPSILON);
        }

        @Test
        @DisplayName("Verify Ln function implementation")
        void testLnBaseFunction() {
            LnFunction lnFunction = new LnFunction();

            // Test a few key values
            assertEquals(0.0, lnFunction.calculate(1.0, EPSILON), EPSILON);
            assertEquals(1.0, lnFunction.calculate(Math.E, EPSILON), EPSILON);
            assertTrue(lnFunction.calculate(2.0, EPSILON) > 0);
            assertTrue(lnFunction.calculate(0.5, EPSILON) < 0);
        }
    }

    /**
     * Phase 2: Trigonometric Function Derivatives Integration
     */
    @Nested
    @DisplayName("Phase 2: Trigonometric Function Derivatives Integration")
    class TrigonometricDerivativesIntegration {

        @Test
        @DisplayName("Verify Cos function integrates with Sin")
        void testCosIntegration() {
            SinFunction sinFunction = new SinFunction();
            CosFunction cosFunction = new CosFunction(sinFunction);

            // Verify interrelationships
            assertEquals(1.0, cosFunction.calculate(0.0, EPSILON), EPSILON);
            assertEquals(0.0, cosFunction.calculate(MathUtils.HALF_PI, EPSILON), EPSILON);
            assertEquals(-1.0, cosFunction.calculate(MathUtils.PI, EPSILON), EPSILON);
            assertEquals(0.0, cosFunction.calculate(3 * MathUtils.HALF_PI, EPSILON), EPSILON);
        }

        @Test
        @DisplayName("Verify Sec and Csc functions integrate with their bases")
        void testSecCscIntegration() {
            SinFunction sinFunction = new SinFunction();
            CosFunction cosFunction = new CosFunction(sinFunction);
            SecFunction secFunction = new SecFunction(cosFunction);
            CscFunction cscFunction = new CscFunction(sinFunction);

            // Test a representative value away from singularities
            double x = MathUtils.PI / 4; // 45 degrees

            // Sec(x) = 1/Cos(x)
            assertEquals(1.0 / cosFunction.calculate(x, EPSILON),
                    secFunction.calculate(x, EPSILON),
                    EPSILON);

            // Csc(x) = 1/Sin(x)
            assertEquals(1.0 / sinFunction.calculate(x, EPSILON),
                    cscFunction.calculate(x, EPSILON),
                    EPSILON);

            // Verify proper handling of domain restrictions
            assertFalse(secFunction.isInDomain(MathUtils.HALF_PI));
            assertFalse(cscFunction.isInDomain(0.0));
        }
    }

    /**
     * Phase 3: Logarithmic Function Derivatives Integration
     */
    @Nested
    @DisplayName("Phase 3: Logarithmic Function Derivatives Integration")
    class LogarithmicDerivativesIntegration {

        @Test
        @DisplayName("Verify Log bases integrate with Ln")
        void testLogBasesIntegration() {
            LnFunction lnFunction = new LnFunction();
            Log2Function log2Function = new Log2Function(lnFunction);
            Log10Function log10Function = new Log10Function(lnFunction);
            Log5Function log5Function = new Log5Function(lnFunction);

            // Verify Log2(2) = 1
            assertEquals(1.0, log2Function.calculate(2.0, EPSILON), EPSILON);

            // Verify Log10(10) = 1
            assertEquals(1.0, log10Function.calculate(10.0, EPSILON), EPSILON);

            // Verify Log5(5) = 1
            assertEquals(1.0, log5Function.calculate(5.0, EPSILON), EPSILON);

            // Verify Log relationships
            // Log_b(x) = ln(x) / ln(b)
            double x = 7.0;

            assertEquals(lnFunction.calculate(x, EPSILON) / lnFunction.calculate(2.0, EPSILON),
                    log2Function.calculate(x, EPSILON),
                    EPSILON);

            assertEquals(lnFunction.calculate(x, EPSILON) / lnFunction.calculate(10.0, EPSILON),
                    log10Function.calculate(x, EPSILON),
                    EPSILON);

            assertEquals(lnFunction.calculate(x, EPSILON) / lnFunction.calculate(5.0, EPSILON),
                    log5Function.calculate(x, EPSILON),
                    EPSILON);
        }
    }

    /**
     * Phase 4: Domain-Specific Functions Integration
     */
    @Nested
    @DisplayName("Phase 4: Domain-Specific Functions Integration")
    class DomainFunctionsIntegration {

        @Test
        @DisplayName("Verify NegativeDomainFunction integrates with trigonometric components")
        void testNegativeDomainIntegration() {
            // Set up real implementations
            SinFunction sinFunction = new SinFunction();
            CosFunction cosFunction = new CosFunction(sinFunction);
            SecFunction secFunction = new SecFunction(cosFunction);
            CscFunction cscFunction = new CscFunction(sinFunction);

            NegativeDomainFunction negativeDomainFunction = new NegativeDomainFunction(
                    sinFunction, cosFunction, secFunction, cscFunction);

            // Test valid points
            double[] validTestPoints = { -0.1, -0.5, -1.0, -2.0 };

            for (double x : validTestPoints) {
                if (negativeDomainFunction.isInDomain(x)) {
                    double sin = sinFunction.calculate(x, EPSILON);
                    double cos = cosFunction.calculate(x, EPSILON);
                    double sec = secFunction.calculate(x, EPSILON);
                    double csc = cscFunction.calculate(x, EPSILON);

                    // Manual calculation: (((((sec(x) * csc(x)) / cos(x)) - sec(x)) ^ 2) - sin(x))
                    double expected = Math.pow(((sec * csc) / cos) - sec, 2) - sin;
                    double actual = negativeDomainFunction.calculate(x, EPSILON);

                    assertEquals(expected, actual, EPSILON,
                            "NegativeDomainFunction calculation mismatch for x = " + x);
                }
            }
        }

        @Test
        @DisplayName("Verify PositiveDomainFunction integrates with logarithmic components")
        void testPositiveDomainIntegration() {
            // Set up real implementations
            LnFunction lnFunction = new LnFunction();
            Log2Function log2Function = new Log2Function(lnFunction);
            Log10Function log10Function = new Log10Function(lnFunction);
            Log5Function log5Function = new Log5Function(lnFunction);

            PositiveDomainFunction positiveDomainFunction = new PositiveDomainFunction(
                    log2Function, log10Function, log5Function);

            // Test valid points
            double[] validTestPoints = { 0.1, 0.5, 1.0, 2.0, 5.0 };

            for (double x : validTestPoints) {
                if (positiveDomainFunction.isInDomain(x)) {
                    double log2 = log2Function.calculate(x, EPSILON);
                    double log10 = log10Function.calculate(x, EPSILON);
                    double log5 = log5Function.calculate(x, EPSILON);

                    // Manual calculation: (((((log_2(x) + log_10(x)) ^ 2) - log_2(x)) - log_10(x))
                    // - log_5(x))
                    double expected = Math.pow(log2 + log10, 2) - log2 - log10 - log5;
                    double actual = positiveDomainFunction.calculate(x, EPSILON);

                    assertEquals(expected, actual, EPSILON,
                            "PositiveDomainFunction calculation mismatch for x = " + x);
                }
            }
        }
    }

    /**
     * Phase 5: Complete System Function Integration
     */
    @Nested
    @DisplayName("Phase 5: Complete System Function Integration")
    class CompleteSystemIntegration {
        private SystemFunctionInterface setupRealSystemFunction() {
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
            NegativeDomainFunction negativeDomainFunction = new NegativeDomainFunction(
                    sinFunction, cosFunction, secFunction, cscFunction);

            PositiveDomainFunction positiveDomainFunction = new PositiveDomainFunction(
                    log2Function, log10Function, log5Function);

            // Main system function
            return new SystemFunction(negativeDomainFunction, positiveDomainFunction);
        }

        @Test
        @DisplayName("Complete system should correctly handle domain delegation")
        void testCompleteDomainDelegation() {
            SystemFunctionInterface systemFunction = setupRealSystemFunction();

            // Test domain correctness
            assertFalse(systemFunction.isInDomain(0.0), "x = 0 should be outside the domain");

            // Test delegation by checking applicable sub-function
            assertEquals(0, systemFunction.getApplicableSubFunction(-0.1),
                    "System should delegate to negative domain for x = -0.1");

            assertEquals(1, systemFunction.getApplicableSubFunction(0.1),
                    "System should delegate to positive domain for x = 0.1");
        }

        @ParameterizedTest
        @DisplayName("System function should correctly calculate for negative domain")
        @CsvSource({
                "-0.25, -0.7467997596",
                "-1.1, -0.4591749",
                "-1.5, -0.8773961"
        })
        void testNegativeDomainCalculation(double x, double expected) {
            SystemFunctionInterface systemFunction = setupRealSystemFunction();

            if (systemFunction.isInDomain(x)) {
                double result = systemFunction.calculate(x, EPSILON);
                assertEquals(expected, result, 0.001,
                        "System function negative domain calculation for x = " + x);
            }
        }

        @ParameterizedTest
        @DisplayName("System function should correctly calculate for positive domain")
        @CsvSource({
                "1.5, 1.8768626",
                "2.0, 2.7067137",
                "3.0, 5.9138996"
        })
        void testPositiveDomainCalculation(double x, double expected) {
            SystemFunctionInterface systemFunction = setupRealSystemFunction();

            if (systemFunction.isInDomain(x)) {
                double result = systemFunction.calculate(x, EPSILON);
                assertEquals(expected, result, 0.001,
                        "System function positive domain calculation for x = " + x);
            }
        }

        @Test
        @DisplayName("System function should handle domain boundaries and invalid inputs")
        void testDomainBoundaries() {
            SystemFunctionInterface systemFunction = setupRealSystemFunction();

            // x = 0 is outside the domain
            assertThrows(IllegalArgumentException.class,
                    () -> systemFunction.calculate(0.0, EPSILON),
                    "System function should throw exception for x = 0");

            // Critical points in negative domain
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
    }

    /**
     * Phase 6: Mixed Integration with Some Stubs
     *
     * This demonstrates partial integration by using stubs for certain components.
     */
    @Nested
    @DisplayName("Phase 6: Mixed Integration with Some Stubs")
    class MixedIntegration {

        @Test
        @DisplayName("System with real trigonometric functions and stub logarithmic functions")
        void testMixedTrigonometricReal() {
            // Real trigonometric functions
            SinFunction sinFunction = new SinFunction();
            CosFunction cosFunction = new CosFunction(sinFunction);
            SecFunction secFunction = new SecFunction(cosFunction);
            CscFunction cscFunction = new CscFunction(sinFunction);

            // Stub logarithmic functions
            LnFunctionStub lnFunctionStub = new LnFunctionStub();
            Log2FunctionStub log2FunctionStub = new Log2FunctionStub();
            Log10FunctionStub log10FunctionStub = new Log10FunctionStub();
            Log5FunctionStub log5FunctionStub = new Log5FunctionStub();

            // Mixed domain functions
            NegativeDomainFunction negativeDomainFunction = new NegativeDomainFunction(
                    sinFunction, cosFunction, secFunction, cscFunction);

            PositiveDomainFunctionStub positiveDomainFunctionStub = new PositiveDomainFunctionStub(
                    log2FunctionStub, log10FunctionStub, log5FunctionStub);

            // Mixed system function
            SystemFunctionInterface systemFunction = new SystemFunction(
                    negativeDomainFunction, positiveDomainFunctionStub);

            // Test domain correctness
            assertTrue(systemFunction.isInDomain(-0.1), "x = -0.1 should be in the domain");

            // Test calculation for negative domain (should use real implementation)
            double x = -0.25;
            double expected = -0.7467997596;
            double result = systemFunction.calculate(x, EPSILON);
            assertEquals(expected, result, 0.001,
                    "Mixed system with real trig functions should calculate correctly for x = " + x);
        }

        @Test
        @DisplayName("System with stub trigonometric functions and real logarithmic functions")
        void testMixedLogarithmicReal() {
            // Stub trigonometric functions
            SinFunctionStub sinFunctionStub = new SinFunctionStub();
            CosFunctionStub cosFunctionStub = new CosFunctionStub(sinFunctionStub);
            SecFunctionStub secFunctionStub = new SecFunctionStub(cosFunctionStub);
            CscFunctionStub cscFunctionStub = new CscFunctionStub(sinFunctionStub);

            // Real logarithmic functions
            LnFunction lnFunction = new LnFunction();
            Log2Function log2Function = new Log2Function(lnFunction);
            Log10Function log10Function = new Log10Function(lnFunction);
            Log5Function log5Function = new Log5Function(lnFunction);

            // Mixed domain functions
            NegativeDomainFunctionStub negativeDomainFunctionStub = new NegativeDomainFunctionStub(
                    sinFunctionStub, cosFunctionStub, secFunctionStub, cscFunctionStub);

            PositiveDomainFunction positiveDomainFunction = new PositiveDomainFunction(
                    log2Function, log10Function, log5Function);

            // Mixed system function
            SystemFunctionInterface systemFunction = new SystemFunction(
                    negativeDomainFunctionStub, positiveDomainFunction);

            // Test domain correctness
            assertTrue(systemFunction.isInDomain(2.0), "x = 2.0 should be in the domain");

            // Test calculation for positive domain (should use real implementation)
            double x = 2.0;
            double expected = 2.7067137;
            double result = systemFunction.calculate(x, EPSILON);
            assertEquals(expected, result, 0.001,
                    "Mixed system with real log functions should calculate correctly for x = " + x);
        }
    }
}
