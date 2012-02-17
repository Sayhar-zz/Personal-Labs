package com.saharmassachi.labs.newfellow;

import java.util.ArrayList;
import java.util.HashMap;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import static com.saharmassachi.labs.newfellow.Constants.BID;
import static com.saharmassachi.labs.newfellow.Constants.FNAME;
import static com.saharmassachi.labs.newfellow.Constants.LNAME;
import static com.saharmassachi.labs.newfellow.Constants.MANUAL;

public class FilterAttendees extends ListActivity {
	DataHelper helper;
	private EditText filterText = null;
	ArrayAdapter<String> adapter = null;
	int pos = 0;
	Context ctx;
	private Intent nextPage;
	private HashMap<String, Long> map;
	Contact[] contacts;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		helper = new DataHelper(this);
		setContentView(R.layout.filterattendees);
		init();
		
		}

	private String[] getStringArrayList() {
		// get all attendees
		map = new HashMap<String, Long>();
		contacts = helper.getAllBasicPublic(); 
		String[] toReturn = new String[contacts.length];
		
		for(int i = 0; i < contacts.length; i++){
			toReturn[i] = contacts[i].getName();
			map.put(toReturn[i], contacts[i].getID());
			//map will remember the associations
		}
		
		return toReturn;
	}

	private TextWatcher filterTextWatcher = new TextWatcher() {

		public void afterTextChanged(Editable s) {
		}

		public void beforeTextChanged(CharSequence s, int start, int count,
				int after) {
		}

		public void onTextChanged(CharSequence s, int start, int before,
				int count) {
			adapter.getFilter().filter(s);
		}

	};

	@Override
	protected void onDestroy() {
		super.onDestroy();
		filterText.removeTextChangedListener(filterTextWatcher);
	}

	public void manualAdd(View v) {
		
		String name = filterText.getText().toString().trim();
		try{
			Long code = Long.parseLong(name);
			//if the name is parseable as a long (aka if it is a number) then it is a badgeid
			//do that instead.
			nextPage.removeExtra(BID);
			nextPage.putExtra(BID, code);
			startActivity(nextPage);
			return;
		}
		catch(Exception e){
			
		}
		if(name.length() == 0){ return;} //button is useless when there is nothing written 
		
		String[] names = name.split(" ", 2);
		nextPage.putExtra(FNAME, names[0]);
		if(names.length == 2){ 
			nextPage.putExtra(LNAME, names[1]);	
		}
		nextPage.putExtra(MANUAL, true);
		startActivity(nextPage);
	}

	
	private void init(){
		ctx = this;
		filterText = (EditText) findViewById(R.id.search_line);
		filterText.addTextChangedListener(filterTextWatcher);
		
		ProgressDialog dialog = ProgressDialog.show(this, "",
				"Loading contacts", true, true);
		
		adapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_1, getStringArrayList());
		setListAdapter(adapter);
		
		dialog.dismiss();

		ListView lv = getListView();
		lv.setTextFilterEnabled(true);
		nextPage = new Intent(this, AddFriendLoc.class);

		lv.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				TextView tview = (TextView) view;
				String name = tview.getText().toString();
				final String fname = name.split(" ")[0];
				final String lname = name.split(" ")[1];
				nextPage.putExtra(FNAME, fname);
				nextPage.putExtra(LNAME, lname);
				nextPage.putExtra(BID, map.get(name));
				startActivity(nextPage);
			}
		});

	}
}
