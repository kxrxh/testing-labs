package ru.functions.output;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Objects;

import ru.functions.utils.Function;
import ru.functions.system.SystemFunctionInterface;

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

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            writer.write(String.format(HEADER_FORMAT, separator));
            writer.newLine();

            double epsilon = 1e-6;

            // Calculate exact number of steps to ensure end point inclusion
            // Add a small epsilon to ensure we include points that might be just at the
            // boundary
            int numSteps = (int) Math.ceil((end - start) / step + epsilon);

            // Track if we've added the exact endpoint
            boolean endPointAdded = false;

            // Include points from start to just before end with step
            for (int i = 0; i <= numSteps; i++) {
                double x = start + i * step;

                // Ensure we don't go beyond the end (with some tolerance for floating point
                // issues)
                if (x > end + epsilon) {
                    break;
                }

                // If this is very close to the end point, use the exact end value
                if (Math.abs(x - end) < epsilon) {
                    x = end;
                    endPointAdded = true;
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

            // Always explicitly include the end point if not already added
            if (!endPointAdded) {
                try {
                    if (function.isInDomain(end)) {
                        double y = function.calculate(end, epsilon);
                        writer.write(end + separator + y);
                        writer.newLine();
                    }
                } catch (IllegalArgumentException e) {
                    // Skip if function is undefined at end
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
