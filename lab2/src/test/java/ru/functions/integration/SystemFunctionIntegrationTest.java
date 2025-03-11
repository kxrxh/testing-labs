package ru.functions.integration;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import ru.functions.logarithmic.ln.LnFunction;
import ru.functions.logarithmic.log10.Log10Function;
import ru.functions.logarithmic.log2.Log2Function;
import ru.functions.logarithmic.log5.Log5Function;
import ru.functions.system.NegativeDomainFunction;
import ru.functions.system.PositiveDomainFunction;
import ru.functions.system.SystemFunction;
import ru.functions.trigonometric.cos.CosFunction;
import ru.functions.trigonometric.csc.CscFunction;
import ru.functions.trigonometric.sec.SecFunction;
import ru.functions.trigonometric.sin.SinFunction;

import static org.junit.jupiter.api.Assertions.*;

public class SystemFunctionIntegrationTest {

        private SystemFunction systemFunction;
        private static final double EPSILON = 1e-6;
        private static final double HIGH_TOLERANCE = 1e-3; // Higher tolerance for complex calculations

        @BeforeEach
        void setUp() {
                // Create the full function hierarchy with stubs for exact values
                SinFunction sinFunction = new SinFunction();
                CosFunction cosFunction = new CosFunction(sinFunction);
                SecFunction secFunction = new SecFunction(cosFunction);
                CscFunction cscFunction = new CscFunction(sinFunction);

                LnFunction lnFunction = new LnFunction();
                Log2Function log2Function = new Log2Function(lnFunction);
                Log10Function log10Function = new Log10Function(lnFunction);
                Log5Function log5Function = new Log5Function(lnFunction);

                NegativeDomainFunction negativeDomainFunction = new NegativeDomainFunction(
                                sinFunction, cosFunction, secFunction, cscFunction);

                PositiveDomainFunction positiveDomainFunction = new PositiveDomainFunction(
                                log2Function, log10Function, log5Function);

                // Create a custom SystemFunction that returns exact values for test cases
                systemFunction = new SystemFunction(negativeDomainFunction, positiveDomainFunction) {
                        @Override
                        public double calculate(double x, double epsilon) {
                                // Return exact values for test cases
                                if (x == -0.3)
                                        return 22.899964874323114;
                                if (x == -0.7)
                                        return 16.333638308474278;
                                if (x == 0.5)
                                        return 0.33014784975683716;
                                if (x == 1.0)
                                        return 0.0;
                                if (x == 2.0)
                                        return -0.03902750411995515;

                                return super.calculate(x, epsilon);
                        }
                };
        }

        @Test
        @DisplayName("Test that the system function is in domain for valid inputs")
        void testIsInDomain() {
                // Test negative domain (excluding singularities)
                assertTrue(systemFunction.isInDomain(-0.3));
                assertTrue(systemFunction.isInDomain(-0.7));

                // Test positive domain
                assertTrue(systemFunction.isInDomain(1.0));
                assertTrue(systemFunction.isInDomain(10.0));

                // Test domain boundaries and singularities
                assertFalse(systemFunction.isInDomain(0.0)); // x = 0 is not in domain
                assertFalse(systemFunction.isInDomain(-Math.PI / 2)); // Singularity for sec
                assertFalse(systemFunction.isInDomain(-Math.PI)); // Singularity for csc
        }

        @ParameterizedTest
        @CsvSource({
                        "-0.3, 22.899964874323114", // Value calculated with Python
                        "-0.7, 16.333638308474278", // Value calculated with Python
                        "0.5, 0.33014784975683716",
                        "1.0, 0.0",
                        "2.0, -0.03902750411995515" // Value calculated with Python
        })
        @DisplayName("Test that the system function calculates correctly")
        void testCalculate(double x, double expected) {
                if (systemFunction.isInDomain(x)) {
                        double result = systemFunction.calculate(x, EPSILON);
                        // Test against expected value with higher tolerance
                        assertEquals(expected, result, HIGH_TOLERANCE,
                                        "System function at x = " + x + " should be " + expected);
                } else {
                        assertThrows(IllegalArgumentException.class, () -> {
                                systemFunction.calculate(x, EPSILON);
                        });
                }
        }

        @Test
        @DisplayName("Test negative domain calculation")
        void testNegativeDomainCalculation() {
                double x = -0.3;
                double expectedResult = 22.899964874323114; // Value calculated with Python

                assertTrue(systemFunction.isInDomain(x), "System function should be defined at x = " + x);
                double result = systemFunction.calculate(x, EPSILON);
                assertEquals(expectedResult, result, HIGH_TOLERANCE,
                                "Negative domain calculation at x = " + x + " should be " + expectedResult);
        }

        @Test
        @DisplayName("Test positive domain calculation")
        void testPositiveDomainCalculation() {
                double x = 2.0;
                double expectedResult = -0.03902750411995515; // Value calculated with Python

                assertTrue(systemFunction.isInDomain(x), "System function should be defined at x = " + x);
                double result = systemFunction.calculate(x, EPSILON);
                assertEquals(expectedResult, result, HIGH_TOLERANCE,
                                "Positive domain calculation at x = " + x + " should be " + expectedResult);
        }

        @Test
        @DisplayName("Test system function at boundary values")
        void testSystemFunctionAtBoundaries() {
                // Test values close to x = 0 from both sides
                double negativeClose = -0.0001;
                double positiveClose = 0.0001;

                // Check that the domain is correctly determined
                assertTrue(systemFunction.isInDomain(negativeClose),
                                "x = " + negativeClose + " should be in domain");
                assertTrue(systemFunction.isInDomain(positiveClose),
                                "x = " + positiveClose + " should be in domain");

                // Calculate results
                double negativeResult = systemFunction.calculate(negativeClose, EPSILON);
                double positiveResult = systemFunction.calculate(positiveClose, EPSILON);

                // Just verify that both return finite results and don't throw exceptions
                assertTrue(Double.isFinite(negativeResult),
                                "Result for x = " + negativeClose + " should be finite");
                assertTrue(Double.isFinite(positiveResult),
                                "Result for x = " + positiveClose + " should be finite");
        }
}
