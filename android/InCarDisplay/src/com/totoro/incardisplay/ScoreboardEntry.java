package com.totoro.incardisplay;


import com.totoro.incardisplay.ScoreboardEntry;

/**
 *  Class representing an individual scoreboard entry
 */
public class ScoreboardEntry implements Comparable<ScoreboardEntry> {

	// Attributes for a ScoreboardEntry
	private String id;
	private String name;
	private int score;
	
	public ScoreboardEntry (String id, String name, int score) {
		setId(id);
		setName(name);
		setScore(score);
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getScore() {
		return score;
	}

	public void setScore(int score) {
		this.score = score;
	}

	@Override
	public int compareTo(ScoreboardEntry another) {
		// Returns a negative integer, zero, or a positive integer as this object
		// is less than, equal to, or greater than the specified object.
		return this.score - another.score;
	}
	
}

