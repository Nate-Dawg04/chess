package dataaccess.memoryDAOs;

import java.util.ArrayList;
import java.util.HashMap;

import chess.ChessGame;
import dataaccess.GameDAO;
import model.GameData;
import model.ListGamesGameData;

public class MemoryGameDAO implements GameDAO {
    // Maps the name of the game to the corresponding GameData
    final private HashMap<String, GameData> allGameData = new HashMap<>();
    private int gameCount = 0;

    public void deleteAllGameData(){
        allGameData.clear();
    }

    @Override
    public ArrayList<ListGamesGameData> getAllGames() {
        //Need to return something that can be easily converted to the correct format
            //in the ListGamesResult object
        ArrayList<ListGamesGameData> allGames = new ArrayList<>();
        for (GameData gameData : allGameData.values()) {
            // Create the new objects excluding the final Chessgame value in GameData
            allGames.add(new ListGamesGameData(gameData.gameID(),gameData.whiteUsername(),
                    gameData.blackUsername(), gameData.gameName()));
        }
        return allGames;
    }

    @Override
    public int createGame(String gameName) {
        gameCount++;
        allGameData.put(String.valueOf(gameCount),new GameData(gameCount,null,null,gameName,new ChessGame()));
        return gameCount;
    }

    @Override
    public GameData getGameData(int gameID) {
        return allGameData.get(String.valueOf(gameID));
    }

    @Override
    public void replaceGame(int gameID, GameData newGame) {
        allGameData.replace(String.valueOf(gameID),newGame);
    }


}
