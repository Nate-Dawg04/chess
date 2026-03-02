package dataaccess;

import model.GameData;

import java.util.ArrayList;

public interface GameDAO {
    void deleteAllGameData();
    ArrayList<GameData> getAllGames();
}
