package com.jarvis.client;

import java.util.ArrayList;
import java.util.HashSet;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

public class NewsActivity extends Activity {

	private static final String EMPTY_STRING = "";

	private ArrayList<String> newsList = new ArrayList<String>();

	private ListView newListView;
	private ArrayAdapter<String> adapter;

	private SharedPreferences sharedPreferences;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_news);

		sharedPreferences = getSharedPreferences("Jarvis", Context.MODE_PRIVATE);

		newListView = (ListView) findViewById(R.id.newsListView);

		adapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_1, newsList);

		newListView.setAdapter(adapter);

		final EditText newsEditText = (EditText) findViewById(R.id.newsEditText);

		newsEditText.setOnEditorActionListener(new OnEditorActionListener() {
			@Override
			public boolean onEditorAction(TextView v, int actionId,
					KeyEvent event) {
				if (actionId == EditorInfo.IME_ACTION_DONE) {
					// Update list view
					newsList.add(newsEditText.getText().toString());
					adapter.notifyDataSetChanged();

					// Clear edit text
					newsEditText.setText(EMPTY_STRING);
				}
				return false;
			}
		});
	}

	public void onNextFinish(View v) {
		SharedPreferences.Editor editor = sharedPreferences.edit();

		editor.putStringSet("News", new HashSet<String>(newsList));
		editor.commit();

		setResult(RESULT_OK, getIntent());

		finish();
	}
}