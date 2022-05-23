package com.dennis_brink.android.mymaththingy;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.util.Locale;
import java.util.Random;

public class GameActivity extends AppCompatActivity implements IGameConstants, IGameActivityListener {

    TextView txtScore;
    TextView txtLife;
    TextView txtTime;
    TextView txtQuestion;
    TextView txtStreak;

    EditText etxtNumberAnswer;

    Button btnOk;
    Button btnNext;

    Boolean lSubmitted = false;
    Boolean lLaunchSoftKeyboard = false;
    Boolean timerIsRunning;
    Boolean lQuestionActive = false;

    Receiver receiver = null;
    Random random = new Random();
    CountDownTimer countDownTimer;

    int number1;
    int number2;
    int calculatedAnswer;
    int userScore;
    int userTime;
    int userLives;
    int userStreaks = 0;
    int correctAnswerStreak; // 10 = extra life

    long time_left_in_millies = START_TIMER_IN_MILLIS;

    String operator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        // getting data from the intent
        Intent i = getIntent();
        operator = i.getStringExtra(GAME_MODE);   // add, sub, multi

        Log.d("DENNIS_B", "GameActivity.class: (onCreate) Operator " + operator);

        setupLogo();

        initGameValues();

        etxtNumberAnswer.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {

                if(actionId == EditorInfo.IME_ACTION_DONE || actionId == EditorInfo.IME_NULL || event.getKeyCode() == KeyEvent.KEYCODE_ENTER){
                    if(!lQuestionActive){
                        Log.d("DENNIS_B", "GameActivity.class: (btnOk.onClick) No active question, nothing to submit");
                        return true;
                    }
                    lSubmitted = true; // set switch
                    lQuestionActive = false; // reset switch

                    Log.d("DENNIS_B", "GameActivity.class: (etxtNumberAnswer.onEditorAction) Answer submitted by keyboard <ok> " + lSubmitted);

                    try { // fails if no calculatedAnswer is given or something not numerical
                        processAnswer(Integer.valueOf(etxtNumberAnswer.getText().toString()));
                    } catch(Exception e){
                        Log.d("DENNIS_B", "GameActivity.class: (etxtNumberAnswer.onEditorAction) Answer submitted was not numerical (int)");
                        processIncorrectInput(2);
                    }
                    return true;
                } else {
                    return false;
                }
            }
        });

        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!lQuestionActive){
                    Log.d("DENNIS_B", "GameActivity.class: (btnOk.onClick) No active question, nothing to submit");
                    return;
                }
                lSubmitted = true; // set switch
                lQuestionActive = false; // reset switch
                Log.d("DENNIS_B", "GameActivity.class: (btnOk.onClick) Answer submitted by btnOk <Submit>" + lSubmitted);

                try { // fails if no answer is given or the answer is something not numerical
                    processAnswer(Integer.valueOf(etxtNumberAnswer.getText().toString()));
                } catch(Exception e){
                    Log.d("DENNIS_B", "GameActivity.class: (btnOk.onClick) Answer submitted was not numerical (int)");
                    processIncorrectInput(2);
                }
            }
        });

        btnNext.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View v) {

                Log.d("DENNIS_B", "GameActivity.class: (btnNext.onClick) Answer submitted " + lSubmitted);
                Log.d("DENNIS_B", "GameActivity.class: (btnNext.onClick) First question  " +  btnNext.getText().equals(FIRST_USE));

                if((btnNext.getText().equals(FIRST_USE)) || lLaunchSoftKeyboard){
                    Log.d("DENNIS_B", "GameActivity.class: (btnNext.onClick) First use, launch keyboard programmatically");
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
                }

                if((!lSubmitted) && (!btnNext.getText().equals(FIRST_USE))){
                    Log.d("DENNIS_B", "GameActivity.class: (btnNext.onClick) Answer was not submitted nor is it the first question --> deduct life");
                    processIncorrectInput(2);
                } else{
                    Log.d("DENNIS_B", "GameActivity.class: (btnNext.onClick) Answer was submitted or it's a first use --> do not deduct life");
                }

                lSubmitted = false; // reset switch
                lLaunchSoftKeyboard = false; // reset switch
                lQuestionActive = true; // set switch
                getQuestion();
                etxtNumberAnswer.requestFocus(); // switches the keyboard to numerical and gives focus to the correct field
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_options, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        switch(id){
            case R.id.action_quit_game:
                AlertDialog dlg = DialogWrapper.getExitConfirmDialog(GameActivity.this);
                dlg.show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void getQuestion(){

        number1 = random.nextInt(100);
        number2 = random.nextInt(100);

        etxtNumberAnswer.setText("");
        etxtNumberAnswer.setVisibility(View.VISIBLE);

        btnNext.setText(GAME_USE);

        pauseTimer();
        resetTimer();

        switch(operator){
            case OPERATOR_ADD:
                txtQuestion.setText(String.format("%s %s %s", number1, "+", number2));
                calculatedAnswer = number1 + number2;
                break;
            case OPERATOR_SUB:
                if(number1 < number2){ // switch to avoid negative numbers
                    txtQuestion.setText(String.format("%s %s %s", number2, "-", number1));
                } else {
                    txtQuestion.setText(String.format("%s %s %s", number1, "-", number2));
                }
                calculatedAnswer = Math.abs(number1 - number2);
                break;
            case OPERATOR_MULTI:
                number1 = random.nextInt(10); // too difficult otherwise for 15 secs of time
                txtQuestion.setText(String.format("%s %s %s", number1, "*", number2));
                calculatedAnswer = number1 * number2;
                break;
            default:
                txtQuestion.setText("ERROR");
                break;
        }
        startTimer();
    }

    public void processAnswer(int userAnswer){

        pauseTimer();

        userTime += ((int)(START_TIMER_IN_MILLIS / 1000) - ((time_left_in_millies / 1000) % 60)); // time taken to get answer

        Log.d("DENNIS_B", "GameActivity.class: (processAnswer) User answer " + userAnswer);
        Log.d("DENNIS_B", "GameActivity.class: (processAnswer) Calculated answer " + calculatedAnswer);

        etxtNumberAnswer.setText("");
        etxtNumberAnswer.setVisibility(View.INVISIBLE);

        if(userAnswer == calculatedAnswer) {

            // correct
            txtQuestion.setText(String.format("Congratulations, your answer %s is correct", userAnswer));
            correctAnswerStreak++;
            userScore += CORRECT_ANSWER;

            updateScoreBoard();

            if(correctAnswerStreak == STREAK){

                userLives++; // extra life with 10 good answers
                correctAnswerStreak = 0;
                userStreaks++;
                userScore += (userStreaks * STREAK_BONUS); // bonus with 10 good answers

                InputMethodManager imm = (InputMethodManager) getSystemService(this.INPUT_METHOD_SERVICE); // hide keyboard first
                imm.hideSoftInputFromWindow(etxtNumberAnswer.getWindowToken(), 0);

                AlertDialog dlg = DialogWrapper.getStreakMessageDialog(GameActivity.this, userStreaks);
                dlg.show();

            }

            Log.d("DENNIS_B", "GameActivity.class: (processAnswer) Correct answer. Streakcounter is set to " + correctAnswerStreak);

        } else {
            // wrong, no points, streak is gone
            processIncorrectInput(1);
        }

    }

    public void startTimer(){

        countDownTimer = new CountDownTimer(time_left_in_millies, 1000) { // every second

            @Override
            public void onTick(long millisUntilFinished) {
                time_left_in_millies = millisUntilFinished;
                updateTimerText();
            }

            @Override
            public void onFinish() {
                // same as wrong answer
                timerIsRunning = false;
                pauseTimer();
                updateTimerText();
                processIncorrectInput(3);

            }

        }.start();
        timerIsRunning = true;
    }

    public void updateTimerText(){
        int second = (int)(time_left_in_millies / 1000) %60;
        txtTime.setText(String.format(Locale.getDefault(), "%02d", second));
    }

    public void pauseTimer(){
        if(countDownTimer != null) { countDownTimer.cancel(); }
        timerIsRunning = false;
    }

    public void resetTimer(){
        time_left_in_millies = START_TIMER_IN_MILLIS;
        updateTimerText();
    }

    private void startResult(int finalScore, int finalTime, int finalStreaks){

        // hide keyboard
        InputMethodManager imm = (InputMethodManager) getSystemService(this.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(etxtNumberAnswer.getWindowToken(), 0);

        // first parameter = from, second parameter what to start
        Intent i = new Intent(GameActivity.this, ResultActivity.class);

        Log.d("DENNIS_B", "GameActivity.class: (startResult) Starting result intent with "  + finalScore + " and " + finalTime);

        try {
            i.putExtra(USER_SCORE, finalScore);
            i.putExtra(USER_TIME, finalTime);
            i.putExtra(USER_STREAKS, finalStreaks);
            startActivity(i); // run it
            finish(); // close this one
        } catch (Exception e){
            Log.d("DENNIS_B", "GameActivity.class: (startResult) --> " + e.getMessage());
        }
    }

    private IntentFilter getFilter(){
        IntentFilter intentFilter = new IntentFilter();
        Log.d("DENNIS_B", "GameActivity.class: (getFilter) Registering for broadcast action " + SOFTKEYBOARD_ACTION + " and " + EXIT_GAME_ACTION);
        intentFilter.addAction(SOFTKEYBOARD_ACTION); // only register receiver for this event
        intentFilter.addAction(EXIT_GAME_ACTION);
        return intentFilter;
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (receiver != null){
            Log.d("DENNIS_B", "GameActivity.class: (onPause) Unregistering receiver");
            this.unregisterReceiver(receiver);
            receiver = null;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(receiver == null){
            Log.d("DENNIS_B", "GameActivity.class: (onResume) Registering receiver");
            receiver = new Receiver();
            receiver.setGameActivityListener(this);
        }
        this.registerReceiver(receiver, getFilter());
    }

    private void initGameValues(){

        txtScore = findViewById(R.id.txtScore);
        txtLife = findViewById(R.id.txtLife);
        txtTime = findViewById(R.id.txtTime);
        txtStreak = findViewById(R.id.txtStreak);
        txtQuestion = findViewById(R.id.txtQuestion);
        etxtNumberAnswer = findViewById(R.id.etxtNumberAnswer);

        btnOk = findViewById(R.id.btnOk);
        btnNext = findViewById(R.id.btnNext);

        userLives = NUM_LIVES;
        userScore = 0;
        correctAnswerStreak = 0;

        etxtNumberAnswer.setVisibility(View.INVISIBLE);
        txtQuestion.setText("Get the first question by hitting the 'First Question' button");
        btnNext.setText(FIRST_USE);

    }

    private void processIncorrectInput(int displayText){

        Log.d("DENNIS_B", "GameActivity.class: (processIncorrectInput) Set to display text " +displayText);

        switch(displayText){
            case 1:
                txtQuestion.setText(String.format("So sad, your answer %s is wrong. It should be %s. You loose a life and the streak.", etxtNumberAnswer.getText().toString(), calculatedAnswer));
                break;
            case 2:
                txtQuestion.setText(String.format("So sad, your answer '%s' is totally wrong. It's not even a decent number.", etxtNumberAnswer.getText().toString()));
                break;
            case 3:
                txtQuestion.setText("You're too slow, time is up! This snail pace will cost you a life!");
                break;
            default:
                txtQuestion.setText("Computer says 'no'");
                break;
        }

        etxtNumberAnswer.setVisibility(View.INVISIBLE);

        pauseTimer();

        userLives--;
        correctAnswerStreak = 0;

        txtLife.setText(String.valueOf(userLives));
        txtStreak.setText(String.valueOf(correctAnswerStreak));

        // if userLives = 0 then game over...
        if(userLives == 0){
            txtQuestion.setText("");
            startResult(userScore, userTime, userStreaks);
        }

    }

    private void updateScoreBoard(){
        txtLife.setText(String.valueOf(userLives));
        txtStreak.setText(String.valueOf(correctAnswerStreak));
        txtScore.setText(String.valueOf(userScore));
    }

    @Override
    public void launchSoftKeyBoard() {
        Log.d("DENNIS_B", "GameActivity.class: (launchSoftKeyBoard) Streak completed, dialog was closed. Launch soft keyboard");
        lLaunchSoftKeyboard = true;
        updateScoreBoard();

    }

    private void setupLogo(){
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setLogo(R.drawable.mt_logo_padding);
        getSupportActionBar().setTitle("Math Thingy");
        getSupportActionBar().setSubtitle("Are you the human calculator?");
        getSupportActionBar().setDisplayUseLogoEnabled(true);
    }

    @Override
    public void exitGame() {
        pauseTimer();
        startResult(userScore, userTime, userStreaks);
    }
}