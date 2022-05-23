package com.dennis_brink.android.mymaththingy;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
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

public class DialogWrapper implements IGameConstants {

    public static AlertDialog getHighScoreInputDialog(Context context, int rank, String key){

        try {
            return createInputDialog(rank + getExtension(rank), key, context);
        } catch (Exception e){
            Log.d("DENNIS_B", "DialogWrapper.class: (getHighScoreInputDialog) --> " + e.getMessage());
        }
        return null;
    }

    public static AlertDialog getStreakMessageDialog(Context context, int streaks){

        String text = String.format("Extra congratulations to you! This is your %s %s answers win streak. You get an extra life and you receive %s * %s = %s bonus points!",
                                                   streaks + getExtension(streaks), STREAK, streaks, STREAK_BONUS, (streaks * STREAK_BONUS));
        try {
            return createMessageDialog(text, context);
        } catch (Exception e){
            Log.d("DENNIS_B", "DialogWrapper.class: (getStreakMessageDialog) --> " + e.getMessage());
        }
        return null;
    }

    public static AlertDialog getDeleteConfirmDialog(Context context){

        String text = String.format("Are you sure you want to       the high score list?"); // mind the gap, it's there for a reason
        try {
            return createConfirmationDialog(context, LAYOUT_CONFIRM_DELETE, text);
        } catch (Exception e){
            Log.d("DENNIS_B", "DialogWrapper.class: (getDeleteConfirmDialog) --> " + e.getMessage());
        }
        return null;
    }

    public static AlertDialog getExitConfirmDialog(Context context){

        String text = String.format("Are you sure you want to leave now, you will lose momentum!");
        try {
            return createConfirmationDialog(context, LAYOUT_CONFIRM_EXIT, text);
        } catch (Exception e){
            Log.d("DENNIS_B", "DialogWrapper.class: (getExitConfirmDialog) --> " + e.getMessage());
        }
        return null;
    }


    private static AlertDialog createInputDialog(String full_rank, String key, Context context)  {

        AlertDialog.Builder builder = new AlertDialog.Builder(context);

        builder.setCancelable(false);
        LayoutInflater inf = LayoutInflater.from(context);
        View dialogMessage = inf.inflate(R.layout.dialog_highscore_input, null);

        builder.setView(dialogMessage);

        TextView txtHighScoreMessage = dialogMessage.findViewById(R.id.txtHighScoreMessage);
        ImageView imgSaveName = dialogMessage.findViewById(R.id.imgHighScoreOk);
        TextView txtHighScoreError = dialogMessage.findViewById(R.id.txtHighScoreErrorMessage);
        EditText etxtHighScoreName = dialogMessage.findViewById(R.id.etxtHighScoreName);

        // hide error, do not use "gone" because of constraints
        txtHighScoreError.setVisibility(View.INVISIBLE);
        String text = String.format("You ranked %s on the high score list. Enter your name and claim your place!", full_rank);
        txtHighScoreMessage.setText(text);

        AlertDialog dlg = builder.create();

        imgSaveName.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View v) {

                Log.d("DENNIS_B", "DialogWrapper.class: (imgSaveName.onClick) High Score name empty? " + etxtHighScoreName.getText().toString().isEmpty());
                Log.d("DENNIS_B", "DialogWrapper.class: (imgSaveName.onClick) High Score name " + etxtHighScoreName.getText().toString());

                if(etxtHighScoreName.getText().toString().isEmpty()){
                    txtHighScoreError.setVisibility(View.VISIBLE);
                } else {
                    // broadcast the name
                    broadcastHighScoreName(etxtHighScoreName.getText().toString(), key, HIGHSCORE_ACTION);
                    // hide keyboard
                    try {
                        Log.d("DENNIS_B", "DialogWrapper.class: (imgSaveName.onClick) Hiding soft keyboard");
                        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(etxtHighScoreName.getWindowToken(), 0);
                    }catch(Exception e){
                        Log.d("DENNIS_B", "DialogWrapper.class: (imgSaveName.onClick) --> " + e.getMessage());
                    }
                    dlg.dismiss();
                }

            }

            private void broadcastHighScoreName(String highScoreName, String key, String action) {
                Log.d("DENNIS_B", String.format("DialogWrapper.class: (broadcastHighScoreName) Start %s sending %s with key %s", action, highScoreName, key));
                Intent i = new Intent();
                i.setAction(action);
                i.putExtra(HIGHSCORE_ACTION, highScoreName);
                i.putExtra(HIGHSCORE_KEY, key);
                context.sendBroadcast(i);
            }

        });

        // setup simple click listener for the filename field,so when the name text field
        // is clicked the error objects are reset for a new attempt -->
        etxtHighScoreName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                txtHighScoreError.setVisibility(View.INVISIBLE);
            }
        });

        Log.d("DENNIS_B", "DialogWrapper.class: (createInputDialog) Return dialog");
        return dlg;

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
                Log.d("DENNIS_B", String.format("DialogWrapper.class: (broadcastKeyboardAlert) Start %s", SOFTKEYBOARD_ACTION));
                Intent i = new Intent();
                i.setAction(SOFTKEYBOARD_ACTION);
                context.sendBroadcast(i);
            }
        });

        Log.d("DENNIS_B", "DialogWrapper.class: (createMessageDialog) Return dialog");
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
                ImageView imgTrashCan = dialogMessage.findViewById(R.id.imgTrashCan);
                imgTrashCan.setVisibility(View.VISIBLE);
                break;
            case LAYOUT_CONFIRM_EXIT:
                dialogMessage = inf.inflate(R.layout.dialog_exit_game, null);
                break;
        }

        builder.setView(dialogMessage);

        TextView textView = dialogMessage.findViewById(R.id.txtMessage);
        ImageView imgOk = dialogMessage.findViewById(R.id.imgOk);
        ImageView imgNotOk = dialogMessage.findViewById(R.id.imgNotOk);
        textView.setText(text);

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
                        Log.d("DENNIS_B", String.format("DialogWrapper.class: (broadcastAlert) Start %s", HIGHSCORE_DELETE_ACTION));
                        break;
                    case LAYOUT_CONFIRM_EXIT:
                        i.setAction(EXIT_GAME_ACTION);
                        Log.d("DENNIS_B", String.format("DialogWrapper.class: (broadcastAlert) Start %s", EXIT_GAME_ACTION));
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
        Log.d("DENNIS_B", "DialogWrapper.class: (createConfirmationDialog) Return dialog");
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
