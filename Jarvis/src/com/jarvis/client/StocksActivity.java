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

public class StocksActivity extends Activity {

	private static final String EMPTY_STRING = "";

	private ArrayList<String> stocksList = new ArrayList<String>();

	private ListView stocksListView;
	private ArrayAdapter<String> adapter;
	
	private SharedPreferences sharedPreferences;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_stocks);
		
		sharedPreferences = getSharedPreferences("Jarvis", Context.MODE_PRIVATE);

		stocksListView = (ListView) findViewById(R.id.stocksListView);

		adapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_1, stocksList);

		stocksListView.setAdapter(adapter);

		final EditText stocksEditText = (EditText) findViewById(R.id.stocksEditText);
		
		stocksEditText.setOnEditorActionListener(new OnEditorActionListener() {
		    @Override
		    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
		        if (actionId == EditorInfo.IME_ACTION_DONE) {
					// Update list view
					stocksList.add(stocksEditText.getText().toString());
					adapter.notifyDataSetChanged();

					// Clear edit text
					stocksEditText.setText(EMPTY_STRING);
		        }
		        return false;
		    }
		});
	}
	
	public void onNextFinish(View v)
	{
		SharedPreferences.Editor editor = sharedPreferences.edit();
		
		editor.putStringSet("Stocks", new HashSet<String>(stocksList));
		editor.commit();
		
		setResult(RESULT_OK, getIntent());

		finish();
	}
}