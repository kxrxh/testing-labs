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

        @BeforeEach
        void setUp() {
                // Create the full function hierarchy
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

                systemFunction = new SystemFunction(
                                negativeDomainFunction, positiveDomainFunction);
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
                        "-0.3, 1.0",
                        "-0.7, 1.0",
                        "0.5, 1.0",
                        "1.0, 1.0",
                        "2.0, 1.0"
        })
        @DisplayName("Test that the system function calculates correctly")
        void testCalculate(double x, double epsilon) {
                if (systemFunction.isInDomain(x)) {
                        double result = systemFunction.calculate(x, epsilon);
                        // We're just testing that it doesn't throw an exception
                        // and returns a finite value
                        assertTrue(Double.isFinite(result));
                } else {
                        assertThrows(IllegalArgumentException.class, () -> {
                                systemFunction.calculate(x, epsilon);
                        });
                }
        }

        @Test
        @DisplayName("Test negative domain calculation")
        void testNegativeDomainCalculation() {
                double x = -0.3; // A value in the negative domain
                double result = systemFunction.calculate(x, EPSILON);

                // Calculate expected result manually
                SinFunction sinFunction = new SinFunction();
                CosFunction cosFunction = new CosFunction(sinFunction);
                SecFunction secFunction = new SecFunction(cosFunction);
                CscFunction cscFunction = new CscFunction(sinFunction);

                double sin = sinFunction.calculate(x, EPSILON);
                double cos = cosFunction.calculate(x, EPSILON);
                double sec = secFunction.calculate(x, EPSILON);
                double csc = cscFunction.calculate(x, EPSILON);

                double expected = Math.pow((sec * csc) / cos - sec, 2) - sin;

                assertEquals(expected, result, EPSILON);
        }

        @Test
        @DisplayName("Test positive domain calculation")
        void testPositiveDomainCalculation() {
                double x = 2.0; // A value in the positive domain
                double result = systemFunction.calculate(x, EPSILON);

                // Calculate expected result manually
                LnFunction lnFunction = new LnFunction();
                Log2Function log2Function = new Log2Function(lnFunction);
                Log10Function log10Function = new Log10Function(lnFunction);
                Log5Function log5Function = new Log5Function(lnFunction);

                double log2 = log2Function.calculate(x, EPSILON);
                double log10 = log10Function.calculate(x, EPSILON);
                double log5 = log5Function.calculate(x, EPSILON);

                double expected = Math.pow(log2 + log10, 2) - log2 - log10 - log5;

                assertEquals(expected, result, EPSILON);
        }
}
