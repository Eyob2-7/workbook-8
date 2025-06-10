package com.pluralsight;

import java.sql.*;
import java.util.Scanner;

public class App {
    public static void main(String[] args) {

        // Fire up scanner
        Scanner scanner = new Scanner(System.in);

        if (args.length != 2) {
            System.out.println("Application needs two arguments to run: " + "java com.pluralsight.UsingDriverManager <username> <password>");
            System.exit(1);
        }
        // assign the username and password
        String userName = args[0];
        String password = args[1];

        // Prompt
        System.out.println("What do you want to do?");
        System.out.println("1) Display all product");
        System.out.println("2) Display all customers");
        System.out.println("0) Exit");
        System.out.println("Select an option");

        int option = scanner.nextInt();
        scanner.nextLine();

        if (option == 0) {
            System.out.println("Goodbye");
            return;
        }

        // Declare JDBC variables that will be used to connect and query the database
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {

            // Establish a connection to the northwind database
            connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/northwind", userName, password);

            // a variable to hold the SQL query (string)
            String query;

            if (option == 1) {
                query = "SELECT ProductID, ProductName, UnitPrice FROM products ORDER BY ProductName;";
                System.out.println("\n                === Product List === ");
                System.out.println("+------------+-----------------------------+------------------+");
                System.out.printf("| %-10s | %-27s | %-8s |\n", "Product ID", "Product Name", "UnitPrice");
                System.out.println("+------------+-----------------------------+------------------+");
            } else if (option == 2) {
                query = "SELECT  ContactName, CompanyName, City, Country, Phone FROM customers ORDER BY country;";
                System.out.println("\n                                      === Customer List ===");

                System.out.println("+----------------------+-----------------------------+--------------------+---------------+--------------+");

                System.out.printf("| %-20s | %-27s | %-18s | %-13s | %-12s |\n",

                        "ContactName", "CompanyName", "City", "Country", "Phone");

                System.out.println("+----------------------+-----------------------------+--------------------+---------------+--------------+");
            } else {
                System.out.println("Invalid option");
                return;
            }

            // Sends the SQL query to the database and stores the returned data in resultSet
            preparedStatement = connection.prepareStatement(query);
            resultSet = preparedStatement.executeQuery();


            while (resultSet.next()) {
                if (option == 1) {
                    int id = resultSet.getInt("ProductID");
                    String name = resultSet.getString("ProductName");
                    double price = resultSet.getDouble("UnitPrice");
                    System.out.printf("| %-10d | %-27s | $%-7.2f |\n", id, name, price);
                } else if (option == 2) {
                    String contact = resultSet.getString("ContactName");
                    String company = resultSet.getString("CompanyName");
                    String city = resultSet.getString("City");
                    String country = resultSet.getString("Country");
                    String phone = resultSet.getString("Phone");
                    System.out.printf("| %-20s | %-27s | %-18s | %-13s | %-12s |\n", contact, company, city, country, phone);

                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            // close the resources
            if (resultSet != null) {
                try {
                    resultSet.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if (preparedStatement != null) {
                try {
                    preparedStatement.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }


            }
        }
    }
}
