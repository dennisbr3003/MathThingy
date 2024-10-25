package com.dennis_brink.android.mymaththingy.registration;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.activity.OnBackPressedCallback;
import androidx.fragment.app.Fragment;

import com.dennis_brink.android.mymaththingy.R;

import java.util.Locale;

public class ResultFragment extends Fragment {

    boolean onlineRegistration;
    TextView tvResult, tvMsg, tvTime, tvStatus;
    ImageView ivRegisterResult;
    CountDownTimer countDownTimer;
    long time_left_in_millis = 5000;
    Boolean timerIsRunning = false;
    Boolean BackIsPressed = true;
    RelativeLayout rl;
    static int progressBarMaxValue = 5;
    ProgressBar progressBar;

    public ResultFragment() {
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
        View v =  inflater.inflate(R.layout.fragment_result, container, false);

        tvResult = v.findViewById(R.id.tvResult);
        tvMsg = v.findViewById(R.id.tvMsg);
        ivRegisterResult = v.findViewById(R.id.ivRegisterResult);

        progressBar = v.findViewById(R.id.pbRegisterResult);
        tvTime = v.findViewById(R.id.tvTime);
        tvStatus = v.findViewById(R.id.tvStatus);

        rl = v.findViewById(R.id.progressBarLayout);
        tvTime.setText(String.format(Locale.getDefault(), "%02d", progressBarMaxValue));
        progressBar.setMax(progressBarMaxValue);
        progressBar.setProgress(progressBarMaxValue);

        Intent i = requireActivity().getIntent();
        onlineRegistration = i.getBooleanExtra("ONLINE_REGISTRATION", false);

        if(onlineRegistration) {
            tvResult.setText(R.string.registration_successful);
            ivRegisterResult.setImageResource(R.drawable.checkok);
        } else {
            tvResult.setText(R.string.registration_failure);
            ivRegisterResult.setImageResource(R.drawable.error);
            tvMsg.setVisibility(View.VISIBLE);
            tvMsg.setText(i.getStringExtra("MSG"));
        }

        rl.setOnClickListener(view -> {
            if(!timerIsRunning) {
                startTimer();
                tvStatus.setText(R.string.tab_to_pause);
            } else {
                pauseTimer();
                tvStatus.setText(R.string.tab_to_resume);
            }
        });

        // needed when you press back and the timer is not finished we have to stop the timer from
        // running in the background and executing an automated back press, this would close the app
        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(), new OnBackPressedCallback(BackIsPressed) {
            @Override
            public void handleOnBackPressed() {
                // custom stuff here
                countDownTimer.cancel(); // this will stop the countdown
                countDownTimer = null; // this kills your timer definitely
                // activity stuff here
                requireActivity().finish(); // invoking back press proved unstable
            }
        });

        // start countdown timer (5 sec)
        startTimer();
        return v;
    }

    public void startTimer(){

        countDownTimer = new CountDownTimer(time_left_in_millis, 1000) { // every  second

            @Override
            public void onTick(long millisUntilFinished) {
                time_left_in_millis = millisUntilFinished;
                updateTimerText();
            }

            @Override
            public void onFinish() {
                timerIsRunning = false;
                pauseTimer();
                updateTimerText();
                // press back programmatically (method available to activity, not to fragment)
                // requireActivity().getOnBackPressedDispatcher().onBackPressed(); // unstable
                requireActivity().finish();
            }

        }.start();
        timerIsRunning = true;
    }

    public void updateTimerText(){
        int second = (int)(time_left_in_millis / 1000);
        tvTime.setText(String.format(Locale.getDefault(), "%02d", second));
        progressBar.setProgress(second, true);
    }

    public void pauseTimer(){
        if(countDownTimer != null) { countDownTimer.cancel(); }
        timerIsRunning = false;
    }
}