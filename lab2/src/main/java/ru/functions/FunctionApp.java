package ru.functions;

import ru.functions.logarithmic.ln.LnFunction;
import ru.functions.logarithmic.log10.Log10Function;
import ru.functions.logarithmic.log2.Log2Function;
import ru.functions.logarithmic.log5.Log5Function;
import ru.functions.output.CSVWriter;
import ru.functions.system.NegativeDomainFunction;
import ru.functions.system.PositiveDomainFunction;
import ru.functions.system.SystemFunction;
import ru.functions.trigonometric.cos.CosFunction;
import ru.functions.trigonometric.csc.CscFunction;
import ru.functions.trigonometric.sec.SecFunction;
import ru.functions.trigonometric.sin.SinFunction;
import ru.functions.utils.Function;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class FunctionApp {
    // Make functionMap static and accessible to all methods
    private static Map<Integer, FunctionEntry> functionMap;

    public static void main(String[] args) {
        // Initialize all functions
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

        // Create a map of all available functions for easy selection
        functionMap = new HashMap<>();
        functionMap.put(1, new FunctionEntry("System Function", systemFunction));
        functionMap.put(2, new FunctionEntry("Sin", sinFunction));
        functionMap.put(3, new FunctionEntry("Cos", cosFunction));
        functionMap.put(4, new FunctionEntry("Sec", secFunction));
        functionMap.put(5, new FunctionEntry("Csc", cscFunction));
        functionMap.put(6, new FunctionEntry("Ln", lnFunction));
        functionMap.put(7, new FunctionEntry("Log2", log2Function));
        functionMap.put(8, new FunctionEntry("Log10", log10Function));
        functionMap.put(9, new FunctionEntry("Log5", log5Function));
        functionMap.put(10, new FunctionEntry("Negative Domain Function", negativeDomainFunction));
        functionMap.put(11, new FunctionEntry("Positive Domain Function", positiveDomainFunction));

        CSVWriter csvWriter = new CSVWriter(",");

        Scanner scanner = new Scanner(System.in);
        boolean running = true;

        while (running) {
            System.out.println("\n=== Function System Calculator ===");
            System.out.println("1. Calculate a specific value");
            System.out.println("2. Generate CSV file for a range");
            System.out.println("0. Exit");
            System.out.print("Choose an option (default is 1): ");

            int choice;
            try {
                String input = scanner.nextLine().trim();
                if (input.isEmpty()) {
                    System.out.println("Using default option: Calculate a specific value");
                    choice = 1;
                } else {
                    choice = Integer.parseInt(input);
                }
            } catch (Exception e) {
                System.out.println("Invalid input. Using default option: Calculate a specific value");
                choice = 1;
            }

            switch (choice) {
                case 0:
                    running = false;
                    break;
                case 1:
                    Function selectedFunction = selectFunction(scanner, functionMap);
                    if (selectedFunction != null) {
                        calculateValue(scanner, selectedFunction);
                    }
                    break;
                case 2:
                    Function selectedFunctionForCSV = selectFunction(scanner, functionMap);
                    if (selectedFunctionForCSV != null) {
                        generateCSV(scanner, csvWriter, selectedFunctionForCSV);
                    }
                    break;
                default:
                    System.out.println("Invalid option! Using default option: Calculate a specific value");
                    Function defaultFunction = selectFunction(scanner, functionMap);
                    if (defaultFunction != null) {
                        calculateValue(scanner, defaultFunction);
                    }
            }
        }

        scanner.close();
    }

    private static Function selectFunction(Scanner scanner, Map<Integer, FunctionEntry> functionMap) {
        System.out.println("\nAvailable Functions:");
        for (Map.Entry<Integer, FunctionEntry> entry : functionMap.entrySet()) {
            System.out.println(entry.getKey() + ". " + entry.getValue().getName());
        }
        System.out.print("Select a function (default is 1 - System Function): ");
        
        int functionChoice;
        try {
            String input = scanner.nextLine().trim();
            if (input.isEmpty()) {
                System.out.println("Using default function: System Function");
                functionChoice = 1;
            } else {
                functionChoice = Integer.parseInt(input);
            }
        } catch (Exception e) {
            System.out.println("Invalid input. Using default function: System Function");
            functionChoice = 1;
        }
        
        FunctionEntry entry = functionMap.get(functionChoice);
        if (entry == null) {
            System.out.println("Invalid function selection! Using default function: System Function");
            entry = functionMap.get(1);
        }
        
        System.out.println("Selected: " + entry.getName());
        return entry.getFunction();
    }

    private static void calculateValue(Scanner scanner, Function function) {
        System.out.print("Enter x: ");
        double x;
        try {
            String input = scanner.nextLine().trim();
            if (input.isEmpty()) {
                System.out.println("Using default value: x = 0.0");
                x = 0.0;
            } else {
                x = Double.parseDouble(input);
            }
        } catch (Exception e) {
            System.out.println("Invalid input. Please enter a number.");
            return;
        }

        System.out.print("Enter precision (e.g., 1e-6): ");
        double epsilon;
        try {
            String input = scanner.nextLine().trim();
            if (input.isEmpty()) {
                System.out.println("Using default precision: 1e-6");
                epsilon = 1e-6;
            } else {
                epsilon = Double.parseDouble(input);
            }
        } catch (Exception e) {
            System.out.println("Invalid input. Using default precision: 1e-6");
            epsilon = 1e-6;
        }

        // Get the function name from the functionMap
        String functionName = "f";
        for (Map.Entry<Integer, FunctionEntry> entry : functionMap.entrySet()) {
            if (entry.getValue().getFunction() == function) {
                functionName = entry.getValue().getName();
                break;
            }
        }

        try {
            if (function.isInDomain(x)) {
                double result = function.calculate(x, epsilon);
                System.out.println(functionName + "(" + x + ") = " + result);
            } else {
                System.out.println("The function " + functionName + " is undefined at x = " + x);
            }
        } catch (IllegalArgumentException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private static void generateCSV(Scanner scanner, CSVWriter csvWriter, Function function) {
        System.out.print("Enter start x (default is -10.0): ");
        double start;
        try {
            String input = scanner.nextLine().trim();
            if (input.isEmpty()) {
                System.out.println("Using default start: -10.0");
                start = -10.0;
            } else {
                start = Double.parseDouble(input);
            }
        } catch (Exception e) {
            System.out.println("Invalid input. Using default start: -10.0");
            start = -10.0;
        }

        System.out.print("Enter end x (default is 10.0): ");
        double end;
        try {
            String input = scanner.nextLine().trim();
            if (input.isEmpty()) {
                System.out.println("Using default end: 10.0");
                end = 10.0;
            } else {
                end = Double.parseDouble(input);
            }
        } catch (Exception e) {
            System.out.println("Invalid input. Using default end: 10.0");
            end = 10.0;
        }

        System.out.print("Enter step size (default is 0.1): ");
        double step;
        try {
            String input = scanner.nextLine().trim();
            if (input.isEmpty()) {
                System.out.println("Using default step: 0.1");
                step = 0.1;
            } else {
                step = Double.parseDouble(input);
            }
        } catch (Exception e) {
            System.out.println("Invalid input. Using default step: 0.1");
            step = 0.1;
        }

        System.out.print("Enter CSV separator (default is ','): ");
        String separator = scanner.nextLine().trim();
        if (separator.isEmpty()) {
            System.out.println("Using default separator: ','");
            separator = ",";
        }
        
        // Create a new CSVWriter with the specified separator
        CSVWriter customCsvWriter = new CSVWriter(separator);

        System.out.print("Enter output file name (default is 'output.csv'): ");
        String fileName = scanner.nextLine().trim();
        if (fileName.isEmpty()) {
            System.out.println("Using default filename: 'output.csv'");
            fileName = "output.csv";
        }

        // Get the function name from the functionMap
        String functionName = "F";
        for (Map.Entry<Integer, FunctionEntry> entry : functionMap.entrySet()) {
            if (entry.getValue().getFunction() == function) {
                functionName = entry.getValue().getName();
                break;
            }
        }

        try {
            customCsvWriter.writeFunction(function, functionName, start, end, step, fileName);
            System.out.println("CSV file generated successfully: " + fileName);
            System.out.println("Using separator: '" + separator + "'");
        } catch (IOException e) {
            System.out.println("Error writing to file: " + e.getMessage());
        }
    }

    // Helper class to store function name and reference
    private static class FunctionEntry {
        private final String name;
        private final Function function;

        public FunctionEntry(String name, Function function) {
            this.name = name;
            this.function = function;
        }

        public String getName() {
            return name;
        }

        public Function getFunction() {
            return function;
        }
    }
}
