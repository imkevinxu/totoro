package com.totoro.incardisplay;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;
import com.totoro.incardisplay.util.SystemUiHider;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 * 
 * @see SystemUiHider
 */
public class Login extends FragmentActivity {
	
	private static final int LOGIN = 0;
	private static final int SELECTION = 1;
	private static final int SETTINGS = 2;
	private static final int FRAGMENT_COUNT = SETTINGS +1;

	private Fragment[] fragments = new Fragment[FRAGMENT_COUNT];
	/**
	 * Whether or not the system UI should be auto-hidden after
	 * {@link #AUTO_HIDE_DELAY_MILLIS} milliseconds.
	 */
	private MenuItem settings;
	
	private Activity loginActivity;
	private boolean isResumed = false;
	
	private UiLifecycleHelper uiHelper;
	private Session.StatusCallback callback = 
	    new Session.StatusCallback() {
	    @Override
	    public void call(Session session, 
	            SessionState state, Exception exception) {
	        onSessionStateChange(session, state, exception);
	    }
	};
	
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
	  super.onActivityResult(requestCode, resultCode, data);
	  uiHelper.onActivityResult(requestCode, resultCode, data);
	  //Session.getActiveSession().onActivityResult(this, requestCode, resultCode, data);
	}
	
	@Override
	public void onResume() {
	    super.onResume();
	    uiHelper.onResume();
	    isResumed = true;
	}

	@Override
	public void onPause() {
	    super.onPause();
	    uiHelper.onPause();
	    isResumed = false;
	}
	
	@Override
	public void onDestroy() {
	    super.onDestroy();
	    uiHelper.onDestroy();
	}
	
	@Override
	protected void onSaveInstanceState(Bundle outState) {
	    super.onSaveInstanceState(outState);
	    uiHelper.onSaveInstanceState(outState);
	}
	
	
	private void showFragment(int fragmentIndex, boolean addToBackStack) {
	    FragmentManager fm = getSupportFragmentManager();
	    FragmentTransaction transaction = fm.beginTransaction();
	    for (int i = 0; i < fragments.length; i++) {
	        if (i == fragmentIndex) {
	            transaction.show(fragments[i]);
	        } else {
	            transaction.hide(fragments[i]);
	        }
	    }
	    if (addToBackStack) {
	        transaction.addToBackStack(null);
	    }
	    transaction.commit();
	}
	
	private void onSessionStateChange(Session session, SessionState state, Exception exception) {
	    // Only make changes if the activity is visible
	    if (isResumed) {
	        FragmentManager manager = getSupportFragmentManager();
	        // Get the number of entries in the back stack
	        int backStackSize = manager.getBackStackEntryCount();
	        // Clear the back stack
	        for (int i = 0; i < backStackSize; i++) {
	            manager.popBackStack();
	        }
	        if (state.isOpened()) {
	            // If the session state is open:
	            // Show the authenticated fragment
	            showFragment(SELECTION, false);
	        } else if (state.isClosed()) {
	            // If the session state is closed:
	            // Show the login fragment
	            showFragment(LOGIN, false);
	        }
	    }
	}
	
	@Override
	protected void onResumeFragments() {
	    super.onResumeFragments();
	    Session session = Session.getActiveSession();

	    if (session != null && session.isOpened()) {
	        // if the session is already open,
	        // try to show the selection fragment
	        showFragment(SELECTION, false);
	    } else {
	        // otherwise present the splash screen
	        // and ask the user to login.
	        showFragment(LOGIN, false);
	    }
	}
	
	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
	    // only add the menu when the selection fragment is showing
	    if (fragments[SELECTION].isVisible()) {
			  if (menu.size() == 0) {
	            settings = menu.add(R.string.settings);
	        }
	        return true;
	    } else {
	        menu.clear();
	        settings = null;
	    }
	    return false;
	}
	
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    if (item.equals(settings)) {
	        showFragment(SETTINGS, true);
	        return true;
	    }
	    return false;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	    uiHelper = new UiLifecycleHelper(this, callback);
	    uiHelper.onCreate(savedInstanceState);
	    
		setContentView(R.layout.activity_login);
		
		loginActivity = this;
		
	    FragmentManager fm = getSupportFragmentManager();
	    fragments[LOGIN] = fm.findFragmentById(R.id.loginFragment);
	    fragments[SELECTION] = fm.findFragmentById(R.id.selectionFragment);
	    fragments[SETTINGS] = fm.findFragmentById(R.id.userSettingsFragment);
	    FragmentTransaction transaction = fm.beginTransaction();
	    for(int i = 0; i < fragments.length; i++) {
	        transaction.hide(fragments[i]);
	    }
	    transaction.commit();
	/*	final View controlsView = findViewById(R.id.fullscreen_content_controls);
		final View contentView = findViewById(R.id.fullscreen_content);

		// Set up an instance of SystemUiHider to control the system UI for
		// this activity.
		mSystemUiHider = SystemUiHider.getInstance(this, contentView,
				HIDER_FLAGS);
		mSystemUiHider.setup();
		mSystemUiHider
				.setOnVisibilityChangeListener(new SystemUiHider.OnVisibilityChangeListener() {
					// Cached values.
					int mControlsHeight;
					int mShortAnimTime;

					@Override
					@TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
					public void onVisibilityChange(boolean visible) {
						if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
							// If the ViewPropertyAnimator API is available
							// (Honeycomb MR2 and later), use it to animate the
							// in-layout UI controls at the bottom of the
							// screen.
							if (mControlsHeight == 0) {
								mControlsHeight = controlsView.getHeight();
							}
							if (mShortAnimTime == 0) {
								mShortAnimTime = getResources().getInteger(
										android.R.integer.config_shortAnimTime);
							}
							controlsView
									.animate()
									.translationY(visible ? 0 : mControlsHeight)
									.setDuration(mShortAnimTime);
						} else {
							// If the ViewPropertyAnimator APIs aren't
							// available, simply show or hide the in-layout UI
							// controls.
							controlsView.setVisibility(visible ? View.VISIBLE
									: View.GONE);
						}

						if (visible && AUTO_HIDE) {
							// Schedule a hide().
							delayedHide(AUTO_HIDE_DELAY_MILLIS);
						}
					}
				});

		// Set up the user interaction to manually show or hide the system UI.
		contentView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				if (TOGGLE_ON_CLICK) {
					mSystemUiHider.toggle();
				} else {
					mSystemUiHider.show();
				}
			}
		});

		// Upon interacting with UI controls, delay any scheduled hide()
		// operations to prevent the jarring behavior of controls going away
		// while interacting with the UI.
		findViewById(R.id.dummy_button).setOnTouchListener(
				mDelayHideTouchListener);
		
		// Dummy button goes to car profile page
		final Button dummy_button = (Button) findViewById(R.id.dummy_button);
        dummy_button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                try {
                	ProfileCarDB db = new ProfileCarDB(v.getContext());
                	db.deleteProfile();
                	db.close();
                } catch (Exception e) {
                	
                }
            }
        });
        
        //	facebook login button!!!!
		final Button login_button = (Button) findViewById(R.id.login_button);
		login_button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                try {
            		// start Facebook Login
            		Session.openActiveSession(loginActivity, true, new Session.StatusCallback() {

            			// callback when session changes state
            			@Override
            			public void call(Session session, SessionState state, Exception exception) {
            				if (session.isOpened()) {
            					// make request to the /me API
            					Request.executeMeRequestAsync(session, new Request.GraphUserCallback() {

            					  // callback after Graph API response with user object
            					  @Override
            					  public void onCompleted(GraphUser user, Response response) {
            						  if (user != null) {
            							  TextView welcome = (TextView) findViewById(R.id.logo);
            							  welcome.setText("Hello " + user.getName() + "!");
            							}
            					  }
            					});
            				}
            			}
            		});
                	//Intent k = new Intent(Login.this, CarProfileForm.class);
                	//startActivity(k);
                	
                } catch (Exception e) {
                	
                }
            }
		});

		final Button logout_button = (Button) findViewById(R.id.login_with_email);
		logout_button.setOnClickListener(new View.OnClickListener(){
			public void onClick(View v) {
				try {
					if (Session.getActiveSession() != null) {
						Session.getActiveSession().closeAndClearTokenInformation();
						Session.getActiveSession().close();
						Session.setActiveSession(null);
						TextView welcome = (TextView) findViewById(R.id.logo);
						welcome.setText("Goodbye");
					} else {
						TextView welcome = (TextView) findViewById(R.id.logo);
						welcome.setText("You weren't logged in to start with");
					}
				} catch (Exception e) {
					
				}
			}
		});*/
		final Button logout_button = (Button) findViewById(R.id.logout);
		logout_button.setOnClickListener(new View.OnClickListener(){
			public void onClick(View v) {
				try {
					if (Session.getActiveSession() != null) {
						Session.getActiveSession().closeAndClearTokenInformation();
						Session.getActiveSession().close();
						Session.setActiveSession(null);
					} else {
						TextView welcome = (TextView) findViewById(R.id.logo);
						welcome.setText("You weren't logged in to start with");
					}
				} catch (Exception e) {
					
				}
			}
		});

	}
	

}
