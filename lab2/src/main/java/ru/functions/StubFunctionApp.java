package ru.functions;

import ru.functions.logarithmic.ln.LnFunctionStub;
import ru.functions.logarithmic.log10.Log10FunctionStub;
import ru.functions.logarithmic.log2.Log2FunctionStub;
import ru.functions.logarithmic.log5.Log5FunctionStub;
import ru.functions.output.CSVWriter;
import ru.functions.system.NegativeDomainFunctionStub;
import ru.functions.system.PositiveDomainFunctionStub;
import ru.functions.system.SystemFunctionStub;
import ru.functions.trigonometric.cos.CosFunctionStub;
import ru.functions.trigonometric.csc.CscFunctionStub;
import ru.functions.trigonometric.sec.SecFunctionStub;
import ru.functions.trigonometric.sin.SinFunctionStub;
import ru.functions.utils.Function;

import java.io.IOException;
import java.util.Scanner;

/**
 * Main application class for stubs
 */
public class StubFunctionApp {

    public static void main(String[] args) {

        SinFunctionStub sinFunctionStub = new SinFunctionStub();
        CosFunctionStub cosFunctionStub = new CosFunctionStub(sinFunctionStub);
        SecFunctionStub secFunctionStub = new SecFunctionStub(cosFunctionStub);
        CscFunctionStub cscFunctionStub = new CscFunctionStub(sinFunctionStub);

        LnFunctionStub lnFunctionStub = new LnFunctionStub();
        Log2FunctionStub log2FunctionStub = new Log2FunctionStub();
        Log10FunctionStub log10FunctionStub = new Log10FunctionStub();
        Log5FunctionStub log5FunctionStub = new Log5FunctionStub();

        NegativeDomainFunctionStub negativeDomainFunctionStub = new NegativeDomainFunctionStub(
                sinFunctionStub, cosFunctionStub, secFunctionStub, cscFunctionStub);

        PositiveDomainFunctionStub positiveDomainFunctionStub = new PositiveDomainFunctionStub(
                log2FunctionStub, log10FunctionStub, log5FunctionStub);

        SystemFunctionStub systemFunctionStub = new SystemFunctionStub(
                negativeDomainFunctionStub, positiveDomainFunctionStub);

        CSVWriter csvWriter = new CSVWriter(",");

        Scanner scanner = new Scanner(System.in);
        boolean running = true;

        while (running) {
            System.out.println("\n=== Function System Calculator (Stub Version) ===");
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
                    calculateValue(scanner, systemFunctionStub);
                    break;
                case 2:
                    generateCSV(scanner, csvWriter, systemFunctionStub);
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
            csvWriter.writeFunction(function, start, end, step, fileName);
            System.out.println("CSV file generated successfully: " + fileName);
        } catch (IOException e) {
            System.out.println("Error writing to file: " + e.getMessage());
        }
    }
}
