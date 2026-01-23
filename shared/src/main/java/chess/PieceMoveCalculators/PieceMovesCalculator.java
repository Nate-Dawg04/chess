package chess.PieceMoveCalculators;

import chess.ChessBoard;
import chess.ChessGame;
import chess.ChessMove;
import chess.ChessPosition;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public interface PieceMovesCalculator {
    /* Returns all the possible moves for a piece */
    Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition position);

    // Check diagonally and return valid moves
    default Collection<ChessMove> checkDiagonals(ChessBoard board, ChessPosition position){
        List<ChessMove> diagonalMoves = new ArrayList<>(0);
//        Now check for possible moves in each direction

        int initialCol = position.getColumn();
        int initialRow = position.getRow();
        ChessGame.TeamColor yourColor = board.getPiece(position).getTeamColor();

        for (int i = 0; i < 4; i++) {
            boolean moreMoves = true;
            int tempCol = position.getColumn();
            int tempRow = position.getRow();

            // right and up diagonally
            if (i == 0){
                while (moreMoves){
                    tempCol++;
                    tempRow++;
                    if (validSpace(board,tempRow,tempCol,yourColor)) {
                        diagonalMoves.add(new ChessMove(new ChessPosition(initialRow,initialCol), new ChessPosition(tempRow,tempCol),null));
                    } else {
                        moreMoves = false;
                    }
                }

            // right and down diagonally
            } else if (i == 1) {
                while (moreMoves){
                    tempCol++;
                    tempRow--;
                    if (validSpace(board,tempRow,tempCol,yourColor)) {
                        diagonalMoves.add(new ChessMove(new ChessPosition(initialRow,initialCol), new ChessPosition(tempRow,tempCol),null));
                    } else {
                        moreMoves = false;
                    }
                }
            // left and up diagonally
            } else if (i == 2) {
                while (moreMoves){
                    tempCol--;
                    tempRow++;
                    if (validSpace(board,tempRow,tempCol,yourColor)) {
                        diagonalMoves.add(new ChessMove(new ChessPosition(initialRow,initialCol), new ChessPosition(tempRow,tempCol),null));
                    } else {
                        moreMoves = false;
                    }
                }
            // left and down diagonally
            } else {
                while (moreMoves){
                    tempCol--;
                    tempRow--;
                    if (validSpace(board,tempRow,tempCol,yourColor)) {
                        diagonalMoves.add(new ChessMove(new ChessPosition(initialRow,initialCol), new ChessPosition(tempRow,tempCol),null));
                    } else {
                        moreMoves = false;
                    }
                }
            }
        }
        return diagonalMoves;
    }
    // Check horizontally and vertically and return valid moves
    default Collection<ChessMove> checkHorizAndVertic(){
        return List.of();
    }
    // Check in the spaces immediately around a piece (all directions) and return valid moves
    default Collection<ChessMove> checkAround(){
        return List.of();
    }

    default boolean validSpace(ChessBoard board, int row, int col, ChessGame.TeamColor yourColor){
//        New position must be within bounds
//        AND either null or a piece of the opposing team

        if (        row <= 8
                && row >= 1
                && col <= 8
                && col >= 1
                && (board.getPiece(new ChessPosition(row,col)) == null || board.getPiece(new ChessPosition(row,col)).getTeamColor() != yourColor)
        ) {
            return true;
        } else {
            return false;
        }
    }

}
