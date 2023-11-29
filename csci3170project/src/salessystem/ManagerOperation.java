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
import java.text.SimpleDateFormat;

public class ManagerOperation extends BasicOperation{
    private Connection connection;
    
    // Constructs a new ManagerOperation
    public ManagerOperation(Connection connection){
        this.connection = connection;
    }
    
    // The main function of ManagerOperation
    public void start() throws Exception{
        displayManagerMenu();
    }
    
    // Disply the Manager manu
    private void displayManagerMenu() throws Exception{
        boolean isExit = false;
        while (!isExit){
            System.out.println("\n-----Operations for manager menu-----");
            System.out.println("What kinds of operations would you like to perform?");
            System.out.println("1. List all salespersons");
            System.out.println("2. Count the no. of sales record of each salesperson under a specific range on years of experience");
            System.out.println("3. Show the total sales value of each manufacturer");
            System.out.println("4. Show the N most popular part");
            System.out.println("5. Return to the main menu");
            
            isExit = selectOp();
        }
    }
    
    // Logic for selecting each operation
    private boolean selectOp() throws Exception{
        System.out.print("Enter Your Choice: ");
        boolean isExit = false;
        
        int choice = getInputInteger();
        if (choice < 0){
            return isExit;
        }
        
        switch (choice){
            case 1: 
                listSalespersons();
                break;
            case 2:
                countNoOfSales();
                break;
            case 3:
                showTotalsales();
                break;
            case 4:
                showNPopularPart();
                break;
            case 5:
                isExit = true;
                break;
            default:
                System.out.println("Error: Input must be within 1 tp 5");
        }
        
        return isExit;
    }
    
    // Operation 1: List all salespersons
    private void listSalespersons() throws Exception{
        boolean isChoiceExit = false;
        while (!isChoiceExit){
            System.out.println("Choose ordering: ");
            System.out.println("1. By ascending order");
            System.out.println("1. By descending order");
            System.out.print("Choose the list ordering: ");
            int order = getInputInteger();
            String orderChoice = ""; 

            switch (order){
                case 1:
                    orderChoice = "ASC";
                    isChoiceExit = true;
                    break;
                case 2:
                    orderChoice = "DESC";
                    isChoiceExit = true;
                    break;
                default:
                    System.out.println("Error: Input must be within 1 tp 2");
                    continue;
            }
        String sqlQuery = "SELECT s.s_id, s.s_name, s.s_phone_number, s.s_experience " + 
                "FROM salesperson s " + 
                "ORDER BY s.s_experience " + orderChoice;
        
        Statement statement = this.connection.createStatement();
        ResultSet resultSet = statement.executeQuery(sqlQuery);
        
        System.out.println("| ID | Name | Mobile Phone | Years of Experience |");
        while (resultSet.next()){
            int sId = resultSet.getInt("s_id");
            String sName = resultSet.getString("s_name");
            int sMP = resultSet.getInt("s_phone_number");
            int sYOE = resultSet.getInt("s_experience");
            
            // Print the result
            System.out.printf("| %d | %s | %d | %d |\n",
            sId, sName, sMP, sYOE);
        }
        }
    }
    
    // By ascending order
    private void ascendingO() throws Exception {

    }

    // By descending order
    private void descendingO() throws Exception {
        String sqlQuery = "SELECT s.s_id, s.s_name, s.s_phone_number, s.s_experience " + 
                "FROM salesperson s " + 
                "ORDER BY s.s_experience DESC";
        
        Statement statement = this.connection.createStatement();
        ResultSet resultSet = statement.executeQuery(sqlQuery);
        
        System.out.println("| ID | Name | Mobile Phone | Years of Experience |");
        while (resultSet.next()){
            int sId = resultSet.getInt("s_id");
            String sName = resultSet.getString("s_name");
            int sMP = resultSet.getInt("s_phone_number");
            int sYOE = resultSet.getInt("s_experience");
            
            // Print the result
            System.out.printf("| %d | %s | %d | %d |\n",
            sId, sName, sMP, sYOE);
        }        
    }
    
    // Operation 2: Count the no. of sales record of each salesperson under a specific range on years of experience
    private void countNoOfSales() throws Exception{
        int lower, upper;
        while (true){
        System.out.println("Type in the lower bound for years of experience: ");
        lower = getInputInteger();
        System.out.println("Type in the upper bound for years of experience: ");
        upper = getInputInteger();
        
            if(lower > upper || lower < 0){
                System.out.println("Invalid input. Please enter a correct range.");
                continue;
            } else {
                break;
            }
        }
        
        String sqlQuery = "SELECT t.s_id, s.s_name, s.s_experience, COUNT(*) AS count " + 
                "FROM (SELECT s2.s_id FROM salesperson s2 WHERE s2.s_experience >= '%" + lower + "%' AND s2.s_experience <= '%" + upper + "%' ) temp, salesperson s, transaction t " +
                //"JOIN transaction t ON temp.s_id = t.s_id " +
                //"JOIN salesperson s ON t.s_id = s.s_id " +
                "WHERE t.s_id = temp.s_id AND t.s_id = s.s_id " +
                "GROUP t.s_id " +
                "ORDER BY t.s_id DESC";
        
        Statement statement = this.connection.createStatement();
        ResultSet resultSet = statement.executeQuery(sqlQuery);
        
        System.out.println("| ID | Name | Years of Experience | Number of Transaction |");
        while (resultSet.next()){
            int sId = resultSet.getInt("s_id");
            String sName = resultSet.getString("s_name");
            int sYOE = resultSet.getInt("s_experience");
            int sNOT = resultSet.getInt("count");
            
            // Print the result
            System.out.printf("| %d | %s | %d | %d |\n",
            sId, sName, sYOE, sNOT);
        }          
        System.out.println("End of Query");
    }

    // Operation 3: Show the total sales value of each manufacturer
    private void showTotalsales() throws Exception{
        String sqlQuery = "SELECT m.m_id, m.m_name, SUM(p.p_price) AS total " + 
                "FROM part p " +
                "JOIN transction t ON p.p_id = t.p_id " +
                "JOIN manufacturer m ON p.m_id = m.m_id " +
                "GROUP m.m_id " +
                "ORDER BY total DESC";
                //"ORDER BY SUM(p.p_price) DESC";
        
        Statement statement = this.connection.createStatement();
        ResultSet resultSet = statement.executeQuery(sqlQuery);
                
        System.out.println("| Manufacturer ID | Manufacturer Name | Total Sales Value |");
        while (resultSet.next()){
            int mid = resultSet.getInt("m_id");
            String mName = resultSet.getString("m_name");
            int totalSales = resultSet.getInt("total");
            
            // Print the result
            System.out.printf("| %d | %s | %d |\n",
            mid, mName, totalSales);
        }           
        System.out.println("End of Query");
    }

    // Operation 4: Show the N most popular part
    private void showNPopularPart() throws Exception{
        int N;
        
        // Get the N
        while(true){
            System.out.println("Type in the number of parts: ");
            N = getInputInteger();
            
            if(N <= 0){
                System.out.println("Invalid input. Please enter a positive integer.");
                continue;
            } else {
                break;
            }
        }

        String sqlQuery = "SELECT p.p_id, p.p_name, COUNT(*) AS count " + 
                "FROM part p " +
                "JOIN transction t ON p.p_id = t.p_id " +
                "GROUP p.p_id " +
                "ORDER BY count DESC " +
                //"ORDER BY COUNT(*) DESC";
                "FETCH FIRST '%" + N + "%' ROWS ONLY";
        
        Statement statement = this.connection.createStatement();
        ResultSet resultSet = statement.executeQuery(sqlQuery);
                
        System.out.println("| Part ID | Part Name | No. of Transaction |");
        while (resultSet.next()){
            int pid = resultSet.getInt("p_id");
            String pName = resultSet.getString("p_name");
            int numberTrans = resultSet.getInt("count");
            
            // Print the result
            System.out.printf("| %d | %s | %d |\n",
            pid, pName, numberTrans);
        }   
        
        System.out.println("End of Query");
    }

    
    public static void main(String[] args) {
        // TODO code application logic here
    }
}

