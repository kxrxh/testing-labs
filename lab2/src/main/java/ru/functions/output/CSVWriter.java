package ru.functions.output;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Objects;

import ru.functions.utils.Function;
import ru.functions.system.SystemFunctionInterface;

/**
 * A utility class for writing function values to CSV files
 */
public class CSVWriter {
    private final String separator;
    private static final String DEFAULT_SEPARATOR = ",";
    private static final String HEADER_FORMAT = "X%sF(X)";

    /**
     * Creates a CSVWriter with the default separator (,)
     */
    public CSVWriter() {
        this(DEFAULT_SEPARATOR);
    }

    /**
     * Creates a CSVWriter with the specified separator
     *
     * @param separator the CSV field separator
     */
    public CSVWriter(String separator) {
        this.separator = separator;
    }

    /**
     * Writes function values to a CSV file for the given range of input values
     *
     * @param function the function to evaluate
     * @param start    the start of the input range
     * @param end      the end of the input range (inclusive)
     * @param step     the step size between input values
     * @param filePath the path of the output file
     * @throws IOException              if an I/O error occurs
     * @throws IllegalArgumentException if the range parameters are invalid
     * @throws NullPointerException     if the function or filePath is null
     */
    public void writeFunction(Function function, double start, double end, double step, String filePath)
            throws IOException, IllegalArgumentException, NullPointerException {
        // Validate parameters
        Objects.requireNonNull(function, "Function cannot be null");
        Objects.requireNonNull(filePath, "File path cannot be null");

        if (start > end) {
            throw new IllegalArgumentException("Start value must be less than or equal to end value");
        }

        if (step <= 0) {
            throw new IllegalArgumentException("Step size must be positive");
        }

        // Write to file
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            // Write header
            writer.write(String.format(HEADER_FORMAT, separator));
            writer.newLine();

            // Write function values
            double epsilon = 1e-6; // Default precision

            // Calculate the number of steps to ensure we include the end point
            int numSteps = (int) Math.round((end - start) / step) + 1;

            for (int i = 0; i < numSteps; i++) {
                double x = start + i * step;
                // Ensure we don't exceed the end value due to floating-point errors
                if (x > end + 1e-10) {
                    break;
                }

                try {
                    if (function.isInDomain(x)) {
                        double y = function.calculate(x, epsilon);
                        writer.write(x + separator + y);
                        writer.newLine();
                    }
                } catch (IllegalArgumentException e) {
                    // Skip points outside the domain or where function is undefined
                }
            }
        }
    }

    /**
     * Writes system function values to a CSV file for the given range of input
     * values
     *
     * @param systemFunction the system function to evaluate
     * @param start          the start of the input range
     * @param end            the end of the input range (inclusive)
     * @param step           the step size between input values
     * @param filePath       the path of the output file
     * @throws IOException              if an I/O error occurs
     * @throws IllegalArgumentException if the range parameters are invalid
     * @throws NullPointerException     if the function or filePath is null
     */
    public void writeFunction(SystemFunctionInterface systemFunction, double start, double end, double step,
            String filePath)
            throws IOException, IllegalArgumentException, NullPointerException {
        writeFunction((Function) systemFunction, start, end, step, filePath);
    }
}
