package com.dennis_brink.android.mymaththingy;

import android.content.Context;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class FileHelper {

    public static final String FILENAME = "gameProfile1.dat";

    public static void writeData(GameProfile config, Context context){
        try {
            FileOutputStream fos = context.openFileOutput(FILENAME, context.MODE_PRIVATE);
            ObjectOutputStream oas = new ObjectOutputStream(fos);
            oas.writeObject(config);
            oas.close();
        } catch (IOException e) {
            logError(e);
        }
    }

    public static void deleteConfigFile(Context context){
        File dir = context.getFilesDir();
        File file = new File(dir, FILENAME);
        boolean result = file.delete();
    }

    public static GameProfile readData (Context context){
        GameProfile config = null;
        try {
            FileInputStream fis = context.openFileInput(FILENAME);
            ObjectInputStream ois = new ObjectInputStream(fis);
            config = (GameProfile) ois.readObject();
        } catch (FileNotFoundException e) {
            // let's not return null here. The first time the app is run there will be no data file and no entries. This will cause
            // this exception and the return of a null object (X). We create a new object, save it and return it
            config = createInitialFile(context);
        } catch (IOException | ClassNotFoundException e) {
            logError(e);
        }
        return config;
    }

    private static GameProfile createInitialFile(Context context) {
        GameProfile config = new GameProfile(context);
        writeData(config, context);
        return config;
    }
    private static void logError(Exception e){
        Log.d("FH", e.getMessage());
    }
}
