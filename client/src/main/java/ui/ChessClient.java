package ui;

import exception.ResponseException;
import server.ServerFacade;

import java.util.Scanner;

public class ChessClient {
    private final ServerFacade server;

    public ChessClient(int port) throws ResponseException {
        server = new ServerFacade(port);
    }

    public void run() {
        System.out.println("Welcome to chess! Enter 'help' to view possible commands");
//        System.out.print(help());

        Scanner scanner = new Scanner(System.in);
        var result = "";
        while (!result.equals("quit")) {
            printPrompt();
            String line = scanner.nextLine();

            try {
//                result = eval(line);
//                System.out.print(BLUE + result);
            } catch (Throwable e) {
                var msg = e.toString();
                System.out.print(msg);
            }
        }
        System.out.println();
    }

    private void printPrompt() {
//        System.out.print("\n" + RESET + ">>> " + GREEN);
    }


}
