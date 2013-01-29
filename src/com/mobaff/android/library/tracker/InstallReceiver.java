package com.mobaff.android.library.tracker;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

/**
 * Handles reporting back to MobAff Tracker
 * 
 * @author Stephen Furnival <stephen@mobaff.com>
 * @copyright MobAff LLC
 */
public class InstallReceiver extends BroadcastReceiver {
	
	private String tracker_url;
	private String tracker_key;
	private String vertical_link_id;

	@Override
	public void onReceive(Context context, Intent intent) {

		if (this.tracker_key != null && this.tracker_url != null) {
		    Bundle extras = intent.getExtras();
		    String referrerString = extras.getString("referrer");
		    ReporterTask tracker_reporter = null;
		    
		    // If there is a fallback vertical link available, parse it
		    if (referrerString != null && referrerString.indexOf("|") >= 0) {
		    	String[] chunks = referrerString.split("\\|");
		    	referrerString = chunks[0];
		    	this.vertical_link_id = chunks[1];
		    }
		    
		    // --
		    // Report by Referrer Value
		    // --
		    if (referrerString != null && referrerString.length() > 0) {
			    tracker_reporter = new ReporterTask(
			    	this.tracker_url,
			    	this.tracker_key,
			    	referrerString
			    );
			    
			    Log.v("MobAff Tracker SDK", "Reporting Install by referrer: " + referrerString);
			    Toast.makeText(context, referrerString, Toast.LENGTH_LONG).show();
			    
		    // --
		    // Report by Vertical Link ID
		    // --
		    } else if ((referrerString == null || referrerString.length() <= 0) && this.vertical_link_id != null) {
			    tracker_reporter = new ReporterTask(
			    	this.tracker_url,
			    	this.tracker_key,
			    	null,
			    	this.vertical_link_id
			    );
			    
			    Log.v("MobAff Tracker SDK", "Reporting Install by Vertical Link: " + this.vertical_link_id);
			    Toast.makeText(context, "Vertical Link: " + this.vertical_link_id, Toast.LENGTH_LONG).show();
		    }
		    
		    // Start the reporter task if we have a referrer or fallback available
		    if (tracker_reporter != null) {
		    	tracker_reporter.execute();
		    }
		    
		} else {
			// Either the Tracker URL or API Key has not been set.
			Log.e("MobAff Install Receiver", "Either the tracker URL or API key has not been set.");
		}
	}
	
	/**
	 * Sets the base domain for the tracker being used.
	 * 
	 * @param	String	url		Base domain of tracker.  
	 */
	protected void setTrackerURL(String url) {
		this.tracker_url = url;
	}
	
	/**
	 * Sets the API key to use when talking to the MobAff
	 * API.
	 * 
	 * @param	String	key		Given API key.
	 */
	protected void setTrackerKey(String key) {
		this.tracker_key = key;
	}
	
}
