package com.pluralsight;

import java.sql.*;
import java.util.Scanner;

public class App {
    static Scanner myScanner = new Scanner(System.in);

    public static void main(String[] args) {


        if (args.length != 2) {
            System.out.println(
                    "Application needs two arguments to run: " +
                            "java com.hca.jdbc.UsingDriverManager <username> <password>");
            System.exit(1);
        }

        // get the username and password from the command line args
        String username = args[0];
        String password = args[1];


        try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/northwind", username, password)) {

            while (true) {

                System.out.println("What do you want to do?");
                System.out.println("\t1) Display all products");
                System.out.println("\t2) Display all customers");
                System.out.println("\t3) Display all categories");
                System.out.println("\t4) Display products by category");
                System.out.println("\t0) Exit");
                System.out.print("Select an option: ");

                switch (myScanner.nextInt()) {
                    case 1:
                        displayAllProducts(connection);
                        break;
                    case 2:
                        displayAllCustomers(connection);
                        break;
                    case 3:
                        displayAllCategories(connection);
                        break;
                    case 4:
                        displayProductsByCategory(connection);
                        break;
                    case 0:
                        System.out.println("Bye Bye!");
                        System.exit(0);
                    default:
                        System.out.println("Invalid Choice");
                }

            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

    public static void displayAllCustomers(Connection connection) {

        try (
                // initialize the preparedStatement
                PreparedStatement preparedStatement = connection.prepareStatement(
                        "SELECT " +
                                "   ContactName, " +
                                "   CompanyName, " +
                                "   City, " +
                                "   Country, " +
                                "   Phone  " +
                                "FROM " +
                                "   Customers " +
                                "ORDER BY " +
                                "   Country");

                // execute the query
                ResultSet resultSet = preparedStatement.executeQuery()
        ) {
            // loop through the results
            printResultSet(resultSet);

        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    public static void displayAllProducts(Connection connection) {

        try (
                // initialize the preparedStatement
                PreparedStatement preparedStatement = connection.prepareStatement(
                        "SELECT " +
                                "   ProductName, " +
                                "   UnitPrice, " +
                                "   UnitsInStock " +
                                "FROM " +
                                "   Products " +
                                "ORDER BY " +
                                "   ProductName");

                // execute the query
                ResultSet resultSet = preparedStatement.executeQuery()
        ) {
            // loop through the results
            printResultSet(resultSet);

        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    // Display All Categories
    public static void displayAllCategories(Connection connection) {

        try (
                // initialize the preparedStatement
                PreparedStatement preparedStatement = connection.prepareStatement(
                        "SELECT " +
                                "   CategoryID, " +
                                "   CategoryName " +
                                "FROM " +
                                "    Categories " +
                                "ORDER BY " +
                                "   CategoryID");

                // execute the query
                ResultSet resultSet = preparedStatement.executeQuery()
        ) {
            // loop through the results
            printResultSet(resultSet);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void displayProductsByCategory(Connection connection) {

        System.out.println("Enter a Category ID: ");
        int categoryId = myScanner.nextInt();
        myScanner.nextLine();

        String query = "SELECT ProductName,UnitPrice,UnitsInStock FROM Products WHERE CategoryID = ? ORDER BY ProductName";
        try (
                PreparedStatement preparedStatement = connection.prepareStatement(query)
        ) {
            // Set the ? parameter to the entered category id
            preparedStatement.setInt(1, categoryId);

            // execute query
            try (ResultSet rs = preparedStatement.executeQuery()) {
                printResultSet(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    // loop over the result set and print out the columns for each result
    public static void printResultSet(ResultSet rs) throws SQLException {
        ResultSetMetaData metaData = rs.getMetaData();
        int columnCount = metaData.getColumnCount();

        while (rs.next()) {
            for (int i = 1; i <= columnCount; i++) {
                String columnName = metaData.getColumnName(i);
                String value = rs.getString(i); // generic, works for most types
                System.out.print(columnName + ": " + value + "  ");
            }
            System.out.println(); // new line after each row
        }
    }

}
