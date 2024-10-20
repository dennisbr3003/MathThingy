package com.dennis_brink.android.mymaththingy;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class Receiver extends BroadcastReceiver implements IGameConstants, ILogConstants, IRegistrationConstants {

    private IHighScoreDialogListener highScoreDialogListener;
    private IHighScoreDeleteListener highScoreDeleteListener;
    private IGameActivityListener gameActivityListener;
    private IRegisterActivityListener registerActivityListener;

    public void setRegisterActivityListener(IRegisterActivityListener registerActivityListener){
        this.registerActivityListener = registerActivityListener;
    }

    public void setHighScoreDialogListener(IHighScoreDialogListener highScoreDialogListener){
        this.highScoreDialogListener = highScoreDialogListener;
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

        if(intent.getAction().equals(HIGHSCORE_ACTION)) {

            Log.d(LOG_TAG, "Receiver.class: (onReceive) High score name received " + intent.getStringExtra(HIGHSCORE_ACTION));
            Log.d(LOG_TAG, "Receiver.class: (onReceive) High score key received " + intent.getStringExtra(HIGHSCORE_KEY));

            if (highScoreDialogListener != null) {
                highScoreDialogListener.processHighScoreName(intent.getStringExtra(HIGHSCORE_ACTION), intent.getStringExtra(HIGHSCORE_KEY));
            }
        }

        if(intent.getAction().equals(HIGHSCORE_DELETE_ACTION)) {
            if (highScoreDeleteListener != null) {
                highScoreDeleteListener.deleteHighScoreList();
            }
        }

        if(intent.getAction().equals(SOFTKEYBOARD_ACTION)) {
            if (gameActivityListener != null) {
                gameActivityListener.launchSoftKeyBoard();
            }
        }

        if(intent.getAction().equals(EXIT_GAME_ACTION)) {
            if (gameActivityListener != null) {
                gameActivityListener.exitGame();
            }
        }

        if(intent.getAction().equals(ONLINE_REGISTRATION_FAILURE)) {
            if (registerActivityListener != null) {
                registerActivityListener.onlineRegistrationFailure(intent.getStringExtra("MSG"));
            }
        }

        if(intent.getAction().equals(ONLINE_REGISTRATION_SUCCESS)) {
            if (registerActivityListener != null) {
                registerActivityListener.onlineRegistrationSuccess();
            }
        }

        if(intent.getAction().equals(LOCAL_REGISTRATION_FAILURE)) {
            if (registerActivityListener != null) {
                registerActivityListener.localRegistrationFailure(intent.getStringExtra("MSG"));
            }
        }

        if(intent.getAction().equals(LOCAL_REGISTRATION_SUCCESS)) {
            if (registerActivityListener != null) {
                registerActivityListener.localRegistrationSuccess();
            }
        }

    }

}
