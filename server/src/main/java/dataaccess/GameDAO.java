package dataaccess;

import dataaccess.exceptions.DataAccessException;
import model.GameData;
import model.ListGamesGameData;

import java.util.ArrayList;

public interface GameDAO {
    void deleteAllGameData() throws DataAccessException;
    ArrayList<ListGamesGameData> getAllGames() throws DataAccessException;
    int createGame(String gameName) throws DataAccessException;
    GameData getGameData(int gameID) throws DataAccessException;
    void replaceGame(int gameID, GameData newGame) throws DataAccessException;
}
