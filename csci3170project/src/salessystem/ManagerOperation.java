package salessystem;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class ManagerOperation extends BasicOperation {
    // Constructs a new ManagerOperation
    protected static BufferedReader inputReader = new BufferedReader(new InputStreamReader(System.in));
    private Connection connection;

    public ManagerOperation(Connection connection) {
        this.connection = connection;
    }

    // The main function of the ManagerOperation
    public void start() throws Exception {
        displayManagerMenu();
    }

    // Display the Manager menu
    private void displayManagerMenu() throws Exception {
        boolean isExit = false;
        while (!isExit) {
            System.out.println("\n-----Operations for manager menu----");
            System.out.println("What kinds of operation would you like to perform?");
            System.out.println("1. List all salespersons");
            System.out.println(
                    "2. Count the no. of sales record of each salesperson under a specific range on years of experience");
            System.out.println("3. Show the total sales value of each manufacturer");
            System.out.println("4. Show the N most popular parts");
            System.out.println("5. Return to the main menu");
            isExit = selectOperation();
        }
    }

    // Logic for selecting each operation
    private boolean selectOperation() throws Exception {
        System.out.print("Enter Your Choice: ");
        boolean isExit = false;

        int choice = getInputInteger();
        if (choice < 0) {
            return isExit;
        }

        switch (choice) {
            case 1:
                listAllSalespersonsByExperience();
                break;
            case 2:
                countTransactionRecordsByExperience();
                break;
            case 3:
                showTotalSalesValueOfEachManufacturer();
                break;
            case 4:
                showNMostPopularParts();
                break;
            case 5:
                isExit = true;
                break;
            case '\n':
                System.out.println("Error: Input must be within 1 to 5");
            default:
                System.out.println("Error: Input must be within 1 to 5");
        }

        return isExit;
    }

    // List all salespersons by experience range and order by specific order
    private void listAllSalespersonsByExperience() throws Exception {
        String order = "";
        while (!(order == "ASC" || order == "DESC")) {
            System.out.println("Choosing ordering:");
            System.out.println("1. By ascending order");
            System.out.println("2. By descending order");

            System.out.print("Choose the list order: ");
            int choice = getInputInteger();

            order = "";
            switch (choice) {
                case 1:
                    order = "ASC";
                    break;
                case 2:
                    order = "DESC";
                    break;
                default:
                    System.out.println("Error: Invalid Choice");
            }
        }

        PreparedStatement statement = this.connection.prepareStatement(
                "SELECT * FROM salesperson ORDER BY s_experience " + order);
        ResultSet rs = statement.executeQuery();

        System.out.println("| ID | Name | Mobile Phone | Years of Experience |");
        while (rs.next()) {
            System.out.printf(
                    "| %d | %s | %d | %d |\n",
                    rs.getInt("s_id"),
                    rs.getString("s_name"),
                    rs.getInt("s_phone_number"),
                    rs.getInt("s_experience"));
        }
        System.out.println("End of Query");
    }

    // Count the no. of sales record of each salesperson under a specific range on
    // years of experience
    private void countTransactionRecordsByExperience() throws Exception {
        System.out.print("Type in the lower bound for years of experience: ");
        int lowerBound = getInputInteger();

        System.out.print("Type in the upper bound for years of experience: ");
        int upperBound = getInputInteger();

        PreparedStatement statement = this.connection
                .prepareStatement(
                        "SELECT *, COUNT(transaction.t_id) AS transaction_count FROM (salesperson INNER JOIN transaction ON transaction.s_id = salesperson.s_id) WHERE s_experience >="
                                + lowerBound + " AND s_experience <=" + upperBound + " GROUP BY salesperson.s_id");

        ResultSet rs = statement.executeQuery();
        System.out.println("Transaction Record:");
        System.out.println("| ID | Name | Years of Experience | Number of Transaction |");
        while (rs.next()) {
            System.out.printf(
                    "| %d | %s | %d | %d |\n",
                    rs.getInt("t_id"),
                    rs.getString("s_name"),
                    rs.getInt("s_experience"),
                    rs.getInt("transaction_count"));
        }

        System.out.println("End of Query");
    }

    // Show total sales value of each manufacturer
    private void showTotalSalesValueOfEachManufacturer() throws Exception {
        PreparedStatement statement = this.connection.prepareStatement(
                "SELECT *, SUM(part.p_price) AS total_sales FROM (transaction INNER JOIN part ON part.p_id = transaction.p_id INNER JOIN manufacturer ON manufacturer.m_id = part.m_id) GROUP BY manufacturer.m_id ORDER BY total_sales DESC");
        ResultSet rs = statement.executeQuery();

        System.out.println("| Manufacturer ID | Manufacturer Name | Total Sales Value |");
        while (rs.next()) {
            System.out.printf(
                    "| %d | %s | %d |\n",
                    rs.getInt("m_id"),
                    rs.getString("m_name"),
                    rs.getInt("total_sales"));
        }

        System.out.println("End of Query");
    }

    // Show the N most popular parts
    private void showNMostPopularParts() throws Exception {
        int n = -1;
        while (!(n >= 0)) {
            System.out.print("Type in the number of parts: ");
            n = getInputInteger();
        }

        PreparedStatement statement = this.connection.prepareStatement(
                "SELECT *, COUNT(transaction.t_id) AS transaction_count FROM (part INNER JOIN transaction ON transaction.p_id = part.p_id) GROUP BY part.p_id ORDER BY transaction_count DESC LIMIT "
                        + n);
        ResultSet rs = statement.executeQuery();

        System.out.println("| Part ID | Part Name | NO. of Transaction |");
        while (rs.next()) {
            System.out.printf(
                    "| %d | %s | %d |\n",
                    rs.getInt("p_id"),
                    rs.getString("p_name"),
                    rs.getInt("transaction_count"));
        }

        System.out.println("End of Query");
    }
}