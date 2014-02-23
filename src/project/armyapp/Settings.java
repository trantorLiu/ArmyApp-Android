package project.armyapp;

import java.util.ArrayList;
import java.util.Calendar;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ToggleButton;

public class Settings extends Activity{
	
	private AlarmManager[] alarmManager;
	private ArrayList<PendingIntent> intentArray;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.settings);
		
		final EditText editNick = (EditText) findViewById(R.id.editNick);
		Button back = (Button) findViewById(R.id.back);
		Button timer_settings = (Button) findViewById(R.id.timer_settings);
		ToggleButton timer_toggle = (ToggleButton) findViewById(R.id.timer_toggle);
		intentArray = new ArrayList<PendingIntent>();
		
		SharedPreferences prefs = getApplicationContext().getSharedPreferences("profile", 0);
	    final SharedPreferences.Editor editor = prefs.edit();
	    
	    editNick.setText(prefs.getString("nickname", ""));
	    timer_toggle.setChecked(prefs.getBoolean("state", false));
		
		back.setOnClickListener(new View.OnClickListener()
	    {
	    	public void onClick(View v)
	        {
	    		editor.putString("nickname", editNick.getText().toString());
	    	    editor.commit();
	    	    finish();
	        }
	    });
		
		timer_settings.setOnClickListener(new View.OnClickListener() 
		{
			public void onClick(View v) {
				startActivity(new Intent(Settings.this, TimerSettings.class));
			}
		});
		
		timer_toggle.setOnClickListener(new View.OnClickListener() 
		{
            public void onClick(View v) {
            	boolean state = ((ToggleButton) v).isChecked();
            	if(state){
            		set_alarm();
            	}
            	else{
            		cancel_alarm();
            	}
        	    set_toggle(state);
            }
        });
	}
	
	private boolean set_toggle(boolean state){
		SharedPreferences prefs = getApplicationContext().getSharedPreferences("profile", 0);
		SharedPreferences.Editor editor = prefs.edit();
	    editor.putBoolean("state", state);
	    return editor.commit();
	}

	private void set_alarm(){
		PendingIntent pi;
		SharedPreferences prefs = getApplicationContext().getSharedPreferences("timerlist", 0);
		int size = prefs.getInt("timer_size", 0) + 1;
		int hour, minute;
		boolean receiver;
		alarmManager = new AlarmManager[size];
		
		for(int i=0;i<size;i++){
		   hour = prefs.getInt("hour" + i, -1);
		   minute = prefs.getInt("minute" + i, -1);
		   if(hour != -1 && minute != -1){
			   Calendar calendar = Calendar.getInstance();
			   calendar.setTimeInMillis(System.currentTimeMillis());
			   calendar.set(Calendar.HOUR_OF_DAY, hour);
			   calendar.set(Calendar.MINUTE, minute);
			   receiver = prefs.getBoolean("leader" + i, false);
			   String dd = hour + ":" + minute + (receiver ? "leader" : "member");
			   Intent intentAlarm = new Intent(Settings.this, AlarmReceiver.class).putExtra("receiver", dd);
		       pi = PendingIntent.getBroadcast(Settings.this, i, intentAlarm, PendingIntent.FLAG_UPDATE_CURRENT);
	
			   alarmManager[i] = (AlarmManager) getSystemService(ALARM_SERVICE);
			   alarmManager[i].setInexactRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pi);
			   intentArray.add(pi);
		   }
		}
	}
	
	private void cancel_alarm(){
	    if(intentArray.size()>0){
	    	int j = 0;
	        for(int i=0; i<alarmManager.length; i++){
	        	if(alarmManager[i] != null){
	        		alarmManager[i].cancel(intentArray.get(j));
	        		j++;
	        	}
	        }
	        intentArray.clear();
	    }
	}

}
