package com.dennis_brink.android.mymaththingy.main;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.dennis_brink.android.mymaththingy.MainActivity;
import com.dennis_brink.android.mymaththingy.R;
import com.dennis_brink.android.mymaththingy.RegisterActivity;
import com.dennis_brink.android.mymaththingy.gamecore.AppContext;
import com.dennis_brink.android.mymaththingy.gamecore.GameCore;
import com.dennis_brink.android.mymaththingy.gamecore.Profile;


public class NotRegisteredFragment extends Fragment {

    Button btnSaveGameMode;

    public NotRegisteredFragment() {
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
        View v = inflater.inflate(R.layout.fragment_not_registered, container, false);

        setRadioGroupValue(v);

        btnSaveGameMode = v.findViewById(R.id.btnSaveGameMode);
        btnSaveGameMode.setOnClickListener(view -> {
            saveGameModeAndRun(v);
        });

        return v;

    }

    private void saveGameModeAndRun(View v) {
        // we need to get the selected value in de radiobutton group
        RadioGroup rg = v.findViewById(R.id.rgOptions);
        Profile profile = GameCore.getProfile();

        int count = rg.getChildCount();

        for (int i = 0; i < count; i++) {
            View x = rg.getChildAt(i);
            if (x instanceof RadioButton) {
                if(((RadioButton) x).isChecked()){
                    try {
                        profile.setPlaymode(Integer.parseInt(x.getTag().toString()));
                        switch (profile.getPlaymode()) {
                            case 0:
                                profile.setRegistered(true); // done
                                profile.setShowRegistrationFragment(false); // don't show again
                                break;
                            case 1:
                                profile.setCompeteOnline(false); // goto registration
                                break;
                            case 2:
                                profile.setCompeteOnline(true); // goto registration
                                break;
                        }
                    } catch (Exception e){
                        Log.d("DENNIS_B", "Game mode (0, 1, 2) not saved " + e.getMessage());
                    }
                    GameCore.saveDataStructure(profile);

                    if(profile.getPlaymode() != 0) {
                        // run the registration activity
                        Intent intent = new Intent(AppContext.getContext(), RegisterActivity.class); // from --> to
                        startActivity(intent); // run it

                    } else { // 0 = go back to main menu (= reload with new profile values)
                        ((MainActivity)requireActivity()).loadMainMenuFragment();
                    }
                }
            }
        }
    }

    private void setRadioGroupValue(View v) {

        RadioGroup rg = v.findViewById(R.id.rgOptions);
        Profile profile = GameCore.getProfile();

        int count = rg.getChildCount();

        for (int i = 0; i < count; i++) {
            View x = rg.getChildAt(i);
            if (x instanceof RadioButton) {
               if(Integer.parseInt(x.getTag().toString()) == profile.getPlaymode()){
                   ((RadioButton) x).setChecked(true);
               }
            }
        }
    }

}