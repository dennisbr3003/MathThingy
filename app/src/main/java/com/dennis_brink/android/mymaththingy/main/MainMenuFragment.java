package com.dennis_brink.android.mymaththingy.main;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.dennis_brink.android.mymaththingy.GameActivity;
import com.dennis_brink.android.mymaththingy.HighScoreActivity;
import com.dennis_brink.android.mymaththingy.IGameConstants;
import com.dennis_brink.android.mymaththingy.ILogConstants;
import com.dennis_brink.android.mymaththingy.R;
import com.dennis_brink.android.mymaththingy.RegisterActivity;
import com.dennis_brink.android.mymaththingy.gamecore.AppContext;

public class MainMenuFragment extends Fragment implements IGameConstants, ILogConstants {

    Button btnAdd, btnSub, btnMulti, btnHigh, btnGoToRegistration;
//    btnExit,

    public MainMenuFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_main_menu, container, false);

        btnAdd = v.findViewById(R.id.btnAdd);
        btnSub = v.findViewById(R.id.btnSubtract);
        btnMulti = v.findViewById(R.id.btnMultiply);
        btnHigh = v.findViewById(R.id.btnHighScores);
        //btnExit = v.findViewById(R.id.btnExit);
        btnGoToRegistration = v.findViewById(R.id.btnGoToRegistration);

        btnAdd.setOnClickListener((View view) -> {
            startGameByMode(OPERATOR_ADD);
        });

        btnSub.setOnClickListener(view -> startGameByMode(OPERATOR_SUB));

        btnMulti.setOnClickListener(view -> startGameByMode(OPERATOR_MULTI));

        btnHigh.setOnClickListener(view -> startHighScore());

        btnGoToRegistration.setOnClickListener(view -> startRegistration());

        //btnExit.setOnClickListener(view -> requireActivity().finish());

        return v;
    }

    private void startGameByMode(String mode){
        // first parameter = from, second parameter what to start, where to
        Intent i = new Intent(AppContext.getContext(), GameActivity.class);
        i.putExtra(GAME_MODE, mode); // Add, Sub, Multi
        startActivity(i); // run it
        requireActivity().finish(); // close this activity
    }

    private void startHighScore(){
        // first parameter = from, second parameter what to start, where to
        Intent i = new Intent(AppContext.getContext(), HighScoreActivity.class);
        startActivity(i); // run it
    }

    private void startRegistration() {
        Intent i = new Intent(AppContext.getContext(), RegisterActivity.class); // from --> to
        startActivity(i); // run it
    }

}