package cli;

import engine.DatabaseEngine;
import engine.QueryResult;

import java.util.Scanner;

/**
 * Interactive SQL command-line interface providing a continuous REPL for user execution.
 */
public class NovaShell {

    private DatabaseEngine db;

    public NovaShell() {
        this.db = new DatabaseEngine("NovaDB");
    }

    /**
     * Bootstraps the terminal loop prompting sequentially for standard inputs continuously.
     */
    public void start() {
        printBanner();
        try (Scanner scanner = new Scanner(System.in)) {
            while (true) {
                System.out.print("\nNovaDB> ");
                if (!scanner.hasNextLine()) {
                    break;
                }
                String input = scanner.nextLine().trim();

                if (input.isEmpty()) {
                    continue;
                }

                if (input.equalsIgnoreCase("QUIT") || input.equalsIgnoreCase("EXIT")) {
                    System.out.println("\nGoodbye!");
                    break;
                }

                if (input.equalsIgnoreCase("HELP")) {
                    printHelp();
                    continue;
                }

                if (input.equalsIgnoreCase("CLEAR")) {
                    clearScreen();
                    continue;
                }

                if (input.endsWith(";")) {
                    input = input.substring(0, input.length() - 1).trim();
                }

                try {
                    QueryResult result = db.execute(input);
                    // Standard operations simply log messages. Sets return full output blocks.
                    if (result.getRecords() != null) {
                        System.out.println("\n" + result.toString());
                    } else {
                        System.out.println("\n" + result.getMessage());
                    }
                } catch (Exception e) {
                    System.out.println("\nUnexpected internal error: " + e.getMessage());
                }
            }
        }
    }

    private void printBanner() {
        System.out.println("=========================================");
        System.out.println("NovaDB Interactive SQL Shell");
        System.out.println("Type SQL commands.");
        System.out.println("Type HELP for help.");
        System.out.println("Type QUIT to exit.");
        System.out.println("=========================================");
    }

    private void printHelp() {
        System.out.println("\nAvailable commands:");
        System.out.println("CREATE TABLE");
        System.out.println("DROP TABLE");
        System.out.println("INSERT");
        System.out.println("SELECT");
        System.out.println("UPDATE");
        System.out.println("DELETE");
        System.out.println("CREATE INDEX");
        System.out.println("DROP INDEX");
        System.out.println("SHOW TABLES");
        System.out.println("QUIT");
        System.out.println("EXIT");
        System.out.println("CLEAR");
    }

    private void clearScreen() {
        for (int i = 0; i < 30; i++) {
            System.out.println();
        }
    }
}
