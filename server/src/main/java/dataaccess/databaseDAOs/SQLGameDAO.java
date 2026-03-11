package dataaccess.databaseDAOs;

import chess.ChessGame;
import com.google.gson.Gson;
import dataaccess.DatabaseManager;
import dataaccess.GameDAO;
import dataaccess.exceptions.DatabaseException;
import model.GameData;
import model.ListGamesGameData;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class SQLGameDAO implements GameDAO {

    public SQLGameDAO() throws DatabaseException {
        String[] createStatements = {
                """
            CREATE TABLE IF NOT EXISTS gameData (
              `gameID` INT NOT NULL AUTO_INCREMENT,
              `whiteUsername` VARCHAR(256),
              `blackUsername` VARCHAR(256),
              `gameName` VARCHAR(256) NOT NULL,
              `gameJSON` JSON,
              PRIMARY KEY (gameID),
              INDEX (gameID),
              FOREIGN KEY (whiteUsername)
              REFERENCES users (username)
              ON DELETE CASCADE,
              FOREIGN KEY (blackUsername)
              REFERENCES users (username)
              ON DELETE CASCADE
            )
            """
        };
        DatabaseManager.configureDatabase(createStatements);
    }

    @Override
    public void deleteAllGameData() throws DatabaseException {
        var statement = "DELETE FROM gameData";
        DatabaseManager.executeUpdate(statement);
    }

    @Override
    public ArrayList<ListGamesGameData> getAllGames() throws DatabaseException {
        var result = new ArrayList<ListGamesGameData>(0);
        try (Connection conn = DatabaseManager.getConnection()) {
            var statement = "SELECT gameID, whiteUsername, blackUsername, gameName, gameJSON FROM gameData";
            try (PreparedStatement ps = conn.prepareStatement(statement)) {
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        GameData tempGameData = readGameData(rs);
                        result.add(new ListGamesGameData(tempGameData.gameID(),tempGameData.whiteUsername(),tempGameData.blackUsername(),tempGameData.gameName()));
                    }
                }
            }
        } catch (Exception e) {
            throw new DatabaseException(String.format("Unable to read data: %s", e.getMessage()));
        }
        return result;
    }

    @Override
    public int createGame(String gameName) throws DatabaseException {
        var statement = "INSERT INTO gameData (gameName, gameJSON) VALUES (?,?)";
        return DatabaseManager.executeUpdate(statement, gameName, new ChessGame());
    }

    @Override
    public GameData getGameData(int gameID) throws DatabaseException {
        try (Connection conn = DatabaseManager.getConnection()) {
            var statement = "SELECT gameID,whiteUsername,blackUsername,gameName,gameJSON FROM gameData WHERE gameID=?";
            try (PreparedStatement ps = conn.prepareStatement(statement)) {
                ps.setInt(1, gameID);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        return readGameData(rs);
                    }
                }
            }
        } catch (Exception e) {
            throw new DatabaseException(String.format("Unable to read data: %s", e.getMessage()));
        }
        return null;
    }

    @Override
    public void replaceGame(int gameID, GameData newGame) throws DatabaseException {
        var statement = "UPDATE gameData SET whiteUsername = ?, blackUsername = ?, gameName = ?, gameJSON = ? WHERE gameID = ?";
        DatabaseManager.executeUpdate(statement, newGame.whiteUsername(),newGame.blackUsername(),newGame.gameName(),newGame.game(),gameID);
    }

    private GameData readGameData(ResultSet rs) throws SQLException {
        int gameID = rs.getInt("gameID");
        String whiteUsername = rs.getString("whiteUsername");
        String blackUsername = rs.getString("blackUsername");
        String gameName = rs.getString("gameName");
        Gson gson = new Gson();
        ChessGame game = gson.fromJson(rs.getString("gameJSON"),ChessGame.class);
        return new GameData(gameID,whiteUsername,blackUsername,gameName,game);
    }

}
