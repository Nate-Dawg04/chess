package ui;

import chess.ChessBoard;
import chess.ChessPiece;
import chess.ChessPosition;

import static ui.EscapeSequences.*;

public class BoardPrinter {
    private static final int BOARD_SIZE_IN_SQUARES = 10;
    private static final int SQUARE_SIZE_IN_PADDED_CHARS = 3;
    private static final int LINE_WIDTH_IN_PADDED_CHARS = 1;
    private ChessBoard board;

    public BoardPrinter(ChessBoard board){
        this.board = board;
    }

    public String drawWhiteBoard(){
        StringBuilder sb = new StringBuilder();

        sb.append(drawHeader(sb,"WHITE"));

        boolean drawWhite = true;
        for (int row = 0; row < 8; row++){
            sb.append(createRow("WHITE",row,sb,drawWhite));
        }

        sb.append(drawHeader(sb,"WHITE"));

        return sb.toString();
    }

    public String drawBlackBoard(){
        StringBuilder sb = new StringBuilder();

        sb.append(drawHeader(sb,"BLACK"));

        boolean drawWhite = true;
        for (int row = 0; row < 8; row++){
            sb.append(createRow("BLACK",row,sb,drawWhite));
        }

        sb.append(drawHeader(sb,"BLACK"));

        return sb.toString();
    }

    //Draw a row of the board full of chess pieces
        // Count will be used for the number at the start and end of each row
    public StringBuilder createRow(String playerColor, int row, StringBuilder sb,boolean drawWhite){
        sb.append(SET_BG_COLOR_LIGHT_GREY).append(" ").append(row+1).append(" ");
        for (int col = 0; col < 8; col++){
            if (drawWhite){
                sb.append(SET_BG_COLOR_WHITE);
            } else {
                sb.append(SET_BG_COLOR_BLACK);
            }
            // Now call another function that returns the string to be added depending on which piece it is
            //Might need to remove the +1 depending on how getPiece is implemented
            sb.append(drawPiece("WHITE",board.getPiece(new ChessPosition(row+1,col+1))));
            drawWhite = !drawWhite;
        }
        sb.append(SET_BG_COLOR_LIGHT_GREY).append(" ").append(row+1).append(" ").append("\n");
        return sb;
    }

    // Draw the a-h part, reverse it when drawing the black board
    public StringBuilder drawHeader(StringBuilder sb, String playerColor){
        char[] columnHeaders;
        sb.append(SET_BG_COLOR_LIGHT_GREY).append(EMPTY);
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
    public String drawPiece(String playerColor, ChessPiece piece){
        if (piece == null){
            return EMPTY;
        }
        if (playerColor.equals("WHITE")){
            return switch (piece.getPieceType()) {
                case PAWN -> WHITE_PAWN;
                case ROOK -> WHITE_ROOK;
                case BISHOP -> WHITE_BISHOP;
                case QUEEN -> WHITE_QUEEN;
                case KNIGHT -> WHITE_KNIGHT;
                default -> EMPTY;
            };
        } else {
            return switch (piece.getPieceType()) {
                case PAWN -> BLACK_PAWN;
                case ROOK -> BLACK_ROOK;
                case BISHOP -> BLACK_BISHOP;
                case QUEEN -> BLACK_QUEEN;
                case KNIGHT -> BLACK_KNIGHT;
                default -> EMPTY;
            };
        }
    }

}
