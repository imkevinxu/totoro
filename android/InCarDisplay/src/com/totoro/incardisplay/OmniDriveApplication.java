package com.totoro.incardisplay;

import java.util.ArrayList;
import java.util.List;

import android.app.Application;

import com.totoro.incardisplay.ScoreboardEntry;
import com.facebook.model.GraphUser;

public class OmniDriveApplication extends Application {
	static final String TAG = "OmniDrive";
	private ArrayList<GraphUser> selectedUsers;
	
	// Current logged in FB user and key for saving/restoring during the Activity lifecycle
	private GraphUser currentFBUser;
	private static final String CURRENT_FB_USER_KEY = "current_fb_user";
	
	// List of ordered ScoreboardEntry objects in order from highest to lowest score to
	// be shown in the ScoreboardFragment
	private ArrayList<ScoreboardEntry> scoreboardEntriesList = null;
	private List<GraphUser> friends;

	public GraphUser getCurrentFBUser() {
		return currentFBUser;
	}

	public void setCurrentFBUser(GraphUser currentFBUser) {
		this.currentFBUser = currentFBUser;
	}
	
	public ArrayList<GraphUser> getSelectedUsers() {
		return selectedUsers;
	}
	
	public void setSelectedUsers(ArrayList<GraphUser> users) {
		selectedUsers = users;
	}
	
	public void setFriends(List<GraphUser> friends) {
		this.friends = friends;
	}
	
	public List<GraphUser> getFriends() {
		return friends;
	}
	
	/* Scoreboard */
	public ArrayList<ScoreboardEntry> getScoreboardEntriesList() {
		return scoreboardEntriesList;
	}

	public void setScoreboardEntriesList(ArrayList<ScoreboardEntry> scoreboardEntriesList) {
		this.scoreboardEntriesList = scoreboardEntriesList;
	}

}
