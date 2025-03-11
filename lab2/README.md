# Function System Calculator

This project implements a system of mathematical functions with integration testing using JUnit 5.

## System Function Definition

The system implements the following piecewise function:

- For x ≤ 0: (((((sec(x) \* csc(x)) / cos(x)) - sec(x)) ^ 2) - sin(x))
- For x > 0: (((((log_2(x) + log_10(x)) ^ 2) - log_2(x)) - log_10(x)) - log_5(x))

The domain of the function is ℝ \ ({0} ∪ {-kπ/2}\_{k=1}^{∞}), meaning the function is undefined at:

- x = 0
- x = -π/2, -π, -3π/2, ...

## Project Structure

The project follows a modular approach with separate implementations for each function:

- **Base Functions**:

  - Sine (SinFunction): Implemented using Taylor series
  - Natural logarithm (LnFunction): Implemented using Taylor series

- **Derived Trigonometric Functions**:

  - Cosine (CosFunction): Derived from sine using cos(x) = sin(x + π/2)
  - Secant (SecFunction): Derived from cosine using sec(x) = 1/cos(x)
  - Cosecant (CscFunction): Derived from sine using csc(x) = 1/sin(x)

- **Derived Logarithmic Functions**:

  - Log base 2 (Log2Function): Derived from natural logarithm using log_2(x) = ln(x)/ln(2)
  - Log base 10 (Log10Function): Derived from natural logarithm using log_10(x) = ln(x)/ln(10)
  - Log base 5 (Log5Function): Derived from natural logarithm using log_5(x) = ln(x)/ln(5)

- **Composite Functions**:

  - NegativeDomainFunction: Implements the function for x ≤ 0
  - PositiveDomainFunction: Implements the function for x > 0
  - SystemFunction: Combines both domains into a single piecewise function

- **Stub Implementations**:
  - Each function also has a stub implementation that uses predefined values for testing

## Running the Application

The application provides two interfaces:

1. **FunctionApp**: Uses actual implementations with Taylor series
2. **StubFunctionApp**: Uses stub implementations with predefined values

Both applications offer the same functionality:

- Calculate function values for a specific input
- Generate CSV files with function values for a range of inputs

## Testing

The project uses JUnit 5 for testing with JaCoCo for test coverage analysis.

Tests are organized into:

- Unit tests for individual functions
- Integration tests for the complete system

### Running Tests

```bash
mvn clean test
```
