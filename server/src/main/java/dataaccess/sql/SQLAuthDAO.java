package dataaccess.sql;

import dataaccess.AuthDAO;
import dataaccess.DatabaseManager;
import dataaccess.exceptions.DatabaseException;
import dataaccess.exceptions.UnauthorizedException;
import model.AuthData;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class SQLAuthDAO implements AuthDAO {

    public SQLAuthDAO() throws DatabaseException {
        String[] createStatements = {
                """
            CREATE TABLE IF NOT EXISTS authData (
              `authToken` varchar(256) NOT NULL,
              `username` varchar(256) NOT NULL,
              PRIMARY KEY (authToken),
              INDEX (authToken),
              FOREIGN KEY (username)
              REFERENCES users (username)
              ON DELETE CASCADE
            )
            """
        };
        DatabaseManager.configureDatabase(createStatements);
    }

    @Override
    public void createAuth(AuthData authData) throws DatabaseException {
        var statement = "INSERT INTO authData (authToken, username) VALUES (?, ?)";
        DatabaseManager.executeUpdate(statement, authData.authToken(), authData.username());
    }

    @Override
    public void deleteAllAuthData() throws DatabaseException {
        var statement = "DELETE FROM authData";
        DatabaseManager.executeUpdate(statement);
    }

    @Override
    public String getAuth(String authToken) throws UnauthorizedException, DatabaseException {
        // Throw Unauthorized Exception if there isn't
        try (Connection conn = DatabaseManager.getConnection()) {
            var statement = "SELECT authToken, username FROM authData WHERE authToken=?";
            try (PreparedStatement ps = conn.prepareStatement(statement)) {
                ps.setString(1, authToken);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        return readAuthData(rs).username();
                    } else {
                        throw new UnauthorizedException("unauthorized");
                    }
                }
            }
        } catch (UnauthorizedException ex) {
            throw new UnauthorizedException("unauthorized");
        } catch (Exception e) {
            throw new DatabaseException(String.format("Unable to read data: %s", e.getMessage()));
        }
    }

    @Override
    public void deleteAuth(String authToken) throws DatabaseException {
        var statement = "DELETE FROM authData WHERE authToken=?";
        DatabaseManager.executeUpdate(statement, authToken);
    }

    private AuthData readAuthData(ResultSet rs) throws SQLException {
        String authToken = rs.getString("authToken");
        String username = rs.getString("username");
        return new AuthData(authToken,username);
    }

}
