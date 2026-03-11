package dataaccess;

import dataaccess.exceptions.DataAccessException;
import dataaccess.exceptions.DatabaseException;
import model.GameData;
import model.ListGamesGameData;

import java.util.ArrayList;

public interface GameDAO {
    void deleteAllGameData() throws DatabaseException;
    ArrayList<ListGamesGameData> getAllGames() throws DatabaseException;
    int createGame(String gameName) throws DatabaseException;
    GameData getGameData(int gameID) throws DatabaseException;
    void replaceGame(int gameID, GameData newGame) throws DatabaseException;
}
