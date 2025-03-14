package ru.functions.integration;

import org.junit.jupiter.api.BeforeEach;
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

public class SystemFunctionIntegrationTest {

        private SystemFunction systemFunction;
        private NegativeDomainFunction negativeDomainFunction;
        private PositiveDomainFunction positiveDomainFunction;
        private static final double EPSILON = 1e-6;
        private static final double HIGH_TOLERANCE = 1e-3;

        @BeforeEach
        void setUp() throws IOException {

                NegativeDomainFunction mockNegativeDomain = Mockito.mock(NegativeDomainFunction.class);
                PositiveDomainFunction mockPositiveDomain = Mockito.mock(PositiveDomainFunction.class);

                configureSystemMocks(mockNegativeDomain, mockPositiveDomain);

                systemFunction = new SystemFunction(mockNegativeDomain, mockPositiveDomain);

                initializeRealImplementations();
        }

        private void configureSystemMocks(NegativeDomainFunction negMock, PositiveDomainFunction posMock)
                        throws IOException {
                String resourcePath = "/system_function_test_cases.csv";
                Path testDataPath = Paths.get(getClass().getResource(resourcePath).getPath());
                List<String> lines = Files.readAllLines(testDataPath);

                for (int i = 1; i < lines.size(); i++) {
                        String line = lines.get(i);
                        if (line.trim().isEmpty())
                                continue;

                        String[] parts = line.split(",");
                        double x = Double.parseDouble(parts[0]);
                        double expected = Double.parseDouble(parts[1]);

                        if (x < 0) {

                                when(negMock.isInDomain(eq(x))).thenReturn(true);
                                when(negMock.calculate(eq(x), anyDouble())).thenReturn(expected);

                                when(posMock.isInDomain(eq(x))).thenReturn(false);
                        } else {

                                when(posMock.isInDomain(eq(x))).thenReturn(true);
                                when(posMock.calculate(eq(x), anyDouble())).thenReturn(expected);

                                when(negMock.isInDomain(eq(x))).thenReturn(false);
                        }
                }

                when(negMock.isInDomain(0.0)).thenReturn(false);
                when(posMock.isInDomain(0.0)).thenReturn(false);

                when(negMock.isInDomain(eq(-Math.PI / 2))).thenReturn(false);

                when(negMock.isInDomain(eq(-Math.PI))).thenReturn(false);

                when(negMock.isInDomain(eq(-0.3))).thenReturn(true);
                when(negMock.isInDomain(eq(-0.7))).thenReturn(true);
                when(negMock.isInDomain(eq(-0.0001))).thenReturn(true);
                when(posMock.isInDomain(eq(0.0001))).thenReturn(true);
                when(posMock.isInDomain(eq(1.0))).thenReturn(true);
                when(posMock.isInDomain(eq(10.0))).thenReturn(true);
        }

        private void initializeRealImplementations() {

                SinFunction sinFunction = new SinFunction();
                CosFunction cosFunction = new CosFunction(sinFunction);
                SecFunction secFunction = new SecFunction(cosFunction);
                CscFunction cscFunction = new CscFunction(sinFunction);

                LnFunction lnFunction = new LnFunction();
                Log2Function log2Function = new Log2Function(lnFunction);
                Log10Function log10Function = new Log10Function(lnFunction);
                Log5Function log5Function = new Log5Function(lnFunction);

                negativeDomainFunction = new NegativeDomainFunction(
                                sinFunction, cosFunction, secFunction, cscFunction);

                positiveDomainFunction = new PositiveDomainFunction(
                                log2Function, log10Function, log5Function);
        }

        @Test
        @DisplayName("Phase 1: Test that the system function is in domain for valid inputs with mocked dependencies")
        void testIsInDomain() {
                assertTrue(systemFunction.isInDomain(-0.3));
                assertTrue(systemFunction.isInDomain(-0.7));
                assertTrue(systemFunction.isInDomain(1.0));
                assertTrue(systemFunction.isInDomain(10.0));
                assertFalse(systemFunction.isInDomain(0.0));
                assertFalse(systemFunction.isInDomain(-Math.PI / 2));
                assertFalse(systemFunction.isInDomain(-Math.PI));
        }

        @ParameterizedTest(name = "System Function at x={0} = {1}")
        @DisplayName("Phase 1: Test that the system function calculates correctly with mocked dependencies")
        @CsvFileSource(resources = "/system_function_test_cases.csv", numLinesToSkip = 1)
        void testCalculate(double x, double expected) {
                if (systemFunction.isInDomain(x)) {
                        double result = systemFunction.calculate(x, EPSILON);
                        assertEquals(expected, result, HIGH_TOLERANCE,
                                        "System function at x = " + x + " should be " + expected);
                } else {
                        assertThrows(IllegalArgumentException.class, () -> {
                                systemFunction.calculate(x, EPSILON);
                        });
                }
        }

        @Test
        @DisplayName("Phase 2: Test negative domain calculation with actual implementation")
        void testNegativeDomainCalculation() {
                double x = -0.3;

                PositiveDomainFunction mockPositiveDomain = Mockito.mock(PositiveDomainFunction.class);
                SystemFunction partialSystem = new SystemFunction(negativeDomainFunction, mockPositiveDomain);

                assertTrue(partialSystem.isInDomain(x), "System function should be defined at x = " + x);

                double result = partialSystem.calculate(x, EPSILON);

                assertTrue(Double.isFinite(result), "Result should be finite for x = " + x);
        }

        @Test
        @DisplayName("Phase 2: Test positive domain calculation with actual implementation")
        void testPositiveDomainCalculation() {
                double x = 2.0;

                NegativeDomainFunction mockNegativeDomain = Mockito.mock(NegativeDomainFunction.class);
                SystemFunction partialSystem = new SystemFunction(mockNegativeDomain, positiveDomainFunction);

                when(mockNegativeDomain.isInDomain(x)).thenReturn(false);

                assertTrue(partialSystem.isInDomain(x), "System function should be defined at x = " + x);

                double result = partialSystem.calculate(x, EPSILON);

                assertTrue(Double.isFinite(result), "Result should be finite for x = " + x);
        }

        @Test
        @DisplayName("Phase 3: Test system function with fully integrated implementations")
        void testSystemFunctionAtBoundaries() {

                SystemFunction fullSystem = new SystemFunction(negativeDomainFunction, positiveDomainFunction);

                double negativeClose = -0.0001;
                double positiveClose = 0.0001;

                assertTrue(fullSystem.isInDomain(negativeClose),
                                "x = " + negativeClose + " should be in domain");
                assertTrue(fullSystem.isInDomain(positiveClose),
                                "x = " + positiveClose + " should be in domain");

                double negativeResult = fullSystem.calculate(negativeClose, EPSILON);
                double positiveResult = fullSystem.calculate(positiveClose, EPSILON);

                assertTrue(Double.isFinite(negativeResult),
                                "Result for x = " + negativeClose + " should be finite");
                assertTrue(Double.isFinite(positiveResult),
                                "Result for x = " + positiveClose + " should be finite");

                double[] testValues = { -0.5, -0.1, 0.1, 0.5, 1.0, 2.0, 5.0 };
                for (double value : testValues) {
                        if (fullSystem.isInDomain(value)) {
                                double result = fullSystem.calculate(value, EPSILON);
                                assertTrue(Double.isFinite(result),
                                                "Result for x = " + value + " should be finite");
                        }
                }
        }
}
