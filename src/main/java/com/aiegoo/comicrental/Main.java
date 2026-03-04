package com.aiegoo.comicrental;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Welcome to the Comic Book Rental System");
        String command;
        while (true) {
            System.out.print("> ");
            command = scanner.nextLine().trim();
            if (command.equalsIgnoreCase("exit")) {
                System.out.println("Shutting down...");
                break;
            }
            // TODO: dispatch commands to services
            System.out.println("Command not implemented yet: " + command);
        }
        scanner.close();
    }
}
