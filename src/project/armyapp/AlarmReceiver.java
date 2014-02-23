package project.armyapp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.SmsManager;
import android.widget.Toast;

public class AlarmReceiver extends BroadcastReceiver
{
	BroadcastReceiver smsSentReceiver;
	
       @Override
       public void onReceive(Context context, Intent intent)
       {                         
    	   String phoneNumberReciver="009718202185";// phone number to which SMS to be send
           String message="Hi I will be there later, See You soon";// message to send
           String number = intent.getStringExtra("receiver");
           //SmsManager sms = SmsManager.getDefault(); 
           //sms.sendTextMessage(phoneNumberReciver, null, message, null, null);
           Toast.makeText(context, "Alarm Triggered and SMS Sent" + number, Toast.LENGTH_SHORT).show();
       }

}