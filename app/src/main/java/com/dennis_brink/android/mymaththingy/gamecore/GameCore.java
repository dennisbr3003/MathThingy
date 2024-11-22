package com.dennis_brink.android.mymaththingy.gamecore;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;
import android.util.Log;

import com.dennis_brink.android.mymaththingy.WebClient;
import com.fasterxml.jackson.core.JsonProcessingException;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Calendar;
import java.util.Locale;
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

    public static ScoreSet getScoreSet() {
        ScoreSet scores = (ScoreSet) readData(SCORE_FILE);
        if (scores == null) {
            scores = new ScoreSet();
        }
        return scores;
    }

    @SuppressLint("DefaultLocale")
    public static String getCurrentDisplayDateTime() {
        Calendar localDateTime = Calendar.getInstance(Locale.getDefault());

        return String.format("%02d/%02d/%04d %02d:%02d",
                localDateTime.get(Calendar.DAY_OF_MONTH),
                localDateTime.get(Calendar.MONTH) + 1, // 0-based, january = month 0
                localDateTime.get(Calendar.YEAR),
                localDateTime.get(Calendar.HOUR_OF_DAY),
                localDateTime.get(Calendar.MINUTE));
    }

    public static String getExtension(int numberValue){
        switch(numberValue){
            case 1:
                return EXT_FIRST;
            case 2:
                return EXT_SECOND;
            case 3:
                return EXT_THIRD;
            default:
                return EXT_REST;
        }
    }

    // naming makes more sense in the context where this method is used
    public static void setGlobalRanking() {
        getGlobalRanking();
    }

    public static void getGlobalRanking() {
        WebClient webClient = new WebClient(AppContext.getContext());
        webClient.initWebClient();
        try {
            webClient.saveScores();
        } catch (JsonProcessingException e) {
            Log.d("DENNIS_B", "error using webclient " + e.getMessage());
        }
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
