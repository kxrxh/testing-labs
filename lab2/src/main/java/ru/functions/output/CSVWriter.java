package ru.functions.output;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import ru.functions.utils.Function;

/**
 * Utility class for writing function values to CSV files
 */
public class CSVWriter {
    private final String delimiter;

    /**
     * Creates a new CSV writer with the specified delimiter
     *
     * @param delimiter The delimiter to use in CSV files (e.g., ",", ";", "\t")
     */
    public CSVWriter(String delimiter) {
        this.delimiter = delimiter;
    }

    /**
     * Writes function values to a CSV file in the format "X, Function(X)"
     *
     * @param function The function to evaluate
     * @param fileName The output file name
     * @param start    The starting x value
     * @param end      The ending x value
     * @param step     The step size
     * @param epsilon  The precision for function evaluation
     * @throws IOException If an I/O error occurs
     */
    public void writeToFile(
            Function function,
            String fileName,
            double start,
            double end,
            double step,
            double epsilon) throws IOException {

        try (PrintWriter writer = new PrintWriter(new FileWriter(fileName))) {
            // Write header
            writer.println("X" + delimiter + "Function(X)");

            // Write data points
            for (double x = start; x <= end; x += step) {
                try {
                    if (function.isInDomain(x)) {
                        double y = function.calculate(x, epsilon);
                        writer.println(x + delimiter + y);
                    } else {
                        writer.println(x + delimiter + "undefined");
                    }
                } catch (IllegalArgumentException e) {
                    writer.println(x + delimiter + "error: " + e.getMessage());
                }
            }
        }
    }
}
