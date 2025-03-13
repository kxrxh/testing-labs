import matplotlib.pyplot as plt
import pandas as pd
import sys
import os
import numpy as np
import math


def plot_reference_functions(ax, x_range=None):
    """Plot reference functions on the given axis"""
    if x_range is None:
        # Default range if not specified
        x_neg = np.linspace(-100, -0.1, 1000)
        x_pos = np.linspace(0.1, 100, 1000)
    else:
        # Use the provided range
        x_min, x_max = x_range
        x_neg = np.linspace(min(x_min, -0.1), -0.1, 1000)
        x_pos = np.linspace(0.1, max(x_max, 10), 1000)

    # Calculate reference values for x <= 0
    y_neg = []
    for x in x_neg:
        try:
            sec_x = 1 / np.cos(x)
            csc_x = 1 / np.sin(x)
            result = ((((sec_x * csc_x) / np.cos(x)) - sec_x) ** 2) - np.sin(x)
            y_neg.append(result)
        except:
            y_neg.append(np.nan)

    # Calculate reference values for x > 0
    y_pos = []
    for x in x_pos:
        try:
            log2_x = np.log2(x)
            log10_x = np.log10(x)
            log5_x = np.log(x) / np.log(5)
            result = ((((log2_x + log10_x) ** 2) - log2_x) - log10_x) - log5_x
            y_pos.append(result)
        except:
            y_pos.append(np.nan)

    # Plot reference functions
    ax.plot(
        x_neg,
        y_neg,
        "r-",
        alpha=0.8,
        linewidth=2,
        label="x ≤ 0: (((((sec(x) * csc(x)) / cos(x)) - sec(x)) ^ 2) - sin(x))",
    )
    ax.plot(
        x_pos,
        y_pos,
        "g-",
        alpha=0.8,
        linewidth=2,
        label="x > 0: (((((log_2(x) + log_10(x)) ^ 2) - log_2(x)) - log_10(x)) - log_5(x))",
    )


def plot_system_with_csv(csv_file):
    """Plot system function and CSV data together"""
    # Read the CSV file
    df = pd.read_csv(csv_file)

    # Extract x and y values
    x_values = df["X"]
    y_values = df["F(X)"]

    # Separate negative and positive domain data
    neg_mask = x_values <= 0
    pos_mask = x_values > 0

    x_neg = x_values[neg_mask]
    y_neg = y_values[neg_mask]
    x_pos = x_values[pos_mask]
    y_pos = y_values[pos_mask]

    # Create the plot with two subplots
    fig, (ax1, ax2) = plt.subplots(1, 2, figsize=(20, 10))
    fig.suptitle("System Function and CSV Data Comparison", fontsize=16)

    # First subplot - Full range with both domains
    # Plot reference functions
    plot_reference_functions(ax1, x_range=(min(x_values), max(x_values)))

    # Plot CSV data
    ax1.scatter(
        x_neg,
        y_neg,
        color="blue",
        s=30,
        alpha=0.7,
        marker="o",
        label="CSV data (x ≤ 0)",
    )
    ax1.scatter(
        x_pos,
        y_pos,
        color="purple",
        s=30,
        alpha=0.7,
        marker="o",
        label="CSV data (x > 0)",
    )

    # Add labels and grid
    ax1.set_xlabel("X", fontsize=12)
    ax1.set_ylabel("F(X)", fontsize=12)
    ax1.set_title("Full Range Plot", fontsize=14)
    ax1.grid(True, alpha=0.3)
    ax1.legend(fontsize=10)

    # Add vertical line at x=0
    ax1.axvline(x=0, color="k", linestyle="--", alpha=0.5)
    # Add horizontal line at y=0
    ax1.axhline(y=0, color="k", linestyle="--", alpha=0.5)

    # Set reasonable y-limits to exclude extreme values
    # Calculate percentiles to exclude outliers
    y_neg_filtered = y_neg[
        (y_neg > np.percentile(y_neg, 5)) & (y_neg < np.percentile(y_neg, 95))
    ]
    if len(y_neg_filtered) > 0:
        y_min = min(0, np.min(y_neg_filtered))
        y_max = max(100, np.max(y_neg_filtered))
        ax1.set_ylim(y_min, y_max)

    # Second subplot - Positive domain only (for better visualization)
    # Plot reference function for x > 0
    x_pos_ref = np.linspace(0.1, max(x_pos), 1000)
    y_pos_ref = []
    for x in x_pos_ref:
        try:
            log2_x = np.log2(x)
            log10_x = np.log10(x)
            log5_x = np.log(x) / np.log(5)
            result = ((((log2_x + log10_x) ** 2) - log2_x) - log10_x) - log5_x
            y_pos_ref.append(result)
        except:
            y_pos_ref.append(np.nan)

    ax2.plot(
        x_pos_ref,
        y_pos_ref,
        "g-",
        alpha=0.8,
        linewidth=2,
        label="x > 0: (((((log_2(x) + log_10(x)) ^ 2) - log_2(x)) - log_10(x)) - log_5(x))",
    )

    # Plot positive domain CSV data
    ax2.scatter(
        x_pos,
        y_pos,
        color="purple",
        s=50,
        alpha=0.7,
        marker="o",
        label="CSV data (x > 0)",
    )

    # Add labels and grid
    ax2.set_xlabel("X", fontsize=12)
    ax2.set_ylabel("F(X)", fontsize=12)
    ax2.set_title("Positive Domain (x > 0)", fontsize=14)
    ax2.grid(True, alpha=0.3)
    ax2.legend(fontsize=10)

    # Add vertical line at x=0
    ax2.axvline(x=0, color="k", linestyle="--", alpha=0.5)
    # Add horizontal line at y=0
    ax2.axhline(y=0, color="k", linestyle="--", alpha=0.5)

    # Adjust layout
    plt.tight_layout()

    # Save the plot
    output_file = "system_function_plot.png"
    plt.savefig(output_file)
    print(f"Plot saved as {output_file}")

    # Show the plot
    plt.show()


def main():
    print("\n=== Function System Plotter ===")

    file_path = input("Enter CSV file path (or press Enter for default '../out.csv'): ")
    if not file_path:
        file_path = "../out.csv"

    if not os.path.exists(file_path):
        print(f"Error: File '{file_path}' not found.")
        return

    try:
        plot_system_with_csv(file_path)
    except Exception as e:
        print(f"Error plotting data: {e}")


if __name__ == "__main__":
    main()
