package chess.PieceMoveCalculators;

import chess.ChessBoard;
import chess.ChessGame;
import chess.ChessMove;
import chess.ChessPosition;

import java.util.Collection;
import java.util.Set;
import java.util.HashSet;

public class KnightMovesCalculator implements PieceMovesCalculator{
    @Override
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition position) {
        Set<ChessMove> knightMoves = new HashSet<>(0);

        int initialCol = position.getColumn();
        int initialRow = position.getRow();
        ChessGame.TeamColor yourColor = board.getPiece(position).getTeamColor();

        // Checks the squares surrounding the position of the piece

        // Check the two spots to the right
        if (validSpace(board, initialRow+1,initialCol+2,yourColor)){
            knightMoves.add(new ChessMove(new ChessPosition(initialRow,initialCol), new ChessPosition(initialRow+1,initialCol+2),null));
        }
        if (validSpace(board, initialRow-1,initialCol+2,yourColor)){
            knightMoves.add(new ChessMove(new ChessPosition(initialRow,initialCol), new ChessPosition(initialRow-1,initialCol+2),null));
        }
        // Check the two spots to the left
        if (validSpace(board, initialRow+1,initialCol-2,yourColor)){
            knightMoves.add(new ChessMove(new ChessPosition(initialRow,initialCol), new ChessPosition(initialRow+1,initialCol-2),null));
        }
        if (validSpace(board, initialRow-1,initialCol-2,yourColor)){
            knightMoves.add(new ChessMove(new ChessPosition(initialRow,initialCol), new ChessPosition(initialRow-1,initialCol-2),null));
        }

        // Check the two spots upwards
        if (validSpace(board, initialRow+2,initialCol+1,yourColor)){
            knightMoves.add(new ChessMove(new ChessPosition(initialRow,initialCol), new ChessPosition(initialRow+2,initialCol+1),null));
        }
        if (validSpace(board, initialRow+2,initialCol-1,yourColor)){
            knightMoves.add(new ChessMove(new ChessPosition(initialRow,initialCol), new ChessPosition(initialRow+2,initialCol-1),null));
        }

        // Check the two spots downwards
        if (validSpace(board, initialRow-2,initialCol+1,yourColor)){
            knightMoves.add(new ChessMove(new ChessPosition(initialRow,initialCol), new ChessPosition(initialRow-2,initialCol+1),null));
        }
        if (validSpace(board, initialRow-2,initialCol-1,yourColor)){
            knightMoves.add(new ChessMove(new ChessPosition(initialRow,initialCol), new ChessPosition(initialRow-2,initialCol-1),null));
        }


        return knightMoves;
    }
}
