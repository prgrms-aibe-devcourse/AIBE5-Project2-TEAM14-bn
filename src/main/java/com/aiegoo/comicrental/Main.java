package com.aiegoo.comicrental;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        DBConnectionUtil.registerShutdownHook();
        App app = new App();
        System.out.println("Welcome to the Comic Book Rental System");
        String command;
        while (true) {
            System.out.print("> ");
            command = scanner.nextLine().trim();
            if (command.equalsIgnoreCase("exit")) {
                System.out.println("Shutting down...");
                break;
            }
            app.handle(command, scanner);
        }
        scanner.close();
    }
}
