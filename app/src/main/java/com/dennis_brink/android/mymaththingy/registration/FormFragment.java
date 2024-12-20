package com.dennis_brink.android.mymaththingy.registration;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.activity.OnBackPressedCallback;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.dennis_brink.android.mymaththingy.gamecore.AppContext;
import com.dennis_brink.android.mymaththingy.gamecore.GameCore;
import com.dennis_brink.android.mymaththingy.gamecore.Player;
import com.dennis_brink.android.mymaththingy.gamecore.Profile;
import com.dennis_brink.android.mymaththingy.spinner.CustomAdapter;
import com.dennis_brink.android.mymaththingy.IRegistrationConstants;
import com.dennis_brink.android.mymaththingy.spinner.LanguageSpinnerItem;
import com.dennis_brink.android.mymaththingy.R;
import com.dennis_brink.android.mymaththingy.WebClient;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;

public class FormFragment extends Fragment implements AdapterView.OnItemSelectedListener, IRegistrationConstants {
    WebClient webClient;
    Button btnRegisterNow;
    TextView tvOfflineTitle, tvOfflineExplanation;
    EditText etDisplayName, etCallSign, etEmailAddress;
    CheckBox cbCompeteOnline;
    TextView tvCurrentSetting;
    Spinner spin;
    ArrayList<LanguageSpinnerItem> languageSpinnerItemArrayList;
    private Runnable runnable;
    private Handler handler;
    Player player;
    Profile profile;

    public FormFragment() {
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
        View v = inflater.inflate(R.layout.fragment_form, container, false);

        webClient = new WebClient(AppContext.getContext());
        webClient.initWebClient();

        btnRegisterNow = v.findViewById(R.id.btnRegisterNow);
        cbCompeteOnline = v.findViewById(R.id.cbUpsertOnline);
        etDisplayName = v.findViewById(R.id.etDisplayName);
        etCallSign = v.findViewById(R.id.etCallSign);
        etEmailAddress = v.findViewById(R.id.etEmailAddress);
        tvCurrentSetting = v.findViewById(R.id.tvCurrentSetting);
        tvOfflineTitle = v.findViewById(R.id.tvOfflineTitle);
        tvOfflineExplanation = v.findViewById(R.id.tvOfflineExplanation);
        player = GameCore.getPlayer();
        profile = GameCore.getProfile();

        setGamePlayModeText();
        initSpinner(v);

        etDisplayName.setText(player.getDisplayName());
        etCallSign.setText(player.getCallSign());
        etEmailAddress.setText(player.getEmail());

        cbCompeteOnline.setChecked(profile.isCompeteOnline());
        setOnlineOfflineText();

        btnRegisterNow.setOnClickListener(view -> saveRegistration());

        cbCompeteOnline.setOnClickListener(view -> {
            setOnlineOfflineText();
        });

        // needed when you press back and the timer is not finished we have to stop the timer from
        // running in the background and executing an automated back press, this would close the app :(
        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(), new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                // custom stuff here
                Log.d("DENNIS_B", "Save profile in FormFragment.class - handleOnBackPress");
                checkGamePlayMode();
                GameCore.saveDataStructure(profile);
                requireActivity().finish(); // invoking back press proved unstable
            }
        });

        return v;

    }

    private void checkGamePlayMode() {
        if(player.isEmpty()) {
            profile.setPlaymode(0); // anonymously
            profile.setCompeteOnline(false);
        } else {
            if(!profile.isRegistered()) { // in case of back press after registration. Do not reset the play mode
                profile.setPlaymode(1); // locally
            }
        }
    }

    private void saveRegistration() {

        boolean useWebClient = false;

        LanguageSpinnerItem languageSpinnerItem = (LanguageSpinnerItem) spin.getSelectedItem();

        player.setCallSign(etCallSign.getText().toString());
        player.setDisplayName(etDisplayName.getText().toString());
        player.setEmail(etEmailAddress.getText().toString());
        player.setLanguage(languageSpinnerItem.getIsoCode());

        // no data = anonymously, otherwise you play locally
        checkGamePlayMode();

        if (profile.isCompeteOnline() || cbCompeteOnline.isChecked()) {
            // currently we are online so we need to check values online
            // 1. if isCompeteOnLine is true but cbCompeteOnline = unchecked we need delete online values
            // 2. if isCompeteOnLine is true and cbCompeteOnline = checked we need to update online values
            // 3. if isCompeteOnLine is false but cbCompeteOnline = checked we need upload local values
            // 4. if isCompeteOnLine is false and cbCompeteOnline = unchecked we need to save locally
            useWebClient = true;
        }

        profile.setCompeteOnline(cbCompeteOnline.isChecked());

        if(!useWebClient){
            // save any changes and updates locally
            GameCore.saveDataStructure(player);
            GameCore.saveDataStructure(profile);
            sendRegistrationSuccess();
            return;
        }

        // show progress bar fragment, this could take some time
        FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.fragmentContainerView, ProgressFragment.class, null)
                .commit();

        // We need to show the progress fragment everytime we save something with the API.
        // If the API is not online a time out will occur after 10 seconds. To avoid the user
        // thinking the app is crashing we show a progress bar running. A successful result (API
        // is online and the save is successful) is THAT fast that showing the progress fragment
        // without a delay looks like a flickering. Hence we delay execution of the actual save
        // with 1 second with a runnable and a handler. If the online action is completed we update
        // player and profile. To do so we send the object to the webclient which handles API calls
        // and knows if a call ended successfully or not

        handler = new Handler();
        runnable = () -> {
            if(cbCompeteOnline.isChecked()) webClient.savePlayerAndScores(player, profile);
            else webClient.deletePlayerAndScores(player, profile);
        };
        handler.postDelayed(runnable, 1000);
    }

    private void createLanguageItemList() {
        languageSpinnerItemArrayList = new ArrayList<>();
        languageSpinnerItemArrayList.add(new LanguageSpinnerItem(R.drawable.nl, "Nederlands", "NL"));
        languageSpinnerItemArrayList.add(new LanguageSpinnerItem(R.drawable.de, "Deutsch", "DE"));
        languageSpinnerItemArrayList.add(new LanguageSpinnerItem(R.drawable.en, "English", "EN"));
        languageSpinnerItemArrayList.add(new LanguageSpinnerItem(R.drawable.fr, "Français", "FR"));
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {
    }

    private void sendRegistrationSuccess() {
        Intent i = new Intent();
        i.setAction(LOCAL_REGISTRATION_SUCCESS);
        requireActivity().sendBroadcast(i);
    }

//    private void sendRegistrationFailure(String msg) {
//        Intent i = new Intent();
//        i.setAction(LOCAL_REGISTRATION_FAILURE);
//        i.putExtra("MSG", msg);
//        requireActivity().sendBroadcast(i);
//    }

    private void setGamePlayModeText(){
        switch(profile.getPlaymode()) {
            case 0:
                tvCurrentSetting.setText(getResources().getString(R.string._current_play_mode,ANONYMOUSLY));
                break;
            case 1:
                tvCurrentSetting.setText(getResources().getString(R.string._current_play_mode,LOCALLY));
                break;
            case 2:
                tvCurrentSetting.setText(getResources().getString(R.string._current_play_mode,ONLINE));
                break;
        }
    }

    private void setOnlineOfflineText(){
        if(cbCompeteOnline.isChecked()){
            tvOfflineTitle.setText(R.string._offline);
            tvOfflineExplanation.setText(R.string._explanation);
        } else {
            tvOfflineTitle.setText(R.string._online);
            tvOfflineExplanation.setText(R.string._onLineExplanation);
        }
    }

    private void initSpinner(View v) {

        // Spinner
        spin = v.findViewById(R.id.spinLanguage);
        spin.setOnItemSelectedListener(this);

        // fill spinner item array list
        createLanguageItemList();

        // attach adapter to spinner
        CustomAdapter customAdapter = new CustomAdapter(AppContext.getContext(), languageSpinnerItemArrayList);
        spin.setAdapter(customAdapter);

        // set spinner to saved item
        AtomicInteger idx = new AtomicInteger(0);
        languageSpinnerItemArrayList.forEach((languageSpinnerItem) -> {
            if(languageSpinnerItem.getIsoCode().equals(player.getLanguage())) {
                spin.setSelection(idx.get(), true);
            }
            idx.getAndIncrement();
        });
    }

}