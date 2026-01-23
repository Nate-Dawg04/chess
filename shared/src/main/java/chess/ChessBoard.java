package chess;

import java.util.Arrays;
import java.util.Objects;

/**
 * A chessboard that can hold and rearrange chess pieces.
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessBoard {
//    Use a 2D array to store the gameboard

    ChessPiece[][] board = new ChessPiece[8][8];

    public ChessBoard() {
//        resetBoard();
    }

    /**
     * Adds a chess piece to the chessboard
     *
     * @param position where to add the piece to
     * @param piece    the piece to add
     */
    public void addPiece(ChessPosition position, ChessPiece piece) {
//        subtract one because arrays are 0 based (while the moves are 1 based)
        board[position.getColumn()-1][position.getRow()-1] = piece;
    }

    /**
     * Gets a chess piece on the chessboard
     *
     * @param position The position to get the piece from
     * @return Either the piece at the position, or null if no piece is at that
     * position
     */
    public ChessPiece getPiece(ChessPosition position) {
        return board[position.getColumn()-1][position.getRow()-1];
    }

    /**
     * Sets the board to the default starting board
     * (How the game of chess normally starts)
     */
    public void resetBoard() {
        // Add the white pieces that aren't pawns
        addOtherPieces(ChessGame.TeamColor.WHITE);
        // Add the black pieces that aren't pawns
        addOtherPieces(ChessGame.TeamColor.BLACK);
        // Add all the pawns
        addPawns();
        // Make all the other rows full of null values
        makeNullRows();
    }


    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessBoard that = (ChessBoard) o;
        return Objects.deepEquals(board, that.board);
    }

    @Override
    public int hashCode() {
        return Arrays.deepHashCode(board);
    }

    // add all the pawns to the board
    private void addPawns(){
        for (int col = 1; col<=8; col++){
            // add all the pawns to the board
            addPiece(new ChessPosition(2,col),
                    new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN));
        }
        for (int col = 1; col<=8; col++){
            // add all the pawns to the board
            addPiece(new ChessPosition(7,col),
                    new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.PAWN));
        }
    }

    /* Adds all the non pawn pieces to the board for a specific color */
    private void addOtherPieces(ChessGame.TeamColor color){
        // Array containing all the pieces in the correct order
        ChessPiece.PieceType[] pieceOrder = {ChessPiece.PieceType.ROOK,ChessPiece.PieceType.KNIGHT,
                ChessPiece.PieceType.BISHOP,ChessPiece.PieceType.QUEEN,ChessPiece.PieceType.KING,
                ChessPiece.PieceType.BISHOP,ChessPiece.PieceType.KNIGHT,ChessPiece.PieceType.ROOK};

        int col = 1;
        if (color == ChessGame.TeamColor.WHITE){
            for (ChessPiece.PieceType piece : pieceOrder){
                addPiece(new ChessPosition(1,col),
                        new ChessPiece(ChessGame.TeamColor.WHITE, piece));
                col++;
            }
        // add the black pieces to the board
        } else {
            for (ChessPiece.PieceType piece : pieceOrder){
                addPiece(new ChessPosition(7,col),
                        new ChessPiece(ChessGame.TeamColor.BLACK, piece));
                col++;
            }
        }
    }

    /* make all the middle rows of the board null for the reset functionality */
    private void makeNullRows(){
        for (int row = 3; row <=6; row++){
            for (int col=1; col <=8; col++){
                board[row-1][col-1] = null;
            }
        }
    }


}
