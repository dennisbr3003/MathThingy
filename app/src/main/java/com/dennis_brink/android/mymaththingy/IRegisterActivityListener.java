package com.dennis_brink.android.mymaththingy;

public interface IRegisterActivityListener {

    void onlineRegistrationSuccess();
    void onlineRegistrationFailure(String msg);
    void localRegistrationSuccess();
    void localRegistrationFailure(String msg);
}
