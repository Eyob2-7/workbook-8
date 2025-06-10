package com.pluralsight;
import java.sql.*;

public class App {
    public static void main(String[] args) {

        if (args.length != 2) {
            System.out.println(
                    "Application needs two arguments to run: " +
                            "java com.pluralsight.UsingDriverManager <username> <password>"
            );
            System.exit(1);
        }

        // get the username and password
        String userName = args[0];
        String password = args[1];
        try {

            // 1. open a connection to the database
            Connection connection;
            connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/northwind", userName, password);


            // create statement
            Statement statement = connection.createStatement();

            // define your query
            String query = "SELECT ProductID, ProductName, UnitPrice, UnitsInStock FROM Products;";

            // 2. Execute your query
            ResultSet results = statement.executeQuery(query);

            // process the results
            while (results.next()) {
                int id = results.getInt("ProductID");
                String name = results.getString("ProductName");
                double price = results.getDouble("UnitPrice");
                int stock = results.getInt("UnitsInStock");

                System.out.println("----------------------");
                System.out.printf("Product Id: %d%nName: %s%nPrice: %.2f%nStock: %d%n",id,name,price,stock);
                System.out.println("----------------------");
            }

            // 3. Close the connection
            connection.close();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }


    }
}
