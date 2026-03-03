package chess.movecalculators;

import chess.*;

import java.util.Collection;
import java.util.Set;
import java.util.HashSet;

public class PawnMovesCalculator implements PieceMovesCalculator{
    @Override
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition position) {
        Set<ChessMove> pawnMoves = new HashSet<>(0);
        int initialRow = position.getRow();
        int initialCol = position.getColumn();
        ChessGame.TeamColor yourColor = board.getPiece(position).getTeamColor();

        // If white color
        if (board.getPiece(position).getTeamColor() == ChessGame.TeamColor.WHITE){
            // If piece hasn't moved, it may move forward 2 spots (if unblocked)
            if (position.getRow() == 2 && emptySpot(board,initialRow+2,initialCol) && emptySpot(board,initialRow+1,initialCol)){
                pawnMoves.add(new ChessMove(position, new ChessPosition(initialRow+2,initialCol),null));
            }

            //Check the three spots in front of the pawn
            int tempRow = initialRow + 1;
            for (int tempCol = initialCol-1; tempCol <= initialCol+1; tempCol++){
                if (validSpace(board,tempRow,tempCol,yourColor)){
                    // only add diagonal moves if capturing a piece
                    // can only move forwards if the spot is empty
                    if ((tempCol == initialCol && emptySpot(board,tempRow,tempCol))
                            || (tempCol != initialCol && capturesPiece(board,tempRow,tempCol,yourColor))
                    ){
                        addPawnMoves(pawnMoves,position,tempRow,tempCol,yourColor);
                    }
                }
            }

        // If black color
        } else {
            // If piece hasn't moved, it may move forward 2 spots (if unblocked)
            if (position.getRow() == 7 && emptySpot(board,initialRow-2,initialCol) && emptySpot(board,initialRow-1,initialCol)){
                pawnMoves.add(new ChessMove(position, new ChessPosition(initialRow-2,initialCol),null));
            }
            //Check the three spots in front of the pawn
            int tempRow = initialRow - 1;
            for (int tempCol = initialCol-1; tempCol <= initialCol+1; tempCol++){
                if (validSpace(board,tempRow,tempCol,yourColor)){
                    // only add diagonal moves if capturing a piece
                    // can only move forwards if the spot is empty
                    if ((tempCol == initialCol && emptySpot(board,tempRow,tempCol))
                            || (tempCol != initialCol && capturesPiece(board,tempRow,tempCol,yourColor))
                    ){
                        addPawnMoves(pawnMoves,position,tempRow,tempCol,yourColor);
                    }
                }
            }
        }
        return pawnMoves;
    }

    private void addPawnMoves(Set<ChessMove> pawnMoves, ChessPosition position,
                              int tempRow, int tempCol,ChessGame.TeamColor yourColor){
        int conditionRow;
        if (yourColor.equals(ChessGame.TeamColor.WHITE)){
            conditionRow = 8;
        } else {
            conditionRow = 1;
        }

        // If moving onto the last row, promote piece. Add move multiple times to the list,
        // but each with a different possible promotion piece
        if (tempRow == conditionRow) {
            pawnMoves.add(new ChessMove(position, new ChessPosition(tempRow,tempCol), ChessPiece.PieceType.ROOK));
            pawnMoves.add(new ChessMove(position, new ChessPosition(tempRow,tempCol), ChessPiece.PieceType.KNIGHT));
            pawnMoves.add(new ChessMove(position, new ChessPosition(tempRow,tempCol), ChessPiece.PieceType.BISHOP));
            pawnMoves.add(new ChessMove(position, new ChessPosition(tempRow,tempCol), ChessPiece.PieceType.QUEEN));
        // otherwise add the move like normal (no promotion)
        } else {
            pawnMoves.add(new ChessMove(position, new ChessPosition(tempRow,tempCol),null));
        }
    }

}
