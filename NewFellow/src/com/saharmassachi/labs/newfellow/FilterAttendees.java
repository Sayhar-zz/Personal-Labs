package com.saharmassachi.labs.newfellow;

import org.json.JSONException;

import android.app.AlertDialog;
import android.app.ListActivity;
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
import android.widget.Toast;

public class FilterAttendees extends ListActivity {
	DBhelper helper;
	private EditText filterText = null;
	ArrayAdapter<String> adapter = null;
	Context ctx;
	private Intent nextPage;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		helper = new DBhelper(this);
		setContentView(R.layout.filterattendees);
		ctx = this;
		filterText = (EditText) findViewById(R.id.search_line);
		filterText.addTextChangedListener(filterTextWatcher);

		adapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_1, getStringArrayList());
		setListAdapter(adapter);

		ListView lv = getListView();
		lv.setTextFilterEnabled(true);
		nextPage = new Intent(this, AddFriendLoc.class);
		
		lv.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				final String name = (String) ((TextView) view).getText();
				new AlertDialog.Builder(ctx)
				.setTitle("Is this it?")
				.setMessage(
						String.format(
								"Is %1$s the person you are thinking of?",
								name))
				.setPositiveButton(R.string.yes,
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								Bundle params = new Bundle();
								nextPage.putExtra("name", name);
								startActivity(nextPage);
							}

						}).setNegativeButton(R.string.no, null).show();
			}
		});
	}

	private String[] getStringArrayList() {
		// get all attendees
		String[] s = helper.getAllAttendees();
		return s;
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

	private void showToast(String msg) {
		Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();

	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		filterText.removeTextChangedListener(filterTextWatcher);
	}

}
