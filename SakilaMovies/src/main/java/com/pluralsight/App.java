package com.pluralsight;

import com.pluralsight.dao.ActorDao;

import com.pluralsight.dao.FilmDao;

import com.pluralsight.models.Actor;

import com.pluralsight.models.Film;

import org.apache.commons.dbcp2.BasicDataSource;

import java.sql.SQLException;

import java.util.List;

import java.util.Scanner;

public class App {

    public static void main(String[] args) {

        // Require username and password from command-line arguments
        if (args.length != 2) {
            System.out.println("Usage: java SakilaMovies <username> <password>");
            return;
        }

        String username = args[0];
        String password = args[1];

        // Set up database connection using Apache DBCP2 connection pool
        BasicDataSource dataSource = new BasicDataSource();
        dataSource.setUrl("jdbc:mysql://localhost:3306/sakila");

        dataSource.setUsername(username);
        dataSource.setPassword(password);

        // Set up DAO objects
        ActorDao actorDao = new ActorDao(dataSource);
        FilmDao filmDao = new FilmDao(dataSource);

        // Use try-with-resources for Scanner (auto-closes)
        try (Scanner scanner = new Scanner(System.in)) {

            // Prompt user for actor's last name
            System.out.print("Enter actor's last name to search: ");
            String lastName = scanner.nextLine();

            // Search for actors with that last name
            List<Actor> actors = actorDao.searchActorsByLastName(lastName);

            // if no results found
            if (actors.isEmpty()) {
                System.out.println("No actors found with last name: " + lastName);
                return;
            }

            // Display matching actors in a table format
            System.out.println("\nMatching actors:");
            System.out.printf("%-5s %-15s %-15s\n", "ID", "First Name", "Last Name");
            System.out.println("----------------------------------------");

            for (Actor actor : actors) {
                System.out.printf("%-5d %-15s %-15s\n",
                        actor.getActorId(),
                        actor.getFirstName(),
                        actor.getLastName());
            }

            // Prompt for actor ID
            System.out.print("\nEnter actor ID to see their films: ");
            int actorId = Integer.parseInt(scanner.nextLine());

            // Retrieve films for the selected actor
            List<Film> films = filmDao.getFilmsByActorId(actorId);

            // Display film list or a no-results message
            if (films.isEmpty()) {
                System.out.println("No films found for this actor.");
            } else {
                System.out.println("\nFilms:");
                System.out.printf("%-30s %-6s %-7s %s\n", "Title", "Year", "Length", "Description");
                System.out.println("--------------------------------------------------------------------------");

                int count = 1;
                for (Film film : films) {

                    // Optionally truncate long descriptions for neat output
                    String desc = film.getDescription();
                    if (desc.length() > 50) {
                        desc = desc.substring(0, 47) + "...";
                    }
                    System.out.printf("%2d. %-30s %-6s %-7d %s\n",
                            count++,

                            film.getTitle(),
                            film.getReleaseYear(),
                            film.getLength(),

                            desc);
                }
            }
        } catch (SQLException e) {

            // Handle SQL/database errors
            System.out.println("Database error occurred:");
            e.printStackTrace();

        } catch (NumberFormatException e) {

            // Handle invalid actor ID input
            System.out.println("Invalid input: Please enter a valid number for actor ID.");
        }
    }
}
