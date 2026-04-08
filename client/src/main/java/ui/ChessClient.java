package ui;

import chess.ChessBoard;
import client.websocket.ServerMessageObserver;
import exception.ResponseException;
import model.ListGamesGameData;
import requests.*;
import server.ServerFacade;
import websocket.messages.ServerMessage;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Objects;
import java.util.Scanner;

import static ui.EscapeSequences.*;

public class ChessClient implements ServerMessageObserver {
    private final ServerFacade server;
    private State state = State.SIGNEDOUT;
    private String authToken;
    private LinkedHashMap<Integer, ListGamesGameData> mostRecentGames;

    public ChessClient(int port){
        server = new ServerFacade(port);
    }

    public void run() {
        System.out.println(SET_TEXT_COLOR_GREEN + SET_TEXT_BOLD + "Welcome to chess!");
        System.out.print(RESET_ALL + SET_TEXT_COLOR_BLUE + help());

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
        System.out.print("\n" + RESET_ALL + "CHESS GAME >>> " + SET_TEXT_COLOR_GREEN);
    }

    public String eval(String input) {
        try {
            String[] tokens = input.toLowerCase().split(" ");
            String cmd = (tokens.length > 0) ? tokens[0] : "help";
            String[] params = Arrays.copyOfRange(tokens, 1, tokens.length);
            if (state == State.SIGNEDOUT) {
                return switch (cmd) {
                    case "login" -> login(params);
                    case "register" -> register(params);
                    case "help" -> help();
                    default -> invalidInput();
                };
            } else {
                return switch (cmd) {
                    case "quit" -> "quit";
                    case "help" -> help();
                    case "list" -> listGames();
                    case "logout" -> logout();
                    case "create" -> createGame(params);
                    case "play" -> joinGame(params);
                    case "observe" -> observeGame(params);
                    default -> invalidInput();
                };
            }
        } catch (ResponseException ex) {
            return SET_TEXT_COLOR_RED + ex.getMessage();
        }
    }

    public String invalidInput() {
        System.out.print("\n" + SET_TEXT_COLOR_RED + "Please enter a valid command" + "\n");
        return help();
    }

    public String help() {
        System.out.print(SET_TEXT_COLOR_WHITE + "\nHere are all the available commands:\n");
        if (state == State.SIGNEDOUT) {
            return
                    SET_TEXT_COMMAND + "- help"
                            + SET_TEXT_EXPLANATION + " - view all commands"
                    + "\n" + SET_TEXT_COMMAND + "- login <username> <password>"
                            + SET_TEXT_EXPLANATION + " - login to user account"
                    + "\n" + SET_TEXT_COMMAND +  "- register <username> <password> <email>"
                            + SET_TEXT_EXPLANATION + " - create a new user"
                    + "\n" + SET_TEXT_COMMAND + "- quit"
                            + SET_TEXT_EXPLANATION + " - stop the chess program" + "\n";

        }
        return
                SET_TEXT_COMMAND + "- help" + SET_TEXT_EXPLANATION + " - view all commands"
                        + "\n" + SET_TEXT_COMMAND + "- create <gameName>"
                        + SET_TEXT_EXPLANATION + " - create a new game with provided gameName"
                        + "\n" + SET_TEXT_COMMAND + "- list"
                        + SET_TEXT_EXPLANATION + " - list all the current games"
                        + "\n" + SET_TEXT_COMMAND + "- play <gameNumber> <WHITE|BLACK>"
                        + SET_TEXT_EXPLANATION + " - join a game with the provided gameNumber as WHITE or BLACK"
                        + "\n" + SET_TEXT_COMMAND + "- observe <gameNumber>"
                        + SET_TEXT_EXPLANATION + " - observe the game with the provided gameNumber"
                        + "\n" + SET_TEXT_COMMAND + "- logout"
                        + SET_TEXT_EXPLANATION + " - logout of account"
                        + "\n" + SET_TEXT_COMMAND + "- quit"
                        + SET_TEXT_EXPLANATION + " - stop the chess program" + "\n";

    }

    public String register(String... params) throws ResponseException {
        if (params.length == 3) {
            try {
                var registerResult = server.register(new RegisterRequest(params[0],params[1],params[2]));
                authToken = registerResult.authToken();
                state = State.SIGNEDIN;
                System.out.printf("Welcome in %s!\n", registerResult.username());
                return help();
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
                authToken = loginResult.authToken();
                System.out.printf("Welcome in %s!\n", loginResult.username());
                return help();
            } catch (Exception ex) {
                throw new ResponseException(ResponseException.Code.ClientError,ex.getMessage());
            }
        }
        throw new ResponseException(ResponseException.Code.ClientError, "Expected: <username> <password>");
    }

    public String logout() throws ResponseException {
        try {
            assertSignedIn();
            server.logout(new LogoutRequest(authToken));
            state = State.SIGNEDOUT;
            return "Successfully logged out\n";
        } catch (Exception ex) {
            throw new ResponseException(ResponseException.Code.ClientError,ex.getMessage());
        }
    }

    public String createGame(String... params) throws ResponseException {
        if (params.length >= 1) {
            try {
                String gameName = String.join(" ", params);
                assertSignedIn();
                server.createGame(new CreateGameRequest(authToken,gameName));
                return "Successfully created a game with the name " + gameName + "\n";
            } catch (Exception ex){
                throw new ResponseException(ResponseException.Code.ClientError,ex.getMessage());
            }
        }
        throw new ResponseException(ResponseException.Code.ClientError,"Expected: <gameName>");
    }

    public String listGames() throws ResponseException {
        try {
            assertSignedIn();
            StringBuilder sb = new StringBuilder();
            LinkedHashMap<Integer,ListGamesGameData> updatedGames = new LinkedHashMap<>(0);
            var listGamesResult = server.listGames(new ListGamesRequest(authToken));
            int count = 1;
            for (ListGamesGameData game : listGamesResult.games()){
                updatedGames.put(count,game);
                sb.append("\n");
                sb.append(SET_TEXT_COLOR_BLUE).append("Game Number: ")
                        .append(SET_TEXT_COLOR_WHITE).append(count).append("\n");
                sb.append(SET_TEXT_COLOR_BLUE).append("Game Name: ")
                        .append(SET_TEXT_COLOR_WHITE).append(game.gameName()).append("\n");
                sb.append(SET_TEXT_COLOR_BLUE).append("White User: ");
                if (game.whiteUsername() != null){
                    sb.append(SET_TEXT_COLOR_WHITE).append(game.whiteUsername());
                }
                sb.append("\n");
                sb.append(SET_TEXT_COLOR_BLUE).append("Black User: ");
                if (game.blackUsername() != null){
                    sb.append(SET_TEXT_COLOR_WHITE).append(game.blackUsername());
                }
                sb.append("\n");

                count++;
            }
            mostRecentGames = updatedGames;
            return sb.toString();
        } catch (Exception ex) {
            throw new ResponseException(ResponseException.Code.ServerError, ex.getMessage());
        }
    }

    public String joinGame(String... params) throws ResponseException{
        if (params.length == 2){
            try {
                if (mostRecentGames == null){
                    throw new ResponseException(ResponseException.Code.ClientError,"Please listGames first");
                }

                if (!Objects.equals(params[1], "white") && !Objects.equals(params[1], "black")){
                    throw new ResponseException(ResponseException.Code.ClientError, "must specify WHITE or BLACK");
                }
                int gameID;
                if (mostRecentGames.get(Integer.parseInt(params[0])) == null){
                    throw new ResponseException(ResponseException.Code.ClientError,
                            "No games match the provided game number");
                } else {
                    gameID = mostRecentGames.get(Integer.parseInt(params[0])).gameID();
                }
                server.joinGame(new JoinGameRequest(authToken,params[1].toUpperCase(),gameID));
                ChessBoard board = new ChessBoard();
                board.resetBoard();
                BoardPrinter boardPrinter = new BoardPrinter(board);
                if (params[1].equals("white")){
                    return boardPrinter.drawWhiteBoard();
                } else {
                    return boardPrinter.drawBlackBoard();
                }
//                return "Successfully joined the game!";
            } catch (NumberFormatException ex) {
                throw new ResponseException(ResponseException.Code.ClientError, "gameNumber must be a valid integer");
            } catch (ResponseException ex) {
                throw new ResponseException(ResponseException.Code.ClientError, ex.getMessage());
            } catch (Exception ex) {
                throw new ResponseException(ResponseException.Code.ServerError, ex.getMessage());
            }
        }
        throw new ResponseException(ResponseException.Code.ClientError,"Expected: <gameNumber> <WHITE|BLACK>");
    }

    public String observeGame(String... params) throws ResponseException{
        if (params.length == 1) {
            try {
                if (mostRecentGames.get(Integer.parseInt(params[0])) == null){
                    throw new ResponseException(ResponseException.Code.ClientError,
                            "No games match the provided game number");
                }
                ChessBoard board = new ChessBoard();
                board.resetBoard();
                BoardPrinter boardPrinter = new BoardPrinter(board);
                return boardPrinter.drawWhiteBoard();
            } catch (NumberFormatException ex){
                throw new ResponseException(ResponseException.Code.ClientError, "gameNumber must be a valid integer");
            }

        }
        throw new ResponseException(ResponseException.Code.ClientError,"Expected: <gameNumber>");
    }

    private void assertSignedIn() throws ResponseException {
        if (state == State.SIGNEDOUT) {
            throw new ResponseException(ResponseException.Code.ClientError, "You must sign in");
        }
    }

    @Override
    public void notify(ServerMessage message) {
        switch(message.getServerMessageType()) {
            // Need to now implement these methods to work properly with the UI
            case NOTIFICATION -> displayNotification(((NotificationMessage) message).getMessage());
            case ERROR -> displayError(((ErrorMessage) message).getErrorMessage());
            case LOAD_GAME -> loadGame(((LoadGameMessage) message).getGame());
        }
    }
}
