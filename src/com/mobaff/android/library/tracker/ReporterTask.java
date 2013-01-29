package com.mobaff.android.library.tracker;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.Writer;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import android.os.AsyncTask;
import android.util.Log;

/**
 * Handles reporting back to MobAff Tracker
 * 
 * @author Stephen Furnival <stephen@mobaff.com>
 * @copyright MobAff LLC
 */
public class ReporterTask extends AsyncTask<Void, Void, Boolean> {
	
	private String api_path;
	private String api_key;
	private String unique_id;
	private String vertical_link_id;

	/**
	 * No Fallback constructor
	 * 
	 * @param tracker_api_path
	 * @param api_key
	 * @param unique_id
	 */
	ReporterTask(String tracker_api_path, String api_key, String unique_id) {
		this.api_path = tracker_api_path;
		this.unique_id = unique_id;
		this.api_key = api_key;
		this.vertical_link_id = null;
	}
	
	/**
	 * Constructor with Vertical Link id fallback
	 * 
	 * @param String	tracker_api_path	Base path to tracker
	 * @param String	api_key				User's API key
	 * @param String	unique_id			Unique click identifier
	 * @param String	vertical_link_id	Vertical Link ID tracking this Install
	 */
	ReporterTask(String tracker_api_path, String api_key, String unique_id, String vertical_link_id) {
		this.api_path = tracker_api_path;
		this.unique_id = unique_id;
		this.api_key = api_key;
		this.vertical_link_id = vertical_link_id;
	}
	
	/**
	 * Begins communication with the MobAff API to report
	 * the install.
	 * 
	 * @return	Boolean		True if install has been reported.
	 */
	private boolean report() {
		Boolean result = false;
		String endpoint = "";
		
		if (this.vertical_link_id == null) {
			endpoint = this.api_path + "/track/install/" + this.unique_id;
		} else {
			endpoint = this.api_path + "/track/install/vid/" + this.vertical_link_id;
		}
		
		try {
			if (this.call_api(endpoint)) {
				// Success
				Log.v("MobAff Tracker SDK", "Report Succeeded.");
				result = true;
			} else {
				// Failed
				Log.e("MobAff Tracker SDK", "Attempt to report install to tracker failed.");
			}
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return result;
	}
	
	/**
	 * Makes a call to the MobAff Tracker API.
	 * 
	 * @param	String	endpoint	API Endpoint to call
	 * @return	Boolean		True if the request returns HTTP 200
	 * @throws MalformedURLException
	 * @throws IOException
	 */
	private boolean call_api(String endpoint) throws MalformedURLException, IOException {
		boolean response = true;
		
		URL url = new URL( endpoint );
		HttpURLConnection urlConnection;
		urlConnection = (HttpURLConnection) url.openConnection();
		
		Log.v("MobAff Tracker SDK", "Calling: " + endpoint);
		
		try {
			urlConnection.setFixedLengthStreamingMode(0);
			InputStream in = new BufferedInputStream(urlConnection.getInputStream());
		} catch (Exception e) {
			e.printStackTrace();
			
			Log.e("MobAff Tracker SDK", e.getMessage());
			
			// Internet Access was unavailable or Tracker API was
			// unavailable.
			response = false;
		} finally {
			urlConnection.disconnect();
		}
	    
		return response;
	}

	@Override
	protected Boolean doInBackground(Void... params) {
		return this.report();
	}
	
}
