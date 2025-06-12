package com.pluralsight;

import org.apache.commons.dbcp2.BasicDataSource;

import java.sql.*;
import java.util.Scanner;

public class App {

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
        dataSource.setUrl("jdbc:mysql://localhost:3306/sakila");
        dataSource.setUsername(username);
        dataSource.setPassword(password);

        // Menu loop
        while (true) {
            System.out.println("\n1. Search actors by last name");
            System.out.println("2. Show movies by full actor name");
            System.out.println("0.Exit");

            int choice;
            try {
                choice = myScanner.nextInt();
                myScanner.nextLine();
            } catch (Exception e) {
                System.out.println("Invalid input. Please enter a number.");
                myScanner.nextLine();
                continue;
            }

            switch (choice) {
                case 1:
                    showActorsByLastName(dataSource, myScanner);
                    break;
                case 2:
                    displayFilmsByActorName(dataSource, myScanner);
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

    // show actors who match a given last name
    public static void showActorsByLastName(BasicDataSource datasource, Scanner scanner) {
        System.out.println("Enter actor's last name: ");
        String lastName = scanner.nextLine().trim();
        String query = "SELECT     " +
                "       actor_id   " +
                "      ,first_name " +
                "      ,last_Name   " +
                "       FROM       " +
                "       Actor      " +
                "       WHERE      " +
                "       last_name = ?";

        //get a new connection from the pool for this query
        try (Connection connection = datasource.getConnection();
             // initialize the preparedStatement
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setString(1, lastName);
            ResultSet resultSet = preparedStatement.executeQuery();// execute the query

            // Check if the ResultSet has any rows before processing.
            // isBeforeFirst() returns true only if there are rows and the cursor is before the first row.
            // If false, it means the query returned no results.
            if (!resultSet.isBeforeFirst()) {
                System.out.println("No actors found");
                return;
            }
            System.out.println("\nActors found");
            printResultSet(resultSet);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Show all movies for a given actor's full name
    public static void displayFilmsByActorName(BasicDataSource datasource, Scanner scanner) {
        System.out.println("Enter actor's first name: ");
        String firstName = scanner.nextLine().trim();

        System.out.println("Enter actor's last name: ");
        String lastName = scanner.nextLine().trim();

        String query = """
                SELECT F.title
                FROM film F
                JOIN film_actor Fa ON Fa.film_id = F.film_id
                JOIN actor A ON A.actor_id =Fa.actor_id
                WHERE A.first_name = ? AND A.last_name = ?
                """;

        try (Connection connection = datasource.getConnection();
             // initialize the preparedStatement
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setString(1, firstName);
            preparedStatement.setString(2, lastName);

            ResultSet resultSet = preparedStatement.executeQuery();// execute the query

            if (!resultSet.isBeforeFirst()) {
                System.out.println("No movies found for that actor");
                return;
            }
            System.out.println("\nMovies featuring " + firstName + " " + lastName + ":");
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
}