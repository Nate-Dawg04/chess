package ui;

import chess.*;
import client.websocket.ServerMessageObserver;
import client.websocket.WebSocketFacade;
import exception.ResponseException;
import model.ListGamesGameData;
import requests.*;
import server.ServerFacade;
import websocket.messages.*;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Objects;
import java.util.Scanner;

import static ui.EscapeSequences.*;

public class ChessClient implements ServerMessageObserver {
    private final ServerFacade server;
    private final WebSocketFacade ws;
    private State state = State.SIGNEDOUT;
    private String authToken;
    private LinkedHashMap<Integer, ListGamesGameData> mostRecentGames;
    private int currentGameReferenceNum;
    private ChessGame currentChessGame;
    private ChessGame.TeamColor currentUserColor;
    private final Object consoleLock = new Object();

    public ChessClient(int port) throws ResponseException{
        server = new ServerFacade(port);
        ws = new WebSocketFacade(port, this);
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
            } else if (state == State.SIGNEDIN) {
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
            } else if (state == State.GAMEPLAY){
                return switch (cmd) {
                    case "help" -> help();
                    case "redraw" -> redraw();
                    case "leave" -> leave();
                    case "move" -> makeMove(params);
                    case "resign" -> resign();
                    case "highlight" -> highlight(params);
                    default -> invalidInput();
                };
            } else {
                return switch (cmd) {
                    case "help" -> help();
                    case "redraw" -> redraw();
                    case "leave" -> leave();
                    case "highlight" -> highlight(params);
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

        } else if (state == State.SIGNEDIN) {
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
        } else if (state == State.GAMEPLAY) {
            return
                    SET_TEXT_COMMAND + "- help" + SET_TEXT_EXPLANATION + " - view all commands"
                            + "\n" + SET_TEXT_COMMAND + "- redraw"
                            + SET_TEXT_EXPLANATION + " - completely redraw the chess board"
                            + "\n" + SET_TEXT_COMMAND + "- leave"
                            + SET_TEXT_EXPLANATION + " - leave the current game"
                            + "\n" + SET_TEXT_COMMAND + "- move <PieceLocation> <NewLocation>"
                            + SET_TEXT_EXPLANATION + " - input what move to make, example: move a2 a3"
                            + "\n" + SET_TEXT_COMMAND + "- resign"
                            + SET_TEXT_EXPLANATION + " - forfeit and end the game"
                            + "\n" + SET_TEXT_COMMAND + "- highlight <PieceLocation>"
                            + SET_TEXT_EXPLANATION + " - highlights all legal moves for the provided piece"  + "\n";
        } else {
            return
                    SET_TEXT_COMMAND + "- help" + SET_TEXT_EXPLANATION + " - view all commands"
                            + "\n" + SET_TEXT_COMMAND + "- redraw"
                            + SET_TEXT_EXPLANATION + " - completely redraw the chess board"
                            + "\n" + SET_TEXT_COMMAND + "- leave"
                            + SET_TEXT_EXPLANATION + " - leave the current game"
                            + "\n" + SET_TEXT_COMMAND + "- highlight <PieceLocation>"
                            + SET_TEXT_EXPLANATION + " - highlights all legal moves for the provided piece, ex: highlight a2"  + "\n";
        }

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

                currentGameReferenceNum = Integer.parseInt(params[0]);

                ws.joinGame(authToken,gameID);

                state = State.GAMEPLAY;

                return "\n";
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
                if (mostRecentGames == null){
                    throw new ResponseException(ResponseException.Code.ClientError,"Please listGames first");
                }

                if (mostRecentGames.get(Integer.parseInt(params[0])) == null){
                    throw new ResponseException(ResponseException.Code.ClientError,
                            "No games match the provided game number");
                }

                currentUserColor = ChessGame.TeamColor.WHITE;
                currentGameReferenceNum = Integer.parseInt(params[0]);
                ws.joinGame(authToken,mostRecentGames.get(currentGameReferenceNum).gameID());
                state = State.OBSERVE;

                return "\nNow observing game\n";
            } catch (NumberFormatException ex){
                throw new ResponseException(ResponseException.Code.ClientError, "gameNumber must be a valid integer");
            }

        } else {
            throw new ResponseException(ResponseException.Code.ClientError,"Expected: <gameNumber>");
        }
    }

    public String leave() throws ResponseException {
        try {
            ws.leaveGame(authToken, mostRecentGames.get(currentGameReferenceNum).gameID());
            state = State.SIGNEDIN;
            return "You left the game";
        } catch (Exception ex) {
            throw new ResponseException(ResponseException.Code.ClientError, "Error: " + ex.getMessage());
        }
    }

    public String makeMove(String... params) throws ResponseException {
        try {
            if (params.length < 2 || params.length > 3){
                throw new ResponseException(ResponseException.Code.ClientError,"Expected: move <PieceLocation> <NewLocation>");
            }
            if (params[0].length() != 2 || params[1].length() != 2){
                throw new ResponseException(ResponseException.Code.ClientError,"Please enter valid moves, examples: a3, b5, etc.");
            }


            ChessPosition startPosition = getChessPosition(params[0]);
            ChessPosition endPosition = getChessPosition(params[1]);

            // Check if a piece is being promoted. If so, ask for the promotion piece
            ChessPiece.PieceType promotionPieceType = null;

            if(currentChessGame.getBoard().getPiece(startPosition) == null){
                throw new ResponseException(ResponseException.Code.ClientError,"No piece in this position");
            }

            ChessPiece.PieceType pieceType = currentChessGame.getBoard().getPiece(startPosition).getPieceType();
            if ((endPosition.getRow() == 8 || endPosition.getRow() == 1) && (pieceType.equals(ChessPiece.PieceType.PAWN))){
                promotionPieceType = getPromotionPiece();
            }

//            System.out.printf("Attempting move from %d %d to %d %d",startMoveRow,startMoveCol,endMoveRow,endMoveCol);
            ChessMove move = new ChessMove(startPosition,endPosition,promotionPieceType);
            ws.makeMove(authToken, mostRecentGames.get(currentGameReferenceNum).gameID(),move);
            return "\n";
        } catch (Exception ex) {
            throw new ResponseException(ResponseException.Code.ClientError, "Error: " + ex.getMessage());
        }
    }


    public String redraw(){
        BoardPrinter boardPrinter = new BoardPrinter(currentChessGame.getBoard());
        if(currentUserColor == ChessGame.TeamColor.WHITE){
            return boardPrinter.drawWhiteBoard();
        } else {
            return boardPrinter.drawBlackBoard();
        }
    }

    public String resign() throws ResponseException{
        System.out.println("Are you sure you want to resign? The game will be finished...");
        System.out.println("Enter YES or NO");

        Scanner scanner = new Scanner(System.in);
        while (true) {
            try {
                printPrompt();
                String line = scanner.nextLine().trim().toLowerCase();

                if (line.isEmpty()) {
                    System.out.println(SET_TEXT_COLOR_RED + "Please enter YES or NO");
                    continue;
                }

                switch (line) {
                    case "yes" -> {
                        ws.resign(authToken, mostRecentGames.get(currentGameReferenceNum).gameID());
                        return "\nSuccessful resignation\n";
                    }
                    case "no" -> {
                        return "\nRemaining in the game\n";
                    }
                    default -> System.out.println(SET_TEXT_COLOR_RED + "Please enter YES or NO");
                }
            } catch (Exception e) {
                var msg = e.toString();
                System.out.print(msg);
            }
        }
    }

    private void assertSignedIn() throws ResponseException {
        if (state == State.SIGNEDOUT) {
            throw new ResponseException(ResponseException.Code.ClientError, "You must sign in");
        }
    }

    @Override
    public void notify(ServerMessage message) {
        switch(message.getServerMessageType()) {
            case NOTIFICATION -> displayMessage(((NotificationMessage) message).getMessage());
            case ERROR -> displayMessage(((ErrorMessage) message).getErrorMessage());
            case LOAD_GAME -> loadGame(((LoadGameMessage) message).getGame());
        }
    }

    private void loadGame(ChessGame game) {
        BoardPrinter boardPrinter = new BoardPrinter(game.getBoard());
        currentChessGame = game;

        System.out.println();
        if (currentUserColor == ChessGame.TeamColor.WHITE) {
            System.out.print(boardPrinter.drawWhiteBoard());
        } else {
            System.out.print(boardPrinter.drawBlackBoard());
        }
        printPrompt();
    }

    private void displayMessage(String message) {
        System.out.println(SET_TEXT_COLOR_GREEN + message);
        printPrompt();

    }

    private ChessPiece.PieceType getPromotionPiece(){
        Scanner scanner = new Scanner(System.in);

        System.out.println("Please enter a promotion piece type");
        System.out.println("Options: QUEEN, BISHOP, ROOK, KNIGHT");

        while (true) {
            try {
                printPrompt();
                String line = scanner.nextLine();
                String[] tokens = line.toLowerCase().split("\\s+");
                String[] params = Arrays.copyOfRange(tokens, 1, tokens.length);

                switch (params[0]) {
                    case "queen" -> {
                        return ChessPiece.PieceType.QUEEN;
                    }
                    case "rook" -> {
                        return ChessPiece.PieceType.ROOK;
                    }
                    case "knight" -> {
                        return ChessPiece.PieceType.KNIGHT;
                    }
                    case "bishop" -> {
                        return ChessPiece.PieceType.BISHOP;
                    }
                    default -> System.out.printf(SET_TEXT_COLOR_RED
                            + "Invalid input, please enter QUEEN, BISHOP, ROOK, or KNIGHT" + "\n");
                }
            } catch (Exception e) {
                var msg = e.toString();
                System.out.print(msg);
            }
        }
    }

    private ChessPosition getChessPosition(String input) throws ResponseException {
        if (input == null || input.length() != 2) {
            throw new ResponseException(
                    ResponseException.Code.ClientError,
                    "Please enter valid moves, examples: a3, b5, etc."
            );
        }

        char file = Character.toLowerCase(input.charAt(0));
        char rank = input.charAt(1);

        // Check that the first character is a letter
        if (!Character.isLetter(file)) {
            throw new ResponseException(
                    ResponseException.Code.ClientError,
                    "First character in a move must be letter a-h"
            );
        }

        // Check that the second character is a number
        if (!Character.isDigit(rank)) {
            throw new ResponseException(
                    ResponseException.Code.ClientError,
                    "Second character in a move must be number 1-8"
            );
        }

        int col = file - 'a' + 1;
        int row = Character.getNumericValue(rank);

        // Check if the row and column are within bounds
        if (col < 1 || col > 8) {
            throw new ResponseException(
                    ResponseException.Code.ClientError,
                    "First character in a move must be letter a-h"
            );
        }
        if (row < 1 || row > 8) {
            throw new ResponseException(
                    ResponseException.Code.ClientError,
                    "Second character in a move must be number 1-8"
            );
        }

        return new ChessPosition(row, col);
    }




}
