package com.dennis_brink.android.mymaththingy;

import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.github.clans.fab.FloatingActionButton;

import java.util.ArrayList;

public class HighScoreActivity extends AppCompatActivity implements IGameConstants, IHighScoreDeleteListener, ILogConstants {

    HighScore highScore;
    private RecyclerView recyclerView;
    private RecyclerAdapter adapter;
    private ArrayList<Integer>imageList = new ArrayList<>();
    private ArrayList<HighScore.HighScoreEntry> full_list_as_array = null;
    private TextView txtWinners;
    Receiver receiver = null;

    private FloatingActionButton floatingActionButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_high_score);

        floatingActionButton = findViewById(R.id.fabDelete);
        txtWinners = findViewById(R.id.txtNoWinners);
        highScore = new HighScore(HighScoreActivity.this, HIGHSCORE_LISTLENGTH);

        try {
            full_list_as_array = highScore.getSetAsArray();
            setWinnerTextView(full_list_as_array);
        } catch(Exception e){
            Log.d(LOG_TAG, "HighScoreActivity.class: (onCreate) --> " + e.getMessage());
        }

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(HighScoreActivity.this, LinearLayoutManager.VERTICAL, false)); // horizontal

        loadImageList();

        adapter = new RecyclerAdapter(full_list_as_array, imageList, HighScoreActivity.this);
        recyclerView.setAdapter(adapter);

        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // show dialog
                if (full_list_as_array.size() != 0) {
                    AlertDialog dlg = DialogWrapper.getDeleteConfirmDialog(HighScoreActivity.this);
                    dlg.show();
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

    private void setWinnerTextView(ArrayList<HighScore.HighScoreEntry> full_list_as_array){
        if (full_list_as_array.size() == 0){
            // show "no winners" textview
            txtWinners.setVisibility(View.VISIBLE);
        } else {
            txtWinners.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void deleteHighScoreList() {

        full_list_as_array.clear(); // clear the high score arraylist used in the activity
        Log.d(LOG_TAG, "HighScoreActivity.class: (deleteHighScoreList) Local arraylist cleared");
        highScore.clearSet(); // clear the TreeSet and save it to file
        Log.d(LOG_TAG, "HighScoreActivity.class: (deleteHighScoreList) Saved TreeSet cleared and written");
        adapter.notifyDataSetChanged();  // notify adapter to refresh itself
        Log.d(LOG_TAG, "HighScoreActivity.class: (deleteHighScoreList) Adapter notified of changes in data");
        setWinnerTextView(full_list_as_array); // show/hide textview

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

    @Override
    protected void onResume() {
        super.onResume();
        if(receiver == null){
            Log.d(LOG_TAG, "HighScoreActivity.class: (onResume) Registering receiver");
            receiver = new Receiver();
            receiver.setHighScoreDeleteDialogListener(this);
        }
        this.registerReceiver(receiver, getFilter());
    }


}