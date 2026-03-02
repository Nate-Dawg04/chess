package dataaccess;

import java.util.HashMap;

import chess.ChessGame;
import model.GameData;

public class MemoryGameDAO implements GameDAO{
    // Maps a ChessGame to it's GameData, maybe change to be a String for the gameID?
    // Would need to convert the int gameID to a string for when accessing the GameData...
    final private HashMap<ChessGame, GameData> allGameData = new HashMap<>();

    public void deleteAllGameData(){
        allGameData.clear();
    }
}
