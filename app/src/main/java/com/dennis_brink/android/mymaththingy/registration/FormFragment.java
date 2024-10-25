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

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.dennis_brink.android.mymaththingy.AppContext;
import com.dennis_brink.android.mymaththingy.gamecore.GameCore;
import com.dennis_brink.android.mymaththingy.gamecore.Player;
import com.dennis_brink.android.mymaththingy.gamecore.Profile;
import com.dennis_brink.android.mymaththingy.spinner.CustomAdapter;
import com.dennis_brink.android.mymaththingy.IRegistrationConstants;
import com.dennis_brink.android.mymaththingy.spinner.LanguageSpinnerItem;
import com.dennis_brink.android.mymaththingy.R;
import com.dennis_brink.android.mymaththingy.WebClient;
import com.fasterxml.jackson.core.JsonProcessingException;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;

public class FormFragment extends Fragment implements AdapterView.OnItemSelectedListener, IRegistrationConstants {
    WebClient webClient;
    Button btnRegisterNow;
    EditText etDisplayName, etCallSign, etEmailAddress;
    CheckBox cbUpsertOnline;
    TextView tvOnlineState;
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
        cbUpsertOnline = v.findViewById(R.id.cbUpsertOnline);
        tvOnlineState = v.findViewById(R.id.tvOnlineState);
        etDisplayName = v.findViewById(R.id.etDisplayName);
        etCallSign = v.findViewById(R.id.etCallSign);
        etEmailAddress = v.findViewById(R.id.etEmailAddress);

        player = GameCore.getPlayer();
        profile = GameCore.getProfile();

        initSpinner(v);

        etDisplayName.setText(player.getDisplayName());
        etCallSign.setText(player.getCallSign());
        etEmailAddress.setText(player.getEmail());

        cbUpsertOnline.setChecked(profile.isDoUpsertOnline());
        if(profile.isRegistered())tvOnlineState.setText(R.string._registered);
        else tvOnlineState.setText(R.string._notregistered);

        btnRegisterNow.setOnClickListener(view -> saveRegistration());

        return v;

    }

    private void saveRegistration() {

        LanguageSpinnerItem languageSpinnerItem = (LanguageSpinnerItem) spin.getSelectedItem();

        player.setCallSign(etCallSign.getText().toString());
        player.setDisplayName(etDisplayName.getText().toString());
        player.setEmail(etEmailAddress.getText().toString());
        player.setLanguage(languageSpinnerItem.getIsoCode());
        GameCore.saveDataStructure(player);

        profile.setDoUpsertOnline(cbUpsertOnline.isChecked());
        GameCore.saveDataStructure(profile);

        if(!cbUpsertOnline.isChecked()){
            sendRegistrationSuccess();
            return;
        }

        // show progress bar fragment, this could take some time
        FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.fragmentContainerView, ProgressFragment.class, null)
                .commit();

        // We need to show the progress fragment everytime we save something with teh API.
        // If the API is not online a time out will occur after 10 seconds. To avoid the user
        // thinking the app is crashing we show a progress bar running. A successful result (API
        // is online and the save is successful) is THAT fast that showing the progress fragment
        // without a delay looks like a flickering. Hence we delay execution of the actual save
        // with 1 second with a runnable and a handler
        handler = new Handler();
        runnable = () -> {
            try {
                webClient.savePlayer(player);
            } catch (JsonProcessingException e) {
                sendRegistrationFailure( e.getMessage());
            }
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
        Log.d("DB1", "trying to send registration success");
        Intent i = new Intent();
        i.setAction(LOCAL_REGISTRATION_SUCCESS);
        requireActivity().sendBroadcast(i);
    }

    private void sendRegistrationFailure(String msg) {
        Log.d("DB1", "trying to send registration failure");
        Intent i = new Intent();
        i.setAction(LOCAL_REGISTRATION_FAILURE);
        i.putExtra("MSG", msg);
        requireActivity().sendBroadcast(i);
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