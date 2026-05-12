package ui;

import dao.UserDAO;
import model.User;
import util.ConsoleUtil;

import java.util.Scanner;

/**
 * Login and Registration screen.
 */
public class AuthUI {

    private final Scanner sc      = new Scanner(System.in);
    private final UserDAO userDAO = new UserDAO();

    public void start() {
        printBanner();
        boolean running = true;
        while (running) {
            ConsoleUtil.printLine();
            System.out.println("  1. Login");
            System.out.println("  2. Register as Student");
            System.out.println("  0. Exit");
            ConsoleUtil.printLine();
            ConsoleUtil.printPrompt("Choice");

            int choice = ConsoleUtil.readInt(sc, 0, 2);
            switch (choice) {
                case 1 -> handleLogin();
                case 2 -> handleRegister();
                case 0 -> { running = false; ConsoleUtil.printInfo("Goodbye!"); }
            }
        }
    }

    private void handleLogin() {
        ConsoleUtil.printHeader("LOGIN");
        ConsoleUtil.printPrompt("Username");
        String user = ConsoleUtil.readLine(sc);
        ConsoleUtil.printPrompt("Password");
        String pass = ConsoleUtil.readLine(sc);

        User loggedIn = userDAO.login(user, pass);

        if (loggedIn == null) {
            ConsoleUtil.printError("Invalid username or password.");
            return;
        }

        ConsoleUtil.printSuccess("Welcome, " + loggedIn.getFullName() + "!");

        if (loggedIn.isAdmin()) {
            new AdminUI(loggedIn).show();
        } else {
            new StudentUI(loggedIn).show();
        }
    }

    private void handleRegister() {
        ConsoleUtil.printHeader("STUDENT REGISTRATION");

        ConsoleUtil.printPrompt("Full Name");
        String name = ConsoleUtil.readLine(sc);

        String username;
        while (true) {
            ConsoleUtil.printPrompt("Username");
            username = ConsoleUtil.readLine(sc);
            if (username.isEmpty()) {
                ConsoleUtil.printError("Username cannot be empty.");
            } else if (userDAO.usernameExists(username)) {
                ConsoleUtil.printError("Username already taken. Choose another.");
            } else {
                break;
            }
        }

        ConsoleUtil.printPrompt("Email");
        String email = ConsoleUtil.readLine(sc);

        ConsoleUtil.printPrompt("Password (min 6 chars)");
        String password = ConsoleUtil.readLine(sc);
        if (password.length() < 6) {
            ConsoleUtil.printError("Password too short. Registration cancelled.");
            return;
        }

        User newUser = new User();
        newUser.setFullName(name);
        newUser.setUsername(username);
        newUser.setEmail(email);
        newUser.setPassword(password);

        if (userDAO.registerStudent(newUser)) {
            ConsoleUtil.printSuccess("Registration successful! You can now login.");
        } else {
            ConsoleUtil.printError("Registration failed. Email may already exist.");
        }
    }

    private void printBanner() {
        System.out.println(ConsoleUtil.CYAN + ConsoleUtil.BOLD);
        System.out.println("  ╔═══════════════════════════════════════════════════════════╗");
        System.out.println("  ║         ONLINE EXAMINATION SYSTEM v1.0                    ║");
        System.out.println("  ║         MCA 2nd Semester — Java + JDBC + MySQL            ║");
        System.out.println("  ╚═══════════════════════════════════════════════════════════╝");
        System.out.println(ConsoleUtil.RESET);
    }
}
