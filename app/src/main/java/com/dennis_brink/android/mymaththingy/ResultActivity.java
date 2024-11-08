package com.dennis_brink.android.mymaththingy;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.res.ResourcesCompat;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.dennis_brink.android.mymaththingy.gamecore.AppContext;
import com.dennis_brink.android.mymaththingy.gamecore.GameCore;
import com.dennis_brink.android.mymaththingy.gamecore.ScoreSet;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.android.material.snackbar.Snackbar;

import java.util.Objects;

public class ResultActivity extends AppCompatActivity implements IGameConstants, IHighScoreDialogListener, ILogConstants {

    Button btnAgain, btnExit;
    TextView txtYourScore;
    int player_score, player_time, player_rank, player_streaks;
    HighScore highScore;
    ScoreSet scoreSet;
    Receiver receiver = null;

    @SuppressLint("StringFormatMatches")
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        btnAgain = findViewById(R.id.btnAgain);
        btnExit = findViewById(R.id.btnClose);
        txtYourScore = findViewById(R.id.txtYourScore);

        setupLogo();

        // getting data from the intent
        Intent i = getIntent();
        player_score = i.getIntExtra(USER_SCORE, 0);
        player_time = i.getIntExtra(USER_TIME, 0);
        player_streaks = i.getIntExtra(USER_STREAKS, 0);

        txtYourScore.setText(String.format(getString(R.string._yourscore), player_score));

        scoreSet = GameCore.getScoreSet();
        String scoreKey = scoreSet.addScore(player_score, player_time, "", player_streaks);
        player_rank = scoreSet.getRank(scoreKey);
        GameCore.saveDataStructure(scoreSet);

        Log.d(LOG_TAG, "ResultActivity.class: Key for ranking NEW " + scoreKey);
        Log.d(LOG_TAG, "ResultActivity.class: RankSet NEW " + player_rank);

        // dialog here
        if(player_rank != -1) {
            AlertDialog dlg = DialogWrapper.getHighScoreInputDialog(ResultActivity.this, player_rank, scoreKey);
            Objects.requireNonNull(dlg).show();
        }

        btnExit.setOnClickListener(v -> finish());
        btnAgain.setOnClickListener(v -> startGame());

        getOnBackPressedDispatcher().addCallback(new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                // custom stuff here which is in this case nothing
            }
        });

    }

    private void setupLogo(){

        Objects.requireNonNull(getSupportActionBar()).setDisplayShowCustomEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        LayoutInflater infl = LayoutInflater.from(this);
        @SuppressLint("InflateParams") View v = infl.inflate(R.layout.actionbar, null);

        TextView tvActionBarMainTitle = v.findViewById(R.id.tvActionBarMainTitle);
        tvActionBarMainTitle.setText(R.string._results);
        tvActionBarMainTitle.setTextColor(getColor(R.color.white));
        TextView tvActionBarSubTitle = v.findViewById(R.id.tvActionBarSubTitle);
        tvActionBarSubTitle.setText(R.string._topten);
        tvActionBarSubTitle.setTextColor(getColor(R.color.white));
        ImageView ivActionBarLogo = v.findViewById(R.id.ivActionBarLogo);
        ImageView ivActionBarActionIcon = v.findViewById(R.id.ivActionBarActionIcon);

        ivActionBarActionIcon.setVisibility(View.INVISIBLE);
        ivActionBarLogo.setImageResource(R.drawable.owl_note_small);

        // the get the custom layout to use full width
        this.getSupportActionBar().setCustomView(v, new Toolbar.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

    }

    @Override
    protected void onPause() {
        super.onPause();
        if (receiver != null){
            this.unregisterReceiver(receiver);
            receiver = null;
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.TIRAMISU)
    @Override
    protected void onResume() {
        super.onResume();
        if(receiver == null){
            receiver = new Receiver();
            receiver.setHighScoreDialogListener(this);
            this.registerReceiver(receiver, getFilter(), Context.RECEIVER_EXPORTED);
        }
        this.registerReceiver(receiver, getFilter(), Context.RECEIVER_EXPORTED);
    }

    private void startGame(){
        // first parameter = from, second parameter = what to start
        Intent i = new Intent(ResultActivity.this, MainActivity.class);
        startActivity(i); // run it
        finish(); // close this activity
    }

    private IntentFilter getFilter(){
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(HIGHSCORE_ACTION); // only register receiver for this event
        return intentFilter;
    }
// todo omzetten naar nieuwe datatsructuur
    @SuppressLint("RestrictedApi")
    @Override
    public void processHighScoreName(String name, String key) {

        Log.d(LOG_TAG, String.format("ResultActivity.class: (processHighScoreName) update high score entry with name %s and key %s", name, key));

        scoreSet = GameCore.getScoreSet();
        scoreSet.setPlayerName(key, name);
        GameCore.saveDataStructure(scoreSet);

        GameCore.getGlobalRanking();

        try {
            showSnackBar();
        } catch(Exception e){
            Log.d(LOG_TAG, "ResultActivity.class: (processHighScoreName) --> " + e.getMessage());
        }

    }

    @SuppressLint("RestrictedApi")
    private void showSnackBar(){

        int marginFromSides = 15; // margins for the snackbar relative to the screen edge
        View view = findViewById(R.id.layoutResultParentLayout); //the layout needs to have an id so it can be found
        Snackbar snackbar = Snackbar.make(view, R.string._newhighscore, Snackbar.LENGTH_INDEFINITE);

        Snackbar.SnackbarLayout s_layout = (Snackbar.SnackbarLayout) snackbar.getView();
        TextView s_textView = s_layout.findViewById(com.google.android.material.R.id.snackbar_text); // get the snackbar textview
        Button s_button = s_layout.findViewById(com.google.android.material.R.id.snackbar_action); // get the snackbar button

        //s_layout.setBackground(getResources().getDrawable(R.drawable.rounded_corners)); // use special shape xml for rounded corners
        s_layout.setBackground(ResourcesCompat.getDrawable(ResultActivity.this.getResources(), R.drawable.rounded_corners, null));
        FrameLayout.LayoutParams parentParams = (FrameLayout.LayoutParams) s_layout.getLayoutParams();
        parentParams.setMargins(marginFromSides, 0, marginFromSides, marginFromSides); // set space between screen edge and snackbar edge to 15
        parentParams.height = FrameLayout.LayoutParams.WRAP_CONTENT; // increase height to the (new) textview height
        parentParams.width = FrameLayout.LayoutParams.MATCH_PARENT; // the snackbar will span the entire width
        s_layout.setLayoutParams(parentParams);

        s_textView.setTextColor(getResources().getColor(R.color.WhiteSmoke)); // set the text color

        Typeface typefaceAM = ResourcesCompat.getFont(ResultActivity.this, R.font.atma_medium);
        s_textView.setTypeface(typefaceAM, Typeface.BOLD);

        // s_textView.setTypeface(s_textView.getTypeface(), Typeface.BOLD); // set the text to bold
        s_textView.setTextSize(20); // increase text size

        // make sure the height of the textview shows the complete text since it was increased
        LinearLayout.LayoutParams textViewLayoutParams = (LinearLayout.LayoutParams) s_textView.getLayoutParams();
        textViewLayoutParams.height = LinearLayout.LayoutParams.WRAP_CONTENT;
        s_textView.setLayoutParams(textViewLayoutParams);

        s_button.setBackgroundColor(getResources().getColor(R.color.holo_blue_dark));
        s_button.setTextColor(getResources().getColor(R.color.WhiteSmoke));
        s_button.setTextSize(14);

        Typeface typefaceA = ResourcesCompat.getFont(ResultActivity.this, R.font.atma);
        s_button.setTypeface(typefaceA);

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