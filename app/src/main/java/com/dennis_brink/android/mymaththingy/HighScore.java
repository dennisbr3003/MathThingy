package com.dennis_brink.android.mymaththingy;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;

import org.apache.commons.lang3.SerializationUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;
import java.util.Set;
import java.util.TreeSet;
import java.util.UUID;

public class HighScore {

	private Set<HighScoreEntry> full_list = new TreeSet<>();
	private Context context;
	private int numberOfElements;

	private void setFull_list(Set<HighScoreEntry> full_list) {
		this.full_list = full_list;
	}

	public HighScore(Context context) {
		this.context = context;
		loadHighScoresFromFile();
	}

	public HighScore(Context context, int numberOfElements) {
		this.context = context;
		loadHighScoresFromFile();
		this.numberOfElements = numberOfElements;
	}

	private void rankSet() {
		int i = 1;
		for (HighScoreEntry highScoreEntry : full_list) {
			highScoreEntry.setRank(i++);
		}
	}		

	public void printSet() {
		for(HighScoreEntry highScoreEntry: full_list){
			Log.d("DENNIS_B", "Highscores " + highScoreEntry.toString());
		}
	}

	private void loadHighScoresFromFile() {
		setFull_list(FileWrapper.readData(this.context));
	}

	private void writeHighScoresToFile() {
		FileWrapper.writeData(this.full_list, this.context);
	}

	private void trimHighScoreSet(){
		full_list = getSubSet(0, numberOfElements - 1);
	}

	private Set<HighScoreEntry> getSubSet(int startElement, int endElement) {
		Set<HighScoreEntry> target = new TreeSet<>();
		int i=0;
		for(HighScoreEntry highScoreEntry: full_list){			
			if ((i >= startElement) && (i <= endElement)){
				Log.d("DENNIS_B", "HighScore.class: (getSubSet) Copying " + highScoreEntry.getName() + " rank " + highScoreEntry.getRank());
				target.add(SerializationUtils.clone(highScoreEntry)); // deep copy (not a reference)
			}
			i++;
		}
		return target;
	}

	public ArrayList getSetAsArray() {
		return new ArrayList<>(full_list);
	}

	public int getRank(String key) {

		Log.d("DENNIS_B", "HighScore.class: (getRank) Getting rank with key " + key);
		if(!full_list.equals(null)) {
			printSet();
			for (HighScoreEntry highScoreEntry : full_list) {
				if (highScoreEntry.getId().equals(key)) {
					Log.d("DENNIS_B", "HighScore.class: (getRank) Finding object with id " + key +  " with rank " + highScoreEntry.getRank());
					return highScoreEntry.getRank();
				}
			}
			Log.d("DENNIS_B", "HighScore.class: (getRank) Could not find object with id " + key);
			return -1; // not a valid key, object with key not found
		} else{
			Log.d("DENNIS_B", "HighScore.class: (getRank) Null collection");
			return -1; // empty collection, object with key not found
		}
	}

	public void setPlayerName(String key, String name) {
		try {
			for (HighScoreEntry highScoreEntry : full_list) {
				if (highScoreEntry.getId().equals(key)) {
					highScoreEntry.setName(name);
					break;
				}
			}
		} finally {
			writeHighScoresToFile(); // on file version equals to memory version now
		}
	}

	public void clearSet() {
		try {
			this.full_list.clear();
			Log.d("DENNIS_B", "HighScore.class: (clearSet) Executed");
		} finally {
			writeHighScoresToFile();
		}
	}
	
	public String addHighScoreEntry (int score, int time, String name, int streaks) {
		try {
			HighScoreEntry highScoreEntry = new HighScoreEntry(score, time,  streaks, name);
			this.full_list.add(highScoreEntry);
			rankSet();
			Log.d("DENNIS_B", "HighScore.class: (addHighScoreEntry) Added high score object with id " + highScoreEntry.getId() +  " with rank " + highScoreEntry.getRank());
			if(full_list.size() > this.numberOfElements) {
				Log.d("DENNIS_B", "HighScore.class: (addHighScoreEntry) Number of elements, " + full_list.size() + ", is greater then the maximum number of objects allowed (" + this.numberOfElements + "); start to trim the set");
				trimHighScoreSet(); // reduce it to 10 elements or less
			}
			return highScoreEntry.getId(); // return id just created
		} finally{
			writeHighScoresToFile(); // on file version equals to memory version now
		}
	}
	
	static class HighScoreEntry implements Comparable<HighScoreEntry>, Serializable {

		private static final long serialVersionUID = 527845683817498201L;
		
		private int score;
		private Integer time;
		private Integer streaks;
		private String name;
		private int rank; 
		private String id;
		private Calendar localDateTime;
		private String displayTime;

		public HighScoreEntry() {
			super();
			setValues();
		}

		public HighScoreEntry(int score, int time, int streaks, String name) {
			super();
			this.score = score;
			this.time = time;
			this.name = name;
			this.streaks = streaks;
			setValues();
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

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public int getRank() {
			return rank;
		}

		public void setRank(int rank) {
			this.rank = rank;
		}

		public String getId() {
			return id;
		}

		public int getStreaks() {
			return streaks;
		}

		public void setStreaks(int streaks) {
			this.streaks = streaks;
		}

		public String getDisplayTime() {
			return displayTime;
		}

		@SuppressLint("DefaultLocale")
		private void setValues() {
			
			this.id = UUID.randomUUID().toString();
			this.localDateTime = Calendar.getInstance(Locale.getDefault());

			this.displayTime = String.format("%02d/%02d/%04d %02d:%02d",
							    			  	this.localDateTime.get(Calendar.DAY_OF_MONTH),
												this.localDateTime.get(Calendar.MONTH) + 1, // 0-based, january = month 0
												this.localDateTime.get(Calendar.YEAR),
												this.localDateTime.get(Calendar.HOUR_OF_DAY),
												this.localDateTime.get(Calendar.MINUTE));
		}

		@Override
		public int compareTo(HighScoreEntry o) {

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

		@Override
		public String toString() {
			return "HighScoreEntry{" +
					"score=" + score +
					", time=" + time +
					", streaks=" + streaks +
					", name='" + name + '\'' +
					", rank=" + rank +
					", id='" + id + '\'' +
					", displayTime='" + displayTime + '\'' +
					'}';
		}
	}
	
}
