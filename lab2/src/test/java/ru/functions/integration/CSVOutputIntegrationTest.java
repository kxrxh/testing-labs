package ru.functions.integration;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import ru.functions.logarithmic.ln.LnFunction;
import ru.functions.logarithmic.log10.Log10Function;
import ru.functions.logarithmic.log2.Log2Function;
import ru.functions.logarithmic.log5.Log5Function;
import ru.functions.output.CSVWriter;
import ru.functions.system.NegativeDomainFunction;
import ru.functions.system.PositiveDomainFunction;
import ru.functions.system.SystemFunction;
import ru.functions.system.SystemFunctionInterface;
import ru.functions.trigonometric.cos.CosFunction;
import ru.functions.trigonometric.csc.CscFunction;
import ru.functions.trigonometric.sec.SecFunction;
import ru.functions.trigonometric.sin.SinFunction;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration test for CSV output functionality with the system function.
 * Tests if values calculated by the system function are correctly written to
 * CSV files.
 */
@DisplayName("CSV Output Integration Test")
class CSVOutputIntegrationTest {
    private static final double EPSILON = 1e-6;
    private static final String CSV_SEPARATOR = ",";

    private SystemFunctionInterface systemFunction;
    private CSVWriter csvWriter;

    @TempDir
    Path tempDir;

    @BeforeEach
    void setUp() {
        // Set up the complete system function with real implementations
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

        systemFunction = new SystemFunction(negativeDomainFunction, positiveDomainFunction);

        // Initialize CSV writer with the system function
        csvWriter = new CSVWriter(CSV_SEPARATOR);
    }

    @AfterEach
    void tearDown() {
        // Clean up if needed
    }

    @Test
    @DisplayName("CSV writer should correctly write system function values for negative domain")
    void testCSVWriterForNegativeDomain() throws IOException {
        // Set up test parameters
        double start = -2.0;
        double end = -0.1;
        double step = 0.1;

        // Create output file in temp directory
        File outputFile = new File(tempDir.toFile(), "negative_domain_output.csv");

        // Write values to CSV
        csvWriter.writeFunction(systemFunction, start, end, step, outputFile.getAbsolutePath());

        // Verify file exists
        assertTrue(outputFile.exists(), "CSV file should be created");

        // Read and verify file contents
        List<String> lines = readCSVFile(outputFile);

        // Check number of lines (should match the range divided by step plus header)
        int expectedLineCount = (int) ((end - start) / step) + 1 + 1; // +1 for header
        assertEquals(expectedLineCount, lines.size(), "CSV should have correct number of lines");

        // Verify header
        assertTrue(lines.get(0).contains("X") && lines.get(0).contains("F(X)"),
                "CSV should have a proper header");

        // Verify some values
        for (int i = 1; i < lines.size(); i++) {
            String[] parts = lines.get(i).split(CSV_SEPARATOR);
            assertEquals(2, parts.length, "Each line should have X and F(X) values");

            double x = Double.parseDouble(parts[0]);
            double expectedY = systemFunction.calculate(x, EPSILON);
            double actualY = Double.parseDouble(parts[1]);

            assertEquals(expectedY, actualY, 0.001,
                    "Function value in CSV should match calculated value for x = " + x);
        }
    }

    @Test
    @DisplayName("CSV writer should correctly write system function values for positive domain")
    void testCSVWriterForPositiveDomain() throws IOException {
        // Set up test parameters
        double start = 0.1;
        double end = 2.0;
        double step = 0.1;

        // Create output file in temp directory
        File outputFile = new File(tempDir.toFile(), "positive_domain_output.csv");

        // Write values to CSV
        csvWriter.writeFunction(systemFunction, start, end, step, outputFile.getAbsolutePath());

        // Verify file exists
        assertTrue(outputFile.exists(), "CSV file should be created");

        // Read and verify file contents
        List<String> lines = readCSVFile(outputFile);

        // Check number of lines (should match the range divided by step plus header)
        int expectedLineCount = (int) ((end - start) / step) + 1 + 1; // +1 for header
        assertEquals(expectedLineCount, lines.size(), "CSV should have correct number of lines");

        // Verify header
        assertTrue(lines.get(0).contains("X") && lines.get(0).contains("F(X)"),
                "CSV should have a proper header");

        // Verify some values
        for (int i = 1; i < lines.size(); i++) {
            String[] parts = lines.get(i).split(CSV_SEPARATOR);
            assertEquals(2, parts.length, "Each line should have X and F(X) values");

            double x = Double.parseDouble(parts[0]);
            double expectedY = systemFunction.calculate(x, EPSILON);
            double actualY = Double.parseDouble(parts[1]);

            assertEquals(expectedY, actualY, 0.001,
                    "Function value in CSV should match calculated value for x = " + x);
        }
    }

    @Test
    @DisplayName("CSV writer should handle different step sizes correctly")
    void testCSVWriterWithDifferentStepSizes() throws IOException {
        // Test with small step size
        double smallStep = 0.01;
        File smallStepFile = new File(tempDir.toFile(), "small_step_output.csv");
        csvWriter.writeFunction(systemFunction, 0.1, 0.5, smallStep, smallStepFile.getAbsolutePath());

        List<String> smallStepLines = readCSVFile(smallStepFile);
        int smallStepExpectedLineCount = (int) ((0.5 - 0.1) / smallStep) + 1 + 1; // +1 for header
        assertEquals(smallStepExpectedLineCount, smallStepLines.size(),
                "CSV with small step should have correct number of lines");

        // Test with large step size
        double largeStep = 0.5;
        File largeStepFile = new File(tempDir.toFile(), "large_step_output.csv");
        csvWriter.writeFunction(systemFunction, 0.1, 2.0, largeStep, largeStepFile.getAbsolutePath());

        List<String> largeStepLines = readCSVFile(largeStepFile);
        int largeStepExpectedLineCount = (int) ((2.0 - 0.1) / largeStep) + 1 + 1; // +1 for header
        assertEquals(largeStepExpectedLineCount, largeStepLines.size(),
                "CSV with large step should have correct number of lines");
    }

    @Test
    @DisplayName("CSV writer should handle invalid inputs correctly")
    void testCSVWriterWithInvalidInputs() {
        // Start > end
        File invalidRangeFile = new File(tempDir.toFile(), "invalid_range.csv");
        assertThrows(IllegalArgumentException.class,
                () -> csvWriter.writeFunction(systemFunction, 2.0, 1.0, 0.1, invalidRangeFile.getAbsolutePath()),
                "CSV writer should throw exception when start > end");

        // Zero or negative step
        File invalidStepFile = new File(tempDir.toFile(), "invalid_step.csv");
        assertThrows(IllegalArgumentException.class,
                () -> csvWriter.writeFunction(systemFunction, 1.0, 2.0, 0, invalidStepFile.getAbsolutePath()),
                "CSV writer should throw exception when step <= 0");

        // Null function
        File nullFunctionFile = new File(tempDir.toFile(), "null_function.csv");
        assertThrows(NullPointerException.class,
                () -> csvWriter.writeFunction(null, 1.0, 2.0, 0.1, nullFunctionFile.getAbsolutePath()),
                "CSV writer should throw exception when function is null");
    }

    // Helper method to read CSV file
    private List<String> readCSVFile(File file) throws IOException {
        List<String> lines = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                lines.add(line);
            }
        }
        return lines;
    }
}
