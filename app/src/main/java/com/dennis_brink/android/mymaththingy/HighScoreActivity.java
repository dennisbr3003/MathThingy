package com.dennis_brink.android.mymaththingy;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.dennis_brink.android.mymaththingy.gamecore.GameCore;
import com.dennis_brink.android.mymaththingy.gamecore.Score;
import com.dennis_brink.android.mymaththingy.gamecore.ScoreSet;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.clans.fab.FloatingActionButton;

import java.util.ArrayList;
import java.util.Objects;

public class HighScoreActivity extends AppCompatActivity implements IGameConstants, IHighScoreDeleteListener, ILogConstants {

    HighScore highScore;
    private RecyclerView recyclerView;
    private RecyclerAdapter adapter;
    private final ArrayList<Integer> imageList = new ArrayList<>();
    private ArrayList<HighScore.HighScoreEntry> full_list_as_array = null;
    private ArrayList<Score> scoreSetAsArray;
    private TextView txtWinners;
    Receiver receiver = null;
    private FloatingActionButton floatingActionButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_high_score);

        setupLogo();

        floatingActionButton = findViewById(R.id.fabDelete);
        txtWinners = findViewById(R.id.txtNoWinners);

        GameCore.getGlobalRanking();

        try {

            ScoreSet scoreSet = GameCore.getScoreSet();
            Log.d(LOG_TAG, "HighScoreActivity.class: (onCreate) --> " + scoreSet.getScores());
            scoreSetAsArray = scoreSet.getSetAsArray();

            String sBody;

            ObjectMapper objectMapper = new ObjectMapper();
            sBody = objectMapper.writeValueAsString(scoreSetAsArray);
            Log.d(LOG_TAG, sBody);

            setWinnerTextView(scoreSetAsArray.isEmpty());
        } catch(Exception e){
            Log.d(LOG_TAG, "HighScoreActivity.class: (onCreate) --> " + e.getMessage());
        }

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(HighScoreActivity.this, LinearLayoutManager.VERTICAL, false)); // horizontal

        loadImageList();

//        adapter = new RecyclerAdapter(full_list_as_array, imageList, HighScoreActivity.this);
        adapter = new RecyclerAdapter(scoreSetAsArray, imageList, HighScoreActivity.this);
        recyclerView.setAdapter(adapter);


        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // show dialog
                if (!scoreSetAsArray.isEmpty()) {
                    AlertDialog dlg = DialogWrapper.getDeleteConfirmDialog(HighScoreActivity.this);
                    Objects.requireNonNull(dlg).show();
                }
            }
        });

    }

    private void loadImageList(){

        imageList.add(R.drawable.nr1);
        imageList.add(R.drawable.nr2);
        imageList.add(R.drawable.nr3);
        imageList.add(R.drawable.nr4);
        imageList.add(R.drawable.nr5);
        imageList.add(R.drawable.nr6);
        imageList.add(R.drawable.nr7);
        imageList.add(R.drawable.nr8);
        imageList.add(R.drawable.nr9);
        imageList.add(R.drawable.nr10);

    }

    private void setWinnerTextView(boolean emptySet){

        Log.d(LOG_TAG, "empty set ? " + emptySet);

        if (emptySet) {
            Log.d(LOG_TAG, "no winners tonen? " + emptySet);
            txtWinners.setVisibility(View.VISIBLE);
        } else {
            txtWinners.setVisibility(View.INVISIBLE);
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    @Override
    public void deleteHighScoreList() {

        full_list_as_array.clear(); // clear the high score arraylist used in the activity
        Log.d(LOG_TAG, "HighScoreActivity.class: (deleteHighScoreList) Local arraylist cleared");
        highScore.clearSet(); // clear the TreeSet and save it to file
        Log.d(LOG_TAG, "HighScoreActivity.class: (deleteHighScoreList) Saved TreeSet cleared and written");
        adapter.notifyDataSetChanged();  // notify adapter to refresh itself
        Log.d(LOG_TAG, "HighScoreActivity.class: (deleteHighScoreList) Adapter notified of changes in data");
        setWinnerTextView(scoreSetAsArray.isEmpty()); // show/hide textview

    }

    private IntentFilter getFilter(){
        IntentFilter intentFilter = new IntentFilter();
        Log.d(LOG_TAG, "HighScoreActivity.class: (getFilter) Registering for broadcast action " + HIGHSCORE_DELETE_ACTION);
        intentFilter.addAction(HIGHSCORE_DELETE_ACTION); // only register receiver for this event
        return intentFilter;
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (receiver != null){
            Log.d(LOG_TAG, "HighScoreActivity.class: (onPause) Unregistering receiver");
            this.unregisterReceiver(receiver);
            receiver = null;
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.TIRAMISU)
    @Override
    protected void onResume() {
        super.onResume();
        if(receiver == null){
            Log.d(LOG_TAG, "HighScoreActivity.class: (onResume) Registering receiver");
            receiver = new Receiver();
            receiver.setHighScoreDeleteDialogListener(this);
        }
        this.registerReceiver(receiver, getFilter(), Context.RECEIVER_EXPORTED);
    }

    private void setupLogo(){
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setLogo(R.drawable.mt_logo_padding_highscore);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setSubtitle("");
        getSupportActionBar().setDisplayUseLogoEnabled(true);
    }

}