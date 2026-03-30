package chess;

import java.util.Arrays;
import java.util.Objects;

/**
 * A chessboard that can hold and rearrange chess pieces.
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessBoard implements Cloneable {
//    Use a 2D array to store the gameboard

    ChessPiece[][] board = new ChessPiece[8][8];

    public ChessBoard() {
//        addOtherPieces();
//        addPawns();
    }

    /**
     * Adds a chess piece to the chessboard
     *
     * @param position where to add the piece to
     * @param piece    the piece to add
     */
    public void addPiece(ChessPosition position, ChessPiece piece) {
//        subtract one because arrays are 0 based (while the moves are 1 based)
        board[position.getRow()-1][position.getColumn()-1] = piece;
    }

    /**
     * Removes a piece from the designated ChessPosition
     *
     * @param position where to remove the piece
     */
    public void removePiece(ChessPosition position){
        board[position.getRow()-1][position.getColumn()-1] = null;
    }

    /**
     * Gets a chess piece on the chessboard
     *
     * @param position The position to get the piece from
     * @return Either the piece at the position, or null if no piece is at that
     * position
     */
    public ChessPiece getPiece(ChessPosition position) {
        return board[position.getRow()-1][position.getColumn()-1];
    }

    /**
     * Sets the board to the default starting board
     * (How the game of chess normally starts)
     */
    public void resetBoard() {
        board = new ChessPiece[8][8];
        // Add the white and black pieces that aren't pawns
        addOtherPieces();
        // Add all the pawns
        addPawns();
        // Make all the other rows full of null values
        //makeNullRows();
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
    private void addOtherPieces(){
        // Array containing all the pieces in the correct order
        ChessPiece.PieceType[] pieceOrder = {ChessPiece.PieceType.ROOK,ChessPiece.PieceType.KNIGHT,
                ChessPiece.PieceType.BISHOP,ChessPiece.PieceType.QUEEN,ChessPiece.PieceType.KING,
                ChessPiece.PieceType.BISHOP,ChessPiece.PieceType.KNIGHT,ChessPiece.PieceType.ROOK};

        int col = 1;
        for (ChessPiece.PieceType piece : pieceOrder){
            addPiece(new ChessPosition(1,col),
                    new ChessPiece(ChessGame.TeamColor.WHITE, piece));
            col++;
        }

        // add the black pieces to the board
        col = 1;
        for (ChessPiece.PieceType piece : pieceOrder){
            addPiece(new ChessPosition(8,col),
                    new ChessPiece(ChessGame.TeamColor.BLACK, piece));
            col++;
        }

    }

    /**
     * pieceString is used in the toString method to generate a nice-looking chess board
     *
     * @param piece the piece that needs a string representation
     * @return the character representing the piece
     */
    private char pieceString(ChessPiece piece){
        // If the spot is null, return a space
        if (piece != null){
            // Rook case
            if (piece.getPieceType() == ChessPiece.PieceType.ROOK){
                if (piece.getTeamColor() == ChessGame.TeamColor.WHITE){
                    return 'R';
                } else {
                    return 'r';
                }
            }
            // Knight case
            if (piece.getPieceType() == ChessPiece.PieceType.KNIGHT){
                if (piece.getTeamColor() == ChessGame.TeamColor.WHITE){
                    return 'N';
                } else {
                    return 'n';
                }
            }
            // Bishop case
            if (piece.getPieceType() == ChessPiece.PieceType.BISHOP){
                if (piece.getTeamColor() == ChessGame.TeamColor.WHITE){
                    return 'B';
                } else {
                    return 'b';
                }
            }
            // Queen case
            if (piece.getPieceType() == ChessPiece.PieceType.QUEEN){
                if (piece.getTeamColor() == ChessGame.TeamColor.WHITE){
                    return 'Q';
                } else {
                    return 'q';
                }
            }
            // King case
            if (piece.getPieceType() == ChessPiece.PieceType.KING){
                if (piece.getTeamColor() == ChessGame.TeamColor.WHITE){
                    return 'K';
                } else {
                    return 'k';
                }
            }
            // Pawn case
            if (piece.getPieceType() == ChessPiece.PieceType.PAWN){
                if (piece.getTeamColor() == ChessGame.TeamColor.WHITE){
                    return 'P';
                } else {
                    return 'p';
                }
            }
        } else {
            return ' ';
        }
        return 0;
    }

    @Override
    public String toString() {
        StringBuilder boardString = new StringBuilder();
        for (int row = 8; row>=1; row--){
            boardString.append("|");
            for (int col=1; col<=8; col++){
                boardString.append(pieceString(getPiece(new ChessPosition(row,col))));
                if (col != 8){
                    boardString.append("|");
                }
            }
            boardString.append("|\n");
        }
        return boardString.toString();
    }

    @Override
    public int hashCode() {
        return Arrays.deepHashCode(board);
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
    public ChessBoard clone() {
        try {
            ChessBoard clone = (ChessBoard) super.clone();
            clone.board  = new ChessPiece[8][8];
            for (int i = 0; i<8; i++){
                for (int j = 0; j < 8; j++){
                    clone.board[i][j] = board[i][j] != null
                            ? board[i][j].clone()
                            : null;
                }
            }
            return clone;
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }

}
