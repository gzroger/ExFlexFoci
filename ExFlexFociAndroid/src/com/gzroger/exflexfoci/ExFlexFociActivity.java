package com.gzroger.exflexfoci;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.DataSetObserver;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class ExFlexFociActivity extends Activity {

	public List<DataSetObserver> rgobserver = new ArrayList<DataSetObserver>();

	class Dbut extends SQLiteOpenHelper {

		private static final int DATABASE_VERSION = 2;
		private static final String DATABASE_NAME = "dbut";

	    Dbut(Context context) {
	        super(context, DATABASE_NAME, null, DATABASE_VERSION);
	    }

	    @Override
	    public void onCreate(SQLiteDatabase db) {
	        db.execSQL("CREATE TABLE player (NAME text)");
	        db.setVersion(1);
	    }

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		}
	}
	
	public class LitwaPlayer implements ListAdapter {

		@Override
		public boolean areAllItemsEnabled() {
			return false;
		}

		@Override
		public boolean isEnabled(int position) {
			return false;
		}

		@Override
		public int getCount() {
			return rgplayer.size();
		}

		@Override
		public Object getItem(int position) {
			return rgplayer.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public int getItemViewType(int position) {
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			TextView textw;
			if (convertView == null) {
				textw = (TextView) getLayoutInflater().inflate(R.layout.listwplayer_item, null, false);
			} else {
				textw = (TextView) convertView;
			}
			textw.setText(rgplayer.get(position).stNameGet());
			return textw;
		}

		@Override
		public int getViewTypeCount() {
			return 1;
		}

		@Override
		public boolean hasStableIds() {
			return false;
		}

		@Override
		public boolean isEmpty() {
			return rgplayer.isEmpty();
		}

		@Override
		public void registerDataSetObserver(DataSetObserver observer) {
			ExFlexFociActivity.this.rgobserver .add(observer);
		}

		@Override
		public void unregisterDataSetObserver(DataSetObserver observer) {
			ExFlexFociActivity.this.rgobserver.remove(observer);
		}

	}

	public class Player {

		String stName;
		public Player(String stName) {
			this.stName = stName;
		}

		public CharSequence stNameGet() {
			return stName;
		}

		@Override
		public String toString() {
			return "Player [stName=" + stName + "]";
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + getOuterType().hashCode();
			result = prime * result
					+ ((stName == null) ? 0 : stName.hashCode());
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			Player other = (Player) obj;
			if (!getOuterType().equals(other.getOuterType()))
				return false;
			if (stName == null) {
				if (other.stName != null)
					return false;
			} else if (!stName.equals(other.stName))
				return false;
			return true;
		}

		private ExFlexFociActivity getOuterType() {
			return ExFlexFociActivity.this;
		}
		

	}


	static final int DATE_DIALOG_ID = 0;

	private Button btnFoci;    
	private ListView listwPlayer;

	private Calendar cal;
	private List<Player> rgplayer = new ArrayList<Player>();

	private SQLiteDatabase db;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		db = new Dbut(getApplicationContext()).getWritableDatabase();
		
		btnFoci = (Button) findViewById(R.id.dateButton);
		btnFoci.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				showDialog(DATE_DIALOG_ID);				
			}
		});
		
		loadData();
				
		listwPlayer = (ListView) findViewById(R.id.listwPlayer);
		listwPlayer.setAdapter(new LitwaPlayer());
		
		// get the current date
		cal = Calendar.getInstance();

		// display the current date (this method is below)
		updateDisplay();
	}

	private void loadData() {
		rgplayer.clear();
		Cursor cur = db.query("player", new String[] {"name"}, null, null, null, null, "name");
		if (cur.moveToFirst()) {
			do {
				rgplayer.add(new Player(cur.getString(0)));
			} while (cur.moveToNext());
		}
		cur.close();
	}

	// updates the date in the TextView
	private void updateDisplay() {
		btnFoci.setText(DateFormat.format("yyyy-MM-dd", cal));
	}    

	@Override
	protected Dialog onCreateDialog(int id) {
		switch (id) {
		case DATE_DIALOG_ID:
			return new DatePickerDialog(this,
					mDateSetListener,
					cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH)
			);
		}
		return super.onCreateDialog(id); 
	}

	// the callback received when the user "sets" the date in the dialog
	private DatePickerDialog.OnDateSetListener mDateSetListener =
		new DatePickerDialog.OnDateSetListener() {

		public void onDateSet(DatePicker view, int year, int moy, int dom) {
			cal.set(Calendar.YEAR, year);
			cal.set(Calendar.MONTH, moy);
			cal.set(Calendar.DAY_OF_MONTH, dom);
			updateDisplay();
		}
	};

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.main_menu, menu);
		return true;
	}     

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle item selection
		switch (item.getItemId()) {
		case R.id.addNewPlayer:
			addNewPlayer();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	private void addNewPlayer() {
		AlertDialog.Builder alert = new AlertDialog.Builder(this);

		alert.setTitle("Create");
		alert.setMessage("Player name:");

		// Set an EditText view to get user input 
		final EditText input = new EditText(this);
		alert.setView(input);

		alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				String value = input.getText().toString();
				createPlayer(value);
			}
		});

		alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				// Canceled.
			}
		});
		alert.show();
	}    


	protected void createPlayer(String stName) {
		ContentValues cv = new ContentValues();
		cv.put("name", stName);
		db.insert("player", null, cv);

		loadData();
		refreshListwPlayer();
	}

	private void refreshListwPlayer() {
		for (DataSetObserver obs : rgobserver) {
			obs.onChanged();
		}
	}
}
