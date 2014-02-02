package project.armyapp;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class Settings extends Activity{

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.settings);
		
		final EditText editNick = (EditText) findViewById(R.id.editNick);
		Button save = (Button) findViewById(R.id.save);
		Button back = (Button) findViewById(R.id.back);
		
		SharedPreferences prefs = getApplicationContext().getSharedPreferences("profile", 0);
	    final SharedPreferences.Editor editor = prefs.edit();
	    
	    editNick.setText(prefs.getString("nickname", ""));
		
		save.setOnClickListener(new View.OnClickListener()
	    {
	    	public void onClick(View v)
	        {
	    		editor.putString("nickname", editNick.getText().toString());
	    	    editor.commit();
	    	    finish();
	        }
	    });
		
		back.setOnClickListener(new View.OnClickListener()
	    {
	    	public void onClick(View v)
	        {
	    	    finish();
	        }
	    });
		
	}

}
