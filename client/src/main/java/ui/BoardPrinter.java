package ui;

import chess.ChessBoard;
import chess.ChessPiece;
import static ui.EscapeSequences.*;

public class BoardPrinter {
    private static final int BOARD_SIZE_IN_SQUARES = 10;
    private static final int SQUARE_SIZE_IN_PADDED_CHARS = 3;
    private static final int LINE_WIDTH_IN_PADDED_CHARS = 1;
    private ChessBoard board;
    char[] columnHeaders = {'a','b','c','d','e','f','g','h'};

    public BoardPrinter(ChessBoard board){
        this.board = board;
    }

    public String drawWhiteBoard(){
        StringBuilder sb = new StringBuilder();

        // Print header
        boolean drawWhite = true;
        for (int row = 0; row < 8; row++){
            sb.append(SET_BG_COLOR_LIGHT_GREY).append(" ").append(row+1).append(" ");
            for (int col = 0; col < 8; col++){
                if (drawWhite){
                    sb.append(SET_BG_COLOR_WHITE);
                } else {
                    sb.append(SET_BG_COLOR_BLACK);
                }
                // Now call another function that returns the string to be added depending on which piece it is
            }
            sb.append(SET_BG_COLOR_LIGHT_GREY).append(" ").append(row+1).append(" ").append("\n");
        }

        // Print header
    }

    public String drawBlackBoard(){

    }

    //Draw a row of the board full of chess pieces
        // Count will be used for the number at the start and end of each row
    public String drawRow(String playerColor, int count){

    }

    // Draw the a-h part, reverse it when drawing the black board
    public String drawHeader(String playerColor){

    }

    public String drawPiece(String playerColor, ChessPiece piece){

    }

}
