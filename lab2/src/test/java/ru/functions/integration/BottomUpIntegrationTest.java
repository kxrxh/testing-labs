package ru.functions.integration;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;
import org.mockito.Mockito;
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

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

/**
 * Bottom-up integration test for the system function.
 * Tests components in order of dependency: base functions first,
 * then derived functions, and finally the full system.
 */
public class BottomUpIntegrationTest {

    private static final double EPSILON = 1e-6;

    /**
     * Phase 1: Test base sin(x) function
     * Using the real implementation of SinFunction
     */
    @ParameterizedTest
    @DisplayName("Phase 1: Base sin function integration test")
    @CsvFileSource(resources = "/sin_test_data.csv", numLinesToSkip = 1)
    void testSinBase(double angle, double expected) {

        SinFunction sinFunction = new SinFunction();
        double result = sinFunction.calculate(angle, EPSILON);
        assertEquals(expected, result, EPSILON, "Sin(" + angle + ") should be " + expected);
    }

    /**
     * Phase 1: Test base ln(x) function
     * Using the real implementation of LnFunction
     */
    @ParameterizedTest
    @DisplayName("Phase 1: Base ln function integration test")
    @CsvFileSource(resources = "/ln_test_data.csv", numLinesToSkip = 1)
    void testLnBase(double value, double expected) {

        LnFunction lnFunction = new LnFunction();
        double result = lnFunction.calculate(value, EPSILON);
        assertEquals(expected, result, EPSILON, "Ln(" + value + ") should be " + expected);
    }

    /**
     * Phase 2: Test trigonometric derivatives - cos(x) integrated with sin(x)
     */
    @ParameterizedTest
    @DisplayName("Phase 2: Trigonometric derivatives - cos function integration test")
    @CsvFileSource(resources = "/cos_test_data.csv", numLinesToSkip = 1)
    void testCosDerivation(double angle, double expected) {

        SinFunction sinFunction = new SinFunction();
        CosFunction cosFunction = new CosFunction(sinFunction);

        double result = cosFunction.calculate(angle, EPSILON);
        assertEquals(expected, result, EPSILON, "Cos(" + angle + ") should be " + expected);
    }

    /**
     * Phase 2: Test trigonometric derivatives - sec(x) integrated with cos(x) and
     * sin(x)
     */
    @ParameterizedTest
    @DisplayName("Phase 2: Trigonometric derivatives - sec function integration test")
    @CsvFileSource(resources = "/sec_test_data.csv", numLinesToSkip = 1)
    void testSecDerivation(double angle, double expected) {

        SinFunction sinFunction = new SinFunction();
        CosFunction cosFunction = new CosFunction(sinFunction);
        SecFunction secFunction = new SecFunction(cosFunction);

        if (Math.abs(Math.cos(angle)) < EPSILON) {
            assertThrows(IllegalArgumentException.class, () -> secFunction.calculate(angle, EPSILON),
                    "Sec(" + angle + ") should throw an exception at singularity");
        } else {
            double result = secFunction.calculate(angle, EPSILON);
            assertEquals(expected, result, EPSILON, "Sec(" + angle + ") should be " + expected);
        }
    }

    /**
     * Phase 2: Test trigonometric derivatives - csc(x) integrated with sin(x)
     */
    @ParameterizedTest
    @DisplayName("Phase 2: Trigonometric derivatives - csc function integration test")
    @CsvFileSource(resources = "/csc_test_data.csv", numLinesToSkip = 1)
    void testCscDerivation(double angle, double expected) {

        SinFunction sinFunction = new SinFunction();
        CscFunction cscFunction = new CscFunction(sinFunction);

        if (Math.abs(Math.sin(angle)) < EPSILON) {
            assertThrows(IllegalArgumentException.class, () -> cscFunction.calculate(angle, EPSILON),
                    "Csc(" + angle + ") should throw an exception at singularity");
        } else {
            double result = cscFunction.calculate(angle, EPSILON);
            assertEquals(expected, result, EPSILON, "Csc(" + angle + ") should be " + expected);
        }
    }

    /**
     * Phase 3: Test logarithmic derivatives - log2 integrated with ln
     */
    @ParameterizedTest
    @DisplayName("Phase 3: Logarithmic derivatives - log2 function integration test")
    @CsvFileSource(resources = "/log2_test_data.csv", numLinesToSkip = 1)
    void testLog2Derivation(double value, double expected) {

        LnFunction lnFunction = new LnFunction();
        Log2Function log2Function = new Log2Function(lnFunction);

        double result = log2Function.calculate(value, EPSILON);
        assertEquals(expected, result, EPSILON, "Log2(" + value + ") should be " + expected);
    }

    /**
     * Phase 3: Test logarithmic derivatives - log10 integrated with ln
     */
    @ParameterizedTest
    @DisplayName("Phase 3: Logarithmic derivatives - log10 function integration test")
    @CsvFileSource(resources = "/log10_test_data.csv", numLinesToSkip = 1)
    void testLog10Derivation(double value, double expected) {

        LnFunction lnFunction = new LnFunction();
        Log10Function log10Function = new Log10Function(lnFunction);

        double result = log10Function.calculate(value, EPSILON);
        assertEquals(expected, result, EPSILON, "Log10(" + value + ") should be " + expected);
    }

    /**
     * Phase 3: Test logarithmic derivatives - log5 integrated with ln
     */
    @ParameterizedTest
    @DisplayName("Phase 3: Logarithmic derivatives - log5 function integration test")
    @CsvFileSource(resources = "/log5_test_data.csv", numLinesToSkip = 1)
    void testLog5Derivation(double value, double expected) {

        LnFunction lnFunction = new LnFunction();
        Log5Function log5Function = new Log5Function(lnFunction);

        double result = log5Function.calculate(value, EPSILON);
        assertEquals(expected, result, EPSILON, "Log5(" + value + ") should be " + expected);
    }

    /**
     * Phase 4: Test negative domain function integration with all trig functions
     */
    @ParameterizedTest
    @DisplayName("Phase 4: Negative domain function integration test")
    @CsvFileSource(resources = "/negative_domain_test_cases.csv", numLinesToSkip = 1)
    void testNegativeDomainIntegration(double x, double sin, double cos, double sec, double csc, double expected) {

        if (x >= 0 || Math.abs(Math.cos(x)) < EPSILON || Math.abs(Math.sin(x)) < EPSILON) {
            return;
        }

        SinFunction sinMock = Mockito.mock(SinFunction.class);
        CosFunction cosMock = Mockito.mock(CosFunction.class);
        SecFunction secMock = Mockito.mock(SecFunction.class);
        CscFunction cscMock = Mockito.mock(CscFunction.class);

        when(sinMock.calculate(eq(x), anyDouble())).thenReturn(sin);
        when(cosMock.calculate(eq(x), anyDouble())).thenReturn(cos);
        when(secMock.calculate(eq(x), anyDouble())).thenReturn(sec);
        when(cscMock.calculate(eq(x), anyDouble())).thenReturn(csc);

        when(sinMock.isInDomain(eq(x))).thenReturn(true);
        when(cosMock.isInDomain(eq(x))).thenReturn(true);
        when(secMock.isInDomain(eq(x))).thenReturn(true);
        when(cscMock.isInDomain(eq(x))).thenReturn(true);

        NegativeDomainFunction negativeDomainFunction = new NegativeDomainFunction(
                sinMock, cosMock, secMock, cscMock);

        double secTimesCsc = sec * csc;
        double secTimesCscDividedByCos = secTimesCsc / cos;
        double secTimesCscDividedByCosMinusSec = secTimesCscDividedByCos - sec;
        double squared = secTimesCscDividedByCosMinusSec * secTimesCscDividedByCosMinusSec;
        double calculatedExpected = squared - sin;

        double result = negativeDomainFunction.calculate(x, EPSILON);
        assertEquals(calculatedExpected, result, EPSILON,
                "Negative domain function at " + x + " should be " + calculatedExpected);
    }

    /**
     * Phase 4: Test positive domain function integration with all log functions
     */
    @ParameterizedTest
    @DisplayName("Phase 4: Positive domain function integration test")
    @CsvFileSource(resources = "/positive_domain_test_cases.csv", numLinesToSkip = 1)
    void testPositiveDomainIntegration(double x, double log2, double log10, double log5, double expected) {

        if (x <= 0) {
            return;
        }

        Log2Function log2Mock = Mockito.mock(Log2Function.class);
        Log10Function log10Mock = Mockito.mock(Log10Function.class);
        Log5Function log5Mock = Mockito.mock(Log5Function.class);

        when(log2Mock.calculate(eq(x), anyDouble())).thenReturn(log2);
        when(log10Mock.calculate(eq(x), anyDouble())).thenReturn(log10);
        when(log5Mock.calculate(eq(x), anyDouble())).thenReturn(log5);

        when(log2Mock.isInDomain(eq(x))).thenReturn(true);
        when(log10Mock.isInDomain(eq(x))).thenReturn(true);
        when(log5Mock.isInDomain(eq(x))).thenReturn(true);

        PositiveDomainFunction positiveDomainFunction = new PositiveDomainFunction(
                log2Mock, log10Mock, log5Mock);

        double log2PlusLog10 = log2 + log10;
        double squared = log2PlusLog10 * log2PlusLog10;
        double squaredMinusLog2 = squared - log2;
        double squaredMinusLog2MinusLog10 = squaredMinusLog2 - log10;
        double calculatedExpected = squaredMinusLog2MinusLog10 - log5;

        double result = positiveDomainFunction.calculate(x, EPSILON);
        assertEquals(calculatedExpected, result, EPSILON,
                "Positive domain function at " + x + " should be " + calculatedExpected);
    }

    /**
     * Phase 5: System function integration test - full system
     * Using real implementations for all components
     */
    @Test
    @DisplayName("Phase 5: Full system function integration test")
    void testFullSystemIntegration() {

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

        double xNeg = -0.3;
        double resultNeg = systemFunction.calculate(xNeg, EPSILON);
        assertTrue(Double.isFinite(resultNeg), "Result should be finite for x = " + xNeg);

        double xPos = 2.0;
        double resultPos = systemFunction.calculate(xPos, EPSILON);
        assertTrue(Double.isFinite(resultPos), "Result should be finite for x = " + xPos);

        assertFalse(systemFunction.isInDomain(0.0), "x = 0 should not be in the domain");
        assertFalse(systemFunction.isInDomain(-Math.PI / 2), "x = -π/2 should not be in the domain");

        double nearZeroNeg = -1e-10;
        double nearZeroPos = 1e-10;

        assertTrue(systemFunction.isInDomain(nearZeroNeg),
                "x = " + nearZeroNeg + " should be in the domain");
        assertTrue(systemFunction.isInDomain(nearZeroPos),
                "x = " + nearZeroPos + " should be in the domain");

        double nearPiOver2 = -Math.PI / 2 + 1e-10;
        double nearPi = -Math.PI + 1e-10;

        assertFalse(systemFunction.isInDomain(-Math.PI / 2),
                "x = -π/2 should not be in the domain");
        assertFalse(systemFunction.isInDomain(-Math.PI),
                "x = -π should not be in the domain");

        assertTrue(systemFunction.isInDomain(nearPiOver2),
                "x = " + nearPiOver2 + " should be in the domain");
        assertTrue(systemFunction.isInDomain(nearPi),
                "x = " + nearPi + " should be in the domain");

        double verySmallPositive = 1e-5;
        assertTrue(systemFunction.isInDomain(verySmallPositive),
                "Small positive value " + verySmallPositive + " should be in the domain");
        double result = systemFunction.calculate(verySmallPositive, EPSILON);
        assertTrue(Double.isFinite(result),
                "Result for very small positive value should be finite");
    }

    /**
     * Phase 6: Mixed integration test with partial mocking
     * This demonstrates partial integration with some real implementations and some
     * mocks
     */
    @Test
    @DisplayName("Phase 6: Mixed integration with partial mocking")
    void testMixedIntegrationWithPartialMocks() throws IOException {

        SinFunction sinFunction = new SinFunction();
        CosFunction cosFunction = new CosFunction(sinFunction);
        SecFunction secFunction = new SecFunction(cosFunction);
        CscFunction cscFunction = new CscFunction(sinFunction);

        NegativeDomainFunction negativeDomainFunction = new NegativeDomainFunction(
                sinFunction, cosFunction, secFunction, cscFunction);

        LnFunction lnMock = Mockito.mock(LnFunction.class);

        String resourcePath = "/positive_domain_test_cases.csv";
        java.net.URL resourceUrl = getClass().getResource(resourcePath);
        if (resourceUrl == null) {
            throw new IOException("Could not find resource: " + resourcePath);
        }

        Path testData = Paths.get(resourceUrl.getPath());
        List<String> lines = Files.readAllLines(testData);
        for (int i = 1; i < lines.size(); i++) {
            String line = lines.get(i);
            if (line.trim().isEmpty())
                continue;

            String[] parts = line.split(",");
            if (parts.length >= 5) {
                try {
                    double x = Double.parseDouble(parts[0]);

                    when(lnMock.calculate(eq(x), anyDouble())).thenReturn(Math.log(x));
                    when(lnMock.isInDomain(eq(x))).thenReturn(true);
                } catch (NumberFormatException e) {

                    System.err.println("Skipping malformed data line: " + line);
                }
            }
        }

        Log2Function log2Function = new Log2Function(lnMock);
        Log10Function log10Function = new Log10Function(lnMock);
        Log5Function log5Function = new Log5Function(lnMock);

        PositiveDomainFunction positiveDomainFunction = new PositiveDomainFunction(
                log2Function, log10Function, log5Function);

        SystemFunction mixedSystem = new SystemFunction(
                negativeDomainFunction, positiveDomainFunction);

        double negValue = -0.3;
        double posValue = 2.0;

        if (mixedSystem.isInDomain(negValue)) {
            double negResult = mixedSystem.calculate(negValue, EPSILON);
            assertTrue(Double.isFinite(negResult),
                    "Mixed system calculation for " + negValue + " should return finite result");
        }

        if (mixedSystem.isInDomain(posValue)) {
            double posResult = mixedSystem.calculate(posValue, EPSILON);
            assertTrue(Double.isFinite(posResult),
                    "Mixed system calculation for " + posValue + " should return finite result");
        }
    }

    /**
     * Test edge cases around domain boundaries and singularities
     */
    @Test
    @DisplayName("Test edge cases around domain boundaries")
    void testEdgeCases() {

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

        double nearZeroNeg = -1e-10;
        double nearZeroPos = 1e-10;

        assertTrue(systemFunction.isInDomain(nearZeroNeg),
                "x = " + nearZeroNeg + " should be in the domain");
        assertTrue(systemFunction.isInDomain(nearZeroPos),
                "x = " + nearZeroPos + " should be in the domain");

        double nearPiOver2 = -Math.PI / 2 + 1e-10;
        double nearPi = -Math.PI + 1e-10;

        assertFalse(systemFunction.isInDomain(-Math.PI / 2),
                "x = -π/2 should not be in the domain");
        assertFalse(systemFunction.isInDomain(-Math.PI),
                "x = -π should not be in the domain");

        assertTrue(systemFunction.isInDomain(nearPiOver2),
                "x = " + nearPiOver2 + " should be in the domain");
        assertTrue(systemFunction.isInDomain(nearPi),
                "x = " + nearPi + " should be in the domain");

        double verySmallPositive = 1e-5;
        assertTrue(systemFunction.isInDomain(verySmallPositive),
                "Small positive value " + verySmallPositive + " should be in the domain");
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

        double largePositive = 100.0;
        double result = systemFunction.calculate(largePositive, EPSILON);
        assertTrue(Double.isFinite(result),
                "System function for large value " + largePositive + " should return finite result");

        double[] testValues = { -Math.PI / 2 + 0.01, -Math.PI + 0.01, -3 * Math.PI / 2 + 0.01 };
        for (double x : testValues) {
            assertTrue(systemFunction.isInDomain(x),
                    "x = " + x + " should be in the domain");
            double res = systemFunction.calculate(x, EPSILON);
            assertTrue(Double.isFinite(res),
                    "Result for x = " + x + " should be finite");
        }
    }
}
