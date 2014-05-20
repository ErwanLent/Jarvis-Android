package com.jarvis.client;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.speech.tts.TextToSpeech.OnInitListener;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

public class AlarmActivity extends Activity {

	private SharedPreferences sharedPreferences;
	private TextToSpeech textToSpeech;
	private String response;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_alarm);

		sharedPreferences = getSharedPreferences("Jarvis", Context.MODE_PRIVATE);

		new GetParagraph().execute();
	}

	public void onDoneClick(View view) {
		Intent startMain = new Intent(Intent.ACTION_MAIN);
		startMain.addCategory(Intent.CATEGORY_HOME);
		startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		startActivity(startMain);

		android.os.Process.killProcess(android.os.Process.myPid());
		System.exit(1);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.start, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		textToSpeech.stop();
		
		switch (id) {
		case R.id.action_settings:
			
	        SharedPreferences.Editor editor = sharedPreferences.edit();
	        editor.putBoolean("Registered", false);
	        editor.commit();
			
			Intent settingsIntent = new Intent(this, StartActivity.class);
			startActivity(settingsIntent);
			
			break;
		case R.id.action_alarm:
			
			Intent alarmIntent = new Intent(this, SetAlarmActivity.class);
			startActivity(alarmIntent);
			
			break;
		}

		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	private class GetParagraph extends AsyncTask<Object, Integer, Exception>
			implements OnInitListener {

		protected void onPostExecute(Exception e) {
			TextView alarmParagraphTextView = (TextView) findViewById(R.id.alarmParagraphTextView);
			alarmParagraphTextView.setText(response);

			textToSpeech = new TextToSpeech(getApplicationContext(), this);
		}

		@Override
		public void onInit(int status) {
			// TODO Auto-generated method stub
			if (status == TextToSpeech.SUCCESS) {
				int result = textToSpeech.setLanguage(Locale.US);
				if (result == TextToSpeech.LANG_MISSING_DATA
						|| result == TextToSpeech.LANG_NOT_SUPPORTED) {
					Log.e("custom_error", "This Language is not supported");
				} else {
					textToSpeech
							.speak(response, TextToSpeech.QUEUE_FLUSH, null);
				}
			} else {
				Log.e("custom_error", "Initilization Failed!");
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
			try {
				HttpClient httpclient = new DefaultHttpClient();
				HttpPost httpPost = new HttpPost(
						"http://jarvisupdate.com/GetCards.php");

				List<NameValuePair> postDataPairs = new ArrayList<NameValuePair>();

				Log.d("custom_message",
						sharedPreferences.getString("UserKey", ""));
				Log.d("custom_message", Float.toString(sharedPreferences
						.getFloat("LastLatitude", 0)));
				Log.d("custom_message", Float.toString(sharedPreferences
						.getFloat("LastLongitude", 0)));

				postDataPairs.add(new BasicNameValuePair("user",
						sharedPreferences.getString("UserKey", "")));
				postDataPairs.add(new BasicNameValuePair("latitude",
						Float.toString(sharedPreferences.getFloat(
								"LastLatitude", 0))));
				postDataPairs.add(new BasicNameValuePair("longitude", Float
						.toString(sharedPreferences
								.getFloat("LastLongitude", 0))));

				httpPost.setEntity(new UrlEncodedFormEntity(postDataPairs));

				// Execute HTTP Post Request
				ResponseHandler<String> responseHandler = new BasicResponseHandler();
				response = httpclient.execute(httpPost, responseHandler);

				// Parse JSON
				JSONObject jObject = new JSONObject(response);
				response = jObject.getString("human");

				Log.d("custom_message", response);

				return true;
			} catch (Exception ex) {
				Log.d("custom_error", ex.toString());

				return false;
			}
		}
	}
}