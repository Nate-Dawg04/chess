package dataaccess;

import model.GameData;
import model.ListGamesGameData;

import java.util.ArrayList;

public interface GameDAO {
    void deleteAllGameData();
    ArrayList<ListGamesGameData> getAllGames();
}
