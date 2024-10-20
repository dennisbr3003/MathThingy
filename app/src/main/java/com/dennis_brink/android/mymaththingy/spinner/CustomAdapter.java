package com.dennis_brink.android.mymaththingy.spinner;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

import com.dennis_brink.android.mymaththingy.R;

import java.util.ArrayList;

public class CustomAdapter extends BaseAdapter implements SpinnerAdapter {
    Context context;
    int position;
    String[] countryNames;
    LayoutInflater inflter;
    ArrayList<LanguageSpinnerItem> languageSpinnerItemArrayList;

    public CustomAdapter(Context context, ArrayList<LanguageSpinnerItem> languageSpinnerItemArrayList) {
        this.context = context;
        this.languageSpinnerItemArrayList = languageSpinnerItemArrayList;
        inflter = (LayoutInflater.from(context));
    }

//    public CustomAdapter(@NonNull Context context, int resource, @NonNull ArrayList<LanguageSpinnerItem> languageSpinnerItemArrayList) {
//        super(context, resource, languageSpinnerItemArrayList);
//    }

    @Override
    public int getCount() {
        return languageSpinnerItemArrayList.size();
    }

    @Override
    public Object getItem(int i) {
        return languageSpinnerItemArrayList.get(i);
    }


    public long getItemId(int i)
    {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        view = inflter.inflate(R.layout.language_item, null);
        ImageView icon = view.findViewById(R.id.ivActionBarLogo);
        icon.setPadding(25, 0, 0, 0);
        TextView names = view.findViewById(R.id.tvActionBarMainTitle);
        icon.setImageResource(languageSpinnerItemArrayList.get(i).getFlag());
        names.setText(languageSpinnerItemArrayList.get(i).getLanguage());
        return view;
    }


}