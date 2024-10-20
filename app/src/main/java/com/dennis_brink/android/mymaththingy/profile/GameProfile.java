package com.dennis_brink.android.mymaththingy.profile;

import android.annotation.SuppressLint;
import android.content.Context;
import android.provider.Settings.Secure;

import androidx.annotation.NonNull;

import java.io.Serializable;

public class GameProfile implements Serializable {
    Player player;
    private String deviceId, callSign, displayName, email, language;
    private boolean isRegistered, doUpsertOnline;

    @SuppressLint("HardwareIds")
    public GameProfile(Context context) {
        this.isRegistered = false;
        this.doUpsertOnline = true;
        this.deviceId = Secure.getString(context.getContentResolver(), Secure.ANDROID_ID);;
    }

    public boolean isDoUpsertOnline() {
        return doUpsertOnline;
    }

    public void setDoUpsertOnline(boolean doUpsertOnline) {
        this.doUpsertOnline = doUpsertOnline;
    }

    public void setRegistered(boolean registered) {
        isRegistered = registered;
    }

    public boolean isRegistered() {
        return isRegistered;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setConfigValues(String callSign, String displayName, String email, boolean goOnline, String language){
        this.callSign = callSign;
        this.displayName = displayName;
        this.email = email;
        this.doUpsertOnline = goOnline;
        this.language = language;
    }

    private void setDeviceId(String diviceId) {
        this.deviceId =  deviceId;
    }

    public Player getPlayer() {
        return new Player(this.deviceId, this.callSign, this.displayName, this.email, this.language);
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    @NonNull
    @Override
    public String toString() {
        return "GameProfile{" +
                "player=" + player +
                ", deviceId='" + deviceId + '\'' +
                ", callSign='" + callSign + '\'' +
                ", displayName='" + displayName + '\'' +
                ", email='" + email + '\'' +
                ", language='" + language + '\'' +
                ", isRegistered=" + isRegistered +
                ", doUpsertOnline=" + doUpsertOnline +
                '}';
    }
}
