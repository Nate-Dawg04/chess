package chess;

import java.util.Collection;
import java.util.HashSet;
import java.util.Objects;

/**
 * For a class that can manage a chess game, making moves on a board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessGame {

    private TeamColor teamTurn;
    ChessBoard board;

    public ChessGame() {
        board = new ChessBoard();
        // Add all the pieces to the board
        board.resetBoard();
        setTeamTurn(TeamColor.WHITE);
    }

    /**
     * @return Which team's turn it is
     */
    public TeamColor getTeamTurn() {
        return teamTurn;
    }

    /**
     * Set's which teams turn it is
     *
     * @param team the team whose turn it is
     */
    public void setTeamTurn(TeamColor team) {
        this.teamTurn = team;
    }

    /**
     * Enum identifying the 2 possible teams in a chess game
     */
    public enum TeamColor {
        WHITE,
        BLACK
    }

    /**
     * Gets a valid moves for a piece at the given location
     *
     * @param startPosition the piece to get valid moves for
     * @return Set of valid moves for requested piece, or null if no piece at
     * startPosition
     */
    public Collection<ChessMove> validMoves(ChessPosition startPosition) {

        // Get the piece at the given position
        ChessPiece piece = board.getPiece(startPosition);

        // Return null if there isn't a piece in that location
        if (piece == null){
            return null;
        }
        // Get all the possible moves for that piece
        Collection<ChessMove> allMoves = piece.pieceMoves(board,startPosition);
        // List to hold all the valid moves
        Collection<ChessMove> validMovesSet = new HashSet<>(0);

        // Check whether executing each move results in the King being in check
        for (ChessMove move : allMoves){
            // Create a new game and a copy of the board to simulate the move
            ChessGame copyGame = new ChessGame();
            ChessBoard copyBoard = board.clone();
            copyGame.setBoard(copyBoard);

            // First add the piece to the new spot
            copyBoard.addPiece(move.getEndPosition(),piece);
            // Then remove the piece from the old spot
            copyBoard.board[startPosition.getRow()-1][startPosition.getColumn()-1] = null;
            // Then check if making that move puts the king in check
            if (!copyGame.isInCheck(piece.getTeamColor())){
                validMovesSet.add(move);
            }
        }
        return validMovesSet;
    }

    /**
     * Makes a move in a chess game
     *
     * @param move chess move to perform
     * @throws InvalidMoveException if move is invalid
     */
    public void makeMove(ChessMove move) throws InvalidMoveException {
        // Check if there's a piece in the location, if not throw exception
        if (board.getPiece(move.getStartPosition()) == null){
            throw new InvalidMoveException();
        }
        ChessPiece piece = board.getPiece(move.getStartPosition());

        // Check if they're attempting to move one of their own pieces
        // If not throw an exception
        if (piece.getTeamColor() != getTeamTurn()){
            throw new InvalidMoveException();
        }

        // First check if the move is in the validMoves for that position
        Collection<ChessMove> valMoves = validMoves(move.getStartPosition());
        // Throw an exception if the move isn't in the valid moves
        if (!valMoves.contains(move)){
            throw new InvalidMoveException();
        }

        // Now execute the move
        // Add the piece to the new spot
        // Check if there's a promotion piece. If so, add that piece
        if (move.getPromotionPiece() != null){
            board.addPiece(move.getEndPosition(),new ChessPiece(piece.getTeamColor(),move.getPromotionPiece()));
        } else {
            board.addPiece(move.getEndPosition(),piece);
        }
        // Set the old spot to null (empty)
        board.board[move.getStartPosition().getRow()-1][move.getStartPosition().getColumn()-1] = null;

        // Change the team's turn to the other team
        if (piece.getTeamColor() == TeamColor.BLACK){
            setTeamTurn(TeamColor.WHITE);
        } else {
            setTeamTurn(TeamColor.BLACK);
        }
    }

    /**
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */
    public boolean isInCheck(TeamColor teamColor) {
        int rowCounter = 1;
        // Loop through each row
        for (ChessPiece[] row : board.board){
            int colCounter = 1;
            // Loop through each piece in the row
            for (ChessPiece piece : row){
                // Check if there's a piece in the location, and it's the opposing teams piece
                if (piece != null && piece.getTeamColor() != teamColor){
                    // Now check all the possible moves that this piece can make
                    for (ChessMove move : piece.pieceMoves(board,new ChessPosition(rowCounter,colCounter))){
                        // Check if the piece will capture a piece with this move, and IF IT'S THE KING
                        ChessPosition endingPosition = move.getEndPosition();
                        if (board.getPiece(endingPosition) != null && board.getPiece(endingPosition).getPieceType() == ChessPiece.PieceType.KING){
                            return true;
                        }
                    }
                }
                colCounter++;
            }
            rowCounter++;
        }
        return false;

    }

    /**
     * Determines if the given team is in checkmate
     *
     * @param teamColor which team to check for checkmate
     * @return True if the specified team is in checkmate
     */
    public boolean isInCheckmate(TeamColor teamColor) {
        Collection<ChessMove> allValidMoves = getTeamValidMoves(teamColor);
        return allValidMoves.isEmpty() && isInCheck(teamColor);
    }

    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves while not in check.
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {
        throw new RuntimeException("Not implemented");
    }

    /**
     * Sets this game's chessboard with a given board
     *
     * @param board the new board to use
     */
    public void setBoard(ChessBoard board) {
        this.board = board;
    }

    /**
     * Gets the current chessboard
     *
     * @return the chessboard
     */
    public ChessBoard getBoard() {
        return board;
    }

    /**
     * Gets all possible valid moves for a given TeamColor
     *
     * @return a HashSet containing those moves
     */
    public Collection<ChessMove> getTeamValidMoves(TeamColor teamColor){
        Collection<ChessMove> allValidMoves = new HashSet<>();
        // Loop through the entire board, and collect all the valid moves
        int rowCount = 1;
        for (ChessPiece[] row : board.board){
            // Loop through each piece in the row
            int colCount = 1;
            for (ChessPiece piece : row) {
                // Check if there's a piece in the location, and it's the opposing teams piece
                if (piece != null && piece.getTeamColor() == teamColor) {
                    allValidMoves.addAll(validMoves(new ChessPosition(rowCount,colCount)));
                }
                colCount++;
            }
            rowCount++;
        }
        return allValidMoves;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessGame chessGame = (ChessGame) o;
        return teamTurn == chessGame.teamTurn && Objects.equals(board, chessGame.board);
    }

    @Override
    public int hashCode() {
        return Objects.hash(teamTurn, board);
    }
}
