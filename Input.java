import java.util.*;

/**
 * Robust console input reader with defensive input validation.
 * Prevents InputMismatchException crashes, newline desynchronization,
 * and handles EOF/closed streams gracefully.
 */
public class Input {
    public static final Scanner sc = new Scanner(System.in);

    /**
     * Safely reads the next line of input, trimming whitespace.
     * Returns empty string if EOF is reached.
     */
    public static String readLine(String prompt) {
        if (prompt != null && !prompt.isEmpty()) {
            System.out.print(prompt);
        }
        try {
            if (sc.hasNextLine()) {
                return sc.nextLine().trim();
            }
        } catch (Exception ex) {
            System.err.println("Input error: " + ex.getMessage());
        }
        return "";
    }

    /**
     * Reads a required non-empty string.
     */
    public static String readNonEmptyString(String prompt) {
        while (true) {
            String val = readLine(prompt);
            if (!val.isEmpty()) {
                return val;
            }
            System.out.println("Input cannot be empty. Please try again.");
        }
    }

    /**
     * Safely reads an integer with boundary checks and error recovery.
     */
    public static int readInt(String prompt, int min, int max) {
        while (true) {
            String line = readLine(prompt);
            try {
                int val = Integer.parseInt(line);
                if (val < min || val > max) {
                    System.out.println("Please enter a number between " + min + " and " + max + ".");
                    continue;
                }
                return val;
            } catch (NumberFormatException e) {
                System.out.println("Invalid numeric input. Please enter a valid whole number.");
            }
        }
    }

    /**
     * Safely reads a positive integer.
     */
    public static int readPositiveInt(String prompt) {
        return readInt(prompt, 1, Integer.MAX_VALUE);
    }

    /**
     * Safely reads a double with a minimum value constraint.
     */
    public static double readDouble(String prompt, double min) {
        while (true) {
            String line = readLine(prompt);
            try {
                double val = Double.parseDouble(line);
                if (Double.isNaN(val) || Double.isInfinite(val)) {
                    System.out.println("Invalid number format. Please enter a finite numeric value.");
                    continue;
                }
                if (val < min) {
                    System.out.println("Value must be at least " + min + ".");
                    continue;
                }
                return val;
            } catch (NumberFormatException e) {
                System.out.println("Invalid numeric input. Please enter a valid decimal number.");
            }
        }
    }
}
