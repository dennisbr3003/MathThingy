package com.dennis_brink.android.mymaththingy;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentManager;

import com.dennis_brink.android.mymaththingy.gamecore.GameCore;
import com.dennis_brink.android.mymaththingy.gamecore.Profile;
import com.dennis_brink.android.mymaththingy.registration.FormFragment;
import com.dennis_brink.android.mymaththingy.registration.ResultFragment;

import java.util.Objects;

public class RegisterActivity extends AppCompatActivity implements IRegisterActivityListener, IRegistrationConstants {

    Receiver receiver = null;
    FragmentManager fragmentManager = getSupportFragmentManager();

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        setupLogo();

        fragmentManager.beginTransaction()
                .add(R.id.fragmentContainerView, FormFragment.class, null)
                .commit();

    }

    private IntentFilter getFilter(){
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ONLINE_REGISTRATION_SUCCESS); // only register this activity for these events for the receiver tio handle
        intentFilter.addAction(ONLINE_REGISTRATION_FAILURE);
        intentFilter.addAction(LOCAL_REGISTRATION_SUCCESS);
        intentFilter.addAction(LOCAL_REGISTRATION_FAILURE);
        return intentFilter;
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
        try {
            if (receiver == null) {
                receiver = new Receiver();
                receiver.setRegisterActivityListener(this);
            }
            this.registerReceiver(receiver, getFilter(), Context.RECEIVER_EXPORTED);
        } catch (Exception e) {
            Log.d("DB1", "RegisterActivity.onResume() error: " + e.getMessage());
        }
    }

    @Override
    public void onlineRegistrationSuccess() {

        Intent i = getIntent();
        i.putExtra("ONLINE_REGISTRATION", true);

        Profile profile = GameCore.getProfile();
        profile.setPlaymode(2);

        Log.d("DENNIS_B", "Save profile in RegisterActivity.class - onlineRegistrationSuccess");
        GameCore.saveDataStructure(profile);

        fragmentManager.beginTransaction()
                .replace(R.id.fragmentContainerView, ResultFragment.class, null)
                .commit();
    }

    @Override
    public void onlineRegistrationFailure(String msg) {

        // getting data from the intent so it will be available for the activity
        Intent i = getIntent();
        i.putExtra("ONLINE_REGISTRATION", false);
        i.putExtra("MSG", msg);

        fragmentManager.beginTransaction()
                .replace(R.id.fragmentContainerView, ResultFragment.class, null)
                .commit();
    }

    @Override
    public void localRegistrationSuccess() {

        Intent i = getIntent();
        i.putExtra("ONLINE_REGISTRATION", true);

        Profile profile = GameCore.getProfile();
        profile.setRegistered(true);
        profile.setShowRegistrationFragment(false);
        Log.d("DENNIS_B", "Save profile in RegisterActivity.class");
        GameCore.saveDataStructure(profile);

        fragmentManager.beginTransaction()
                .replace(R.id.fragmentContainerView, ResultFragment.class, null)
                .commit();
    }

    @Override
    public void localRegistrationFailure(String msg) {

        // getting data from the intent so it will be available for the activity
        Intent i = getIntent();
        i.putExtra("ONLINE_REGISTRATION", false);
        i.putExtra("MSG", msg);

        fragmentManager.beginTransaction()
                .replace(R.id.fragmentContainerView, ResultFragment.class, null)
                .commit();
    }

    private void setupLogo(){

        Objects.requireNonNull(getSupportActionBar()).setDisplayShowCustomEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        LayoutInflater infl = LayoutInflater.from(this);
        View v = infl.inflate(R.layout.actionbar, null);

        TextView tvActionBarMainTitle = v.findViewById(R.id.tvActionBarMainTitle);
        tvActionBarMainTitle.setText(R.string._gameprofile);
        tvActionBarMainTitle.setTextColor(getColor(R.color.white));
        TextView tvActionBarSubTitle = v.findViewById(R.id.tvActionBarSubTitle);
        tvActionBarSubTitle.setText(R.string._setupprofile);
        tvActionBarSubTitle.setTextColor(getColor(R.color.white));
        ImageView ivActionBarActionIcon = v.findViewById(R.id.ivActionBarActionIcon);

        ivActionBarActionIcon.setOnClickListener(view -> {
            getOnBackPressedDispatcher().onBackPressed();
        });

        // the get the custom layout to use full width
        this.getSupportActionBar().setCustomView(v, new Toolbar.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

    }

}