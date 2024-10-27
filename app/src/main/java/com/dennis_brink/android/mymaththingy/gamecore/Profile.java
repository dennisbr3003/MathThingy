package com.dennis_brink.android.mymaththingy.gamecore;

import androidx.annotation.NonNull;

import java.io.Serializable;

public class Profile extends DataStructure implements Serializable {

    private boolean isRegistered, CompeteOnline, showRegistrationFragment;
    private int playmode;

    // isRegistered = registered locally
    // CompeteOnline = compete online
    // isRegistered + CompeteOnline = compete globally
    // gamemode: 0 = anonymously, 1 = local, 2 = local and online

    public Profile() {
        super(StructureType.PROFILE);
        this.showRegistrationFragment = true;
        this.playmode = 2;
    }

    public boolean isRegistered() {
        return isRegistered;
    }

    public void setRegistered(boolean registered) {
        isRegistered = registered;
    }

    public boolean isCompeteOnline() {
        return CompeteOnline;
    }

    public void setCompeteOnline(boolean competeOnline) {
        this.CompeteOnline = competeOnline;
    }

    public boolean isShowRegistrationFragment() {
        return showRegistrationFragment;
    }

    public void setShowRegistrationFragment(boolean showRegistrationFragment) {
        this.showRegistrationFragment = showRegistrationFragment;
    }

    public int getPlaymode() {
        return playmode;
    }

    public void setPlaymode(int playmode) {
        this.playmode = playmode;
    }

    @NonNull
    @Override
    public String toString() {
        return "Profile{" +
                "isRegistered=" + isRegistered +
                ", CompeteOnline=" + CompeteOnline +
                ", showRegistrationFragment=" + showRegistrationFragment +
                ", playmode=" + playmode +
                '}';
    }
}
