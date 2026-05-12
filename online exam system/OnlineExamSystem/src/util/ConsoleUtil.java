package util;

import java.util.Scanner;

/**
 * Console formatting utilities for a polished terminal UI.
 */
public class ConsoleUtil {

    public static final String RESET   = "\033[0m";
    public static final String BOLD    = "\033[1m";
    public static final String GREEN   = "\033[32m";
    public static final String RED     = "\033[31m";
    public static final String YELLOW  = "\033[33m";
    public static final String CYAN    = "\033[36m";
    public static final String BLUE    = "\033[34m";
    public static final String MAGENTA = "\033[35m";
    public static final String WHITE   = "\033[37m";

    private static final int LINE_WIDTH = 65;

    public static void printLine() {
        System.out.println(CYAN + "─".repeat(LINE_WIDTH) + RESET);
    }

    public static void printDoubleLine() {
        System.out.println(CYAN + "═".repeat(LINE_WIDTH) + RESET);
    }

    public static void printHeader(String title) {
        System.out.println();
        printDoubleLine();
        int pad = (LINE_WIDTH - title.length()) / 2;
        System.out.println(BOLD + CYAN + " ".repeat(Math.max(0, pad)) + title + RESET);
        printDoubleLine();
    }

    public static void printSuccess(String msg) {
        System.out.println(GREEN + "  ✔  " + msg + RESET);
    }

    public static void printError(String msg) {
        System.out.println(RED + "  ✘  " + msg + RESET);
    }

    public static void printInfo(String msg) {
        System.out.println(YELLOW + "  ℹ  " + msg + RESET);
    }

    public static void printPrompt(String label) {
        System.out.print(BOLD + WHITE + "  " + label + ": " + RESET);
    }

    public static String readLine(Scanner sc) {
        return sc.nextLine().trim();
    }

    public static int readInt(Scanner sc, int min, int max) {
        while (true) {
            try {
                int val = Integer.parseInt(sc.nextLine().trim());
                if (val >= min && val <= max) return val;
                printError("Enter a number between " + min + " and " + max);
            } catch (NumberFormatException e) {
                printError("Invalid input. Enter a number.");
            }
            printPrompt("Choice");
        }
    }

    public static void pause(Scanner sc) {
        System.out.print(MAGENTA + "\n  Press [ENTER] to continue..." + RESET);
        sc.nextLine();
    }

    public static void printTableHeader(String... cols) {
        StringBuilder sb = new StringBuilder("  ");
        for (String col : cols) sb.append(String.format("%-20s", col));
        System.out.println(BOLD + BLUE + sb + RESET);
        printLine();
    }

    /** Print a grade badge */
    public static String gradeBadge(String grade) {
        return switch (grade) {
            case "A+" -> GREEN + BOLD + "[A+]" + RESET;
            case "A"  -> GREEN + "[A]" + RESET;
            case "B"  -> CYAN  + "[B]" + RESET;
            case "C"  -> YELLOW + "[C]" + RESET;
            case "D"  -> YELLOW + "[D]" + RESET;
            default   -> RED   + "[F]" + RESET;
        };
    }

    public static void printPassFail(boolean passed) {
        if (passed) System.out.println(GREEN + BOLD + "  ✔  PASSED" + RESET);
        else        System.out.println(RED   + BOLD + "  ✘  FAILED" + RESET);
    }

    /** Simple ASCII progress bar */
    public static String progressBar(double pct) {
        int filled = (int) (pct / 5);   // 20 blocks = 100%
        return "[" + "█".repeat(filled) + "░".repeat(20 - filled) + "] " +
               String.format("%.1f%%", pct);
    }
}
