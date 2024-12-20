package com.dennis_brink.android.mymaththingy.gamecore;

import android.util.Log;

import androidx.annotation.NonNull;

import org.apache.commons.lang3.SerializationUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.atomic.AtomicInteger;

public class ScoreSet extends DataStructure implements Serializable, IGameCore {

    private static final long serialVersionUID = -3466108228447407941L;
    private Set<Score> scores = new TreeSet<>();
    public ScoreSet() {
        super(StructureType.SCORE);
    }
    public Set<Score> getScores() {
        return scores;
    }
    public void setScores(Set<Score> scores) {
        this.scores = scores;
    }

    private void rankSet() {
        int i = 1;
        for (Score score : scores) {
            score.setRank(i++);
        }
    }

    public String addScore (int points, int time, String name, int streaks) {
        try {
            Score score = new Score(name, points, time, streaks);
            this.scores.add(score);
            rankSet();
            return score.getId(); // return id just created
        } catch(Exception e) {
            Log.d("DENNIS_B", "Error saving new high score " + e.getMessage());
            return "no_save";
        }
    }

    public int getRank(String key) {
        AtomicInteger rank = new AtomicInteger(-1);
        scores.forEach(score -> {
            if(score.getId().equals(key)){
                rank.set(score.getRank());
            }
        });
        return rank.get();
    }

    public ArrayList<Score> getSubSetAsArray() {
        ArrayList<Score> subset = new ArrayList<>();
        this.scores.forEach((x)->{
            if(!x.isGl_loaded()) subset.add(x);
        });
        return subset;
    }

    public void removeGlobalRanking() {
        for (Score x : this.scores) {
            x.setGl_sync("");
            x.setGl_rank(-1);
            x.setGl_loaded(false);
        }
    }

    public ArrayList<Score> getTop10AsArray() {
        ArrayList<Score> subset = new ArrayList<>();
        int idx = 0;
        for(Score score: this.scores){
            idx++;
            if (idx <= 10) subset.add(SerializationUtils.clone(score)); // deep copy (not a reference)
        }
        return subset;
    }

    public void setPlayerName(String key, String name) {
        scores.forEach(score -> {
            if(score.getId().equals(key)){
                score.setName(name);
            }
        });
    }

    public ArrayList<Score> getSetAsArray() {
        return new ArrayList<>(scores);
    }

    public void updateScoreSetByRanking(RankSet rankSet)  {
        rankSet.getResult().forEach((rank) -> scores.forEach(score -> {
            if(score.getId().equals(rank.getId())){
                score.setGl_rank(rank.getGlRank());
                score.setGl_loaded(true);
                score.setGl_sync(GameCore.getCurrentDisplayDateTime());
            }
        }));
    }

    public void clearScoreSet() {
        this.scores.clear();
    }


    @NonNull
    @Override
    public String toString() {
        return "ScoreSet{" +
                "scores=" + scores +
                '}';
    }
}
