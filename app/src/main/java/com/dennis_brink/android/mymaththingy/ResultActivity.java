package com.dennis_brink.android.mymaththingy;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class ResultActivity extends AppCompatActivity implements IGameConstants, IHighScoreDialogListener {

    Button btnAgain;
    Button btnExit;
    TextView txtYourScore;
    int user_score;
    int user_time;
    int user_rank;
    int user_streaks;
    HighScore highScore;
    Receiver receiver = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        btnAgain = findViewById(R.id.btnAgain);
        btnExit = findViewById(R.id.btnClose);
        txtYourScore = findViewById(R.id.txtYourScore);

        Log.d("DENNIS_B", "ResultActivity.class: (onCreate)  Start");

        // getting data from the intent
        Intent i = getIntent();
        user_score = i.getIntExtra(USER_SCORE, 0);
        user_time = i.getIntExtra(USER_TIME, 0);
        user_streaks = i.getIntExtra(USER_STREAKS, 0);

        Log.d("DENNIS_B", "ResultActivity.class: (onCreate) Score from intent " + user_score + ", time from intent " + user_time);

        txtYourScore.setText(String.format("You scored an amazing %s points!", user_score));

        highScore = new HighScore(ResultActivity.this, HIGHSCORE_LISTLENGTH);
        String highscore_key = highScore.addHighScoreEntry(user_score, user_time, "ABC", user_streaks);
        user_rank = highScore.getRank(highscore_key); // if rank = -1 the score did not make the list

        Log.d("DENNIS_B", "ResultActivity.class: (onCreate) Key for ranking " + highscore_key);
        Log.d("DENNIS_B", "ResultActivity.class: (onCreate) Rank " + user_rank);

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

    @Override
    protected void onPause() {
        super.onPause();
        if (receiver != null){
            Log.d("DENNIS_B", "ResultActivity.class: (onPause) Unregistering receiver");
            this.unregisterReceiver(receiver);
            receiver = null;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(receiver == null){
            Log.d("DENNIS_B", "ResultActivity.class: (onResume) Registering receiver");
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
        Log.d("DENNIS_B", "ResultActivity.class: (getFilter) Registering for broadcast action " + HIGHSCORE_ACTION);
        intentFilter.addAction(HIGHSCORE_ACTION); // only register receiver for this event
        return intentFilter;
    }

    @Override
    public void processHighScoreName(String name, String key) {

        Log.d("DENNIS_B", String.format("ResultActivity.class: (processHighScoreName) update high score entry with name %s and key %s", name, key));
        highScore = new HighScore(ResultActivity.this, HIGHSCORE_LISTLENGTH);
        highScore.setPlayerName(key, name);
        highScore.printSet();

        Log.d("DENNIS_B", "ResultActivity.class: (processHighScoreName) show toaster");
        Toast toast= Toast.makeText(getApplicationContext(),"New high score saved", Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.BOTTOM,0,0);
        toast.show();

    }

}