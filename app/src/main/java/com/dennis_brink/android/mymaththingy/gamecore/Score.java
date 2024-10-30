package com.dennis_brink.android.mymaththingy.gamecore;

import android.annotation.SuppressLint;

import androidx.annotation.NonNull;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Locale;
import java.util.UUID;

public class Score implements Comparable<Score>, Serializable, IGameCore {

    private static final long serialVersionUID = -6048047469895770785L;

    private String name, id, created, callSign;
    private Integer score, time, streaks, rank, gl_rank;
    private boolean loaded;

    public Score() {
        this.loaded = false; // default not uploaded to MongoDB
        initScore();
    }

    public Score(String name, int score, int time, int streaks) {
        this.loaded = false; // default not uploaded to MongoDB
        this.name = name;
        this.score = score;
        this.time = time;
        this.streaks = streaks;
        initScore();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCreated() {
        return created;
    }

    public void setCreated(String created) {
        this.created = created;
    }

    public String getCallSign() {
        return callSign;
    }

    public void setCallSign(String callSign) {
        this.callSign = callSign;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public int getTime() {
        return time;
    }

    public void setTime(int time) {
        this.time = time;
    }

    public int getStreaks() {
        return streaks;
    }

    public void setStreaks(int streaks) {
        this.streaks = streaks;
    }

    public int getRank() {
        return rank;
    }

    public void setRank(int rank) {
        this.rank = rank;
    }

    public int getGl_rank() {
        return gl_rank;
    }

    public void setGl_rank(int gl_rank) {
        this.gl_rank = gl_rank;
    }

    public boolean isLoaded() {
        return loaded;
    }

    public void setLoaded(boolean loaded) {
        this.loaded = loaded;
    }

    @SuppressLint("DefaultLocale")
    private void initScore() {

        this.id = UUID.randomUUID().toString();
        this.gl_rank = -1;
        this.callSign = "";

        Calendar localDateTime = Calendar.getInstance(Locale.getDefault());

        this.created = String.format("%02d/%02d/%04d %02d:%02d",
                localDateTime.get(Calendar.DAY_OF_MONTH),
                localDateTime.get(Calendar.MONTH) + 1, // 0-based, january = month 0
                localDateTime.get(Calendar.YEAR),
                localDateTime.get(Calendar.HOUR_OF_DAY),
                localDateTime.get(Calendar.MINUTE));
    }

    @Override
    public int compareTo(Score o) {

        // Ranking is determined by -->
        // 1. score = number of points (higher is better)
        // 2. time = time used to get the correct answers (lower is better)
        // 3. streaks = number of times the user answered 10 consecutive questions correctly (higher is better)
        // 4. name = user name; in case of equal score this is used to determine ranking in alphabetical order
        // 5. id = GUID; in case of equal score and equal name this is used to determine ranking in alphabetical order

        if(score > o.score){ // greater = 1, equal = 0 smaller = -1
            return -1;
        }
        if (score < o.score) {
            return 1;
        }
        if(!(time.equals(o.time))) {
            return time.compareTo(o.time);
        }
        else {
            if(!(streaks.equals(o.streaks))) {
                return (-1 * streaks.compareTo(o.streaks));
            } else {
                if(!(name.equals(o.name))){
                    return name.compareTo(o.name);
                } else {
                    return id.compareTo(o.id);
                }
            }
        }
    }

    @NonNull
    @Override
    public String toString() {
        return "Score{" +
                "name='" + name + '\'' +
                ", id='" + id + '\'' +
                ", created='" + created + '\'' +
                ", callSign='" + callSign + '\'' +
                ", score=" + score +
                ", time=" + time +
                ", streaks=" + streaks +
                ", rank=" + rank +
                ", gl_rank=" + gl_rank +
                ", loaded=" + loaded +
                '}';
    }
}
