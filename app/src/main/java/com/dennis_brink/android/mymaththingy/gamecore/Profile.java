package com.dennis_brink.android.mymaththingy.gamecore;

import androidx.annotation.NonNull;

import java.io.Serializable;

public class Profile extends DataStructure implements Serializable {

    private boolean isRegistered, doUpsertOnline, showRegistrationFragment;

    public Profile() {
        super(StructureType.PROFILE);
    }

    public boolean isRegistered() {
        return isRegistered;
    }

    public void setRegistered(boolean registered) {
        isRegistered = registered;
    }

    public boolean isDoUpsertOnline() {
        return doUpsertOnline;
    }

    public void setDoUpsertOnline(boolean doUpsertOnline) {
        this.doUpsertOnline = doUpsertOnline;
    }

    public boolean isShowRegistrationFragment() {
        return showRegistrationFragment;
    }

    public void setShowRegistrationFragment(boolean showRegistrationFragment) {
        this.showRegistrationFragment = showRegistrationFragment;
    }

    @NonNull
    @Override
    public String toString() {
        return "Profile{" +
                "isRegistered=" + isRegistered +
                ", doUpsertOnline=" + doUpsertOnline +
                ", showRegistrationFragment=" + showRegistrationFragment +
                '}';
    }
}
