package com.pluralsight;

import com.pluralsight.dao.ShippersDao;
import org.apache.commons.dbcp2.BasicDataSource;

public class App {

    public static void main(String[] args) {

        if (args.length != 2) {
            System.out.println("Usage: java northwind <username> <password>");
            return;
        }

        String username = args[0];
        String password = args[1];

        // Set up database connection using Apache DBCP2 connection pool
        BasicDataSource dataSource = new BasicDataSource();
        dataSource.setUrl("jdbc:mysql://localhost:3306/northwind");

        dataSource.setUsername(username);
        dataSource.setPassword(password);

        // Create DAO and UI objects
        ShippersDao shippersDao = new ShippersDao(dataSource);
        UserInterface ui = new UserInterface(shippersDao);

        // Start the menu
        ui.displayMenu();
    }
}
