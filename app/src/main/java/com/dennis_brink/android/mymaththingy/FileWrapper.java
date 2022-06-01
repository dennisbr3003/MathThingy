package com.dennis_brink.android.mymaththingy;

import android.content.Context;
import android.util.Log;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Set;
import java.util.TreeSet;

public class FileWrapper implements ILogConstants {

    private static final String FILENAME = "highscore.hsc";

    public static void writeData(Set<HighScore.HighScoreEntry> highScoreEntrySet, Context context){

        Log.d(LOG_TAG, "FileWrapper.class: (writeData) Start");

        try {
            FileOutputStream fos = context.openFileOutput(FILENAME, Context.MODE_PRIVATE);
            ObjectOutputStream oas = new ObjectOutputStream(fos);
            oas.writeObject(highScoreEntrySet);
            oas.close();
        } catch (IOException e) {
            logError(e);
        }
    }

    public static Set<HighScore.HighScoreEntry> readData(Context context){

        Log.d(LOG_TAG, "FileWrapper.class: (readData) Start");

        Set<HighScore.HighScoreEntry> highScoreEntrySet = null;
        try {
            FileInputStream fis = context.openFileInput(FILENAME);
            ObjectInputStream ois = new ObjectInputStream(fis);
            highScoreEntrySet = (Set<HighScore.HighScoreEntry>) ois.readObject();

            Log.d(LOG_TAG, "FileWrapper.class: (readData) High score file is a valid object " + (highScoreEntrySet != null));

        } catch (FileNotFoundException e) {
            Log.d(LOG_TAG, "FileWrapper.class: (readData) No high score file found, return empty TreeSet instance");
            // let's not return null here. The first time the app is run
            // there will be no data file and no entries. This will cause
            // this exception and the return of a null object (X)
            highScoreEntrySet = new TreeSet<>();
            logError(e);
        } catch (IOException | ClassNotFoundException e) {
            logError(e);
        }
        finally {
            return highScoreEntrySet;
        }
    }

    private static void logError(Exception e){
        Log.d(LOG_TAG, "FileWrapper.class: (logError) --> " + e.getMessage());
    }
}
