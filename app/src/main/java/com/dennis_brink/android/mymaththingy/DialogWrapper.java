package com.dennis_brink.android.mymaththingy;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.text.Spanned;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.core.text.HtmlCompat;

public class DialogWrapper implements IGameConstants, ILogConstants {

    public static AlertDialog getStreakMessageDialog(Context context, int streaks){

        @SuppressLint("StringFormatMatches") String text = String.format(context.getString(R.string._streakwin),
                                                   streaks + getExtension(streaks), STREAK, streaks, STREAK_BONUS, (streaks * STREAK_BONUS));
        try {
            return createMessageDialog(text, context);
        } catch (Exception e){
            Log.d(LOG_TAG, "DialogWrapper.class: (getStreakMessageDialog) --> " + e.getMessage());
        }
        return null;
    }

    public static AlertDialog getDeleteConfirmDialog(Context context){
        try {
            return createConfirmationDialog(context, LAYOUT_CONFIRM_DELETE, "delete");
        } catch (Exception e){
            Log.d(LOG_TAG, "DialogWrapper.class: (getDeleteConfirmDialog) --> " + e.getMessage());
        }
        return null;
    }

    public static AlertDialog getExitConfirmDialog(Context context){

        String text = context.getString(R.string._momentum);
        try {
            return createConfirmationDialog(context, LAYOUT_CONFIRM_EXIT, text);
        } catch (Exception e){
            Log.d(LOG_TAG, "DialogWrapper.class: (getExitConfirmDialog) --> " + e.getMessage());
        }
        return null;
    }

    private static AlertDialog createMessageDialog(String text, Context context){

        AlertDialog.Builder builder = new AlertDialog.Builder(context);

        builder.setCancelable(true);
        LayoutInflater inf = LayoutInflater.from(context);
        View dialogMessage = inf.inflate(R.layout.dialog_win_streak, null);

        builder.setView(dialogMessage);

        TextView textView = dialogMessage.findViewById(R.id.txtMessage);
        ImageView imageView = dialogMessage.findViewById(R.id.imgOk);
        textView.setText(text);

        AlertDialog dlg = builder.create();

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                broadcastKeyboardAlert();
                dlg.dismiss();
            }

            private void broadcastKeyboardAlert() {
                Log.d(LOG_TAG, String.format("DialogWrapper.class: (broadcastKeyboardAlert) Start %s", SOFTKEYBOARD_ACTION));
                Intent i = new Intent();
                i.setAction(SOFTKEYBOARD_ACTION);
                context.sendBroadcast(i);
            }
        });

        Log.d(LOG_TAG, "DialogWrapper.class: (createMessageDialog) Return dialog");
        return dlg;

    }

    private static AlertDialog createConfirmationDialog(Context context, String dlg_type, String text){

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        View dialogMessage = null;
        builder.setCancelable(false);
        LayoutInflater inf = LayoutInflater.from(context);

        switch(dlg_type){
            case LAYOUT_CONFIRM_DELETE:
                dialogMessage = inf.inflate(R.layout.dialog_delete_highscore, null);
                break;
            case LAYOUT_CONFIRM_EXIT:
                dialogMessage = inf.inflate(R.layout.dialog_exit_game, null);
                break;
        }

        builder.setView(dialogMessage);

        TextView textView = dialogMessage.findViewById(R.id.txtMessage);
        ImageView imgOk = dialogMessage.findViewById(R.id.imgOk);
        ImageView imgNotOk = dialogMessage.findViewById(R.id.imgNotOk);

        if (dlg_type.equals(LAYOUT_CONFIRM_DELETE)){
            String text2 = context.getString(R.string._clearhighscore);
            Spanned dynamicStyledText = HtmlCompat.fromHtml(text2, HtmlCompat.FROM_HTML_MODE_COMPACT);
            textView.setText(dynamicStyledText);
        } else {
            textView.setText(text);
        }

        AlertDialog dlg = builder.create();

        imgOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                broadcastAlert();
                dlg.dismiss();
            }

            private void broadcastAlert() {
                Intent i = new Intent();
                switch(dlg_type) {
                    case LAYOUT_CONFIRM_DELETE:
                        i.setAction(HIGHSCORE_DELETE_ACTION);
                        Log.d(LOG_TAG, String.format("DialogWrapper.class: (broadcastAlert) Start %s", HIGHSCORE_DELETE_ACTION));
                        break;
                    case LAYOUT_CONFIRM_EXIT:
                        i.setAction(EXIT_GAME_ACTION);
                        Log.d(LOG_TAG, String.format("DialogWrapper.class: (broadcastAlert) Start %s", EXIT_GAME_ACTION));
                        break;
                }
                context.sendBroadcast(i);
            }
        });
        imgNotOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dlg.dismiss();
            }
        });
        Log.d(LOG_TAG, "DialogWrapper.class: (createConfirmationDialog) Return dialog");
        return dlg;
    }

    private static String getExtension(int numberValue){
        switch(numberValue){
            case 1:
                return EXT_FIRST;
            case 2:
                return EXT_SECOND;
            case 3:
                return EXT_THIRD;
            default:
                return EXT_REST;
        }
    }
}
