package com.jarvis.client;

import java.util.Calendar;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.TimePicker;

public class SetAlarmActivity extends Activity {

	private final int ALARM_REQUEST_CODE = 1;

	private SharedPreferences sharedPreferences;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_set_alarm);

		sharedPreferences = getSharedPreferences("Jarvis", Context.MODE_PRIVATE);
	}

	public void onDoneClick(View v) {
		TimePicker timePicker = (TimePicker) findViewById(R.id.timePicker);
		
		Calendar calNow = Calendar.getInstance();
        Calendar calSet = (Calendar) calNow.clone();

        calSet.set(Calendar.HOUR_OF_DAY, timePicker.getCurrentHour());
        calSet.set(Calendar.MINUTE, timePicker.getCurrentMinute());
        calSet.set(Calendar.SECOND, 0);
        calSet.set(Calendar.MILLISECOND, 0);
        
        if (calSet.compareTo(calNow) <= 0) {
            // Today Set time passed, count to tomorrow
            calSet.add(Calendar.DATE, 1);
        }
        
        setAlarm(calSet);
        
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("Registered", true);
        editor.commit();
        
        Intent startMain = new Intent(this, AlarmActivity.class);
        startActivity(startMain);
	}

	private void setAlarm(Calendar targetCal) {
		Intent intent = new Intent(getBaseContext(), AlarmReceiver.class);
		PendingIntent pendingIntent = PendingIntent.getBroadcast(
				getBaseContext(), ALARM_REQUEST_CODE, intent, 0);
		AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
		alarmManager.set(AlarmManager.RTC_WAKEUP, targetCal.getTimeInMillis(),
				pendingIntent);
	}
}