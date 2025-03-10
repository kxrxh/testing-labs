package ru.functions;

import ru.functions.logarithmic.LnFunction;
import ru.functions.logarithmic.Log10Function;
import ru.functions.logarithmic.Log2Function;
import ru.functions.logarithmic.Log5Function;
import ru.functions.output.CSVWriter;
import ru.functions.system.NegativeDomainFunction;
import ru.functions.system.PositiveDomainFunction;
import ru.functions.system.SystemFunction;
import ru.functions.trigonometric.CosFunction;
import ru.functions.trigonometric.CscFunction;
import ru.functions.trigonometric.SecFunction;
import ru.functions.trigonometric.SinFunction;
import ru.functions.utils.Function;

import java.io.IOException;
import java.util.Scanner;

/**
 * Main application class
 */
public class FunctionApp {

    public static void main(String[] args) {
        // Create function instances
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

        // Create CSV writer
        CSVWriter csvWriter = new CSVWriter(",");

        // Set up user interface
        Scanner scanner = new Scanner(System.in);
        boolean running = true;

        while (running) {
            System.out.println("\n=== Function System Calculator ===");
            System.out.println("1. Calculate a specific value");
            System.out.println("2. Generate CSV file for a range");
            System.out.println("0. Exit");
            System.out.print("Choose an option: ");

            int choice = scanner.nextInt();

            switch (choice) {
                case 0:
                    running = false;
                    break;
                case 1:
                    calculateValue(scanner, systemFunction);
                    break;
                case 2:
                    generateCSV(scanner, csvWriter, systemFunction);
                    break;
                default:
                    System.out.println("Invalid option!");
            }
        }

        scanner.close();
    }

    private static void calculateValue(Scanner scanner, Function function) {
        System.out.print("Enter x: ");
        double x = scanner.nextDouble();

        System.out.print("Enter precision (e.g., 1e-6): ");
        double epsilon = scanner.nextDouble();

        try {
            if (function.isInDomain(x)) {
                double result = function.calculate(x, epsilon);
                System.out.println("f(" + x + ") = " + result);
            } else {
                System.out.println("The function is undefined at x = " + x);
            }
        } catch (IllegalArgumentException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private static void generateCSV(Scanner scanner, CSVWriter csvWriter, Function function) {
        System.out.print("Enter start x: ");
        double start = scanner.nextDouble();

        System.out.print("Enter end x: ");
        double end = scanner.nextDouble();

        System.out.print("Enter step size: ");
        double step = scanner.nextDouble();

        System.out.print("Enter precision (e.g., 1e-6): ");
        double epsilon = scanner.nextDouble();

        System.out.print("Enter output file name: ");
        scanner.nextLine(); // Consume newline
        String fileName = scanner.nextLine();

        try {
            csvWriter.writeToFile(function, fileName, start, end, step, epsilon);
            System.out.println("CSV file generated successfully: " + fileName);
        } catch (IOException e) {
            System.out.println("Error writing to file: " + e.getMessage());
        }
    }
}
