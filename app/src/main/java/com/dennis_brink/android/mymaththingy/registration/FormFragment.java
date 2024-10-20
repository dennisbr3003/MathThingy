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
import com.dennis_brink.android.mymaththingy.profile.AppProfile;
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
    // GameProfile gameProfile;
    ArrayList<LanguageSpinnerItem> languageSpinnerItemArrayList;
    private Runnable runnable;
    private Handler handler;
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

        //Intent i = requireActivity().getIntent();
        //gameProfile = (GameProfile) i.getSerializableExtra("CONFIG");

        webClient = new WebClient(AppContext.getContext());
        webClient.initWebClient();

        btnRegisterNow = v.findViewById(R.id.btnRegisterNow);
        cbUpsertOnline = v.findViewById(R.id.cbUpsertOnline);
        tvOnlineState = v.findViewById(R.id.tvOnlineState);
        etDisplayName = v.findViewById(R.id.etDisplayName);
        etCallSign = v.findViewById(R.id.etCallSign);
        etEmailAddress = v.findViewById(R.id.etEmailAddress);

        // Spinner
        spin = v.findViewById(R.id.spinLanguage);
        spin.setOnItemSelectedListener(this);

        // fill spinner item array list
        createLanguageItemList();

        // attach adapter to spinner
        CustomAdapter customAdapter = new CustomAdapter(requireActivity().getApplicationContext(), languageSpinnerItemArrayList);
        spin.setAdapter(customAdapter);

        // set spinner to saved item
        AtomicInteger idx = new AtomicInteger(0);
        languageSpinnerItemArrayList.forEach((languageSpinnerItem) -> {
            if(languageSpinnerItem.getIsoCode().equals(AppProfile.getInstance().getPlayer().getLanguage())){
                spin.setSelection(idx.get(), true);
            }
            idx.getAndIncrement();
        });

        etDisplayName.setText(AppProfile.getInstance().getPlayer().getDisplayName());
        etCallSign.setText(AppProfile.getInstance().getPlayer().getCallSign());
        etEmailAddress.setText(AppProfile.getInstance().getPlayer().getEmail());

        cbUpsertOnline.setChecked(AppProfile.getInstance().getGameProfile().isDoUpsertOnline());
        if(AppProfile.getInstance().getGameProfile().isRegistered())tvOnlineState.setText(R.string._registered);
        else tvOnlineState.setText(R.string._notregistered);

        btnRegisterNow.setOnClickListener(view -> saveRegistration());

        return v;

    }

    private void saveRegistration() {

        AppProfile.getInstance().getGameProfile().setDoUpsertOnline(cbUpsertOnline.isChecked()); // save the checkbox value
        LanguageSpinnerItem languageSpinnerItem = (LanguageSpinnerItem) spin.getSelectedItem();

        AppProfile.getInstance().getGameProfile().setConfigValues(
                etCallSign.getText().toString(),
                etDisplayName.getText().toString(),
                etEmailAddress.getText().toString(),
                cbUpsertOnline.isChecked(),
                languageSpinnerItem.getIsoCode()
        );

        // FileHelper.writeData(gameProfile, requireActivity());
        AppProfile.getInstance().saveGameProfile();

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
                webClient.savePlayer(AppProfile.getInstance().getPlayer());
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
        //AppProfile.getInstance().getPlayer().setLanguage(languageSpinnerItemArrayList.get(i).getIsoCode());
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

}