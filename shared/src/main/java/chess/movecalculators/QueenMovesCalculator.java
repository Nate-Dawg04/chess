package chess.movecalculators;

import chess.ChessBoard;
import chess.ChessMove;
import chess.ChessPosition;

import java.util.HashSet;
import java.util.Collection;
import java.util.Set;

public class QueenMovesCalculator implements PieceMovesCalculator{
    @Override
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition position) {
        Set<ChessMove> queenMoves = new HashSet<>(0);
        queenMoves.addAll(checkDiagonals(board,position));
        queenMoves.addAll(checkHorizAndVertic(board,position));
        queenMoves.addAll(checkAround(board,position));
        return queenMoves;
    }
}
