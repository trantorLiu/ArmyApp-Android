package project.armyapp;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.View;

public class MainActivity extends Activity implements View.OnClickListener {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		findViewById(R.id.at_home).setOnClickListener(this);
		findViewById(R.id.report).setOnClickListener(this);
		findViewById(R.id.food_des).setOnClickListener(this);
		findViewById(R.id.pre_back).setOnClickListener(this);
		findViewById(R.id.edit_member).setOnClickListener(this);
		findViewById(R.id.settings).setOnClickListener(this);
		
	}
	
	@Override
	public void onClick(View clickedView){
		switch(clickedView.getId()){
			case R.id.at_home:
				startActivity(new Intent(MainActivity.this, SendMessage.class).putExtra("msg", "已經到家"));
				break;
			case R.id.report:
				startActivity(new Intent(MainActivity.this, SendMessage.class).putExtra("msg", "定時回報"));
				break;
			case R.id.food_des:
				startActivity(new Intent(MainActivity.this, SendMessage.class).putExtra("msg", "餐敘回報"));
				break;
			case R.id.pre_back:
				startActivity(new Intent(MainActivity.this, SendMessage.class).putExtra("msg", "準備返營"));
				break;
			case R.id.edit_member:
				startActivity(new Intent(MainActivity.this, EditMember.class));
				break;
			case R.id.settings:
				startActivity(new Intent(MainActivity.this, Settings.class));
				break;
			default:
				break;
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

}
