package com.dennis_brink.android.mymaththingy;

import android.app.Application;
import android.util.Log;

public class AppProfile extends Application implements ILogConstants {

    private static final AppProfile instance = new AppProfile();
    private static GameProfile gameProfile = null;

    private AppProfile() {
    }

    public static AppProfile getInstance() {
        if(gameProfile==null){
            initGameProfile();
        }
        return instance;
    }

    private static void initGameProfile(){
        gameProfile = FileHelper.readData(AppContext.getContext());
    }

    public String getHelp() {
        return "help's on the way";
    }

    public GameProfile getGameProfile() {
        return gameProfile;
    }

    public Player getPlayer() {
        return gameProfile.getPlayer();
    }

    public void reloadProfile() {
        initGameProfile();
    }

}
