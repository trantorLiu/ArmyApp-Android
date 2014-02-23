package project.armyapp;

import java.util.ArrayList;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.format.Time;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.ToggleButton;

public class TimerSettings extends Activity{
	
	TimerAdapter adapter = null;
	ListView list = null;
	int PICK_TIMER;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.timer_settings);
		
		Button add = (Button) findViewById(R.id.add);
		Button finish = (Button) findViewById(R.id.finish);
		list = (ListView) findViewById(R.id.list);
		
		add.setOnClickListener(new View.OnClickListener() 
		{
			public void onClick(View v) {
				LayoutInflater inflater = getLayoutInflater();
				View dialoglayout = inflater.inflate(R.layout.timer_dialog, null);
				AlertDialog.Builder builder = new AlertDialog.Builder(TimerSettings.this);

				final TimePicker selected_time = (TimePicker)dialoglayout.findViewById(R.id.timePicker);
	    		final ToggleButton receiver = (ToggleButton)dialoglayout.findViewById(R.id.receiver);
				
	    		builder.setTitle("新增定時回報")
				       .setView(dialoglayout)
				       .setPositiveButton("新增", new DialogInterface.OnClickListener() {
				    	   public void onClick(DialogInterface dialog, int whichButton) {
				    		   Time time = new Time();
				    		   time.hour = selected_time.getCurrentHour();
				    		   time.minute = selected_time.getCurrentMinute();
				    		   Boolean stat = receiver.isChecked();
				    		   saveTimer(time, stat, getApplicationContext());
				    		   activity_reload();
				    	   }})
				       .setNegativeButton("取消", null).show();
			}
		});
		finish.setOnClickListener(new View.OnClickListener() 
		{
			public void onClick(View v) {
				finish();
			}
		});
		adapter = new TimerAdapter();
		list.setAdapter(adapter); 
	}
	
	public ArrayList<Timer> loadTimer(Context mContext) {
		  SharedPreferences prefs = mContext.getSharedPreferences("timerlist", 0);
		  int size = prefs.getInt("timer_size", 0) + 1;
		  ArrayList<Timer> array = new ArrayList<Timer>();
		  for(int i=0;i<size;i++){
			  Timer tmp = new Timer();
			  tmp.time = new Time();
			  tmp.time.hour = prefs.getInt("hour" + i, -1);
			  tmp.time.minute = prefs.getInt("minute" + i, -1);
			  tmp.stat = prefs.getBoolean("leader" + i, false);
			  tmp.sid = i;
			  if(tmp.time.hour != -1){
				  array.add(tmp);
			  }
		  }
		  return array;
	}
	
	public boolean saveTimer(Time time, Boolean stat, Context mContext) {
	    SharedPreferences prefs = mContext.getSharedPreferences("timerlist", 0);
	    SharedPreferences.Editor editor = prefs.edit();  
	    int num = prefs.getInt("timer_size", -1) + 1;
	    editor.putInt("hour" + num, time.hour);
	    editor.putInt("minute" + num, time.minute);
	    editor.putBoolean("leader" + num, stat);
	    editor.putInt("timer_size", num);
	    return editor.commit();
	} 
	
	public boolean editTimer(Time tm, Boolean stat, int id, Context mContext) {   
	    SharedPreferences prefs = mContext.getSharedPreferences("timerlist", 0);
	    SharedPreferences.Editor editor = prefs.edit();
	    editor.putInt("hour" + id, tm.hour);
	    editor.putInt("minute" + id, tm.minute);
	    editor.putBoolean("leader" + id, stat);
	    return editor.commit();  
	} 
	
	public boolean deleteTimer(int id, Context mContext) {   
	    SharedPreferences prefs = mContext.getSharedPreferences("timerlist", 0);
	    SharedPreferences.Editor editor = prefs.edit();
	    editor.putInt("hour" + id, -1);
	    editor.putInt("minute" + id, -1);
	    return editor.commit();  
	} 
	
	public void startEdit(final int position, Time tm, Boolean stat){
		LayoutInflater inflater = getLayoutInflater();
		View dialoglayout = inflater.inflate(R.layout.timer_dialog, null);
		AlertDialog.Builder builder = new AlertDialog.Builder(TimerSettings.this);

		final TimePicker selected_time = (TimePicker)dialoglayout.findViewById(R.id.timePicker);
		final ToggleButton receiver = (ToggleButton)dialoglayout.findViewById(R.id.receiver);
		
		selected_time.setCurrentHour(tm.hour);
		selected_time.setCurrentMinute(tm.minute);
		receiver.setChecked(stat);
		
		builder.setTitle("編輯定時回報")
		       .setView(dialoglayout)
		       .setPositiveButton("確定", new DialogInterface.OnClickListener() {
		    	   public void onClick(DialogInterface dialog, int whichButton) {
		    		   Time time = new Time();
		    		   time.hour = selected_time.getCurrentHour();
		    		   time.minute = selected_time.getCurrentMinute();
		    		   Boolean stat = receiver.isChecked();
		    		   editTimer(time, stat, position, getApplicationContext());
		    		   activity_reload();
		    	   }})
		       .setNegativeButton("取消", null).show();
	}
	
	public void startDelete(final int position){
		new AlertDialog.Builder(this)
        .setTitle("刪除定時回報")
        .setMessage("確定要刪除此定時設定？")
        .setPositiveButton("確定", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                deleteTimer(position, getApplicationContext());
                activity_reload();
            }})
        .setNegativeButton("取消", null).show();
	}
	
	public void activity_reload(){
    	finish();
    	startActivity(getIntent());
	}
	
	class TimerAdapter extends ArrayAdapter<Timer>{
		private ArrayList<Timer> timerList;
		
		TimerAdapter(){
			super(TimerSettings.this, android.R.layout.simple_list_item_1, loadTimer(TimerSettings.this));
			this.timerList = loadTimer(TimerSettings.this);
		}
		
		public int getCount(){
			return timerList.size();
		}
		
		public Timer getItem(int position){
			return timerList.get(position);
		}
		
		public long getItemId(int position){
			return timerList.get(position).hashCode();
		}
		
		@Override
		public View getView(int position, View convertView, ViewGroup parent){
			ViewHolder holder;
			Button edit, delete;
			
			if(convertView == null){
				LayoutInflater inflater = getLayoutInflater();
				convertView = inflater.inflate(R.layout.list_time, null);
				holder = new ViewHolder(convertView);
				convertView.setTag(holder);
			}
			else{
				holder = (ViewHolder)convertView.getTag();
			}
			holder.populateFrom(timerList.get(position));
			edit = (Button)convertView.findViewById(R.id.edit);
			delete = (Button)convertView.findViewById(R.id.delete);
			
			final int positionForListener = position;
			
			edit.setOnClickListener(new View.OnClickListener() {
	            @Override
	            public void onClick(View v) {
	            	Timer cc = (Timer)list.getItemAtPosition(positionForListener);
	            	startEdit(cc.sid, cc.time, cc.stat);
	            }
	        });
			delete.setOnClickListener(new View.OnClickListener() {
	            @Override
	            public void onClick(View v) {
	            	Timer cc = (Timer)list.getItemAtPosition(positionForListener);
	            	startDelete(cc.sid);
	            }
	        });
			return convertView;
		}
	}
	
	class ViewHolder
	{
		public TextView time = null;
		public TextView receiver = null;
		public int num;
		ViewHolder(View row){
			time = (TextView)row.findViewById(R.id.time);
			receiver = (TextView)row.findViewById(R.id.receiver);
		}
		
		void populateFrom(Timer timer){
			time.setText(String.format("%02d", timer.time.hour) + ":" + String.format("%02d", timer.time.minute));
			receiver.setText(timer.stat ? "寄給組長" : "寄給組員");
			setNum(timer.sid);
		}

		public int getNum() {
			return num;
		}

		public void setNum(int num) {
			this.num = num;
		}
	}
	
	class Timer
	{
		Time time;
		boolean stat;
		int sid;
	}
	
}