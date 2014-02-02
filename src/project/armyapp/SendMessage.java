package project.armyapp;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class SendMessage extends Activity {
	
	String message;
	String time, nickname, address, head_message;
	EditText messageContent;
	Button btnSendLeader, btnSendAll, cancel;
	Boolean flag;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.send_message);
		
		flag = true;
		message = getIntent().getStringExtra("msg");
		SharedPreferences prefs = getApplicationContext().getSharedPreferences("profile", 0);
		
		messageContent = (EditText) findViewById(R.id.msgContent);
		btnSendLeader = (Button) findViewById(R.id.btnSendLeader);
		btnSendAll = (Button) findViewById(R.id.btnSendAll);
		cancel = (Button) findViewById(R.id.cancel);
		
		btnSendLeader.setOnClickListener(new View.OnClickListener()
	    {
	    	public void onClick(View v)
	        {
	    		sendSMS(messageContent.getText().toString(), true);
	        }
	    });
		btnSendAll.setOnClickListener(new View.OnClickListener()
	    {
	    	public void onClick(View v)
	        {
	    		sendSMS(messageContent.getText().toString(), false);
	        }
	    });
		cancel.setOnClickListener(new View.OnClickListener()
	    {
	    	public void onClick(View v)
	        {
	    		finish();
	        }
	    });
		
		Calendar c = Calendar.getInstance(); 
		String hour = String.format("%02d", c.get(Calendar.HOUR_OF_DAY));
		String minute = String.format("%02d", c.get(Calendar.MINUTE));
		time = hour + ":" + minute;
		nickname = prefs.getString("nickname", "");
		address = "";
		head_message = nickname + message + "，時間 " + time;
		
		messageContent.setText(head_message);
		
		LocationManager lm = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
		Location location_init = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
		
		if(location_init != null){
			setLocation(location_init);
			flag = false;
		}
		
		final LocationListener locationListener = new LocationListener() {
		    public void onLocationChanged(Location location) {
		    	if(flag){
		    		setLocation(location);
		    		flag = false;
		    	}
		    }

		    public void onProviderDisabled(String arg0) {
		    	showAlert();
		    }

		    public void onProviderEnabled(String arg0) {
		        // TODO Auto-generated method stub

		    }

		    public void onStatusChanged(String arg0, int arg1, Bundle arg2) {
		        // TODO Auto-generated method stub

		    }
		};
		
		lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 2000, 10, locationListener);
	}
	
	protected void showAlert() {
		try{
			new AlertDialog.Builder(this)
	        .setTitle("無法顯示您的所在地")
	        .setMessage("請檢查是否已開啟手機定位功能")
	        .setPositiveButton("前往開啟", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {
					startActivityForResult(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS), 0);
				}})
			.setNegativeButton("取消", null).show();
		}
		catch (Exception e){
			
		}
	}

	public void setLocation(Location location){
		Geocoder gCoder = new Geocoder(getApplicationContext());
		List<Address> addresses = null;
		try {
			addresses = gCoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (addresses != null && addresses.size() > 0) {
		    address = addresses.get(0).getCountryName() + addresses.get(0).getAdminArea() + addresses.get(0).getSubLocality();
		}
		
		messageContent.setText(head_message + "，所在地爲" + address + "。");
	}
	
	
	public ArrayList<Contact> loadContact(Context mContext, boolean stat) {
		  SharedPreferences prefs = mContext.getSharedPreferences("memberlist", 0);
		  int size = prefs.getInt("contact_size", 0) + 1;
		  ArrayList<Contact> array = new ArrayList<Contact>();
		  for(int i=0;i<size;i++){
			  Contact tmp = new Contact();
			  tmp.phone_num = prefs.getString("phone" + i, null);
			  tmp.leader_stat = prefs.getBoolean("leader" + i, false);
			  tmp.sid = i;
			  if(tmp.phone_num != null && tmp.leader_stat == stat){
				  array.add(tmp);
			  }
		  }
		  return array;
	}

    private void sendSMS(String message, boolean leader)
    {
    	Intent smsIntent = new Intent(Intent.ACTION_VIEW);
    	ArrayList<Contact> receiver;
    	String receiver_number = "";
    	if(leader){
    		receiver = loadContact(getApplicationContext(), true);
    	}
    	else{
    		receiver = loadContact(getApplicationContext(), false);
    	}
    	for(Contact c: receiver){
    		receiver_number += ";" + c.phone_num;
    	}
    	
    	smsIntent.setData(Uri.parse("smsto:"));
        smsIntent.setType("vnd.android-dir/mms-sms");

        smsIntent.putExtra("address", new String (receiver_number));
        smsIntent.putExtra("sms_body", message);
        try {
           startActivity(smsIntent);
           finish();
        } catch (android.content.ActivityNotFoundException ex) {
           Toast.makeText(SendMessage.this, "簡訊傳送失敗。", Toast.LENGTH_SHORT).show();
        }
    }
    
    class Contact
	{
		String phone_num;
		boolean leader_stat;
		int sid;
	}
	
}