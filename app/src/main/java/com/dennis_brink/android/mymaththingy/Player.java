package com.dennis_brink.android.mymaththingy;

import java.io.Serializable;

public class Player implements Serializable {

    private String deviceId, callSign, displayName, email, language;

    public Player() {
    }

    public Player(String deviceId, String callSign, String displayName, String email, String language) {
        this.deviceId = deviceId==null?"":deviceId;
        this.callSign = callSign==null?"":callSign;
        this.displayName = displayName==null?"":displayName;
        this.email = email==null?"":email;
        this.language = language==null||language==""?"EN":language;
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

