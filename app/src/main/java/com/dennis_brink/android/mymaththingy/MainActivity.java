package com.dennis_brink.android.mymaththingy;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentManager;

import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.dennis_brink.android.mymaththingy.gamecore.GameCore;
import com.dennis_brink.android.mymaththingy.gamecore.Profile;
import com.dennis_brink.android.mymaththingy.main.MainMenuFragment;
import com.dennis_brink.android.mymaththingy.main.NotRegisteredFragment;

import java.util.Objects;

public class MainActivity extends AppCompatActivity implements ILogConstants {
    FragmentManager fragmentManager = getSupportFragmentManager();
    ImageView ivMathBook;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        ivMathBook = findViewById(R.id.ivMathBook);

        setupLogo();
        getSupportFragmentManager().popBackStack();
        loadMainMenuFragment();

    }

    @RequiresApi(api = Build.VERSION_CODES.TIRAMISU)
    @Override
    protected void onResume() {

        super.onResume();

        Profile profile = GameCore.getProfile();
        if(!(profile.isCompeteOnline() && profile.isRegistered()) && profile.isShowRegistrationFragment() && profile.getPlaymode() != 0) {
            setupLogoNotRegisteredText();
            ivMathBook.setVisibility(View.GONE);
            // this runs after onCreate so this code will replace the menu fragment if needed.
            fragmentManager.beginTransaction()
                    .replace(R.id.fragmentContainerMainActivity, NotRegisteredFragment.class, null, "regis")
                    .setReorderingAllowed(true)
                    .commit();
        }
    }

    private void setupLogo(){

        Objects.requireNonNull(getSupportActionBar()).setDisplayShowCustomEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        LayoutInflater inflater = LayoutInflater.from(this);
        View v = inflater.inflate(R.layout.actionbar, null);

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

    private void setupLogoNotRegisteredText() {
        TextView tvTitle = findViewById(R.id.tvActionBarMainTitle);
        TextView tvSubTitle = findViewById(R.id.tvActionBarSubTitle);
        tvTitle.setText(R.string._regcomptitle);
        tvSubTitle.setText(R.string._playstylesub);
    }

    public void loadMainMenuFragment() {
        ivMathBook.setVisibility(View.VISIBLE);
        fragmentManager.beginTransaction()
                .replace(R.id.fragmentContainerMainActivity, MainMenuFragment.class, null, "menu")
                .setReorderingAllowed(true)
                .commit();
    }

}