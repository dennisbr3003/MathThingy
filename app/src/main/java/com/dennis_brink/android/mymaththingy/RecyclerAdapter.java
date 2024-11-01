package com.dennis_brink.android.mymaththingy;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.dennis_brink.android.mymaththingy.gamecore.Score;

import java.util.ArrayList;
import java.util.Set;

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.HighScoreViewHolder> implements ILogConstants {

    private Set<HighScore.HighScoreEntry> full_list;
    // private ArrayList<HighScore.HighScoreEntry> full_list_array_list;
    private final ArrayList<Score> full_list_array_list;
    private final ArrayList<Integer> imageList;
    //private Context context; // for the toaster

//    public RecyclerAdapter(ArrayList<HighScore.HighScoreEntry> full_list_array_list, ArrayList<Integer> imageList, Context context) {
//        this.full_list_array_list = full_list_array_list;
//        this.imageList = imageList;
//        this.context = context;
//    }

    public RecyclerAdapter(ArrayList<Score> full_list_array_list, ArrayList<Integer> imageList, Context context) {
        this.full_list_array_list = full_list_array_list;
        this.imageList = imageList;
        //this.context = context;
    }

    @NonNull
    @Override
    // this gets called the moment the inner class CountryViewHolder gets created
    public RecyclerAdapter.HighScoreViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // parent would be the HighScoreActivity. We could also use context. That would have the context passed from the MainActivity
        // so it is the same value.The view is created here based on the card_design XML layout
        try {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.highscore_card_design, parent, false);
            return new HighScoreViewHolder(view);
        } catch (Exception e){
            Log.d(LOG_TAG, "RecyclerAdapter.class: (onCreateViewHolder) --> " + e.getMessage());
            return null;
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerAdapter.HighScoreViewHolder holder, int position) {

        try {
            holder.textViewHighScoreName.setText(full_list_array_list.get(position).getName());
            holder.textViewDetails.setText(String.valueOf(full_list_array_list.get(position).getCreated()));
            holder.textHighScore.setText((String.valueOf(full_list_array_list.get(position).getScore())));
            holder.textHighScoreStreaks.setText((String.valueOf(full_list_array_list.get(position).getStreaks())));
            holder.textHighScoreTime.setText((String.valueOf(full_list_array_list.get(position).getTime())));
            holder.imageRank.setImageResource(imageList.get(position));
        } catch (Exception e){
            Log.d(LOG_TAG, "RecyclerAdapter.class: (onBindViewHolder) --> " + e.getMessage());
        }

    }

    @Override
    public int getItemCount() {
        return full_list_array_list.size();
    }

    public class HighScoreViewHolder extends RecyclerView.ViewHolder {

        // we need a holder for the card_design

        private TextView textViewHighScoreName,textViewDetails,textHighScore,textHighScoreStreaks,textHighScoreTime;
        private ImageView imageRank;
        private CardView cardView;

        public HighScoreViewHolder(@NonNull View itemView) {
            super(itemView);
            // itemView is actually the card_design and so itemView "has" al the controls on it we need to fill
            try {
                textViewHighScoreName = itemView.findViewById(R.id.textViewHighScoreName);
                textViewDetails = itemView.findViewById(R.id.textViewDescription);
                textHighScore = itemView.findViewById(R.id.txtHighScore);
                textHighScoreStreaks = itemView.findViewById(R.id.txtHighScoreStreaks);
                textHighScoreTime = itemView.findViewById(R.id.txtHighScoreTime);
                imageRank = itemView.findViewById(R.id.imageRank);
                cardView = itemView.findViewById(R.id.cardViewListItem);
            } catch (Exception e){
                Log.d(LOG_TAG, "RecyclerAdapter.class: (HighScoreViewHolder) --> " + e.getMessage());
            }

        }
    }

}
