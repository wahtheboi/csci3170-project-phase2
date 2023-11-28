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
            case 1;
            searchForParts();
            break;
            case 2;
            performTransaction();
            break;
            case 3;
            isExit = true;
            break;
            default:
                System.out.println("Error: Input must be within 1 to 3");
        }
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
                case 1;
                searchByPartName();
                isChoiceExit = true;
                break;
                case 2;
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
        while (true) {
            System.out.println("Choose ordering: ");
            System.out.println("1. By price, ascending order");
            System.out.println("2. By price, descending order");
            System.out.print("Choose the search criterion: ");
            int searchOrder = getInputInteger();

            if (searchOrder !=1 || searchOrder !=2) {
                System.out.println("Invalid input. Please enter a non-empty search keyword.");
                continue;
            } else {
                break;
            }
        }

        if (searchOrder == 1) {
            String sqlQuery = "SELECT p.pID, p.pName, m.mName, c.cName, p.pAvailableQuantity, p.pWarrantyPeriod, p.pPrice " +
            "FROM part p " +
            "JOIN manufacturer m ON p.mID = m.mID " +
            "JOIN category c ON p.cID = c.cID " +
            "WHERE p.pName LIKE '%" + searchKeyword + "%' " +
            "ORDER BY p.pPrice ASC";
        } else if (searchOrder == 2) {
            String sqlQuery = "SELECT p.pID, p.pName, m.mName, c.cName, p.pAvailableQuantity, p.pWarrantyPeriod, p.pPrice " +
            "FROM part p " +
            "JOIN manufacturer m ON p.mID = m.mID " +
            "JOIN category c ON p.cID = c.cID " +
            "WHERE p.pName LIKE '%" + searchKeyword + "%' " +
            "ORDER BY p.pPrice DESC";
        }
        
        Statement statement = this.connection.createStatement();
        ResultSet resultSet = statement.executeQuery(sqlQuery);

        System.out.println("| ID | Name | Manufacturer | Category | Qunatity | Warranty | Price |");
        
        while (resultSet.next()) {
            int pID = resultSet.getInt("pID");
            String pName = resultSet.getString("pName");
            String mName = resultSet.getString("mName");
            String cName = resultSet.getString("cName");
            int pAvailableQuantity = resultSet.getInt("pAvailableQuantity");
            int pWarrantyPeriod = resultSet.getInt("pWarrantyPeriod");
            int pPrice = resultSet.getInt("pPrice");

            //print search result
            System.out.printf("| %d | %s | %s | %s | %d | %d | %d |\n", 
            pID, pName, mName, cName, pAvailableQuantity, pWarrantyPeriod, pPrice);

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
        while (true) {
            System.out.println("Choose ordering: ");
            System.out.println("1. By price, ascending order");
            System.out.println("2. By price, descending order");
            System.out.print("Choose the search criterion: ");
            int searchOrder = getInputInteger();

            if (searchOrder !=1 || searchOrder !=2) {
                System.out.println("Invalid input. Please enter a non-empty search keyword.");
                continue;
            } else {
                break;
            }
        }

        if (searchOrder == 1) {
            String sqlQuery = "SELECT p.pID, p.pName, m.mName, c.cName, p.pAvailableQuantity, p.pWarrantyPeriod, p.pPrice " +
            "FROM part p " +
            "JOIN manufacturer m ON p.mID = m.mID " +
            "JOIN category c ON p.cID = c.cID " +
            "WHERE m.mName LIKE '%" + searchKeyword + "%' " +
            "ORDER BY p.pPrice ASC";
        } else if (searchOrder == 2) {
            String sqlQuery = "SELECT p.pID, p.pName, m.mName, c.cName, p.pAvailableQuantity, p.pWarrantyPeriod, p.pPrice " +
            "FROM part p " +
            "JOIN manufacturer m ON p.mID = m.mID " +
            "JOIN category c ON p.cID = c.cID " +
            "WHERE p.pName LIKE '%" + searchKeyword + "%' " +
            "ORDER BY p.pPrice DESC";
        }
        
        Statement statement = this.connection.createStatement();
        ResultSet resultSet = statement.executeQuery(sqlQuery);

        System.out.println("| ID | Name | Manufacturer | Category | Qunatity | Warranty | Price |");
        
        while (resultSet.next()) {
            int pID = resultSet.getInt("pID");
            String pName = resultSet.getString("pName");
            String mName = resultSet.getString("mName");
            String cName = resultSet.getString("cName");
            int pAvailableQuantity = resultSet.getInt("pAvailableQuantity");
            int pWarrantyPeriod = resultSet.getInt("pWarrantyPeriod");
            int pPrice = resultSet.getInt("pPrice");

            //print search result
            System.out.printf("| %d | %s | %s | %s | %d | %d | %d |\n", 
            pID, pName, mName, cName, pAvailableQuantity, pWarrantyPeriod, pPrice);

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
            String sqlQuery = "SELECT pID, pName, pAvailableQuantity FROM part WHERE pID = " + pChoice;
            Statement statement = this.connection.createStatement();
            ResultSet resultSet = statement.executeQuery(sqlQuery);

            int pAvailableQuantity = resultSet.getInt("pAvailableQuantity");
            int pName = resultSet.getString("pName");
            if (pAvailableQuantity > 0) {
                //insert transacion
                Statement statement = this.connection.createStatement();

                LocalDate dateObj = LocalDate.now();
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy");
                String date = dateObj.format(formatter);

                ResultSet rs = statement.executeQuery("SELECT MAX(tID) FROM transaction");
                int newtID = rs.getInt("tID") + 1;

                String insertQ = "INSERT INTO transaction (tID, tDate, pID, sID) VALUES (?, ?, ?, ?)";
                int newpAvailableQuantity = pAvailableQuantity -1;
                
                PreparedStatement stmt = this.connection.prepareStatement(insertQ);
                stmt.setInt(1,newtID);
                stmt.setString(2, date);
                stmt.setInt(3,pChoice);
                stmt.setInt(4, sChoice);
                stmt.executeUpdate();


                //Update part available quantity
                String updateQ = String.format("UPDATE part SET pAvailableQuantity = %d WHERE pID = %d", 
                newpAvailableQuantity, pChoice);
                statement.executeUpdate(updateQ);
               
                //print out message
                System.out.printf("Product: %s(id: %d) Remaining Quality: %d",
                pName, pChoice, newpAvailableQuantity);
                break
            }
            else { //warning message
            System.out.println("The part is not available.");
            System.out.println("Please enter available part.");
            continue;
            }
        }
    }

}