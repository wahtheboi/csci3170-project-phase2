package salessystem;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;

public class BasicOperation {
    protected static BufferedReader inputReader = new BufferedReader(new InputStreamReader(System.in));

    // All tables in the database
    public static final String[] TABLES = {
            "category",
            "manufacturer",
            "part",
            "salesperson",
            "transaction"
    };

    // Check if the input is the valid table name which is in the TABLES array
    public static boolean isValidTableName(String tableName) {
        return Arrays.stream(TABLES).anyMatch(x -> x.equals(tableName));
    }

    // Check if the input is the valid integer
    public static boolean isValidOption(String s) {
        return s.matches("^[0-9]+$");
    }

    // Ask for input an integer and perform validation
    public static int getInputInteger() throws IOException {
        int choice;
        try {
            String input = inputReader.readLine();
            if (input.isEmpty() || !isValidOption(input)) {
                System.out.println("Please enter a valid number");
                return -1;
            }

            choice = Integer.parseInt(input);
        } catch (NumberFormatException e) {
            System.out.println("Please enter a valid number");
            return -1;
        }

        return choice;
    }

    // Ask for input a string and perform validation
    public static String getInputString() throws IOException {
        String input;
        input = inputReader.readLine();
        if (input.isEmpty()) {
            System.out.println("Please enter a non empty string");
            return null;
        }
        return input;
    }

}
