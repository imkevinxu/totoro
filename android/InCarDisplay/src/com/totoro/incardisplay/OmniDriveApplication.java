package com.totoro.incardisplay;

import java.util.ArrayList;

import android.app.Application;

import com.facebook.model.GraphUser;

public class OmniDriveApplication extends Application {
	private ArrayList<GraphUser> selectedUsers;
	
	public ArrayList<GraphUser> getSelectedUsers() {
		return selectedUsers;
	}
	
	public void setSelectedUsers(ArrayList<GraphUser> users) {
		selectedUsers = users;
	}

}
