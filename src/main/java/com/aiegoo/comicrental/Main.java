package com.aiegoo.comicrental;

import java.util.Scanner;

import com.aiegoo.comicrental.util.DBConnectionUtil;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        DBConnectionUtil.registerShutdownHook();
        System.out.println("Welcome to the Comic Book Rental System");

        if (args.length > 0 && "dashboard".equalsIgnoreCase(args[0])) {
            // launch interactive dashboard mode
            new Dashboard(scanner).run();
            scanner.close();
            return;
        }

        App app = new App();
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
