package salessystem;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class SalespersonOperation extends BasicOperation {
    private Connection connection;

    // Constructs a new SalespersonOperation
    public SalespersonOperation(Connection connection) {
        this.connection = connection;
    }

    // The main function of the SalespersonOperation
    public void start() throws Exception {
        displaySalesMenu();
    }

    // Display the Salesperson menu
    private void displaySalesMenu() throws Exception {
        boolean isExit = false;
        while (!isExit) {
            System.out.println("\n-----Operations for salesperson menu-----");
            System.out.println("What kinds of operation would you like to perform?");
            System.out.println("1. Search for parts");
            System.out.println("2. Sell a part");
            System.out.println("3. Return to the main menu");

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
                searchPartsOption();
                break;
            case 2:
                sellPartOption();
                break;
            case 3:
                isExit = true;
                break;
            default:
                System.out.println("Error: Input must be within 1 to 5");
        }

        return isExit;
    }

    // Perform search parts operation
    private void searchPartsOption() throws Exception {
        String criterion;
        String criterionValue;
        String orderDirection;

        criterion = getCriterion();
        criterionValue = getCriterionValue();
        orderDirection = getOrderDirection();

        PreparedStatement statement = this.connection.prepareStatement(
                "SELECT * FROM (part INNER JOIN manufacturer ON part.m_id = manufacturer.m_id INNER JOIN category ON part.c_id = category.c_id) WHERE "
                        + criterion + " LIKE'%" + criterionValue + "%' ORDER BY part.p_price " + orderDirection);

        ResultSet rs = statement.executeQuery();
        System.out.println("| ID | Name | Manufacturer | Category | Quantity | Warranty | Price |");
        while (rs.next()) {
            System.out.printf(
                    "| %d | %s | %s | %s | %d | %d | %d |\n",
                    rs.getInt("p_id"),
                    rs.getString("p_name"),
                    rs.getString("m_name"),
                    rs.getString("c_name"),
                    rs.getInt("p_available_quantity"),
                    rs.getInt("p_warranty_period"),
                    rs.getInt("p_price"));
        }
        System.out.println("End of Query");
    }

    // Get the criterion
    private String getCriterion() throws Exception {
        while (true) {
            System.out.println("Choose the Search Criterion:");
            System.out.println("1. Part Name");
            System.out.println("2. Manufacturer Name");

            System.out.print("Choose the search criterion: ");

            int choice = getInputInteger();
            switch (choice) {
                case 1:
                    return "part.p_name";
                case 2:
                    return "manufacturer.m_name";
                default:
                    System.out.println("Error: Invalid Choice");
            }
        }
    }

    // Get the value of criterion
    private String getCriterionValue() throws Exception {
        while (true) {
            System.out.print("Type in the Search Keyword: ");

            String input = getInputString();
            if (input != null) {
                return input;
            }
        }
    }

    // Get the search order
    private String getOrderDirection() throws Exception {
        while (true) {
            System.out.println("Choosing ordering:");
            System.out.println("1. By price, ascending order");
            System.out.println("2. By price, descending order");

            System.out.print("Choose the search criterion: ");

            int choice = getInputInteger();
            switch (choice) {
                case 1:
                    return "ASC";
                case 2:
                    return "DESC";
                default:
                    System.out.println("Error: Invalid Choice");
            }
        }
    }

    //sell a part
    private void sellPartOption() throws Exception {
        int partID = -1;
        int salespersonID = -1;

        //part id validation
        while (partID <= 0 || partID > 32) {
            System.out.print("Enter The Part ID: ");
            partID = getInputInteger();
            if (partID <= 0 || partID > 32) {
                System.out.println("No existing Part ID, please try again.");
            }
        }
        //salesperson id validation
        while (salespersonID <= 0 || salespersonID > 4) {
            System.out.print("Enter The Salesperson ID: ");
            salespersonID = getInputInteger();
            if (salespersonID <= 0 || salespersonID > 4) {
                System.out.println("No existing Salesperson ID, please try again.");
            }
        }

        PreparedStatement statement = this.connection
                .prepareStatement("SELECT * FROM part WHERE part.p_id = " + partID);
        ResultSet rs = statement.executeQuery();
        //part quantity validation
        rs.next();
        if (rs.getInt("p_available_quantity") > 0) {
            sellPart(rs, partID, salespersonID);
            return;
        }
        System.out.println("Error: The part is not available");
    }

    private void sellPart(ResultSet rs, int partID, int salespersonID) throws SQLException {
        // Get the new transaction ID to insert into the transaction table
        String transactionID = "t_id";
        String tableName = "transaction";

        String query = "SELECT MAX(" + transactionID + ") FROM " + tableName;
        Statement statement = this.connection.createStatement();
        ResultSet resultSet = statement.executeQuery(query);
        resultSet.next();

        int newPrimaryKey = resultSet.getInt(1) + 1;

        // Get the date for the new transaction
        PreparedStatement ps = this.connection.prepareStatement("SELECT CURDATE()");
        resultSet = ps.executeQuery();
        resultSet.next();

        Date currentDate = resultSet.getDate(1);

        //System.out.println(newPrimaryKey);
        //System.out.println(currentDate);

        ps = this.connection.prepareStatement("INSERT INTO transaction (t_id, p_id, s_id, t_date) VALUES (?, ?, ?, ?)");
        ps.setInt(1, newPrimaryKey);
        ps.setInt(2, partID);
        ps.setInt(3, salespersonID);
        ps.setDate(4, currentDate);

        ps.executeUpdate();

        //update part quantity
        ps = this.connection.prepareStatement("Update part SET p_available_quantity=? WHERE p_id =" + partID);
        ps.setInt(1, rs.getInt("p_available_quantity") - 1);
        ps.executeUpdate();

        System.out.printf("Product: %s(id: %d) Remaining Quantity: %d\n",
                rs.getString("p_name"), partID, rs.getInt("p_available_quantity") - 1);
    }
}