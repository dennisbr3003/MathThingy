package com.dennis_brink.android.mymaththingy;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity implements IGameConstants, ILogConstants {

    Button btnAdd, btnSub, btnMulti, btnHigh, btnExit, btnGoToRegistration;
    GameProfile gameProfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.d(LOG_TAG, "MainActivity.class: (onCreate) Start Math Thingy");

        setContentView(R.layout.activity_main);
        setupLogo();

        gameProfile = FileHelper.readData(MainActivity.this);

        btnAdd = findViewById(R.id.btnAdd);
        btnSub = findViewById(R.id.btnSubtract);
        btnMulti = findViewById(R.id.btnMultiply);
        btnHigh = findViewById(R.id.btnHighScores);
        btnExit = findViewById(R.id.btnExit);
        btnGoToRegistration = findViewById(R.id.btnGoToRegistration);

        btnAdd.setOnClickListener(v -> startGameByMode(OPERATOR_ADD));

        btnSub.setOnClickListener(v -> startGameByMode(OPERATOR_SUB));

        btnMulti.setOnClickListener(v -> startGameByMode(OPERATOR_MULTI));

        btnHigh.setOnClickListener(v -> startHighScore());

        btnGoToRegistration.setOnClickListener(v -> {
            startRegistration(gameProfile);
        });

        btnExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }

    private void startGameByMode(String mode){
        // first parameter = from, second parameter what to start, where to
        Intent i = new Intent(MainActivity.this, GameActivity.class);
        i.putExtra(GAME_MODE, mode); // Add, Sub, Multi
        startActivity(i); // run it
        finish(); // close this activity
    }

    private void startHighScore(){
        // first parameter = from, second parameter what to start, where to
        Intent i = new Intent(MainActivity.this, HighScoreActivity.class);
        startActivity(i); // run it
        //finish(); // close this activity
    }

    private void startRegistration(GameProfile config) {
        Intent i = new Intent(MainActivity.this, RegisterActivity.class); // from --> to
        i.putExtra("CONFIG", FileHelper.readData(MainActivity.this)); // send this parameter
        startActivity(i); // run it
        // finish(); // close this activity
    }
    private void setupLogo(){
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setLogo(R.drawable.mt_logo_padding_main);
        getSupportActionBar().setTitle(getString(R.string._appname));
        getSupportActionBar().setSubtitle(getString(R.string._favoperator));
        getSupportActionBar().setDisplayUseLogoEnabled(true);
    }

}