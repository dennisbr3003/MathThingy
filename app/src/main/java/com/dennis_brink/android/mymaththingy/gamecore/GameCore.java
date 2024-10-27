package com.dennis_brink.android.mymaththingy.gamecore;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Objects;

public class GameCore extends Application implements IGameCore {

    public static Profile getProfile() {
        Profile profile = (Profile) readData(PROFILE_FILE);
        if (profile == null) {
            profile = new Profile();
        }
        return profile;
    }

    public static Player getPlayer() {
        Player player = (Player) readData(PLAYER_FILE);
        if (player == null) {
            player = new Player();
        }
        return player;
    }

    public static void saveDataStructure(DataStructure dataStructure) {
        Log.d(TAG, "saveDataStructure " + dataStructure.toString());
        switch(dataStructure.getType()) {
            case SCORE:
                writeData(dataStructure, SCORE_FILE);
                break;
            case PLAYER:
                writeData(dataStructure, PLAYER_FILE);
                break;
            case PROFILE:
                writeData(dataStructure, PROFILE_FILE);
                break;
        }
    }

    private static void writeData(DataStructure dataStructure, String filename){
        try {
            FileOutputStream fos = AppContext.getContext().openFileOutput(filename, Context.MODE_PRIVATE);
            ObjectOutputStream oas = new ObjectOutputStream(fos);
            oas.writeObject(dataStructure);
            oas.close();
        } catch (IOException e) {
            logError(e);
        }
    }

    private static void logError(Exception e){
        Log.d(TAG, Objects.requireNonNull(e.getMessage()));
    }

    private static DataStructure readData(String filename) {
        DataStructure dataStructure = null;
        try {
            FileInputStream fis = AppContext.getContext().openFileInput(filename);
            ObjectInputStream ois = new ObjectInputStream(fis);
            dataStructure =  (DataStructure) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            // let's return null here. The first time the app is run there will be no data file and
            // no entries. This will cause this exception and the system will return an error. In
            // the calling proc the appropriate empty object of the correct type wil be created and
            // returned. (Player, Config or Score(s))
            logError(e);
        }
        return dataStructure;
    }
    public String getHelp() {
        return "help is on it's way";
    }

}
