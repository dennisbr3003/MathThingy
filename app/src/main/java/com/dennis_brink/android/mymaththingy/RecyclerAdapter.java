package com.dennis_brink.android.mymaththingy;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.dennis_brink.android.mymaththingy.gamecore.Score;

import java.util.ArrayList;

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.HighScoreViewHolder> implements ILogConstants {

    private final ArrayList<Score> scoreSetAsArrayList;
    private final ArrayList<Integer> imageList;
    private Context context; // for the toaster

    public RecyclerAdapter(ArrayList<Score> scoreSetAsArrayList, ArrayList<Integer> imageList, Context context) {
        this.scoreSetAsArrayList = scoreSetAsArrayList;
        this.imageList = imageList;
        this.context = context;
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
            holder.textViewHighScoreName.setText(scoreSetAsArrayList.get(position).getName());
            holder.textViewDetails.setText(String.valueOf(scoreSetAsArrayList.get(position).getCreated()));
            holder.textHighScore.setText((String.valueOf(scoreSetAsArrayList.get(position).getScore())));
            holder.textHighScoreStreaks.setText((String.valueOf(scoreSetAsArrayList.get(position).getStreaks())));
            holder.textHighScoreTime.setText((String.valueOf(scoreSetAsArrayList.get(position).getTime())));
            holder.imageRank.setImageResource(imageList.get(position));
            holder.tvGlobalScore.setText(getGlobalScoreText(scoreSetAsArrayList.get(position)));
            holder.tvSynced.setText(getSyncedText(scoreSetAsArrayList.get(position)));
            holder.tvNotOnline.setVisibility(getScoreIsSyncedOnline(scoreSetAsArrayList.get(position)));
        } catch (Exception e){
            Log.d(LOG_TAG, "RecyclerAdapter.class: (onBindViewHolder) --> " + e.getMessage());
        }

    }

    private String getSyncedText(Score score) {
        Log.d(LOG_TAG, "leeg? "+ score.getGl_sync().isEmpty());
        if(!score.getGl_sync().isEmpty()){
            return String.format("Synced: %s", score.getGl_sync());
        } else {
            return "";
        }
    }

    private String getGlobalScoreText(Score score) {
        if(score.isGl_loaded()){
            return String.valueOf(score.getGl_rank());
        } else {
            return ""; // this will effectively hide the textView
        }
    }

    private int getScoreIsSyncedOnline(Score score) {
        if(score.isGl_loaded()){
            return View.INVISIBLE; // not visible
        }
        return View.VISIBLE; // visible
    }

    @Override
    public int getItemCount() {
        return scoreSetAsArrayList.size();
    }

    public static class HighScoreViewHolder extends RecyclerView.ViewHolder {
        private TextView textViewHighScoreName,textViewDetails,textHighScore,textHighScoreStreaks,textHighScoreTime,tvGlobalScore,tvSynced,tvNotOnline;
        private ImageView imageRank;
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
                tvGlobalScore = itemView.findViewById(R.id.tvGlobalScore);
                tvSynced = itemView.findViewById(R.id.tvSynced);
                tvNotOnline = itemView.findViewById(R.id.tvNotOnline);
            } catch (Exception e){
                Log.d(LOG_TAG, "RecyclerAdapter.class: (HighScoreViewHolder) --> " + e.getMessage());
            }

        }
    }

}
