package client;

import java.sql.Connection;
import java.sql.DriverManager;

import salessystem.SalesSystem;

public class App {
    public static void main(String[] args) {
        // for local testing
        // String url = "jdbc:mysql://127.0.0.1:3306/app";
        // String user = "mysql";
        // String password = "mysql";

        // for production
        String url = "jdbc:mysql://projgw.cse.cuhk.edu.hk:2633/db45";
        String user = "Group45";
        String password = "CSCI3170";

        try {
            Connection connection = DriverManager.getConnection(url, user, password);
            SalesSystem system = new SalesSystem(connection);
            system.execute();

            connection.close();
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
    }
}
