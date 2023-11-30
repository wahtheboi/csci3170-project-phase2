package salessystem;

import java.sql.Connection;

public class SalesSystem extends BasicOperation {
    private Connection connection;

    public SalesSystem(Connection connection) {
        this.connection = connection;
    }

    // The starting point of the SalesSystem
    public void execute() throws Exception {
        System.out.println("Welcome to sales system!");
        displayMainMenu();
    }

    // Display the main menu for chooing operations
    private void displayMainMenu() throws Exception {
        boolean isExit = false;
        while (!isExit) {
            System.out.println("\n-----Main Menu-----");
            System.out.println("What kinds of operation would you like to perform?");
            System.out.println("1. Operations for administrator");
            System.out.println("2. Operations for salesperson");
            System.out.println("3. Operations for manager");
            System.out.println("4. Exit this program");

            isExit = selectRole();
        }
    }

    // Logic for selecting each operation
    private boolean selectRole() throws Exception {
        System.out.print("Enter Your Choice: ");
        boolean isExit = false;

        int choice = getInputInteger();
        if (choice < 0) {
            return isExit;
        }

        switch (choice) {
            case 1:
                AdministratorOperation admin = new AdministratorOperation(connection);
                admin.start();
                break;
            case 2:
                SalespersonOperation sales = new SalespersonOperation(connection);
                sales.start();
                break;
            // case 3:
            // ManagerOperation head = new ManagerOperation(db);
            // head.start();
            // break;
            case 4:
                isExit = true;
                break;
            default:
                System.out.println("ERROR!! Input must be within 1 to 4!");
                break;
        }
        return isExit;
    }
}
