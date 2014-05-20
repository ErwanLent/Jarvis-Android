package com.jarvis.client;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

public class StartActivity extends Activity implements LocationListener {

	private ProgressDialog loadingDialogue;

	private static final int STOCKS_ACTIVITY = 1;
	private static final int NEWS_ACTIVITY = 2;

	private boolean _isWeatherButtonSelected = false;
	private boolean _isStocksButtonSelected = false;
	private boolean _isNewsButtonSelected = false;

	private boolean isAddStocksComplete = false;
	private boolean isAddNewsComplete = false;

	private SharedPreferences sharedPreferences;

	private String userKey = UUID.randomUUID().toString();

	private LocationManager locationManager;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_start);

		sharedPreferences = getSharedPreferences("Jarvis", Context.MODE_PRIVATE);

		if (sharedPreferences.getBoolean("Registered", false)) {
			Intent intent = new Intent(this, AlarmActivity.class);
			startActivity(intent);
		}

		locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0,
				0, this);
	}

	@Override
	public void onLocationChanged(Location location) {
		SharedPreferences.Editor editor = sharedPreferences.edit();

		editor.putFloat("LastLatitude", (float) location.getLatitude());
		editor.putFloat("LastLongitude", (float) location.getLongitude());

		editor.commit();

		Log.d("custom_message", "Latitude:" + location.getLatitude()
				+ ", Longitude:" + location.getLongitude());

		locationManager.removeUpdates(this);
	}

	@Override
	public void onProviderDisabled(String provider) {
		Log.d("custom_message", "disable");
	}

	@Override
	public void onProviderEnabled(String provider) {
		Log.d("custom_message", "enable");
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		Log.d("custom_message", "status");
	}

	public void onWeatherButtonClick(View view) {
		_isWeatherButtonSelected = !_isWeatherButtonSelected;
	}

	public void onStocksButtonClick(View view) {
		_isStocksButtonSelected = !_isStocksButtonSelected;

		if (_isStocksButtonSelected) {
			Intent i = new Intent(this, StocksActivity.class);
			startActivityForResult(i, STOCKS_ACTIVITY);
		}
	}

	public void onNewsButtonClick(View view) {
		_isNewsButtonSelected = !_isNewsButtonSelected;

		if (_isNewsButtonSelected) {
			Intent i = new Intent(this, NewsActivity.class);
			startActivityForResult(i, NEWS_ACTIVITY);
		}
	}

	public void onStartButtonClick(View v) {
		// Display Loading Dialogue
		loadingDialogue = new ProgressDialog(this);
		loadingDialogue.setCancelable(false);
		loadingDialogue.setMessage("Baking cookies...");
		loadingDialogue.show();

		// Start Async Task
		new CreateAccount().execute();
	}

	private class CreateAccount extends AsyncTask<Object, Integer, Exception> {

		private boolean successful = false;

		protected void onProgressUpdate(Integer... progress) {

		}

		protected void onPostExecute(Exception e) {

			if (successful) {

				// Save user key
				SharedPreferences.Editor editor = sharedPreferences.edit();
				editor.putString("UserKey", userKey);
				editor.putBoolean("Registered", true);
				editor.commit();

				// Register stocks and news
				if (_isStocksButtonSelected) {
					new AddStocks().execute();
				} else {
					isAddStocksComplete = true;
				}

				if (_isNewsButtonSelected) {
					new AddNews().execute();
				} else {
					isAddNewsComplete = true;
				}

			} else {
				loadingDialogue.dismiss();

				AlertDialog.Builder builder = new AlertDialog.Builder(
						getApplicationContext());
				builder.setMessage("A connection error occurred.")
						.setTitle("Connection Error").setCancelable(false);

				AlertDialog alert = builder.create();
				alert.show();
			}
		}

		@Override
		protected Exception doInBackground(Object... params) {
			try {

				if (!postData()) {
					return new Exception("Failure");
				}

				successful = true;
				return new Exception("Success");
			} catch (Exception e) {
				return e;
			}
		}

		public boolean postData() {
			// Create a new HttpClient and Post Header
			HttpClient httpclient = new DefaultHttpClient();
			HttpPost httpPost = new HttpPost(
					"http://jarvisupdate.com/CreateUser.php");

			try {
				// Add your data
				List<NameValuePair> postDataPairs = new ArrayList<NameValuePair>();

				postDataPairs.add(new BasicNameValuePair("user", userKey));

				if (_isWeatherButtonSelected) {
					postDataPairs.add(new BasicNameValuePair("keywords[]",
							"weather"));
				}

				if (_isNewsButtonSelected) {
					postDataPairs.add(new BasicNameValuePair("keywords[]",
							"rss"));
				}

				if (_isStocksButtonSelected) {
					postDataPairs.add(new BasicNameValuePair("keywords[]",
							"stocks"));
				}

				httpPost.setEntity(new UrlEncodedFormEntity(postDataPairs));

				// Execute HTTP Post Request
				ResponseHandler<String> responseHandler = new BasicResponseHandler();
				String response = httpclient.execute(httpPost, responseHandler);

				Log.d("custom_message", "Create User Response: " + response);

				if (response.equals("True")) {
					return true;
				}

				return false;
			} catch (Exception e) {
				Log.d("custom_error", "Create User Exception: " + e.toString());
				return false;
			}
		}
	}

	private class AddStocks extends AsyncTask<Object, Integer, Exception> {

		protected void onPostExecute(Exception e) {

			isAddStocksComplete = true;

			if (isAddNewsComplete) {
				loadingDialogue.dismiss();

				Intent intent = new Intent(getApplicationContext(),
						SetAlarmActivity.class);
				startActivity(intent);
			}

		}

		@Override
		protected Exception doInBackground(Object... params) {
			try {

				if (!postData()) {
					return new Exception("Failure");
				}

				return new Exception("Success");
			} catch (Exception e) {
				return e;
			}
		}

		public boolean postData() {
			// Create a new HttpClient and Post Header
			HttpClient httpclient = new DefaultHttpClient();
			HttpPost httpPost = new HttpPost(
					"http://jarvisupdate.com/AddStocks.php");

			try {
				// Add your data
				List<NameValuePair> postDataPairs = new ArrayList<NameValuePair>();

				postDataPairs.add(new BasicNameValuePair("user", userKey));

				for (String stock : sharedPreferences.getStringSet("Stocks",
						null)) {
					postDataPairs
							.add(new BasicNameValuePair("stocks[]", stock));
				}

				httpPost.setEntity(new UrlEncodedFormEntity(postDataPairs));

				// Execute HTTP Post Request
				ResponseHandler<String> responseHandler = new BasicResponseHandler();
				String response = httpclient.execute(httpPost, responseHandler);

				Log.d("custom_message", "Add Stocks Response: " + response);

				if (response.equals("True")) {
					return true;
				}

				return false;
			} catch (Exception e) {
				Log.d("custom_error", "Add Stocks Exception: " + e.toString());
				return false;
			}
		}
	}

	private class AddNews extends AsyncTask<Object, Integer, Exception> {

		protected void onPostExecute(Exception e) {

			isAddNewsComplete = true;

			if (isAddStocksComplete) {
				loadingDialogue.dismiss();

				Intent intent = new Intent(getApplicationContext(),
						SetAlarmActivity.class);
				startActivity(intent);
			}

		}

		@Override
		protected Exception doInBackground(Object... params) {
			try {

				if (!postData()) {
					return new Exception("Failure");
				}

				return new Exception("Success");
			} catch (Exception e) {
				return e;
			}
		}

		public boolean postData() {
			// Create a new HttpClient and Post Header
			HttpClient httpclient = new DefaultHttpClient();
			HttpPost httpPost = new HttpPost(
					"http://jarvisupdate.com/AddRss.php");

			try {
				// Add your data
				List<NameValuePair> postDataPairs = new ArrayList<NameValuePair>();

				Set<String> news = sharedPreferences.getStringSet("News", null);

				postDataPairs.add(new BasicNameValuePair("user", userKey));
				postDataPairs.add(new BasicNameValuePair("rss", news.iterator()
						.next()));

				httpPost.setEntity(new UrlEncodedFormEntity(postDataPairs));

				// Execute HTTP Post Request
				ResponseHandler<String> responseHandler = new BasicResponseHandler();
				String response = httpclient.execute(httpPost, responseHandler);

				Log.d("custom_message", "Add News Response: " + response);

				if (response.equals("True")) {
					return true;
				}

				return false;
			} catch (Exception e) {
				Log.d("custom_error", "Add News Exception: " + e.toString());
				return false;
			}
		}
	}
}