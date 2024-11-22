package com.dennis_brink.android.mymaththingy;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Locale;
import java.util.Objects;
import java.util.Random;

public class GameActivity extends AppCompatActivity implements IGameConstants, IGameActivityListener, ILogConstants {

    TextView txtScore,txtLife,txtTime,txtQuestion,txtStreak;
    EditText etxtNumberAnswer;

    Button btnOk,btnNext;

    Boolean lSubmitted = false;
    Boolean lLaunchSoftKeyboard = false;
    Boolean timerIsRunning;
    Boolean lQuestionActive = false;

    Receiver receiver = null;
    Random random = new Random();
    CountDownTimer countDownTimer;

    int number1,number2;

    Game game = new Game();

    long time_left_in_millies = START_TIMER_IN_MILLIS;

    String operator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        // getting data from the intent
        Intent i = getIntent();
        operator = i.getStringExtra(GAME_MODE);   // add, sub, multi

        Log.d(LOG_TAG, "GameActivity.class: (onCreate) Operator " + operator);

        setupLogo();

        initGameValues();

        etxtNumberAnswer.setOnEditorActionListener((v, actionId, event) -> {

            if(actionId == EditorInfo.IME_ACTION_DONE || actionId == EditorInfo.IME_NULL || event.getKeyCode() == KeyEvent.KEYCODE_ENTER){
                if(!lQuestionActive){
                    Log.d(LOG_TAG, "GameActivity.class: (btnOk.onClick) No active question, nothing to submit");
                    return true;
                }
                lSubmitted = true; // set switch
                lQuestionActive = false; // reset switch

                Log.d(LOG_TAG, "GameActivity.class: (etxtNumberAnswer.onEditorAction) Answer submitted by keyboard <ok> " + lSubmitted);

                try { // fails if no calculatedAnswer is given or something not numerical
                    game.setUserAnswer(Integer.parseInt(etxtNumberAnswer.getText().toString()));
                    processAnswer();
                } catch(Exception e){
                    Log.d(LOG_TAG, "GameActivity.class: (etxtNumberAnswer.onEditorAction) Answer submitted was not numerical (int)");
                    processIncorrectInput(2);
                }
                return true;
            } else {
                return false;
            }
        });

        btnOk.setOnClickListener(v -> {
            if(!lQuestionActive){
                Log.d(LOG_TAG, "GameActivity.class: (btnOk.onClick) No active question, nothing to submit");
                return;
            }
            lSubmitted = true; // set switch
            lQuestionActive = false; // reset switch
            Log.d(LOG_TAG, "GameActivity.class: (btnOk.onClick) Answer submitted by btnOk <Submit>" + lSubmitted);

            try { // fails if no answer is given or the answer is something not numerical
                game.setUserAnswer(Integer.parseInt(etxtNumberAnswer.getText().toString()));
                processAnswer();
            } catch(Exception e){
                Log.d(LOG_TAG, "GameActivity.class: (btnOk.onClick) Answer submitted was not numerical (int)");
                processIncorrectInput(2);
            }
        });

        btnNext.setOnClickListener(v -> {

            Log.d(LOG_TAG, "GameActivity.class: (btnNext.onClick) Answer submitted " + lSubmitted);
            Log.d(LOG_TAG, "GameActivity.class: (btnNext.onClick) First question  " +  btnNext.getText().equals(FIRST_USE));

            if((btnNext.getText().equals(FIRST_USE)) || lLaunchSoftKeyboard){
                Log.d(LOG_TAG, "GameActivity.class: (btnNext.onClick) First use, launch keyboard programmatically");
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
            }

            if((!lSubmitted) && (!btnNext.getText().equals(FIRST_USE))){
                Log.d(LOG_TAG, "GameActivity.class: (btnNext.onClick) Answer was not submitted nor is it the first question --> deduct life");
                processIncorrectInput(2);
            } else{
                Log.d(LOG_TAG, "GameActivity.class: (btnNext.onClick) Answer was submitted or it's a first use --> do not deduct life");
            }

            lSubmitted = false; // reset switch
            lLaunchSoftKeyboard = false; // reset switch
            lQuestionActive = true; // set switch
            getQuestion();
            etxtNumberAnswer.requestFocus(); // switches the keyboard to numerical and gives focus to the correct field
        });

        getOnBackPressedDispatcher().addCallback(new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                // custom stuff here
                Intent i = new Intent(GameActivity.this, MainActivity.class); // from class --> to class
                try {
                    startActivity(i); // run it
                    finish(); // close this one
                } catch (Exception e){
                    Log.d(LOG_TAG, "GameActivity.class: (onBackPressed) --> " + e.getMessage());
                }
            }
        });

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
                game.setCalculatedAnswer(number1 + number2);
                break;
            case OPERATOR_SUB:
                if(number1 < number2){ // switch to avoid negative numbers
                    txtQuestion.setText(String.format("%s %s %s", number2, "-", number1));
                } else {
                    txtQuestion.setText(String.format("%s %s %s", number1, "-", number2));
                }
                game.setCalculatedAnswer(Math.abs(number1 - number2));
                break;
            case OPERATOR_MULTI:
                number1 = random.nextInt(10); // too difficult otherwise for 15 secs of time
                txtQuestion.setText(String.format("%s %s %s", number1, "*", number2));
                game.setCalculatedAnswer(number1 * number2);
                break;
            default:
                txtQuestion.setText(R.string._error);
                game.setCalculatedAnswer(-1);
                break;
        }
        startTimer();
    }

    @SuppressLint("StringFormatMatches")
    public void processAnswer(){

        pauseTimer();

        //userTime += ((int)(START_TIMER_IN_MILLIS / 1000) - ((time_left_in_millies / 1000) % 60)); // time taken to get answer
        game.incrementUserTime((int) ((START_TIMER_IN_MILLIS / 1000) - ((time_left_in_millies / 1000) % 60)));

        Log.d(LOG_TAG, "GameActivity.class: (processAnswer) User answer (Session singleton) " + game.getUserAnswer());
        Log.d(LOG_TAG, "GameActivity.class: (processAnswer) Calculated answer (Session singleton) " + game.getCalculatedAnswer());

        etxtNumberAnswer.setText("");
        etxtNumberAnswer.setVisibility(View.INVISIBLE);

        if(game.getUserAnswer() == game.getCalculatedAnswer()) {

            // correct
            txtQuestion.setText(String.format(getString(R.string._correctanswer), game.getUserAnswer()));
            game.incrementCorrectAnswerStreak();
            game.incrementUserScore(CORRECT_ANSWER);

            updateScoreBoard();

            if(game.getCorrectAnswerStreak() == STREAK){

                game.incrementUserLives(); // extra life with 10 good answers
                game.setCorrectAnswerStreak(0);
                game.incrementUserStreaks();
                game.incrementUserScore(game.getUserStreaks() * STREAK_BONUS); // bonus with 10 good answers

                InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE); // hide keyboard first
                imm.hideSoftInputFromWindow(etxtNumberAnswer.getWindowToken(), 0);

                AlertDialog dlg = DialogWrapper.getStreakMessageDialog(GameActivity.this, game.getUserStreaks());
                if(dlg!=null)dlg.show();

            }

            Log.d(LOG_TAG, "GameActivity.class: (processAnswer) Correct answer. Streakcounter is set to " + game.getCorrectAnswerStreak());

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
        InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(etxtNumberAnswer.getWindowToken(), 0);

        // first parameter = from, second parameter what to start
        Intent i = new Intent(GameActivity.this, ResultActivity.class);

        Log.d(LOG_TAG, "GameActivity.class: (startResult) Starting result intent with "  + finalScore + " and " + finalTime);

        try {
            i.putExtra(USER_SCORE, finalScore);
            i.putExtra(USER_TIME, finalTime);
            i.putExtra(USER_STREAKS, finalStreaks);
            startActivity(i); // run it
            finish(); // close this one
        } catch (Exception e){
            Log.d(LOG_TAG, "GameActivity.class: (startResult) --> " + e.getMessage());
        }
    }

    private IntentFilter getFilter(){
        IntentFilter intentFilter = new IntentFilter();
        Log.d(LOG_TAG, "GameActivity.class: (getFilter) Registering for broadcast action " + SOFTKEYBOARD_ACTION + " and " + EXIT_GAME_ACTION);
        intentFilter.addAction(SOFTKEYBOARD_ACTION); // only register receiver for this event
        intentFilter.addAction(EXIT_GAME_ACTION);
        return intentFilter;
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (receiver != null){
            Log.d(LOG_TAG, "GameActivity.class: (onPause) Unregistering receiver");
            this.unregisterReceiver(receiver);
            receiver = null;
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.TIRAMISU)
    @Override
    protected void onResume() {
        super.onResume();
        if(receiver == null){
            Log.d(LOG_TAG, "GameActivity.class: (onResume) Registering receiver");
            receiver = new Receiver();
            receiver.setGameActivityListener(this);
        }
        this.registerReceiver(receiver, getFilter(), Context.RECEIVER_EXPORTED);
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

        game.setUserLives(NUM_LIVES);
        game.setUserScore(0);
        game.setCorrectAnswerStreak(0);

        etxtNumberAnswer.setVisibility(View.INVISIBLE);
        txtQuestion.setText(R.string._hitfirstquestion);
        btnNext.setText(FIRST_USE);

    }

    @SuppressLint("StringFormatMatches")
    private void processIncorrectInput(int displayText){

        Log.d(LOG_TAG, "GameActivity.class: (processIncorrectInput) Set to display text " +displayText);

        switch(displayText){
            case 1:
                txtQuestion.setText(String.format(getString(R.string._wronganswer), game.getUserAnswer(), game.getCalculatedAnswer()));
                break;
            case 2:
                txtQuestion.setText(String.format(getString(R.string._nonnumerical), game.getUserAnswer()));
                break;
            case 3:
                txtQuestion.setText(R.string._snailpace);
                break;
            default:
                txtQuestion.setText(R.string._computersaysno);
                break;
        }

        etxtNumberAnswer.setVisibility(View.INVISIBLE);

        pauseTimer();

        game.decreaseUserLives();
        game.setCorrectAnswerStreak(0);

        txtLife.setText(String.valueOf(game.getUserLives()));
        txtStreak.setText(String.valueOf(game.getCorrectAnswerStreak()));

        // if userLives = 0 then game over...
        if(game.getUserLives() == 0){
            txtQuestion.setText("");
            startResult(game.getUserScore(), game.getUserTime(), game.getUserStreaks());
        }

    }

    private void updateScoreBoard(){
        txtLife.setText(String.valueOf(game.getUserLives()));
        txtStreak.setText(String.valueOf(game.getCorrectAnswerStreak()));
        txtScore.setText(String.valueOf(game.getUserScore()));
    }

    @Override
    public void launchSoftKeyBoard() {
        Log.d(LOG_TAG, "GameActivity.class: (launchSoftKeyBoard) Streak completed, dialog was closed. Launch soft keyboard");
        lLaunchSoftKeyboard = true;
        updateScoreBoard();
    }

    private void setupLogo(){

        Objects.requireNonNull(getSupportActionBar()).setDisplayShowCustomEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        LayoutInflater infl = LayoutInflater.from(this);
        View v = infl.inflate(R.layout.actionbar, null);

        TextView tvActionBarMainTitle = v.findViewById(R.id.tvActionBarMainTitle);
        tvActionBarMainTitle.setText(R.string._appname);
        tvActionBarMainTitle.setTextColor(getColor(R.color.white));
        TextView tvActionBarSubTitle = v.findViewById(R.id.tvActionBarSubTitle);
        tvActionBarSubTitle.setText(R.string._humancalculator);
        tvActionBarSubTitle.setTextColor(getColor(R.color.white));
        ImageView ivActionBarActionIcon = v.findViewById(R.id.ivActionBarActionIcon);
        ivActionBarActionIcon.setImageResource(R.drawable.exit2); // override back arrow

        ivActionBarActionIcon.setOnClickListener(view -> {
            AlertDialog dlg = DialogWrapper.getExitConfirmDialog(GameActivity.this);
            if (dlg != null) dlg.show();
        });

        // the get the custom layout to use full width
        this.getSupportActionBar().setCustomView(v, new Toolbar.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

    }

    @Override
    public void exitGame() {
        pauseTimer();
        startResult(game.getUserScore(), game.getUserTime(), game.getUserStreaks());
    }

    private class Game{

        private int userAnswer,calculatedAnswer,userScore,userTime,userLives,userStreaks,correctAnswerStreak;

        public Game() {
            initGame();
        }

        public int getUserAnswer() {
            return userAnswer;
        }

        public void setUserAnswer(int userAnswer) {
            this.userAnswer = userAnswer;
        }

        public int getCalculatedAnswer() {
            return calculatedAnswer;
        }

        public void setCalculatedAnswer(int calculatedAnswer) {
            this.calculatedAnswer = calculatedAnswer;
        }

        public int getUserScore() {
            return userScore;
        }

        public void setUserScore(int userScore) {
            this.userScore = userScore;
        }

        public void incrementUserScore(int userScore) {
            this.userScore += userScore;
        }

        public int getUserTime() {
            return userTime;
        }

        public void incrementUserTime(int userTime) {
            this.userTime += userTime;
        }

        public int getUserLives() {
            return userLives;
        }

        public void setUserLives(int userLives) {
            this.userLives = userLives;
        }

        public void incrementUserLives() {
            this.userLives++;
        }

        public void decreaseUserLives() {
            this.userLives--;
        }
        public int getUserStreaks() {
            return userStreaks;
        }

        public void incrementUserStreaks() {
            this.userStreaks++;
        }

        public int getCorrectAnswerStreak() {
            return correctAnswerStreak;
        }

        public void setCorrectAnswerStreak(int correctAnswerStreak) {
            this.correctAnswerStreak = correctAnswerStreak;
        }

        public void incrementCorrectAnswerStreak() {
            this.correctAnswerStreak++;
        }

        @NonNull
        @Override
        public String toString() {
            return "Game{" +
                    "userAnswer=" + userAnswer +
                    ", calculatedAnswer=" + calculatedAnswer +
                    ", userScore=" + userScore +
                    ", userTime=" + userTime +
                    ", userLives=" + userLives +
                    ", userStreaks=" + userStreaks +
                    ", correctAnswerStreak=" + correctAnswerStreak +
                    '}';
        }

        private void initGame(){
            userAnswer=0;
            calculatedAnswer=0;
            userScore = 0;
            userStreaks = 0;
            userTime = 0;
            correctAnswerStreak = 0;
        }

    }
}