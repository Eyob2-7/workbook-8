package com.pluralsight;

import com.pluralsight.dao.ShippersDao;
import com.pluralsight.models.Shippers;
import java.util.List;
import java.util.Scanner;

public class UserInterface {

    private final ShippersDao shippersDao;
    private final Scanner scanner;

    // Constructor that accepts the DAO for DB operations
    public UserInterface(ShippersDao shippersDao) {
        this.shippersDao = shippersDao;
        this.scanner = new Scanner(System.in);

    }

    // Entry point for interacting with the user
    public void displayMenu() {
        boolean running = true;

        while (running) {
            System.out.println("""

                \n=== Northwind Shippers Menu ===
                1. Add new shipper
                2. View all shippers
                3. Update phone number
                4. Delete a shipper
                0. Exit

                """);

            System.out.print("Enter option: ");
            int choice = Integer.parseInt(scanner.nextLine());

            switch (choice) {

                case 1 -> addShipper();
                case 2 -> showAllShippers();
                case 3 -> updatePhoneNumber();
                case 4 -> deleteShipper();
                case 0 -> {
                    System.out.println("Exiting... Goodbye!");
                    running = false;
                }
                default -> System.out.println("Invalid option. Try again.");
            }
        }
    }

    // 1. Prompt user for new shipper data and insert into DB
    private void addShipper() {

        System.out.print("Enter company name: ");
        String name = scanner.nextLine();
        System.out.print("Enter phone number: ");
        String phone = scanner.nextLine();
        int newId = shippersDao.insertShipper(name, phone);

        System.out.println("New shipper added with ID: " + newId);

    }

    // 2 Display all shippers
    private void showAllShippers() {
        List<Shippers> shippers = shippersDao.getAllShippers();
        System.out.println("\n--- All Shippers ---");
        for (Shippers shipper : shippers) {
            System.out.println(shipper);
        }
    }

    // 3. Prompt user to change phone number for a shipper
    private void updatePhoneNumber() {
        System.out.print("Enter shipper ID to update: ");
        int id = Integer.parseInt(scanner.nextLine());
        System.out.print("Enter new phone number: ");
        String phone = scanner.nextLine();
        shippersDao.updatePhoneNumber(id, phone);
        System.out.println("Phone number updated.");
    }

    // 4. Delete shipper
    private void deleteShipper() {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter the ID of the shipper to delete: ");
        int id = scanner.nextInt();
        scanner.nextLine();
        if (id <= 3) {
            System.out.println("❌ You are not allowed to delete default shippers with ID 1–3.");
            return;
        }
        boolean deleted = shippersDao.deleteShipperById(id);
        if (deleted) {
            System.out.println("✅ Shipper deleted successfully.");
        } else {
            System.out.println("❌ Failed to delete shipper. Check the ID and try again.");
        }
    }
}
