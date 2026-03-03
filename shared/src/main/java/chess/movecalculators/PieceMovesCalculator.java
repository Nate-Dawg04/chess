package chess.movecalculators;

import chess.ChessBoard;
import chess.ChessGame;
import chess.ChessMove;
import chess.ChessPosition;

import java.util.HashSet;
import java.util.Collection;
import java.util.Set;

public interface PieceMovesCalculator {
    /* Returns all the possible moves for a piece */
    Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition position);

    // Check diagonally and return valid moves
    default Collection<ChessMove> checkDiagonals(ChessBoard board, ChessPosition position){
        Set<ChessMove> diagonalMoves = new HashSet<>(0);
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
                        // If the spot is null, continue checking for more spots, otherwise stop
                        moreMoves = emptySpot(board, tempRow, tempCol);
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
                        // If the spot is null, continue checking for more spots, otherwise stop
                        moreMoves = emptySpot(board, tempRow, tempCol);
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
                        // If the spot is null, continue checking for more spots, otherwise stop
                        moreMoves = emptySpot(board, tempRow, tempCol);
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
                        // If the spot is null, continue checking for more spots, otherwise stop
                        moreMoves = emptySpot(board, tempRow, tempCol);
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
    default Collection<ChessMove> checkHorizAndVertic(ChessBoard board, ChessPosition position){
        Set<ChessMove> horizAndVerticMoves = new HashSet<>(0);
//        Now check for possible moves in each direction

        int initialCol = position.getColumn();
        int initialRow = position.getRow();
        ChessGame.TeamColor yourColor = board.getPiece(position).getTeamColor();

        for (int i = 0; i < 4; i++) {
            boolean moreMoves = true;
            int tempCol = position.getColumn();
            int tempRow = position.getRow();

            // Check upwards
            if (i == 0){
                while (moreMoves){
                    tempCol++;
                    if (validSpace(board,tempRow,tempCol,yourColor)) {
                        // If the spot is null, continue checking for more spots, otherwise stop
                        moreMoves = emptySpot(board, tempRow, tempCol);
                        horizAndVerticMoves.add(new ChessMove(new ChessPosition(initialRow,initialCol), new ChessPosition(tempRow,tempCol),null));
                    } else {
                        moreMoves = false;
                    }
                }

                // Check downwards
            } else if (i == 1) {
                while (moreMoves){
                    tempCol--;
                    if (validSpace(board,tempRow,tempCol,yourColor)) {
                        // If the spot is null, continue checking for more spots, otherwise stop
                        moreMoves = emptySpot(board, tempRow, tempCol);
                        horizAndVerticMoves.add(new ChessMove(new ChessPosition(initialRow,initialCol), new ChessPosition(tempRow,tempCol),null));
                    } else {
                        moreMoves = false;
                    }
                }
                // Check to the right
            } else if (i == 2) {
                while (moreMoves){
                    tempRow++;
                    if (validSpace(board,tempRow,tempCol,yourColor)) {
                        // If the spot is null, continue checking for more spots, otherwise stop
                        moreMoves = emptySpot(board, tempRow, tempCol);
                        horizAndVerticMoves.add(new ChessMove(new ChessPosition(initialRow,initialCol), new ChessPosition(tempRow,tempCol),null));
                    } else {
                        moreMoves = false;
                    }
                }
                // Check to the left
            } else {
                while (moreMoves){
                    tempRow--;
                    if (validSpace(board,tempRow,tempCol,yourColor)) {
                        // If the spot is null, continue checking for more spots, otherwise stop
                        moreMoves = emptySpot(board, tempRow, tempCol);
                        horizAndVerticMoves.add(new ChessMove(new ChessPosition(initialRow,initialCol), new ChessPosition(tempRow,tempCol),null));
                    } else {
                        moreMoves = false;
                    }
                }
            }
        }
        return horizAndVerticMoves;
    }

    // Check in the spaces immediately around a piece (all directions) and return valid moves
    default Collection<ChessMove> checkAround(ChessBoard board, ChessPosition position){
        Set<ChessMove> aroundMoves = new HashSet<>(0);

        int initialCol = position.getColumn();
        int initialRow = position.getRow();
        ChessGame.TeamColor yourColor = board.getPiece(position).getTeamColor();

        // Checks the squares surrounding the position of the piece
        for (int tempRow = initialRow-1; tempRow <= initialRow+1; tempRow++){
            for (int tempCol = initialCol-1; tempCol <= initialCol+1; tempCol++){
                if (validSpace(board,tempRow,tempCol,yourColor)) {
                    aroundMoves.add(new ChessMove(new ChessPosition(initialRow,initialCol), new ChessPosition(tempRow,tempCol),null));
                }
            }
        }
        return aroundMoves;
    }

    default boolean validSpace(ChessBoard board, int row, int col, ChessGame.TeamColor yourColor){
//        New position must be within bounds
//        AND either null or a piece of the opposing team

        return row <= 8
                && row >= 1
                && col <= 8
                && col >= 1
                && (emptySpot(board,row,col) || capturesPiece(board,row,col,yourColor));
    }

    /* Returns true if the spot is null */
    default boolean emptySpot(ChessBoard board, int row, int col){
        return board.getPiece(new ChessPosition(row, col)) == null;
    }

    /* Returns true if moving to this spot would capture a piece */
    default boolean capturesPiece(ChessBoard board, int row, int col, ChessGame.TeamColor yourColor){
        if (emptySpot(board,row,col)){
            return false;
        }
        return board.getPiece(new ChessPosition(row, col)).getTeamColor() != yourColor;
    }

}
