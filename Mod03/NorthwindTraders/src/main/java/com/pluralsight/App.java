package com.pluralsight;

import org.apache.commons.dbcp2.BasicDataSource;

import java.sql.*;
import java.util.Scanner;

public class App {
    /* The creation of the BasicDataSource is moved outside any try so that it stays open for the life of the program. Otherwise,
    it defeats the purpose of the connection pooling. If it's in the try, it can get closed earlier than you might want.
    * Instead of passing the connection to our methods that do the query, we pass the dataSource variable into the method.
    * We get the connection from using datasource.getConnection() inside each of those methods. This  grabs a connection from the connection pool for our query to use.
    * The book does not discuss this but with this approach, you do need to close the dataSource at some point. You can see happening this in my switch case that exists the program.
    * This also eliminates the need for a try catch around all the code in my main method
     */
    public static void main(String[] args) {

        // Did I pass command line arguments in at runtime
        if (args.length != 2) {
            System.out.println(
                    "Application needs two arguments to run: " +
                            "java com.hca.jdbc.UsingDriverManager <username> <password>");
            System.exit(1);
        }

        // get the username and password from the command line args
        String username = args[0];
        String password = args[1];

        Scanner myScanner = new Scanner(System.in);

        //create the datasource
        BasicDataSource dataSource = new BasicDataSource();

        // Configure the datasource
        dataSource.setUrl("jdbc:mysql://localhost:3306/northwind");
        dataSource.setUsername(username);
        dataSource.setPassword(password);

        while (true) {

            System.out.println("What do you want to do?");
            System.out.println("\t1) Display all products");
            System.out.println("\t2) Display all customers");
            System.out.println("\t3) Display all categories");
            System.out.println("\t0) Exit");
            System.out.print("Select an option: ");

            switch (myScanner.nextInt()) {
                case 1:
                    displayAllProducts(dataSource);
                    break;
                case 2:
                    displayAllCustomers(dataSource);
                    break;
                case 3:
                    displayAllCategories(dataSource);

                    //ask them if they want to see products for a specific category
                    System.out.println("Do you want to see the products in a category?");
                    System.out.println("\t1) Yes");
                    System.out.println("\t2) No");
                    System.out.print("Select an option: ");

                    switch (myScanner.nextInt()) {
                        case 1:

                            System.out.print("What category id would you like to view products for?: ");
                            int catID = myScanner.nextInt();
                            displayProductsByCategory(dataSource, catID);
                            break;

                        default:
                            System.out.println("Ok, back to the main menu then!");
                    }

                    break;
                case 0:
                    System.out.println("Bye Bye!");

                    //close the DataSource
                    try {
                        dataSource.close();
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                    System.exit(0);
                default:
                    System.out.println("Invalid Choice");
            }

        }

    }

    // Display all customers
    public static void displayAllCustomers(BasicDataSource datasource) {

        try (
                //get a new connection from the pool for this query
                Connection connection = datasource.getConnection();
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

    // Display all products
    public static void displayAllProducts(BasicDataSource datasource) {

        try (
                //get a new connection from the pool for this query
                Connection connection = datasource.getConnection();

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

    // Display all categories
    public static void displayAllCategories(BasicDataSource datasource) {

        try (
                //get a new connection from the pool for this query
                Connection connection = datasource.getConnection();
                // initialize the preparedStatement
                PreparedStatement preparedStatement = connection.prepareStatement(
                        "SELECT " +
                                "   CategoryID, " +
                                "   CategoryName " +
                                "FROM " +
                                "   Categories " +
                                "ORDER BY " +
                                "   CategoryId");

                // execute the query
                ResultSet resultSet = preparedStatement.executeQuery()
        ) {
            // loop through the results
            printResultSet(resultSet);

        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    // loop over the result set and print out the columns for each result
    public static void printResultSet(ResultSet rs) throws SQLException {
        ResultSetMetaData metaData = rs.getMetaData();
        int columnCount = metaData.getColumnCount();

        if (!rs.next()) {
            System.out.println("No results for Found!");
        }

        while (rs.next()) {
            for (int i = 1; i <= columnCount; i++) {
                String columnName = metaData.getColumnName(i);
                String value = rs.getString(i); // generic, works for most types
                System.out.print(columnName + ": " + value + "  ");
            }
            System.out.println(); // new line after each row
        }
    }

    public static void displayProductsByCategory(BasicDataSource datasource, int categoryId) {

        try (
                //get a new connection from the pool for this query
                Connection connection = datasource.getConnection();

                // initialize the preparedStatement
                PreparedStatement preparedStatement = connection.prepareStatement(
                        "SELECT " +
                                "   ProductName, " +
                                "   UnitPrice, " +
                                "   UnitsInStock " +
                                "FROM " +
                                "   Products " +
                                "WHERE" +
                                "   CategoryId = ? " +
                                "ORDER BY " +
                                "   ProductName");

        ) {

            preparedStatement.setInt(1, categoryId);

            // execute the query
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                // loop through the results
                printResultSet(resultSet);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

}
