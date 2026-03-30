package ui;

import chess.ChessBoard;
import chess.ChessGame;
import chess.ChessPiece;
import chess.ChessPosition;

import static ui.EscapeSequences.*;

public class BoardPrinter {
    private final ChessBoard board;
    boolean drawWhite;

    public BoardPrinter(ChessBoard board){
        this.board = board;
        drawWhite = true;
    }

    public String drawWhiteBoard(){
        StringBuilder sb = new StringBuilder();

        sb.append(SET_BG_COLOR_LIGHT_GREY).append("\n").append(drawHeader("WHITE"));

        int count = 8;
        for (int row = 0; row < 8; row++){
            sb.append(createRow("WHITE",row,count));
            count--;
        }

        sb.append(drawHeader("WHITE"));

        return sb.toString();
    }

    public String drawBlackBoard(){
        StringBuilder sb = new StringBuilder();

        sb.append(SET_BG_COLOR_LIGHT_GREY).append("\n").append(drawHeader("BLACK"));

        int count = 1;
        for (int row = 7; row >= 0; row--){
            sb.append(createRow("BLACK",row,count));
            count++;
        }

        sb.append(drawHeader("BLACK"));

        return sb.toString();
    }

    public StringBuilder createRow(String playerColor,int row, int count){
        StringBuilder sb = new StringBuilder();
        sb.append(SET_BG_COLOR_LIGHT_GREY).append(SET_TEXT_COLOR_BLACK).append(" ").append(count).append(" ");
        int blackCol = 8;
        for (int col = 0; col < 8; col++){
            if (drawWhite){
                sb.append(SET_BG_COLOR_WHITE);
            } else {
                sb.append(SET_BG_COLOR_BROWN);
            }
            if (playerColor.equals("WHITE")){
                sb.append(SET_TEXT_COLOR_LIGHT_GREY);
                sb.append(drawPiece(board.getPiece(new ChessPosition(row+1,col+1))));
            } else {
                sb.append(SET_TEXT_COLOR_BLACK);
                sb.append(drawPiece(board.getPiece(new ChessPosition(row+1,blackCol))));
            }
            if (col != 7){
                drawWhite = !drawWhite;
            }
            blackCol--;
        }

        sb.append(SET_BG_COLOR_LIGHT_GREY).append(SET_TEXT_COLOR_BLACK).append(" ").append(count).append(" ").append("\n");
        return sb;
    }

    // Draw the a-h part, reverse it when drawing the black board
    public StringBuilder drawHeader(String playerColor){
        StringBuilder sb = new StringBuilder();
        char[] columnHeaders;
        sb.append(SET_BG_COLOR_LIGHT_GREY).append(EMPTY).append(SET_TEXT_COLOR_BLACK);
        if (playerColor.equals("WHITE")){
            columnHeaders = new char[]{'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h'};
        } else {
            columnHeaders = new char[]{'h', 'g', 'f', 'e', 'd', 'c', 'b', 'a'};
        }
        for (char c : columnHeaders){
            sb.append(" ").append(c).append(" ");
        }
        sb.append(EMPTY).append("\n");
        return sb;
    }

    // Returns the appropriate white or black piece, or an empty space if piece is null
    public String drawPiece(ChessPiece piece){
        if (piece == null){
            return EMPTY;
        }
        ChessGame.TeamColor pieceColor = piece.getTeamColor();
        if (pieceColor.equals(ChessGame.TeamColor.WHITE)){
            return switch (piece.getPieceType()) {
                case PAWN -> WHITE_PAWN;
                case ROOK -> WHITE_ROOK;
                case BISHOP -> WHITE_BISHOP;
                case QUEEN -> WHITE_QUEEN;
                case KNIGHT -> WHITE_KNIGHT;
                case KING -> WHITE_KING;
            };
        } else {
            return switch (piece.getPieceType()) {
                case PAWN -> BLACK_PAWN;
                case ROOK -> BLACK_ROOK;
                case BISHOP -> BLACK_BISHOP;
                case QUEEN -> BLACK_QUEEN;
                case KNIGHT -> BLACK_KNIGHT;
                case KING -> BLACK_KING;
            };
        }
    }

}
