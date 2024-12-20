package com.dennis_brink.android.mymaththingy;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.res.ResourcesCompat;
import androidx.core.text.HtmlCompat;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.text.Spanned;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.dennis_brink.android.mymaththingy.gamecore.GameCore;
import com.dennis_brink.android.mymaththingy.gamecore.Player;
import com.dennis_brink.android.mymaththingy.gamecore.Profile;
import com.dennis_brink.android.mymaththingy.gamecore.ScoreSet;
import com.google.android.material.snackbar.Snackbar;

import java.util.Objects;

public class ResultActivity extends AppCompatActivity implements IGameConstants, ILogConstants, IRegistrationConstants, IRegisterActivityListener {

    Button btnAgain, btnExit;
    TextView txtYourScore;
    int player_score, player_time, player_rank, player_streaks;
    ScoreSet scoreSet;
    Profile profile;
    Player player;
    Receiver receiver = null;
    String callSign, scoreKey;

    @SuppressLint("StringFormatMatches")
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        player = GameCore.getPlayer();
        profile = GameCore.getProfile();
        scoreSet = GameCore.getScoreSet();

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

        callSign = player.getCallSign();

        scoreKey = scoreSet.addScore(player_score, player_time, callSign, player_streaks);
        player_rank = scoreSet.getRank(scoreKey);
        GameCore.saveDataStructure(scoreSet);
        scoreSet = GameCore.getScoreSet(); // refresh object

        // dialog here
        if(player_rank != -1) {
            AlertDialog alertDialog = createInputDialog(ResultActivity.this, player_rank, callSign, scoreKey);
            if (alertDialog != null) alertDialog.show();
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

    private AlertDialog createInputDialog(Context context, int rank, String callSign, String key){

        try {
            return createInputDialog(rank + GameCore.getExtension(rank), key, callSign, context);
        } catch (Exception e){
            Log.d(LOG_TAG, "ResultActivity.class: (getHighScoreInputDialog) --> " + e.getMessage());
        }
        return null;
    }

    private AlertDialog createInputDialog(String full_rank, String key, String callSign, Context context)  {

        AlertDialog.Builder builder = new AlertDialog.Builder(context);

        builder.setCancelable(false);
        LayoutInflater inf = LayoutInflater.from(context);
        View dialogMessage = inf.inflate(R.layout.dialog_highscore_input, null);

        builder.setView(dialogMessage);

        TextView txtHighScoreMessage = dialogMessage.findViewById(R.id.txtHighScoreMessage);
        ImageView imgSaveName = dialogMessage.findViewById(R.id.imgHighScoreOk);
        TextView txtHighScoreError = dialogMessage.findViewById(R.id.txtHighScoreErrorMessage);
        EditText etxtHighScoreName = dialogMessage.findViewById(R.id.etxtHighScoreName);

        // hide error, do not use "gone" because of constraints
        txtHighScoreError.setVisibility(View.INVISIBLE);
        String text = context.getString(R.string._enterhighscore);
        String dynamicText = String.format(text, full_rank);
        Spanned dynamicStyledText = HtmlCompat.fromHtml(dynamicText, HtmlCompat.FROM_HTML_MODE_COMPACT);
        txtHighScoreMessage.setText(dynamicStyledText);

        etxtHighScoreName.setText(callSign);

        AlertDialog dlg = builder.create();

        imgSaveName.setOnClickListener(v -> {
            // check if name is empty, name is mandatory
            if(etxtHighScoreName.getText().toString().isEmpty()){
                txtHighScoreError.setVisibility(View.VISIBLE);
            } else {
                scoreSet.setPlayerName(key, callSign);
                GameCore.saveDataStructure(scoreSet);
                // send this to the internet if the profile is competing online
                if(profile.isCompeteOnline()) GameCore.setGlobalRanking();

                // hide the keyboard
                InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(etxtHighScoreName.getWindowToken(), 0);

                // show snack bar here if we are not competing online
                if(!profile.isCompeteOnline()) showSnackBar();

                // close dialog
                dlg.dismiss();
                // show snack bar with quick access to highscore list
            }

        });

        // setup simple click listener for the filename field,so when the name text field
        // is clicked the error objects are reset for a new attempt -->
        etxtHighScoreName.setOnClickListener(v -> txtHighScoreError.setVisibility(View.INVISIBLE));
        return dlg;

    }

    public static Spanned fromHtml(String source) {
        Log.d(LOG_TAG, "fromHtml " + source);
        return Html.fromHtml(source, Html.FROM_HTML_MODE_LEGACY);
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
            // receiver.setHighScoreDialogListener(this);
            receiver.setRegisterActivityListener(this);
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
        intentFilter.addAction(ONLINE_REGISTRATION_SUCCESS); // only register this activity for these events for the receiver tio handle
        intentFilter.addAction(ONLINE_REGISTRATION_FAILURE);
        intentFilter.addAction(LOCAL_REGISTRATION_SUCCESS);
        intentFilter.addAction(LOCAL_REGISTRATION_FAILURE);
        return intentFilter;
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

    @Override
    public void onlineRegistrationSuccess() {
        showSnackBar();
    }

    @Override
    public void onlineRegistrationFailure(String msg) {
        Log.d(LOG_TAG, "Something went wrong " + msg);
    }

    @Override
    public void localRegistrationSuccess() {

    }

    @Override
    public void localRegistrationFailure(String msg) {

    }
}