package ui;

import exception.ResponseException;
import requests.LoginRequest;
import requests.RegisterRequest;
import server.ServerFacade;

import java.util.Arrays;
import java.util.Scanner;

import static ui.EscapeSequences.*;

public class ChessClient {
    private final ServerFacade server;
    private State state = State.SIGNEDOUT;

    public ChessClient(int port) throws ResponseException {
        server = new ServerFacade(port);
    }

    public void run() {
        System.out.println("Welcome to chess!");
        System.out.print(help());

        Scanner scanner = new Scanner(System.in);
        var result = "";
        while (!result.equals("quit")) {
            printPrompt();
            String line = scanner.nextLine();

            try {
                result = eval(line);
                System.out.print(SET_TEXT_COLOR_BLUE + result);
            } catch (Throwable e) {
                var msg = e.toString();
                System.out.print(msg);
            }
        }
        System.out.println();
    }

    private void printPrompt() {
        System.out.print("\n" + RESET_ALL + ">>> " + SET_TEXT_COLOR_GREEN);
    }

    public String eval(String input) {
        try {
            String[] tokens = input.toLowerCase().split(" ");
            String cmd = (tokens.length > 0) ? tokens[0] : "help";
            String[] params = Arrays.copyOfRange(tokens, 1, tokens.length);
            return switch (cmd) {
                case "login" -> login(params);
                case "register" -> register(params);
//                case "rescue" -> rescuePet(params);
//                case "list" -> listPets();
//                case "signout" -> signOut();
//                case "adopt" -> adoptPet(params);
//                case "adoptall" -> adoptAllPets();
                case "quit" -> "quit";
                default -> help();
            };
        } catch (ResponseException ex) {
            return ex.getMessage();
        }
    }

    public String invalidInput() {
        System.out.print("\n" + SET_TEXT_COLOR_RED + "Please enter a valid command");
        return help();
    }

    public String help() {
        if (state == State.SIGNEDOUT) {
            return """
                    - login <username> <password>
                    - register <username> <password> <email>
                    - quit
                    """;
        }
        return """
                - help
                - logout
                - createGame <gameName>
                - listGames
                - playGame <gameNumber> <WHITE|BLACK>
                - observeGame <gameNumber>
                - quit
                """;
    }

    public String register(String... params) throws ResponseException {
        if (params.length == 3) {
            try {
                var registerResult = server.register(new RegisterRequest(params[0],params[1],params[2]));
                state = State.SIGNEDIN;
                return String.format("Welcome in %s!", registerResult.username());
            } catch (Exception ex) {
                throw new ResponseException(ResponseException.Code.ClientError,ex.getMessage());
            }
        }
        throw new ResponseException(ResponseException.Code.ClientError, "Expected: <username> <password> <email>");
    }

    public String login(String... params) throws ResponseException {
        if (params.length == 2) {
            try {
                var loginResult = server.login(new LoginRequest(params[0],params[1]));
                state = State.SIGNEDIN;
                return String.format("Welcome in %s!", loginResult.username());
            } catch (Exception ex) {
                throw new ResponseException(ResponseException.Code.ClientError,ex.getMessage());
            }
        }
        throw new ResponseException(ResponseException.Code.ClientError, "Expected: <username> <password>");
    }

    private void assertSignedIn() throws ResponseException {
        if (state == State.SIGNEDOUT) {
            throw new ResponseException(ResponseException.Code.ClientError, "You must sign in");
        }
    }


}
