package com.dennis_brink.android.mymaththingy;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.util.Objects;

public class Receiver extends BroadcastReceiver implements IGameConstants, ILogConstants, IRegistrationConstants {

    private IHighScoreDeleteListener highScoreDeleteListener;
    private IGameActivityListener gameActivityListener;
    private IRegisterActivityListener registerActivityListener;

    public void setRegisterActivityListener(IRegisterActivityListener registerActivityListener){
        this.registerActivityListener = registerActivityListener;
    }

    public void setGameActivityListener(IGameActivityListener gameActivityListener){
        this.gameActivityListener = gameActivityListener;
    }

    public void setHighScoreDeleteDialogListener(IHighScoreDeleteListener highScoreDeleteListener) {
        this.highScoreDeleteListener = highScoreDeleteListener;
    }

    @Override
    public void onReceive(Context context, Intent intent) {

        Log.d(LOG_TAG, "Receiver.class: (onReceive) Receiver reached with action " + intent.getAction());

        if(Objects.equals(intent.getAction(), HIGHSCORE_DELETE_ACTION)) {
            if (highScoreDeleteListener != null) {
                highScoreDeleteListener.deleteHighScoreList();
            }
        }

        if(Objects.equals(intent.getAction(), SOFTKEYBOARD_ACTION)) {
            if (gameActivityListener != null) {
                gameActivityListener.launchSoftKeyBoard();
            }
        }

        if(Objects.equals(intent.getAction(), EXIT_GAME_ACTION)) {
            if (gameActivityListener != null) {
                gameActivityListener.exitGame();
            }
        }

        if(Objects.equals(intent.getAction(), ONLINE_REGISTRATION_FAILURE)) {
            if (registerActivityListener != null) {
                registerActivityListener.onlineRegistrationFailure(intent.getStringExtra("MSG"));
            }
        }

        if(Objects.equals(intent.getAction(), ONLINE_REGISTRATION_SUCCESS)) {
            if (registerActivityListener != null) {
                registerActivityListener.onlineRegistrationSuccess();
            }
        }

        if(Objects.equals(intent.getAction(), LOCAL_REGISTRATION_FAILURE)) {
            if (registerActivityListener != null) {
                registerActivityListener.localRegistrationFailure(intent.getStringExtra("MSG"));
            }
        }

        if(Objects.equals(intent.getAction(), LOCAL_REGISTRATION_SUCCESS)) {
            if (registerActivityListener != null) {
                registerActivityListener.localRegistrationSuccess();
            }
        }

    }

}
