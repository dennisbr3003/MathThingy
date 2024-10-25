package com.dennis_brink.android.mymaththingy.gamecore;

import android.annotation.SuppressLint;
import android.provider.Settings;
import androidx.annotation.NonNull;
import com.dennis_brink.android.mymaththingy.AppContext;
import java.io.Serializable;

// If you use serializable on this class, make sure you also do this on the super
// https://stackoverflow.com/questions/9747443/java-io-invalidclassexception-no-valid-constructor

public class Player extends DataStructure implements Serializable, IGameCore {

    private String deviceId, callSign, displayName, email, language;

    @SuppressLint("HardwareIds")
    public Player() {
        super(StructureType.PLAYER);
        this.deviceId = Settings.Secure.getString(AppContext.getContext().getContentResolver(), Settings.Secure.ANDROID_ID);
        this.language = DEFAULT_LANG; // default language
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getCallSign() {
        return callSign;
    }

    public void setCallSign(String callSign) {
        this.callSign = callSign;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    @NonNull
    @Override
    public String toString() {
        return "Player{" +
                "deviceId='" + deviceId + '\'' +
                ", callSign='" + callSign + '\'' +
                ", displayName='" + displayName + '\'' +
                ", email='" + email + '\'' +
                ", language='" + language + '\'' +
                '}';
    }
}

