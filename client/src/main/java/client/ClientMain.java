package client;

import chess.*;
import ui.ChessClient;


import java.util.Scanner;

public class ClientMain {
    public static void main(String[] args) {
        var piece = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN);
        System.out.println("♕ 240 Chess Client: " + piece);
//        String serverUrl = "http://localhost:8080";
        try {
            new ChessClient(8080).run();
        } catch (Throwable ex) {
            System.out.printf("Unable to start server: %s%n", ex.getMessage());
        }
    }
}
