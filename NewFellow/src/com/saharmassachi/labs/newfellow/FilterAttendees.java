package com.saharmassachi.labs.newfellow;

import java.util.ArrayList;

import org.json.JSONException;

import com.saharmassachi.labs.newfellow.book.FriendsGetProfilePics;
import com.saharmassachi.labs.newfellow.book.Utility;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import static com.saharmassachi.labs.newfellow.Constants.NAME;
import static com.saharmassachi.labs.newfellow.Constants.CID;

public class FilterAttendees extends ListActivity {
	DBhelper helper;
	private EditText filterText = null;
	ArrayAdapter<String> adapter = null;
	int pos = 0;
	Context ctx;
	private Intent nextPage;
	long[] ids;
	

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
		ProgressDialog dialog = ProgressDialog.show(this, "",
				"Loading contacts", true, true);
		while (adapter.isEmpty()) {
			dialog.show();
		}
		dialog.dismiss();

		ListView lv = getListView();
		lv.setTextFilterEnabled(true);
		nextPage = new Intent(this, AddFriendLoc.class);

		lv.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				final String name = (String) ((TextView) view).getText();
				pos = parent.getPositionForView(view);
				
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
										nextPage.putExtra(NAME, name);
										nextPage.putExtra(CID, ids[pos]);
										startActivity(nextPage);
									}

								}).setNegativeButton(R.string.no, null).show();
			}
		});
	}

	private String[] getStringArrayList() {
		// get all attendees

		Contact[] contacts = helper.getAllAttendees();
		String[] toReturn = new String[contacts.length];
		ids = new long[contacts.length];
		for (int i = 0; i<contacts.length; i++){
			toReturn[i] = contacts[i].getName();
			ids[i] = contacts[i].getID();
		}
		
		//ids = s[0];
		
		// ArrayList[] ss;
		// ss[1] == attendee name
		// ss[0] == attendee id;
		// 
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

}
