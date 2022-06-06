package com.dennis_brink.android.mymaththingy;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;

public class ResultActivity extends AppCompatActivity implements IGameConstants, IHighScoreDialogListener, ILogConstants {

    Button btnAgain;
    Button btnExit;
    TextView txtYourScore;
    int user_score;
    int user_time;
    int user_rank;
    int user_streaks;
    HighScore highScore;
    Receiver receiver = null;

    @SuppressLint("StringFormatMatches")
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        btnAgain = findViewById(R.id.btnAgain);
        btnExit = findViewById(R.id.btnClose);
        txtYourScore = findViewById(R.id.txtYourScore);

        Log.d(LOG_TAG, "ResultActivity.class: (onCreate)  Start");

        setupLogo();

        // getting data from the intent
        Intent i = getIntent();
        user_score = i.getIntExtra(USER_SCORE, 0);
        user_time = i.getIntExtra(USER_TIME, 0);
        user_streaks = i.getIntExtra(USER_STREAKS, 0);

        Log.d(LOG_TAG, "ResultActivity.class: (onCreate) Score from intent " + user_score + ", time from intent " + user_time);

        txtYourScore.setText(String.format(getString(R.string._yourscore), user_score));

        highScore = new HighScore(ResultActivity.this, HIGHSCORE_LISTLENGTH);
        String highscore_key = highScore.addHighScoreEntry(user_score, user_time, "ABC", user_streaks);
        user_rank = highScore.getRank(highscore_key); // if rank = -1 the score did not make the list

        Log.d(LOG_TAG, "ResultActivity.class: (onCreate) Key for ranking " + highscore_key);
        Log.d(LOG_TAG, "ResultActivity.class: (onCreate) Rank " + user_rank);

        // dialog here
        if(user_rank != -1) {
            AlertDialog dlg = DialogWrapper.getHighScoreInputDialog(ResultActivity.this, user_rank, highscore_key);
            dlg.show();
        }

        btnExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish(); // close this activity
            }
        });

        btnAgain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startGame();
            }
        });

    }

    private void setupLogo(){
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setLogo(R.drawable.rs_logo_padding);
        getSupportActionBar().setTitle(R.string._results);
        getSupportActionBar().setSubtitle(getString(R.string._topten));
        getSupportActionBar().setDisplayUseLogoEnabled(true);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (receiver != null){
            Log.d(LOG_TAG, "ResultActivity.class: (onPause) Unregistering receiver");
            this.unregisterReceiver(receiver);
            receiver = null;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(receiver == null){
            Log.d(LOG_TAG, "ResultActivity.class: (onResume) Registering receiver");
            receiver = new Receiver();
            receiver.setHighScoreDialogListener(this);
            this.registerReceiver(receiver, getFilter());
        }
        this.registerReceiver(receiver, getFilter());
    }

    private void startGame(){
        // first parameter = from, second parameter = what to start
        Intent i = new Intent(ResultActivity.this, MainActivity.class);
        startActivity(i); // run it
        finish(); // close this activity
    }

    private IntentFilter getFilter(){
        IntentFilter intentFilter = new IntentFilter();
        Log.d(LOG_TAG, "ResultActivity.class: (getFilter) Registering for broadcast action " + HIGHSCORE_ACTION);
        intentFilter.addAction(HIGHSCORE_ACTION); // only register receiver for this event
        return intentFilter;
    }

    @SuppressLint("RestrictedApi")
    @Override
    public void processHighScoreName(String name, String key) {

        Log.d(LOG_TAG, String.format("ResultActivity.class: (processHighScoreName) update high score entry with name %s and key %s", name, key));
        highScore = new HighScore(ResultActivity.this, HIGHSCORE_LISTLENGTH);
        highScore.setPlayerName(key, name);
        highScore.printSet();

        Log.d(LOG_TAG, "ResultActivity.class: (processHighScoreName) show customized snackbar");
        try {
            showSnackBar();
        } catch(Exception e){
            Log.d(LOG_TAG, "ResultActivity.class: (processHighScoreName) --> " + e.getMessage());
        }

    }

    @Override
    public void onBackPressed() {
        // override and do nothing, user should use the buttons
    }

    @SuppressLint("RestrictedApi")
    private void showSnackBar(){

        int marginFromSides = 15; // margins for the snackbar relative to the screen edge
        View view = findViewById(R.id.layoutResultParentLayout); //the layout needs to have an id so it can be found
        Snackbar snackbar = Snackbar.make(view, R.string._newhighscore, Snackbar.LENGTH_INDEFINITE);

        Snackbar.SnackbarLayout s_layout = (Snackbar.SnackbarLayout) snackbar.getView();
        TextView s_textView = s_layout.findViewById(com.google.android.material.R.id.snackbar_text); // get the snackbar textview
        Button s_button = s_layout.findViewById(com.google.android.material.R.id.snackbar_action); // get the snackbar button

        s_layout.setBackground(getResources().getDrawable(R.drawable.rounded_corners)); // use special shape xml for rounded corners
        FrameLayout.LayoutParams parentParams = (FrameLayout.LayoutParams) s_layout.getLayoutParams();
        parentParams.setMargins(marginFromSides, 0, marginFromSides, marginFromSides); // set space between screen edge and snackbar edge to 15
        parentParams.height = FrameLayout.LayoutParams.WRAP_CONTENT; // increase height to the (new) textview height
        parentParams.width = FrameLayout.LayoutParams.MATCH_PARENT; // the snackbar will span the entire width
        s_layout.setLayoutParams(parentParams);

        s_textView.setTextColor(getResources().getColor(R.color.WhiteSmoke)); // set the text color
        s_textView.setTypeface(s_textView.getTypeface(), Typeface.BOLD); // set the text to bold
        s_textView.setTextSize(20); // increase text size

        // make sure the height of the textview shows the complete text since it was increased
        LinearLayout.LayoutParams textViewLayoutParams = (LinearLayout.LayoutParams) s_textView.getLayoutParams();
        textViewLayoutParams.height = LinearLayout.LayoutParams.WRAP_CONTENT;
        s_textView.setLayoutParams(textViewLayoutParams);

        s_button.setBackgroundColor(getResources().getColor(R.color.holo_blue_dark));
        s_button.setTextColor(getResources().getColor(R.color.WhiteSmoke));
        s_button.setTextSize(14);

        // the button is too close to the left border so it needs to move to the inside
        LinearLayout.LayoutParams buttonLayoutParams = (LinearLayout.LayoutParams) s_button.getLayoutParams();
        buttonLayoutParams.rightMargin = buttonLayoutParams.rightMargin + 8;
        s_button.setLayoutParams(buttonLayoutParams);

        // button on click listener
        snackbar.setAction(R.string._showme, v -> {
            startHighScore(); // go to high score activity. Do not close this one so it will open if the user returns
            snackbar.dismiss();
        });

        snackbar.show();
    }

    private void startHighScore(){
        // first parameter = from, second parameter what to start, where to
        Intent i = new Intent(ResultActivity.this, HighScoreActivity.class);
        startActivity(i); // run it
    }

}