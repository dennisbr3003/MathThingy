package com.dennis_brink.android.mymaththingy;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentManager;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Objects;

public class MainActivity extends AppCompatActivity implements IGameConstants, ILogConstants {
    FragmentManager fragmentManager = getSupportFragmentManager();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        setupLogo();

        // todo: check here if there is an online registration show another fragment asking to register
        // todo: webclient check on internet access could end up here using a broadcast-receiver method

        fragmentManager.beginTransaction()
                .add(R.id.fragmentContainerMainActivity, MainMenuFragment.class, null)
                .commit();

    }

    private void setupLogo(){
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowCustomEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        LayoutInflater infl = LayoutInflater.from(this);
        View v = infl.inflate(R.layout.actionbar, null);

        TextView tvActionBarMainTitle = v.findViewById(R.id.tvActionBarMainTitle);
        tvActionBarMainTitle.setText(R.string._gamemenu);
        tvActionBarMainTitle.setTextColor(getColor(R.color.white));
        TextView tvActionBarSubTitle = v.findViewById(R.id.tvActionBarSubTitle);
        tvActionBarSubTitle.setText(R.string._selectdifficulty);
        tvActionBarSubTitle.setTextColor(getColor(R.color.white));
        ImageView ivActionBarActionIcon = v.findViewById(R.id.ivActionBarActionIcon);
        ivActionBarActionIcon.setVisibility(View.INVISIBLE);
        // the get the custom layout to use full width
        this.getSupportActionBar().setCustomView(v, new Toolbar.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

    }

}