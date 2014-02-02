package project.armyapp;

import java.util.ArrayList;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.ToggleButton;

public class EditMember extends Activity {

	MemberAdapter adapter = null;
	ListView list = null;
	int PICK_CONTACT;
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.edit_member);
		
		final Button from_contact = (Button) findViewById(R.id.from_contact);
		final Button add = (Button) findViewById(R.id.plusButton);
		final Button finish = (Button) findViewById(R.id.finish);
		final EditText newNumber = (EditText) findViewById(R.id.newNumber);
		final EditText newName = (EditText) findViewById(R.id.newName);
		list = (ListView) findViewById(R.id.list);
		
		from_contact.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
            	startActivityForResult(intent, PICK_CONTACT);
            }
        });
        add.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	String number = newNumber.getText().toString();
            	String name = newName.getText().toString();
            	if(number.length() > 0){
            		if(checkContact(number, getApplicationContext())){
		            	saveContact(number, name, getApplicationContext());
		            	activity_reload();
            		}
            	}
            }
        });
        finish.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	finish();
            }
        });
        adapter = new MemberAdapter();
		list.setAdapter(adapter); 
	}
	
	@Override
	public void onActivityResult(int reqCode, int resultCode, Intent data) {
		super.onActivityResult(reqCode, resultCode, data);

		if (reqCode == PICK_CONTACT) {
			if (resultCode == Activity.RESULT_OK) {
				Uri contactData = data.getData();
				Cursor c =  getContentResolver().query(contactData, null, null, null, null);
				if (c.moveToFirst()) {
					String name = c.getString(c.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
			        String id = c.getString(c.getColumnIndexOrThrow(ContactsContract.Contacts._ID));
			        Cursor phones = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
		                            null, ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = " + id, null, null);
		            phones.moveToFirst();
		            String number = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
		            if(checkContact(number, getApplicationContext())){
		            	saveContact(number, name, getApplicationContext());
			            activity_reload();
		            }
				}
			}
		}
	}
	
	public boolean checkContact(String number, Context mContext){
		SharedPreferences prefs = mContext.getSharedPreferences("memberlist", 0);
		String cur_number;
		int size = prefs.getInt("contact_size", 0) + 1;
		for(int i=0;i<size;i++){
			cur_number = prefs.getString("phone" + i, null);
			if(number.equals(cur_number)){
				new AlertDialog.Builder(this)
		        .setTitle("組員已存在")
		        .show();
				return false;
			}
		}
		return true;
	}
	
	public boolean saveContact(String phone, String name, Context mContext) {
	    SharedPreferences prefs = mContext.getSharedPreferences("memberlist", 0);
	    SharedPreferences.Editor editor = prefs.edit();  
	    int num = prefs.getInt("contact_size", -1) + 1;
	    editor.putString("phone" + num, phone);
	    editor.putString("name" + num, name);
	    editor.putBoolean("leader" + num, false);
	    editor.putInt("contact_size", num);
	    return editor.commit();  
	} 
	
	public ArrayList<Contact> loadContact(Context mContext) {
		  SharedPreferences prefs = mContext.getSharedPreferences("memberlist", 0);
		  int size = prefs.getInt("contact_size", 0) + 1;
		  ArrayList<Contact> array = new ArrayList<Contact>();
		  for(int i=0;i<size;i++){
			  Contact tmp = new Contact();
			  tmp.phone_num = prefs.getString("phone" + i, null);
			  tmp.name = prefs.getString("name" + i, null);
			  tmp.leader_stat = prefs.getBoolean("leader" + i, false);
			  tmp.sid = i;
			  if(tmp.phone_num != null){
				  array.add(tmp);
			  }
		  }
		  return array;
	}
	
	public boolean editContact(String phone, String name, int id, Context mContext) {   
	    SharedPreferences prefs = mContext.getSharedPreferences("memberlist", 0);
	    SharedPreferences.Editor editor = prefs.edit();
	    editor.putString("phone" + id, phone);
	    editor.putString("name" + id, name);
	    return editor.commit();  
	} 
	
	public boolean editContactStatus(int id, boolean status, Context mContext) {   
	    SharedPreferences prefs = mContext.getSharedPreferences("memberlist", 0);
	    SharedPreferences.Editor editor = prefs.edit();
	    editor.putBoolean("leader" + id, status);
	    return editor.commit();  
	} 
	
	public boolean deleteContact(int id, Context mContext) {   
	    SharedPreferences prefs = mContext.getSharedPreferences("memberlist", 0);
	    SharedPreferences.Editor editor = prefs.edit();
	    editor.putString("phone" + id, null);
	    editor.putString("name" + id, null);
	    return editor.commit();  
	} 
	
	public void startEdit(final int position, String phone_num, String member_name){
		LinearLayout layout = new LinearLayout(this);
		final EditText inputName = new EditText(this);
		final EditText inputNumber = new EditText(this);
		
		inputNumber.setInputType(InputType.TYPE_CLASS_PHONE);
		inputNumber.setText(phone_num);
		inputName.setText(member_name);
		
		layout.setOrientation(LinearLayout.VERTICAL);
		layout.addView(inputName);
		layout.addView(inputNumber);
		
		new AlertDialog.Builder(this)
		.setTitle("編輯組員")
		.setView(layout)
		.setPositiveButton("確定", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				String newNumber = inputNumber.getText().toString();
				String newName = inputName.getText().toString();
				editContact(newNumber, newName, position, getApplicationContext());
				activity_reload();
			}})
		.setNegativeButton("取消", null).show();
	}
	
	public void startDelete(final int position){
		new AlertDialog.Builder(this)
        .setTitle("刪除組員")
        .setMessage("確定要刪除此組員？")
        .setPositiveButton("確定", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                deleteContact(position, getApplicationContext());
                activity_reload();
            }})
        .setNegativeButton("取消", null).show();
	}
	
	public void activity_reload(){
    	finish();
    	startActivity(getIntent());
	}
	
	class MemberAdapter extends ArrayAdapter<Contact>{
		private ArrayList<Contact> memberList;
		
		MemberAdapter(){
			super(EditMember.this, android.R.layout.simple_list_item_1, loadContact(EditMember.this));
			this.memberList = loadContact(EditMember.this);
		}
		
		public int getCount(){
			return memberList.size();
		}
		
		public Contact getItem(int position){
			return memberList.get(position);
		}
		
		public long getItemId(int position){
			return memberList.get(position).hashCode();
		}
		
		@Override
		public View getView(int position, View convertView, ViewGroup parent){
			ViewHolder holder;
			Button edit, delete;
			ToggleButton switcher;
			
			if(convertView == null){
				LayoutInflater inflater = getLayoutInflater();
				convertView = inflater.inflate(R.layout.list_content, null);
				holder = new ViewHolder(convertView);
				convertView.setTag(holder);
			}
			else{
				holder = (ViewHolder)convertView.getTag();
			}
			holder.populateFrom(memberList.get(position));
			edit = (Button)convertView.findViewById(R.id.edit);
			delete = (Button)convertView.findViewById(R.id.delete);
			switcher = (ToggleButton)convertView.findViewById(R.id.togglebutton);
			
			final int positionForListener = position;
			
			edit.setOnClickListener(new View.OnClickListener() {
	            @Override
	            public void onClick(View v) {
	            	Contact cc = (Contact)list.getItemAtPosition(positionForListener);
	            	startEdit(cc.sid, cc.phone_num, cc.name);
	            }
	        });
			delete.setOnClickListener(new View.OnClickListener() {
	            @Override
	            public void onClick(View v) {
	            	Contact cc = (Contact)list.getItemAtPosition(positionForListener);
	            	startDelete(cc.sid);
	            }
	        });
			switcher.setOnClickListener(new View.OnClickListener() {
	            @Override
	            public void onClick(View v) {
	            	Contact cc = (Contact)list.getItemAtPosition(positionForListener);
	            	boolean on = ((ToggleButton) v).isChecked();
	            	editContactStatus(cc.sid, on, getApplicationContext());
	            }
	        });
			return convertView;
		}
	}
	
	class ViewHolder
	{
		public TextView member = null;
		public ToggleButton stat = null;
		public int num;
		ViewHolder(View row){
			member = (TextView)row.findViewById(R.id.label);
			stat = (ToggleButton)row.findViewById(R.id.togglebutton);
		}
		
		void populateFrom(Contact contact){
			if(contact.name != null && contact.name != ""){
				member.setText(contact.name);
			}
			else{
				member.setText(contact.phone_num);
			}
			stat.setChecked(contact.leader_stat);
			setNum(contact.sid);
		}

		public int getNum() {
			return num;
		}

		public void setNum(int num) {
			this.num = num;
		}
	}
	
	class Contact
	{
		String phone_num;
		String name;
		boolean leader_stat;
		int sid;
	}

}
