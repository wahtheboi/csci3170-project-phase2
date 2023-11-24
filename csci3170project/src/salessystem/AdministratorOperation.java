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

public class AdministratorOperation extends BasicOperation {
    private Connection connection;

    // Constructs a new AdministratorOperation
    public AdministratorOperation(Connection connection) {
        this.connection = connection;
    }

    // The main function of AdministratorOperation
    public void start() throws Exception {
        displayAdminMenu();
    }

    // Display the Administrator manu
    private void displayAdminMenu() throws Exception {
        boolean isExit = false;
        while (!isExit) {
            System.out.println("\n-----Operations for administrator menu-----");
            System.out.println("What kinds of operation would you like to perform?");
            System.out.println("1. Create all tables");
            System.out.println("2. Delete all tables");
            System.out.println("3. Load from datafile");
            System.out.println("4. Show content of a table");
            System.out.println("5. Return to the main menu");
            isExit = selectOp();
        }
    }

    // Logic for selecting each operation
    private boolean selectOp() throws Exception {
        System.out.print("Enter Your Choice: ");
        boolean isExit = false;

        int choice = getInputInteger();
        if (choice < 0) {
            return isExit;
        }

        switch (choice) {
            case 1:
                createTables();
                break;
            case 2:
                deleteTables();
                break;
            case 3:
                loadData();
                break;
            case 4:
                showContent();
                break;
            case 5:
                isExit = true;
                break;
            default:
                System.out.println("Error: Input must be within 1 to 5");
        }

        return isExit;
    }

    // Create all tables
    private void createTables() throws Exception {
        System.out.print("Processing...");
        Statement statement = this.connection.createStatement();
        statement.addBatch("CREATE TABLE category (c_id INTEGER PRIMARY KEY, c_name VARCHAR(20) UNIQUE NOT NULL);");
        statement.addBatch(
                "CREATE TABLE manufacturer (m_id INTEGER PRIMARY KEY, m_name VARCHAR(20) NOT NULL, m_address VARCHAR(50) NOT NULL, m_phone_number INTEGER NOT NULL);");
        statement.addBatch(
                "CREATE TABLE part (p_id INTEGER PRIMARY KEY, p_name VARCHAR(20) NOT NULL, p_price INTEGER NOT NULL, m_ID INTEGER NOT NULL, c_id INTEGER NOT NULL, p_warranty_period INTEGER NOT NULL, p_available_quantity INTEGER NOT NULL, FOREIGN KEY (m_id) REFERENCES manufacturer (m_id) ON DELETE CASCADE, FOREIGN KEY (c_id) REFERENCES category (c_id) ON DELETE CASCADE);");
        statement.addBatch(
                "CREATE TABLE salesperson (s_id INTEGER PRIMARY KEY, s_name VARCHAR(20) NOT NULL, s_address VARCHAR(50) NOT NULL, s_phone_number INTEGER NOT NULL, s_experience INTEGER NOT NULL);");
        statement.addBatch(
                "CREATE TABLE transaction (t_id INTEGER PRIMARY KEY, p_id INTEGER NOT NULL, s_id INTEGER NOT NULL, t_date DATE NOT NULL);");
        statement.executeBatch();

        System.out.println("Done! Database is initialized!");
    }

    // Delete all tables
    private void deleteTables() throws Exception {
        System.out.print("Processing...");
        Statement statement = this.connection.createStatement();
        statement.addBatch("DROP TABLE IF EXISTS transaction");
        statement.addBatch("DROP TABLE IF EXISTS salesperson");
        statement.addBatch("DROP TABLE IF EXISTS part");
        statement.addBatch("DROP TABLE IF EXISTS manufacturer");
        statement.addBatch("DROP TABLE IF EXISTS category");
        statement.executeBatch();
        System.out.println("Done! Database is removed!");
    }

    // Load data from source folder
    private void loadData() throws Exception {
        // Ask for source data folder path
        System.out.print("Type in the Source Data Folder Path: ");
        String folderName = inputReader.readLine();
        URL inputFileURL = getClass().getClassLoader().getResource(folderName);

        if (inputFileURL == null) {
            throw new FileNotFoundException("Error: File does not exist");
        }

        String folderPath = inputFileURL.getFile();
        File inputFile = new File(folderPath);

        if (inputFile.isFile()) {
            throw new IllegalArgumentException("Error: The input path is a file, please input a valid folder path");
        }

        System.out.print("Processing...");
        try (
                InputStream input = getClass().getClassLoader().getResourceAsStream(folderName);
                InputStreamReader inputReader = new InputStreamReader(input);
                BufferedReader fileReader = new BufferedReader(inputReader);) {
            String fileName;
            while ((fileName = fileReader.readLine()) != null) {
                String absoluteFilePath = Paths.get(folderPath, fileName).toString();
                this.processFileData(absoluteFilePath);
            }
        }

        System.out.println("Done! Data is inputted to the database!");
    }

    // Process a file content and perform insertions
    private void processFileData(String filepath) throws Exception {
        File file = new File(filepath);
        if (file.isDirectory()) {
            System.out.printf("Error: %s is a directory", file.getPath());
            return;
        }

        switch (file.getName()) {
            case "category.txt":
                insertCategoryDataFromFile(file.getPath());
                break;
            case "manufacturer.txt":
                insertManufacturerDataFromFile(file.getPath());
                break;
            case "part.txt":
                insertPartDataFromFile(file.getPath());
                break;
            case "salesperson.txt":
                insertSalespersonDataFromFile(file.getPath());
                break;
            case "transaction.txt":
                insertTransactionDataFromFile(file.getPath());
                break;
            default:
                System.out.printf("Error: Invalid file %s\n", file.getPath());
        }
    }

    // Insert the category data to the database
    private void insertCategoryDataFromFile(String filepath) throws Exception {
        String line;
        PreparedStatement statement = this.connection
                .prepareStatement("INSERT INTO category (c_id, c_name) VALUES (?, ?)");
        try (
                FileInputStream fs = new FileInputStream(filepath);
                InputStreamReader inputReader = new InputStreamReader(fs);
                BufferedReader fileReader = new BufferedReader(inputReader);) {
            while ((line = fileReader.readLine()) != null) {
                String[] record = line.split("\t");
                statement.setInt(1, Integer.valueOf(record[0]));
                statement.setString(2, record[1]);
                statement.addBatch();
            }
            statement.executeBatch();
        }
    }

    // Insert the manufacturer data to the database
    private void insertManufacturerDataFromFile(String filepath) throws Exception {
        String line;
        PreparedStatement statement = this.connection
                .prepareStatement(
                        "INSERT INTO manufacturer (m_id, m_name, m_address, m_phone_number) VALUES (?, ?, ?, ?)");
        try (
                FileInputStream fs = new FileInputStream(filepath);
                InputStreamReader inputReader = new InputStreamReader(fs);
                BufferedReader fileReader = new BufferedReader(inputReader);) {
            while ((line = fileReader.readLine()) != null) {
                String[] record = line.split("\t");
                statement.setInt(1, Integer.valueOf(record[0]));
                statement.setString(2, record[1]);
                statement.setString(3, record[2]);
                statement.setInt(4, Integer.valueOf(record[3]));
                statement.addBatch();
            }
            statement.executeBatch();
        }
    }

    // Insert the part data to the database
    private void insertPartDataFromFile(String filepath) throws Exception {
        String line;
        PreparedStatement statement = this.connection
                .prepareStatement(
                        "INSERT INTO part (p_id, p_name, p_price, m_id, c_id, p_warranty_period, p_available_quantity) VALUES (?, ?, ?, ?, ?, ?, ?)");
        try (
                FileInputStream fs = new FileInputStream(filepath);
                InputStreamReader inputReader = new InputStreamReader(fs);
                BufferedReader fileReader = new BufferedReader(inputReader);) {
            while ((line = fileReader.readLine()) != null) {
                String[] record = line.split("\t");
                statement.setInt(1, Integer.valueOf(record[0]));
                statement.setString(2, record[1]);
                statement.setInt(3, Integer.valueOf(record[2]));
                statement.setInt(4, Integer.valueOf(record[3]));
                statement.setInt(5, Integer.valueOf(record[4]));
                statement.setInt(6, Integer.valueOf(record[5]));
                statement.setInt(7, Integer.valueOf(record[6]));
                statement.addBatch();
            }
            statement.executeBatch();
        }
    }

    // Insert the salesperson data to the database
    private void insertSalespersonDataFromFile(String filepath) throws Exception {
        String line;
        PreparedStatement statement = this.connection
                .prepareStatement(
                        "INSERT INTO salesperson (s_id, s_name, s_address, s_phone_number, s_experience) VALUES (?, ?, ?, ?, ?)");
        try (
                FileInputStream fs = new FileInputStream(filepath);
                InputStreamReader inputReader = new InputStreamReader(fs);
                BufferedReader fileReader = new BufferedReader(inputReader);) {
            while ((line = fileReader.readLine()) != null) {
                String[] record = line.split("\t");
                statement.setInt(1, Integer.valueOf(record[0]));
                statement.setString(2, record[1]);
                statement.setString(3, record[2]);
                statement.setInt(4, Integer.valueOf(record[3]));
                statement.setInt(5, Integer.valueOf(record[4]));
                statement.addBatch();
            }
            statement.executeBatch();
        }
    }

    // Insert the transaction data to the database
    private void insertTransactionDataFromFile(String filepath) throws Exception {
        String line;
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        PreparedStatement statement = this.connection
                .prepareStatement("INSERT INTO transaction (t_id, p_id, s_id, t_date) VALUES (?, ?, ?, ?)");
        try (
                FileInputStream fs = new FileInputStream(filepath);
                InputStreamReader inputReader = new InputStreamReader(fs);
                BufferedReader fileReader = new BufferedReader(inputReader);) {
            while ((line = fileReader.readLine()) != null) {
                String[] record = line.split("\t");
                statement.setInt(1, Integer.valueOf(record[0]));
                statement.setInt(2, Integer.valueOf(record[1]));
                statement.setInt(3, Integer.valueOf(record[2]));
                statement.setDate(4, new Date(dateFormat.parse(record[3]).getTime()));
                statement.addBatch();
            }
            statement.executeBatch();
        }
    }

    // Show content of the corresponding database table
    private void showContent() throws Exception {
        System.out.print("Which table would you like to show: ");
        String choice = inputReader.readLine();

        if (!isValidTableName(choice)) {
            System.out.println("Error: " + choice + " is not a table in database.");
            return;
        }

        System.out.println("Content of table category:");
        switch (choice) {
            case "category":
                PreparedStatement statement = this.connection
                        .prepareStatement("SELECT * FROM category ORDER BY c_id ASC");
                ResultSet rs = statement.executeQuery();

                System.out.println("| c_id | c_name |");
                while (rs.next()) {
                    System.out.printf(
                            "| %d | %s |\n",
                            rs.getInt("c_id"),
                            rs.getString("c_name"));
                }
                break;
            case "manufacturer":
                statement = this.connection
                        .prepareStatement("SELECT * FROM manufacturer ORDER BY m_id ASC");
                rs = statement.executeQuery();

                System.out.println("| m_id | m_name | m_address | m_phone_number |");
                while (rs.next()) {
                    System.out.printf(
                            "| %d | %s | %s | %d |\n",
                            rs.getInt("m_id"),
                            rs.getString("m_name"),
                            rs.getString("m_address"),
                            rs.getInt("m_phone_number"));
                }
                break;
            case "part":
                statement = this.connection
                        .prepareStatement("SELECT * FROM part ORDER BY p_id ASC");
                rs = statement.executeQuery();

                System.out.println(
                        "| p_id | p_name | p_price | m_id | c_id | p_warranty_period | p_available_quantity |");
                while (rs.next()) {
                    System.out.printf(
                            "| %d | %s | %d | %d | %d | %d | %d |\n",
                            rs.getInt("p_id"),
                            rs.getString("p_name"),
                            rs.getInt("p_price"),
                            rs.getInt("m_id"),
                            rs.getInt("c_id"),
                            rs.getInt("p_warranty_period"),
                            rs.getInt("p_available_quantity"));
                }
                break;
            case "salesperson":
                statement = this.connection
                        .prepareStatement("SELECT * FROM salesperson ORDER BY s_id ASC");
                rs = statement.executeQuery();
                System.out.println("| s_id | s_name | s_address | s_phone_number | s_experience |");
                while (rs.next()) {
                    System.out.printf(
                            "| %d | %s | %s | %d | %d\n",
                            rs.getInt("s_id"),
                            rs.getString("s_name"),
                            rs.getString("s_address"),
                            rs.getInt("s_phone_number"),
                            rs.getInt("s_experience"));
                }
                break;
            case "transaction":
                statement = this.connection
                        .prepareStatement("SELECT * FROM transaction ORDER BY t_id ASC");
                rs = statement.executeQuery();
                SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
                System.out.println("| t_id | p_id | s_id | t_date |");
                while (rs.next()) {
                    System.out.printf(
                            "| %d | %d | %d | %s |\n",
                            rs.getInt("t_id"),
                            rs.getInt("p_id"),
                            rs.getInt("s_id"),
                            dateFormat.format((rs.getDate("t_date").getTime()))
                    // rs.getDate(new Date dateFormat.parse("t_date"))
                    );
                }
                break;
        }
    }
}
