package com.aiegoo.comicrental;

import java.util.Scanner;
import java.util.List;

/**
 * Simple terminal dashboard that presents menu options and
 * displays results in a formatted way. Uses only standard
 * Java classes so it can run without external dependencies.
 */
public class Dashboard {
    private final App app = new App();
    private final Scanner scanner;

    public Dashboard(Scanner scanner) {
        this.scanner = scanner;
    }

    public void run() {
        while (true) {
            printHeader();
            printMenu();
            System.out.print("Choose an option: ");
            String choice = scanner.nextLine().trim();
            if (choice.equals("0") || choice.equalsIgnoreCase("exit")) {
                System.out.println("Exiting dashboard.");
                break;
            }
            try {
                switch (choice) {
                    case "1" -> app.handle("comic-list", scanner);
                    case "2" -> app.handle("member-list", scanner);
                    case "3" -> app.handle("rental-list", scanner);
                    case "4" -> app.handle("comic-add", scanner);
                    case "5" -> app.handle("member-add", scanner);
                    case "6" -> performRent();
                    case "7" -> performReturn();
                    default -> System.out.println("Invalid selection.");
                }
            } catch (Exception e) {
                System.err.println("Error: " + e.getMessage());
            }
            System.out.println();
        }
    }

    private void printHeader() {
        System.out.println("============================");
        System.out.println("  Comic Rental Dashboard");
        System.out.println("============================");
    }

    private void printMenu() {
        System.out.println("1) List comics");
        System.out.println("2) List members");
        System.out.println("3) List rentals");
        System.out.println("4) Add comic");
        System.out.println("5) Add member");
        System.out.println("6) Rent comic");
        System.out.println("7) Return comic");
        System.out.println("0) Exit");
    }

    private void performRent() throws Exception {
        System.out.print("Comic ID: ");
        String c = scanner.nextLine().trim();
        System.out.print("Member ID: ");
        String m = scanner.nextLine().trim();
        app.handle("rent " + c + " " + m, scanner);
    }

    private void performReturn() throws Exception {
        System.out.print("Rental ID: ");
        String r = scanner.nextLine().trim();
        app.handle("return " + r, scanner);
    }
}
