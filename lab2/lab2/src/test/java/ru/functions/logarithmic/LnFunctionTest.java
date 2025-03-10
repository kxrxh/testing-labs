package ru.functions.logarithmic;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.*;

class LnFunctionTest {
    private static final double EPSILON = 1e-6;
    private LnFunction lnFunction;

    @BeforeEach
    void setUp() {
        lnFunction = new LnFunction();
    }

    @ParameterizedTest
    @DisplayName("Ln function should calculate correctly for standard values")
    @CsvSource({
            "1.0, 0.0",
            "2.0, 0.6931471805599453",
            "2.718281828459045, 1.0", // e
            "5.0, 1.6094379124341003",
            "10.0, 2.302585092994046",
            "100.0, 4.605170185988092"
    })
    void testLnForStandardValues(double x, double expected) {
        assertEquals(expected, lnFunction.calculate(x, EPSILON), EPSILON,
                "Ln(" + x + ") should be " + expected);
    }

    @Test
    @DisplayName("Ln function should handle values close to 1 correctly")
    void testLnForValuesCloseToOne() {
        double[] testValues = { 0.9, 0.99, 0.999, 0.9999, 1.0, 1.0001, 1.001, 1.01, 1.1 };

        for (double x : testValues) {
            double result = lnFunction.calculate(x, EPSILON);
            // For x close to 1, ln(x) ≈ x - 1
            double approximation = x - 1;

            if (Math.abs(x - 1.0) < 0.01) {
                // For very close values, the approximation should be quite accurate
                assertEquals(approximation, result, 0.01,
                        "Ln(" + x + ") should be close to " + approximation);
            } else {
                // Just verify the result is finite and has correct sign
                assertTrue(Double.isFinite(result), "Ln(" + x + ") should be finite");
                assertEquals(Math.signum(x - 1), Math.signum(result), "Ln(" + x + ") should have correct sign");
            }
        }
    }

    @Test
    @DisplayName("Ln function should throw exception for non-positive values")
    void testLnForNonPositiveValues() {
        double[] testValues = { 0.0, -0.1, -1.0, -10.0, Double.NEGATIVE_INFINITY };

        for (double x : testValues) {
            Exception exception = assertThrows(IllegalArgumentException.class,
                    () -> lnFunction.calculate(x, EPSILON),
                    "Ln should throw exception for " + x);

            assertTrue(exception.getMessage().contains("outside the domain"),
                    "Exception message should mention domain issue");
        }
    }

    @Test
    @DisplayName("Ln function should handle very large values")
    void testLnForLargeValues() {
        double[] testValues = { 1e10, 1e20, 1e50, 1e100, Double.MAX_VALUE };

        for (double x : testValues) {
            double result = lnFunction.calculate(x, EPSILON);

            assertTrue(Double.isFinite(result), "Ln(" + x + ") should be finite");
            assertTrue(result > 0, "Ln(" + x + ") should be positive");

            // Very rough check: ln(10^n) ≈ n * ln(10) ≈ n * 2.3
            double expectedOrder = Math.log10(x) * 2.3;
            assertTrue(Math.abs(result - expectedOrder) < expectedOrder * 0.1,
                    "Ln(" + x + ") should be roughly " + expectedOrder);
        }
    }

    @Test
    @DisplayName("Ln function should satisfy the logarithm identity ln(a*b) = ln(a) + ln(b)")
    void testLnIdentity() {
        double[] aValues = { 2.0, 3.0, 5.0, 10.0 };
        double[] bValues = { 1.5, 2.5, 4.0, 7.0 };

        for (double a : aValues) {
            for (double b : bValues) {
                double lnA = lnFunction.calculate(a, EPSILON);
                double lnB = lnFunction.calculate(b, EPSILON);
                double lnAB = lnFunction.calculate(a * b, EPSILON);

                assertEquals(lnA + lnB, lnAB, EPSILON * 10,
                        "Ln(" + a + " * " + b + ") should equal Ln(" + a + ") + Ln(" + b + ")");
            }
        }
    }

    @Test
    @DisplayName("Ln function should have correct domain")
    void testLnDomain() {
        double[] validValues = { 0.1, 1.0, 10.0, 100.0 };
        double[] invalidValues = { 0.0, -0.1, -1.0, -10.0 };

        for (double x : validValues) {
            assertTrue(lnFunction.isInDomain(x), "Ln should be defined for " + x);
        }

        for (double x : invalidValues) {
            assertFalse(lnFunction.isInDomain(x), "Ln should not be defined for " + x);
        }
    }
}
