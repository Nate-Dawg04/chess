package dataaccess.databaseDAOs;

import dataaccess.GameDAO;
import model.GameData;
import model.ListGamesGameData;

import java.util.ArrayList;

public class SQLGameDAO implements GameDAO {
    @Override
    public void deleteAllGameData() {

    }

    @Override
    public ArrayList<ListGamesGameData> getAllGames() {
        return null;
    }

    @Override
    public int createGame(String gameName) {
        return 0;
    }

    @Override
    public GameData getGameData(int gameID) {
        return null;
    }

    @Override
    public void replaceGame(int gameID, GameData newGame) {

    }
}
