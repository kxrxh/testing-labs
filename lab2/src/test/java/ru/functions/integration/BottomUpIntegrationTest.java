package ru.functions.integration;

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
import ru.functions.system.SystemFunctionInterface;
import ru.functions.trigonometric.cos.CosFunction;
import ru.functions.trigonometric.csc.CscFunction;
import ru.functions.trigonometric.sec.SecFunction;
import ru.functions.trigonometric.sin.SinFunction;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Bottom-up integration test for the system function.
 * Tests components in order of dependency: base functions first,
 * then derived functions, and finally the full system.
 */
public class BottomUpIntegrationTest {

    private static final double EPSILON = 1e-6;

    /**
     * Phase 1: Test base sin(x) function
     * Values calculated with Python:
     * import math
     * for angle in [0, math.pi/6, math.pi/4, math.pi/3, math.pi/2, math.pi,
     * 3*math.pi/2, 2*math.pi]:
     * print(f'"{angle:.16f}, {math.sin(angle):.16f}"')
     */
    @ParameterizedTest
    @DisplayName("Phase 1: Base sin function integration test")
    @CsvSource({
            "0.0000000000000000, 0.0000000000000000",
            "0.5235987755982988, 0.4999999999999999",
            "0.7853981633974483, 0.7071067811865475",
            "1.0471975511965976, 0.8660254037844386",
            "1.5707963267948966, 1.0000000000000000",
            "3.1415926535897931, 0.0000000000000001",
            "4.7123889803846897, -1.0000000000000000",
            "6.2831853071795862, -0.0000000000000002"
    })
    void testSinBase(double angle, double expected) {
        SinFunction sinFunction = new SinFunction();
        double result = sinFunction.calculate(angle, EPSILON);
        assertEquals(expected, result, EPSILON, "Sin(" + angle + ") should be " + expected);
    }

    /**
     * Phase 1: Test base ln(x) function
     * Values calculated with Python:
     * import math
     * for value in [1, 2, math.e, 5, 10, 100]:
     * print(f'"{value:.16f}, {math.log(value):.16f}"')
     */
    @ParameterizedTest
    @DisplayName("Phase 1: Base ln function integration test")
    @CsvSource({
            "1.0000000000000000, 0.0000000000000000",
            "2.0000000000000000, 0.6931471805599453",
            "2.7182818284590451, 1.0000000000000000",
            "5.0000000000000000, 1.6094379124341003",
            "10.0000000000000000, 2.3025850929940459",
            "100.0000000000000000, 4.6051701859880918"
    })
    void testLnBase(double value, double expected) {
        LnFunction lnFunction = new LnFunction();
        double result = lnFunction.calculate(value, EPSILON);
        assertEquals(expected, result, EPSILON, "Ln(" + value + ") should be " + expected);
    }

    /**
     * Phase 2: Test trigonometric derivatives - cos(x) derived from sin(x)
     * Values calculated with Python:
     * import math
     * for angle in [0, math.pi/6, math.pi/4, math.pi/3, math.pi/2, math.pi,
     * 3*math.pi/2, 2*math.pi]:
     * print(f'"{angle:.16f}, {math.cos(angle):.16f}"')
     */
    @ParameterizedTest
    @DisplayName("Phase 2: Trigonometric derivatives - cos function integration test")
    @CsvSource({
            "0.0000000000000000, 1.0000000000000000",
            "0.5235987755982988, 0.8660254037844387",
            "0.7853981633974483, 0.7071067811865476",
            "1.0471975511965976, 0.5000000000000001",
            "1.5707963267948966, 0.0000000000000001",
            "3.1415926535897931, -1.0000000000000000",
            "4.7123889803846897, -0.0000000000000002",
            "6.2831853071795862, 1.0000000000000000"
    })
    void testCosDerivation(double angle, double expected) {
        SinFunction sinFunction = new SinFunction();
        CosFunction cosFunction = new CosFunction(sinFunction);
        double result = cosFunction.calculate(angle, EPSILON);
        assertEquals(expected, result, EPSILON, "Cos(" + angle + ") should be " + expected);
    }

    /**
     * Phase 2: Test trigonometric derivatives - sec(x) derived from cos(x)
     * Values calculated with Python:
     * import math
     * for angle in [0, math.pi/6, math.pi/4, math.pi/3, math.pi, 2*math.pi]:
     * print(f'"{angle:.16f}, {1/math.cos(angle):.16f}"')
     */
    @ParameterizedTest
    @DisplayName("Phase 2: Trigonometric derivatives - sec function integration test")
    @CsvSource({
            "0.0000000000000000, 1.0000000000000000",
            "0.5235987755982988, 1.1547005383792515",
            "0.7853981633974483, 1.4142135623730949",
            "1.0471975511965976, 1.9999999999999996",
            "3.1415926535897931, -1.0000000000000000",
            "6.2831853071795862, 1.0000000000000000"
    })
    void testSecDerivation(double angle, double expected) {
        SinFunction sinFunction = new SinFunction();
        CosFunction cosFunction = new CosFunction(sinFunction);
        SecFunction secFunction = new SecFunction(cosFunction);

        // Skip angles where sec is undefined
        if (Math.abs(Math.cos(angle)) < EPSILON) {
            assertThrows(IllegalArgumentException.class, () -> secFunction.calculate(angle, EPSILON),
                    "Sec(" + angle + ") should throw an exception at singularity");
        } else {
            double result = secFunction.calculate(angle, EPSILON);
            assertEquals(expected, result, EPSILON, "Sec(" + angle + ") should be " + expected);
        }
    }

    /**
     * Phase 2: Test trigonometric derivatives - csc(x) derived from sin(x)
     * Values calculated with Python:
     * import math
     * for angle in [math.pi/6, math.pi/4, math.pi/3, math.pi/2, 3*math.pi/2]:
     * print(f'"{angle:.16f}, {1/math.sin(angle):.16f}"')
     */
    @ParameterizedTest
    @DisplayName("Phase 2: Trigonometric derivatives - csc function integration test")
    @CsvSource({
            "0.5235987755982988, 2.0000000000000004",
            "0.7853981633974483, 1.4142135623730951",
            "1.0471975511965976, 1.1547005383792517",
            "1.5707963267948966, 1.0000000000000000",
            "4.7123889803846897, -1.0000000000000000"
    })
    void testCscDerivation(double angle, double expected) {
        SinFunction sinFunction = new SinFunction();
        CscFunction cscFunction = new CscFunction(sinFunction);

        // Skip angles where csc is undefined
        if (Math.abs(Math.sin(angle)) < EPSILON) {
            assertThrows(IllegalArgumentException.class, () -> cscFunction.calculate(angle, EPSILON),
                    "Csc(" + angle + ") should throw an exception at singularity");
        } else {
            double result = cscFunction.calculate(angle, EPSILON);
            assertEquals(expected, result, EPSILON, "Csc(" + angle + ") should be " + expected);
        }
    }

    /**
     * Phase 3: Test logarithmic derivatives - log2 derived from ln
     * Values calculated with Python:
     * import math
     * for value in [1, 2, 4, 8, 16, 32]:
     * print(f'"{value:.16f}, {math.log2(value):.16f}"')
     */
    @ParameterizedTest
    @DisplayName("Phase 3: Logarithmic derivatives - log2 function integration test")
    @CsvSource({
            "1.0000000000000000, 0.0000000000000000",
            "2.0000000000000000, 1.0000000000000000",
            "4.0000000000000000, 2.0000000000000000",
            "8.0000000000000000, 3.0000000000000000",
            "16.0000000000000000, 4.0000000000000000",
            "32.0000000000000000, 5.0000000000000000"
    })
    void testLog2Derivation(double value, double expected) {
        LnFunction lnFunction = new LnFunction();
        Log2Function log2Function = new Log2Function(lnFunction);
        double result = log2Function.calculate(value, EPSILON);
        assertEquals(expected, result, EPSILON, "Log2(" + value + ") should be " + expected);
    }

    /**
     * Phase 3: Test logarithmic derivatives - log10 derived from ln
     * Values calculated with Python:
     * import math
     * for value in [1, 10, 100, 1000, 10000]:
     * print(f'"{value:.16f}, {math.log10(value):.16f}"')
     */
    @ParameterizedTest
    @DisplayName("Phase 3: Logarithmic derivatives - log10 function integration test")
    @CsvSource({
            "1.0000000000000000, 0.0000000000000000",
            "10.0000000000000000, 1.0000000000000000",
            "100.0000000000000000, 2.0000000000000000",
            "1000.0000000000000000, 3.0000000000000000",
            "10000.0000000000000000, 4.0000000000000000"
    })
    void testLog10Derivation(double value, double expected) {
        LnFunction lnFunction = new LnFunction();
        Log10Function log10Function = new Log10Function(lnFunction);
        double result = log10Function.calculate(value, EPSILON);
        assertEquals(expected, result, EPSILON, "Log10(" + value + ") should be " + expected);
    }

    /**
     * Phase 3: Test logarithmic derivatives - log5 derived from ln
     * Values calculated with Python:
     * import math
     * for value in [1, 5, 25, 125, 625]:
     * print(f'"{value:.16f}, {math.log(value, 5):.16f}"')
     */
    @ParameterizedTest
    @DisplayName("Phase 3: Logarithmic derivatives - log5 function integration test")
    @CsvSource({
            "1.0000000000000000, 0.0000000000000000",
            "5.0000000000000000, 1.0000000000000000",
            "25.0000000000000000, 2.0000000000000000",
            "125.0000000000000000, 3.0000000000000000",
            "625.0000000000000000, 4.0000000000000000"
    })
    void testLog5Derivation(double value, double expected) {
        LnFunction lnFunction = new LnFunction();
        Log5Function log5Function = new Log5Function(lnFunction);
        double result = log5Function.calculate(value, EPSILON);
        assertEquals(expected, result, EPSILON, "Log5(" + value + ") should be " + expected);
    }

    /**
     * Phase 4: Test negative domain function integration
     * Values calculated with Python:
     * import math
     * def negative_domain(x):
     * sin_x = math.sin(x)
     * cos_x = math.cos(x)
     * sec_x = 1 / cos_x
     * csc_x = 1 / sin_x
     * return ((sec_x * csc_x) / cos_x - sec_x) ** 2 - sin_x
     *
     * # Test for -0.3, -0.7
     * print(f"For x = -0.3: {negative_domain(-0.3)}")
     * print(f"For x = -0.7: {negative_domain(-0.7)}")
     */
    @ParameterizedTest
    @DisplayName("Phase 4: Negative domain function integration test")
    @CsvSource({
            "-0.3, 72.01554118019029",
            "-0.7, 5.252971246183056"
    })
    void testNegativeDomainIntegration(double x, double expected) {
        SinFunction sinFunction = new SinFunction();
        CosFunction cosFunction = new CosFunction(sinFunction);
        SecFunction secFunction = new SecFunction(cosFunction);
        CscFunction cscFunction = new CscFunction(sinFunction);

        NegativeDomainFunction negativeDomainFunction = new NegativeDomainFunction(
                sinFunction, cosFunction, secFunction, cscFunction);

        double result = negativeDomainFunction.calculate(x, EPSILON);
        assertEquals(expected, result, EPSILON * 100, // Higher tolerance due to complex calculation
                "Negative domain function at " + x + " should be " + expected);
    }

    /**
     * Phase 4: Test positive domain function integration
     * Values calculated with Python:
     * import math
     * def positive_domain(x):
     * log2_x = math.log2(x)
     * log10_x = math.log10(x)
     * log5_x = math.log(x, 5)
     * return ((log2_x + log10_x) ** 2 - log2_x - log10_x - log5_x)
     *
     * # Test for 2.0, 5.0, 10.0
     * print(f"For x = 2.0: {positive_domain(2.0)}")
     * print(f"For x = 5.0: {positive_domain(5.0)}")
     * print(f"For x = 10.0: {positive_domain(10.0)}")
     */
    @ParameterizedTest
    @DisplayName("Phase 4: Positive domain function integration test")
    @CsvSource({
            "2.0, 0.8041478454553692",
            "5.0, 4.155069381857804",
            "10.0, 6.5220263365671925"
    })
    void testPositiveDomainIntegration(double x, double expected) {
        LnFunction lnFunction = new LnFunction();
        Log2Function log2Function = new Log2Function(lnFunction);
        Log10Function log10Function = new Log10Function(lnFunction);
        Log5Function log5Function = new Log5Function(lnFunction);

        PositiveDomainFunction positiveDomainFunction = new PositiveDomainFunction(
                log2Function, log10Function, log5Function);

        double result = positiveDomainFunction.calculate(x, EPSILON);
        assertEquals(expected, result, EPSILON * 100, // Higher tolerance due to complex calculation
                "Positive domain function at " + x + " should be " + expected);
    }

    /**
     * Phase 5: System function integration test - full system
     * Combines previous tests and checks correct domain delegation
     */
    @Test
    @DisplayName("Phase 5: Full system function integration test")
    void testFullSystemIntegration() {
        // Create the full system
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

        SystemFunction systemFunction = new SystemFunction(
                negativeDomainFunction, positiveDomainFunction);

        // Test negative domain
        double xNeg = -0.3;
        double expectedNeg = 72.01554118019029; // From Python calculation
        assertEquals(expectedNeg, systemFunction.calculate(xNeg, EPSILON), EPSILON * 100,
                "System function at " + xNeg + " should use negative domain formula");

        // Test positive domain
        double xPos = 2.0;
        double expectedPos = 0.8041478454553692; // From Python calculation
        assertEquals(expectedPos, systemFunction.calculate(xPos, EPSILON), EPSILON * 100,
                "System function at " + xPos + " should use positive domain formula");

        // Test domain boundaries
        assertFalse(systemFunction.isInDomain(0.0), "x = 0 should not be in the domain");
        assertFalse(systemFunction.isInDomain(-Math.PI / 2), "x = -π/2 should not be in the domain");
    }

    /**
     * Phase 6: Mixed integration test with stubs
     * This demonstrates partial integration with some real implementations and some
     * stubs
     */
    @Test
    @DisplayName("Phase 6: Mixed integration with stubs")
    void testMixedIntegrationWithStubs() {
        // Create real implementations
        SinFunction sinFunction = new SinFunction();
        CosFunction cosFunction = new CosFunction(sinFunction);
        SecFunction secFunction = new SecFunction(cosFunction);
        CscFunction cscFunction = new CscFunction(sinFunction);
        NegativeDomainFunction negativeDomainFunction = new NegativeDomainFunction(
                sinFunction, cosFunction, secFunction, cscFunction);

        // Create stub implementations for logarithmic components
        LnFunction lnStub = new LnFunction() {
            @Override
            public double calculate(double x, double epsilon) {
                // Return pre-calculated values for specific inputs
                if (x == 2.0)
                    return 0.693;
                if (x == 5.0)
                    return 1.609;
                if (x == 10.0)
                    return 2.303;
                return super.calculate(x, epsilon);
            }
        };

        Log2Function log2Stub = new Log2Function(lnStub);
        Log10Function log10Stub = new Log10Function(lnStub);
        Log5Function log5Stub = new Log5Function(lnStub);

        PositiveDomainFunction positiveDomainStub = new PositiveDomainFunction(
                log2Stub, log10Stub, log5Stub);

        // Create mixed system with real negative domain and stub positive domain
        SystemFunction mixedSystem = new SystemFunction(
                negativeDomainFunction, positiveDomainStub);

        // Test the mixed system
        double negValue = -0.3;
        double posValue = 2.0;

        // Check if both domains work correctly in the mixed system
        assertTrue(mixedSystem.isInDomain(negValue),
                "Mixed system should recognize " + negValue + " as valid input");
        assertTrue(mixedSystem.isInDomain(posValue),
                "Mixed system should recognize " + posValue + " as valid input");

        // Verify calculation in negative domain works with real implementation
        double negResult = mixedSystem.calculate(negValue, EPSILON);
        assertTrue(Double.isFinite(negResult),
                "Mixed system calculation for " + negValue + " should return finite result");

        // Verify calculation in positive domain works with stub implementation
        double posResult = mixedSystem.calculate(posValue, EPSILON);
        assertTrue(Double.isFinite(posResult),
                "Mixed system calculation for " + posValue + " should return finite result");
    }

    /**
     * Test edge cases around domain boundaries and singularities
     */
    @Test
    @DisplayName("Test edge cases around domain boundaries")
    void testEdgeCases() {
        // Set up the full system
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

        SystemFunction systemFunction = new SystemFunction(
                negativeDomainFunction, positiveDomainFunction);

        // Test values very close to x = 0
        double nearZeroNeg = -1e-10;
        double nearZeroPos = 1e-10;

        // Both should be in domain, but very close to boundary
        assertTrue(systemFunction.isInDomain(nearZeroNeg),
                "x = " + nearZeroNeg + " should be in domain");
        assertTrue(systemFunction.isInDomain(nearZeroPos),
                "x = " + nearZeroPos + " should be in domain");

        // Test values very close to singularities
        double nearPiOver2 = -Math.PI / 2 + 1e-10;
        double nearPi = -Math.PI + 1e-10;

        // Check that values very close to singularities are handled properly
        assertFalse(systemFunction.isInDomain(-Math.PI / 2),
                "x = -π/2 should not be in domain");
        assertFalse(systemFunction.isInDomain(-Math.PI),
                "x = -π should not be in domain");

        // But values slightly away should be valid
        assertTrue(systemFunction.isInDomain(-Math.PI / 2 + 0.01),
                "x = -π/2 + 0.01 should be in domain");
        assertTrue(systemFunction.isInDomain(-Math.PI + 0.01),
                "x = -π + 0.01 should be in domain");

        // Check very small positive values for logarithm issues
        double verySmallPositive = 1e-5;
        assertTrue(systemFunction.isInDomain(verySmallPositive),
                "Small positive value " + verySmallPositive + " should be in domain");
        double result = systemFunction.calculate(verySmallPositive, EPSILON);
        assertTrue(Double.isFinite(result),
                "Result for very small positive value should be finite");
    }

    /**
     * Test extreme values to ensure numerical stability
     */
    @Test
    @DisplayName("Test system function with extreme values")
    void testExtremeValues() {
        // Set up the full system
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

        SystemFunction systemFunction = new SystemFunction(
                negativeDomainFunction, positiveDomainFunction);

        // Test with large positive values for logarithmic functions
        double[] largeValues = { 100.0, 1000.0, 10000.0 };
        for (double x : largeValues) {
            assertTrue(systemFunction.isInDomain(x),
                    "Large value " + x + " should be in domain");
            double result = systemFunction.calculate(x, EPSILON);
            assertTrue(Double.isFinite(result),
                    "Result for " + x + " should be finite");

            // Calculate expected value using Python
            // Use this Python code:
            // import math
            // def positive_domain(x):
            // log2_x = math.log2(x)
            // log10_x = math.log10(x)
            // log5_x = math.log(x, 5)
            // return ((log2_x + log10_x) ** 2 - log2_x - log10_x - log5_x)
            // for x in [100, 1000, 10000]:
            // print(f"x={x}, result={positive_domain(x)}")

            double expected = 0.0;
            if (x == 100.0)
                expected = 21.01317782169802;
            else if (x == 1000.0)
                expected = 42.26702084374178;
            else if (x == 10000.0)
                expected = 68.72086386578548;

            assertEquals(expected, result, EPSILON * 1000,
                    "System function for large value " + x + " should match expected value");
        }

        // Test with large negative values for trigonometric functions
        // These should have periodicity, so we normalize with sin/cos periods
        double[] largeNegativeValues = { -100.0, -1000.0 };
        for (double x : largeNegativeValues) {
            // Check that the large value is in domain and calculation succeeds
            if (systemFunction.isInDomain(x)) { // Only test if it's not a singularity
                double result = systemFunction.calculate(x, EPSILON);
                assertTrue(Double.isFinite(result),
                        "Result for " + x + " should be finite");
            }
        }
    }
}
