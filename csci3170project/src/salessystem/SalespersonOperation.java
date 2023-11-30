package salessystem;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Scanner;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.text.SimpleDateFormat;
import java.text.DateFormat;

public class SalespersonOperation extends BasicOperation {
    private Connection connection;

    public SalespersonOperation(Connection connection) {
        this.connection = connection;
    }

    // The main function of SalespersonOperation
    public void start() throws Exception {
        displaySalespersonMenu();
    }

    //Display salesperson menu
    private void displaySalespersonMenu() throws Exception {
        boolean isExit = false;
        while (!isExit) {
            System.out.println("\n-----Operations for salesperson menu-----");
            System.out.println("What kinds of operation would you like to perform?");
            System.out.println("1. Search for parts");
            System.out.println("2. Sell a part");
            System.out.println("3. Return to the main menu");

            isExit = selectOp();
        }
    }


    //operation selecting on menu
    private boolean selectOp() throws Exception {
        System.out.print("Enter Your Choice: ");
        boolean isExit = false;

        int choice = getInputInteger();
        if (choice < 0) {
            return isExit;
        }

        switch (choice) {
            case 1:
            searchForParts();
            break;
            case 2:
            performTransaction();
            break;
            case 3:
            isExit = true;
            break;
            default:
                System.out.println("Error: Input must be within 1 to 3");
        }
        return isExit;
    }

    //operation 1: search for parts, choosing search criterion
    private void searchForParts() throws Exception {
        boolean isChoiceExit = false;
        while (!isChoiceExit) {
            System.out.println("Choose the Search Criterion:");
            System.out.println("1. Search by part name");
            System.out.println("2. Search by part category");
            
            System.out.print("Choose the Search Criterion: ");
            int criterionChoice = getInputInteger();

            switch (criterionChoice) {
                case 1:
                searchByPartName();
                isChoiceExit = true;
                break;
                case 2:
                searchByManufacturerName();
                isChoiceExit = true;
                break;
                default:
                    System.out.println("Error: Input must be within 1 to 2.");
                    continue;
            }
        }

    }

    //searching by part name
    private void searchByPartName() throws Exception {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Type in the search Keyword: ");
        String searchKeyword = scanner.nextLine();
        //check for empty input
        while (searchKeyword.trim().isEmpty()) {
            System.out.println("Invalid input. Please enter a non-empty search keyword.");
            System.out.print("Type in a search keyword: ");
            searchKeyword = scanner.nextLine();
        }

        //get ordering choice
        int searchOrder = 0;

        while (true) {
            System.out.println("Choose ordering: ");
            System.out.println("1. By price, ascending order");
            System.out.println("2. By price, descending order");
            System.out.print("Choose the search criterion: ");
            searchOrder = getInputInteger();

            if (searchOrder !=1 && searchOrder !=2 ) {
                System.out.println("Invalid input. Please enter a non-empty search keyword.");
                continue;
            } else {
                break;
            }
        }

        String sqlQuery = "";

        if (searchOrder == 1) {
            sqlQuery = "SELECT p.p_id, p.p_name, m.m_name, c.c_name, p.p_available_quantity, p.p_warranty_period, p.p_price " +
            "FROM part p " +
            "JOIN manufacturer m ON p.m_id = m.m_id " +
            "JOIN category c ON p.c_id = c.c_id " +
            "WHERE p.p_name LIKE '%" + searchKeyword + "%' " +
            "ORDER BY p.p_price ASC";
        } else if (searchOrder == 2) {
            sqlQuery = "SELECT p.p_id, p.p_name, m.m_name, c.c_name, p.p_available_quantity, p.p_warranty_period, p.p_price " +
            "FROM part p " +
            "JOIN manufacturer m ON p.m_id = m.m_id " +
            "JOIN category c ON p.c_id = c.c_id " +
            "WHERE p.p_name LIKE '%" + searchKeyword + "%' " +
            "ORDER BY p.p_price DESC";
        }
        
        Statement statement = this.connection.createStatement();
        ResultSet resultSet = statement.executeQuery(sqlQuery);

        System.out.println("| ID | Name | Manufacturer | Category | Qunatity | Warranty | Price |");
        
        while (resultSet.next()) {
            int p_id = resultSet.getInt("p_id");
            String p_name = resultSet.getString("p_name");
            String m_name = resultSet.getString("m_name");
            String c_name = resultSet.getString("c_name");
            int p_available_quantity = resultSet.getInt("p_available_quantity");
            int p_warranty_period = resultSet.getInt("p_warranty_period");
            int p_price = resultSet.getInt("p_price");

            //print search result
            System.out.printf("| %d | %s | %s | %s | %d | %d | %d |\n", 
            p_id, p_name, m_name, c_name, p_available_quantity, p_warranty_period, p_price);

        }
        
        System.out.println("End of Query");
    }

    //searching by manufacturer name
    private void searchByManufacturerName() throws Exception {
       Scanner scanner = new Scanner(System.in);
        System.out.print("Type in the search Keyword: ");
        String searchKeyword = scanner.nextLine();
        //check for empty input
        while (searchKeyword.trim().isEmpty()) {
            System.out.println("Invalid input. Please enter a non-empty search keyword.");
            System.out.print("Type in a search keyword: ");
            searchKeyword = scanner.nextLine();
        }

        //get ordering choice
        int searchOrder;
        while (true) {
            System.out.println("Choose ordering: ");
            System.out.println("1. By price, ascending order");
            System.out.println("2. By price, descending order");
            System.out.print("Choose the search criterion: ");
            searchOrder = getInputInteger();

            if (searchOrder !=1 && searchOrder !=2) {
                System.out.println("Invalid input. Please enter a non-empty search keyword.");
                continue;
            } else {
                break;
            }
        }

        String sqlQuery = "";

        if (searchOrder == 1) {
            sqlQuery = "SELECT p.p_id, p.p_name, m.m_name, c.c_name, p.p_available_quantity, p.p_warranty_period, p.p_price " +
            "FROM part p " +
            "JOIN manufacturer m ON p.m_id = m.m_id " +
            "JOIN category c ON p.c_id = c.c_id " +
            "WHERE m.m_name LIKE '%" + searchKeyword + "%' " +
            "ORDER BY p.p_price ASC";
        } else if (searchOrder == 2) {
            sqlQuery = "SELECT p.p_id, p.p_name, m.m_name, c.c_name, p.p_available_quantity, p.p_warranty_period, p.p_price " +
            "FROM part p " +
            "JOIN manufacturer m ON p.m_id = m.m_id " +
            "JOIN category c ON p.c_id = c.c_id " +
            "WHERE p.p_name LIKE '%" + searchKeyword + "%' " +
            "ORDER BY p.p_price DESC";
        }
        
        Statement statement = this.connection.createStatement();
        ResultSet resultSet = statement.executeQuery(sqlQuery);

        System.out.println("| ID | Name | Manufacturer | Category | Qunatity | Warranty | Price |");
        
        while (resultSet.next()) {
            int p_id = resultSet.getInt("p_id");
            String p_name = resultSet.getString("p_name");
            String m_name = resultSet.getString("m_name");
            String c_name = resultSet.getString("c_name");
            int p_available_quantity = resultSet.getInt("p_available_quantity");
            int p_warranty_period = resultSet.getInt("p_warranty_period");
            int p_price = resultSet.getInt("p_price");

            //print search result
            System.out.printf("| %d | %s | %s | %s | %d | %d | %d |\n", 
            p_id, p_name, m_name, c_name, p_available_quantity, p_warranty_period, p_price);

        }
        
        System.out.println("End of Query");

    }

    //operation 2: perform transaction
    private void performTransaction() throws Exception {
        while (true) {
            System.out.print("Enter The Part ID: ");
            int pChoice = getInputInteger();

            System.out.print("Enter The Salesperson ID: ");
            int sChoice = getInputInteger();

            //check part availablity
            String sqlQuery = "SELECT * FROM part WHERE p_id = " + pChoice + "";

            Statement statement = this.connection.createStatement();
            ResultSet resultSet = statement.executeQuery(sqlQuery);

            //-------ERROR1: cannot get valid result set ^^^^^-------

            int p_available_quantity = resultSet.getInt("p_available_quantity");
            String p_name = resultSet.getString("p_name");


            if (p_available_quantity > 0) {
                //insert transacion

                LocalDate dateObj = LocalDate.now();
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
                String date = dateObj.format(formatter);
                //-----ERROR2: need to change to Date format ^^^^------
                

                String insertQ = "INSERT INTO transaction (t_id, t_date, p_id, s_id) VALUES (?, ?, ?, ?)";
                int newp_available_quantity = p_available_quantity -1;

                int newtid = 0;
                ResultSet tidRs = statement.executeQuery("SELECT COUNT(*) FROM transaction");
                newtid = tidRs.getInt(1);
                newtid = newtid + 1;

                //-----ERROR3: cannot get valid result set ^^^^^-------
                //Below are not yet tested since i cannot get valid results

                PreparedStatement stmt = this.connection.prepareStatement(insertQ);
                
                stmt.setInt(1,newtid);
                stmt.setDate(2, date);
                stmt.setInt(3,pChoice);
                stmt.setInt(4, sChoice);
                stmt.executeUpdate();


                //Update part available quantity
                String updateQ = String.format("UPDATE part SET p_available_quantity = %d WHERE p_id = %d", 
                newp_available_quantity, pChoice);
                statement.executeUpdate(updateQ);
               
                //print out message
                System.out.printf("Product: %s(id: %d) Remaining Quality: %d",
                p_name, pChoice, newp_available_quantity);
                break;
            }
            else { //warning message
            System.out.println("The part is not available.");
            System.out.println("Please enter available part.");
            continue;
            }
        }
    }

}